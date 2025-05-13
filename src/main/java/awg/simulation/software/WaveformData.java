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
    private String noiseType = "None";
    private double noiseAmplitude = 0;
    private double jitterAmount = 0;
    private double driftRate = 0;
    private boolean quantizationNoise = false;
    private double dcOffset = 0;
    private double dutyCycle = 50;
    private boolean burstMode = false;
    private int burstCycles = 1;
    private double burstPeriod = 0.001;
    private boolean frequencySweep = false;
    private String sweepType = "Linear";
    private double startFreq = 1000;
    private double stopFreq = 2000;
    private double sweepDuration = 0.001;
    private double phaseNoise = 0;

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

    public String getNoiseType() {
        return noiseType;
    }

    public void setNoiseType(String noiseType) {
        this.noiseType = noiseType;
    }

    public double getNoiseAmplitude() {
        return noiseAmplitude;
    }

    public void setNoiseAmplitude(double noiseAmplitude) {
        this.noiseAmplitude = noiseAmplitude;
    }

    public double getJitterAmount() {
        return jitterAmount;
    }

    public void setJitterAmount(double jitterAmount) {
        this.jitterAmount = jitterAmount;
    }

    public double getDriftRate() {
        return driftRate;
    }

    public void setDriftRate(double driftRate) {
        this.driftRate = driftRate;
    }

    public boolean isQuantizationNoise() {
        return quantizationNoise;
    }

    public void setQuantizationNoise(boolean quantizationNoise) {
        this.quantizationNoise = quantizationNoise;
    }

    public double getDcOffset() {
        return dcOffset;
    }

    public void setDcOffset(double dcOffset) {
        this.dcOffset = dcOffset;
    }

    public double getDutyCycle() {
        return dutyCycle;
    }

    public void setDutyCycle(double dutyCycle) {
        this.dutyCycle = dutyCycle;
    }

    public boolean isBurstMode() {
        return burstMode;
    }

    public void setBurstMode(boolean burstMode) {
        this.burstMode = burstMode;
    }

    public int getBurstCycles() {
        return burstCycles;
    }

    public void setBurstCycles(int burstCycles) {
        this.burstCycles = burstCycles;
    }

    public double getBurstPeriod() {
        return burstPeriod;
    }

    public void setBurstPeriod(double burstPeriod) {
        this.burstPeriod = burstPeriod;
    }

    public boolean isFrequencySweep() {
        return frequencySweep;
    }

    public void setFrequencySweep(boolean frequencySweep) {
        this.frequencySweep = frequencySweep;
    }

    public String getSweepType() {
        return sweepType;
    }

    public void setSweepType(String sweepType) {
        this.sweepType = sweepType;
    }

    public double getStartFreq() {
        return startFreq;
    }

    public void setStartFreq(double startFreq) {
        this.startFreq = startFreq;
    }

    public double getStopFreq() {
        return stopFreq;
    }

    public void setStopFreq(double stopFreq) {
        this.stopFreq = stopFreq;
    }

    public double getSweepDuration() {
        return sweepDuration;
    }

    public void setSweepDuration(double sweepDuration) {
        this.sweepDuration = sweepDuration;
    }

    public double getPhaseNoise() {
        return phaseNoise;
    }

    public void setPhaseNoise(double phaseNoise) {
        this.phaseNoise = phaseNoise;
    }
}