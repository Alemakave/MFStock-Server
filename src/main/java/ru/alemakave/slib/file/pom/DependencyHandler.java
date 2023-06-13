package ru.alemakave.slib.file.pom;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.alemakave.slib.utils.Lib;
import ru.alemakave.slib.utils.LibUtils;
import ru.alemakave.slib.utils.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DependencyHandler extends DefaultHandler {
    public static boolean CLEAR_MAVEN_META = false;

    private boolean isEntered = false;
    private String lastElementName;
    private String groupID;
    private String artifactID;
    private String version;
    private String scope;
    private List<Lib> dependencies = new ArrayList<>();
    private PropertiesHandler propertiesHandler;
    private LibUtils libUtils;

    public DependencyHandler(PropertiesHandler handler, LibUtils libUtils) {
        propertiesHandler = handler;
        this.libUtils = libUtils;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("dependency")) isEntered = true;
        lastElementName = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String information = new String(ch, start, length);

        information = information.replace("\n", "").trim();

        if (!information.isEmpty()) {
            if (isEntered) {
                if (lastElementName.equals("groupId")) {
                    groupID = information;
                }

                if (lastElementName.equals("artifactId")) {
                    artifactID = information;
                }

                if (lastElementName.equals("version")) {
                    if (information.startsWith("$")) {
                        information = propertiesHandler.getPropertyString(information.replace("${", "").replace("}", ""));
                    }
                    version = information;
                }

                if (lastElementName.equals("scope")) {
                    scope = information;
                }

                if (lastElementName.equals("optional") && information.equalsIgnoreCase("true")) {
                    scope = "test";
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("dependency")) isEntered = false;
        else return;

        if (!((scope != null && (scope.equals("provided") || scope.equals("test") || scope.equals("system") || scope.equals("compile")))))
            if (version == null || version.isEmpty()) {
                try {
                    version = getLastVersion();
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
            }

        if (groupID != null && !groupID.isEmpty() && artifactID != null && !artifactID.isEmpty() && version != null && !version.isEmpty()) {
            if (!((scope != null && (scope.equals("provided") || scope.equals("test") || scope.equals("system") || scope.equals("compile")))))
                dependencies.add(new Lib(groupID, artifactID, version));
            groupID = null;
            artifactID = null;
            version = null;
            scope = null;
        }
    }

    public List<Lib> getDependencies() {
        return dependencies;
    }

    private String getLastVersion() throws ParserConfigurationException, SAXException, IOException {
        //libUtils.getMavenMetaDataFile(groupID, artifactID);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        VersionHandler versionHandler = new VersionHandler();
        File mavenMeta = libUtils.getMavenMetaDataFile(groupID, artifactID);
        if (mavenMeta == null) {
            throw new IOException("Maven meta is null");
        }
        parser.parse(mavenMeta, versionHandler);

        String version = versionHandler.version;

        if (CLEAR_MAVEN_META) {
            boolean deleted = mavenMeta.delete();
            if (!deleted) Logger.errorF(String.format("File \"%s\" not deleted!\n", mavenMeta.getName()));
        }

        return version;
    }

    private static class VersionHandler extends DefaultHandler {
        public String version;
        private String lastElemName;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            lastElemName = qName;
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            String information = new String(ch, start, length);

            information = information.replace("\n", "").trim();

            if (!information.isEmpty()) {
                if (lastElemName.equalsIgnoreCase("release"))
                    version = information;
            }
        }
    }
}
