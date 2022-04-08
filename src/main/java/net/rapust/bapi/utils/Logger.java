package net.rapust.bapi.utils;

import net.rapust.bapi.BossesAPI;

public class Logger {

    public static void log(String message) {
        BossesAPI.getInstance().getLogger().info(message);
    }

    public static void warn(String message) {
        BossesAPI.getInstance().getLogger().warning(message);
    }

}
