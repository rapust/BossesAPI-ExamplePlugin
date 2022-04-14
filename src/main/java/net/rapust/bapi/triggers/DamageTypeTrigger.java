package net.rapust.bapi.triggers;

import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.bosses.Ability;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageTypeTrigger extends Trigger {

    @Getter @Setter private EntityDamageEvent.DamageCause cause;

    public DamageTypeTrigger(Ability ability, EntityDamageEvent.DamageCause cause) {
        super(ability);
        this.cause = cause;
    }

}
