package com.utegiscomoany.soundlistener;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("SoundMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 740);
        stage.setMinWidth(850);
        stage.setMinHeight(740);
        stage.setTitle("Sound Listener");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}