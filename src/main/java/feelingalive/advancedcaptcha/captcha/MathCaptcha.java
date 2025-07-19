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

public class MathCaptcha implements CaptchaTypeHandler {
    private final CaptchaManager captchaManager;
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;

    public MathCaptcha(CaptchaManager captchaManager, JavaPlugin plugin, ConfigManager configManager, MessagesManager messagesManager) {
        this.captchaManager = captchaManager;
        this.plugin = plugin;
        this.configManager = configManager;
        this.messagesManager = messagesManager;
    }

    @Override
    public void showCaptcha(Player player, CaptchaSession session) {
        int number1 = (int) (Math.random() * 10) + 1;
        int number2 = (int) (Math.random() * 10) + 1;
        String operator = Math.random() > 0.5 ? "+" : "-";
        int correctAnswer = operator.equals("+") ? number1 + number2 : number1 - number2;
        String expression = number1 + " " + operator + " " + number2 + " = ?";
        session.setMathExpression(expression);
        session.setCorrectAnswer(correctAnswer);
        session.setMathCaptcha(true);

        int buttonCount = configManager.getButtonCountMin() + (int) (Math.random() * (configManager.getButtonCountMax() - configManager.getButtonCountMin() + 1));
        int correctButton = (int) (Math.random() * buttonCount);
        session.setCorrectButton(correctButton);

        String sessionToken = TokenGenerator.generateToken(session.getPlayerId(), System.currentTimeMillis(), session.getIp());
        session.setSessionToken(sessionToken);

        List<Integer> answers = generateAnswers(correctAnswer, buttonCount);
        session.setMathAnswers(answers);

        player.sendMessage(messagesManager.getMessage("captcha-math-instruction", expression));

        TextComponent[] buttons = new TextComponent[buttonCount];
        org.bukkit.ChatColor buttonColor = configManager.getButtonColors().get((int) (Math.random() * configManager.getButtonColors().size()));

        for (int i = 0; i < buttonCount; i++) {
            String answerText = String.valueOf(answers.get(i));
            TextComponent button = new TextComponent(answerText);
            button.setColor(TextUtils.convertToBungeeChatColor(buttonColor));
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
                            message.addExtra("  "); // Added extra space to prevent button overlap
                        }
                    }
                    player.spigot().sendMessage(message);
                }
            }
        }.runTaskLater(plugin, (long) (Math.random() * 14));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && captchaManager.isAwaitingVerification(session.getPlayerId())) {
                    player.sendMessage(messagesManager.getMessage("captcha-timeout"));
                    captchaManager.kickPlayer(player, messagesManager.getMessage("captcha-timeout-kick"));
                }
            }
        }.runTaskLater(plugin, Math.max(configManager.getTimeout() - session.getAttempts() * 20 * 3, 200));
    }

    @Override
    public boolean verify(Player player, CaptchaSession session, int selectedButton) {
        List<Integer> answers = session.getMathAnswers();
        return selectedButton == session.getCorrectButton() && answers.get(selectedButton) == session.getCorrectAnswer();
    }

    private List<Integer> generateAnswers(int correctAnswer, int buttonCount) {
        List<Integer> answers = new ArrayList<>();
        answers.add(correctAnswer);
        for (int i = 1; i < buttonCount; i++) {
            int wrongAnswer;
            do {
                wrongAnswer = correctAnswer + (int) (Math.random() * 10 - 5);
            } while (answers.contains(wrongAnswer));
            answers.add(wrongAnswer);
        }
        Collections.shuffle(answers);
        return answers;
    }
}