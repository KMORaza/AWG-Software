package awg.simulation.software;

public class WaveformData {
    private double samplingRate = 1000000;
    private int samples = 1000;
    private double duration = 0.001;
    private String resolution = "8-bit";
    private double amplitude = 1.0;
    private double frequency = 1000;
    private double phase = 0;
    private double harmonic1Amp = 0;
    private double harmonic1Freq = 2;
    private double carrierFreq = 10000;
    private double modIndex = 1.0;
    private boolean iqSignal = false;
    private String selectedChannel = "Channel 1";
    private double phaseOffset = 0;
    private boolean syncChannels = false;

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
        this.duration = samples / samplingRate;
    }

    public double getDuration() {
        return duration;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getPhase() {
        return phase;
    }

    public void setPhase(double phase) {
        this.phase = phase;
    }

    public double getHarmonic1Amp() {
        return harmonic1Amp;
    }

    public void setHarmonic1Amp(double harmonic1Amp) {
        this.harmonic1Amp = harmonic1Amp;
    }

    public double getHarmonic1Freq() {
        return harmonic1Freq;
    }

    public void setHarmonic1Freq(double harmonic1Freq) {
        this.harmonic1Freq = harmonic1Freq;
    }

    public double getCarrierFreq() {
        return carrierFreq;
    }

    public void setCarrierFreq(double carrierFreq) {
        this.carrierFreq = carrierFreq;
    }

    public double getModIndex() {
        return modIndex;
    }

    public void setModIndex(double modIndex) {
        this.modIndex = modIndex;
    }

    public boolean isIqSignal() {
        return iqSignal;
    }

    public void setIqSignal(boolean iqSignal) {
        this.iqSignal = iqSignal;
    }

    public String getSelectedChannel() {
        return selectedChannel;
    }

    public void setSelectedChannel(String selectedChannel) {
        this.selectedChannel = selectedChannel;
    }

    public double getPhaseOffset() {
        return phaseOffset;
    }

    public void setPhaseOffset(double phaseOffset) {
        this.phaseOffset = phaseOffset;
    }

    public boolean isSyncChannels() {
        return syncChannels;
    }

    public void setSyncChannels(boolean syncChannels) {
        this.syncChannels = syncChannels;
    }
}