package ru.alemakave.mfstock.model.json.sticker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EmployeeSticker implements Sticker {
    @JsonProperty("EmployeeCode")
    private String code;
    @JsonProperty("EmployeeName")
    private String name;
    @JsonProperty("EmployeePass")
    private String pass;

    @Override
    public UUID getUUID() {
        return UUID.nameUUIDFromBytes(toString().getBytes());
    }
}
