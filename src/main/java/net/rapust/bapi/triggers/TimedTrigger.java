package net.rapust.bapi.triggers;

import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.bosses.Ability;

public class TimedTrigger extends Trigger {

    @Getter @Setter private Long repeatingTime;
    @Getter @Setter private Long lastUsageTime = 0L;

    public TimedTrigger(Ability ability, Long time) {
        super(ability);
        this.repeatingTime = time;
    }

}
