package com.minepokemine.mhs.economy;

import com.minepokemine.mhs.PluginMHS;
import com.minepokemine.mhs.savedata.Account;
import com.minepokemine.mhs.sidebar.PlayerSidebar;
import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;

public class Modern implements Economy {

    @Override
    public @NotNull BigDecimal balance(@NotNull String pluginName, @NotNull UUID accountID) {
        return BigDecimal.valueOf(PluginMHS.instance.economyAmount.get(accountID.toString()).balance);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return PluginMHS.instance.economyAmount.get(accountID.toString()).balance == amount.intValue();
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return PluginMHS.instance.economyAmount.get(accountID.toString()).balance == amount.intValue();
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return PluginMHS.instance.economyAmount.get(accountID.toString()).balance == amount.intValue();
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        BigDecimal prevAmt = balance(pluginName, accountID);

        if (prevAmt.add(amount).signum() == -1) {
            /*return new EconomyResponse(
                    BigDecimal.ZERO, BigDecimal.valueOf(PluginMHS.instance.economyAmount.get(accountID.toString()).balance),
                    EconomyResponse.ResponseType.FAILURE, "Withdrawal would set balance at negative number" );*/
            return set(pluginName, accountID, BigDecimal.ZERO);
        }

        return set(pluginName, accountID, prevAmt.add(amount));
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return deposit(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return deposit(pluginName, accountID, amount);
    }

    @Override
    public boolean createSharedAccount(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String name, @NotNull UUID owner) {
        return false;
    }

    @Override
    public boolean isAccountOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return accountID == uuid;
    }

    @Override
    public boolean setOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean isAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return uuid == accountID;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission... initialPermissions) {
        return false;
    }

    @Override
    public boolean removeAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean hasAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission) {
        return false;
    }

    @Override
    public boolean updateAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission, boolean value) {
        return false;
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        BigDecimal prevAmt = BigDecimal.valueOf(PluginMHS.instance.economyAmount.get(accountID.toString()).balance);

        if (prevAmt.subtract(amount).signum() == -1) {
            /*return new EconomyResponse(
                    BigDecimal.ZERO, BigDecimal.valueOf(PluginMHS.instance.economyAmount.get(accountID.toString()).balance),
                    EconomyResponse.ResponseType.FAILURE, "Withdrawal would set balance at negative number");*/
            return set(pluginName, accountID, BigDecimal.ZERO);
        }

        return set(pluginName, accountID, prevAmt.subtract(amount));
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return withdraw(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return withdraw(pluginName, accountID, amount);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, boolean player) {
        if (!player) {
            return false;
        }
        PluginMHS.instance.economyAmount.add(accountID.toString(), new Account(name));
        return true;
    }

    @Override public boolean createAccount(@NotNull UUID accountID, @NotNull String name) { return createAccount(accountID, name, true); }
    @Override public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName) {return createAccount(accountID, name, true); }
    @Override public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName, boolean player) {return createAccount(accountID, name, player);}

    @Override
    public @NotNull Map<UUID, String> getUUIDNameMap() {
        HashMap<UUID, String> map = new HashMap<>();
        PluginMHS.instance.economyAmount.getAll().forEach((key, value) -> {
            map.put(UUID.fromString(key), value.name);
        });
        return map;
    }

    @Override
    public Optional<String> getAccountName(@NotNull UUID accountID) {
        return Optional.empty();
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID) {
        return PluginMHS.instance.economyAmount.has(accountID.toString());
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID, @NotNull String worldName) {
        return PluginMHS.instance.economyAmount.has(accountID.toString());
    }

    @Override
    public boolean renameAccount(@NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean renameAccount(@NotNull String plugin, @NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean deleteAccount(@NotNull String plugin, @NotNull UUID accountID) {
        PluginMHS.instance.economyAmount.remove(accountID.toString());
        return true;
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency) {
        return currency.equalsIgnoreCase("MH$");
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency, @NotNull String world) {
        return false;
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID) {
        return balance(pluginName, accountID);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world) {
        return balance(pluginName, accountID);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world, @NotNull String currency) {
        return balance(pluginName, accountID);
    }

    @Override public boolean isEnabled() { return true; }
    @Override public @NotNull String getName() { return "MH$ Legacy"; }
    @Override public boolean hasSharedAccountSupport() { return false; }
    @Override public boolean hasMultiCurrencySupport() { return false; }
    @Override public int fractionalDigits(@NotNull String pluginName) { return 0; }

    @Override public @NotNull String format(@NotNull BigDecimal amount) { return "MH$" + amount.toString(); }
    @Override public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount) { return "MH$" + amount.toString(); }
    @Override public @NotNull String format(@NotNull BigDecimal amount, @NotNull String currency) { return "MH$" + amount.toString(); }
    @Override public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount, @NotNull String currency) { return "MH$" + amount.toString(); }

    @Override public boolean hasCurrency(@NotNull String currency) { return currency.equalsIgnoreCase("MH$"); }
    @Override public @NotNull String getDefaultCurrency(@NotNull String pluginName) { return "MH$"; }
    @Override public @NotNull String defaultCurrencyNamePlural(@NotNull String pluginName) { return "MH$"; }
    @Override public @NotNull String defaultCurrencyNameSingular(@NotNull String pluginName) { return "MH$"; }
    @Override public @NotNull Collection<String> currencies() { return List.of("MH$"); }

    @Override
    public EconomyResponse set(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        if (PluginMHS.instance.economyAmount.getSet(accountID.toString(), (val) -> new Account(val.name, amount.intValue()))) {
            if (PluginMHS.instance.sidebars.containsKey(accountID)) {
                PlayerSidebar.Generate(Bukkit.getPlayer(accountID)).apply(PluginMHS.instance.sidebars.get(accountID));
            }
            return new EconomyResponse(
                    amount, BigDecimal.valueOf(PluginMHS.instance.economyAmount.get(accountID.toString()).balance),
                    EconomyResponse.ResponseType.SUCCESS, null);
        }
        else {
            return new EconomyResponse(
                    BigDecimal.ZERO, BigDecimal.valueOf(PluginMHS.instance.economyAmount.get(accountID.toString()).balance),
                    EconomyResponse.ResponseType.FAILURE, "Property \"balance\" not supported");
        }
    }
}
