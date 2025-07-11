package me.uyuyuy99.marketplace.util;

import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
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

}
