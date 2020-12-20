package net.llamadevelopment.bansystemdap.components.provider;

import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystemdap.BanSystemDAP;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class YamlProvider extends Provider{

    private Config data;

    @Override
    public void connect(BanSystemDAP instance) {
        instance.saveResource("/data/data.yml");
        this.data = new Config(instance.getDataFolder() + "/data/data.yml", Config.YAML);
    }

    @Override
    public void updatePlayerIp(String player, InetSocketAddress socketAddress) {
        this.data.set("data." + player, this.hash(socketAddress.getAddress().getHostAddress()));
        this.data.save();
        this.data.reload();
    }

    @Override
    public void getDuplicateAccounts(String player, Consumer<Set<String>> accounts) {
        Set<String> set = new HashSet<>();
        String ip = this.data.getString("data." + player);
        for (String s : this.data.getSection("data").getAll().getKeys(false)) {
            if (this.data.getString("data." + s).equals(ip)) {
                set.add(s);
            }
        }
        accounts.accept(set);
    }

    @Override
    public void hasEntry(String player, Consumer<Boolean> has) {
        has.accept(this.data.exists("data." + player));
    }

    @Override
    public String getProvider() {
        return "Yaml";
    }

}
