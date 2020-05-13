package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;

public final class CommandHelp {
    private CommandHelp() {

    }

    static boolean commandHelp(String[] args, Player player) {
        if(args.length < 2)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
            return true;
        }

        if(args[1].equalsIgnoreCase("on"))
        {
            player.sendMessage(DynamicShop.dsPrefix + "켜짐");
            DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".cmdHelp",true);
            DynamicShop.ccUser.save();
        }
        else if(args[1].equalsIgnoreCase("off"))
        {
            player.sendMessage(DynamicShop.dsPrefix + "꺼짐");
            DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".cmdHelp",false);
            DynamicShop.ccUser.save();
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
