package me.uyuyuy99.marketplace.listing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// Manages all the item listings in the marketplace
public class ListingManager {

    private Queue<Listing> listings = new ConcurrentLinkedQueue<>();

    public void add(Listing listing) {
        listings.add(listing);
    }

}
