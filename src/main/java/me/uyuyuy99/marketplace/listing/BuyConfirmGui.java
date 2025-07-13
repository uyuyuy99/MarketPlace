package me.uyuyuy99.marketplace.listing;

import de.themoep.inventorygui.*;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.storage.Config;
import me.uyuyuy99.marketplace.util.ItemUtil;
import me.uyuyuy99.marketplace.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyConfirmGui extends InventoryGui {

    private Player viewer;

    public BuyConfirmGui(Player viewer, Listing listing) {
        super(MarketPlace.get(), viewer, Config.getString("buy-confirm-gui.title"), buildGui());

        // Create filler for header area
        setFiller(Config.getIcon("buy-confirm-gui.filler-icon"));

        // Create cancel & confirm buttons
        GuiElement cancelBtn = new StaticGuiElement('a',
                Config.getIcon("buy-confirm-gui.cancel-icon"),
                click -> {
                    Config.sendMsg("buy-cancel", viewer);
                    close(viewer);
                    return true;
                },
                Config.getStringArray("buy-confirm-gui.cancel-text",
                        "amount", listing.getItem().getAmount(),
                        "item", ItemUtil.getDisplayName(listing.getItem()),
                        "seller", listing.getUsername(),
                        "price", NumberUtil.formatLong(listing.getPrice()))
        );
        GuiElement confirmBtn = new StaticGuiElement('b',
                Config.getIcon("buy-confirm-gui.confirm-icon"),
                click -> {
                    // Double-check if listing is still available (prevents duping if other player buys the item while your menu is open)
                    if (MarketPlace.listings().getListing(listing.getId()) == null) {
                        Config.sendMsg("sold-out", viewer);
                        return true;
                    }

                    // Double-check that player still has the funds to buy it
                    if (MarketPlace.econ().has(viewer, listing.getPrice())) {
                        // Exchange money
                        MarketPlace.econ().withdrawPlayer(viewer, listing.getPrice());
                        MarketPlace.econ().depositPlayer(Bukkit.getOfflinePlayer(listing.getUuid()), listing.getPrice());

                        // Give the item to the player & remove listing
                        ItemUtil.giveOrDropItem(viewer, listing.getItem());
                        MarketPlace.listings().removeListing(listing);

                        Config.sendMsg("buy-confirm", viewer,
                                "amount", listing.getItem().getAmount(),
                                "item", ItemUtil.getDisplayName(listing.getItem()),
                                "price", NumberUtil.formatLong(listing.getPrice()));
                    } else {
                        Config.sendMsg("cant-afford", viewer);
                    }
                    close(viewer);
                    return true;
                },
                Config.getStringArray("buy-confirm-gui.confirm-text",
                        "amount", listing.getItem().getAmount(),
                        "item", ItemUtil.getDisplayName(listing.getItem()),
                        "seller", listing.getUsername(),
                        "price", NumberUtil.formatLong(listing.getPrice()))
        );

        // Add buttons to GUI
        setElement(Config.get().getInt("buy-confirm-gui.cancel-slot"), cancelBtn);
        setElement(Config.get().getInt("buy-confirm-gui.confirm-slot"), confirmBtn);
    }

    private static String[] buildGui() {
        int rows = Math.min(6, Math.max(1, Config.get().getInt("buy-confirm-gui.rows")));
        String[] guiSetup = new String[rows];
        Arrays.fill(guiSetup, "         ");
        return guiSetup;
    }

}
