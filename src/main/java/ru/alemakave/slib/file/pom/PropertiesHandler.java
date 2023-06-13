package ru.alemakave.slib.file.pom;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

public class PropertiesHandler extends DefaultHandler {
    private boolean isEntered = false;
    private HashMap<String, String> properties = new HashMap<>();
    private String lastElementName;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("properties")) isEntered = true;
        lastElementName = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String information = new String(ch, start, length);

        information = information.replace("\n", "").trim();

        if (!information.isEmpty()) {
            if (isEntered) {
                properties.put(lastElementName, information);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("properties")) isEntered = false;
    }

    public String getPropertyString(String name) {
        return properties.get(name);
    }
}
