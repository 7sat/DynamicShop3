package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Comparator;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.events.OnChat;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;

public final class ItemPalette extends InGameUI
{
    public ItemPalette()
    {
        uiType = UI_TYPE.ItemPalette;
    }

    private final int CLOSE = 45;
    private final int PAGE = 49;
    private final int ADD_ALL = 51;
    private final int SEARCH = 53;

    private static final ArrayList<Material> sortedMat = new ArrayList<>();

    // 파렛트 정렬용
    public static final Comparator<Material> sortMat = Comparator.comparing(ItemPalette::getMatName);

    private static String getMatName(Material m2)
    {
        String name2 = m2.name();
        if (name2.startsWith("LIGHT_")) name2 = name2.substring(6);
        if (name2.startsWith("DARK_")) name2 = name2.substring(5);
        int idx2 = name2.indexOf('_');
        if (idx2 != -1)
        {
            name2 = name2.substring(idx2);
        }
        return name2;
    }

    public Inventory getGui(Player player, int page, String search)
    {
        inventory = Bukkit.createInventory(player, 54, t("PALETTE_TITLE"));
        ArrayList<Material> paletteList = new ArrayList<>();

        if (search.length() > 0)
        {
            Material[] allMat = Material.values();
            for (Material m : allMat)
            {
                if (m.name().contains(search.toUpperCase())) paletteList.add(m);
            }
        } else
        {
            if (sortedMat.isEmpty())
            {
                Material[] allMat = Material.values();
                ArrayList<Material> allMatList = new ArrayList<>();
                for (Material m : allMat)
                {
                    if (m.isItem()) allMatList.add(m);
                }
                //------------------------------------------
                allMatList.sort(sortMat);

                for (Material m : allMatList)
                {
                    if (m.isEdible()) sortedMat.add(m);
                }
                for (Material m : allMatList)
                {
                    if (m.name().contains("SPAWN_EGG")) sortedMat.add(m);
                }
                for (Material m : allMatList)
                {
                    if (!sortedMat.contains(m)) sortedMat.add(m);
                }
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Sorting Items...This should run only once.");
            }

            paletteList = sortedMat;
        }


        CreateCloseButton(CLOSE); // 닫기 버튼
        CreateButton(PAGE, Material.PAPER, page + t("PAGE"), t("PAGE_LORE"), page); // 페이지 버튼
        CreateButton(ADD_ALL, Material.YELLOW_STAINED_GLASS_PANE, t("ADDALL"), search); // 모두추가 버튼
        CreateButton(SEARCH, Material.COMPASS, t("SEARCH"), search); // 검색 버튼

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
                    lore.add(t("PALETTE_LORE"));
                    lore.add(t("DECO_CREATE_LORE"));
                    btnMeta.setLore(lore);
                    btn.setItemMeta(btnMeta);
                }

                inventory.setItem(i, btn);
            } catch (Exception ignored)
            {
            }
        }
        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        String shopName = temp[0];
        @SuppressWarnings("ConstantConditions")
        int curPage = e.getInventory().getItem(PAGE).getAmount();

        // 닫기 버튼
        if (e.getSlot() == CLOSE)
        {
            DynamicShop.userInteractItem.put(player.getUniqueId(), "");
            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 페이지 버튼
        else if (e.getSlot() == PAGE)
        {
            int targetPage = curPage;
            if (e.isLeftClick())
            {
                targetPage -= 1;
                if (targetPage < 1) targetPage = 30;
            } else if (e.isRightClick())
            {
                targetPage += 1;
                if (targetPage > 30) targetPage = 1;
            }

            @SuppressWarnings("ConstantConditions")
            String search = e.getClickedInventory().getItem(SEARCH).getItemMeta().getLore().toString().replace("[", "").replace("]", "");
            DynaShopAPI.openItemPalette(player, targetPage, search);
        }
        // 모두 추가 버튼
        else if (e.getSlot() == ADD_ALL)
        {
            for (int i = 0; i < 45; i++)
            {
                if (e.getClickedInventory() != null && e.getClickedInventory().getItem(i) != null)
                {
                    //noinspection ConstantConditions
                    Material material = e.getClickedInventory().getItem(i).getType();
                    if (material == Material.AIR)
                        continue;

                    int existSlot = ShopUtil.findItemFromShop(shopName, new ItemStack(material));
                    if (-1 != existSlot) // 이미 상점에 등록되어 있는 아이템 무시
                        continue;

                    int idx = ShopUtil.findEmptyShopSlot(shopName);
                    if (idx != -1)
                    {
                        ItemStack tempIs = new ItemStack(material);
                        ShopUtil.addItemToShop(shopName, idx, tempIs, 1, 1, 0.01, -1, 10000, 10000);
                    } else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_EMPTY_SLOT"));
                        break;
                    }
                }
            }

            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 검색 버튼
        else if (e.getSlot() == SEARCH)
        {
            player.closeInventory();

            DynamicShop.userTempData.put(player.getUniqueId(), "waitforPalette");
            OnChat.WaitForInput(player);

            player.sendMessage(DynamicShop.dsPrefix + t("SEARCH_ITEM"));
        } else if (e.getSlot() <= 45)
        {
            // 파렛트에서 뭔가 선택
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().toString().equals(Material.AIR.toString()))
            {
                ItemStack iStack = new ItemStack(e.getCurrentItem().getType());
                if (e.isLeftClick())
                {
                    DynaShopAPI.openItemSettingGui(player, iStack, 1, 10, 10, 0.01, -1, 10000, 10000);
                } else
                {
                    ShopUtil.addItemToShop(shopName, Integer.parseInt(temp[1]), iStack, -1, -1, -1, -1, -1, -1);
                    DynaShopAPI.openShopGui(player, shopName, Integer.parseInt(temp[1]) / 45 + 1);
                }
            }
        }
    }

    @Override
    public void OnClickLowerInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() != null && !e.getCurrentItem().getType().toString().equals(Material.AIR.toString()))
        {
            if (e.isLeftClick())
            {
                DynaShopAPI.openItemSettingGui(player, e.getCurrentItem(), 1, 10, 10, 0.01, -1, 1000, 1000);
            } else
            {
                String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");

                ShopUtil.addItemToShop(temp[0], Integer.parseInt(temp[1]), e.getCurrentItem(), -1, -1, -1, -1, -1, -1);
                DynaShopAPI.openShopGui(player, temp[0], Integer.parseInt(temp[1]) / 45 + 1);
            }
        }
    }
}
