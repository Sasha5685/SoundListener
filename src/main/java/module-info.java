module com.utegiscomoany.soundlistener {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires javafx.media;


    opens com.utegiscomoany.soundlistener to javafx.fxml;
    exports com.utegiscomoany.soundlistener;
    opens com.utegiscomoany.soundlistener.System to javafx.fxml;
    exports com.utegiscomoany.soundlistener.System;
}