package awg.simulation.software;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class WaveformGenerator {
    private List<double[]> customPoints = new ArrayList<>();
    private double[] waveform;

    public double[] generateWaveform(String type, WaveformData data) {
        int samples = data.getSamples();
        waveform = new double[samples];
        double baseFreq = data.getFrequency();
        double amplitude = data.getAmplitude();
        double phase = Math.toRadians(data.getPhase());
        double phaseOffset = Math.toRadians(data.getPhaseOffset());
        double harmonic1Amp = data.getHarmonic1Amp();
        double harmonic1Freq = data.getHarmonic1Freq() * baseFreq;
        boolean syncChannels = data.isSyncChannels();

        /// Use base phase if channels are synchronized
        double totalPhase = syncChannels ? phase : phase + phaseOffset;

        for (int i = 0; i < samples; i++) {
            double t = (double) i / data.getSamplingRate();
            double value = 0;
            switch (type) {
                case "Sine":
                    value = Math.sin(2 * Math.PI * baseFreq * t + totalPhase);
                    break;
                case "Square":
                    value = Math.sin(2 * Math.PI * baseFreq * t + totalPhase) >= 0 ? 1 : -1;
                    break;
                case "Triangle":
                    value = 2 * Math.abs(2 * (t * baseFreq - Math.floor(t * baseFreq + 0.5))) - 1;
                    break;
                case "Sawtooth":
                    value = 2 * (t * baseFreq - Math.floor(t * baseFreq)) - 1;
                    break;
                case "Custom":
                    value = evaluateCustom(t);
                    break;
            }
            /// Apply amplitude and harmonic
            value *= amplitude;
            if (harmonic1Amp > 0 && !type.equals("Custom")) {
                value += harmonic1Amp * Math.sin(2 * Math.PI * harmonic1Freq * t + totalPhase);
            }
            waveform[i] = quantize(value, data.getResolution());
        }
        return waveform;
    }

    public void importCsv(File file) {
        customPoints.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 2) {
                    try {
                        double time = Double.parseDouble(values[0]);
                        double amplitude = Double.parseDouble(values[1]);
                        customPoints.add(new double[]{time, amplitude});
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid CSV line: " + line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private double quantize(double value, String resolution) {
        int bits = Integer.parseInt(resolution.split("-")[0]);
        int levels = (int) Math.pow(2, bits);
        double step = 2.0 / (levels - 1);
        return Math.round(value / step) * step;
    }

    public double[] getWaveform() {
        return waveform;
    }

    public void setWaveformData(WaveformData waveformData) {
        this.waveformData = waveformData;
    }

    private WaveformData waveformData;
}