package ru.alemakave.mfstock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alemakave.mfstock.service.IDBService;

@RestController
public class DBController {
    private final IDBService dbService;

    public DBController(IDBService dbService) {
        this.dbService = dbService;
    }

    @GetMapping(path = {"/mfstock-get-db-date"}, produces="application/json")
    public Object getDBDate() {
        return dbService.getDBDate();
    }

    @GetMapping(path = {"/mfstock-find-from-scan"}, produces="application/json")
    public String findFromScan(String searchString) {
        return dbService.findFromScan(searchString);
    }

    @GetMapping(path = {"/mfstock-close-db"})
    public String closeDB() {
        return dbService.closeDB();
    }

    @Deprecated
    @GetMapping(params = {"mfstock-get-db-date"}, produces="application/json")
    public Object getDBDateOutdated(@RequestParam(name="mfstock-get-db-date") String mfstockGetDbDate) {
        return getDBDate();
    }

    @Deprecated
    @GetMapping(params = {"mfstock-find-from-scan"}, produces="application/json")
    public Object findFromScanOutdated(@RequestParam(name="mfstock-find-from-scan") String searchString) {
        return findFromScan(searchString);
    }
}
