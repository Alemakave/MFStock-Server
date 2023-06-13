package ru.alemakave.mfstock.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class DBConfigs {
    public DBColumnConfigs[] columns;

    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class DBColumnConfigs {
        public String headerText;
        public String prefix;

        public DBColumnConfigs() {}
    }
}
