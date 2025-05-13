package awg.simulation.software;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;

public class MainController {
    @FXML private ComboBox<String> waveformType, resolutionCombo, channelSelect, modulationType, noiseType, sweepType;
    @FXML private TextField samplingRateField, durationField, samplesField, phaseOffsetField, carrierFreqField, modIndexField;
    @FXML private TextField amplitudeField, frequencyField, phaseField, harmonic1AmpField, harmonic1FreqField;
    @FXML private TextField noiseAmplitudeField, jitterAmountField, driftRateField;
    @FXML private TextField dcOffsetField, dutyCycleField, burstCyclesField, burstPeriodField;
    @FXML private TextField startFreqField, stopFreqField, sweepDurationField, phaseNoiseField;
    @FXML private Slider samplingRateSlider, amplitudeSlider, frequencySlider, phaseSlider, harmonic1AmpSlider, harmonic1FreqSlider;
    @FXML private Slider dcOffsetSlider, dutyCycleSlider;
    @FXML private CheckBox syncChannels, iqSignal, quantizationNoise, burstMode, frequencySweep;
    @FXML private Canvas waveformCanvas;
    @FXML private Button importCsvButton, importNoiseCsvButton, generateButton, clearCanvasButton;
    @FXML private ToggleButton drawToggle;

    private WaveformGenerator waveformGenerator = new WaveformGenerator();
    private ModulationManager modulationManager = new ModulationManager();
    private NoiseGenerator noiseGenerator = new NoiseGenerator();
    private WaveformData waveformData = new WaveformData();
    private PauseTransition debounce = new PauseTransition(Duration.millis(200));

    @FXML
    public void initialize() {
        waveformType.getItems().addAll("Sine", "Square", "Triangle", "Sawtooth", "Custom");
        waveformType.setValue("Sine");
        resolutionCombo.getItems().addAll("8-bit", "10-bit", "12-bit", "14-bit");
        resolutionCombo.setValue("8-bit");
        channelSelect.getItems().addAll("Channel 1", "Channel 2", "Channel 3", "Channel 4");
        channelSelect.setValue("Channel 1");
        modulationType.getItems().addAll("AM", "FM", "PM", "FSK", "PSK", "QAM", "None");
        modulationType.setValue("None");
        noiseType.getItems().addAll("None", "White", "Gaussian", "Custom");
        noiseType.setValue("None");
        sweepType.getItems().addAll("Linear", "Logarithmic");
        sweepType.setValue("Linear");

        /// Bind sliders to text fields and waveformData
        samplingRateSlider.valueProperty().addListener((obs, old, newVal) -> {
            samplingRateField.setText(String.format("%.0f", newVal.doubleValue()));
            waveformData.setSamplingRate(newVal.doubleValue());
            updatePreview();
        });
        amplitudeSlider.valueProperty().addListener((obs, old, newVal) -> {
            amplitudeField.setText(String.format("%.2f", newVal.doubleValue()));
            waveformData.setAmplitude(newVal.doubleValue());
            updatePreview();
        });
        frequencySlider.valueProperty().addListener((obs, old, newVal) -> {
            frequencyField.setText(String.format("%.0f", newVal.doubleValue()));
            waveformData.setFrequency(newVal.doubleValue());
            updatePreview();
        });
        phaseSlider.valueProperty().addListener((obs, old, newVal) -> {
            phaseField.setText(String.format("%.0f", newVal.doubleValue()));
            waveformData.setPhase(newVal.doubleValue());
            updatePreview();
        });
        harmonic1AmpSlider.valueProperty().addListener((obs, old, newVal) -> {
            harmonic1AmpField.setText(String.format("%.2f", newVal.doubleValue()));
            waveformData.setHarmonic1Amp(newVal.doubleValue());
            updatePreview();
        });
        harmonic1FreqSlider.valueProperty().addListener((obs, old, newVal) -> {
            harmonic1FreqField.setText(String.format("%.0f", newVal.doubleValue()));
            waveformData.setHarmonic1Freq(newVal.doubleValue());
            updatePreview();
        });
        dcOffsetSlider.valueProperty().addListener((obs, old, newVal) -> {
            dcOffsetField.setText(String.format("%.2f", newVal.doubleValue()));
            waveformData.setDcOffset(newVal.doubleValue());
            updatePreview();
        });
        dutyCycleSlider.valueProperty().addListener((obs, old, newVal) -> {
            dutyCycleField.setText(String.format("%.0f", newVal.doubleValue()));
            waveformData.setDutyCycle(newVal.doubleValue());
            updatePreview();
        });

        /// Initialize text fields
        amplitudeField.setText("1.0");
        frequencyField.setText("1000");
        phaseField.setText("0");
        harmonic1AmpField.setText("0");
        harmonic1FreqField.setText("2");
        carrierFreqField.setText("10000");
        modIndexField.setText("1.0");
        phaseOffsetField.setText("0");
        noiseAmplitudeField.setText("0");
        jitterAmountField.setText("0");
        driftRateField.setText("0");
        dcOffsetField.setText("0");
        dutyCycleField.setText("50");
        burstCyclesField.setText("1");
        burstPeriodField.setText("0.001");
        startFreqField.setText("1000");
        stopFreqField.setText("2000");
        sweepDurationField.setText("0.001");
        phaseNoiseField.setText("0");

        /// Bind text fields to waveformData with validation
        amplitudeField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0.1, Math.min(2.0, value));
                amplitudeField.setText(String.format("%.2f", value));
                waveformData.setAmplitude(value);
                updatePreview();
            } catch (NumberFormatException e) {
                amplitudeField.setText("1.0");
            }
        });
        frequencyField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(1, Math.min(10000, value));
                frequencyField.setText(String.format("%.0f", value));
                waveformData.setFrequency(value);
                updatePreview();
            } catch (NumberFormatException e) {
                frequencyField.setText("1000");
            }
        });
        phaseField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0, Math.min(360, value));
                phaseField.setText(String.format("%.0f", value));
                waveformData.setPhase(value);
                updatePreview();
            } catch (NumberFormatException e) {
                phaseField.setText("0");
            }
        });
        harmonic1AmpField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0, Math.min(1.0, value));
                harmonic1AmpField.setText(String.format("%.2f", value));
                waveformData.setHarmonic1Amp(value);
                updatePreview();
            } catch (NumberFormatException e) {
                harmonic1AmpField.setText("0");
            }
        });
        harmonic1FreqField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(1, Math.min(10, value));
                harmonic1FreqField.setText(String.format("%.0f", value));
                waveformData.setHarmonic1Freq(value);
                updatePreview();
            } catch (NumberFormatException e) {
                harmonic1FreqField.setText("2");
            }
        });
        carrierFreqField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(1, value); // Ensure positive carrier frequency
                carrierFreqField.setText(String.format("%.0f", value));
                waveformData.setCarrierFreq(value);
                updatePreview();
            } catch (NumberFormatException e) {
                carrierFreqField.setText("10000");
            }
        });
        modIndexField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0, value); // Modulation index should be non-negative
                modIndexField.setText(String.format("%.2f", value));
                waveformData.setModIndex(value);
                updatePreview();
            } catch (NumberFormatException e) {
                modIndexField.setText("1.0");
            }
        });
        phaseOffsetField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                phaseOffsetField.setText(String.format("%.0f", value));
                waveformData.setPhaseOffset(value);
                updatePreview();
            } catch (NumberFormatException e) {
                phaseOffsetField.setText("0");
            }
        });
        noiseAmplitudeField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0, Math.min(0.5, value));
                noiseAmplitudeField.setText(String.format("%.2f", value));
                waveformData.setNoiseAmplitude(value);
                updatePreview();
            } catch (NumberFormatException e) {
                noiseAmplitudeField.setText("0");
            }
        });
        jitterAmountField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0, Math.min(0.001, value));
                jitterAmountField.setText(String.format("%.6f", value));
                waveformData.setJitterAmount(value);
                updatePreview();
            } catch (NumberFormatException e) {
                jitterAmountField.setText("0");
            }
        });
        driftRateField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0, Math.min(10, value));
                driftRateField.setText(String.format("%.2f", value));
                waveformData.setDriftRate(value);
                updatePreview();
            } catch (NumberFormatException e) {
                driftRateField.setText("0");
            }
        });
        dcOffsetField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(-1.0, Math.min(1.0, value));
                dcOffsetField.setText(String.format("%.2f", value));
                waveformData.setDcOffset(value);
                updatePreview();
            } catch (NumberFormatException e) {
                dcOffsetField.setText("0");
            }
        });
        dutyCycleField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(10, Math.min(90, value));
                dutyCycleField.setText(String.format("%.0f", value));
                waveformData.setDutyCycle(value);
                updatePreview();
            } catch (NumberFormatException e) {
                dutyCycleField.setText("50");
            }
        });
        burstCyclesField.textProperty().addListener((obs, old, newVal) -> {
            try {
                int value = Integer.parseInt(newVal);
                value = Math.max(1, Math.min(1000, value));
                burstCyclesField.setText(String.valueOf(value));
                waveformData.setBurstCycles(value);
                updatePreview();
            } catch (NumberFormatException e) {
                burstCyclesField.setText("1");
            }
        });
        burstPeriodField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0.0001, value);
                burstPeriodField.setText(String.format("%.6f", value));
                waveformData.setBurstPeriod(value);
                updatePreview();
            } catch (NumberFormatException e) {
                burstPeriodField.setText("0.001");
            }
        });
        startFreqField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(1, value);
                startFreqField.setText(String.format("%.0f", value));
                waveformData.setStartFreq(value);
                updatePreview();
            } catch (NumberFormatException e) {
                startFreqField.setText("1000");
            }
        });
        stopFreqField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(1, value);
                stopFreqField.setText(String.format("%.0f", value));
                waveformData.setStopFreq(value);
                updatePreview();
            } catch (NumberFormatException e) {
                stopFreqField.setText("2000");
            }
        });
        sweepDurationField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0.0001, value);
                sweepDurationField.setText(String.format("%.6f", value));
                waveformData.setSweepDuration(value);
                updatePreview();
            } catch (NumberFormatException e) {
                sweepDurationField.setText("0.001");
            }
        });
        phaseNoiseField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                value = Math.max(0, Math.min(30, value));
                phaseNoiseField.setText(String.format("%.2f", value));
                waveformData.setPhaseNoise(value);
                updatePreview();
            } catch (NumberFormatException e) {
                phaseNoiseField.setText("0");
            }
        });

        // Bind CheckBox and ComboBox
        iqSignal.selectedProperty().addListener((obs, old, newVal) -> {
            waveformData.setIqSignal(newVal);
            updatePreview();
        });
        syncChannels.selectedProperty().addListener((obs, old, newVal) -> {
            waveformData.setSyncChannels(newVal);
            updatePreview();
        });
        channelSelect.valueProperty().addListener((obs, old, newVal) -> {
            waveformData.setSelectedChannel(newVal);
            updatePreview();
        });
        modulationType.valueProperty().addListener((obs, old, newVal) -> {
            updatePreview();
        });
        noiseType.valueProperty().addListener((obs, old, newVal) -> {
            waveformData.setNoiseType(newVal);
            updatePreview();
        });
        quantizationNoise.selectedProperty().addListener((obs, old, newVal) -> {
            waveformData.setQuantizationNoise(newVal);
            updatePreview();
        });
        burstMode.selectedProperty().addListener((obs, old, newVal) -> {
            waveformData.setBurstMode(newVal);
            updatePreview();
        });
        frequencySweep.selectedProperty().addListener((obs, old, newVal) -> {
            waveformData.setFrequencySweep(newVal);
            updatePreview();
        });
        sweepType.valueProperty().addListener((obs, old, newVal) -> {
            waveformData.setSweepType(newVal);
            updatePreview();
        });

        resolutionCombo.setOnAction(e -> waveformData.setResolution(resolutionCombo.getValue()));
        durationField.textProperty().addListener((obs, old, newVal) -> updateSamples());
        samplesField.textProperty().addListener((obs, old, newVal) -> updateDuration());

        /// Initialize noise generator
        noiseGenerator.setWaveformData(waveformData);

        /// Debounce preview updates
        debounce.setOnFinished(e -> generateWaveform(false));
        waveformType.valueProperty().addListener((obs, old, newVal) -> updatePreview());
    }

    @FXML
    private void importCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                waveformGenerator.importCsv(file);
                drawWaveform(waveformGenerator.getWaveform());
            } catch (Exception e) {
                showAlert("Error", "Failed to import CSV: " + e.getMessage());
            }
        }
    }

    @FXML
    private void importNoiseCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                noiseGenerator.importNoiseCsv(file);
                updatePreview();
            } catch (Exception e) {
                showAlert("Error", "Failed to import noise CSV: " + e.getMessage());
            }
        }
    }

    @FXML
    private void toggleDrawMode() {
        if (drawToggle.isSelected()) {
            waveformGenerator = new WaveformGenerator();
            waveformGenerator.setWaveformData(waveformData);
            waveformCanvas.setOnMouseDragged(e -> {
                GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
                gc.setFill(Color.YELLOW);
                double x = e.getX();
                double y = (1 - (e.getY() / waveformCanvas.getHeight())) * 2 - 1;
                gc.fillOval(x, e.getY(), 5, 5);
                waveformGenerator.addCustomPoint(x / waveformCanvas.getWidth(), y);
            });
        } else {
            waveformCanvas.setOnMouseDragged(null);
            drawGrid();
        }
    }

    @FXML
    private void clearCanvas() {
        waveformGenerator = new WaveformGenerator();
        waveformGenerator.setWaveformData(waveformData);
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight());
        drawGrid();
        drawToggle.setSelected(false);
        waveformCanvas.setOnMouseDragged(null);
    }

    @FXML
    private void generateWaveform() {
        generateWaveform(true);
    }

    private void generateWaveform(boolean applyModulation) {
        String type = waveformType.getValue();
        if (type == null) {
            if (applyModulation) showAlert("Warning", "Please select a waveform type.");
            return;
        }

        if (type.equals("Custom") && waveformGenerator.getWaveform() == null) {
            if (applyModulation) showAlert("Warning", "Please draw a custom waveform or import a CSV.");
            return;
        }

        try {
            waveformGenerator.setWaveformData(waveformData);
            double[] waveform = waveformGenerator.generateWaveform(type, waveformData);
            String modType = modulationType.getValue();
            if (modType != null && !modType.equals("None")) {
                waveform = modulationManager.applyModulation(waveform, modType, waveformData);
            }
            /// Apply noise
            String noiseType = waveformData.getNoiseType();
            double noiseAmplitude = waveformData.getNoiseAmplitude();
            if (noiseType != null && !noiseType.equals("None") && noiseAmplitude > 0) {
                double[] noise = noiseGenerator.generateNoise(noiseType, noiseAmplitude, waveform.length);
                for (int i = 0; i < waveform.length; i++) {
                    waveform[i] = Math.max(-1, Math.min(1, waveform[i] + noise[i]));
                }
            }
            drawWaveform(waveform);
        } catch (Exception e) {
            if (applyModulation) showAlert("Error", "Failed to generate waveform: " + e.getMessage());
        }
    }

    private void updatePreview() {
        if (!waveformType.getValue().equals("Custom")) {
            debounce.playFromStart();
        }
    }

    private void drawGrid() {
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        double width = waveformCanvas.getWidth();
        double height = waveformCanvas.getHeight();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);
        gc.setStroke(Color.web("#333333"));
        gc.setLineWidth(0.5);
        for (double x = 0; x <= width; x += 20) {
            gc.setLineWidth(x % 100 == 0 ? 1.0 : 0.5);
            gc.strokeLine(x, 0, x, height);
        }
        for (double y = 0; y <= height; y += 20) {
            gc.setLineWidth(y % 100 == 0 ? 1.0 : 0.5);
            gc.strokeLine(0, y, width, y);
        }
        gc.setStroke(Color.web("#666666"));
        gc.setLineWidth(1.5);
        gc.strokeLine(0, height / 2, width, height / 2);
        gc.strokeLine(width / 2, 0, width / 2, height);
        gc.setFill(Color.WHITE);
        gc.setFont(new javafx.scene.text.Font("Courier New", 14));
        gc.fillText("1.0", width / 2 + 5, 10);
        gc.fillText("0.0", width / 2 + 5, height / 2 + 5);
        gc.fillText("-1.0", width / 2 + 5, height - 5);
        double duration = waveformData.getDuration();
        gc.fillText("0", 5, height / 2 + 15);
        gc.fillText(String.format("%.3fs", duration / 2), width / 2 - 20, height / 2 + 15);
        gc.fillText(String.format("%.3fs", duration), width - 30, height / 2 + 15);
    }

    private void drawWaveform(double[] waveform) {
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        double width = waveformCanvas.getWidth();
        double height = waveformCanvas.getHeight();
        drawGrid();
        gc.setStroke(Color.web("#FFFF00"));
        gc.setLineWidth(2);
        gc.beginPath();
        for (int i = 0; i < waveform.length; i++) {
            double x = (double) i / waveform.length * width;
            double y = (1 - waveform[i]) * height / 2;
            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }
        gc.stroke();
    }

    private void updateSamples() {
        try {
            double duration = Double.parseDouble(durationField.getText());
            if (duration <= 0) throw new NumberFormatException("Duration must be positive");
            double rate = waveformData.getSamplingRate();
            int samples = (int) (duration * rate);
            samplesField.setText(String.valueOf(samples));
            waveformData.setSamples(samples);
            updatePreview();
        } catch (NumberFormatException e) {
            samplesField.setText("");
            showAlert("Error", "Invalid duration: " + e.getMessage());
        }
    }

    private void updateDuration() {
        try {
            int samples = Integer.parseInt(samplesField.getText());
            if (samples <= 0) throw new NumberFormatException("Samples must be positive");
            double rate = waveformData.getSamplingRate();
            double duration = samples / rate;
            durationField.setText(String.format("%.6f", duration));
            waveformData.setSamples(samples);
            updatePreview();
        } catch (NumberFormatException e) {
            durationField.setText("");
            showAlert("Error", "Invalid samples: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}