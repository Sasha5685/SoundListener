module com.utegiscomoany.soundlistener {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.xml;

    opens com.utegiscomoany.soundlistener to javafx.fxml;
    exports com.utegiscomoany.soundlistener;
    opens com.utegiscomoany.soundlistener.System to javafx.fxml;
    exports com.utegiscomoany.soundlistener.System;
}