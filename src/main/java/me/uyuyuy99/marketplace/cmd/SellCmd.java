package me.uyuyuy99.marketplace.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LongArgument;
import me.uyuyuy99.marketplace.storage.Config;
import me.uyuyuy99.marketplace.util.ItemUtil;
import me.uyuyuy99.marketplace.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SellCmd extends Cmd {

    @Override
    public void register() {
        new CommandAPICommand("sell")
                .withPermission("marketplace.sell")
                .withArguments(new LongArgument("price"))
                .executesPlayer((player, args) -> {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    long price = NumberUtil.longValue(args.get("price"));

                    // Check if player is holding an item
                    if (item == null || item.getType() == Material.AIR) {
                        Config.sendMsg("no-item-in-hand", player);
                        return;
                    }

                    // Check if player has reached his maximum # of listings
                    long listingCount = listings.getListings().stream().filter(l -> l.getUuid().equals(player.getUniqueId())).count();
                    int maxListings = Config.get().getInt("options.max-item-listings");
                    if (listingCount >= maxListings) {
                        Config.sendMsg("reached-max-listings", player, "max", maxListings);
                        return;
                    }

                    // Add listing & remove item from player's inventory
                    listings.addListing(player, item, price);
                    Config.sendMsg("list-item", player,
                            "amount", item.getAmount(),
                            "item", ItemUtil.getDisplayName(item),
                            "price", NumberUtil.formatLong(price));
                    player.getInventory().setItemInMainHand(null);
                })
                .register();
    }

}
