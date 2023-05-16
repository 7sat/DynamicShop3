package me.sat7.dynamicshop.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_COPY_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class CopyShop extends DSCMD
{
    public CopyShop()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_COPY_SHOP;
        validArgCount.add(3);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "copyshop"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds copyshop <name> <new name>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            if (args[1].equals(args[2]))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                return;
            }

            String newName = args[2].replace("/", "");
            ShopUtil.copyShop(args[1], newName);
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + newName);
        } else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"));
        }
    }
}
