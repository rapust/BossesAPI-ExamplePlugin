package net.rapust.bapi.utils;

import lombok.Getter;
import net.rapust.bapi.bosses.Boss;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    @Getter private static FileConfiguration configuration;

    private static String hours;
    private static String minutes;
    private static String seconds;

    public static void setConfiguration(FileConfiguration messages) {
        configuration = messages;

        hours = getMessage("time.hours");
        minutes = getMessage("time.minutes");
        seconds = getMessage("time.seconds");
    }

    public static String getMessage(String messageCode) {
        String message = configuration.getString(messageCode);
        if (message == null) return messageCode;
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> convertList(List<String> string, Boss boss, String time) {
        List<String> result = new ArrayList<>();
        string.forEach(s -> {
            result.add(s.replace("%boss%", boss.getName()).replace("%time%", time).replace("&", "§"));
        });
        return result;
    }

    public static String convertTime(Long time) {

        String result = "";

        Long fromHours = time % 3600000;
        if (fromHours != 0) {
            result += (time-fromHours)/3600000 + " " + hours + " ";
        } else fromHours = time;

        Long fromMinutes = fromHours % 60000;
        if (fromMinutes != 0) {
            result += (fromHours-fromMinutes)/60000 + " " + minutes + " ";
        } else fromMinutes = fromHours;

        Long fromSeconds = fromMinutes / 1000;
        result += fromSeconds + " " + seconds;

        return result;
    }

}
