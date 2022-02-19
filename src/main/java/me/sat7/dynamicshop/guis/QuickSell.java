package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.transactions.Sell;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class QuickSell extends InGameUI
{
    public QuickSell()
    {
        uiType = UI_TYPE.QuickSell;
    }

    public static CustomConfig quickSellGui;

    public Inventory getGui(Player player)
    {
        inventory = Bukkit.createInventory(player, quickSellGui.get().getInt("UiSlotCount"), t("QUICK_SELL_TITLE"));

        ConfigurationSection confSec = quickSellGui.get().getConfigurationSection("Buttons");
        for(String s : confSec.getKeys(false))
        {
            try{
                int i = Integer.parseInt(s);
                if (i > inventory.getSize())
                    break;

                Material mat = Material.getMaterial(confSec.getString(s));
                if(mat == null)
                    mat = Material.RED_STAINED_GLASS_PANE;

                CreateButton(i, mat, t("QUICK_SELL.GUIDE_TITLE"), t("QUICK_SELL.GUIDE_LORE"));
            }catch (Exception ignore){}
        }
        return inventory;
    }

    public static void SetupQuickSellGUIFile()
    {
        quickSellGui.setup("QuickSell", null);
        quickSellGui.get().addDefault("UiSlotCount", 9);

        if (quickSellGui.get().getKeys(false).size() == 0)
        {
            for (int i = 0; i<9; i++)
                quickSellGui.get().set("Buttons." + i, "RED_STAINED_GLASS_PANE");
        }

        quickSellGui.get().options().copyDefaults(true);
        quickSellGui.save();
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
            } else if (e.isRightClick())
            {
                player.closeInventory();
                DynaShopAPI.openShopGui(player, topShopName, 1);
            }
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.QSELL_NA") + topShopName);
        }
    }
}
