package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import org.bukkit.Bukkit;
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
        inventory = Bukkit.createInventory(player, 27, t("COLOR_PICKER_TITLE"));

        this.slotIndex = slotIndex;

        CreateColorButtons();
        CreateCloseButton(CLOSE);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
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
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".icon", e.getCurrentItem().getType());
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".action", "");

            DynaShopAPI.openStartPage(player);
        }
    }

    private void CreateColorButtons()
    {
        CreateButton(0, Material.BLACK_STAINED_GLASS_PANE, "§fBlack", "");
        CreateButton(1, Material.GRAY_STAINED_GLASS_PANE, "§fGray", "");
        CreateButton(2, Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§fLight gray", "");
        CreateButton(3, Material.WHITE_STAINED_GLASS_PANE, "§fWhite", "");
        CreateButton(4, Material.CYAN_STAINED_GLASS_PANE, "§fCyan", "");
        CreateButton(5, Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§fLight blue", "");
        CreateButton(6, Material.BLUE_STAINED_GLASS_PANE, "§fBlue", "");
        CreateButton(7, Material.BROWN_STAINED_GLASS_PANE, "§fBrown", "");
        CreateButton(8, Material.GREEN_STAINED_GLASS_PANE, "§fGreen", "");
        CreateButton(9, Material.LIME_STAINED_GLASS_PANE, "§fLime", "");
        CreateButton(10, Material.YELLOW_STAINED_GLASS_PANE, "§fYellow", "");
        CreateButton(11, Material.ORANGE_STAINED_GLASS_PANE, "§fOrange", "");
        CreateButton(12, Material.PINK_STAINED_GLASS_PANE, "§fPink", "");
        CreateButton(13, Material.MAGENTA_STAINED_GLASS_PANE, "§fMagenta", "");
        CreateButton(14, Material.PURPLE_STAINED_GLASS_PANE, "§fPurple", "");
        CreateButton(15, Material.RED_STAINED_GLASS_PANE, "§fRed", "");
    }
}
