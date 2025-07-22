package feelingalive.advancedcaptcha.captcha;

import feelingalive.advancedcaptcha.config.ConfigManager;
import feelingalive.advancedcaptcha.config.MessagesManager;
import feelingalive.advancedcaptcha.utils.TextUtils;
import feelingalive.advancedcaptcha.utils.TokenGenerator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CaptchaManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final Map<UUID, CaptchaSession> sessions = new HashMap<>();
    private final Map<UUID, Long> verifiedPlayers = new HashMap<>();
    private final Map<UUID, Long> lastClickTimes = new HashMap<>();

    public CaptchaManager(JavaPlugin plugin, ConfigManager configManager, MessagesManager messagesManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messagesManager = messagesManager;
        startSessionCleanup();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void startCaptcha(Player player) {
        UUID playerId = player.getUniqueId();
        String ip = player.getAddress().getAddress().getHostAddress();
        long currentTime = System.currentTimeMillis();

        synchronized (verifiedPlayers) {
            if (verifiedPlayers.containsKey(playerId) && currentTime - verifiedPlayers.get(playerId) < configManager.getCooldown()) {
                player.sendMessage(messagesManager.getMessage("captcha-already-verified"));
                removeFreeze(player);
                return;
            }
            verifiedPlayers.remove(playerId);
        }

        String playerLocale = player.getLocale().split("_")[0];
        String language = configManager.getSupportedLanguages().contains(playerLocale) ? playerLocale : configManager.getDefaultLanguage();
        CaptchaSession session = new CaptchaSession(playerId, ip, language, configManager, messagesManager);
        synchronized (sessions) {
            sessions.put(playerId, session);
        }

        // Apply freeze to player
        applyFreeze(player);

        CaptchaType captchaType = configManager.getCaptchaType();
        if (captchaType == CaptchaType.RANDOM) {
            captchaType = Math.random() > 0.5 ? CaptchaType.BUTTON : CaptchaType.MATH;
        }

        switch (captchaType) {
            case BUTTON:
                new ButtonCaptcha(this, plugin, configManager, messagesManager).showCaptcha(player, session);
                break;
            case MATH:
                new MathCaptcha(this, plugin, configManager, messagesManager).showCaptcha(player, session);
                break;
        }
    }

    public void handleClick(Player player, String clickValue) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        synchronized (lastClickTimes) {
            if (lastClickTimes.containsKey(playerId) && currentTime - lastClickTimes.get(playerId) < 1000) {
                player.sendMessage(messagesManager.getMessage("captcha-spam-click"));
                log(Level.WARNING, messagesManager.getMessage("log-failed-attempt", player.getName(), "unknown", "Click spam"));
                return;
            }
            lastClickTimes.put(playerId, currentTime);
        }

        CaptchaSession session;
        synchronized (sessions) {
            session = sessions.get(playerId);
            if (session == null) {
                kickPlayer(player, messagesManager.getMessage("captcha-invalid-session"));
                return;
            }
        }

        String[] parts = clickValue.split("_");
        if (parts.length != 3 || !parts[0].equals(playerId.toString()) || !parts[1].equals(session.getSessionToken())) {
            kickPlayer(player, messagesManager.getMessage("captcha-invalid-token"));
            log(Level.WARNING, messagesManager.getMessage("log-failed-attempt", player.getName(), session.getIp(), "Invalid token"));
            return;
        }

        String currentIp = player.getAddress().getAddress().getHostAddress();
        if (!isValidIp(session.getIp(), currentIp)) {
            kickPlayer(player, messagesManager.getMessage("captcha-ip-changed"));
            log(Level.WARNING, messagesManager.getMessage("log-failed-attempt", player.getName(), session.getIp(), "IP changed"));
            return;
        }

        int selectedButton;
        try {
            selectedButton = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            kickPlayer(player, messagesManager.getMessage("captcha-invalid-format"));
            log(Level.WARNING, messagesManager.getMessage("log-failed-attempt", player.getName(), session.getIp(), "Invalid button index"));
            return;
        }

        if (isSuspiciousClick(currentTime, session)) {
            kickPlayer(player, messagesManager.getMessage("captcha-macro-detected"));
            log(Level.WARNING, messagesManager.getMessage("log-failed-attempt", player.getName(), session.getIp(), "Macro detected"));
            return;
        }

        CaptchaType captchaType = configManager.getCaptchaType();
        if (captchaType == CaptchaType.RANDOM) {
            captchaType = session.isMathCaptcha() ? CaptchaType.MATH : CaptchaType.BUTTON;
        }

        boolean isCorrect = false;
        switch (captchaType) {
            case BUTTON:
                isCorrect = new ButtonCaptcha(this, plugin, configManager, messagesManager).verify(player, session, selectedButton);
                break;
            case MATH:
                isCorrect = new MathCaptcha(this, plugin, configManager, messagesManager).verify(player, session, selectedButton);
                break;
        }

        session.incrementAttempts();

        if (isCorrect) {
            synchronized (verifiedPlayers) {
                verifiedPlayers.put(playerId, currentTime);
            }
            synchronized (sessions) {
                sessions.remove(playerId); // Immediate session removal
            }
            player.sendMessage(messagesManager.getMessage("captcha-success"));
            if (configManager.getSuccessSound() != null) {
                player.playSound(player.getLocation(), configManager.getSuccessSound(), 1.0f, 1.0f);
            }
            if (configManager.getSuccessParticle() != null) {
                player.spawnParticle(configManager.getSuccessParticle(), player.getLocation(), 10);
            }
            log(Level.INFO, messagesManager.getMessage("log-success", player.getName(), session.getIp()));
            // Remove freeze upon successful captcha
            removeFreeze(player);
        } else {
            if (session.getAttempts() >= configManager.getMaxAttempts()) {
                kickPlayer(player, messagesManager.getMessage("captcha-too-many-attempts"));
                log(Level.WARNING, messagesManager.getMessage("log-failed-attempt", player.getName(), session.getIp(), "Too many attempts"));
                return;
            }
            player.sendMessage(messagesManager.getMessage("captcha-wrong-button", String.valueOf(configManager.getMaxAttempts() - session.getAttempts())));
            if (configManager.getFailSound() != null) {
                player.playSound(player.getLocation(), configManager.getFailSound(), 1.0f, 1.0f);
            }
            if (configManager.getFailParticle() != null) {
                player.spawnParticle(configManager.getFailParticle(), player.getLocation(), 10);
            }
            log(Level.WARNING, messagesManager.getMessage("log-failed-attempt", player.getName(), session.getIp(), "Wrong answer"));
            if (captchaType == CaptchaType.BUTTON) {
                new ButtonCaptcha(this, plugin, configManager, messagesManager).showCaptcha(player, session);
            } else {
                new MathCaptcha(this, plugin, configManager, messagesManager).showCaptcha(player, session);
            }
        }
    }

    public boolean isAwaitingVerification(UUID playerId) {
        synchronized (sessions) {
            return sessions.containsKey(playerId);
        }
    }

    public boolean isVerified(UUID playerId) {
        synchronized (verifiedPlayers) {
            return verifiedPlayers.containsKey(playerId) && (System.currentTimeMillis() - verifiedPlayers.get(playerId) < configManager.getCooldown());
        }
    }

    public void cleanupPlayer(UUID playerId) {
        synchronized (sessions) {
            sessions.remove(playerId);
        }
        synchronized (lastClickTimes) {
            lastClickTimes.remove(playerId);
        }
        // Remove freeze when player leaves
        Player player = plugin.getServer().getPlayer(playerId);
        if (player != null) {
            removeFreeze(player);
        }
    }

    public void cleanup() {
        synchronized (sessions) {
            for (UUID playerId : sessions.keySet()) {
                Player player = plugin.getServer().getPlayer(playerId);
                if (player != null) {
                    removeFreeze(player);
                }
            }
            sessions.clear();
        }
        synchronized (verifiedPlayers) {
            verifiedPlayers.clear();
        }
        synchronized (lastClickTimes) {
            lastClickTimes.clear();
        }
    }

    public void kickPlayer(Player player, String message) {
        player.closeInventory();
        if (player.getVehicle() != null) {
            player.leaveVehicle();
        }
        player.kickPlayer(TextUtils.color(message));
        cleanupPlayer(player.getUniqueId());
    }

    private void applyFreeze(Player player) {
        // Apply potion effects to restrict movement
        if (configManager.isFreezeEnabled()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, configManager.getFreezeSlownessLevel(), false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -5, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false));
        }
    }

    private void removeFreeze(Player player) {
        // Remove potion effects
        if (configManager.isFreezeEnabled()) {
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    private void startSessionCleanup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                synchronized (sessions) {
                    sessions.entrySet().removeIf(entry -> {
                        if (currentTime - entry.getValue().getCreationTime() > configManager.getTimeout()) {
                            Player player = plugin.getServer().getPlayer(entry.getKey());
                            if (player != null && player.isOnline()) {
                                player.sendMessage(messagesManager.getMessage("captcha-timeout"));
                                kickPlayer(player, messagesManager.getMessage("captcha-timeout-kick"));
                            }
                            return true;
                        }
                        return false;
                    });
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 60, 20L * 60);
    }

    private boolean isValidIp(String sessionIp, String currentIp) {
        return sessionIp.equals(currentIp);
    }

    private boolean isSuspiciousClick(long currentTime, CaptchaSession session) {
        long clickTime = currentTime - session.getCreationTime();
        return clickTime < configManager.getAntiMacroThreshold();
    }

    private void log(Level level, String message) {
        if (configManager.getLogLevel().intValue() <= level.intValue()) {
            plugin.getLogger().log(level, message);
        }
    }
}