package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;

public final class SetTax {
    private SetTax() {

    }

    static boolean setTax(String[] args, Player player) {
        if(!player.hasPermission("dshop.admin.settax"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
            return true;
        }

        if(args.length == 2)
        {
            try
            {
                int newValue = Integer.parseInt(args[1]);
                if(newValue <= 2) newValue = 2;
                if(newValue > 99) newValue = 99;

                DynamicShop.plugin.getConfig().set("SalesTax",newValue);
                DynamicShop.plugin.saveConfig();

                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
                return true;
            }
            catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
            }
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
