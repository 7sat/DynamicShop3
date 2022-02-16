package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;
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

import static me.sat7.dynamicshop.utilities.LangUtil.t;

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

    private static String getSortName(ItemStack stack)
    {
        String ret = stack.getType().name();

        int idx = ret.lastIndexOf('_');
        //int idx = ret.indexOf('_');
        if (idx != -1)
            ret = ret.substring(idx);

        return ret;
    }
    private static int getArmorType(ItemStack stack)
    {
        String name = stack.getType().name();
        if(name.contains("HELMET"))
            return 0;
        if(name.contains("CHESTPLATE"))
            return 1;
        if(name.contains("LEGGINGS"))
            return 2;
        if(name.contains("BOOTS"))
            return 3;
        if(name.contains("TURTLE_SHELL"))
            return 4;

        return 5;
    }

    private static ArrayList<ItemStack> sortedList = new ArrayList<>();

    private void SortAllItems()
    {
        ArrayList<ItemStack> allItems = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (m.isItem())
                allItems.add(new ItemStack(m));
        }

        Collections.sort(allItems, ((Comparator<ItemStack>) (o1, o2) ->
        {
            if (o1.getType().getMaxDurability() > 0 && o2.getType().getMaxDurability() > 0)
                return 0;
            else if (o1.getType().getMaxDurability() > 0)
                return -1;
            else if (o2.getType().getMaxDurability() > 0)
                return 1;

            int isEdible = Boolean.compare(o2.getType().isEdible(), o1.getType().isEdible());
            if (isEdible != 0)
                return isEdible;

            int isSolid = Boolean.compare(o2.getType().isSolid(), o1.getType().isSolid());
            if (isSolid != 0)
                return isSolid;

            int isRecord = Boolean.compare(o2.getType().isRecord(), o1.getType().isRecord());
            if (isRecord != 0)
                return isRecord;

            return 0;
        }).thenComparing(ItemPalette::getArmorType).thenComparing(ItemPalette::getSortName));

        sortedList = allItems;
    }

    private String search = "";
    private int maxPage;
    private int currentPage;
    String shopName = "";
    int shopSlotIndex = 0;

    public Inventory getGui(Player player, int page, String search)
    {
        this.search = search;

        String[] userData = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        shopName = userData[0];
        shopSlotIndex = Integer.parseInt(userData[1]);

        inventory = Bukkit.createInventory(player, 54, t("PALETTE_TITLE") + "§7 | §8" + shopName);
        ArrayList<ItemStack> paletteList = new ArrayList<>();

        if (search.length() > 0)
        {
            Material[] allMat = Material.values();
            for (Material m : allMat)
            {
                String target = m.name().toUpperCase();

                String[] temp = search.split(" ");

                if (temp.length == 1)
                {
                    if (target.contains(search.toUpperCase()))
                    {
                        paletteList.add(new ItemStack(m));
                    }
                    else if (target.contains(search.toUpperCase().replace(" ", "_")))
                    {
                        paletteList.add(new ItemStack(m));
                    }
                }
                else
                {
                    String[] targetTemp = target.split("_");

                    if(targetTemp.length > 1 && temp.length > 1 && targetTemp.length == temp.length)
                    {
                        boolean match = true;
                        for(int i = 0; i < targetTemp.length; i++)
                        {
                            if(!targetTemp[i].startsWith(temp[i].toUpperCase()))
                            {
                                match = false;
                                break;
                            }
                        }
                        if(match)
                            paletteList.add(new ItemStack(m));
                    }
                }
            }
        } else
        {
            if (sortedList.isEmpty())
                SortAllItems();

            paletteList = sortedList;
        }

        paletteList.removeIf(itemStack -> ShopUtil.findItemFromShop(shopName, new ItemStack(itemStack.getType())) != -1);

        maxPage = paletteList.size() / 45 + 1;
        currentPage = page;

        CreateCloseButton(CLOSE); // 닫기 버튼

        String pageString = t("PALETTE.PAGE_TITLE");
        pageString = pageString.replace("{curPage}", page + "");
        pageString = pageString.replace("{maxPage}", maxPage + "");

        CreateButton(PAGE, Material.PAPER, pageString, t("PALETTE.PAGE_LORE"), page); // 페이지 버튼
        CreateButton(ADD_ALL, Material.YELLOW_STAINED_GLASS_PANE, t("PALETTE.ADD_ALL"), ""); // 모두추가 버튼
        String filterString = search.isEmpty() ? "" : t("PALETTE.FILTER_APPLIED") + search;
        filterString += "\n" + t("PALETTE.FILTER_LORE");
        CreateButton(SEARCH, Material.COMPASS, t("PALETTE.SEARCH"), filterString); // 검색 버튼

        //45개씩 끊어서 표시.
        for (int i = 0; i < 45; i++)
        {
            try
            {
                int idx = i + ((page - 1) * 45);
                if (idx >= paletteList.size()) break;

                ItemStack btn = paletteList.get(idx);
                ItemMeta btnMeta = btn.getItemMeta();

                String searchName = btn.getType().name();
                int subStrIdx = searchName.lastIndexOf('_');
                if (subStrIdx != -1)
                    searchName = searchName.substring(subStrIdx);

                if (btnMeta != null)
                {
                    String[] lore = t("PALETTE.LORE").replace("{item}", searchName.replace("_","")).split("\n");
                    btnMeta.setLore(new ArrayList<>(Arrays.asList(lore)));
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

        // 닫기 버튼
        if (e.getSlot() == CLOSE)
        {
            DynamicShop.userInteractItem.put(player.getUniqueId(), "");
            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 페이지 버튼
        else if (e.getSlot() == PAGE)
        {
            int targetPage = currentPage;
            if (e.isLeftClick())
            {
                targetPage -= 1;
                if (targetPage < 1) targetPage = maxPage;
            } else if (e.isRightClick())
            {
                targetPage += 1;
                if (targetPage > maxPage) targetPage = 1;
            }

            DynaShopAPI.openItemPalette(player, targetPage, this.search);
        }
        // 모두 추가 버튼
        else if (e.getSlot() == ADD_ALL)
        {
            int targetSlotIdx = shopSlotIndex;
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

                    targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);
                    ItemStack tempIs = new ItemStack(material);
                    ShopUtil.addItemToShop(shopName, targetSlotIdx, tempIs, 1, 1, 0.01, -1, 10000, 10000);
                }
            }
            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 검색 버튼
        else if (e.getSlot() == SEARCH)
        {
            if(e.isLeftClick())
            {
                player.closeInventory();

                DynamicShop.userTempData.put(player.getUniqueId(), "waitforPalette");
                OnChat.WaitForInput(player);

                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.SEARCH_ITEM"));
            }
            else if(e.isRightClick())
            {
                DynaShopAPI.openItemPalette(player, currentPage, "");
            }
        }
        // 파렛트에서 뭔가 선택
        else if (e.getSlot() <= 45)
        {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().toString().equals(Material.AIR.toString()))
            {
                ItemStack iStack = new ItemStack(e.getCurrentItem().getType());

                if (e.isLeftClick())
                {
                    DynaShopAPI.openItemSettingGui(player, iStack, 1, 10, 10, 0.01, -1, 10000, 10000);
                } else if (e.isRightClick())
                {
                    int targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);
                    DynamicShop.userInteractItem.put(player.getUniqueId(), shopName + "/" + targetSlotIdx + 1);

                    ShopUtil.addItemToShop(shopName, targetSlotIdx, iStack, -1, -1, -1, -1, -1, -1);
                    DynaShopAPI.openShopGui(player, shopName, targetSlotIdx / 45 + 1);
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
