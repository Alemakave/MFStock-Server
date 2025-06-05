package ru.alemakave.mfstock.model;

import lombok.*;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private String username;
    private String password;
    private UserDataRoles role;
}