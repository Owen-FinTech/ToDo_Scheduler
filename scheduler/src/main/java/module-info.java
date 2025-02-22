module com.scheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.security.crypto;
    requires transitive java.sql;
    requires transitive javafx.graphics;

    opens com.scheduler to javafx.fxml;
    opens com.scheduler.ui to javafx.fxml;
    
    exports com.scheduler;
    exports com.scheduler.model;
}
