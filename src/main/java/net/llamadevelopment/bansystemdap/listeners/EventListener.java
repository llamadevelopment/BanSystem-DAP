package net.llamadevelopment.bansystemdap.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import lombok.AllArgsConstructor;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.provider.Provider;
import net.llamadevelopment.bansystemdap.BanSystemDAP;

import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
public class EventListener implements Listener {

    private final BanSystemDAP instance;

    @EventHandler
    public void on(final PlayerPreLoginEvent event) {
        final Player player = event.getPlayer();
        this.instance.provider.updatePlayerIp(player.getName(), player.getSocketAddress());
        final Provider api = BanSystem.getApi().getProvider();
        api.playerIsBanned(event.getPlayer().getName(), (been) -> {
            if (!been) {
                this.instance.provider.getDuplicateAccounts(player.getName(), accounts -> {
                    AtomicBoolean gotBanned = new AtomicBoolean(false);
                    accounts.forEach(e -> api.playerIsBanned(e, is -> {
                        if (gotBanned.get()) return;
                        if (is) {
                            api.getBan(e, ban -> {
                                int seconds = ban.getTime() == -1 ? -1 : (int) ((ban.getTime() - System.currentTimeMillis()) / 1000);
                                api.banPlayer(player.getName(), ban.getReason(), ban.getBanner(), seconds);
                                gotBanned.set(true);
                            });
                        }
                    }));
                });
            }
        });
    }

}

