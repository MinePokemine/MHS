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

public class OpCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) return false;
        switch (args[0].toLowerCase()) {
            case "add", "give", "increase":
                if (args.length < 3) {
                    sender.sendMessage("Usage: /" + command.getName() + " add [Player] [Amount]");
                    return true;
                }
                BigDecimal value;
                try {
                    value = BigDecimal.valueOf(Integer.parseInt(args[2]));
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("Usage: /" + command.getName() + " add [Player] [Amount]");
                    return true;
                }

                org.bukkit.entity.Player reciever = Bukkit.getPlayer(args[1]);
                if (reciever == null) {
                    sender.sendMessage("Usage: /" + command.getName() + " add [Player] [Amount]");
                    return true;
                }
                PluginMHS.instance.economyModern.deposit(PluginMHS.instance.getName(), reciever.getUniqueId(), value);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/mhs reload");
                return true;

            case "subtract", "take", "remove", "decrease":
                if (args.length < 3) {
                    sender.sendMessage("Usage: /" + command.getName() + " subtract [Player] [Amount]");
                    return true;
                }
                BigDecimal value2;
                try {
                    value2 = BigDecimal.valueOf(Integer.parseInt(args[2]));
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("Usage: /" + command.getName() + " subtract [Player] [Amount]");
                    return true;
                }

                org.bukkit.entity.Player reciever2 = Bukkit.getPlayer(args[1]);
                if (reciever2 == null) {
                    sender.sendMessage("Usage: /" + command.getName() + " subtract [Player] [Amount]");
                    return true;
                }
                PluginMHS.instance.economyModern.withdraw(PluginMHS.instance.getName(), reciever2.getUniqueId(), value2);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/mhs reload");
                return true;

            case "set", "setbalance", "setbal", "setmhs", "setmh$":
                if (args.length < 3) {
                    sender.sendMessage("Usage: /" + command.getName() + " set [Player] [Amount]");
                    return true;
                }
                BigDecimal value3;
                try {
                    value3 = BigDecimal.valueOf(Integer.parseInt(args[2]));
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("Usage: /" + command.getName() + " set [Player] [Amount]");
                    return true;
                }

                Player reciever3 = Bukkit.getPlayer(args[1]);
                if (reciever3 == null) {
                    sender.sendMessage("Usage: /" + command.getName() + " set [Player] [Amount]");
                    return true;
                }
                PluginMHS.instance.economyModern.set(PluginMHS.instance.getName(), reciever3.getUniqueId(), value3);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/mhs reload");
                return true;

            case "load":
                try {
                    PluginMHS.instance.economyAmount.load();
                }
                catch (IOException e) {
                    sender.sendMessage("Failed to load information");
                    PluginMHS.instance.logger.warning("Failed to load information (IOException) from /load: " + e.getMessage());
                }
                return true;

            default:
                return false;

        }
    }
}
