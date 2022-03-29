package me.sat7.dynamicshop.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_RENAME_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class RenameShop extends DSCMD
{
    public RenameShop()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_RENAME_SHOP;
        validArgCount.add(3);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "renameshop"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds renameshop <old name> <new name>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            String newName = args[2].replace("/", "");
            ShopUtil.renameShop(args[1], newName);
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + newName);
        } else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"));
        }
    }
}
