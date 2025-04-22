package me.artyom.vimeauth;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class AuthListener implements Listener {

    private final VimeGroupSync plugin;

    public AuthListener(VimeGroupSync plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        String ip = event.getAddress().getHostAddress();
        plugin.getRealIpMap().put(event.getPlayer().getUniqueId(), ip);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        String ip = plugin.getRealIpMap().getOrDefault(event.getPlayer().getUniqueId(), "0.0.0.0");
        if (!plugin.getDataConfig().contains(ip)) event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        String ip = plugin.getRealIpMap().getOrDefault(event.getPlayer().getUniqueId(), "0.0.0.0");
        if (!plugin.getDataConfig().contains(ip)) event.setCancelled(true);
    }
}