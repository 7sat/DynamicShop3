package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SET_DEFAULT_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class SetDefaultShop extends DSCMD
{
    public SetDefaultShop()
    {
        permission = P_ADMIN_SET_DEFAULT_SHOP;
        validArgCount.add(2);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "setdefaultshop"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds setdefaultshop <shop name>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, Player player)
    {
        if(!CheckValid(args, player))
            return;

        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            DynamicShop.plugin.getConfig().set("Command.DefaultShopName", args[1]);
            DynamicShop.plugin.saveConfig();
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + args[1]);
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
        }
    }
}
