module edu.citu.procrammers.eva {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires spring.security.crypto;
    requires mysql.connector.java;
    requires java.desktop;
    requires jdk.compiler;
    requires org.json;
    requires java.net.http;


    opens edu.citu.procrammers.eva to javafx.fxml;
    exports edu.citu.procrammers.eva;
    exports edu.citu.procrammers.eva.controllers;
    opens edu.citu.procrammers.eva.controllers to javafx.fxml;
    exports edu.citu.procrammers.eva.data;
    opens edu.citu.procrammers.eva.data to javafx.fxml;
    exports edu.citu.procrammers.eva.utils;
    opens edu.citu.procrammers.eva.utils to javafx.fxml;

    opens edu.citu.procrammers.eva.models.data_structures to javafx.fxml;
    exports edu.citu.procrammers.eva.models.data_structures;

    opens edu.citu.procrammers.eva.utils.visuals to javafx.fxml;
    exports edu.citu.procrammers.eva.utils.visuals;
    exports edu.citu.procrammers.eva.models.user;
    opens edu.citu.procrammers.eva.models.user to javafx.fxml;

}