package me.uyuyuy99.marketplace.storage;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import me.uyuyuy99.marketplace.MarketPlace;
import me.uyuyuy99.marketplace.listing.Listing;
import me.uyuyuy99.marketplace.util.ItemUtil;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDatabase {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private MongoClient client;
    private com.mongodb.client.MongoDatabase db;
    private MongoCollection<Document> counters;
    private MongoCollection<Document> itemTable;
    private MongoCollection<Document> historyTable;

    public MongoDatabase(String host, int port, String database, String username, String password) {
        super();
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    private int getNextItemId() {
        Document result = counters.findOneAndUpdate(
                Filters.eq("_id", "item_id"),
                Updates.inc("seq", 1),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );
        return result.getInteger("seq");
    }

    public void connect() {
        client = new MongoClient(host, port);
        db = client.getDatabase(database);
        counters = db.getCollection("counters");
        itemTable = db.getCollection("item_listings");
        historyTable = db.getCollection("history");

        counters.updateOne(
                Filters.eq("_id", "item_id"),
                Updates.setOnInsert("seq", 0),
                new UpdateOptions().upsert(true)
        );
    }

    public void disconnect() {
        client.close();
    }

    // Loads all the item listings from the database into the item listing manager
    public void loadItems() {
        int itemCount = 0;

        for (Document doc : itemTable.find()) {
            int id = doc.getInteger("_id");
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            String name = doc.getString("username");
            ItemStack item = ItemUtil.deserializeItem(doc.getString("item_data"));
            long price = doc.getLong("price");
            long timeListed = doc.getLong("time_start");

            MarketPlace.listings().getListings().add(new Listing(id, uuid, name, item, price, timeListed));
            itemCount++;
        }

        MarketPlace.get().getLogger().info("Successfully loaded " + itemCount + " item listings from MongoDB.");
    }

    // Adds listing to database, returns Listing
    public CompletableFuture<Listing> addItemListing(Player player, ItemStack item, long price) {
        return CompletableFuture.supplyAsync(() -> {
            int id = getNextItemId();
            Listing listing = new Listing(id, player.getUniqueId(), player.getName(), item, price, System.currentTimeMillis());

            itemTable.insertOne(new Document("_id", id)
                    .append("uuid", listing.getUuid().toString())
                    .append("username", listing.getUsername())
                    .append("item_data", ItemUtil.serializeItem(item))
                    .append("price", price)
                    .append("time_start", listing.getTimeListed())
            );
            return listing;
        });
    }

}
