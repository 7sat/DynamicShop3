package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class MaxPage extends DSCMD
{
    public MaxPage()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "maxpage"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> maxpage <number>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, Player player)
    {
        if(!CheckValid(args, player))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        int newValue;
        try
        {
            newValue = Integer.parseInt(args[3]);
        } catch (Exception e)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
            return;
        }

        if (newValue <= 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.VALUE_ZERO"));
        } else
        {
            shopData.get().set("Options.page", newValue);
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + args[3]);
            shopData.save();
        }
    }
}
