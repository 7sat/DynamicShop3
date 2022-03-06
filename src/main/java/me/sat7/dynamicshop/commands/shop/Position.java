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

public class Position extends DSCMD
{
    public Position()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "position"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> position <pos1 | pos2 | clear>");

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

        if (args[3].equalsIgnoreCase("pos1"))
        {
            shopData.get().set("Options.world", player.getWorld().getName());
            shopData.get().set("Options.pos1", player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
            shopData.save();
            player.sendMessage(DynamicShop.dsPrefix + "p1");
        } else if (args[3].equalsIgnoreCase("pos2"))
        {
            shopData.get().set("Options.world", player.getWorld().getName());
            shopData.get().set("Options.pos2", player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
            shopData.save();
            player.sendMessage(DynamicShop.dsPrefix + "p2");
        } else if (args[3].equalsIgnoreCase("clear"))
        {
            shopData.get().set("Options.world", null);
            shopData.get().set("Options.pos1", null);
            shopData.get().set("Options.pos2", null);
            shopData.save();
            player.sendMessage(DynamicShop.dsPrefix + "clear");
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
        }
    }
}
