package me.uyuyuy99.marketplace.listing;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.storage.Config;
import me.uyuyuy99.marketplace.util.ItemUtil;
import me.uyuyuy99.marketplace.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MarketGui extends InventoryGui {

    private String configKey;
    private int curPage = 0;
    private int maxPages;
    private GuiStateElement prevPageElement;
    private GuiStateElement nextPageElement;

    public MarketGui(Player viewer, boolean black, boolean mine) {
        super(MarketPlace.get(), viewer, " ", buildGui(black));
        if (black) {
            this.configKey = "black-market-gui.";
        } else if (mine) {
            this.configKey = "my-listings-gui.";
        } else {
            this.configKey = "market-gui.";
        }

        // Create filler for header area
        setFiller(Config.getIcon(configKey + "header-icon"));

        // If black market, randomize item listings
        Collection<Listing> listings;
        if (black) {
            listings = MarketPlace.listings().getBlackMarketListings();
        } else {
            if (mine) { // Only show your own listings
                listings = MarketPlace.listings().getListings().stream()
                        .filter(l -> viewer.getUniqueId().equals(l.getUuid()))
                        .toList();
            } else { // Show all listings
                listings = MarketPlace.listings().getListings();
            }
        }

        // Create the item listings
        GuiElementGroup group = new GuiElementGroup('b');
        for (Listing listing : listings) {
            long price = black ? listing.getPrice() / 2 : listing.getPrice();
            ItemStack item = listing.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();

            // Add text showing the seller & price to the end of the item lore
            String listingTextKey = listing.getUuid().equals(viewer.getUniqueId()) ? "my-listing-text" : "listing-text";
            lore.addAll(Config.getStringList(configKey + listingTextKey,
                    "seller", listing.getUsername(),
                    "price", NumberUtil.formatLong(price)));
            meta.setLore(lore);
            item.setItemMeta(meta);

            final int id = listing.getId();
            group.addElement(new StaticGuiElement('c', item, (click) -> {
                // Check if listing is still available (prevents duping if other player buys the item while your menu is open)
                if (MarketPlace.listings().getListing(id) == null) {
                    Config.sendMsg("sold-out", viewer);
                    return true;
                }

                // If player owns this item, remove from marketplace & give his item back
                if (listing.getUuid().equals(viewer.getUniqueId())) {
                    MarketPlace.listings().removeListing(listing);
                    ItemUtil.giveOrDropItem(viewer, listing.getItem());
                    Config.sendMsg("unlisted-item", viewer,
                            "amount", listing.getItem().getAmount(),
                            "item", ItemUtil.getDisplayName(listing.getItem())
                    );
                    close(viewer);
                    return true;
                }

                // Check if player can afford the item
                if (!MarketPlace.econ().has(viewer, price)) {
                    Config.sendMsg("cant-afford", viewer);
                    return true;
                }

                // If all checks pass, confirm the purchase
                BuyConfirmGui confirmGui = new BuyConfirmGui(viewer, listing, black);
                confirmGui.show(viewer);
                return true;
            }));
        }
        addElement(group);
        this.maxPages = Math.max(1, ((group.size() - 1) / (Config.get().getInt(configKey + "rows-per-page") * 9)) + 1);

        // If showing your own listings, create player head icon
        if (mine) {
            ItemStack headIcon = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) headIcon.getItemMeta();
            skullMeta.setOwnerProfile(viewer.getPlayerProfile());
            headIcon.setItemMeta(skullMeta);
            setElement(4, new StaticGuiElement('h', headIcon, Config.getStringArray(configKey + "player-head-text",
                    "player", viewer.getName()
            )));
        }

        // Create next/previous page buttons
        prevPageElement = new GuiStateElement('p',
                () -> curPage == 0 ? "off" : "on",
                new GuiStateElement.State(
                        change -> {},
                        "on",
                        Config.getIcon(configKey + "previous-page-icon"),
                        Config.getStringArray(configKey + "previous-page-text")
                ),
                new GuiStateElement.State(
                        change -> {},
                        "off",
                        Config.getIcon(configKey + "header-icon"),
                        " "
                )
        );
        nextPageElement = new GuiStateElement('n',
                () -> curPage + 1 >= maxPages ? "off" : "on",
                new GuiStateElement.State(
                        change -> {},
                        "on",
                        Config.getIcon(configKey + "next-page-icon"),
                        Config.getStringArray(configKey + "next-page-text")
                ),
                new GuiStateElement.State(
                        change -> {},
                        "off",
                        Config.getIcon(configKey + "header-icon"),
                        " "
                )
        );
        addElement(prevPageElement);
        addElement(nextPageElement);
        prevPageElement.setAction(click -> {
            if (prevPageElement.getState().getKey().equals("off")) return true;
            setPageNumber(curPage = Math.max(0, curPage - 1));
            updateTitle();
            show(viewer);
            return true;
        });
        nextPageElement.setAction(click -> {
            if (nextPageElement.getState().getKey().equals("off")) return true;
            setPageNumber(curPage = Math.min(maxPages, curPage + 1));
            show(viewer);
            return true;
        });
    }

    private static String[] buildGui(boolean black) {
        String configKey = black ? "black-market-gui." : "market-gui.";
        int rows = Math.min(6, Math.max(2, Config.get().getInt(configKey + "rows-per-page") + 1));
        String[] guiSetup = new String[rows];
        guiSetup[0] = "p       n";
        for (int i = 1; i < rows; i++) {
            guiSetup[i] = "bbbbbbbbb";
        }
        return guiSetup;
    }

    private void updateTitle() {
        setTitle(Config.getString(configKey + "title",
                "page", curPage + 1,
                "maxpages", maxPages));
    }

    @Override
    public void show(HumanEntity player, boolean checkOpen) {
        updateTitle();
        super.show(player, checkOpen);
    }

    @Override
    public void draw(HumanEntity who, boolean updateDynamic, boolean recreateInventory) {
        updateTitle();
        super.draw(who, updateDynamic, recreateInventory);
    }

}
