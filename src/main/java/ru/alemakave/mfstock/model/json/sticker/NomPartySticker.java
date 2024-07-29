package ru.alemakave.mfstock.model.json.sticker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class NomPartySticker extends NomSticker {
    @JsonProperty("NomParty")
    private String party;

    @Override
    public UUID getUUID() {
        return UUID.nameUUIDFromBytes(toString().getBytes());
    }
}
