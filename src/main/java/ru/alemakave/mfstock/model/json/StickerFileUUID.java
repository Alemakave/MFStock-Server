package ru.alemakave.mfstock.model.json;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public final class StickerFileUUID {
    private UUID stickerFileUUID;
}
