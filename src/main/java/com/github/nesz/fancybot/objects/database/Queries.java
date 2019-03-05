package com.github.nesz.fancybot.objects.database;

public class Queries {

    public static final String CREATE_TABLE_DATA =
            "CREATE TABLE IF NOT EXISTS data (" +
            "ID INT NOT NULL AUTO_INCREMENT, " +
            "UUID CHAR(36) NOT NULL, " +
            "NAME VARCHAR(32) NOT NULL CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
            "OWNER LONG NOT NULL, " +
            "TIME_CREATED DATETIME NOT NULL, " +
            "PRIMARY KEY (ID)) CHARACTER SET utf8mb4";

    public static final String CREATE_TABLE_TRACKS =
            "CREATE TABLE IF NOT EXISTS tracks (" +
            "ID INT NOT NULL AUTO_INCREMENT, " +
            "VIDEO_ID TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
            "VIDEO_TITLE TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
            "VIDEO_UPLOADER TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
            "VIDEO_DURATION LONG, " +
            "PRIMARY KEY (ID)) CHARACTER SET utf8mb4";
}
