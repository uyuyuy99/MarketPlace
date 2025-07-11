package me.uyuyuy99.marketplace.util;

import org.bukkit.ChatColor;

import java.util.List;

public class CC {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> list) {
        list.replaceAll(CC::translate);
        return list;
    }

    public static String strip(String string) {
        return ChatColor.stripColor(string);
    }

}
