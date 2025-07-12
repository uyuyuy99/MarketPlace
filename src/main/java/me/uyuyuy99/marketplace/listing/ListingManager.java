package me.uyuyuy99.marketplace.listing;

import lombok.Getter;
import me.uyuyuy99.marketplace.MarketPlace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// Manages all the item listings in the marketplace
@Getter
public class ListingManager {

    private Queue<Listing> listings = new ConcurrentLinkedQueue<>();

    public void addListing(Player player, ItemStack item, long price) {
        // Add to DB, and add to manager once completed (async)
        MarketPlace.db().addItemListing(player, item, price).thenAccept((listing) -> {
            listings.add(listing);
        });
    }

}
