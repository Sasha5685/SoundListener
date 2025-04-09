module com.utegiscomoany.soundlistener {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.utegiscomoany.soundlistener to javafx.fxml;
    exports com.utegiscomoany.soundlistener;
}