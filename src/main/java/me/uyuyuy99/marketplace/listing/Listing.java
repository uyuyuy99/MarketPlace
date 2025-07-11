package me.uyuyuy99.marketplace.listing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

// Represents a single item listing in the marketplace
@Getter
@AllArgsConstructor
public class Listing {

    private final int id;
    private UUID uuid;
    private ItemStack item;
    private long price;
    private long timeListed;

}
