package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NomPartySticker extends NomSticker {
    @JsonProperty("NomParty")
    private String party;

    public String getParty() {
        return party;
    }

    public void setParty(String serial) {
        this.party = serial;
    }
}
