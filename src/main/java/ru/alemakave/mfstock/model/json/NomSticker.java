package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NomSticker {
    @JsonProperty("NomCode")
    private String code;
    @JsonProperty("NomName")
    private String name;
    @JsonProperty("StickerCopies")
    private String copies;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCopies() {
        return copies;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String nme) {
        this.name = nme;
    }

    public void setCopies(String copies) {
        this.copies = copies;
    }
}
