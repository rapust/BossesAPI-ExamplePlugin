package net.rapust.bapi.bosses;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public class Equipment {

    @Getter @Setter private ItemStack itemInMainHand;
    @Getter @Setter private ItemStack itemInOffHand;
    @Getter @Setter private ItemStack[] armor;

    public Equipment() {
        armor = new ItemStack[4];
    }

}
