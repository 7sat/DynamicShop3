package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class SetToRecAll
{
    private SetToRecAll()
    {

    }

    static void setToRecAll(String[] args, Player player)
    {
        if (!player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
            return;
        }

        ShopUtil.SetToRecommendedValueAll(args[1], player);
        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_UPDATED"));
    }
}