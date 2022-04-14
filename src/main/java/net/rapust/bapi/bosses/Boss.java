package net.rapust.bapi.bosses;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import net.rapust.bapi.databases.SQLDatabase;
import net.rapust.bapi.enums.Resist;
import net.rapust.bapi.managers.BossesManager;
import net.rapust.bapi.triggers.DamageTypeTrigger;
import net.rapust.bapi.triggers.HealthTrigger;
import net.rapust.bapi.triggers.TimedTrigger;
import net.rapust.bapi.triggers.Trigger;
import net.rapust.bapi.utils.Logger;
import net.rapust.bapi.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

public class Boss {

    @Getter private final Integer id;
    @Getter private final String name;

    @Getter @Setter private EntityType entityType;
    @Getter @Setter private Location spawnLocation;
    @Getter @Setter private Long respawnTime;

    @Getter @Setter private Equipment equipment = null;
    @Getter @Setter private List<Resist> attackResists;
    @Getter @Setter private List<EntityDamageEvent.DamageCause> damageResists;
    @Getter @Setter private List<Object> triggers;
    @Getter @Setter private Map<Attribute, Double> baseAttributes;
    @Getter @Setter private Boolean isBaby = false;

    @Getter @Setter private Long lastExistTime = 0L;
    @Getter @Setter private Integer lastKilledId = null;

    @Getter private Entity entity = null;
    @Getter private Map<Player, Double> playersDamage;

    @Getter private Hologram hologram = null;

    public Boss(String name, Integer id, EntityType entityType, Location spawnLocation, Long respawnTime) {
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.spawnLocation = spawnLocation;
        this.respawnTime = respawnTime;

        this.attackResists = new ArrayList<>();
        this.damageResists = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.baseAttributes = new HashMap<>();

        this.hologram = new Hologram();
        Bukkit.getOnlinePlayers().forEach(player -> {
            hologram.addPlayer(player);
        });

    }

    public void playerDamage(Player player, Double damage) {
        Double already = playersDamage.get(player);
        if (already == null) {
            already = damage;
        } else {
            already+=damage;
        }
        playersDamage.put(player, already);
    }

    public void start() {
        BossesManager.getInstance().addBoss(this);
    }

    public void stop() {
        BossesManager.getInstance().removeBoss(this);
        entity.remove();
        entity = null;
        Bukkit.getOnlinePlayers().forEach(player -> {
            getHologram().killHolograms(player);
        });
    }

    public void spawn() {

        if (entity != null) return;

        triggers.forEach(trigger -> {
            if (trigger instanceof TimedTrigger timedTrigger) {
                timedTrigger.setLastUsageTime(0L);
            }
        });

        if (playersDamage == null) {
            playersDamage = new HashMap<>();
        } else {
            playersDamage.clear();
        }

        Entity entity = Objects.requireNonNull(spawnLocation.getWorld()).spawnEntity(spawnLocation, entityType);

        if (entity instanceof Ageable ageable) {
            if (isBaby) {
                ageable.setBaby();
            } else {
                ageable.setAdult();
            }
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        EntityEquipment entityEquipment = livingEntity.getEquipment();
        if (entityEquipment != null && equipment != null) {
            entityEquipment.setItemInMainHand(equipment.getItemInMainHand());
            entityEquipment.setItemInOffHand(equipment.getItemInOffHand());
            entityEquipment.setArmorContents(equipment.getArmor());
        }

        baseAttributes.keySet().forEach(attribute -> {
            Double value = baseAttributes.get(attribute);
            Objects.requireNonNull(livingEntity.getAttribute(attribute)).setBaseValue(value);
        });

        livingEntity.setCustomNameVisible(true);
        livingEntity.setCustomName(name);

        livingEntity.setHealth(Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());

        livingEntity.setRemoveWhenFarAway(false);

        this.entity = livingEntity;

        Bukkit.broadcastMessage(Messages.getMessage("messages.prefix")+Messages.getMessage("messages.spawned").replace("%boss%", name));

    }



    public void onDeath() {

        lastKilledId = entity.getEntityId();
        entity = null;

        lastExistTime = System.currentTimeMillis();

        Bukkit.broadcastMessage(Messages.getMessage("messages.prefix")+Messages.getMessage("messages.killed").replace("%boss%", name));

        Map<Player, Double> sortedByValue = playersDamage.entrySet()
                .stream()
                .sorted(Map.Entry.<Player, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Object[] array = sortedByValue.keySet().toArray();
        JsonArray jsonArray = new JsonArray();
        for (Integer i = 0; i <= 2; i++) {
            if (array.length <= i) break;
            JsonObject jsonObject = new JsonObject();
            Player player = (Player) array[i];
            Double damage = sortedByValue.get(player);
            jsonObject.addProperty("player", player.getName());
            jsonObject.addProperty("damage", damage);
            jsonArray.add(jsonObject);
            String playersString = Messages.getMessage("messages.players_top");
            Bukkit.broadcastMessage(playersString.replace("%player_name%", player.getName()).replace("%damage%", String.valueOf((Math.round(damage*100.0D))/100.0D)));
        }

        String jsonStr = jsonArray.toString();

        try {
            Connection connection = SQLDatabase.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BossesAPITable (boss_id, at_time, players) VALUES (?, ?, ?)");
            preparedStatement.setInt(1, id);
            preparedStatement.setLong(2, System.currentTimeMillis());
            preparedStatement.setString(3, jsonStr);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            Logger.warn("Error while writing data to the database! (SQLite)");
            e.printStackTrace();
        }

        triggers.forEach(trigger -> {
            if (trigger instanceof Trigger t) {
                t.getAbility().getSchedulers().forEach(id -> {
                    Bukkit.getScheduler().cancelTask(id);
                });
            }
        });
    }
}