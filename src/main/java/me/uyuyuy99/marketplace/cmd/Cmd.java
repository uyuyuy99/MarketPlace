package me.uyuyuy99.marketplace.cmd;

import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.listing.ListingManager;

public abstract class Cmd {

    protected MarketPlace plugin;
    protected ListingManager listings;

    public Cmd() {
        this.plugin = MarketPlace.get();
        this.listings = MarketPlace.listings();
    }

    public abstract void register(); // Register the command

}
