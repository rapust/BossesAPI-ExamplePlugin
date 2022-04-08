package net.rapust.bapi.bosses;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class Ability {

    @Getter private final Boss boss;
    @Getter private final List<Integer> schedulers;

    public Ability(Boss boss) {
        this.boss = boss;
        this.schedulers = new ArrayList<>();
    }

    public abstract void execute();

    public void addScheduler(Integer id) {
        schedulers.add(id);
    }
}
