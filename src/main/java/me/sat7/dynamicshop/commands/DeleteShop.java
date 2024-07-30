package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.RotationUtil;
import me.sat7.dynamicshop.utilities.UserUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_DELETE_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class DeleteShop extends DSCMD
{
    public DeleteShop()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_DELETE_SHOP;
        validArgCount.add(2);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "§c§ldeleteshop§f§r"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds deleteshop <shopname>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(args[1]);
            data.delete();

            ShopUtil.shopConfigFiles.remove(args[1]);
            ShopUtil.shopDirty.remove(args[1]);
            RotationUtil.OnShopDeleted(args[1]);
            UserUtil.ClearTradeLimitData(args[1]);

            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.SHOP_DELETED"));
        } else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"));
        }
    }
}
