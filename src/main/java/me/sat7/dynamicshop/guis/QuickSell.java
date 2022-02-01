package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.transactions.Sell;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import me.sat7.dynamicshop.utilities.LangUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class QuickSell extends InGameUI
{

    public QuickSell()
    {
        uiType = UI_TYPE.QuickSell;
    }

    public Inventory getGui(Player player)
    {
        Inventory inven = Bukkit.createInventory(player, 9, LangUtil.ccLang.get().getString("QUICKSELL_TITLE"));

        ItemStack infoBtn = ItemsUtil.createItemStack(Material.RED_STAINED_GLASS_PANE,
                null,
                LangUtil.ccLang.get().getString("QUICKSELL_GUIDE_TITLE"),
                new ArrayList<String>(Arrays.asList(LangUtil.ccLang.get().getString("QUICKSELL_GUIDE_LORE").split("\n"))),
                1);

        for (int i = 0; i < 9; i++)
            inven.setItem(i, infoBtn);

        return inven;
    }

    @Override
    public void OnClickLowerInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        if (player == null)
            return;

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
        {
            return;
        }

        String[] targetShopInfo = ShopUtil.FindTheBestShopToSell(player, e.getCurrentItem());
        String topShopName = targetShopInfo[0];
        int tradeIdx = Integer.parseInt(targetShopInfo[1]);

        if (topShopName.length() > 0)
        {
            if (e.isLeftClick())
            {
                // 찾은 상점에 판매
                Sell.quickSellItem(player, e.getCurrentItem(), topShopName, tradeIdx, e.isShiftClick(), e.getSlot());
                //player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("QSELL_RESULT")+topShopName);
            } else if (e.isRightClick())
            {
                player.closeInventory();
                DynaShopAPI.openShopGui(player, topShopName, 1);
            }
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("QSELL_NA") + topShopName);
        }
    }
}
