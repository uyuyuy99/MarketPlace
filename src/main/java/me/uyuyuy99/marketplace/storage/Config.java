package me.uyuyuy99.marketplace.storage;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.util.CC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

public class Config {

    private static YamlDocument config;

    public static YamlDocument get() {
        return config;
    }

    public static void load() throws IOException {
        config = YamlDocument.create(
                new File(MarketPlace.get().getDataFolder(), "config.yml"),
                MarketPlace.get().getResource("config.yml"),
                LoaderSettings.builder().setAutoUpdate(true).build()
        );
    }

    public static void reload() throws IOException {
        config.reload();
    }

    // Gets a string from config with given replacement values
    public static String getString(String key, Object... args) {
        String msg = CC.translate(config.getString(key));

        for (int i = 0; i < args.length; i += 2) {
            msg = msg.replace("{" + args[i] + "}", args[i + 1].toString());
        }

        return msg;
    }

    // Gets a string list from config with given replacement values
    public static List<String> getStringList(String key, Object... args) {
        List<String> list = config.getStringList(key);

        ListIterator<String> iter = list.listIterator();
        while (iter.hasNext()) {
            String msg = CC.translate(iter.next());
            for (int i = 0; i < args.length; i += 2) {
                msg = msg.replace("{" + args[i] + "}", args[i + 1].toString());
            }
            iter.set(msg);
        }

        return list;
    }

    public static String[] getStringArray(String key, Object... args) {
        return getStringList(key, args).toArray(new String[]{});
    }

    public static String getMsg(String key, Object... args) {
        return getString("messages." + key, args);
    }

    // Recipient can be null; message simply won't be sent
    public static void sendMsg(String key, CommandSender recipient, Object... args) {
        if (recipient == null) return;
        recipient.sendMessage(getMsg(key, args));
    }

    public static ItemStack getIcon(String key) {
        return new ItemStack(Material.valueOf(MarketPlace.get().getConfig().getString(key).toUpperCase()), 1);
    }

}
