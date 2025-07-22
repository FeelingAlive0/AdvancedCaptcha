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
        // Generate math expression and correct answer
        List<String> allowedOperations = configManager.getAllowedOperations();
        String operator = allowedOperations.get((int) (Math.random() * allowedOperations.size()));
        int rangeMin = configManager.getNumberRangeMin();
        int rangeMax = configManager.getNumberRangeMax();
        int number1 = rangeMin + (int) (Math.random() * (rangeMax - rangeMin + 1));
        int number2 = rangeMin + (int) (Math.random() * (rangeMax - rangeMin + 1));
        int correctAnswer;
        switch (operator) {
            case "PLUS":
                correctAnswer = number1 + number2;
                break;
            case "MINUS":
                correctAnswer = number1 - number2;
                break;
            case "MULTIPLY":
                correctAnswer = number1 * number2;
                break;
            default:
                correctAnswer = number1 + number2; // Fallback to addition
        }
        String expression = number1 + " " + (operator.equals("MULTIPLY") ? "*" : operator.equals("PLUS") ? "+" : "-") + " " + number2 + " = ?";

        // Store data in session
        session.setMathExpression(expression);
        session.setCorrectAnswer(correctAnswer);
        session.setMathCaptcha(true);

        // Generate buttons and answers
        int buttonCount = configManager.getButtonCountMin() + (int) (Math.random() * (configManager.getButtonCountMax() - configManager.getButtonCountMin() + 1));
        List<Integer> answers = generateAnswers(correctAnswer, buttonCount);

        // Set correct button to the index where correctAnswer is located
        int correctButton = answers.indexOf(correctAnswer);
        session.setCorrectButton(correctButton);
        session.setMathAnswers(answers);

        // Generate new session token
        String sessionToken = TokenGenerator.generateToken(session.getPlayerId(), System.currentTimeMillis(), session.getIp());
        session.setSessionToken(sessionToken);

        // Send instruction
        player.sendMessage(messagesManager.getMessage("captcha-math-instruction", expression));

        // Create clickable buttons with answers in square brackets and different colors
        TextComponent[] buttons = new TextComponent[buttonCount];
        List<org.bukkit.ChatColor> availableColors = new ArrayList<>(configManager.getButtonColors());
        Collections.shuffle(availableColors);

        for (int i = 0; i < buttonCount; i++) {
            String answerText = "[" + answers.get(i) + "]";
            TextComponent button = new TextComponent(answerText);
            org.bukkit.ChatColor buttonColor = availableColors.get(i % availableColors.size());
            button.setColor(TextUtils.convertToBungeeChatColor(buttonColor));
            button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/captcha_click " + session.getPlayerId() + "_" + sessionToken + "_" + i));
            buttons[i] = button;
        }

        // Display buttons with three spaces between them
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    TextComponent message = new TextComponent();
                    for (int i = 0; i < buttons.length; i++) {
                        message.addExtra(buttons[i]);
                        if (i < buttons.length - 1) {
                            message.addExtra("   ");
                        }
                    }
                    player.spigot().sendMessage(message);
                }
            }
        }.runTaskLater(plugin, (long) (Math.random() * 14));

        // Schedule timeout
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
        if (answers == null || answers.isEmpty()) {
            return false;
        }
        return selectedButton == session.getCorrectButton();
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
        // Shuffle answers
        for (int i = answers.size() - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            int temp = answers.get(i);
            answers.set(i, answers.get(j));
            answers.set(j, temp);
        }
        return answers;
    }
}