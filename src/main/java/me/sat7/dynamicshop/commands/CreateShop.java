package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public final class CreateShop {
    private CreateShop() {

    }

    static boolean createShop(String[] args, Player player) {
        if(args.length >= 2)
        {
            if(!player.hasPermission("dshop.admin.createshop"))
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                return true;
            }

            String shopname = args[1].replace("/","");

            if(ShopUtil.ccShop.get().contains(shopname))
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_EXIST"));
                return true;
            }

            ShopUtil.ccShop.get().set(shopname+".Options.title",shopname);
            ShopUtil.ccShop.get().set(shopname+".Options.lore","");
            ShopUtil.ccShop.get().set(shopname+".Options.page",2);
            if(args.length >= 3)
            {
                if(args[2].equalsIgnoreCase("true"))
                {
                    ShopUtil.ccShop.get().set(shopname+".Options.permission","dshop.user.shop."+shopname);
                }
                else if(args[2].equalsIgnoreCase("false"))
                {
                    ShopUtil.ccShop.get().set(shopname+".Options.permission","");
                }
                else
                {
                    ShopUtil.ccShop.get().set(shopname+".Options.permission",args[2]);
                }
            }
            else
            {
                ShopUtil.ccShop.get().set(shopname+".Options.permission","");
            }

            ShopUtil.ccShop.get().set(shopname+".0.mat","DIRT");
            ShopUtil.ccShop.get().set(shopname+".0.value",1);
            ShopUtil.ccShop.get().set(shopname+".0.median",10000);
            ShopUtil.ccShop.get().set(shopname+".0.stock",10000);
            ShopUtil.ccShop.save();
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("SHOP_CREATED"));
            DynaShopAPI.openShopGui(player,shopname,1);
            return true;
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
