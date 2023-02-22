package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Command extends DSCMD
{
    public Command()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "command"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... command <sell | buy> add <index> <command>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... command <sell | buy> delete <index>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... command active <true | false>");

        player.sendMessage("");
    }

    public static void PrintCurrentState(CommandSender sender, String shopName, boolean showSell, boolean showBuy)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        if (data.get().contains("Options.command.active") && data.get().getBoolean("Options.command.active"))
        {
            if (showSell && data.get().contains("Options.command.sell"))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender,"MESSAGE.SELL_COMMAND_CUR"));
                if (data.get().getConfigurationSection("Options.command.sell") != null)
                {
                    for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.sell").getValues(false).entrySet())
                    {
                        sender.sendMessage(" " + s.getKey() + "/" + s.getValue());
                    }

                    sender.sendMessage("Placeholders: {player} {shop} {itemType} {amount} {priceSum} {tax}");
                }
            }
            if (showBuy && data.get().contains("Options.command.buy"))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender,"MESSAGE.BUY_COMMAND_CUR"));
                if (data.get().getConfigurationSection("Options.command.buy") != null)
                {
                    for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.buy").getValues(false).entrySet())
                    {
                        sender.sendMessage(" " + s.getKey() + "/" + s.getValue());
                    }

                    sender.sendMessage("Placeholders: {player} {shop} {itemType} {amount} {priceSum}");
                }
            }
        }

        sender.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        if (args.length >= 5)
        {
            if (args[3].equalsIgnoreCase("active") && args.length == 5)
            {
                boolean setActive = args[4].equalsIgnoreCase("true");
                shopData.get().set("Options.command.active", setActive);
                sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.COMMAND_TOGGLE") + ": " + args[4]);
            }
            else if (args[3].equalsIgnoreCase("sell"))
            {
                subCmd(sender, args, shopData, "sell");
            }
            else if (args[3].equalsIgnoreCase("buy"))
            {
                subCmd(sender, args, shopData, "buy");
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

    private void subCmd(CommandSender sender, String[] args, CustomConfig shopData, String sellBuyString)
    {
        if(args[4].equalsIgnoreCase("delete") && args.length == 6)
        {
            int idx = 0;
            try
            {
                idx = Integer.parseInt(args[5]);
            }catch(Exception ignore)
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }

            shopData.get().set("Options.command." + sellBuyString + "." + idx, null);
            ShopUtil.CleanupCommandIndex(Shop.GetShopName(args), sellBuyString);
            PrintCurrentState(sender, Shop.GetShopName(args), true, true);
        }
        else if(args[4].equalsIgnoreCase("add") && args.length >= 7)
        {
            int idx = 0;
            try
            {
                idx = Integer.parseInt(args[5]);
            }catch(Exception ignore)
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }

            StringBuilder cmdString = new StringBuilder();
            for (int i = 6; i < args.length; i++)
            {
                if (i == 6 && args[i].startsWith("/"))
                {
                    args[6] = args[6].replace("/","");
                }

                cmdString.append(args[i]);
                cmdString.append(" ");
            }

            shopData.get().set("Options.command." + sellBuyString + "." + idx, cmdString.toString());
            ShopUtil.CleanupCommandIndex(Shop.GetShopName(args), sellBuyString);
            PrintCurrentState(sender, Shop.GetShopName(args), true, true);
        }
        else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
        }
    }
}
