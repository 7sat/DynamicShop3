package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.utilities.ConfigUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SET_DEFAULT_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class SetDefaultShop extends DSCMD
{
    public SetDefaultShop()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SET_DEFAULT_SHOP;
        validArgCount.add(2);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "setdefaultshop"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds setdefaultshop <shop name>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            ConfigUtil.SetDefaultShopName(args[1]);
            ConfigUtil.Save();

            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[1]);
        } else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"));
        }
    }
}
