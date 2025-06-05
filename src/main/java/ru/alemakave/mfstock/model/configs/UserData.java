package ru.alemakave.mfstock.model.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UserData {
    private String username;
    private String password;
}