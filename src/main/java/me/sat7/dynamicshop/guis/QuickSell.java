package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.transactions.Sell;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

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
        inventory = Bukkit.createInventory(player, 9, t("QUICKSELL_TITLE"));

        for (int i = 0; i < 9; i++)
        {
            CreateButton(i, Material.RED_STAINED_GLASS_PANE, t("QUICKSELL_GUIDE_TITLE"), new ArrayList<>(Arrays.asList(t("QUICKSELL_GUIDE_LORE").split("\n"))));
        }

        return inventory;
    }

    @Override
    public void OnClickLowerInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

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
                //player.sendMessage(DynamicShop.dsPrefix + t("QSELL_RESULT")+topShopName);
            } else if (e.isRightClick())
            {
                player.closeInventory();
                DynaShopAPI.openShopGui(player, topShopName, 1);
            }
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("QSELL_NA") + topShopName);
        }
    }
}
