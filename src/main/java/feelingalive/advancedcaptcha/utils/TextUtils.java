package feelingalive.advancedcaptcha.utils;

import net.md_5.bungee.api.ChatColor;

public class TextUtils {
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static org.bukkit.ChatColor getChatColor(String color) {
        try {
            return org.bukkit.ChatColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static ChatColor convertToBungeeChatColor(org.bukkit.ChatColor bukkitColor) {
        return ChatColor.valueOf(bukkitColor.name());
    }
}