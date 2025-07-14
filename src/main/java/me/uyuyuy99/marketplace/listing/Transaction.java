package me.uyuyuy99.marketplace.listing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Transaction {

    private ItemStack item;
    private UUID buyer;
    private UUID seller;
    private long moneySpent;
    private long moneyEarned;
    private long time;

    public boolean isBuyer(Player player) {
        return player.getUniqueId().equals(buyer);
    }

    public boolean isSeller(Player player) {
        return player.getUniqueId().equals(seller);
    }

}
