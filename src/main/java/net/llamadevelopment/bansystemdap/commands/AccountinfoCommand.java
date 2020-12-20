package net.llamadevelopment.bansystemdap.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystemdap.BanSystemDAP;
import net.llamadevelopment.bansystemdap.components.language.Language;

public class AccountinfoCommand extends PluginCommand<BanSystemDAP> {

    public AccountinfoCommand(BanSystemDAP owner) {
        super(owner.getConfig().getString("Commands.Accountinfo.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Accountinfo.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Accountinfo.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Accountinfo.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(this.getPermission())) {
                if (args.length == 1) {
                    String target = args[0];
                    this.getPlugin().provider.hasEntry(target, has -> {
                        if (has) {
                            BanSystemDAP.getApi().getFormWindows().openAccountInfo(player, target);
                        } else player.sendMessage(Language.get("no-entries-found"));
                    });
                } else player.sendMessage(Language.get("usage-accountinfo", this.getName()));
            } else player.sendMessage(Language.get("no-permission"));
        }
        return true;
    }

}
