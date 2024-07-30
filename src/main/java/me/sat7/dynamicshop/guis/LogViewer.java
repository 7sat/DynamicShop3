package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LogUtil;
import me.sat7.dynamicshop.utilities.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class LogViewer extends InGameUI
{
    public LogViewer()
    {
        uiType = InGameUI.UI_TYPE.LogViewer;
    }

    private int subState;
    private String selectedFileName;

    private final int TOGGLE_EXPAND = 0;
    private final int PAGE = 9;
    private final int CLOSE = 45;

    private Player player;
    private String shopName;
    private boolean isCollapsed;
    private int currentPage = 1;
    private int maxPage = 1;

    ArrayList<String> logData = null;

    public Inventory getGui(Player player, String shopName)
    {
        this.shopName = shopName;

        this.player = player;
        inventory = Bukkit.createInventory(player, 54, t(player, "LOG_VIEWER_TITLE") + "§7 | §8" + shopName);

        CreateFileSelectUI();

        return inventory;
    }

    private void CreateFileSelectUI()
    {
        ClearUI();

        ArrayList<String> files = LogUtil.GetLogFileList(shopName);
        int slotIdx = 0;
        for (String path : files)
        {
            CreateButton(slotIdx, Material.BOOK, path, t(player, "LOG_VIEWER.FILE_LORE"));
            slotIdx++;
            if (slotIdx > 45)
                break;
        }

        CreateCloseButton(player, CLOSE);
    }

    private void CreateLogViewerUI()
    {
        ClearUI();

        ShowLogData(currentPage);

        CreateToggleButton();
        CreatePageButton();

        CreateCloseButton(player, CLOSE);
    }

    private void CreateToggleButton()
    {
        String toggleName = isCollapsed ? t(null, "LOG_VIEWER.EXPAND") : t(null, "LOG_VIEWER.COLLAPSE");
        CreateButton(TOGGLE_EXPAND, Material.REDSTONE_BLOCK, toggleName, "");
    }

    private void CreatePageButton()
    {
        CreateButton(PAGE, InGameUI.GetPageButtonIconMat(), CreatePageButtonName(), CreatePageButtonLore(), currentPage);
    }

    private String CreatePageButtonName()
    {
        String pageString = t(null, "LOG_VIEWER.PAGE_TITLE");
        pageString = pageString.replace("{curPage}", String.valueOf(currentPage));
        pageString = pageString.replace("{maxPage}", String.valueOf(maxPage));
        return pageString;
    }

    private String CreatePageButtonLore()
    {
        return t(null, "LOG_VIEWER.PAGE_LORE");
    }

    private void ShowLogData(int page)
    {
        if (logData == null)
            logData = LogUtil.LoadDataFromCSV(shopName, selectedFileName);

        int dataPerPage = isCollapsed ? 48 : 16;
        maxPage = logData.size()/dataPerPage + 1;
        int idx = -((page - 1) * dataPerPage);

        for (String s : logData)
        {
            if (!(idx < dataPerPage && idx >= 0))
            {
                idx++;
                continue;
            }

            int slotIdx = idx;

            if (isCollapsed)
            {
                slotIdx += (slotIdx/8) + 1;
            } else
            {
                slotIdx++;
                if (slotIdx > 8)
                    slotIdx += 19;
            }

            // 0DataIdx, 1User, 2Item, 3Amount, 4Date, 5Time, 6Currency, 7Price
            String[] data = s.split(",");
            Material mat = Material.getMaterial(data[2]);
            if (mat == null)
                mat = Material.DIRT;

            Player p = null;
            if (!data[1].equals(shopName))
                p = Bukkit.getOfflinePlayer(data[1]).getPlayer();

            int amount = Integer.parseInt(data[3]);
            boolean sell;
            Material sellBuy;

            if (amount < 0)
            {
                sell = true;
                sellBuy = Material.GREEN_STAINED_GLASS;
            } else
            {
                sell = false;
                sellBuy = Material.RED_STAINED_GLASS;
            }

            String itemTitle = "§7#" + data[0];
            ArrayList<String> itemLore = new ArrayList<>();

            if (sell)
            {
                itemLore.add("§a" + data[1] + " ▶ " + shopName);
            } else
            {
                itemLore.add("§c" + shopName + " ▶ " + data[1]);
            }

            itemLore.add("§f" + ItemsUtil.getBeautifiedName(data[2]) + " x" + Math.abs(amount));
            double price = Double.parseDouble(data[7]);
            itemLore.add(t(p,"LOG_VIEWER.PRICE") + n(price) + " (" + n(price / Math.abs(amount)) + "/ea)");
            itemLore.add(" ");
            itemLore.add(t(p, "LOG_VIEWER.DATE") + data[4]);
            itemLore.add(t(p,"LOG_VIEWER.TIME") + data[5].replace(".", ":"));
            itemLore.add(t(p,"LOG_VIEWER.CURRENCY") + data[6]);

            int amountForUI = amount;
            if (amountForUI < 0)
                amountForUI *= -1;
            amountForUI = MathUtil.Clamp(amountForUI, 1, 64);

            if (isCollapsed)
            {
                CreateButton(slotIdx, mat, itemTitle, itemLore, amountForUI);
                ItemStack is = inventory.getItem(slotIdx);
                ItemMeta im = is.getItemMeta();
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                im.addItemFlags(ItemFlag.HIDE_DYE);
                is.setItemMeta(im);
            } else
            {
                CreateButton(slotIdx, Material.PLAYER_HEAD, itemTitle, itemLore);
                CreateButton(slotIdx + 9, mat, itemTitle, itemLore, amountForUI);
                CreateButton(slotIdx + 18, sellBuy, itemTitle, itemLore);

                ItemStack is = inventory.getItem(slotIdx + 9);
                ItemMeta im = is.getItemMeta();
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                im.addItemFlags(ItemFlag.HIDE_DYE);
                is.setItemMeta(im);

                int finalSlotIdx = slotIdx;
                Player finalP = p;
                Bukkit.getScheduler().runTaskAsynchronously(DynamicShop.plugin, () -> LoadAndSetSkin(finalP, finalSlotIdx));
            }

            idx++;
        }
    }

    private void LoadAndSetSkin(Player p, int idx)
    {
        if (p == null)
            return;

        ItemStack tempIs = inventory.getItem(idx);
        SkullMeta meta = (SkullMeta) tempIs.getItemMeta();
        try
        {
            meta.setOwningPlayer(p);
        } catch (Exception ignore)
        {
            return;
        }

        tempIs.setItemMeta(meta);
        inventory.setItem(idx, tempIs);
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        if (subState == 0)
        {
            if (e.getSlot() == CLOSE)
            {
                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            else if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BOOK)
            {
                if (e.isLeftClick())
                {
                    subState = 1;
                    currentPage = 1;
                    selectedFileName = e.getCurrentItem().getItemMeta().getDisplayName();
                    logData = null;

                    CreateLogViewerUI();
                }
                else if(e.isRightClick() && e.isShiftClick())
                {
                    selectedFileName = e.getCurrentItem().getItemMeta().getDisplayName();
                    LogUtil.DeleteLogFile(shopName, selectedFileName);
                    logData = null;

                    CreateFileSelectUI();
                }
            }
        }
        else
        {
            if (e.getSlot() == CLOSE)
            {
                subState = 0;

                CreateFileSelectUI();
            } else if (e.getSlot() == TOGGLE_EXPAND)
            {
                isCollapsed = !isCollapsed;
                currentPage = 1;

                ClearUI();
                ShowLogData(currentPage);
                CreateToggleButton();
                CreatePageButton();
                CreateCloseButton(player, CLOSE);
            }
            else if (e.getSlot() == PAGE)
            {
                if (e.isLeftClick())
                {
                    if (currentPage <= 1)
                        return;

                    currentPage--;
                }
                else if(e.isRightClick())
                {
                    if (currentPage >= maxPage)
                        return;

                    currentPage++;
                }

                ClearUI();
                ShowLogData(currentPage);
                CreateToggleButton();
                CreatePageButton();
                CreateCloseButton(player, CLOSE);
            }
        }
    }

    private void ClearUI()
    {
        for (int i = 0; i < 54; i++)
        {
            inventory.setItem(i, null);
        }
    }
}
