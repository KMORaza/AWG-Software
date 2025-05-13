module com.example.awgsimulationsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.scripting;


    opens awg.simulation.software to javafx.fxml;
    exports awg.simulation.software;
}