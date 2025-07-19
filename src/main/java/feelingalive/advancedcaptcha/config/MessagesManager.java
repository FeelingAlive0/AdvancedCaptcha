package feelingalive.advancedcaptcha.config;

import feelingalive.advancedcaptcha.utils.TextUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesManager {
    private final JavaPlugin plugin;
    private String language;
    private Map<String, String> messages;
    private List<String> buttonTexts;
    private Map<String, String> colorNames;
    private long lastMessagesUpdate;

    public MessagesManager(JavaPlugin plugin, String defaultLanguage) {
        this.plugin = plugin;
        this.language = defaultLanguage;
        loadMessages();
        startMessagesRefresh();
    }

    private void loadMessages() {
        messages = new HashMap<>();
        buttonTexts = new ArrayList<>();
        colorNames = new HashMap<>();
        File messagesFile = new File(plugin.getDataFolder(), "messages/messages_" + language + ".yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages/messages_" + language + ".yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);
        for (String key : config.getKeys(true)) {
            if (!config.isConfigurationSection(key)) {
                if (key.equals("captcha.button-texts")) {
                    buttonTexts = config.getStringList("captcha.button-texts");
                } else if (key.startsWith("captcha.color-names")) {
                    colorNames = config.getConfigurationSection("captcha.color-names").getValues(false).entrySet().stream()
                            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey().toUpperCase(), (String) entry.getValue()), HashMap::putAll);
                } else {
                    messages.put(key, config.getString(key));
                }
            }
        }
        lastMessagesUpdate = System.currentTimeMillis();
    }

    private void startMessagesRefresh() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastMessagesUpdate > 60000) {
                    loadMessages();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 60, 20L * 60);
    }

    public String getLanguage() {
        return language;
    }

    public String getMessage(String key, String... placeholders) {
        String message = messages.getOrDefault(key, key);
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("{" + i + "}", placeholders[i]);
        }
        return TextUtils.color(message);
    }

    public List<String> getButtonTexts() {
        return buttonTexts;
    }

    public String getButtonText(int index) {
        return buttonTexts.get(index % buttonTexts.size());
    }

    public String getColorName(String color) {
        return colorNames.getOrDefault(color.toUpperCase(), color.toLowerCase());
    }
}