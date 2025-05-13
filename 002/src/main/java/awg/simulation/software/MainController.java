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
    @FXML private ComboBox<String> waveformType, resolutionCombo, channelSelect, modulationType, noiseType;
    @FXML private TextField samplingRateField, durationField, samplesField, phaseOffsetField, carrierFreqField, modIndexField;
    @FXML private TextField amplitudeField, frequencyField, phaseField, harmonic1AmpField, harmonic1FreqField;
    @FXML private TextField noiseAmplitudeField, jitterAmountField, driftRateField;
    @FXML private Slider samplingRateSlider, amplitudeSlider, frequencySlider, phaseSlider, harmonic1AmpSlider, harmonic1FreqSlider;
    @FXML private CheckBox syncChannels, iqSignal, quantizationNoise;
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

        /// Bind text fields to waveformData
        amplitudeField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setAmplitude(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                amplitudeField.setText("1.0");
            }
        });
        frequencyField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setFrequency(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                frequencyField.setText("1000");
            }
        });
        phaseField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setPhase(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                phaseField.setText("0");
            }
        });
        harmonic1AmpField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setHarmonic1Amp(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                harmonic1AmpField.setText("0");
            }
        });
        harmonic1FreqField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setHarmonic1Freq(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                harmonic1FreqField.setText("2");
            }
        });
        carrierFreqField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setCarrierFreq(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                carrierFreqField.setText("10000");
            }
        });
        modIndexField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setModIndex(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                modIndexField.setText("1.0");
            }
        });
        phaseOffsetField.textProperty().addListener((obs, old, newVal) -> {
            try {
                waveformData.setPhaseOffset(Double.parseDouble(newVal));
                updatePreview();
            } catch (NumberFormatException e) {
                phaseOffsetField.setText("0");
            }
        });
        noiseAmplitudeField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                waveformData.setNoiseAmplitude(Math.max(0, Math.min(0.5, value)));
                updatePreview();
            } catch (NumberFormatException e) {
                noiseAmplitudeField.setText("0");
            }
        });
        jitterAmountField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                waveformData.setJitterAmount(Math.max(0, Math.min(0.001, value)));
                updatePreview();
            } catch (NumberFormatException e) {
                jitterAmountField.setText("0");
            }
        });
        driftRateField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                waveformData.setDriftRate(Math.max(0, Math.min(10, value)));
                updatePreview();
            } catch (NumberFormatException e) {
                driftRateField.setText("0");
            }
        });

        /// Bind CheckBox and ComboBox
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
            waveformGenerator.importCsv(file);
            drawWaveform(waveformGenerator.getWaveform());
        }
    }

    @FXML
    private void importNoiseCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            noiseGenerator.importNoiseCsv(file);
            updatePreview();
        }
    }

    @FXML
    private void toggleDrawMode() {
        if (drawToggle.isSelected()) {
            waveformGenerator = new WaveformGenerator();
            waveformGenerator.setWaveformData(waveformData);
            waveformCanvas.setOnMouseDragged(e -> {
                GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
                gc.setFill(Color.GREEN);
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
        gc.setFill(Color.GREEN);
        gc.setFont(new javafx.scene.text.Font("Consolas", 10));
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
        gc.setStroke(Color.GREEN);
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
            double rate = waveformData.getSamplingRate();
            int samples = (int) (duration * rate);
            samplesField.setText(String.valueOf(samples));
            waveformData.setSamples(samples);
            updatePreview();
        } catch (NumberFormatException e) {
            samplesField.setText("");
        }
    }

    private void updateDuration() {
        try {
            int samples = Integer.parseInt(samplesField.getText());
            double rate = waveformData.getSamplingRate();
            double duration = samples / rate;
            durationField.setText(String.format("%.6f", duration));
            waveformData.setSamples(samples);
            updatePreview();
        } catch (NumberFormatException e) {
            durationField.setText("");
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