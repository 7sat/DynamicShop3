package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public final class Convert {
    private Convert() {

    }

    static boolean convert(String[] args, Player player) {
        if(!player.hasPermission("dshop.admin.convert"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
            return true;
        }

        if(args.length != 2)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
            return true;
        }

        if(!args[1].equals("Shop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
            return true;
        }

        ShopUtil.convertDataFromShop(player);
        return true;
    }
}
