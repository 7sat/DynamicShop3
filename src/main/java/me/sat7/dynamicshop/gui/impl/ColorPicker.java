package me.sat7.dynamicshop.gui.impl;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.gui.InGameUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class ColorPicker extends InGameUI
{
    public ColorPicker()
    {
        uiType = UI_TYPE.StartPage_ColorList;
    }

    private final int CLOSE = 18;

    private int slotIndex;

    public Inventory getGui(Player player, int slotIndex)
    {
        inventory = Bukkit.createInventory(player, 27, t(player, "COLOR_PICKER_TITLE"));

        this.slotIndex = slotIndex;

        CreateColorButtons();
        createCloseButton(player, CLOSE);

        return inventory;
    }

    @Override
    public void onClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openStartPageSettingGui(player, slotIndex);
        }
        else if(e.getCurrentItem() != null && !e.getCurrentItem().getType().isAir())
        {
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".displayName", null);
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".lore", null);
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".icon", ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()) + "_STAINED_GLASS_PANE");
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".action", "");
            StartPage.ccStartPage.save();

            DynaShopAPI.openStartPage(player);
        }
    }

    private void CreateColorButtons()
    {
        createButton(0, Material.BLACK_STAINED_GLASS_PANE, "§fBLACK", "");
        createButton(1, Material.GRAY_STAINED_GLASS_PANE, "§fGRAY", "");
        createButton(2, Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§fLIGHT_GRAY", "");
        createButton(3, Material.WHITE_STAINED_GLASS_PANE, "§fWHITE", "");
        createButton(4, Material.CYAN_STAINED_GLASS_PANE, "§fCYAN", "");
        createButton(5, Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§fLIGHT_BLUE", "");
        createButton(6, Material.BLUE_STAINED_GLASS_PANE, "§fBLUE", "");
        createButton(7, Material.BROWN_STAINED_GLASS_PANE, "§fBROWN", "");
        createButton(8, Material.GREEN_STAINED_GLASS_PANE, "§fGREEN", "");
        createButton(9, Material.LIME_STAINED_GLASS_PANE, "§fLIME", "");
        createButton(10, Material.YELLOW_STAINED_GLASS_PANE, "§fYELLOW", "");
        createButton(11, Material.ORANGE_STAINED_GLASS_PANE, "§fORANGE", "");
        createButton(12, Material.PINK_STAINED_GLASS_PANE, "§fPINK", "");
        createButton(13, Material.MAGENTA_STAINED_GLASS_PANE, "§fMAGENTA", "");
        createButton(14, Material.PURPLE_STAINED_GLASS_PANE, "§fPURPLE", "");
        createButton(15, Material.RED_STAINED_GLASS_PANE, "§fRED", "");
    }
}
