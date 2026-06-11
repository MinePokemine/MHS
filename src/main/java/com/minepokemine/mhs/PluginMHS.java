package com.minepokemine.mhs;

import com.minepokemine.mhs.commands.BaseCommands;
import com.minepokemine.mhs.commands.OpCommands;
import com.minepokemine.mhs.economy.Legacy;
import com.minepokemine.mhs.economy.Modern;
import com.minepokemine.mhs.savedata.Account;
import com.minepokemine.mhs.savedata.KVDataStorage;
import com.minepokemine.mhs.sidebar.PlayerSidebar;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public final class PluginMHS extends JavaPlugin implements Listener {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PluginMHS.class);
    public Logger logger;

    public static PluginMHS instance;

    public ScoreboardLibrary scoreLib;
    public HashMap<UUID, Sidebar> sidebars;

    public Modern economyModern;
    public Legacy economyLegacy;

    public KVDataStorage<String, Account> economyAmount;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        sidebars = new HashMap<>();

        logger = getLogger();
        instance = this;

        PluginMHS.instance.getLogger().info("Get Logger Enable");
        PluginMHS.instance.logger.info("Logger Enable");

        getCommand("mhs").setExecutor(new BaseCommands());
        getCommand("mhsop").setExecutor(new OpCommands());

        try {
            scoreLib = ScoreboardLibrary.loadScoreboardLibrary(this);
        } catch (NoPacketAdapterAvailableException e) {
            scoreLib = new NoopScoreboardLibrary();
            logger.warning("Server version unsupported, scoreboard functionality will not be visible!");
        }


        economyAmount = new KVDataStorage<>("player_currency", String.class, Account.class);
        try {
            economyAmount.load();
        } catch (IOException e) {
            logger.severe("Could not load MH$ data: " + e.getMessage());
        }

        // Vault Setup

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Registering Vault economy...");

        economyModern = new Modern();
        getServer().getServicesManager().register(
                Economy.class,
                economyModern,
                this,
                ServicePriority.Normal
        );

        economyLegacy = new Legacy();
        getServer().getServicesManager().register(
                net.milkbowl.vault.economy.Economy.class,
                economyLegacy,
                this,
                ServicePriority.Normal
        );

        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);

        getLogger().info("Provider = " + (rsp == null ? "null" : rsp.getProvider().getName()));


        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if (scoreLib != null) {
            scoreLib.close();
        }

        // -- SAVE --
        // -- NEEDS TO BE LAST --
        for (int i = 0; i < getConfig().getInt("saveTries"); i++) {
            try {
                economyAmount.save();
                return;
            } catch (IOException e) {
                logger.warning("Failed to save data on try " + (i + 1) + "/" + getConfig().getInt("saveTries") + ": " + e.getMessage());
            }
        }
        logger.severe("Failed to save data");
        logger.info("Data: ");
        logger.info(economyAmount.getDataJson());
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!economyModern.hasAccount(player.getUniqueId())) {
            economyModern.createAccount(player.getUniqueId(), player.getName(), true);
            economyModern.set(getName(), player.getUniqueId(), BigDecimal.valueOf(getConfig().getInt("startingMH$")));
        }

        if (!economyLegacy.hasAccount(player.getName())) {
            economyLegacy.createPlayerAccount(player.getName());
        }

        setPlayerHealth(player, economyModern.balance(getName(), player.getUniqueId()));

        ComponentSidebarLayout layout = PlayerSidebar.Generate(player);
        Sidebar bar = scoreLib.createSidebar();
        layout.apply(bar);
        bar.addPlayer(player);
        sidebars.put(player.getUniqueId(), bar);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Sidebar bar = sidebars.get(player.getUniqueId());
        sidebars.remove(player.getUniqueId());
        bar.close();
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Entity cause = event.getDamageSource().getCausingEntity();
        if (cause instanceof Player killer) {
            Player victim = event.getPlayer();
            logger.info("Player " + victim.getName() + " killed by " + killer.getName() + " in world " + victim.getWorld().getName());

            int change = getConfig().getInt("lifestealMH$", -1);
            if (change == -1) {
                return;
            }

            EconomyResponse resp1 = economyModern.withdraw(getName(), victim.getUniqueId(), BigDecimal.valueOf(change));
            if (!resp1.transactionSuccess()) {
                logger.warning("Could not subtract money from victim " + victim.getName() + ". " + resp1.errorMessage);
                return;
            }

            EconomyResponse resp2 = economyModern.deposit (getName(), killer.getUniqueId(), BigDecimal.valueOf(change));
            if (!resp2.transactionSuccess()) {
                logger.warning("Could not add money to killer " + killer.getName() + ". " + resp1.errorMessage);
                economyModern.set(getName(), victim.getUniqueId(), resp1.balance.add(resp1.amount));
                return;
            }

            setPlayerHealth(victim, economyModern.balance(getName(), victim.getUniqueId()));
            setPlayerHealth(killer, economyModern.balance(getName(), victim.getUniqueId()));
        }
    }

    @EventHandler
    void onPlayerJoinWorld(PlayerChangedWorldEvent event) {
        World world = event.getPlayer().getWorld();
        logger.info("Player " + event.getPlayer().getName() + " went to world " + event.getPlayer().getWorld().getName());
        boolean shouldSpectate = getConfig().getBoolean("shouldSpectateBelowMinMH$");
        if (shouldSpectate && (economyModern.balance(getName(), event.getPlayer().getUniqueId())
                .compareTo(BigDecimal.valueOf(getConfig().getInt("healthLimit.min") / getConfig().getInt("MH$perMaxHealth"))) < 0)) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        else {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    public void setPlayerHealth(Player plr, BigDecimal mh$) {
        AttributeInstance health = plr.getAttribute(Attribute.MAX_HEALTH);
        int min_mh$ = getConfig().getInt("healthLimit.min") * getConfig().getInt("MH$perMaxHealth");
        int max_mh$ = getConfig().getInt("healthLimit.max") * getConfig().getInt("MH$perMaxHealth");
        if (health != null) {
            if (mh$.intValue() < min_mh$) {
                if (getConfig().getBoolean("shouldSpectateBelowMinMH$")) {
                    plr.setGameMode(GameMode.SPECTATOR);
                } else {
                    health.setBaseValue(getConfig().getInt("healthLimit.min"));
                }
            }
            else if (mh$.intValue() > max_mh$) {
                health.setBaseValue(getConfig().getInt("healthLimit.max"));
            }
            else {
                health.setBaseValue(mh$.doubleValue() / getConfig().getInt("MH$perMaxHealth"));
            }
        }
    }
}