package com.utegiscomoany.soundlistener.Creator;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class MusicFileHandler {
    public static void handleDroppedFiles(List<File> files) {
        try {
            String musicDirPath = System.getProperty("user.home") + "/Documents/SoundListener/MusicPage_1";
            Path musicDir = Paths.get(musicDirPath);

            if (!Files.exists(musicDir)) {
                Files.createDirectories(musicDir);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Создаем или загружаем существующий документ
            Document doc;
            File xmlFile = new File(musicDirPath + "/MusicPage.page");
            Element root;

            if (xmlFile.exists()) {
                doc = builder.parse(xmlFile);
                root = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                root = doc.createElement("allMusic");
                doc.appendChild(root);
            }

            for (File file : files) {
                if (isAudioFile(file)) {
                    // Копируем файл
                    Path destination = musicDir.resolve(file.getName());
                    Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                    // Создаем элементы XML
                    Element track = doc.createElement("track");

                    Element name = doc.createElement("name");
                    name.setTextContent(file.getName().replaceFirst("[.][^.]+$", ""));

                    Element type = doc.createElement("type");
                    type.setTextContent(getFileExtension(file.getName()));

                    Element path = doc.createElement("path");
                    path.setTextContent(destination.toString());

                    track.appendChild(name);
                    track.appendChild(type);
                    track.appendChild(path);
                    root.appendChild(track);
                }
            }

            // Сохраняем XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".ogg");
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}