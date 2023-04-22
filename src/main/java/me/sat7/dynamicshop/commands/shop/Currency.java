package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Currency extends DSCMD
{
    public Currency()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "currency"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... currency <currency>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        if (args[3].equalsIgnoreCase(Constants.S_VAULT) ||
            args[3].equalsIgnoreCase(Constants.S_EXP) ||
            args[3].equalsIgnoreCase(Constants.S_PLAYERPOINT) ||
            args[3].equalsIgnoreCase(Constants.S_JOBPOINT))
        {
            shopData.get().set("Options.currency", args[3].toLowerCase());
            shopData.save();

            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[2] + " " + args[3]);
        } else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
        }
    }
}
