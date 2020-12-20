package net.llamadevelopment.bansystemdap.components.provider;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.llamadevelopment.bansystemdap.BanSystemDAP;
import org.bson.Document;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongodbProvider extends Provider{

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> ipsCollection;

    @Override
    public void connect(BanSystemDAP instance) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(instance.getConfig().getString("MongoDB.Uri"));
            this.mongoClient = new MongoClient(uri);
            this.mongoDatabase = this.mongoClient.getDatabase(instance.getConfig().getString("MongoDB.Database"));
            this.ipsCollection = this.mongoDatabase.getCollection("ips");
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.OFF);
            instance.getLogger().info("[MongoClient] Connection opened.");
        });
    }

    @Override
    public void disconnect(BanSystemDAP instance) {
        this.mongoClient.close();
        instance.getLogger().info("[MongoClient] Connection closed.");
    }

    @Override
    public void updatePlayerIp(String player, InetSocketAddress socketAddress) {
        CompletableFuture.runAsync(() -> {
            Document document = this.ipsCollection.find(new Document("player", player)).first();
            if (document != null) {
                this.ipsCollection.updateOne(Objects.requireNonNull(this.ipsCollection.find(new Document("player", player)).first()), new Document("$set", new Document("ip", this.hash(socketAddress.getAddress().getHostAddress()))));
            } else {
                Document insert = new Document("player", player).append("ip", this.hash(socketAddress.getAddress().getHostAddress()));
                this.ipsCollection.insertOne(insert);
            }
        });
    }

    @Override
    public void getDuplicateAccounts(String player, Consumer<Set<String>> accounts) {
        CompletableFuture.runAsync(() -> {
           Set<String> set = new HashSet<>();

           Document self = this.ipsCollection.find(new Document("player", player)).first();
           assert self != null;
           String ip = self.getString("ip");

           this.ipsCollection.find(new Document("ip", ip)).forEach((Block<? super Document>) document -> {
               set.add(document.getString("player"));
           });

           accounts.accept(set);
        });
    }

    @Override
    public void hasEntry(String player, Consumer<Boolean> has) {
        CompletableFuture.runAsync(() -> {
            Document document = this.ipsCollection.find(new Document("player", player)).first();
            has.accept(document != null);
        });
    }

    @Override
    public String getProvider() {
        return "MongoDB";
    }

}
