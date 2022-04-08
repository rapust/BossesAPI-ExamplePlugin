package net.rapust.bapi.triggers;

import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.bosses.Ability;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageTypeTrigger {

    @Getter @Setter private Ability ability;
    @Getter @Setter private EntityDamageEvent.DamageCause cause;

    public DamageTypeTrigger(Ability ability, EntityDamageEvent.DamageCause cause) {
        this.ability = ability;
        this.cause = cause;
    }

}
