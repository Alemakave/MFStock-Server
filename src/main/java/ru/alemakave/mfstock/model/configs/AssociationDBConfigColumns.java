package ru.alemakave.mfstock.model.configs;

import lombok.Data;

@Data
public class AssociationDBConfigColumns {
    private DBConfigsColumns mainDB;
    private DBConfigsColumns otherDB;
}
