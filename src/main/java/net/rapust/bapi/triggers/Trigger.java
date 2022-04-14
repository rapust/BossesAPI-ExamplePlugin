package net.rapust.bapi.triggers;

import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.bosses.Ability;

public class Trigger {

    @Getter @Setter private Ability ability;

    public Trigger(Ability ability) {
        this.ability = ability;
    }

}
