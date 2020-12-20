package net.llamadevelopment.bansystemdap;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import net.llamadevelopment.bansystemdap.commands.AccountinfoCommand;
import net.llamadevelopment.bansystemdap.components.api.API;
import net.llamadevelopment.bansystemdap.components.forms.FormListener;
import net.llamadevelopment.bansystemdap.components.forms.FormWindows;
import net.llamadevelopment.bansystemdap.components.language.Language;
import net.llamadevelopment.bansystemdap.components.provider.MongodbProvider;
import net.llamadevelopment.bansystemdap.components.provider.MySqlProvider;
import net.llamadevelopment.bansystemdap.components.provider.Provider;
import net.llamadevelopment.bansystemdap.components.provider.YamlProvider;
import net.llamadevelopment.bansystemdap.listeners.EventListener;

import java.util.HashMap;
import java.util.Map;

public class BanSystemDAP extends PluginBase {

    private final Map<String, Provider> providers = new HashMap<>();
    public Provider provider;

    @Getter
    private static API api;

    @Override
    public void onEnable() {
        try {
            this.saveDefaultConfig();
            this.providers.put("MongoDB", new MongodbProvider());
            this.providers.put("MySql", new MySqlProvider());
            this.providers.put("Yaml", new YamlProvider());
            if (!this.providers.containsKey(this.getConfig().getString("Provider"))) {
                this.getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                return;
            }
            this.provider = this.providers.get(this.getConfig().getString("Provider"));
            this.provider.connect(this);
            this.getLogger().info("§aSuccessfully loaded " + this.provider.getProvider() + " provider.");
            api = new API(this.provider, new FormWindows(this.provider));
            Language.init(this);
            this.loadPlugin();
            this.getLogger().info("§aBanSystemDAP successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load BanSystemDAP.");
        }
    }

    private void loadPlugin() {
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        this.getServer().getCommandMap().register("bansystem", new AccountinfoCommand(this));
    }

    @Override
    public void onDisable() {
        this.provider.disconnect(this);
    }
}
