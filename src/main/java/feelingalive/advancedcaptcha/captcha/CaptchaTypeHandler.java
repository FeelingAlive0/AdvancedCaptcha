package feelingalive.advancedcaptcha.captcha;

import org.bukkit.entity.Player;

public interface CaptchaTypeHandler {
    void showCaptcha(Player player, CaptchaSession session);
    boolean verify(Player player, CaptchaSession session, int selectedButton);
}