package com.minepokemine.mhs.commands;

import com.minepokemine.mhs.PluginMHS;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class BaseCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) { PluginMHS.instance.logger.info("Sender " + sender.getName() + " tried to send a /mhs command without any arguments"); return true; }
        switch (args[0].toLowerCase()) {
            case "gift":
                if (args.length < 3) {
                    sender.sendMessage("Usage: /" + command.getName() + " " + args[0] + " [Player] [Amount]");
                    return true;
                }
                if (sender instanceof org.bukkit.entity.Player gifter) {
                    BigDecimal value;
                    try {
                        value = BigDecimal.valueOf(Integer.parseInt(args[2]));
                    }
                    catch (NumberFormatException e) {
                        sender.sendMessage("Usage: /" + command.getName() + " " + args[0] + " [Player] [Amount]");
                        return true;
                    }

                    if (value.signum() == -1) {
                        sender.sendMessage(PluginMHS.instance.getConfig().getString("message.negativeGift"));
                        return true;
                    }

                    if (PluginMHS.instance.economyModern.has(PluginMHS.instance.getName(), gifter.getUniqueId(), value)) {
                        org.bukkit.entity.Player reciever = Bukkit.getPlayer(args[1]);
                        if (reciever == null) {
                            sender.sendMessage("Usage: /" + command.getName() + " " + args[0] + " [Player] [Amount]");
                            return true;
                        }
                        if (PluginMHS.instance.economyModern.withdraw(PluginMHS.instance.getName(), gifter.getUniqueId(), value).transactionSuccess()) {
                            PluginMHS.instance.economyModern.deposit(PluginMHS.instance.getName(), reciever.getUniqueId(), value);
                        }
                        BaseCommands.reloadPlayer(gifter);
                        BaseCommands.reloadPlayer(reciever);
                    }
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/mhs reload");
                return true;

            case "reload":
                if (args.length == 1) {
                    PluginMHS.instance.economyAmount.getAll().forEach((uuid, acc) -> {
                        org.bukkit.entity.Player plr = Bukkit.getPlayer(UUID.fromString(uuid));
                        if (plr != null) {
                            reloadPlayer(plr);
                        }
                    });
                    return true;
                }

                else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage("Usage: /" + command.getName() + " " + args[0] + " or /" + command.getName() + " " + args[0] + " [Player]");
                        return true;
                    }

                    reloadPlayer(target);
                }

            case "save":
                PluginMHS.instance.logger.warning("Hello");
                try {
                    PluginMHS.instance.economyAmount.save();
                }
                catch (IOException e) {
                    sender.sendMessage("Failed to save information");
                    PluginMHS.instance.logger.warning("Failed to save information (IOException) from /save: " + e.getMessage());
                }
                return true;

            default:
                return false;
        }
    }

    public static void reloadPlayer(Player plr) {
        PluginMHS.instance.setPlayerHealth(plr, PluginMHS.instance.economyModern.balance(PluginMHS.instance.getName(), plr.getUniqueId()));
    }
}
