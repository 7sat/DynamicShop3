package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_OPEN_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class OpenShop extends DSCMD
{
    public OpenShop()
    {
        permission = P_ADMIN_OPEN_SHOP;
        validArgCount.add(2);
        validArgCount.add(3);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "openshop"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds openshop [shopname] <playername>");
        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, Player player)
    {
        if(!CheckValid(args, player))
            return;

        Player target = null;
        String shopName;
        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            shopName = args[1];
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
            return;
        }

        if (args.length == 2)
        {
            target = player;
        }
        if (args.length > 2)
        {
            target = Bukkit.getPlayer(args[2]);
        }

        if (target != null)
        {
            DynaShopAPI.openShopGui(target, shopName, 1);
        }
    }
}
