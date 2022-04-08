package net.rapust.bapi;

import lombok.Getter;
import net.rapust.bapi.commands.BossesAPICommand;
import net.rapust.bapi.databases.SQLDatabase;
import net.rapust.bapi.listeners.*;
import net.rapust.bapi.managers.BossesManager;
import net.rapust.bapi.utils.Logger;
import net.rapust.bapi.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class BossesAPI extends JavaPlugin {

    @Getter private static BossesAPI instance;

    @Override
    public void onEnable() {
        instance = this;

        initFiles();
        registerEvents();
        registerCommands();

        new SQLDatabase();
        new BossesManager();

        BossesManager.getInstance().getBosses().forEach(boss -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                boss.getHologram().addPlayer(player);
            });
        });

        Logger.log("API is enabled!");
    }

    @Override
    public void onDisable() {

        BossesManager.getInstance().getBosses().forEach(boss -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                boss.getHologram().killHolograms(player);
            });
        });

        Logger.log("API is disabled!");
    }

    private void initFiles() {

        File configFile = new File(getDataFolder()+File.separator+"config.yml");
        if (!configFile.exists()) {
            getConfig().options().copyDefaults();
            saveDefaultConfig();
        }

        saveResource("messages.yml", false);

        File messagesFile = new File(getDataFolder()+File.separator+"messages.yml");
        FileConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);
        Messages.setConfiguration(messages);

        Logger.log("Files are initialized!");

    }

    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EntityDamageListener(), this);
        pluginManager.registerEvents(new EntityDamageByEntityListener(), this);
        pluginManager.registerEvents(new EntityDeathListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);

        Logger.log("Events are registered!");
    }

    private void registerCommands() {
        getCommand("bossesapi").setExecutor(new BossesAPICommand());

        Logger.log("Commands are registered!");
    }

}
