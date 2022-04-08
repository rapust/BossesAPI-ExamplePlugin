package net.rapust.bapi.commands;

import net.rapust.bapi.BossesAPI;
import net.rapust.bapi.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class BossesAPICommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String prefix = Messages.getMessage("messages.prefix");

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(prefix+Messages.getMessage("messages.bossesapi.help"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                try {
                    if (!sender.hasPermission("bossesapi.reload")) {
                        sender.sendMessage(prefix+Messages.getMessage("messages.bossesapi.no_perm"));
                        return true;
                    }

                    BossesAPI plugin = BossesAPI.getInstance();
                    plugin.reloadConfig();

                    File messagesFile = new File(plugin.getDataFolder()+File.separator+"messages.yml");
                    FileConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);
                    Messages.setConfiguration(messages);

                    sender.sendMessage(prefix+Messages.getMessage("messages.bossesapi.reloaded"));
                } catch (Exception e) {
                    sender.sendMessage(prefix+Messages.getMessage("messages.bossesapi.error_reloading"));
                }
                break;
            default:
                sender.sendMessage(prefix+Messages.getMessage("messages.bossesapi.help"));
        }

        return true;
    }
}
