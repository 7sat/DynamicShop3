package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        PageEditor,
        LogViewer,
        StockSimulator,
        RotationEditor
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
        ArrayList<String> finalLore = new ArrayList<>();
        for(String loreLine : lore)
        {
            if (loreLine.contains("\n"))
            {
                finalLore.addAll(Arrays.asList(loreLine.split("\n")));
            }
            else
            {
                finalLore.add(loreLine);
            }
        }

        ItemStack itemStack = ItemsUtil.createItemStack(icon, null, name, finalLore, amount);
        inventory.setItem(slotIndex, itemStack);

        return itemStack;
    }

    @SuppressWarnings("SameParameterValue")
    protected void CreateCloseButton(Player player, int slotIndex)
    {
        CreateButton(slotIndex, InGameUI.GetCloseButtonIconMat(), t(player, "CLOSE"), t(player, "CLOSE_LORE"));
    }

    public static Material GetCloseButtonIconMat()
    {
        String iconName = ConfigUtil.GetCloseButtonIcon();
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.BARRIER;
            ConfigUtil.SetCloseButtonIcon("BARRIER");
            ConfigUtil.Save();
        }
        return mat;
    }

    public static Material GetPageButtonIconMat()
    {
        String iconName = ConfigUtil.GetPageButtonIcon();
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.PAPER;
            ConfigUtil.SetPageButtonIcon("PAPER");
            ConfigUtil.Save();
        }
        return mat;
    }

    public static Material GetShopInfoButtonIconMat()
    {
        String iconName = ConfigUtil.GetShopInfoButtonIcon();
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.GOLD_BLOCK;
            ConfigUtil.SetShopInfoButtonIcon("GOLD_BLOCK");
            ConfigUtil.Save();
        }
        return mat;
    }

    public static Material GetBalanceButtonIconMat()
    {
        String iconName = ConfigUtil.GetBalanceButtonIcon();
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.EMERALD;
            ConfigUtil.SetBalanceButtonIcon("EMERALD");
            ConfigUtil.Save();
        }
        return mat;
    }

    public static Material GetSellToggleButtonIconMat()
    {
        String iconName = ConfigUtil.GetSellToggleButtonIcon();
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.GREEN_STAINED_GLASS;
            ConfigUtil.SetSellToggleButtonIcon("GREEN_STAINED_GLASS");
            ConfigUtil.Save();
        }
        return mat;
    }

    public static Material GetBuyToggleButtonIconMat()
    {
        String iconName = ConfigUtil.GetBuyToggleButtonIcon();
        Material mat = Material.getMaterial(iconName);
        if (mat == null)
        {
            mat = Material.RED_STAINED_GLASS;
            ConfigUtil.SetBuyToggleButtonIcon("RED_STAINED_GLASS");
            ConfigUtil.Save();
        }
        return mat;
    }
}
