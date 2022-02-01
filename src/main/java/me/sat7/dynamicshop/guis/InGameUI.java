package me.sat7.dynamicshop.guis;

import org.bukkit.event.inventory.InventoryClickEvent;

public class InGameUI
{
    public enum UI_TYPE
    {
        ItemPalette,
        ItemSettings,
        ItemTrade,
        QuickSell,
        Shop,
        ShopSettings,
        StartPage,
        StartPageSettings
    }

    public UI_TYPE uiType;

    public void OnClickUpperInventory(InventoryClickEvent e) {};
    public void OnClickLowerInventory(InventoryClickEvent e) {};
}
