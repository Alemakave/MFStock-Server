package ru.alemakave.mfstock.server;

import ru.alemakave.slib.utils.OS;

public class Configs {
    public String port = "-1";

    public String dbFilePath = OS.getJarDir(MFStockServer.class) + OS.sep + "Cache" + OS.sep + "DB.xlsx";
}
