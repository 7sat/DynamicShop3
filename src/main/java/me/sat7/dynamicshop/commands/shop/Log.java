package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
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
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
        validArgCount.add(5);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "log"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shop name> log <enable | disable | clear>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shop name> log <printToConsole | printToAdmin> <on | off>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        if (args.length == 4)
        {
            if (args[3].equalsIgnoreCase("enable"))
            {
                shopData.get().set("Options.log.active", true);
                sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "LOG.LOG") + ": " + args[3]);
            } else if (args[3].equalsIgnoreCase("disable"))
            {
                shopData.get().set("Options.log.active", false);
                sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "LOG.LOG") + ": " + args[3]);
            } else if (args[3].equalsIgnoreCase("clear"))
            {
                LogUtil.DeleteShopLog(shopName);

                sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "LOG.CLEAR"));
            } else
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                return;
            }
        }
        else if(args.length == 5)
        {
            if (args[3].equalsIgnoreCase("printToConsole"))
            {
                if (args[4].equalsIgnoreCase("on"))
                {
                    shopData.get().set("Options.log.printToConsole", true);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.LOG_PRINT_CONSOLE") + ": " + args[4]);
                }
                else if(args[4].equalsIgnoreCase("off"))
                {
                    shopData.get().set("Options.log.printToConsole", false);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.LOG_PRINT_CONSOLE") + ": " + args[4]);
                }
                else
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                    return;
                }
            }
            else if (args[3].equalsIgnoreCase("printToAdmin"))
            {
                if (args[4].equalsIgnoreCase("on"))
                {
                    shopData.get().set("Options.log.printToAdmin", true);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.LOG_PRINT_ADMIN") + ": " + args[4]);
                }
                else if(args[4].equalsIgnoreCase("off"))
                {
                    shopData.get().set("Options.log.printToAdmin", false);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.LOG_PRINT_ADMIN") + ": " + args[4]);
                }
                else
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                    return;
                }
            }
            else
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                return;
            }
        }
        else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
            return;
        }

        shopData.save();
    }
}
