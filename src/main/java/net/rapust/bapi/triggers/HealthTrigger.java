package net.rapust.bapi.triggers;

import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.bosses.Ability;

public class HealthTrigger extends Trigger {

    @Getter @Setter private Double onHealth = 0.0D;
    @Getter @Setter private Double onPercent = 0.0D;

    public HealthTrigger(Ability ability) {
        super(ability);
    }

}
