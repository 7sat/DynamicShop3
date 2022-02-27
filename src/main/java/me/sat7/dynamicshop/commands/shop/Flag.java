package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Flag extends DSCMD
{
    public Flag()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(5);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "flag"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> flag <flag> <set | unset>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, Player player)
    {
        if(!CheckValid(args, player))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        boolean set;
        if (args[4].equalsIgnoreCase("set"))
        {
            set = true;
        } else if (args[4].equalsIgnoreCase("unset"))
        {
            set = false;
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
            return;
        }

        if (args[3].equalsIgnoreCase("signshop") ||
                args[3].equalsIgnoreCase("localshop") ||
                args[3].equalsIgnoreCase("deliverycharge") ||
                args[3].equalsIgnoreCase("jobpoint") ||
                args[3].equalsIgnoreCase("showValueChange") ||
                args[3].equalsIgnoreCase("hidestock") ||
                args[3].equalsIgnoreCase("hidepricingtype") ||
                args[3].equalsIgnoreCase("hideshopbalance") ||
                args[3].equalsIgnoreCase("showmaxstock") ||
                args[3].equalsIgnoreCase("hiddenincommand")
        )
        {
            if (set)
            {
                if (args[3].equalsIgnoreCase("signshop"))
                {
                    shopData.get().set("Options.flag.localshop", null);
                    shopData.get().set("Options.flag.deliverycharge", null);
                }
                if (args[3].equalsIgnoreCase("localshop"))
                {
                    shopData.get().set("Options.flag.signshop", null);

                    if(!shopData.get().contains("Options.pos1") || !shopData.get().contains("Options.pos2") || !shopData.get().contains("Options.world"))
                    {
                        shopData.get().set("Options.pos1", (player.getLocation().getBlockX() - 2) + "_" + (player.getLocation().getBlockY() - 1) + "_" + (player.getLocation().getBlockZ() - 2));
                        shopData.get().set("Options.pos2", (player.getLocation().getBlockX() + 2) + "_" + (player.getLocation().getBlockY() + 1) + "_" + (player.getLocation().getBlockZ() + 2));
                        shopData.get().set("Options.world", player.getWorld().getName());
                    }
                }
                if (args[3].equalsIgnoreCase("deliverycharge"))
                {
                    shopData.get().set("Options.flag.signshop", null);

                    shopData.get().set("Options.flag.localshop", "");
                    if(!shopData.get().contains("Options.pos1") || !shopData.get().contains("Options.pos2") || !shopData.get().contains("Options.world"))
                    {
                        shopData.get().set("Options.pos1", (player.getLocation().getBlockX() - 2) + "_" + (player.getLocation().getBlockY() - 1) + "_" + (player.getLocation().getBlockZ() - 2));
                        shopData.get().set("Options.pos2", (player.getLocation().getBlockX() + 2) + "_" + (player.getLocation().getBlockY() + 1) + "_" + (player.getLocation().getBlockZ() + 2));
                        shopData.get().set("Options.world", player.getWorld().getName());
                    }
                }

                shopData.get().set("Options.flag." + args[3].toLowerCase(), "");
            } else
            {
                if (args[3].equalsIgnoreCase("localshop"))
                {
                    shopData.get().set("Options.flag.deliverycharge", null);
                    shopData.get().set("Options.pos1", null);
                    shopData.get().set("Options.pos2", null);
                    shopData.get().set("Options.world", null);
                }
                shopData.get().set("Options.flag." + args[3].toLowerCase(), null);
            }
            shopData.save();
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + args[3] + " " + args[4]);
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
        }
    }
}
