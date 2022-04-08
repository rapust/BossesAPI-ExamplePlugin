package net.rapust.bapi.managers;

import lombok.Getter;
import net.rapust.bapi.bosses.Boss;
import net.rapust.bapi.bosses.Spawner;
import org.bukkit.entity.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BossesManager {

    @Getter private static BossesManager instance;

    @Getter private List<Boss> bosses;

    public BossesManager() {
        instance = this;

        this.bosses = new ArrayList<>();

        Spawner.startSpawner();
    }

    public void addBoss(Boss boss) {
        bosses.add(boss);
    }

    public void removeBoss(Boss boss) {
        bosses.remove(boss);
    }

    public Boss getBossByEntityId(Integer id) {
        for (Boss boss : bosses) {
            Entity entity = boss.getEntity();
            if (entity != null && entity.getEntityId() == id) return boss;
        }
        return null;
    }

    public Boss getBossById(Integer id) {
        for (Boss boss : bosses) {
            if (Objects.equals(boss.getId(), id)) return boss;
        }
        return null;
    }

    public Integer generateRandomBossId() {
        Integer id = (int)Math.floor(Math.random()*(90000)+10000);
        if (getBossById(id) != null) {
            return generateRandomBossId();
        } else {
            return id;
        }
    }

    public List<Object> getTriggers(Class<?> triggerType) {
        List<Object> triggers = new ArrayList<>();

        getBosses().forEach(boss -> {
            boss.getTriggers().forEach(trigger -> {
                if (triggerType.isInstance(trigger)) {
                    triggers.add(trigger);
                }
            });
        });

        return triggers;
    }
}
