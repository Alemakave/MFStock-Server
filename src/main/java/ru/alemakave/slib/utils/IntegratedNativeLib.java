package ru.alemakave.slib.utils;

import ru.alemakave.slib.utils.OS.OSBitType;

import static ru.alemakave.slib.utils.OS.OSBitType.X64;

public class IntegratedNativeLib extends IntegratedLib {
    private final OSBitType osBitType;

    public IntegratedNativeLib(String groupID, String artifactID, String version, OSBitType systemBitType) {
        super(groupID, artifactID, version);
        this.osBitType = systemBitType;
    }

    public String getSystemBitTypeAsString() {
        if (osBitType == X64) {
            return "x64";
        } else {
            return "x32";
        }
    }
}
