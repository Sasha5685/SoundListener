package com.utegiscomoany.soundlistener.System;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPageParser {
    public static List<MusicTrack> parse(String filePath) {
        List<MusicTrack> tracks = new ArrayList<>();

        try {
            File file = new File(filePath);
            if (!file.exists()) return tracks;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList trackNodes = doc.getElementsByTagName("track");

            for (int i = 0; i < trackNodes.getLength(); i++) {
                Node trackNode = trackNodes.item(i);
                if (trackNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element trackElement = (Element) trackNode;

                    String name = getElementText(trackElement, "name");
                    String type = getElementText(trackElement, "type");
                    String path = getElementText(trackElement, "path");

                    if (name != null && type != null && path != null) {
                        tracks.add(new MusicTrack(name, type, path));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing music page: " + e.getMessage());
            e.printStackTrace();
        }

        return tracks;
    }

    private static String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    public static class MusicTrack {
        private final String name;
        private final String type;
        private final String path;

        public MusicTrack(String name, String type, String path) {
            this.name = name;
            this.type = type;
            this.path = path;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public String getPath() { return path; }
    }
}