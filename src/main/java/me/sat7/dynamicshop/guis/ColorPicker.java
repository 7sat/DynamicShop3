package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import me.sat7.dynamicshop.utilities.UserUtil;
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
        CreateCloseButton(player, CLOSE);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        if (e.getSlot() == CLOSE)
        {
            if (slotIndex == -1)
            {
                String shopName = UserUtil.userInteractItem.get(player.getUniqueId()).split("/")[0];
                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            else
            {
                DynaShopAPI.openStartPageSettingGui(player, slotIndex);
            }
        }
        else if (slotIndex == -1)
        {
            String shopName = UserUtil.userInteractItem.get(player.getUniqueId()).split("/")[0];
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

            String color = null;
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().isAir())
                color = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

            data.get().set("Options.background", color);
            data.save();

            DynaShopAPI.openShopSettingGui(player, shopName);
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
        CreateButton(0, Material.BLACK_STAINED_GLASS_PANE, "§fBLACK", "");
        CreateButton(1, Material.GRAY_STAINED_GLASS_PANE, "§fGRAY", "");
        CreateButton(2, Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§fLIGHT_GRAY", "");
        CreateButton(3, Material.WHITE_STAINED_GLASS_PANE, "§fWHITE", "");
        CreateButton(4, Material.CYAN_STAINED_GLASS_PANE, "§fCYAN", "");
        CreateButton(5, Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§fLIGHT_BLUE", "");
        CreateButton(6, Material.BLUE_STAINED_GLASS_PANE, "§fBLUE", "");
        CreateButton(7, Material.BROWN_STAINED_GLASS_PANE, "§fBROWN", "");
        CreateButton(8, Material.GREEN_STAINED_GLASS_PANE, "§fGREEN", "");
        CreateButton(9, Material.LIME_STAINED_GLASS_PANE, "§fLIME", "");
        CreateButton(10, Material.YELLOW_STAINED_GLASS_PANE, "§fYELLOW", "");
        CreateButton(11, Material.ORANGE_STAINED_GLASS_PANE, "§fORANGE", "");
        CreateButton(12, Material.PINK_STAINED_GLASS_PANE, "§fPINK", "");
        CreateButton(13, Material.MAGENTA_STAINED_GLASS_PANE, "§fMAGENTA", "");
        CreateButton(14, Material.PURPLE_STAINED_GLASS_PANE, "§fPURPLE", "");
        CreateButton(15, Material.RED_STAINED_GLASS_PANE, "§fRED", "");
    }
}
