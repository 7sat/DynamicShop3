package me.sat7.dynamicshop.commands;

import org.bukkit.configuration.file.FileConfiguration;
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
        FileConfiguration config = DynamicShop.ccUser.get(player);
        if(args[1].equalsIgnoreCase("on"))
        {
            player.sendMessage(DynamicShop.dsPrefix + "켜짐");
            config.set("tmpString","");
            config.set("cmdHelp",true);
            DynamicShop.ccUser.save(player);
        }
        else if(args[1].equalsIgnoreCase("off"))
        {
            player.sendMessage(DynamicShop.dsPrefix + "꺼짐");
            config.set("tmpString","");
            config.set("cmdHelp",false);
            DynamicShop.ccUser.save(player);
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
