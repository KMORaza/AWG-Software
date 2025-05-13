package awg.simulation.software;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaveformGenerator {
    private List<double[]> customPoints = new ArrayList<>();
    private double[] waveform;
    private WaveformData waveformData;
    private Random random = new Random();

    public double[] generateWaveform(String type, WaveformData data) {
        this.waveformData = data;
        int samples = data.getSamples();
        waveform = new double[samples];
        double baseFreq = data.getFrequency();
        double amplitude = data.getAmplitude();
        double phase = Math.toRadians(data.getPhase());
        double phaseOffset = Math.toRadians(data.getPhaseOffset());
        double harmonic1Amp = data.getHarmonic1Amp();
        double harmonic1Freq = data.getHarmonic1Freq() * baseFreq;
        boolean syncChannels = data.isSyncChannels();
        double jitterAmount = data.getJitterAmount();
        double driftRate = data.getDriftRate();
        boolean quantizationNoise = data.isQuantizationNoise();
        double dcOffset = data.getDcOffset();
        double dutyCycle = data.getDutyCycle() / 100.0;
        boolean burstMode = data.isBurstMode();
        int burstCycles = data.getBurstCycles();
        double burstPeriod = data.getBurstPeriod();
        boolean frequencySweep = data.isFrequencySweep();
        String sweepType = data.getSweepType();
        double startFreq = Math.min(data.getStartFreq(), data.getStopFreq());
        double stopFreq = Math.max(data.getStartFreq(), data.getStopFreq());
        double sweepDuration = data.getSweepDuration();
        double phaseNoise = Math.toRadians(data.getPhaseNoise());
        double totalPhase = syncChannels ? phase : phase + phaseOffset;

        for (int i = 0; i < samples; i++) {
            double t = (double) i / data.getSamplingRate();
            /// Apply jitter (random time offset)
            double jitter = jitterAmount > 0 ? random.nextGaussian() * jitterAmount : 0;
            double jitteredT = Math.max(0, t + jitter);
            /// Apply frequency sweep
            double currentFreq = baseFreq;
            if (frequencySweep && sweepDuration > 0) {
                double fraction = Math.min(t / sweepDuration, 1.0);
                if ("Linear".equals(sweepType)) {
                    currentFreq = startFreq + (stopFreq - startFreq) * fraction;
                } else if ("Logarithmic".equals(sweepType)) {
                    double logStart = Math.log10(startFreq);
                    double logStop = Math.log10(stopFreq);
                    currentFreq = Math.pow(10, logStart + (logStop - logStart) * fraction);
                }
            }
            /// Apply phase noise
            double phaseNoiseValue = phaseNoise > 0 ? random.nextGaussian() * phaseNoise : 0;
            /// Apply drift (linear amplitude scaling)
            double driftFactor = 1 + driftRate * t / 100; /// drift rate in %/s
            double value = 0;
            switch (type) {
                case "Sine":
                    value = Math.sin(2 * Math.PI * currentFreq * jitteredT + totalPhase + phaseNoiseValue);
                    break;
                case "Square":
                    double squarePhase = 2 * Math.PI * currentFreq * jitteredT + totalPhase + phaseNoiseValue;
                    value = (squarePhase % (2 * Math.PI)) < (2 * Math.PI * dutyCycle) ? 1 : -1;
                    break;
                case "Triangle":
                    value = 2 * Math.abs(2 * (jitteredT * currentFreq - Math.floor(jitteredT * currentFreq + 0.5))) - 1;
                    break;
                case "Sawtooth":
                    double sawPhase = jitteredT * currentFreq - Math.floor(jitteredT * currentFreq);
                    value = sawPhase < dutyCycle ? (sawPhase / dutyCycle) * 2 - 1 : -1 + (sawPhase - dutyCycle) / (1 - dutyCycle) * 2;
                    break;
                case "Custom":
                    value = evaluateCustom(t);
                    break;
            }
            /// Apply amplitude, harmonic, and drift
            value *= amplitude * driftFactor;
            if (harmonic1Amp > 0 && !type.equals("Custom")) {
                double harmonicFreq = harmonic1Freq * (currentFreq / baseFreq);
                value += harmonic1Amp * Math.sin(2 * Math.PI * harmonicFreq * jitteredT + totalPhase + phaseNoiseValue) * driftFactor;
            }
            /// Apply DC offset
            value += dcOffset;
            /// Apply burst mode
            if (burstMode && burstPeriod > 0) {
                double cycleTime = 1.0 / currentFreq;
                double burstDuration = burstCycles * cycleTime;
                double burstPhase = t % burstPeriod;
                if (burstPhase > burstDuration) {
                    value = 0;
                }
            }
            /// Apply quantization
            value = quantize(value, data.getResolution(), quantizationNoise);
            waveform[i] = value;
        }
        return waveform;
    }

    public void importCsv(File file) {
        customPoints.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 2) {
                    throw new IllegalArgumentException("CSV must have exactly two columns (time, amplitude)");
                }
                try {
                    double time = Double.parseDouble(values[0]);
                    double amplitude = Double.parseDouble(values[1]);
                    if (time < 0 || time > 1) {
                        throw new IllegalArgumentException("Time values must be between 0 and 1");
                    }
                    customPoints.add(new double[]{time, amplitude});
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format in CSV line: " + line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import CSV: " + e.getMessage(), e);
        }
    }

    public void addCustomPoint(double x, double y) {
        customPoints.add(new double[]{x, y});
    }

    private double evaluateCustom(double t) {
        if (!customPoints.isEmpty()) {
            double duration = waveformData.getDuration();
            double normalizedT = t / duration;
            for (int i = 0; i < customPoints.size() - 1; i++) {
                double t1 = customPoints.get(i)[0], v1 = customPoints.get(i)[1];
                double t2 = customPoints.get(i + 1)[0], v2 = customPoints.get(i + 1)[1];
                if (normalizedT >= t1 && normalizedT <= t2) {
                    return v1 + (v2 - v1) * (normalizedT - t1) / (t2 - t1);
                }
            }
            return customPoints.get(customPoints.size() - 1)[1];
        }
        return 0;
    }

    private double quantize(double value, String resolution, boolean addNoise) {
        int bits = Integer.parseInt(resolution.split("-")[0]);
        int levels = (int) Math.pow(2, bits);
        double step = 2.0 / (levels - 1);
        double quantized = Math.round(value / step) * step;
        if (addNoise && bits < 14) {
            double noiseAmplitude = step * 0.1;
            quantized += noiseAmplitude * (2 * random.nextDouble() - 1);
        }
        return Math.max(-1, Math.min(1, quantized));
    }

    public double[] getWaveform() {
        return waveform;
    }

    public void setWaveformData(WaveformData waveformData) {
        this.waveformData = waveformData;
    }
}