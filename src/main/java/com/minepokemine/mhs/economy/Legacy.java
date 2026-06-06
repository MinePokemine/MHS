package com.minepokemine.mhs.economy;

import com.minepokemine.mhs.PluginMHS;
import com.minepokemine.mhs.savedata.Account;
import com.minepokemine.mhs.sidebar.PlayerSidebar;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Legacy implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() { return "MH$ Legacy"; }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return "MH$" + amount;
    }

    @Override
    public String currencyNamePlural() {
        return "MH$";
    }

    @Override
    public String currencyNameSingular() {
        return "MH$";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return PluginMHS.instance.economyAmount.has(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return PluginMHS.instance.economyAmount.has(player.getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return PluginMHS.instance.economyAmount.get(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString()).balance;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return PluginMHS.instance.economyAmount.get(player.getUniqueId().toString()).balance;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return getBalance(playerName, worldName) == amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return getBalance(player, worldName) == amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        String accountID = Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
        if (!has(playerName, amount)) {

            return new net.milkbowl.vault.economy.EconomyResponse(
                    0, PluginMHS.instance.economyAmount.get(accountID).balance,
                    net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Withdrawal would set balance at negative number" );
        }

        if (PluginMHS.instance.economyAmount.getSet(accountID, (orig) -> new Account(orig.name, (int)Math.floor(PluginMHS.instance.economyAmount.get(accountID.toString()).balance - amount)))) {
            if (PluginMHS.instance.sidebars.containsKey(accountID)) {
                PlayerSidebar.Generate(Bukkit.getPlayer(accountID)).apply(PluginMHS.instance.sidebars.get(accountID));
            }
            return new net.milkbowl.vault.economy.EconomyResponse(
                    amount, PluginMHS.instance.economyAmount.get(accountID).balance,
                    net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new net.milkbowl.vault.economy.EconomyResponse(
                0, PluginMHS.instance.economyAmount.get(accountID).balance,
                net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Property \"balance\" not supported");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        String accountID = Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
        if (has(playerName, -amount)) {
            return new net.milkbowl.vault.economy.EconomyResponse(
                0, PluginMHS.instance.economyAmount.get(accountID.toString()).balance,
                net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Deposit would set balance at negative number");
        }
        if (PluginMHS.instance.economyAmount.getSet(accountID.toString(), (orig) -> new Account(orig.name, (int)Math.floor(PluginMHS.instance.economyAmount.get(accountID.toString()).balance + amount)))) {
            if (PluginMHS.instance.sidebars.containsKey(accountID)) {
                PlayerSidebar.Generate(Bukkit.getPlayer(accountID)).apply(PluginMHS.instance.sidebars.get(accountID));
            }
            return new net.milkbowl.vault.economy.EconomyResponse(
                amount, PluginMHS.instance.economyAmount.get(accountID.toString()).balance,
                net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new net.milkbowl.vault.economy.EconomyResponse(
                0, PluginMHS.instance.economyAmount.get(accountID.toString()).balance,
                net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Property \"balance\" not supported");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(
                0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not implemented" );
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        PluginMHS.instance.economyAmount.add(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString(), new Account(playerName));
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        PluginMHS.instance.economyAmount.add(player.getUniqueId().toString(), new Account(player.getName()));
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
}
