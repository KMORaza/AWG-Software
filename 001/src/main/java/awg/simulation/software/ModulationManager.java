package awg.simulation.software;

public class ModulationManager {
    public double[] applyModulation(double[] waveform, String modulationType, WaveformData data) {
        double[] modulated = new double[waveform.length];
        double carrierFreq = data.getCarrierFreq();
        double modIndex = data.getModIndex();
        double samplingRate = data.getSamplingRate();
        boolean iqSignal = data.isIqSignal();
        for (int i = 0; i < waveform.length; i++) {
            double t = (double) i / samplingRate;
            double carrier = Math.sin(2 * Math.PI * carrierFreq * t);
            switch (modulationType) {
                case "AM":
                    modulated[i] = waveform[i] * (1 + modIndex * carrier);
                    break;
                case "FM":
                    double integral = 0;
                    for (int j = 0; j <= i; j++) {
                        integral += waveform[j] * (1.0 / samplingRate);
                    }
                    modulated[i] = Math.sin(2 * Math.PI * carrierFreq * t + modIndex * integral);
                    break;
                case "PM":
                    modulated[i] = Math.sin(2 * Math.PI * carrierFreq * t + modIndex * waveform[i]);
                    break;
                case "FSK":
                    double freq = carrierFreq + modIndex * waveform[i] * carrierFreq;
                    modulated[i] = Math.sin(2 * Math.PI * freq * t);
                    break;
                case "PSK":
                    modulated[i] = waveform[i] * Math.cos(modIndex * carrier);
                    break;
                case "QAM":
                    if (iqSignal) {
                        double iSignal = waveform[i] * Math.cos(2 * Math.PI * carrierFreq * t);
                        double qSignal = waveform[i] * Math.sin(2 * Math.PI * carrierFreq * t);
                        modulated[i] = iSignal + qSignal;
                    } else {
                        modulated[i] = waveform[i];
                    }
                    break;
                default:
                    modulated[i] = waveform[i];
            }
        }
        return modulated;
    }
}