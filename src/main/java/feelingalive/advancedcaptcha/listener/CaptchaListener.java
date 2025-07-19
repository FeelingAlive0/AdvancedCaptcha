package feelingalive.advancedcaptcha.listener;

import feelingalive.advancedcaptcha.captcha.CaptchaManager;
import feelingalive.advancedcaptcha.config.MessagesManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class CaptchaListener implements Listener {
    private final CaptchaManager captchaManager;
    private final MessagesManager messagesManager;

    public CaptchaListener(CaptchaManager captchaManager, MessagesManager messagesManager) {
        this.captchaManager = captchaManager;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            String command = event.getMessage().toLowerCase();
            if (command.startsWith("/captcha_click ")) {
                event.setCancelled(true);
                captchaManager.handleClick(event.getPlayer(), command.split(" ")[1]);
            } else if (!captchaManager.getConfigManager().getAllowedCommands().contains(command)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(messagesManager.getMessage("captcha-blocked-command"));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (captchaManager.isAwaitingVerification(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(messagesManager.getMessage("captcha-blocked-chat"));
        }
    }
}