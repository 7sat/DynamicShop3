package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

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
        StartPage_ShopList,
        StartPage_ColorList,
    }

    public UI_TYPE uiType;

    public void OnClickUpperInventory(InventoryClickEvent e)
    {
    }

    public void OnClickLowerInventory(InventoryClickEvent e)
    {
    }

    public void RefreshUI(){}

    protected Inventory inventory;

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack CreateButton(int slotIndex, Material icon, String name, String lore)
    {
        if (lore != null && lore.isEmpty())
            lore = null;

        if (lore == null)
        {
            return CreateButton(slotIndex, icon, name, 1);
        }
        else if (lore.contains("\n"))
        {
            return CreateButton(slotIndex, icon, name, new ArrayList<>(Arrays.asList(lore.split("\n"))), 1);
        }
        else
        {
            return CreateButton(slotIndex, icon, name, new ArrayList<>(Collections.singletonList(lore)), 1);
        }
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack CreateButton(int slotIndex, Material icon, String name, ArrayList<String> lore)
    {
        return CreateButton(slotIndex, icon, name, lore, 1);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack CreateButton(int slotIndex, Material icon, String name, String lore, int amount)
    {
        if (lore != null && lore.isEmpty())
            lore = null;

        if (lore == null)
        {
            return CreateButton(slotIndex, icon, name, amount);
        }
        else if (lore.contains("\n"))
        {
            return CreateButton(slotIndex, icon, name, new ArrayList<>(Arrays.asList(lore.split("\n"))), amount);
        }
        else
        {
            return CreateButton(slotIndex, icon, name, new ArrayList<>(Collections.singletonList(lore)), amount);
        }
    }

    protected ItemStack CreateButton(int slotIndex, Material icon, String name, int amount)
    {
        ItemStack itemStack = ItemsUtil.createItemStack(icon, null, name, null, amount);
        inventory.setItem(slotIndex, itemStack);

        return itemStack;
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
        CreateButton(slotIndex, InGameUI.GetCloseButtonIconMat(), t("CLOSE"), t("CLOSE_LORE"));
    }

    public static Material GetCloseButtonIconMat()
    {
        String iconName = DynamicShop.plugin.getConfig().getString("UI.CloseButtonIcon");
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.BARRIER;
            DynamicShop.plugin.getConfig().set("UI.CloseButtonIcon", "BARRIER");
            DynamicShop.plugin.saveConfig();
        }
        return mat;
    }

    public static Material GetPageButtonIconMat()
    {
        String iconName = DynamicShop.plugin.getConfig().getString("UI.PageButtonIcon");
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.PAPER;
            DynamicShop.plugin.getConfig().set("UI.PageButtonIcon", "PAPER");
            DynamicShop.plugin.saveConfig();
        }
        return mat;
    }

    public static Material GetShopInfoButtonIconMat()
    {
        String iconName = DynamicShop.plugin.getConfig().getString("UI.ShopInfoButtonIcon");
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.GOLD_BLOCK;
            DynamicShop.plugin.getConfig().set("UI.ShopInfoButtonIcon", "GOLD_BLOCK");
            DynamicShop.plugin.saveConfig();
        }
        return mat;
    }
}
