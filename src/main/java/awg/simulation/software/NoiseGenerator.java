package awg.simulation.software;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoiseGenerator {
    private List<double[]> customNoisePoints = new ArrayList<>();
    private Random random = new Random();
    private WaveformData waveformData;

    public void setWaveformData(WaveformData waveformData) {
        this.waveformData = waveformData;
    }

    public double[] generateNoise(String noiseType, double amplitude, int samples) {
        double[] noise = new double[samples];
        if (noiseType == null || noiseType.equals("None") || amplitude <= 0) {
            return noise;
        }

        switch (noiseType) {
            case "White":
                for (int i = 0; i < samples; i++) {
                    noise[i] = amplitude * (2 * random.nextDouble() - 1);
                }
                break;
            case "Gaussian":
                for (int i = 0; i < samples; i++) {
                    noise[i] = amplitude * random.nextGaussian();
                }
                break;
            case "Custom":
                if (!customNoisePoints.isEmpty()) {
                    double duration = waveformData.getDuration();
                    for (int i = 0; i < samples; i++) {
                        double t = (double) i / waveformData.getSamplingRate();
                        double normalizedT = t / duration;
                        noise[i] = amplitude * evaluateCustomNoise(normalizedT);
                    }
                }
                break;
        }
        return noise;
    }

    public void importNoiseCsv(File file) {
        customNoisePoints.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 2) {
                    try {
                        double time = Double.parseDouble(values[0]);
                        double amplitude = Double.parseDouble(values[1]);
                        customNoisePoints.add(new double[]{time, amplitude});
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid CSV line: " + line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double evaluateCustomNoise(double t) {
        if (customNoisePoints.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < customNoisePoints.size() - 1; i++) {
            double t1 = customNoisePoints.get(i)[0], v1 = customNoisePoints.get(i)[1];
            double t2 = customNoisePoints.get(i + 1)[0], v2 = customNoisePoints.get(i + 1)[1];
            if (t >= t1 && t <= t2) {
                return v1 + (v2 - v1) * (t - t1) / (t2 - t1);
            }
        }
        return customNoisePoints.get(customNoisePoints.size() - 1)[1];
    }
}