package me.uyuyuy99.marketplace.listing;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.SneakyThrows;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.util.NumberUtil;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class BlackMarketTask extends BukkitRunnable {

    private static YamlDocument storage;
    private static long lastUpdated;
    private static long updateInterval = 1000 * 60 * 60 * 24;

    @SneakyThrows
    public BlackMarketTask() {
        storage = YamlDocument.create(
                new File(MarketPlace.get().getDataFolder(), "storage"),
                MarketPlace.get().getResource("storage")
        );
        lastUpdated = NumberUtil.longValue(storage.get("last-updated-black-market", 0L));
    }

    @SneakyThrows
    public static void save() {
        storage.set("last-updated-black-market", lastUpdated);
        storage.save();
    }

    @Override
    public void run() {
        refreshIfNeeded();
    }

    // Refreshes the items in the black market if 24 hours have passesd since the last refresh
    public static void refreshIfNeeded() {
        if (MarketPlace.listings().getListings().isEmpty()) {
            return;
        }
        if (System.currentTimeMillis() - lastUpdated > updateInterval) {
            MarketPlace.listings().refreshBlackMarket();
            lastUpdated = System.currentTimeMillis();
            save();
        }
    }

    public static long getTimeUntilNextUpdate() {
        return updateInterval - (System.currentTimeMillis() - lastUpdated);
    }

}
