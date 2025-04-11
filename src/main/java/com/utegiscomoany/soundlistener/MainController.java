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
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            audioPlayer.setVolume(newVal.doubleValue() / 100);
        });
        volumeSlider.setValue(50);

        // Обработчик создания новой страницы
        createPagePane.setOnMouseClicked(e -> {
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
        });
    }

    private void loadPages() {
        pagesContainer.getChildren().clear();

        // Проверяем, существует ли MainPage, если нет - создаем
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

                // Показываем кнопку удаления только для не-MainPage страниц
                if (!"MainPage".equals(page)) {
                    deleteButton.setVisible(true);
                    deleteButton.setOnAction(e -> {
                        PageManager.removePage(page);
                        loadPages();
                        if (currentPage.equals(page)) {
                            currentPage = "MainPage";
                            loadMusicTracks();
                        }
                    });
                }

                pagePane.setOnMouseClicked(e -> {
                    // Сбрасываем стиль предыдущей выбранной страницы
                    if (selectedPagePane != null && !selectedPagePane.equals(pagePane)) {
                        selectedPagePane.setStyle("-fx-background-color: #e74c3c;");
                    }

                    // Устанавливаем стиль для выбранной страницы
                    pagePane.setStyle("-fx-background-color: #3498db;");
                    selectedPagePane = pagePane;

                    // Загружаем треки для выбранной страницы
                    currentPage = page;
                    loadMusicTracks();
                });

                // Выделяем текущую страницу
                if (page.equals(currentPage)) {
                    pagePane.setStyle("-fx-background-color: #3498db;");
                    selectedPagePane = pagePane;
                }

                pagesContainer.getChildren().add(pagePane);
                yPos += 75.0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Позиционируем кнопку создания страницы
        createPagePane.setLayoutY(yPos);
    }

    private void loadMusicTracks() {
        musicContainer.getChildren().clear();
        musicContainer.setHgap(10);
        musicContainer.setVgap(10);
        musicContainer.setStyle("-fx-padding: 10;");

        String musicPagePath = System.getProperty("user.home") + "/Documents/SoundListener/" + currentPage + "/MusicPage.page";
        List<MusicPageParser.MusicTrack> tracks = MusicPageParser.parse(musicPagePath);

        if (tracks.isEmpty()) {
            dropLabel.setText("Drag music files here");
        } else {
            dropLabel.setText("");
        }

        for (MusicPageParser.MusicTrack track : tracks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/utegiscomoany/soundlistener/track_item.fxml"));
                HBox trackBox = loader.load();
                Label nameLabel = (Label) trackBox.lookup("#trackName");
                Label durationLabel = (Label) trackBox.lookup("#trackDuration");

                nameLabel.setText(track.getName());
                durationLabel.setText("Loading..."); // Временная надпись

                // Загружаем длительность асинхронно
                loadDurationAsync(track.getPath(), durationLabel);

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

                musicContainer.getChildren().add(trackBox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDurationAsync(String filePath, Label durationLabel) {
        new Thread(() -> {
            try {
                Media media = new Media(new File(filePath).toURI().toString());
                MediaPlayer tempPlayer = new MediaPlayer(media);

                tempPlayer.setOnReady(() -> {
                    Duration duration = media.getDuration();
                    int minutes = (int) duration.toMinutes();
                    int seconds = (int) (duration.toSeconds() % 60);
                    String durationText = String.format("%d:%02d", minutes, seconds);

                    // Обновляем UI в JavaFX Application Thread
                    Platform.runLater(() -> durationLabel.setText(durationText));

                    tempPlayer.dispose();
                });
            } catch (Exception e) {
                Platform.runLater(() -> durationLabel.setText("0:00"));
            }
        }).start();
    }
}