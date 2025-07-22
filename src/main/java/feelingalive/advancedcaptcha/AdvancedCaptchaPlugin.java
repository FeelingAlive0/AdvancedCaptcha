package feelingalive.advancedcaptcha;

import feelingalive.advancedcaptcha.captcha.CaptchaListener;
import feelingalive.advancedcaptcha.captcha.CaptchaManager;
import feelingalive.advancedcaptcha.config.ConfigManager;
import feelingalive.advancedcaptcha.config.MessagesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedCaptchaPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private CaptchaManager captchaManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this, configManager);
        captchaManager = new CaptchaManager(this, configManager, messagesManager);
        getServer().getPluginManager().registerEvents(new CaptchaListener(captchaManager, configManager, messagesManager), this);
        getLogger().info("AdvancedCaptcha has been enabled!");
    }

    @Override
    public void onDisable() {
        captchaManager.cleanup();
        getLogger().info("AdvancedCaptcha has been disabled!");
    }
}