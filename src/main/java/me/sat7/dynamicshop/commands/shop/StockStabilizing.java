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

public class StockStabilizing extends DSCMD
{
    public StockStabilizing()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
        validArgCount.add(5);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "stockStabilizing"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> stockStabilizing <interval> <strength>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> stockStabilizing off");

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

        if (args.length == 4)
        {
            if (args[3].equals("off"))
            {
                shopData.get().set("Options.stockStabilizing", null);
                shopData.save();
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CHANGES_APPLIED") + "stockStabilizing Off");
            } else
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_USAGE"));
            }
        } else if (args.length >= 5)
        {
            int interval;
            try
            {
                interval = Clamp(Integer.parseInt(args[3]), 1, 999);
            } catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_DATATYPE"));
                return;
            }

            try
            {
                double strength = Double.parseDouble(args[4]);
                shopData.get().set("Options.stockStabilizing.interval", interval);
                shopData.get().set("Options.stockStabilizing.strength", strength);
                shopData.save();

                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
            } catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_DATATYPE"));
            }
        }
    }
}
