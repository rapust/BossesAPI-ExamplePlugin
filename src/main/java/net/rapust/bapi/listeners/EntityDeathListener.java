package net.rapust.bapi.listeners;

import net.rapust.bapi.bosses.Boss;
import net.rapust.bapi.managers.BossesManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        for (Boss boss : BossesManager.getInstance().getBosses()) {
            if (boss.getLastKilledId() != null && boss.getLastKilledId() == entity.getEntityId()) {
                event.getDrops().clear();
                event.setDroppedExp(0);
                boss.setLastKilledId(null);
                return;
            }
        }
    }
}
