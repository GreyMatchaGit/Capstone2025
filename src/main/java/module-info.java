module edu.citu.procrammers.eva {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens edu.citu.procrammers.eva to javafx.fxml;
    exports edu.citu.procrammers.eva;
    exports edu.citu.procrammers.eva.controllers;
    opens edu.citu.procrammers.eva.controllers to javafx.fxml;
    exports edu.citu.procrammers.eva.data;
    opens edu.citu.procrammers.eva.data to javafx.fxml;
    exports edu.citu.procrammers.eva.utils;
    opens edu.citu.procrammers.eva.utils to javafx.fxml;
    exports edu.citu.procrammers.eva.models.user;
    opens edu.citu.procrammers.eva.models.user to javafx.fxml;
}