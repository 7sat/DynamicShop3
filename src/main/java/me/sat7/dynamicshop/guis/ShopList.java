package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.utilities.MathUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class ShopList extends InGameUI
{
    public ShopList()
    {
        uiType = InGameUI.UI_TYPE.StartPage_ShopList;
    }

    private final int CLOSE = 45;
    private final int PAGE = 49;

    private int page;
    private int maxPage;
    private int slotIndex;

    public Inventory getGui(Player player, int page, int slotIndex)
    {
        inventory = Bukkit.createInventory(player, 54, t(player, "START_PAGE.SHOP_LIST_TITLE"));

        this.maxPage = ShopUtil.shopConfigFiles.size() / 45 + 1;
        this.page = MathUtil.Clamp(page, 1, maxPage);
        this.slotIndex = slotIndex;

        CreateShopButtons();
        CreateCloseButton(player, CLOSE);
        CreateButton(PAGE, GetPageButtonIconMat(),
                t(player, "START_PAGE.SHOP_LIST.PAGE_TITLE").replace("{curPage}", String.valueOf(this.page)).replace("{maxPage}", String.valueOf(this.maxPage)),
                t(player, "START_PAGE.SHOP_LIST.PAGE_LORE"));

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openStartPageSettingGui(player, slotIndex);
        } else if (e.getSlot() == PAGE)
        {
            if (e.isLeftClick())
            {
                page--;
                if (page < 1)
                    page = maxPage;
            } else if (e.isRightClick())
            {
                page++;
                if (page > maxPage)
                    page = 1;
            }

            DynaShopAPI.openShopListUI(player, page, slotIndex);
        } else if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.CHEST)
        {
            String shopName = e.getCurrentItem().getItemMeta().getDisplayName();
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".displayName", "§3" + shopName);
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".lore", t(player, "START_PAGE.DEFAULT_SHOP_LORE"));
            StartPage.ccStartPage.get().set("Buttons." + slotIndex + ".action", "ds shop " + shopName);
            StartPage.ccStartPage.save();

            DynaShopAPI.openStartPage(player);
        }
    }

    private void CreateShopButtons()
    {
        int idx = 0;
        int slotIdx = 0;
        for (String shopName : ShopUtil.shopConfigFiles.keySet())
        {
            if (idx > page * 45)
                break;

            if (idx >= (page - 1) * 45)
            {
                CreateButton(slotIdx, Material.CHEST, shopName, "");
                slotIdx++;
            }

            idx++;
        }
    }
}
