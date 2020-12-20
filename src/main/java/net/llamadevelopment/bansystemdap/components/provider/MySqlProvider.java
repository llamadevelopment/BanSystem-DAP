package net.llamadevelopment.bansystemdap.components.provider;

import net.llamadevelopment.bansystemdap.BanSystemDAP;
import net.llamadevelopment.bansystemdap.components.simplesqlclient.MySqlClient;
import net.llamadevelopment.bansystemdap.components.simplesqlclient.objects.SqlColumn;
import net.llamadevelopment.bansystemdap.components.simplesqlclient.objects.SqlDocument;
import net.llamadevelopment.bansystemdap.components.simplesqlclient.objects.SqlDocumentSet;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MySqlProvider extends Provider {

    private MySqlClient client;

    @Override
    public void connect(BanSystemDAP instance) {
        CompletableFuture.runAsync(() -> {
            try {
                this.client = new MySqlClient(
                        instance.getConfig().getString("MySql.Host"),
                        instance.getConfig().getString("MySql.Port"),
                        instance.getConfig().getString("MySql.User"),
                        instance.getConfig().getString("MySql.Password"),
                        instance.getConfig().getString("MySql.Database")
                );

                this.client.createTable("ips",
                        new SqlColumn("player", SqlColumn.Type.VARCHAR, 64)
                                .append("ip", SqlColumn.Type.VARCHAR, 64));

                instance.getLogger().info("[MySqlClient] Connection opened.");
            } catch (Exception e) {
                e.printStackTrace();
                instance.getLogger().info("[MySqlClient] Failed to connect to database.");
            }
        });
    }

    @Override
    public void disconnect(BanSystemDAP instance) {
        instance.getLogger().info("[MySqlClient] Connection closed.");
    }

    @Override
    public void updatePlayerIp(String player, InetSocketAddress socketAddress) {
        CompletableFuture.runAsync(() -> {
            SqlDocument document = this.client.find("ips", "player", player).first();
            if (document != null) {
                this.client.update("ips", new SqlDocument("player", player), new SqlDocument("ip", this.hash(socketAddress.getAddress().getHostAddress())));
            } else {
                this.client.insert("ips", new SqlDocument("player", player).append("ip", this.hash(socketAddress.getAddress().getHostAddress())));
            }
        });
    }

    @Override
    public void getDuplicateAccounts(String player, Consumer<Set<String>> accounts) {
        CompletableFuture.runAsync(() -> {
            Set<String> set = new HashSet<>();

            SqlDocument document = this.client.find("ips", "player", player).first();
            String ip = document.getString("ip");

            SqlDocumentSet documentSet = this.client.find("ips", new SqlDocument("ip", ip));
            documentSet.getAll().forEach(sqlDocument -> set.add(sqlDocument.getString("player")));

            accounts.accept(set);
        });
    }

    @Override
    public void hasEntry(String player, Consumer<Boolean> has) {
        CompletableFuture.runAsync(() -> {
            SqlDocument document = this.client.find("ips", "player", player).first();
            has.accept(document != null);
        });
    }

    @Override
    public String getProvider() {
        return "MySql";
    }

}
