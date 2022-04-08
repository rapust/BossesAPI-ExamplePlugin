package net.rapust.bapi.bosses;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hologram {

    private final Map<Player, List<EntityArmorStand>> armorStands;

    public Hologram() {
        this.armorStands = new HashMap<>();
    }

    public void addPlayer(Player player) {
        armorStands.put(player, new ArrayList<>());
    }

    public void removePlayer(Player player) {
        armorStands.remove(player);
    }

    public void setHologram(List<String> strings, Location location) {
        location = location.clone();
        location.add(0.0, 1.5, 0.0);
        for (Player player : armorStands.keySet()) {
            if (!player.isOnline()) continue;
            List<EntityArmorStand> entityArmorStands = armorStands.get(player);
            if (entityArmorStands == null) entityArmorStands = new ArrayList<>();
            if (entityArmorStands.size() != strings.size()) {
                killHolograms(player);
                spawnHolograms(player, strings, location);
            } else {
                renameHolograms(player, strings);
            }
        }
    }

    public void killHolograms(Player player) {
        List<EntityArmorStand> stands = armorStands.get(player);
        if (stands == null) return;
        stands.forEach(stand -> {
            PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(stand.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
        });
        this.armorStands.put(player, new ArrayList<>());
    }

    private void renameHolograms(Player player, List<String> strings) {
        List<EntityArmorStand> entityArmorStands = armorStands.get(player);
        if (entityArmorStands == null) return;
        for (Integer i = 0; i < strings.size(); i++) {
            EntityArmorStand armorStand = entityArmorStands.get(i);
            armorStand.setCustomName(new ChatMessage(strings.get(i).replace("%player_name%", player.getName())));
            PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);
        }
    }

    private void spawnHolograms(Player player, List<String> strings, Location location) {
        List<EntityArmorStand> entityArmorStands = armorStands.get(player);
        if (entityArmorStands == null) entityArmorStands = new ArrayList<>();

        List<EntityArmorStand> finalEntityArmorStands = entityArmorStands;
        for (String string : strings) {
            CraftWorld craftWorld = (CraftWorld) location.getWorld();
            EntityArmorStand entityArmorStand = new EntityArmorStand(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ());

            entityArmorStand.setInvisible(true);
            entityArmorStand.setNoGravity(true);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setCustomName(new ChatMessage(string.replace("%player_name%", player.getName())));

            PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
            PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntityLiving);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);

            finalEntityArmorStands.add(entityArmorStand);

            location = location.subtract(0.0, 0.5, 0.0);
        }


        armorStands.put(player, finalEntityArmorStands);
    }

}
