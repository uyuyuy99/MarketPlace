package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.listing.MarketGui;
import me.uyuyuy99.marketplace.storage.Config;

public class BlackMarketCmd extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("blackmarket")
                .withPermission("marketplace.blackmarket")
                .executesPlayer((player, args) -> {
                    // Only open menu if there are active listings
                    if (MarketPlace.listings().getListings().isEmpty()) {
                        Config.sendMsg("market-empty", player);
                        return;
                    }
                    MarketGui gui = new MarketGui(player, true, false);
                    gui.show(player);
                })
                .register();
    }

}
