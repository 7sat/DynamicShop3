package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Background extends DSCMD
{
    public Background()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "background"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... background <color | clear>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (args[3].equalsIgnoreCase("clear"))
        {
            String shopName = Shop.GetShopName(args);
            CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
            shopData.get().set("Options.background", null);
            shopData.save();

            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[2] + " " + args[3]);

            return;
        }

        Material mat = Material.getMaterial(args[3].toUpperCase() + "_STAINED_GLASS_PANE");
        if (mat == null)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
            return;
        }

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        shopData.get().set("Options.background", args[3].toUpperCase());
        shopData.save();

        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[2] + " " + args[3]);
    }
}
