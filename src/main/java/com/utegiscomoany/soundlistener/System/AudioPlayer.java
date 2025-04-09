package com.utegiscomoany.soundlistener.System;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private double volume = 0.5;

    public void play(String filePath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media = new Media(new File(filePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void setVolume(double volume) {
        this.volume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public double getVolume() {
        return volume;
    }
}