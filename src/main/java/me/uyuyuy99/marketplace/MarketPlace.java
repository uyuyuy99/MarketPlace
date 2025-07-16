package me.uyuyuy99.marketplace;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.uyuyuy99.marketplace.cmd.*;
import me.uyuyuy99.marketplace.listing.ListingManager;
import me.uyuyuy99.marketplace.storage.Config;
import me.uyuyuy99.marketplace.storage.MongoDatabase;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class MarketPlace extends JavaPlugin {

    private static MarketPlace plugin;
    private static MongoDatabase db;
    private static ListingManager listings;
    private static Economy econ;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onEnable() {
        plugin = this;
        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();

        // Load/generate config.yml
        try {
            Config.load();
        } catch (IOException e) {
            getLogger().severe("Could not load config.yml!");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        // Setup economy
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Unable to to hook into Vault economy!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        econ = rsp.getProvider();

        // Create item listing manager & database
        listings = new ListingManager();
        db = new MongoDatabase(
                Config.get().getString("mongodb.host"),
                Config.get().getInt("mongodb.port"),
                Config.get().getString("mongodb.database"),
                Config.get().getString("mongodb.user"),
                Config.get().getString("mongodb.password")
        );

        // Connect to DB & load item listings
        try {
            db.connect();
        } catch (Exception e) {
            getLogger().severe("Couldn't connect to MongoDB! Please fill in your connection details in config.yml.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            db.loadItems();
        } catch (Exception e) {
            getLogger().severe("Couldn't load item listings from MongoDB!");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        // Register commands
        new SellCmd().register();
        new MarketplaceCmd().register();
        new BlackMarketCmd().register();
        new MyListingsCommand().register();
        new TransactionsCmd().register();
    }

    @Override
    public void onDisable() {
        db.disconnect();
    }

    public static MarketPlace get() {
        return plugin;
    }

    public static MongoDatabase db() {
        return db;
    }

    public static ListingManager listings() {
        return listings;
    }

    public static Economy econ() {
        return econ;
    }

}
