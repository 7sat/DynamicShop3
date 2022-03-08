package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class SetToRecAll extends DSCMD
{
    public SetToRecAll()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(2);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "SetToRecAll"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> SetToRecAll");
        player.sendMessage(" - " + t(player, "HELP.SET_TO_REC_ALL"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        Player player = (Player) sender;

        ShopUtil.SetToRecommendedValueAll(args[1], player);
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_UPDATED"));
    }
}