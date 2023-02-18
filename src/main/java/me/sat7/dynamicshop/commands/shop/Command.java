package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shop name> command <sell | buy> <command | clear>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shop name> command active <true | false>");

        player.sendMessage("");
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
            } else if (args[3].equalsIgnoreCase("sell"))
            {
                if(args[4].equalsIgnoreCase("clear") && args.length == 5)
                {
                    shopData.get().set("Options.command.sell", null);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.COMMAND_SELL") + ": null");
                }
                else
                {
                    String cmdString = "";
                    for (int i = 4; i < args.length; i++)
                    {
                        if (i == 4 && args[i].startsWith("/"))
                        {
                            args[4] = args[4].replace("/","");
                        }

                        cmdString += args[i];
                        cmdString += " ";
                    }

                    shopData.get().set("Options.command.sell", cmdString);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.COMMAND_SELL") + ": ");
                    sender.sendMessage("/" + cmdString);
                }
            }
            else if (args[3].equalsIgnoreCase("buy"))
            {
                if(args[4].equalsIgnoreCase("clear") && args.length == 5)
                {
                    shopData.get().set("Options.command.buy", null);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.COMMAND_BUY") + ": null");
                }
                else
                {
                    String cmdString = "";
                    for (int i = 4; i < args.length; i++)
                    {
                        if (i == 4 && args[i].startsWith("/"))
                        {
                            args[4] = args[4].replace("/","");
                        }

                        cmdString += args[i];
                        cmdString += " ";
                    }

                    shopData.get().set("Options.command.buy", cmdString);
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + shopName + "/" + t(sender, "SHOP_SETTING.COMMAND_BUY") + ": ");
                    sender.sendMessage("/" + cmdString);
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
