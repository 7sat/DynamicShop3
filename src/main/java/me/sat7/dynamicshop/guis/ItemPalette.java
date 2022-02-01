package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Collections;
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
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;

public class ItemPalette extends InGameUI
{

    public ItemPalette()
    {
        uiType = UI_TYPE.ItemPalette;
    }

    private static ArrayList<Material> sortedMat = new ArrayList<>();

    // 파렛트 정렬용
    public static Comparator<Material> sortMat = (m1, m2) -> getMatName(m1).compareTo(
            getMatName(m2));

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
        Inventory inven = Bukkit.createInventory(player, 54, LangUtil.ccLang.get().getString("PALETTE_TITLE"));
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

        // 닫기 버튼
        ItemStack closeBtn = ItemsUtil.createItemStack(Material.BARRIER, null,
                LangUtil.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("CLOSE_LORE"))), 1);

        inven.setItem(45, closeBtn);

        // 페이지 버튼
        ItemStack pageBtn = ItemsUtil.createItemStack(Material.PAPER, null,
                page + LangUtil.ccLang.get().getString("PAGE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("PAGE_LORE"))), page);

        inven.setItem(49, pageBtn);

        // 모두추가 버튼
        ItemStack addAllBtn = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null,
                LangUtil.ccLang.get().getString("ADDALL"),
                new ArrayList<>(Collections.singletonList(search)), 1);

        inven.setItem(51, addAllBtn);

        // 검색 버튼
        ItemStack searchBtn = ItemsUtil.createItemStack(Material.COMPASS, null,
                LangUtil.ccLang.get().getString("SEARCH"),
                new ArrayList<>(Collections.singletonList(search)), 1);

        inven.setItem(53, searchBtn);

        //45개씩 끊어서 표시.
        for (int i = 0; i < 45; i++)
        {
            try
            {
                int idx = i + ((page - 1) * 45);
                if (idx >= paletteList.size()) break;

                ItemStack btn = new ItemStack(paletteList.get(idx), 1);

                ItemMeta btnMeta = btn.getItemMeta();
                ArrayList<String> btnlore = new ArrayList<>();
                btnlore.add(LangUtil.ccLang.get().getString("PALETTE_LORE"));
                btnlore.add(LangUtil.ccLang.get().getString("DECO_CREATE_LORE"));
                btnMeta.setLore(btnlore);
                btn.setItemMeta(btnMeta);

                inven.setItem(i, btn);
            } catch (Exception ignored)
            {
            }
        }
        return inven;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        if (player == null)
            return;

        String[] temp = DynamicShop.ccUser.get().getString(player.getUniqueId() + ".interactItem").split("/");
        String shopName = temp[0];
        int curPage = e.getInventory().getItem(49).getAmount();

        // 닫기 버튼
        if (e.getSlot() == 45)
        {
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "");
            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 페이지 버튼
        else if (e.getSlot() == 49)
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
            String search = e.getClickedInventory().getItem(53).getItemMeta().getLore().toString().replace("[", "").replace("]", "");
            DynaShopAPI.openItemPalette(player, targetPage, search);
        }
        // 모두 추가 버튼
        else if (e.getSlot() == 51)
        {
            for (int i = 0; i < 45; i++)
            {
                if (e.getClickedInventory().getItem(i) != null && e.getClickedInventory().getItem(i).getType() != Material.AIR)
                {
                    int existSlot = ShopUtil.findItemFromShop(shopName, new ItemStack(e.getClickedInventory().getItem(i).getType()));
                    if (-1 == existSlot)
                    {
                        int idx = ShopUtil.findEmptyShopSlot(shopName);
                        if (idx != -1)
                        {
                            ItemStack tempIs = new ItemStack(e.getClickedInventory().getItem(i).getType());
                            ShopUtil.addItemToShop(shopName, idx, tempIs, 1, 1, 0.01, -1, 10000, 10000);
                        } else
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_EMPTY_SLOT"));
                            break;
                        }
                    } // 이미 상점에 추가되 있는 아이탬이라면 아무일도 안함.
                }
            }

            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 검색 버튼
        else if (e.getSlot() == 53)
        {
            player.closeInventory();

            DynamicShop.ccUser.get().set(player.getUniqueId() + ".tmpString", "waitforPalette");
            OnChat.WaitForInput(player);

            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("SEARCH_ITEM"));
        } else if (e.getSlot() > 45)
        {
            return;
        }
        // 파렛트에서 뭔가 선택
        else
        {
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
        if (player == null)
            return;

        if (e.getCurrentItem() != null && !e.getCurrentItem().getType().toString().equals(Material.AIR.toString()))
        {
            if (e.isLeftClick())
            {
                DynaShopAPI.openItemSettingGui(player, e.getCurrentItem(), 1, 10, 10, 0.01, -1, 1000, 1000);
            } else
            {
                String[] temp = DynamicShop.ccUser.get().getString(player.getUniqueId() + ".interactItem").split("/");

                ShopUtil.addItemToShop(temp[0], Integer.parseInt(temp[1]), e.getCurrentItem(), -1, -1, -1, -1, -1, -1);
                DynaShopAPI.openShopGui(player, temp[0], Integer.parseInt(temp[1]) / 45 + 1);
            }
        }
    }
}
