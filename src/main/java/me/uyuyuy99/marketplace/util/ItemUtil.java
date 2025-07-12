package me.uyuyuy99.marketplace.util;

import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemUtil {

    // Serialize an item to a string for DB storage
    @SneakyThrows
    public static String serializeItem(ItemStack item) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BukkitObjectOutputStream output = new BukkitObjectOutputStream(stream);
        output.writeObject(item);
        output.close();
        return Base64Coder.encodeLines(stream.toByteArray());
    }

    // Retrieve an ItemStack object from a serialized string
    @SneakyThrows
    public static ItemStack deserializeItem(String serialized) {
        ByteArrayInputStream stream = new ByteArrayInputStream(Base64Coder.decodeLines(serialized));
        BukkitObjectInputStream input = new BukkitObjectInputStream(stream);
        ItemStack item = (ItemStack) input.readObject();
        input.close();
        return item;
    }

    // Gets the metadata name of the item, or a default name if there is none
    public static String getDisplayName(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }

        // Get default name from Material enum
        String[] toks = item.getType().name().split("_");
        for (int i = 0; i < toks.length; i += 1) {
            toks[i] = toks[i].substring(0, 1).toUpperCase() + toks[i].substring(1).toLowerCase();
        }
        return String.join(" ", toks);
    }

}
