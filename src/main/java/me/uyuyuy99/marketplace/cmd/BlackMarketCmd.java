package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.listing.BlackMarketTask;
import me.uyuyuy99.marketplace.listing.MarketGui;
import me.uyuyuy99.marketplace.storage.Config;
import me.uyuyuy99.marketplace.util.TimeUtil;

public class BlackMarketCmd extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("blackmarket")
                .withPermission("marketplace.blackmarket")
                .executesPlayer((player, args) -> {
                    // Only open menu if there are active listings
                    if (MarketPlace.listings().getBlackMarketListings().isEmpty()) {
                        Config.sendMsg("black-market-empty", player,
                                "time", TimeUtil.formatTimeAbbr(BlackMarketTask.getTimeUntilNextUpdate() / 1000
                        ));
                        return;
                    }
                    MarketGui gui = new MarketGui(player, true, false);
                    gui.show(player);
                })
                .withSubcommand(new CommandAPICommand("refresh")
                        .withPermission("marketplace.refresh")
                        .executes((sender, args) -> {
                            MarketPlace.listings().refreshBlackMarket();
                        })
                )
                .register();
    }

}
