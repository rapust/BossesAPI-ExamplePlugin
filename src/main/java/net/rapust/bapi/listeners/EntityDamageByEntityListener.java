package net.rapust.bapi.listeners;

import net.rapust.bapi.bosses.Boss;
import net.rapust.bapi.managers.BossesManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity target = event.getEntity();

        BossesManager manager = BossesManager.getInstance();

        Boss boss = manager.getBossByEntityId(target.getEntityId());

        if (!(attacker instanceof Player) || boss == null || boss.getEntity() == null) return;

        Double damage = event.getFinalDamage();

        boss.playerDamage((Player) attacker, damage);

        if (damage >= ((LivingEntity) boss.getEntity()).getHealth()) {
            boss.onDeath();
        }

    }

}
