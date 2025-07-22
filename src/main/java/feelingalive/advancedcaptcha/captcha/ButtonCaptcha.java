package feelingalive.advancedcaptcha.captcha;

import feelingalive.advancedcaptcha.config.ConfigManager;
import feelingalive.advancedcaptcha.config.MessagesManager;
import feelingalive.advancedcaptcha.utils.TextUtils;
import feelingalive.advancedcaptcha.utils.TokenGenerator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ButtonCaptcha implements CaptchaTypeHandler {
    private final CaptchaManager captchaManager;
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;

    public ButtonCaptcha(CaptchaManager captchaManager, JavaPlugin plugin, ConfigManager configManager, MessagesManager messagesManager) {
        this.captchaManager = captchaManager;
        this.plugin = plugin;
        this.configManager = configManager;
        this.messagesManager = messagesManager;
    }

    @Override
    public void showCaptcha(Player player, CaptchaSession session) {
        int buttonCount = configManager.getButtonCountMin() + (int) (Math.random() * (configManager.getButtonCountMax() - configManager.getButtonCountMin() + 1));
        int correctButton = (int) (Math.random() * buttonCount);
        int duplicateButton;
        do {
            duplicateButton = (int) (Math.random() * buttonCount);
        } while (duplicateButton == correctButton);

        String buttonText = messagesManager.getButtonTexts().get((int) (Math.random() * messagesManager.getButtonTexts().size()));
        org.bukkit.ChatColor buttonColor = configManager.getButtonColors().get((int) (Math.random() * configManager.getButtonColors().size()));
        session.setCorrectButton(correctButton);
        session.setButtonText(buttonText);
        session.setButtonColor(buttonColor);
        session.setMathCaptcha(false);

        String sessionToken = TokenGenerator.generateToken(session.getPlayerId(), System.currentTimeMillis(), session.getIp());
        session.setSessionToken(sessionToken);

        player.sendMessage(messagesManager.getMessage("captcha-instruction", buttonText, messagesManager.getColorName(buttonColor.name())));

        TextComponent[] buttons = new TextComponent[buttonCount];
        List<org.bukkit.ChatColor> availableColors = new ArrayList<>(configManager.getButtonColors());
        Collections.shuffle(availableColors);
        availableColors.remove(buttonColor);

        for (int i = 0; i < buttonCount; i++) {
            TextComponent button;
            org.bukkit.ChatColor color;
            if (i == correctButton) {
                button = new TextComponent("[" + buttonText + "]");
                color = buttonColor;
            } else if (i == duplicateButton) {
                button = new TextComponent("[" + messagesManager.getButtonTexts().get((int) (Math.random() * messagesManager.getButtonTexts().size())) + "]");
                color = buttonColor;
            } else {
                button = new TextComponent("[" + messagesManager.getButtonTexts().get((int) (Math.random() * messagesManager.getButtonTexts().size())) + "]");
                color = availableColors.get(i % availableColors.size());
            }
            button.setColor(TextUtils.convertToBungeeChatColor(color));
            button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/captcha_click " + session.getPlayerId() + "_" + sessionToken + "_" + i));
            buttons[i] = button;
        }

        Collections.shuffle(Arrays.asList(buttons));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    TextComponent message = new TextComponent();
                    for (int i = 0; i < buttons.length; i++) {
                        message.addExtra(buttons[i]);
                        if (i < buttons.length - 1) {
                            message.addExtra(" ");
                        }
                    }
                    player.spigot().sendMessage(message);
                }
            }
        }.runTaskLater(plugin, (long) (Math.random() * 14));
    }

    @Override
    public boolean verify(Player player, CaptchaSession session, int selectedButton) {
        return selectedButton == session.getCorrectButton();
    }
}