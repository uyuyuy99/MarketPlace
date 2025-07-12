package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import me.uyuyuy99.marketplace.listing.MarketGui;

public class MarketplaceCmd extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("marketplace")
                .withPermission("marketplace.view")
                .executesPlayer((player, args) -> {
                    MarketGui gui = new MarketGui(player);
                    gui.show(player);
                })
                .register();
    }

}
