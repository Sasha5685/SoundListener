package com.utegiscomoany.soundlistener;

import com.utegiscomoany.soundlistener.Creator.AppInitializer;
import com.utegiscomoany.soundlistener.Creator.MusicFileHandler;
import com.utegiscomoany.soundlistener.System.AudioPlayer;
import com.utegiscomoany.soundlistener.System.MusicPageParser;
import com.utegiscomoany.soundlistener.System.PageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.util.List;

public class MainController {
    @FXML private AnchorPane mainPane;
    @FXML private Label dropLabel;
    @FXML private FlowPane musicContainer;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Slider volumeSlider;
    @FXML private Pane pagesContainer;
    @FXML private Pane createPagePane;

    private AudioPlayer audioPlayer = new AudioPlayer();
    private String selectedTrackPath;
    private String currentPage = "MainPage";
    private Pane selectedPagePane = null;

    @FXML
    public void initialize() {
        AppInitializer.initializeAppDirectories();
        setupDragAndDrop();
        setupControls();
        loadPages();
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
                MusicFileHandler.handleDroppedFiles(db.getFiles(), currentPage);
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

        // Настройка громкости
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            audioPlayer.setVolume(newVal.doubleValue() / 100);
        });
        volumeSlider.setValue(50);

        createPagePane.setOnMouseClicked(e -> showCreatePageDialog());
    }

    private void showCreatePageDialog() {
        TextInputDialog dialog = new TextInputDialog("New Page");
        dialog.setTitle("Create New Page");
        dialog.setHeaderText("Enter page name:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                PageManager.addPage(name);
                loadPages();
            }
        });
    }

    private void loadPages() {
        pagesContainer.getChildren().clear();

        List<String> pages = PageManager.getPages();
        if (!pages.contains("MainPage")) {
            PageManager.addPage("MainPage");
            pages = PageManager.getPages();
        }

        double yPos = 0;
        for (String page : pages) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utegiscomoany/soundlistener/page_item.fxml"));
                Pane pagePane = loader.load();
                Label pageLabel = (Label) pagePane.lookup("#pageNameLabel");
                Button deleteButton = (Button) pagePane.lookup("#deleteButton");

                pageLabel.setText(page);
                pagePane.setLayoutY(yPos);

                if (!"MainPage".equals(page)) {
                    deleteButton.setVisible(true);
                    deleteButton.setOnAction(e -> deletePage(page));
                }

                setupPageSelection(page, pagePane);
                pagesContainer.getChildren().add(pagePane);
                yPos += 75.0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        createPagePane.setLayoutY(yPos);
    }

    private void deletePage(String page) {
        PageManager.removePage(page);
        loadPages();
        if (currentPage.equals(page)) {
            currentPage = "MainPage";
            loadMusicTracks();
        }
    }

    private void setupPageSelection(String page, Pane pagePane) {
        pagePane.setOnMouseClicked(e -> {
            if (selectedPagePane != null) {
                selectedPagePane.setStyle("-fx-background-color: #e74c3c;");
            }
            pagePane.setStyle("-fx-background-color: #3498db;");
            selectedPagePane = pagePane;
            currentPage = page;
            loadMusicTracks();
        });

        if (page.equals(currentPage)) {
            pagePane.setStyle("-fx-background-color: #3498db;");
            selectedPagePane = pagePane;
        }
    }

    private void loadMusicTracks() {
        musicContainer.getChildren().clear();
        musicContainer.setHgap(10);
        musicContainer.setVgap(10);
        musicContainer.setStyle("-fx-padding: 10;");

        String musicPagePath = System.getProperty("user.home") + "/Documents/SoundListener/" + currentPage + "/MusicPage.page";
        List<MusicPageParser.MusicTrack> tracks = MusicPageParser.parse(musicPagePath);

        dropLabel.setText(tracks.isEmpty() ? "Drag music files here" : "");

        for (MusicPageParser.MusicTrack track : tracks) {
            try {
                HBox trackBox = createTrackBox(track);
                musicContainer.getChildren().add(trackBox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HBox createTrackBox(MusicPageParser.MusicTrack track) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utegiscomoany/soundlistener/track_item.fxml"));
        HBox trackBox = loader.load();
        Label nameLabel = (Label) trackBox.lookup("#trackName");
        Label durationLabel = (Label) trackBox.lookup("#trackDuration");

        nameLabel.setText(track.getName());
        durationLabel.setText("Loading...");
        trackBox.setStyle("-fx-background-color: #3a3a3a;");

        loadDurationAsync(track.getPath(), durationLabel);
        setupTrackSelection(track, trackBox);

        return trackBox;
    }

    private void setupTrackSelection(MusicPageParser.MusicTrack track, HBox trackBox) {
        trackBox.setOnMouseClicked(e -> {
            selectedTrackPath = track.getPath();
            musicContainer.getChildren().forEach(node ->
                    node.setStyle("-fx-background-color: #3a3a3a;"));
            trackBox.setStyle("-fx-background-color: #2980b9;");
            audioPlayer.play(selectedTrackPath);
        });

        trackBox.setOnMouseEntered(e -> {
            if (!trackBox.getStyle().contains("#2980b9")) {
                trackBox.setStyle("-fx-background-color: #4a4a4a;");
            }
        });

        trackBox.setOnMouseExited(e -> {
            if (!trackBox.getStyle().contains("#2980b9")) {
                trackBox.setStyle("-fx-background-color: #3a3a3a;");
            }
        });
    }

    private void loadDurationAsync(String filePath, Label durationLabel) {
        new Thread(() -> {
            try {
                Media media = new Media(new File(filePath).toURI().toString());
                MediaPlayer tempPlayer = new MediaPlayer(media);

                tempPlayer.setOnReady(() -> {
                    Duration duration = media.getDuration();
                    String durationText = formatDuration(duration);
                    Platform.runLater(() -> durationLabel.setText(durationText));
                    tempPlayer.dispose();
                });
            } catch (Exception e) {
                Platform.runLater(() -> durationLabel.setText("0:00"));
            }
        }).start();
    }

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
}