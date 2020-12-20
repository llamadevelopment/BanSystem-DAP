package net.llamadevelopment.bansystemdap.components.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import lombok.AllArgsConstructor;
import net.llamadevelopment.bansystemdap.components.forms.simple.SimpleForm;
import net.llamadevelopment.bansystemdap.components.language.Language;
import net.llamadevelopment.bansystemdap.components.provider.Provider;

import java.util.Set;

@AllArgsConstructor
public class FormWindows {

    private final Provider provider;

    public void openAccountInfo(Player player, String target) {
        this.provider.getDuplicateAccounts(target, accounts -> {
            Player t = Server.getInstance().getPlayer(target);
            String content = t == null ?
                    Language.getNP("ui-accountinfo-content-offline", accounts.size() - 1) :
                    Language.getNP("ui-accountinfo-content-online", accounts.size() - 1, this.provider.getDevice(t.getLoginChainData().getDeviceOS()), this.provider.getInput(t.getLoginChainData().getCurrentInputMode()));
            SimpleForm.Builder formBuilder = new SimpleForm.Builder(Language.getNP("ui-accountinfo-title", target), content);
            formBuilder.addButton(new ElementButton(Language.getNP("ui-accountinfo-accounts")), e -> this.openAccountList(player, target, accounts));
            formBuilder.build().send(player);
        });
    }

    public void openAccountList(Player player, String target, Set<String> accounts) {
        SimpleForm.Builder formBuilder = new SimpleForm.Builder(Language.getNP("ui-accountlist-title", target), Language.getNP("ui-accountlist-content", target));
        accounts.forEach(e -> formBuilder.addButton(new ElementButton(Language.getNP("ui-accountlist-entry", e)), f -> this.openAccountInfo(player, e)));
        formBuilder.build().send(player);
    }

}
