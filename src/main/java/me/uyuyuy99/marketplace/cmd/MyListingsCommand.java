package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.listing.MarketGui;
import me.uyuyuy99.marketplace.storage.Config;
import org.bukkit.ChatColor;

import java.io.IOException;

public class MyListingsCommand extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("mylistings")
                .withPermission("marketplace.view")
                .executesPlayer((player, args) -> {
                    MarketGui gui = new MarketGui(player, false, true);
                    gui.show(player);
                })
                .register();
    }

}
