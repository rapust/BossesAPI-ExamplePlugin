package net.rapust.bapi.triggers;

import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.bosses.Ability;

public class TimedTrigger {

    @Getter @Setter private Ability ability;
    @Getter @Setter private Long repeatingTime;
    @Getter @Setter private Long lastUsageTime = 0L;

    public TimedTrigger(Ability ability, Long time) {
        this.ability = ability;
        this.repeatingTime = time;
    }

}
