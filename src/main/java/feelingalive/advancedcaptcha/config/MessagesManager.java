package feelingalive.advancedcaptcha.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import feelingalive.advancedcaptcha.utils.TextUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final Map<String, YamlConfiguration> messages = new HashMap<>();
    private final List<String> buttonTexts = Arrays.asList("Click", "Tap", "Press", "Select");
    private final Map<String, String> colorNames = new HashMap<>();

    public MessagesManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        loadMessages();
        initColorNames();
    }

    private void loadMessages() {
        File messagesDir = new File(plugin.getDataFolder(), "messages");
        if (!messagesDir.exists()) {
            messagesDir.mkdirs();
        }
        for (String lang : configManager.getSupportedLanguages()) {
            File file = new File(messagesDir, "messages_" + lang + ".yml");
            if (!file.exists()) {
                plugin.saveResource("messages/messages_" + lang + ".yml", false);
            }
            messages.put(lang, YamlConfiguration.loadConfiguration(file));
        }
    }

    private void initColorNames() {
        colorNames.put("BLUE", "Blue");
        colorNames.put("GREEN", "Green");
        colorNames.put("RED", "Red");
        colorNames.put("YELLOW", "Yellow");
        colorNames.put("WHITE", "White");
        colorNames.put("BLACK", "Black");
        colorNames.put("GRAY", "Gray");
        colorNames.put("LIGHT_PURPLE", "Light Purple");
    }

    public String getMessage(String key, String... placeholders) {
        String lang = configManager.getDefaultLanguage();
        YamlConfiguration msgConfig = messages.get(lang);
        if (msgConfig == null) {
            return TextUtils.color("Message not found: " + key);
        }
        String message = msgConfig.getString(key, "Message not found: " + key);
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("%" + (i + 1), placeholders[i]);
        }
        return TextUtils.color(message);
    }

    public List<String> getButtonTexts() {
        return buttonTexts;
    }

    public String getColorName(String color) {
        return colorNames.getOrDefault(color, color);
    }
}