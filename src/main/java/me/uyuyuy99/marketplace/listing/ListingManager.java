package me.uyuyuy99.marketplace.listing;

import lombok.Getter;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.storage.Config;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadLocalRandom;

// Manages all the item listings in the marketplace
@Getter
public class ListingManager {

    private Deque<Listing> listings = new ConcurrentLinkedDeque<>();
    private BlackMarketTask blackMarketTask;
    private Deque<Listing> blackMarketListings = new ConcurrentLinkedDeque<>();

    public ListingManager() {
        blackMarketTask = new BlackMarketTask();
        blackMarketTask.runTaskTimer(MarketPlace.get(), 100L, 1200L);
    }

    public Listing getListing(int id) {
        return listings.stream()
                .filter(l -> l.getId() == id)
                .findAny()
                .orElse(null);
    }

    public void addListing(Player player, ItemStack item, long price) {
        // Add to DB, and add to manager once completed (async)
        MarketPlace.db().addItemListing(player, item, price).thenAccept((listing) -> {
            listings.addFirst(listing);
        });
    }

    public void removeListing(Listing listing) {
        listings.remove(listing);
        blackMarketListings.remove(listing);
        MarketPlace.db().removeItemListing(listing);
    }

    public void refreshBlackMarket() {
        double chance = ((double) Config.get().getInt("options.black-market-item-chance")) / 100.0;
        List<Listing> shuffledListings = new ArrayList<>(listings);
        Collections.shuffle(shuffledListings);
        blackMarketListings = new ConcurrentLinkedDeque<>(shuffledListings.stream()
                .filter(l -> ThreadLocalRandom.current().nextDouble() < chance)
                .limit(Config.get().getInt("options.black-market-max-items"))
                .toList());

        // If no items were chosen, pick at least one so the black market isn't empty
        if (blackMarketListings.isEmpty() && !shuffledListings.isEmpty()) {
            blackMarketListings.add(shuffledListings.get(0));
        }

        // Save the new black market listings in DB
        MarketPlace.db().saveBlackMarketListings();

        // Broadcast message
        String broadcastMsg = Config.getMsg("black-market-reset-broadcast");
        if (!broadcastMsg.isEmpty()) {
            MarketPlace.get().getServer().broadcastMessage(broadcastMsg);
        }
    }

    public boolean isOnBlackMarket(Listing listing) {
        return blackMarketListings.contains(listing);
    }

}
