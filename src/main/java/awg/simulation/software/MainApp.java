package awg.simulation.software;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WaveformView.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 780);
        scene.getStylesheets().add(getClass().getResource("waveform.css").toExternalForm());
        primaryStage.setTitle("Arbitrary Waveform Generator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}