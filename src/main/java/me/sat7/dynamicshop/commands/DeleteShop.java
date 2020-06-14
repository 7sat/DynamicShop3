package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public final class DeleteShop {
    private DeleteShop() {

    }

    static boolean deleteShop(String[] args, Player player) {
        if(args.length >= 2)
        {
            if(!player.hasPermission("dshop.admin.deleteshop"))
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                return true;
            }

            try
            {
                if(ShopUtil.ccShop.get().contains(args[1]))
                {
                    ShopUtil.ccShop.get().set(args[1],null);
                    ShopUtil.ccShop.save();
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("SHOP_DELETED"));
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
                }
            }
            catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
            }
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
