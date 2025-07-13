package me.uyuyuy99.marketplace.listing;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.storage.Config;
import me.uyuyuy99.marketplace.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MarketGui extends InventoryGui {

    private Player viewer;
    private int curPage = 0;
    private int maxPages;
    private GuiStateElement prevPageElement;
    private GuiStateElement nextPageElement;

    public MarketGui(Player viewer) {
        super(MarketPlace.get(), viewer, " ", buildGui());
        this.viewer = viewer;

        // Create filler for header area
        setFiller(Config.getIcon("market-gui.header-icon"));

        // Create the item listings
        GuiElementGroup group = new GuiElementGroup('b');
        for (Listing listing : MarketPlace.listings().getListings()) {
            ItemStack item = listing.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();

            lore.addAll(Config.getStringList("market-gui.listing-text",
                    "seller", listing.getUsername(),
                    "price", NumberUtil.formatLong(listing.getPrice())));
            meta.setLore(lore);
            item.setItemMeta(meta);

            final int id = listing.getId();
            group.addElement(new StaticGuiElement('c', item, (click) -> {
                // Check if listing is still available (prevents duping if other player buys the item while your menu is open)
                if (MarketPlace.listings().getListing(id) == null) {
                    Config.sendMsg("sold-out", viewer);
                    return true;
                }

                // TODO check if player is buying from himself

                // Check if player can afford the item
                if (!MarketPlace.econ().has(viewer, listing.getPrice())) {
                    Config.sendMsg("cant-afford", viewer);
                    return true;
                }

                // Open confirmation menu
                BuyConfirmGui confirmGui = new BuyConfirmGui(viewer, listing);
                confirmGui.show(viewer);
                return true;
            }));
        }
        addElement(group);
        this.maxPages = Math.max(1, ((group.size() - 1) / (Config.get().getInt("market-gui.rows-per-page") * 9)) + 1);

        // Create next/previous page buttons
        prevPageElement = new GuiStateElement('p',
                () -> curPage == 0 ? "off" : "on",
                new GuiStateElement.State(
                        change -> {},
                        "on",
                        Config.getIcon("market-gui.previous-page-icon"),
                        Config.getStringArray("market-gui.previous-page-text")
                ),
                new GuiStateElement.State(
                        change -> {},
                        "off",
                        Config.getIcon("market-gui.header-icon"),
                        " "
                )
        );
        nextPageElement = new GuiStateElement('n',
                () -> curPage + 1 >= maxPages ? "off" : "on",
                new GuiStateElement.State(
                        change -> {},
                        "on",
                        Config.getIcon("market-gui.next-page-icon"),
                        Config.getStringArray("market-gui.next-page-text")
                ),
                new GuiStateElement.State(
                        change -> {},
                        "off",
                        Config.getIcon("market-gui.header-icon"),
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

    private static String[] buildGui() {
        int rows = Math.min(6, Math.max(2, Config.get().getInt("market-gui.rows-per-page") + 1));
        String[] guiSetup = new String[rows];
        guiSetup[0] = "p       n";
        for (int i = 1; i < rows; i++) {
            guiSetup[i] = "bbbbbbbbb";
        }
        return guiSetup;
    }

    private void updateTitle() {
        setTitle(Config.getString("market-gui.title",
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
