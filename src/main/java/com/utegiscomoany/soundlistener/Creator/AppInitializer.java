package com.utegiscomoany.soundlistener.Creator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppInitializer {
    private static final String APP_NAME = "SoundListener";
    private static final String SETTINGS_FILE = "SoundSettings.settings";
    private static final String MUSIC_PAGE_FOLDER = "MusicPage_1";
    private static final String MUSIC_PAGE_FILE = "MusicPage.page";

    public static void initializeAppDirectories() {
        try {
            // Get user's documents directory
            String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
            Path appDirectory = Paths.get(documentsPath, APP_NAME);

            // Create main app directory if it doesn't exist
            if (!Files.exists(appDirectory)) {
                Files.createDirectory(appDirectory);
                System.out.println("Created directory: " + appDirectory);
            }

            // Create settings file if it doesn't exist
            Path settingsFile = appDirectory.resolve(SETTINGS_FILE);
            if (!Files.exists(settingsFile)) {
                Files.createFile(settingsFile);
                System.out.println("Created file: " + settingsFile);
                // Write initial XML structure
                String initialContent = "<allPages>\n<page name=\"MainPage\"/>\n</allPages>";
                Files.write(settingsFile, initialContent.getBytes());
            }

            // Create MainPage directory if it doesn't exist
            Path mainPageDir = appDirectory.resolve("MainPage");
            if (!Files.exists(mainPageDir)) {
                Files.createDirectory(mainPageDir);
                System.out.println("Created directory: " + mainPageDir);

                Path musicPageFile = mainPageDir.resolve(MUSIC_PAGE_FILE);
                Files.createFile(musicPageFile);

                // Write initial XML structure
                String initialContent = "<allMusic>\n</allMusic>";
                Files.write(musicPageFile, initialContent.getBytes());
                System.out.println("Created file: " + musicPageFile);
            }
        } catch (IOException e) {
            System.err.println("Error initializing application directories: " + e.getMessage());
            e.printStackTrace();
        }
    }
}