package feelingalive.advancedcaptcha.captcha;

import feelingalive.advancedcaptcha.config.ConfigManager;
import feelingalive.advancedcaptcha.config.MessagesManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CaptchaSession {
    private final UUID playerId;
    private final String ip;
    private final String language;
    private final long creationTime;
    private int attempts;
    private int correctButton;
    private String buttonText;
    private ChatColor buttonColor;
    private String sessionToken;
    private boolean isMathCaptcha;
    private String mathExpression;
    private int correctAnswer;
    private List<Integer> mathAnswers;

    public CaptchaSession(UUID playerId, String ip, String language, ConfigManager configManager, MessagesManager messagesManager) {
        this.playerId = playerId;
        this.ip = ip;
        this.language = language;
        this.creationTime = System.currentTimeMillis();
        this.attempts = 0;
    }

    public void incrementAttempts() {
        this.attempts++;
    }
}