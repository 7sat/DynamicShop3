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

public class Permission extends DSCMD
{
    public Permission()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(3);
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "permission"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> permission [<true | false | custom >]");

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

        if (args.length == 3)
        {
            String s = shopData.get().getConfigurationSection("Options").getString("permission");
            if (s == null || s.length() == 0) s = t(player, "NULL(OPEN)");
            player.sendMessage(DynamicShop.dsPrefix(player) + s);
        } else if (args.length >= 4)
        {
            if (args[3].equalsIgnoreCase("true"))
            {
                shopData.get().set("Options.permission", "dshop.user.shop." + args[1]);
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CHANGES_APPLIED") + "dshop.user.shop." + args[1]);
            } else if (args[3].equalsIgnoreCase("false"))
            {
                shopData.get().set("Options.permission", "");
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CHANGES_APPLIED") + t(player, "NULL(OPEN)"));
            } else
            {
                shopData.get().set("Options.permission", args[3]);
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CHANGES_APPLIED") + args[3]);
            }
            shopData.save();
        }
    }
}
