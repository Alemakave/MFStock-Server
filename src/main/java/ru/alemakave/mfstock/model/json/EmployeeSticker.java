package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeSticker {
    @JsonProperty("employeeCode")
    private String code;
    @JsonProperty("employeeName")
    private String name;
    @JsonProperty("employeePass")
    private String pass;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
