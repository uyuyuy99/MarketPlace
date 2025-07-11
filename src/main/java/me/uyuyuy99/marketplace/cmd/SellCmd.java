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

                    String serialized = ItemUtil.serializeItem(item);
                    //TODO
                })
                .register();
    }

}
