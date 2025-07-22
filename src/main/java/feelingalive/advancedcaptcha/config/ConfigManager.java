package feelingalive.advancedcaptcha.config;

import feelingalive.advancedcaptcha.captcha.CaptchaType;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {
    private final JavaPlugin plugin;
    private String defaultLanguage;
    private List<String> supportedLanguages;
    private Level logLevel;
    private CaptchaSettings captchaSettings;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        defaultLanguage = config.getString("general.default-language", "en");
        supportedLanguages = config.getStringList("general.supported-languages");
        logLevel = Level.parse(config.getString("general.log-level", "INFO").toUpperCase());

        captchaSettings = new CaptchaSettings();
        captchaSettings.captchaType = CaptchaType.valueOf(config.getString("captcha.type", "RANDOM").toUpperCase());
        captchaSettings.cooldown = config.getLong("captcha.cooldown", 21600000);
        captchaSettings.buttonCountMin = config.getInt("captcha.button-count-min", 4);
        captchaSettings.buttonCountMax = config.getInt("captcha.button-count-max", 7);
        captchaSettings.maxAttempts = config.getInt("captcha.max-attempts", 2);
        captchaSettings.timeout = config.getInt("captcha.timeout", 300) * 20L;
        captchaSettings.allowedCommands = config.getStringList("captcha.allowed-commands");
        captchaSettings.buttonColors = new ArrayList<>();
        for (String color : config.getStringList("captcha.button-colors")) {
            try {
                captchaSettings.buttonColors.add(ChatColor.valueOf(color.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid color in config: " + color);
            }
        }
        try {
            captchaSettings.successSound = config.getString("captcha.success-sound") != null ? Sound.valueOf(config.getString("captcha.success-sound").toUpperCase()) : null;
            captchaSettings.failSound = config.getString("captcha.fail-sound") != null ? Sound.valueOf(config.getString("captcha.fail-sound").toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound in config: " + e.getMessage());
        }
        try {
            captchaSettings.successParticle = config.getString("captcha.success-particle") != null ? org.bukkit.Particle.valueOf(config.getString("captcha.success-particle").toUpperCase()) : null;
            captchaSettings.failParticle = config.getString("captcha.fail-particle") != null ? org.bukkit.Particle.valueOf(config.getString("captcha.fail-particle").toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid particle in config: " + e.getMessage());
        }
        captchaSettings.antiMacroThreshold = config.getInt("captcha.anti-macro-threshold", 200);
        captchaSettings.allowedOperations = config.getStringList("math-captcha.allowed-operations");
        captchaSettings.numberRangeMin = config.getInt("math-captcha.number-range-min", 1);
        captchaSettings.numberRangeMax = config.getInt("math-captcha.number-range-max", 20);
        captchaSettings.freezeEnabled = config.getBoolean("captcha.freeze.enabled", true);
        captchaSettings.freezeSlownessLevel = config.getInt("captcha.freeze.slowness-level", 6);
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public CaptchaType getCaptchaType() {
        return captchaSettings.captchaType;
    }

    public long getCooldown() {
        return captchaSettings.cooldown;
    }

    public int getButtonCountMin() {
        return captchaSettings.buttonCountMin;
    }

    public int getButtonCountMax() {
        return captchaSettings.buttonCountMax;
    }

    public int getMaxAttempts() {
        return captchaSettings.maxAttempts;
    }

    public long getTimeout() {
        return captchaSettings.timeout;
    }

    public List<String> getAllowedCommands() {
        return captchaSettings.allowedCommands;
    }

    public List<ChatColor> getButtonColors() {
        return captchaSettings.buttonColors;
    }

    public Sound getSuccessSound() {
        return captchaSettings.successSound;
    }

    public Sound getFailSound() {
        return captchaSettings.failSound;
    }

    public org.bukkit.Particle getSuccessParticle() {
        return captchaSettings.successParticle;
    }

    public org.bukkit.Particle getFailParticle() {
        return captchaSettings.failParticle;
    }

    public int getAntiMacroThreshold() {
        return captchaSettings.antiMacroThreshold;
    }

    public List<String> getAllowedOperations() {
        return captchaSettings.allowedOperations;
    }

    public int getNumberRangeMin() {
        return captchaSettings.numberRangeMin;
    }

    public int getNumberRangeMax() {
        return captchaSettings.numberRangeMax;
    }

    public boolean isFreezeEnabled() {
        return captchaSettings.freezeEnabled;
    }

    public int getFreezeSlownessLevel() {
        return captchaSettings.freezeSlownessLevel;
    }

    public static class CaptchaSettings {
        private CaptchaType captchaType;
        private long cooldown;
        private int buttonCountMin;
        private int buttonCountMax;
        private int maxAttempts;
        private long timeout;
        private List<String> allowedCommands;
        private List<ChatColor> buttonColors;
        private Sound successSound;
        private Sound failSound;
        private org.bukkit.Particle successParticle;
        private org.bukkit.Particle failParticle;
        private int antiMacroThreshold;
        private List<String> allowedOperations;
        private int numberRangeMin;
        private int numberRangeMax;
        private boolean freezeEnabled;
        private int freezeSlownessLevel;
    }
}