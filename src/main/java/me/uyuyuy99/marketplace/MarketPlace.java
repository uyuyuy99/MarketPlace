package me.uyuyuy99.marketplace;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import me.uyuyuy99.marketplace.cmd.SellCmd;
import me.uyuyuy99.marketplace.listing.ListingManager;
import me.uyuyuy99.marketplace.storage.Config;
import me.uyuyuy99.marketplace.storage.MongoDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public final class MarketPlace extends JavaPlugin {

    private static MarketPlace plugin;
    private static MongoDatabase db;
    private static ListingManager listings;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        plugin = this;
        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();

        Config.load();

        //TODO load Mongo details from config, connect to DB

        // Register commands
        new SellCmd().register();
    }

    @Override
    public void onDisable() {
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

}
