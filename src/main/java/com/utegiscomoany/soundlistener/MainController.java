package com.utegiscomoany.soundlistener;

import com.utegiscomoany.soundlistener.Creator.AppInitializer;
import com.utegiscomoany.soundlistener.Creator.MusicFileHandler;
import com.utegiscomoany.soundlistener.System.AudioPlayer;
import com.utegiscomoany.soundlistener.System.MusicPageParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.io.File;
import java.util.List;

public class MainController {
    @FXML private AnchorPane mainPane;
    @FXML private Label dropLabel;
    @FXML private FlowPane musicContainer;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Slider volumeSlider;

    private AudioPlayer audioPlayer = new AudioPlayer();
    private String selectedTrackPath;

    @FXML
    public void initialize() {
        AppInitializer.initializeAppDirectories();
        setupDragAndDrop();
        setupControls();
        loadMusicTracks();
    }

    private void setupDragAndDrop() {
        mainPane.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        mainPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                MusicFileHandler.handleDroppedFiles(db.getFiles());
                loadMusicTracks();
                dropLabel.setText(db.getFiles().size() + " files added successfully!");
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void setupControls() {
        playButton.setOnAction(e -> {
            if (selectedTrackPath != null) {
                audioPlayer.play(selectedTrackPath);
            }
        });

        pauseButton.setOnAction(e -> audioPlayer.pause());

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            audioPlayer.setVolume(newVal.doubleValue() / 100);
        });
        volumeSlider.setValue(50);
    }

    private void loadMusicTracks() {
        musicContainer.getChildren().clear();

        String musicPagePath = System.getProperty("user.home") + "/Documents/SoundListener/MusicPage_1/MusicPage.page";
        List<MusicPageParser.MusicTrack> tracks = MusicPageParser.parse(musicPagePath);

        for (MusicPageParser.MusicTrack track : tracks) {
            try {
                VBox trackBox = FXMLLoader.load(getClass().getResource("/com/utegiscomoany/soundlistener/track_item.fxml"));
                Label nameLabel = (Label) trackBox.lookup("#trackName");
                nameLabel.setText(track.getName());

                trackBox.setOnMouseClicked(e -> {
                    selectedTrackPath = track.getPath();
                    // Подсветка выбранного трека
                    musicContainer.getChildren().forEach(node ->
                            node.setStyle("-fx-background-color: #34495e;"));
                    trackBox.setStyle("-fx-background-color: #2980b9;");
                });

                musicContainer.getChildren().add(trackBox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}