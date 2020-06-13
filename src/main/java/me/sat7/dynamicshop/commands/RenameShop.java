package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public final class RenameShop {
    private RenameShop() {

    }

    static boolean renameShop(String[] args, Player player) {
        if(args.length >= 3)
        {
            if(!player.hasPermission("dshop.admin.renameshop"))
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                return true;
            }

            try
            {
                if(ShopUtil.ccShop.get().contains(args[1]))
                {
                    String newName = args[2].replace("/","");
                    ShopUtil.renameShop(args[1],newName);
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newName);
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
