package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.models.DSItem;
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
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(5);
        validArgCount.add(7);
        validArgCount.add(9);
        validArgCount.add(10);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "edit"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... edit <item> <value> <median> <stock>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... edit <item> <value> <min value> <max value> <median> <stock> [<max stock>]");
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

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        int idx;
        double buyValue;
        double valueMin = 0.01;
        double valueMax = -1;
        int median;
        int stock;
        int maxStock;

        try
        {
            String[] temp = args[3].split("/");
            idx = Integer.parseInt(temp[0]);
            if (!shopData.get().contains(temp[0]))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_ITEM_NAME"));
                return;
            }
            buyValue = Double.parseDouble(args[4]);

            // 삭제
            if (buyValue <= 0)
            {
                ShopUtil.removeItemFromShop(shopName, idx);
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_DELETED"));
                return;
            } else
            {
                if (args.length == 7)
                {
                    median = Integer.parseInt(args[5]);
                    stock = Integer.parseInt(args[6]);
                    maxStock = shopData.get().getInt(idx + ".maxStock", -1);
                } else
                {
                    valueMin = Integer.parseInt(args[5]);
                    valueMax = Integer.parseInt(args[6]);
                    median = Integer.parseInt(args[7]);
                    stock = Integer.parseInt(args[8]);

                    if (args.length == 10)
                        maxStock = Integer.parseInt(args[9]);
                    else
                        maxStock = shopData.get().getInt(idx + ".maxStock", -1);

                    // 유효성 검사
                    if (valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.MAX_LOWER_THAN_MIN"));
                        return;
                    }
                    if (valueMax > 0 && buyValue > valueMax)
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                        return;
                    }
                    if (valueMin > 0 && buyValue < valueMin)
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                        return;
                    }
                }
            }
        } catch (Exception e)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
            return;
        }

        // 수정
        int discount = shopData.get().getInt(idx + ".discount");
        int sellLimit = shopData.get().getInt(idx + ".tradeLimitPerPlayer.sell");
        int buyLimit = shopData.get().getInt(idx + ".tradeLimitPerPlayer.buy");
        long tradeLimitInterval = shopData.get().getLong(idx + ".tradeLimitPerPlayer.interval");
        long tradeLimitNextTimer = shopData.get().getLong(idx + ".tradeLimitPerPlayer.nextTimer");

        DSItem temp = new DSItem(null, buyValue, buyValue, valueMin, valueMax, median, stock, maxStock, discount,
                                 sellLimit, buyLimit, tradeLimitInterval, tradeLimitNextTimer);
        ShopUtil.editShopItem(shopName, idx, temp);
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_UPDATED"));
        ItemsUtil.sendItemInfo(sender, shopName, idx, "HELP.ITEM_INFO");
    }
}
