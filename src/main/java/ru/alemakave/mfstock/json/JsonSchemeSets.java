package ru.alemakave.mfstock.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class JsonSchemeSets {
    private JsonSchemeSets() {}

    @Deprecated
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class DatabaseDateTimeJson {
        @JsonProperty(value = "Database_Date_Time")
        public String datetime;
    }

    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class DatabaseDateJson {
        @JsonProperty(value = "Database_Date")
        public String date;
    }
}
