package feelingalive.advancedcaptcha.utils;

import net.md_5.bungee.api.ChatColor;

public class TextUtils {
    public static ChatColor convertToBungeeChatColor(org.bukkit.ChatColor color) {
        switch (color) {
            case BLACK: return ChatColor.BLACK;
            case DARK_BLUE: return ChatColor.DARK_BLUE;
            case DARK_GREEN: return ChatColor.DARK_GREEN;
            case DARK_AQUA: return ChatColor.DARK_AQUA;
            case DARK_RED: return ChatColor.DARK_RED;
            case DARK_PURPLE: return ChatColor.DARK_PURPLE;
            case GOLD: return ChatColor.GOLD;
            case GRAY: return ChatColor.GRAY;
            case DARK_GRAY: return ChatColor.DARK_GRAY;
            case BLUE: return ChatColor.BLUE;
            case GREEN: return ChatColor.GREEN;
            case AQUA: return ChatColor.AQUA;
            case RED: return ChatColor.RED;
            case LIGHT_PURPLE: return ChatColor.LIGHT_PURPLE;
            case YELLOW: return ChatColor.YELLOW;
            case WHITE: return ChatColor.WHITE;
            default: return ChatColor.WHITE;
        }
    }

    public static String color(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }
}