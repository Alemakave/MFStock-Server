package ru.alemakave.mfstock.service;

import ru.alemakave.mfstock.model.table.Table;

public interface IDBService {
    Object getDBDate();
    String findFromScan(String searchString);
    String closeDB();
    String reloadDB();
    Table getDB();
    byte[] getPhoto(String nomCode, int index);
}
