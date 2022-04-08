package net.rapust.bapi.triggers;

import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.bosses.Ability;

public class HealthTrigger {

    @Getter @Setter private Ability ability;
    @Getter @Setter private Double onHealth = null;
    @Getter @Setter private Double onPercent = null;

    public HealthTrigger(Ability ability) {
        this.ability = ability;
    }

}
