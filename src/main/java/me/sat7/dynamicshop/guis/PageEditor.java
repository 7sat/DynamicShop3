package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynamicShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class PageEditor extends InGameUI
{
    public PageEditor()
    {
        uiType = UI_TYPE.PageEditor;
    }

    private final int CLOSE = 45;
    private final int PAGE = 49;

    public Inventory getGui(Player player)
    {
        inventory = Bukkit.createInventory(player, 54, t("PAGE_EDITOR_TITLE"));

        CreateButton(CLOSE, Material.BARRIER, t("CLOSE"), t("CLOSE_LORE"));
        CreateButton(PAGE, Material.PAPER, t("PAGE"), t("PAGE_LORE"));

        /*
        //45개씩 끊어서 표시.
        for (int i = 0; i < 45; i++)
        {
            try
            {
                int idx = i + ((page - 1) * 45);
                if (idx >= paletteList.size()) break;

                ItemStack btn = new ItemStack(paletteList.get(idx), 1);

                ItemMeta btnMeta = btn.getItemMeta();
                if (btnMeta != null)
                {
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(LangUtil.ccLang.get().getString("PALETTE_LORE"));
                    lore.add(LangUtil.ccLang.get().getString("DECO_CREATE_LORE"));
                    btnMeta.setLore(lore);
                    btn.setItemMeta(btnMeta);
                }

                inventory.setItem(i, btn);
            } catch (Exception ignored)
            {
            }
        }
        */

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        // 닫기 버튼
        if (e.getSlot() == CLOSE)
        {
            DynamicShop.userInteractItem.put(player.getUniqueId(), "");
            //DynaShopAPI.openShopGui(player, shopName, 1);
        }
    }
}
