package feelingalive.advancedcaptcha;

import feelingalive.advancedcaptcha.captcha.CaptchaManager;
import feelingalive.advancedcaptcha.config.ConfigManager;
import feelingalive.advancedcaptcha.config.MessagesManager;
import feelingalive.advancedcaptcha.listener.CaptchaListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedCaptchaPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private CaptchaManager captchaManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this, configManager.getDefaultLanguage());
        captchaManager = new CaptchaManager(this, configManager, messagesManager);
        getServer().getPluginManager().registerEvents(new CaptchaListener(captchaManager, messagesManager), this);
        getLogger().info(messagesManager.getMessage("plugin-enabled"));
    }

    @Override
    public void onDisable() {
        captchaManager.cleanup();
        getLogger().info(messagesManager.getMessage("plugin-disabled"));
    }
}