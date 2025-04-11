package com.utegiscomoany.soundlistener.System;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PageManager {
    private static final String SETTINGS_FILE = System.getProperty("user.home") + "/Documents/SoundListener/SoundSettings.settings";

    public static void addPage(String pageName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            File xmlFile = new File(SETTINGS_FILE);

            if (xmlFile.exists()) {
                doc = builder.parse(xmlFile);
            } else {
                doc = builder.newDocument();
                doc.appendChild(doc.createElement("allPages"));
            }

            Element root = doc.getDocumentElement();
            Element page = doc.createElement("page");
            page.setAttribute("name", pageName);
            root.appendChild(page);

            saveDocument(doc, xmlFile);
            createPageDirectory(pageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removePage(String pageName) {
        if ("MainPage".equals(pageName)) {
            return; // Не удаляем MainPage
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(SETTINGS_FILE));

            NodeList pages = doc.getElementsByTagName("page");
            for (int i = 0; i < pages.getLength(); i++) {
                Element page = (Element) pages.item(i);
                if (page.getAttribute("name").equals(pageName)) {
                    page.getParentNode().removeChild(page);
                    break;
                }
            }

            saveDocument(doc, new File(SETTINGS_FILE));
            deletePageDirectory(pageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPages() {
        List<String> pages = new ArrayList<>();
        try {
            File xmlFile = new File(SETTINGS_FILE);
            if (!xmlFile.exists()) return pages;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList pageNodes = doc.getElementsByTagName("page");
            for (int i = 0; i < pageNodes.getLength(); i++) {
                Element page = (Element) pageNodes.item(i);
                pages.add(page.getAttribute("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pages;
    }

    private static void saveDocument(Document doc, File file) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private static void createPageDirectory(String pageName) throws IOException {
        String dirPath = System.getProperty("user.home") + "/Documents/SoundListener/" + pageName;
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);

            // Create page file
            Path pageFile = Paths.get(dirPath + "/MusicPage.page");
            Files.createFile(pageFile);

            // Write initial XML structure
            String initialContent = "<allMusic>\n</allMusic>";
            Files.write(pageFile, initialContent.getBytes());
        }
    }

    private static void deletePageDirectory(String pageName) throws IOException {
        String dirPath = System.getProperty("user.home") + "/Documents/SoundListener/" + pageName;
        Path dir = Paths.get(dirPath);
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}