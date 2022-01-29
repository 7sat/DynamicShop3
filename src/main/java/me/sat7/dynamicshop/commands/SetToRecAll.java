package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.entity.Player;

public final class SetToRecAll {
    private SetToRecAll() {

    }

    static void setToRecAll(String[] args, Player player) {
        if(!player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
            return;
        }

        ShopUtil.SetToRecommendedValueAll(args[1], player);
        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));

        ShopUtil.ccShop.save();
    }
}