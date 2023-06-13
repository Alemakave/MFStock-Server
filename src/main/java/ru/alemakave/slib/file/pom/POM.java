package ru.alemakave.slib.file.pom;

import org.xml.sax.SAXException;
import ru.alemakave.slib.utils.Lib;
import ru.alemakave.slib.utils.LibUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;

// http://maven.apache.org/pom.html
public class POM {
    private DependencyHandler dependencyHandler;

    public POM(String filePath, LibUtils libUtils) throws ParserConfigurationException, SAXException, IOException {
        this(new File(filePath), libUtils);
    }

    public POM(File pom, LibUtils libUtils) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        PropertiesHandler propertiesHandler = new PropertiesHandler();
        parser.parse(pom, propertiesHandler);

        dependencyHandler = new DependencyHandler(propertiesHandler, libUtils);
        parser.parse(pom, dependencyHandler);
    }

    public List<Lib> getDependencies() {
        return dependencyHandler.getDependencies();
    }
}
