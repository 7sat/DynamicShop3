package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class OpenShop {
    public static boolean openShop(String[] args, CommandSender sender) {
        Player target = null;
        String shopName = null;
        if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                if (DynamicShop.plugin.getConfig().getBoolean("OpenStartPageInsteadOfDefaultShop")) {
                    DynamicShop.ccUser.get().set(target.getUniqueId() + ".interactItem", "");
                    DynaShopAPI.openStartPage(target);
                    return false;
                }
            }
            shopName = DynamicShop.plugin.getConfig().getString("DefaultShopName");
        } else if (args.length > 2) {
            if (!sender.hasPermission("dshop.admin.openshop")) {
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                return true;
            }

            if (ShopUtil.ccShop.get().contains(args[1])) {
                shopName = args[1];
            } else {
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
                return true;
            }

            target = Bukkit.getPlayer(args[2]);
        } else {
            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
        }

        if (target != null) {
            ConfigurationSection shopConf = ShopUtil.ccShop.get().getConfigurationSection(shopName + ".Options");
            if (shopConf.contains("shophours") && !target.hasPermission("dshop.admin.shopedit")) {
                int curTime = (int) (target.getWorld().getTime()) / 1000 + 6;
                if (curTime > 24) curTime -= 24;

                String[] temp = shopConf.getString("shophours").split("~");

                int open = Integer.parseInt(temp[0]);
                int close = Integer.parseInt(temp[1]);

                if (close > open) {
                    if (!(open <= curTime && curTime < close)) {
                        target.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return true;
                    }
                } else {
                    if (!(open <= curTime || curTime < close)) {
                        target.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return true;
                    }
                }
            }


            DynamicShop.ccUser.get().set(target.getUniqueId() + ".tmpString", "");
            DynamicShop.ccUser.get().set(target.getUniqueId() + ".interactItem", "");
            DynaShopAPI.openShopGui(target, shopName, 1);
        }

        return false;
    }
}
