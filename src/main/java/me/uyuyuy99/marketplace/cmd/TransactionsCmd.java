package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.listing.MarketGui;
import me.uyuyuy99.marketplace.listing.TransactionsGui;
import me.uyuyuy99.marketplace.storage.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class TransactionsCmd extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("transactions")
                .withPermission("marketplace.history")
                .executesPlayer((player, args) -> {
                    // Get transaction history from DB (async), then show GUI
                    MarketPlace.db().getTransactions(player).thenAccept(transactions -> {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                TransactionsGui gui = new TransactionsGui(player, transactions);
                                gui.show(player);
                            }
                        }.runTask(MarketPlace.get());
                    });
                })
                .register();
    }

}
