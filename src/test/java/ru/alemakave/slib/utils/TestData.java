package ru.alemakave.slib.utils;

import ru.alemakave.slib.vc.Version;

import java.util.HashMap;
import java.util.Map;

public class TestData {
    public static Map<String, Version> getValidSemanticVersion() {
        Map<String, Version> testMapData = new HashMap<>();

        testMapData.put("0.0.4", new Version(0, 0, 4));
        testMapData.put("1.2.3", new Version(1, 2, 3));
        testMapData.put("10.20.30", new Version(10, 20, 30));
        testMapData.put("1.1.2-prerelease+meta", new Version(1, 1, 2, "prerelease", "meta"));
        testMapData.put("1.1.2+meta", new Version(1, 1, 2, null, "meta"));
        testMapData.put("1.1.2+meta-valid", new Version(1, 1, 2, null, "meta-valid"));
        testMapData.put("1.0.0-alpha", new Version(1, 0, 0, "alpha"));
        testMapData.put("1.0.0-beta", new Version(1, 0, 0, "beta"));
        testMapData.put("1.0.0-alpha.beta", new Version(1, 0, 0, "alpha.beta"));
        testMapData.put("1.0.0-alpha.beta.1", new Version(1, 0, 0, "alpha.beta.1"));
        testMapData.put("1.0.0-alpha.1", new Version(1, 0, 0, "alpha.1"));
        testMapData.put("1.0.0-alpha0.valid", new Version(1, 0, 0, "alpha0.valid"));
        testMapData.put("1.0.0-alpha.0valid", new Version(1, 0, 0, "alpha.0valid"));
        testMapData.put("1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay", new Version(1, 0, 0, "alpha-a.b-c-somethinglong", "build.1-aef.1-its-okay"));
        testMapData.put("1.0.0-rc.1+build.1", new Version(1, 0, 0, "rc.1", "build.1"));
        testMapData.put("2.0.0-rc.1+build.123", new Version(2, 0, 0, "rc.1", "build.123"));
        testMapData.put("1.2.3-beta", new Version(1, 2, 3, "beta"));
        testMapData.put("10.2.3-DEV-SNAPSHOT", new Version(10, 2, 3, "DEV-SNAPSHOT"));
        testMapData.put("1.2.3-SNAPSHOT-123", new Version(1, 2, 3, "SNAPSHOT-123"));
        testMapData.put("1.0.0", new Version(1, 0, 0));
        testMapData.put("2.0.0", new Version(2, 0, 0));
        testMapData.put("1.1.7", new Version(1, 1, 7));
        testMapData.put("2.0.0+build.1848", new Version(2, 0, 0, null, "build.1848"));
        testMapData.put("2.0.1-alpha.1227", new Version(2, 0, 1, "alpha.1227"));
        testMapData.put("1.0.0-alpha+beta", new Version(1, 0, 0, "alpha", "beta"));
        testMapData.put("1.2.3----RC-SNAPSHOT.12.9.1--.12+788", new Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "788"));
        testMapData.put("1.2.3----R-S.12.9.1--.12+meta", new Version(1, 2, 3, "---R-S.12.9.1--.12", "meta"));
        testMapData.put("1.2.3----RC-SNAPSHOT.12.9.1--.12", new Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12"));
        testMapData.put("1.0.0+0.build.1-rc.10000aaa-kk-0.1", new Version(1, 0, 0, null, "0.build.1-rc.10000aaa-kk-0.1"));
        testMapData.put("1.0.0-0A.is.legal", new Version(1, 0, 0, "0A.is.legal"));

        return testMapData;
    }

    public static Map<Version[], Integer> getCompareVersionData() {
        final Map<Version[], Integer> compareVersionData = new HashMap<>();

        compareVersionData.put(new Version[] {
                    new Version(1, 1, 2, "null", "meta"),
                    new Version(1, 1, 2, "null", "meta-valid")},
                0);
        compareVersionData.put(new Version[] {
                    new Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "788"),
                    new Version(1, 0, 0, "null", "null")},
                1);
        compareVersionData.put(new Version[] {
                    new Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "null"),
                    new Version(10, 2, 3, "DEV-SNAPSHOT", "null")},
                -1);
        compareVersionData.put(new Version[] {
                    new Version(1, 2, 3, "beta", "null"),
                    new Version(1, 0, 0, "alpha0.valid", "null")},
                1);
        compareVersionData.put(new Version[] {
                    new Version(1, 0, 0, "null", "0.build.1-rc.10000aaa-kk-0.1"),
                    new Version(1, 0, 0, "alpha.1", "null")},
                1);
        compareVersionData.put(new Version[] {
                    new Version(1, 0, 0, "alpha.beta.1", "null"),
                    new Version(2, 0, 0, "null", "null")},
                -1);
        compareVersionData.put(new Version[] {
                    new Version(1, 0, 0, "alpha-a.b-c-somethinglong", "build.1-aef.1-its-okay"),
                    new Version(1, 0, 0, "0A.is.legal", "null")},
                1);
        compareVersionData.put(new Version[] {
                    new Version(1, 2, 3, "---R-S.12.9.1--.12", "meta"),
                    new Version(1, 0, 0, "alpha", "beta")},
                1);
        compareVersionData.put(new Version[] {
                    new Version(1, 2, 3, "null", "null"),
                    new Version(1, 2, 3, "SNAPSHOT-123", "null")},
                1);
        compareVersionData.put(new Version[] {
                    new Version(2, 0, 0, "null", "build.1848"),
                    new Version(1, 0, 0, "beta", "null")},
                1);
        compareVersionData.put(new Version[] {
                    new Version(1, 1, 2, "prerelease", "meta"),
                    new Version(2, 0, 0, "rc.1", "build.123")},
                -1);
        compareVersionData.put(new Version[] {
                    new Version(1, 0, 0, "alpha.0valid", "null"),
                    new Version(10, 20, 30, "null", "null")},
                -1);
        compareVersionData.put(new Version[] {
                    new Version(1, 0, 0, "alpha.beta", "null"),
                    new Version(2, 0, 1, "alpha.1227", "null")},
                -1);
        compareVersionData.put(new Version[] {
                    new Version(0, 0, 4, "null", "null"),
                    new Version(1, 0, 0, "alpha", "null")},
                -1);
        compareVersionData.put(new Version[] {
                    new Version(1, 1, 7, "null", "null"),
                    new Version(1, 0, 0, "rc.1", "build.1")},
                1);

        return compareVersionData;
    }
}
