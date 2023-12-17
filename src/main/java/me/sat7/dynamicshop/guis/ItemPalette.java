package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.events.OnChat;
import me.sat7.dynamicshop.models.DSItem;
import me.sat7.dynamicshop.utilities.ShopUtil;
import me.sat7.dynamicshop.utilities.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ItemPalette extends InGameUI
{
    public ItemPalette()
    {
        uiType = UI_TYPE.ItemPalette;
    }

    private int uiSubType = 0; // 0 : forShop, 1 : forStartPage

    private final int CLOSE = 45;
    private final int PAGE = 49;
    private final int ADD_ALL = 51;
    private final int SEARCH = 53;

    private static ArrayList<ItemStack> sortedList = new ArrayList<>();
    private ArrayList<ItemStack> paletteList = new ArrayList<>();

    private Player player;
    private String shopName = "";
    private int shopSlotIndex = 0;
    private String search = "";
    private int maxPage;
    private int currentPage;

    public Inventory getGui(Player player, int uiSubType, String shopName, int targetSlot, int page, String search)
    {
        this.uiSubType = uiSubType;

        this.player = player;
        this.shopName = shopName;
        this.shopSlotIndex = targetSlot;
        this.search = search;

        String title;
        if (uiSubType == 0)
        {
            title = t(player, "PALETTE_TITLE") + "§7 | §8" + shopName;
        }
        else
        {
            title = t(player, "PALETTE_TITLE2");
        }

        inventory = Bukkit.createInventory(player, 54, title);
        paletteList.clear();
        paletteList = CreatePaletteList();
        maxPage = paletteList.size() / 45 + 1;
        currentPage = Clamp(page, 1, maxPage);

        // Items
        ShowItems();

        // Close Button
        CreateCloseButton(player, CLOSE);

        // Page Button
        String pageString = t(player, "PALETTE.PAGE_TITLE")
                .replace("{curPage}", Integer.toString(page))
                .replace("{maxPage}", Integer.toString(maxPage));
        CreateButton(PAGE, InGameUI.GetPageButtonIconMat(), pageString, t(player, "PALETTE.PAGE_LORE"), page);

        // Add all Button
        if(uiSubType == 0)
        {
            if(!paletteList.isEmpty())
                CreateButton(ADD_ALL, Material.YELLOW_STAINED_GLASS_PANE, t(player, "PALETTE.ADD_ALL"), t(player, "PALETTE.ADD_ALL_LORE_LOCKED"));
        }

        // Search Button
        String filterString = search.isEmpty() ? "" : t(player, "PALETTE.FILTER_APPLIED") + search;
        filterString += "\n" + t(player, "PALETTE.FILTER_LORE");
        CreateButton(SEARCH, Material.COMPASS, t(player, "PALETTE.SEARCH"), filterString);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        this.player = (Player) e.getWhoClicked();

        if (e.getSlot() == CLOSE) CloseUI();
        else if (e.getSlot() == PAGE) MovePage(e.isLeftClick(), e.isRightClick());
        else if (e.getSlot() == ADD_ALL && e.isLeftClick() && uiSubType == 0) AddAll(e.isShiftClick());
        else if (e.getSlot() == SEARCH) OnClickSearch(e.isLeftClick(), e.isRightClick());
        else if (e.getSlot() <= 45) OnClickItem(e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getCurrentItem());
    }

    @Override
    public void OnClickLowerInventory(InventoryClickEvent e)
    {
        this.player = (Player) e.getWhoClicked();

        OnClickUserItem(e.isLeftClick(), e.isRightClick(), e.getCurrentItem());
    }

    private ArrayList<ItemStack> CreatePaletteList()
    {
        ArrayList<ItemStack> paletteList = new ArrayList<>();

        if (search.length() > 0)
        {
            for (ItemStack itemStack : sortedList)
            {
                String target = itemStack.getType().name().toUpperCase();
                String[] temp = search.split(" ");

                if (temp.length == 1)
                {
                    if (target.contains(search.toUpperCase()))
                    {
                        paletteList.add(itemStack);
                    } else if (target.contains(search.toUpperCase().replace(" ", "_")))
                    {
                        paletteList.add(itemStack);
                    }
                } else
                {
                    String[] targetTemp = target.split("_");

                    if (targetTemp.length > 1 && temp.length > 1 && targetTemp.length == temp.length)
                    {
                        boolean match = true;
                        for (int i = 0; i < targetTemp.length; i++)
                        {
                            if (!targetTemp[i].startsWith(temp[i].toUpperCase()))
                            {
                                match = false;
                                break;
                            }
                        }
                        if (match)
                            paletteList.add(itemStack);
                    }
                }
            }
        } else
        {
            if (sortedList.isEmpty())
                SortAllItems();

            paletteList = new ArrayList<>(sortedList);
        }

        paletteList.removeIf(itemStack -> ShopUtil.findItemFromShop(this.shopName, CreateItemStackWithRef(itemStack)) != -1);

        return paletteList;
    }

    private void ShowItems()
    {
        for (int i = 0; i < 45; i++)
        {
            try
            {
                int idx = i + ((currentPage - 1) * 45);
                if (idx >= paletteList.size()) break;

                ItemStack btn = paletteList.get(idx);
                ItemMeta btnMeta = btn.getItemMeta();

                String lastName = GetItemLastName(btn);

                if (btnMeta != null)
                {
                    String[] lore;

                    if (uiSubType == 0)
                    {
                        lore = t(player, "PALETTE.LORE_PREMIUM").replace("{item}", lastName.replace("_", "")).split("\n");
                    }
                    else
                    {
                        lore = t(player, "PALETTE.LORE2").replace("{item}", lastName.replace("_", "")).split("\n");
                    }

                    btnMeta.setLore(new ArrayList<>(Arrays.asList(lore)));
                    btn.setItemMeta(btnMeta);
                }

                inventory.setItem(i, btn);
            } catch (Exception ignored)
            {
            }
        }
    }

    public ItemStack getPotionItemStack(Material potionMat, PotionType type, boolean extend, boolean upgraded){
        ItemStack potion = new ItemStack(potionMat, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(type, extend, upgraded));
        potion.setItemMeta(meta);
        return potion;
    }

    public ItemStack getPotionItemStack(Material potionMat, PotionType potionType){
        ItemStack potion = new ItemStack(potionMat, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionType(potionType);
        potion.setItemMeta(meta);
        return potion;
    }

    private void SortAllItems()
    {
        ArrayList<ItemStack> allItems = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (m.isAir())
                continue;

            if (m.isItem())
                allItems.add(new ItemStack(m));
        }

        Material[] potionMat = {Material.POTION, Material.LINGERING_POTION, Material.SPLASH_POTION};
        for (Material mat : potionMat)
        {
            for (PotionType pt : PotionType.values())
            {
                allItems.add(getPotionItemStack(mat, pt));
            }
        }

        allItems.sort(((Comparator<ItemStack>) (o1, o2) ->
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

            int isInteractable = Boolean.compare(o2.getType().isInteractable(), o1.getType().isInteractable());
            if (isInteractable != 0)
                return isInteractable;

            return Boolean.compare(o2.getType().isRecord(), o1.getType().isRecord());
        }).thenComparing(ItemPalette::GetArmorType).thenComparing(ItemPalette::GetPotionType).thenComparing(ItemPalette::GetSortName));

        sortedList = allItems;
    }

    private static String GetSortName(ItemStack stack)
    {
        String ret = stack.getType().name();

        int idx = ret.lastIndexOf('_');
        //int idx = ret.indexOf('_');
        if (idx != -1)
            ret = ret.substring(idx);

        return ret;
    }

    private static int GetArmorType(ItemStack stack)
    {
        String name = stack.getType().name();
        if (name.contains("HELMET"))
            return 0;
        if (name.contains("CHESTPLATE"))
            return 1;
        if (name.contains("LEGGINGS"))
            return 2;
        if (name.contains("BOOTS"))
            return 3;
        if (name.contains("TURTLE_SHELL"))
            return 4;

        return 5;
    }

    private static int GetPotionType(ItemStack stack)
    {
        if (stack.getType() == Material.POTION)
            return 0;
        else if (stack.getType() == Material.SPLASH_POTION)
            return 1;
        else if (stack.getType() == Material.LINGERING_POTION)
            return 2;
        else
            return 3;
    }

    private String GetItemLastName(ItemStack iStack)
    {
        String itemName = iStack.getType().name();
        String[] temp = itemName.split("_");
        if(temp.length >= 2)
        {
            if(temp[temp.length-1].equals(search))
                return temp[temp.length-2];
            else
                return temp[temp.length-1];
        }

        return itemName;
    }

    private void CloseUI()
    {
        if(uiSubType == 0)
        {
            DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
        }
        else
        {
            DynaShopAPI.openStartPageSettingGui(player, shopSlotIndex);
        }
    }

    private void MovePage(boolean isLeft, boolean isRight)
    {
        int targetPage = currentPage;
        if (isLeft)
        {
            targetPage -= 1;
            if (targetPage < 1) targetPage = maxPage;
        } else if (isRight)
        {
            targetPage += 1;
            if (targetPage > maxPage) targetPage = 1;
        }

        if(targetPage == currentPage)
            return;

        DynaShopAPI.openItemPalette(player, uiSubType, shopName, shopSlotIndex, targetPage, this.search);
    }

    private void AddAll(boolean applyRecommend)
    {
        if (applyRecommend)
        {
            DynamicShop.PaidOnlyMsg(player);
            return;
        }

        if(paletteList.isEmpty())
            return;

        int targetSlotIdx;
        for (int i = 0; i < 45; i++)
        {
            if (inventory.getItem(i) != null)
            {
                ItemStack original = inventory.getItem(i); // UI 요소가 추가된 상태임.
                if (original == null || original.getType() == Material.AIR)
                    continue;

                ItemStack itemStack = CreateItemStackWithRef(original);

                targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);

                DSItem temp = new DSItem(itemStack, 1, 1, 0.0001, -1, 10000, 10000);
                ShopUtil.addItemToShop(shopName, targetSlotIdx, temp);
            }
        }
        DynaShopAPI.openShopGui(player, shopName, 1);
    }

    private void OnClickSearch(boolean isLeft, boolean isRight)
    {
        if (isLeft)
        {
            player.closeInventory();

            UserUtil.userTempData.put(player.getUniqueId(), "waitforPalette" + uiSubType);
            OnChat.WaitForInput(player);

            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SEARCH_ITEM"));
        } else if (isRight)
        {
            if(!search.isEmpty())
                DynaShopAPI.openItemPalette(player, uiSubType, shopName, shopSlotIndex, currentPage, "");
        }
    }

    private void OnClickItem(boolean isLeft, boolean isRight, boolean isShift, ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR)
            return;

        // 인자로 들어오는 item은 UI요소임
        ItemStack itemStack = CreateItemStackWithRef(item);

        if(uiSubType == 0)
        {
            if (isLeft)
            {
                if (isShift)
                {
                    DSItem dsItem = new DSItem(itemStack, 10, 10, 0.0001, -1, 10000, 10000);
                    DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex,0, dsItem);
                } else
                {
                    int targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);
                    DSItem temp = new DSItem(itemStack, 1, 1, 0.0001, -1, 10000, 10000);
                    ShopUtil.addItemToShop(shopName, targetSlotIdx, temp);
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_ADDED"));

                    DynaShopAPI.openItemPalette(player, uiSubType, shopName, shopSlotIndex, currentPage, search);
                }
            } else if (isRight)
            {
                if (isShift)
                {
                    DynaShopAPI.openItemPalette(player, uiSubType, shopName, shopSlotIndex, 1, GetItemLastName(item));
                } else
                {
                    int targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);
                    DSItem temp = new DSItem(itemStack, -1, -1, -1, -1, -1, -1);
                    ShopUtil.addItemToShop(shopName, targetSlotIdx, temp);
                    DynaShopAPI.openShopGui(player, shopName, targetSlotIdx / 45 + 1);
                }
            }
        }
        else
        {
            if (isLeft)
            {
                StartPage.ccStartPage.get().set("Buttons." + shopSlotIndex + ".icon", item.getType().toString());

                ItemMeta meta = item.getItemMeta();
                if(meta != null)
                {
                    meta.setDisplayName(null);
                    meta.setLore(null);
                    StartPage.ccStartPage.get().set("Buttons." + shopSlotIndex + ".itemStack", meta);
                }

                StartPage.ccStartPage.save();

                //DynaShopAPI.openStartPageSettingGui(player, shopSlotIndex);
                DynaShopAPI.openStartPage(player);
            }
            else if (isRight && isShift)
            {
                DynaShopAPI.openItemPalette(player, uiSubType, shopName, shopSlotIndex, 1, GetItemLastName(item));
            }
        }
    }

    private void OnClickUserItem(boolean isLeft, boolean isRight, ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR)
            return;

        // 0 == Shop
        if (uiSubType == 0)
        {
            if (isLeft)
            {
                DSItem dsItem = new DSItem(item, 10, 10, 0.0001, -1, 10000, 10000);
                DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, 0, dsItem);
            } else if (isRight)
            {
                DSItem temp = new DSItem(item, -1, -1, -1, -1, -1, -1);
                ShopUtil.addItemToShop(shopName, shopSlotIndex, temp);

                DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
            }
        }
        // 1 == StartPage
        else
        {
            if (isLeft)
            {
                StartPage.ccStartPage.get().set("Buttons." + shopSlotIndex + ".icon", item.getType().toString());
                ItemMeta meta = item.getItemMeta();
                if(meta != null)
                {
                    meta.setDisplayName(null);
                    meta.setLore(null);
                    StartPage.ccStartPage.get().set("Buttons." + shopSlotIndex + ".itemStack", meta);
                }

                StartPage.ccStartPage.save();

                //DynaShopAPI.openStartPageSettingGui(player, shopSlotIndex);
                DynaShopAPI.openStartPage(player);
            }
        }
    }

    private ItemStack CreateItemStackWithRef(ItemStack ref)
    {
        ItemStack newItem = new ItemStack(ref.getType());

        if (ref.getType() == Material.POTION || ref.getType() == Material.LINGERING_POTION || ref.getType() == Material.SPLASH_POTION)
        {
            PotionMeta pmRef = (PotionMeta)ref.getItemMeta();
            PotionMeta pmCopy = (PotionMeta)newItem.getItemMeta();

            pmCopy.setBasePotionData(pmRef.getBasePotionData());
            newItem.setItemMeta(pmCopy);
        }

        return newItem;
    }
}
