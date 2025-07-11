package me.uyuyuy99.marketplace.cmd;

import me.uyuyuy99.marketplace.MarketPlace;

public abstract class Cmd {

    protected MarketPlace plugin;

    public Cmd() {
        this.plugin = MarketPlace.get();
    }

    public abstract void register(); // Register the command

}
