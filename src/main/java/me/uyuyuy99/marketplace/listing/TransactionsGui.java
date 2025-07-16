package me.uyuyuy99.marketplace.listing;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.storage.Config;
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

public class TransactionsGui extends InventoryGui {

    private static String configKey = "transactions-gui.";
    private int curPage = 0;
    private int maxPages;
    private GuiStateElement prevPageElement;
    private GuiStateElement nextPageElement;

    public TransactionsGui(Player viewer, List<Transaction> transactions) {
        super(MarketPlace.get(), viewer, " ", buildGui());

        // Create filler for header area
        setFiller(Config.getIcon(configKey + "header-icon"));

        // Create the player head icon showing total transaction stats
        ItemStack headIcon = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) headIcon.getItemMeta();
        skullMeta.setOwnerProfile(viewer.getPlayerProfile());
        headIcon.setItemMeta(skullMeta);
        setElement(4, new StaticGuiElement('h', headIcon, Config.getStringArray(configKey + "player-head-text",
                "player", viewer.getName(),
                "purchases", transactions.stream().filter(t -> t.isBuyer(viewer)).count(),
                "sales", transactions.stream().filter(t -> t.isSeller(viewer)).count(),
                "moneyspent", NumberUtil.formatLong(transactions.stream().filter(t -> t.isBuyer(viewer)).mapToLong(Transaction::getMoneySpent).sum()),
                "moneyearned", NumberUtil.formatLong(transactions.stream().filter(t -> t.isSeller(viewer)).mapToLong(Transaction::getMoneyEarned).sum())
        )));

        // Create the item transaction icon
        GuiElementGroup group = new GuiElementGroup('b');
        for (Transaction tran : transactions) {
            ItemStack item = tran.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();

            if (tran.isBuyer(viewer)) {
                lore.addAll(Config.getStringList(configKey + "bought-text",
                        "seller", tran.getSellerName(),
                        "price", NumberUtil.formatLong(tran.getMoneySpent())));
            } else {
                lore.addAll(Config.getStringList(configKey + "sold-text",
                        "buyer", tran.getBuyerName(),
                        "money", NumberUtil.formatLong(tran.getMoneyEarned())));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);

            group.addElement(new StaticGuiElement('c', item));
        }
        addElement(group);
        this.maxPages = Math.max(1, ((group.size() - 1) / (Config.get().getInt(configKey + "rows-per-page") * 9)) + 1);

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

    private static String[] buildGui() {
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
