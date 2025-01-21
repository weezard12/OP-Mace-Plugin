package com.weezard12.maceOP;

import org.bukkit.plugin.java.JavaPlugin;

public final class MaceOP extends JavaPlugin {

    public static MaceOP plugin;

    private double healthBoost;
    private int shieldDisableTicks;

    @Override
    public void onEnable() {
        plugin = this;
        // Save default config if not present
        saveDefaultConfig();
        loadConfigValues();

        getServer().getPluginManager().registerEvents(new MaceListener(),this);

        getCommand("about").setExecutor(new AboutCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfigValues() {
        healthBoost = getConfig().getDouble("health-boost", 6.0);
        shieldDisableTicks = getConfig().getInt("shield-disable-ticks", 220);

        getLogger().info("Loaded Config - Health Boost: " + healthBoost + ", Shield Disable Ticks: " + shieldDisableTicks);
    }

    public double getHealthBoost() {
        return healthBoost;
    }

    public int getShieldDisableTicks() {
        return shieldDisableTicks;
    }
}
