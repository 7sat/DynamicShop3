package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_CREATE_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class CreateShop extends DSCMD
{
    public CreateShop()
    {
        permission = P_ADMIN_CREATE_SHOP;
        validArgCount.add(2);
        validArgCount.add(3);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "createshop"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds create <shopname> [<permission>]");
        player.sendMessage(" - " + t("HELP.CREATE_SHOP_2"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, Player player)
    {
        if(!CheckValid(args, player))
            return;

        String shopname = args[1].replace("/", "");

        CustomConfig data = new CustomConfig();
        data.setup(shopname, "Shop");
        if (!ShopUtil.shopConfigFiles.containsKey(shopname))
        {
            data.get().set("Options.title", shopname);
            data.get().set("Options.enable", false);
            data.get().set("Options.lore", "");
            data.get().set("Options.page", 2);
            if (args.length >= 3)
            {
                if (args[2].equalsIgnoreCase("true"))
                {
                    data.get().set("Options.permission", "dshop.user.shop." + shopname);
                } else if (args[2].equalsIgnoreCase("false"))
                {
                    data.get().set("Options.permission", "");
                } else
                {
                    data.get().set("Options.permission", args[2]);
                }
            } else
            {
                data.get().set("Options.permission", "");
            }

            data.get().set("0.mat", "DIRT");
            data.get().set("0.value", 1);
            data.get().set("0.median", 10000);
            data.get().set("0.stock", 10000);
            data.save();

            ShopUtil.shopConfigFiles.put(shopname, data);

            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.SHOP_CREATED"));
            DynaShopAPI.openShopGui(player, shopname, 1);
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_EXIST"));
        }
    }
}
