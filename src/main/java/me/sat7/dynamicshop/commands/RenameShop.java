package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_RENAME_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class RenameShop extends DSCMD
{
    public RenameShop()
    {
        permission = P_ADMIN_RENAME_SHOP;
        validArgCount.add(3);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "renameshop"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds renameshop <old name> <new name>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, Player player)
    {
        if(!CheckValid(args, player))
            return;

        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            String newName = args[2].replace("/", "");
            ShopUtil.renameShop(args[1], newName);
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + newName);
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
        }
    }
}
