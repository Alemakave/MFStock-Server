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
public class OrderNumberSticker implements Sticker {
    @JsonProperty("OrderNumber")
    public String orderNumber;
    @JsonProperty("OrderCountCargoSpaces")
    public int orderCountCargoSpaces;

    @Override
    public UUID getUUID() {
        return UUID.nameUUIDFromBytes(toString().getBytes());
    }
}
