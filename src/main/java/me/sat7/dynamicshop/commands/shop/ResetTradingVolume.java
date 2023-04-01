package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import me.sat7.dynamicshop.utilities.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class ResetTradingVolume extends DSCMD
{
    public ResetTradingVolume()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(3);
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "resetTradingVolume"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... resetTradingVolume [<player>]");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);

        if (args.length == 3)
        {
            UserUtil.ClearTradeLimitData(shopName);

            sender.sendMessage(t(sender, "MESSAGE.CHANGES_APPLIED_2"));
        }
        else
        {
            OfflinePlayer op = Bukkit.getOfflinePlayer(args[3]);

            UserUtil.ClearTradeLimitData(op.getUniqueId(), shopName);

            sender.sendMessage(t(sender, "MESSAGE.CHANGES_APPLIED_2"));
        }
    }
}
