package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.LogUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Log extends DSCMD
{
    public Log()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "log"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shop name> log < enable | disable | clear >");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        Player player = (Player) sender;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        if (args[3].equalsIgnoreCase("enable"))
        {
            shopData.get().set("Options.log", true);
            player.sendMessage(DynamicShop.dsPrefix + shopName + "/" + t("LOG.LOG") + ": " + args[3]);
        } else if (args[3].equalsIgnoreCase("disable"))
        {
            shopData.get().set("Options.log", null);
            player.sendMessage(DynamicShop.dsPrefix + shopName + "/" + t("LOG.LOG") + ": " + args[3]);
        } else if (args[3].equalsIgnoreCase("clear"))
        {
            LogUtil.ccLog.get().set(shopName, null);
            LogUtil.ccLog.save();
            player.sendMessage(DynamicShop.dsPrefix + shopName + "/" + LangUtil.ccLang.get().getString("LOG.CLEAR"));
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
            return;
        }

        shopData.save();
    }
}
