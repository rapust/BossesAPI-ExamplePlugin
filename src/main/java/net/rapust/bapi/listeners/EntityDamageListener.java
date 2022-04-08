package net.rapust.bapi.listeners;

import net.rapust.bapi.BossesAPI;
import net.rapust.bapi.bosses.Boss;
import net.rapust.bapi.enums.Resist;
import net.rapust.bapi.managers.BossesManager;
import net.rapust.bapi.triggers.DamageTypeTrigger;
import net.rapust.bapi.triggers.HealthTrigger;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import java.util.List;
import java.util.Objects;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();
        Boss boss = BossesManager.getInstance().getBossByEntityId(entity.getEntityId());
        if (boss == null) return;

        if (boss.getDamageResists().contains(cause)) {
            event.setCancelled(true);
            return;
        }

        if (event.getFinalDamage() >= ((LivingEntity) entity).getHealth()) {
            boss.onDeath();
            return;
        }

        if (boss.getAttackResists().contains(Resist.KNOCKBACK)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BossesAPI.getInstance(), new Runnable() {
                @Override
                public void run() {
                    boss.getEntity().setVelocity(new Vector());
                }
            }, 1L);
        }

        List<Object> triggers = BossesManager.getInstance().getTriggers(DamageTypeTrigger.class);
        triggers.forEach(trigger -> {
            DamageTypeTrigger damageTypeTrigger = (DamageTypeTrigger) trigger;
            if (damageTypeTrigger.getAbility().getBoss().getId().equals(boss.getId())) {
                if (damageTypeTrigger.getCause() == cause) {
                    damageTypeTrigger.getAbility().execute();
                }
            }
        });

        triggers = BossesManager.getInstance().getTriggers(HealthTrigger.class);
        triggers.forEach(trigger -> {
            HealthTrigger healthTrigger = (HealthTrigger) trigger;
            if (healthTrigger.getAbility().getBoss().getId().equals(boss.getId())) {
                LivingEntity livingEntity = (LivingEntity) boss.getEntity();
                if (healthTrigger.getOnHealth() != null) {
                    if (livingEntity.getHealth() <= healthTrigger.getOnHealth()) {
                        healthTrigger.getAbility().execute();
                    }
                } else if (healthTrigger.getOnPercent() != null) {
                    Double percentHealth = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()*(healthTrigger.getOnPercent()/100);
                    if (livingEntity.getHealth() <= percentHealth) {
                        healthTrigger.getAbility().execute();
                    }
                }
            }
        });

    }
}
