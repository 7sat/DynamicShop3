package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

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
        StartPageSettings,
        PageEditor
    }

    public UI_TYPE uiType;

    public void OnClickUpperInventory(InventoryClickEvent e)
    {
    }

    public void OnClickLowerInventory(InventoryClickEvent e)
    {
    }

    protected Inventory inventory;

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack CreateButton(int slotIndex, Material icon, String name, String lore)
    {
        return CreateButton(slotIndex, icon, name, new ArrayList<>(Collections.singletonList(lore)), 1);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack CreateButton(int slotIndex, Material icon, String name, ArrayList<String> lore)
    {
        return CreateButton(slotIndex, icon, name, lore, 1);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack CreateButton(int slotIndex, Material icon, String name, String lore, int amount)
    {
        return CreateButton(slotIndex, icon, name, new ArrayList<>(Collections.singletonList(lore)), amount);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack CreateButton(int slotIndex, Material icon, String name, ArrayList<String> lore, int amount)
    {
        ItemStack itemStack = ItemsUtil.createItemStack(icon, null, name, lore, amount);
        inventory.setItem(slotIndex, itemStack);

        return itemStack;
    }

    @SuppressWarnings("SameParameterValue")
    protected void CreateCloseButton(int slotIndex)
    {
        CreateButton(slotIndex, Material.BARRIER, t("CLOSE"), t("CLOSE_LORE"));
    }

    protected String t(String tag)
    {
        return LangUtil.ccLang.get().getString(tag);
    }
}
