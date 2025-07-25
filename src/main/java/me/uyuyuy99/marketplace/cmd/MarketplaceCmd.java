package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.listing.MarketGui;
import me.uyuyuy99.marketplace.storage.Config;
import org.bukkit.ChatColor;

import java.io.IOException;

public class MarketplaceCmd extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("marketplace")
                .withPermission("marketplace.view")
                .executesPlayer((player, args) -> {
                    // Only open menu if there are active listings
                    if (MarketPlace.listings().getListings().isEmpty()) {
                        Config.sendMsg("market-empty", player);
                        return;
                    }
                    MarketGui gui = new MarketGui(player, false, false);
                    gui.show(player);
                })
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("marketplace.reload")
                        .executes((sender, args) -> {
                            try {
                                Config.reload();
                                sender.sendMessage(ChatColor.GREEN + "[MarketPlace] Successfully reloaded config.yml.");
                            } catch (IOException e) {
                                sender.sendMessage(ChatColor.RED + "[MarketPlace] Error reloading config.yml.");
                                throw new RuntimeException(e);
                            }
                        })
                )
                .register();
    }

}
