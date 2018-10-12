package com.spud.sgsclasscountdownapp;

/**
 * Created by Stephen Ogden on 10/9/18.
 * FTC 6128 | 7935
 * FRC 1595
 */
public enum UpdateType {
    BuiltIn,
    Automatic,
    ManualADay,
    ManualEDay,
    ManualFullDay,
    ManualCustomDay;

    static UpdateType getUpdateType() {
        Database database = new Database();
        return database.getUpdateType();
    }
}