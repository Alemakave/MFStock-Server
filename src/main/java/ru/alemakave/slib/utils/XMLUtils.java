package ru.alemakave.slib.utils;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class XMLUtils {
    public static Document readXMLDocumentFromInputStream(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(inputStream);

        document.getDocumentElement().normalize();

        return document;
    }
}
