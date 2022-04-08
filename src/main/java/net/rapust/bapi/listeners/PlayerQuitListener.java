package net.rapust.bapi.listeners;

import net.rapust.bapi.managers.BossesManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BossesManager.getInstance().getBosses().forEach(boss -> {
            boss.getHologram().removePlayer(event.getPlayer());
        });
    }

}
