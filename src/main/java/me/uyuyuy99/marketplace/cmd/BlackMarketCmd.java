package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import me.uyuyuy99.marketplace.listing.MarketGui;

public class BlackMarketCmd extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("blackmarket")
                .withPermission("marketplace.blackmarket")
                .executesPlayer((player, args) -> {
                    MarketGui gui = new MarketGui(player, true);
                    gui.show(player);
                })
                .register();
    }

}
