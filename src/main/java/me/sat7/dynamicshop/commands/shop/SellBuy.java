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

public class SellBuy extends DSCMD
{
    public SellBuy()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "sellbuy"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shop name> sellbuy < sellonly | buyonly | clear >");

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

        String temp;
        if (args[3].equalsIgnoreCase("SellOnly"))
        {
            temp = "SellOnly";
        } else if (args[3].equalsIgnoreCase("BuyOnly"))
        {
            temp = "BuyOnly";
        } else
        {
            temp = "SellBuy";
        }

        for (String s : shopData.get().getKeys(false))
        {
            try
            {
                // i를 직접 사용하지는 않지만 의도적으로 넣은 코드임.
                int i = Integer.parseInt(s);
                if (!shopData.get().contains(s + ".value")) continue; //장식용임
            } catch (Exception e)
            {
                continue;
            }

            if (temp.equalsIgnoreCase("SellBuy"))
            {
                shopData.get().set(s + ".tradeType", null);
            } else
            {
                shopData.get().set(s + ".tradeType", temp);
            }
        }

        shopData.save();
        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + temp);
    }
}
