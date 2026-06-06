package com.minepokemine.mhs.savedata;

import com.minepokemine.mhs.PluginMHS;
import org.bukkit.configuration.file.FileConfiguration;

public class Account {
    public int balance;
    public String name;

    public Account(String name) {
        FileConfiguration config = PluginMHS.instance.getConfig();
        balance = config.getInt("startingMH$");
        this.name = name;
    }

    public Account(String name, int balance) {
        this.balance = balance;
        this.name = name;
    }
}
