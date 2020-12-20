package net.llamadevelopment.bansystemdap.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import lombok.AllArgsConstructor;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.provider.Provider;
import net.llamadevelopment.bansystemdap.BanSystemDAP;

@AllArgsConstructor
public class EventListener implements Listener {

    private final BanSystemDAP instance;

    @EventHandler
    public void on(final PlayerPreLoginEvent event) {
        final Player player = event.getPlayer();
        this.instance.provider.updatePlayerIp(player.getName(), player.getSocketAddress());
    }

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Provider api = BanSystemAPI.getProvider();
        this.instance.provider.getDuplicateAccounts(player.getName(), accounts -> {
            if (accounts.size() == 0) return;
            accounts.forEach(e -> api.playerIsBanned(e, is -> {
                if (is) {
                    api.getBan(e, ban -> {
                        int seconds = (int) ((ban.getTime() - System.currentTimeMillis()) / 1000);
                        api.banPlayer(player.getName(), ban.getReason(), ban.getBanner(), seconds);
                    });
                }
            }));
        });
    }

}
