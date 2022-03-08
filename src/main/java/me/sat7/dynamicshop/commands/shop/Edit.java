package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Edit extends DSCMD
{
    public Edit()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
        validArgCount.add(7);
        validArgCount.add(9);
        validArgCount.add(10);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "edit"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <median> <stock>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <min value> <max value> <median> <stock> [<max stock>]");
        player.sendMessage(" - " + t(player, "HELP.SHOP_EDIT"));
        player.sendMessage(" - " + t(player, "HELP.PRICE"));
        player.sendMessage(" - " + t(player, "HELP.INF_STATIC"));

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

        int idx;
        double buyValue;
        double valueMin = 0.01;
        double valueMax = -1;
        int median;
        int stock;
        int maxStock = -1;

        try
        {
            String[] temp = args[3].split("/");
            idx = Integer.parseInt(temp[0]);
            if (!shopData.get().contains(temp[0]))
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_ITEM_NAME"));
                return;
            }
            buyValue = Double.parseDouble(args[4]);

            // 삭제
            if (buyValue <= 0)
            {
                ShopUtil.removeItemFromShop(shopName, idx);
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_DELETED"));
                return;
            } else
            {
                if (args.length == 7)
                {
                    median = Integer.parseInt(args[5]);
                    stock = Integer.parseInt(args[6]);
                } else
                {
                    valueMin = Integer.parseInt(args[5]);
                    valueMax = Integer.parseInt(args[6]);
                    median = Integer.parseInt(args[7]);
                    stock = Integer.parseInt(args[8]);

                    if (args.length == 10)
                        maxStock = Integer.parseInt(args[9]);
                    if (maxStock < 1)
                        maxStock = -1;

                    // 유효성 검사
                    if (valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                    {
                        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.MAX_LOWER_THAN_MIN"));
                        return;
                    }
                    if (valueMax > 0 && buyValue > valueMax)
                    {
                        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                        return;
                    }
                    if (valueMin > 0 && buyValue < valueMin)
                    {
                        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                        return;
                    }
                }
            }
        } catch (Exception e)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_DATATYPE"));
            return;
        }

        // 수정
        ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock, maxStock);
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_UPDATED"));
        ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
    }
}
