module edu.citu.procrammers.eva {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.citu.procrammers.eva to javafx.fxml;
    exports edu.citu.procrammers.eva;
}