package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Add extends DSCMD
{
    public Add()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(7);
        validArgCount.add(9);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "add"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <median> <stock>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <min value> <max value> <median> <stock>");
        player.sendMessage(" - " + t(player, "HELP.SHOP_ADD_ITEM"));
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

        Material mat;
        double buyValue;
        double valueMin = 0.01;
        double valueMax = -1;
        int median;
        int stock;

        try
        {
            if (args.length == 7)
            {
                mat = Material.getMaterial(args[3].toUpperCase());
                buyValue = Double.parseDouble(args[4]);
                median = Integer.parseInt(args[5]);
                stock = Integer.parseInt(args[6]);
            } else
            {
                mat = Material.getMaterial(args[3].toUpperCase());
                buyValue = Double.parseDouble(args[4]);
                valueMin = Double.parseDouble(args[5]);
                valueMax = Double.parseDouble(args[6]);
                median = Integer.parseInt(args[7]);
                stock = Integer.parseInt(args[8]);

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

            if (buyValue < 0.01 || median == 0 || stock == 0)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.VALUE_ZERO"));
                return;
            }
        } catch (Exception e)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_DATATYPE"));
            return;
        }

        // 금지품목
        if (Material.getMaterial(args[3]) == Material.AIR)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.ITEM_FORBIDDEN"));
            return;
        }

        // 상점에서 같은 아이탬 찾기
        ItemStack itemStack;
        try
        {
            itemStack = new ItemStack(mat);
        } catch (Exception e)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_ITEM_NAME"));
            return;
        }

        int idx = ShopUtil.findItemFromShop(shopName, itemStack);
        // 상점에 새 아이탬 추가
        if (idx == -1)
        {
            idx = ShopUtil.findEmptyShopSlot(shopName, 1, true);
            if (idx == -1)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_EMPTY_SLOT"));
            } else if (ShopUtil.addItemToShop(shopName, idx, itemStack, buyValue, buyValue, valueMin, valueMax, median, stock)) // 아이탬 추가
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_ADDED"));
                ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
            }
        }
        // 기존 아이탬 수정
        else
        {
            ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock);
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_UPDATED"));
            ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
        }
    }
}
