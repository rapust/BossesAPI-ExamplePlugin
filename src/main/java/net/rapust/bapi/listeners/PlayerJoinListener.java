package net.rapust.bapi.listeners;

import net.rapust.bapi.managers.BossesManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BossesManager.getInstance().getBosses().forEach(boss -> {
            boss.getHologram().addPlayer(event.getPlayer());
        });
    }

}
