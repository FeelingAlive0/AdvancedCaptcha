package feelingalive.advancedcaptcha.captcha;

import feelingalive.advancedcaptcha.config.ConfigManager;
import feelingalive.advancedcaptcha.config.MessagesManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CaptchaListener implements Listener {
    private final CaptchaManager captchaManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;

    public CaptchaListener(CaptchaManager captchaManager, ConfigManager configManager, MessagesManager messagesManager) {
        this.captchaManager = captchaManager;
        this.configManager = configManager;
        this.messagesManager = messagesManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        captchaManager.startCaptcha(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        captchaManager.cleanupPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesManager.getMessage("captcha-no-chat"));
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId()) && !captchaManager.isVerified(event.getPlayer().getUniqueId())) {
            String command = event.getMessage().split(" ")[0].toLowerCase();
            if (!configManager.getAllowedCommands().contains(command)) {
                if (command.equals("/captcha_click")) {
                    captchaManager.handleClick(event.getPlayer(), event.getMessage().substring("/captcha_click ".length()));
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(messagesManager.getMessage("captcha-no-commands"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (configManager.isFreezeEnabled() && captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesManager.getMessage("captcha-no-move"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesManager.getMessage("captcha-no-interact"));
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesManager.getMessage("captcha-no-drop"));
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesManager.getMessage("captcha-no-pickup"));
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (captchaManager.isAwaitingVerification(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(messagesManager.getMessage("captcha-no-attack"));
            }
        }
    }
}