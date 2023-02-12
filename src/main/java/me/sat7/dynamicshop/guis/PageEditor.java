package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.events.OnChat;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.transactions.Calc;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;
import static me.sat7.dynamicshop.utilities.ShopUtil.GetShopMaxPage;

public final class PageEditor extends InGameUI
{
    public PageEditor()
    {
        uiType = InGameUI.UI_TYPE.PageEditor;
    }

    private final int CLOSE = 45;
    private final int PAGE_SCROLL_DOWN = 46;
    private final int PAGE_BUTTON_START = 47;
    private final int PAGE_BUTTON_END = 52;
    private final int PAGE_SCROLL_UP = 53;

    private String shopName;
    private int page;

    public Inventory getGui(Player player, String shopName, int page)
    {
        this.shopName = shopName;
        this.page = page;

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        
        page = Clamp(page, 1, GetShopMaxPage(shopName));

        inventory = Bukkit.createInventory(player, 54, t(player, "PAGE_EDITOR_TITLE") + "§7 | §8" + shopName + "§7 | §8" + page + "/" + GetShopMaxPage(shopName));

        for (String s : data.get().getKeys(false))
        {
            try
            {
                // 현재 페이지에 해당하는 것들만 출력
                int idx = Integer.parseInt(s);
                idx -= ((page - 1) * 45);
                if (!(idx < 45 && idx >= 0)) continue;

                // 아이탬 생성
                String itemName = data.get().getString(s + ".mat"); // 메테리얼
                ItemStack itemStack = new ItemStack(Material.getMaterial(itemName), 1); // 아이탬 생성
                itemStack.setItemMeta((ItemMeta) data.get().get(s + ".itemStack")); // 저장된 메타 적용

                // 커스텀 메타 설정
                ItemMeta meta = itemStack.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();

                if (data.get().contains(s + ".value"))
                {
                    String stockStr;

                    if (data.get().getInt(s + ".stock") <= 0)
                    {
                        stockStr = "INF";
                    } else if (DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack"))
                    {
                        stockStr = t(player, "PAGE_EDITOR.STACKS").replace("{num}", n(data.get().getInt(s + ".stock") / 64));
                    } else
                    {
                        stockStr = n(data.get().getInt(s + ".stock"));
                    }

                    double buyPrice = Calc.getCurrentPrice(shopName, s, true);
                    double sellPrice = Calc.getCurrentPrice(shopName, s, false);

                    String tradeType = "default";
                    if (data.get().contains(s + ".tradeType"))
                        tradeType = data.get().getString(s + ".tradeType");

                    if (!tradeType.equalsIgnoreCase("SellOnly"))
                        lore.add(t(player, "PAGE_EDITOR.PRICE").replace("{num}", n(buyPrice)));

                    if (!tradeType.equalsIgnoreCase("BuyOnly"))
                        lore.add(t(player, "PAGE_EDITOR.SELL_PRICE").replace("{num}", n(sellPrice)));

                    if (data.get().getInt(s + ".stock") <= 0 || data.get().getInt(s + ".median") <= 0)
                    {
                        lore.add(t(player, "PAGE_EDITOR.STATIC_PRICE"));
                    }
                    lore.add(t(player, "PAGE_EDITOR.STOCK").replace("{num}", stockStr));
                }
                // 장식용
                else
                {
                    meta.setDisplayName(" ");
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                }
                lore.add("§8#" + s);

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                inventory.setItem(idx, itemStack);
            } catch (Exception e)
            {
                if (!s.equalsIgnoreCase("Options"))
                {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "ERR. incomplete data. check shop.yml");
                }
            }
        }

        CreateCloseButton(player, CLOSE);
        SetupPageButtons(shopName, page);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        int pageButtonCount = PAGE_BUTTON_END - PAGE_BUTTON_START + 1;

        // 닫기 버튼
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openShopGui(player, shopName, page);
        }
        if (e.getCurrentItem() != null)
        {
            if (e.getSlot() == PAGE_SCROLL_DOWN)
            {
                if (e.isLeftClick())
                    DynaShopAPI.openPageEditor(player, shopName, page - pageButtonCount);
            } else if (e.getSlot() >= PAGE_BUTTON_START && e.getSlot() <= PAGE_BUTTON_END)
            {
                int selectedPage = e.getCurrentItem().getAmount();

                if (e.isLeftClick())
                {
                    if (e.isShiftClick())
                    {
                        InsertPage(player, shopName, selectedPage);
                    } else
                    {
                        DynaShopAPI.openPageEditor(player, shopName, selectedPage);
                    }
                } else if (e.isRightClick())
                {
                    if (e.isShiftClick())
                    {
                        DeletePage(player, shopName, selectedPage);
                    } else
                    {
                        SwapPage(player, shopName, selectedPage);
                    }
                }
            } else if (e.getSlot() == PAGE_SCROLL_UP)
            {
                if (e.isLeftClick())
                    DynaShopAPI.openPageEditor(player, shopName, page + pageButtonCount);
            }
        }
    }

    private void SetupPageButtons(String shopName, int page)
    {
        int pageButtonCount = PAGE_BUTTON_END - PAGE_BUTTON_START + 1;
        int temp = ((page - 1) / pageButtonCount) * pageButtonCount;
        int maxPage = GetShopMaxPage(shopName);

        for (int i = 0; i <= pageButtonCount; i++)
        {
            int pageNum = temp + i + 1;
            if (pageNum > maxPage)
                break;

            String pageLore = ShopUtil.IsPageEmpty(shopName, pageNum) ? t(null, "PAGE_EDITOR.EMPTY") + "\n" : "";
            pageLore += t(null, "PAGE_EDITOR.PAGE_LORE_PREMIUM");

            CreateButton(PAGE_BUTTON_START + i, InGameUI.GetPageButtonIconMat(), pageNum + "", pageLore, pageNum);
        }

        if (temp > 0)
            CreateButton(PAGE_SCROLL_DOWN, InGameUI.GetPageButtonIconMat(), t(null, "PAGE_EDITOR.PREV"), "");

        if (temp + pageButtonCount < maxPage)
            CreateButton(PAGE_SCROLL_UP, InGameUI.GetPageButtonIconMat(), t(null, "PAGE_EDITOR.NEXT"), "");
    }

    private void SwapPage(Player player, String shopName, int selectedPage)
    {
        if (DynamicShop.userTempData.get(player.getUniqueId()).contains("swapPage"))
        {
            String[] pageSwapData = DynamicShop.userTempData.get(player.getUniqueId()).split("/");

            int pageTarget = Integer.parseInt(pageSwapData[1]);
            if (selectedPage != pageTarget)
            {
                boolean result = ShopUtil.SwapPage(shopName, selectedPage, pageTarget);
                DynamicShop.userTempData.put(player.getUniqueId(), "");

                DynaShopAPI.openPageEditor(player, shopName, selectedPage);

                if (result)
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "PAGE_EDITOR.PAGE_SWAP_SUCCESS"));
                else
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "PAGE_EDITOR.PAGE_SWAP_FAIL"));
            }
        } else
        {
            DynamicShop.userTempData.put(player.getUniqueId(), "swapPage/" + selectedPage);
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "PAGE_EDITOR.PAGE_SWAP_SELECTED"));
        }
    }

    private void DeletePage(Player player, String shopName, int page)
    {
        if (!player.hasPermission(P_ADMIN_SHOP_EDIT))
        {
            return;
        }

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        if (data.get().getInt("Options.page") > 1)
        {
            if (ShopUtil.IsPageEmpty(shopName, page))
            {
                ShopUtil.deleteShopPage(shopName, page);

                int openPage = Clamp(page, 1, ShopUtil.GetShopMaxPage(shopName));
                DynaShopAPI.openPageEditor(player, shopName, openPage);
            } else
            {
                ShopUtil.closeInventoryWithDelay(player);

                DynamicShop.userInteractItem.put(player.getUniqueId(), shopName + "/" + page); // 삭제 확인을 위해 필요.
                DynamicShop.userTempData.put(player.getUniqueId(), "waitforPageDelete");
                OnChat.WaitForInput(player);

                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.DELETE_CONFIRM"));
            }
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CANT_DELETE_LAST_PAGE"));
        }
    }

    private void InsertPage(Player player, String shopName, int page)
    {
        if(ShopUtil.GetShopMaxPage(shopName) >= 20)
            return;

        ShopUtil.insetShopPage(shopName, page);
        DynaShopAPI.openPageEditor(player, shopName, page);
    }
}
