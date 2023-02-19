package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public class Fluctuation extends DSCMD
{
    public Fluctuation()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
        validArgCount.add(5);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "fluctuation"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... fluctuation <interval> <strength>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... fluctuation off");

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
            if (args[3].equals("off"))
            {
                shopData.get().set("Options.fluctuation", null);
                shopData.save();
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + "Fluctuation Off");
            } else
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
            }
        } else if (args.length >= 5)
        {
            int interval;
            try
            {
                interval = Clamp(Integer.parseInt(args[3]), 1, 999);
            } catch (Exception e)
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }

            try
            {
                double strength = Double.parseDouble(args[4]);
                shopData.get().set("Options.fluctuation.interval", interval);
                shopData.get().set("Options.fluctuation.strength", strength);
                shopData.save();

                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
            } catch (Exception e)
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
            }
        }
    }
}
