package net.rapust.bapi.bosses;

import net.rapust.bapi.BossesAPI;
import net.rapust.bapi.managers.BossesManager;
import net.rapust.bapi.triggers.TimedTrigger;
import net.rapust.bapi.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import java.util.List;

public class Spawner {

    public static void startSpawner() {
        BossesAPI plugin = BossesAPI.getInstance();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            List<Object> triggers = BossesManager.getInstance().getTriggers(TimedTrigger.class);
            triggers.forEach(trigger -> {
                TimedTrigger timedTrigger = (TimedTrigger) trigger;
                if (timedTrigger.getAbility().getBoss().getEntity() != null && timedTrigger.getLastUsageTime()+timedTrigger.getRepeatingTime() < System.currentTimeMillis()) {
                    timedTrigger.setLastUsageTime(System.currentTimeMillis());
                    timedTrigger.getAbility().execute();
                }
            });

            BossesManager.getInstance().getBosses().forEach(boss -> {

                Entity entity = boss.getEntity();

                if (entity == null) {

                    Long respawnTime = boss.getRespawnTime();
                    Long lastExistTime = boss.getLastExistTime();
                    Long spawnAt = lastExistTime+respawnTime;
                    Long timeNow = System.currentTimeMillis();

                    if (spawnAt > timeNow) {

                        String timeString = Messages.convertTime(spawnAt-timeNow);
                        List<String> hologramStrings = Messages.getConfiguration().getStringList("holograms.spawning");

                        boss.getHologram().setHologram(Messages.convertList(hologramStrings, boss, timeString), boss.getSpawnLocation());

                    } else {
                        boss.spawn();
                    }

                } else {

                    List<String> hologramStrings = Messages.getConfiguration().getStringList("holograms.spawned");
                    boss.getHologram().setHologram(Messages.convertList(hologramStrings, boss, ""), boss.getSpawnLocation());

                }

            });

        }, 0, 20);
    }
}
