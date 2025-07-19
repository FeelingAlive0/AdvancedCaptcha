package feelingalive.advancedcaptcha.config;

import feelingalive.advancedcaptcha.captcha.CaptchaType;
import feelingalive.advancedcaptcha.utils.TextUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private List<org.bukkit.ChatColor> buttonColors;
    private List<String> supportedLanguages;
    private CaptchaType captchaType;
    private Level logLevel;
    private Sound successSound;
    private Sound failSound;
    private org.bukkit.Particle successParticle;
    private org.bukkit.Particle failParticle;
    private long lastConfigUpdate;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
        startConfigRefresh();
    }

    private void loadConfig() {
        config = plugin.getConfig();
        buttonColors = config.getStringList("captcha.button-colors").stream()
                .map(TextUtils::getChatColor)
                .filter(color -> color != null)
                .collect(Collectors.toList());
        supportedLanguages = config.getStringList("supported-languages");
        captchaType = CaptchaType.valueOf(config.getString("captcha.captcha-type", "RANDOM").toUpperCase());
        logLevel = Level.parse(config.getString("log-level", "INFO").toUpperCase());
        successSound = config.getString("captcha.success-sound") != null ? Sound.valueOf(config.getString("captcha.success-sound")) : null;
        failSound = config.getString("captcha.fail-sound") != null ? Sound.valueOf(config.getString("captcha.fail-sound")) : null;
        successParticle = config.getString("captcha.success-particle") != null ? org.bukkit.Particle.valueOf(config.getString("captcha.success-particle")) : null;
        failParticle = config.getString("captcha.fail-particle") != null ? org.bukkit.Particle.valueOf(config.getString("captcha.fail-particle")) : null;
        lastConfigUpdate = System.currentTimeMillis();
    }

    private void startConfigRefresh() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastConfigUpdate > 60000) {
                    loadConfig();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 60, 20L * 60);
    }

    public String getDefaultLanguage() {
        return config.getString("language", "en");
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public long getCooldown() {
        return config.getLong("captcha.cooldown", 21600000L);
    }

    public int getButtonCountMin() {
        return config.getInt("captcha.button-count-min", 4);
    }

    public int getButtonCountMax() {
        return config.getInt("captcha.button-count-max", 7);
    }

    public int getMaxAttempts() {
        return config.getInt("captcha.max-attempts", 2);
    }

    public long getTimeout() {
        return config.getLong("captcha.timeout", 300L) * 20L;
    }

    public List<String> getAllowedCommands() {
        return config.getStringList("captcha.allowed-commands");
    }

    public List<org.bukkit.ChatColor> getButtonColors() {
        return buttonColors;
    }

    public CaptchaType getCaptchaType() {
        return captchaType;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public Sound getSuccessSound() {
        return successSound;
    }

    public Sound getFailSound() {
        return failSound;
    }

    public org.bukkit.Particle getSuccessParticle() {
        return successParticle;
    }

    public org.bukkit.Particle getFailParticle() {
        return failParticle;
    }

    public long getAntiMacroThreshold() {
        return config.getLong("captcha.anti-macro-threshold", 200L);
    }
}