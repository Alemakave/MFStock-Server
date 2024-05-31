package ru.alemakave.slib.utils.version;

import org.junit.jupiter.api.Test;
import ru.alemakave.slib.vc.Version;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.alemakave.slib.utils.TestData.getCompareVersionData;
import static ru.alemakave.slib.utils.TestData.getValidSemanticVersion;

public class VersionTest {
    @Test
    public void parseTest() {
        System.out.println("Parse test: ");

        Map<String, Version> testMapData = getValidSemanticVersion();
        int testNumber = 1;
        for (String versionAsString : testMapData.keySet()) {
            System.out.printf("\t[%d/%d] Test \"%s\" == \"%s\"", testNumber, testMapData.size(), versionAsString, testMapData.get(versionAsString));
            assertEquals(testMapData.get(versionAsString), Version.parse(versionAsString));
            System.out.println(" [Ok]");
            testNumber++;

        }
        System.out.println("Parse test [Done]");
    }

    @Test
    public void compareTest() {
        System.out.println("Compare test: ");

        System.out.println("  Test part 1");

        System.out.printf("\t[1/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0-alpha", "1.0.0-alpha.1", -1);
        assertEquals(-1, Version.parse("1.0.0-alpha").compareTo(Version.parse("1.0.0-alpha.1")));
        System.out.printf("\t[2/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0-alpha.1", "1.0.0-alpha.beta", -1);
        assertEquals(-1, Version.parse("1.0.0-alpha.1").compareTo(Version.parse("1.0.0-alpha.beta")));
        System.out.printf("\t[3/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0-alpha.beta", "1.0.0-beta", -1);
        assertEquals(-1, Version.parse("1.0.0-alpha.beta").compareTo(Version.parse("1.0.0-beta")));
        System.out.printf("\t[4/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0-beta", "1.0.0-beta.2", -1);
        assertEquals(-1, Version.parse("1.0.0-beta").compareTo(Version.parse("1.0.0-beta.2")));
        System.out.printf("\t[5/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0-beta.2", "1.0.0-beta.11", -1);
        assertEquals(-1, Version.parse("1.0.0-beta.2").compareTo(Version.parse("1.0.0-beta.11")));
        System.out.printf("\t[6/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0-beta.11", "1.0.0-rc.1", -1);
        assertEquals(-1, Version.parse("1.0.0-beta.11").compareTo(Version.parse("1.0.0-rc.1")));
        System.out.printf("\t[7/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0-rc.1", "1.0.0", -1);
        assertEquals(-1, Version.parse("1.0.0-rc.1").compareTo(Version.parse("1.0.0")));
        System.out.printf("\t[8/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0", "2.0.0", -1);
        assertEquals(-1, Version.parse("1.0.0").compareTo(Version.parse("2.0.0")));
        System.out.printf("\t[9/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0", "1.0.0", 0);
        assertEquals(0, Version.parse("1.0.0").compareTo(Version.parse("1.0.0")));
        System.out.printf("\t[10/10] Compare \"%s\" to \"%s\" expected: %s\n", "1.0.0", "1.0.0", 0);
        assertEquals(0, Version.parse("1.0.0+meta1").compareTo(Version.parse("1.0.0+meta2")));

        System.out.println("  Test part 2");

        Map<Version[], Integer> testMapData = getCompareVersionData();
        int testNumber = 1;

        for (Version[] comparableVersion : testMapData.keySet()) {
            System.out.printf("\t[%d/%d] Compare \"%s\" to \"%s\" expected: %s", testNumber, testMapData.size(), comparableVersion[0], comparableVersion[1], testMapData.get(comparableVersion));
            assertEquals(testMapData.get(comparableVersion), comparableVersion[0].compareTo(comparableVersion[1]));
            System.out.println(" [Ok]");
            testNumber++;
        }
        System.out.println("Compare test [Done]");
    }
}