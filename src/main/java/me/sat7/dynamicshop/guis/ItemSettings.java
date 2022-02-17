package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.models.DSItem;
import me.sat7.dynamicshop.transactions.Calc;

import static me.sat7.dynamicshop.DynaShopAPI.df;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ItemSettings extends InGameUI
{
    public ItemSettings()
    {
        uiType = UI_TYPE.ItemSettings;
    }

    private final int SAMPLE_ITEM = 0;

    private final int DONE = 8;
    private final int CLOSE = 27;
    private final int RECOMMEND = 31;
    private final int REMOVE = 35;

    private final int BUY_VALUE = 2;
    private final int SELL_VALUE = 3;
    private final int MIN_VALUE = 4;
    private final int MAX_VALUE = 5;
    private final int MEDIAN = 6;
    private final int STOCK = 7;

    private final int RESET = 13;
    private final int ROUND_DOWN = 20;
    private final int DIVIDE = 21;
    private final int SHIFT = 22;
    private final int MULTIPLY = 23;
    private final int SET_TO_OTHER = 24;

    private Player player;
    private String shopName;
    private int shopSlotIndex;
    private DSItem dsItem;
    private int currentTab;

    private double buyValue;
    private double sellValue;
    private double minValue;
    private double maxValue;
    private int median;
    private int stock;

    private boolean oldSbSame;

    public Inventory getGui(Player player, String shopName, int shopSlotIndex, int tab, DSItem dsItem)
    {
        this.player = player;
        this.shopName = shopName;
        this.shopSlotIndex = shopSlotIndex;
        this.dsItem = dsItem;
        this.currentTab = Clamp(tab, BUY_VALUE, STOCK);

        inventory = Bukkit.createInventory(player, 36, t("ITEM_SETTING_TITLE") + "§7 | §8" + shopName);

        String buyValueStr = t("ITEM_SETTING.VALUE_BUY") + dsItem.getBuyValue();
        String sellValueStr = t("ITEM_SETTING.VALUE_SELL") + dsItem.getSellValue();
        String priceMinStr = t("ITEM_SETTING.PRICE_MIN") + dsItem.getMinPrice();
        String priceMaxStr = t("ITEM_SETTING.PRICE_MAX") + dsItem.getMaxPrice();
        String medianStr = t("ITEM_SETTING.MEDIAN") + dsItem.getMedian();
        String stockStr = t("ITEM_SETTING.STOCK") + dsItem.getStock();

        String sellValueLore = (dsItem.getBuyValue() != dsItem.getSellValue()) ? "§7(" + t("ITEM_SETTING.TAX_IGNORED") + ")" : "";
        String medianLore = t("ITEM_SETTING.MEDIAN_HELP");
        if (dsItem.getMedian() <= 0)
            medianLore += "§7(" + t("ITEM_SETTING.STATIC_PRICE") + ")";
        String stockLore = (dsItem.getStock() <= 0) ? "§7(" + t("ITEM_SETTING.INF_STOCK") + ")" : "";
        String maxPriceLore = (dsItem.getMaxPrice() <= 0) ? "§7(" + t("ITEM_SETTING.UNLIMITED") + ")" : "";

        Material red = Material.RED_STAINED_GLASS_PANE;
        Material gray = Material.GRAY_STAINED_GLASS_PANE;
        Material black = Material.BLACK_STAINED_GLASS_PANE;
        Material white = Material.WHITE_STAINED_GLASS_PANE;
        Material yellow = Material.YELLOW_STAINED_GLASS_PANE;

        CreateButton(BUY_VALUE, (currentTab == BUY_VALUE) ? red : black, buyValueStr, "");
        CreateButton(SELL_VALUE, (currentTab == SELL_VALUE) ? red : gray, sellValueStr, sellValueLore);
        CreateButton(MIN_VALUE, (currentTab == MIN_VALUE) ? red : gray, priceMinStr, "");
        CreateButton(MAX_VALUE, (currentTab == MAX_VALUE) ? red : gray, priceMaxStr, maxPriceLore);
        CreateButton(MEDIAN, (currentTab == MEDIAN) ? red : black, medianStr, medianLore);
        CreateButton(STOCK, (currentTab == STOCK) ? red : black, stockStr, stockLore);

        CreateButton(SHIFT, black, "Shift = x5", "");

        // 조절버튼
        if (dsItem.getBuyValue() == dsItem.getSellValue()) sellValueStr = "§7" + ChatColor.stripColor(sellValueStr);
        if (dsItem.getMinPrice() <= 0.01) priceMinStr = "§7" + ChatColor.stripColor(priceMinStr);
        if (dsItem.getMaxPrice() <= 0)
            priceMaxStr = "§7" + ChatColor.stripColor(t("ITEM_SETTING.PRICE_MAX") + t("ITEM_SETTING.UNLIMITED"));

        if (currentTab == BUY_VALUE) buyValueStr = "§3>" + buyValueStr;
        else if (currentTab == SELL_VALUE) sellValueStr = "§3>" + sellValueStr;
        else if (currentTab == MIN_VALUE) priceMinStr = "§3>" + priceMinStr;
        else if (currentTab == MAX_VALUE) priceMaxStr = "§3>" + priceMaxStr;
        else if (currentTab == MEDIAN) medianStr = "§3>" + medianStr;
        else if (currentTab == STOCK) stockStr = "§3>" + stockStr;

        if (dsItem.getMedian() <= 0)
            medianStr = medianStr + "§7(" + t("ITEM_SETTING.STATIC_PRICE") + ")";
        if (dsItem.getStock() <= 0) stockStr = stockStr + "§7(" + t("ITEM_SETTING.INF_STOCK") + ")";

        ArrayList<String> editBtnLore = new ArrayList<>();
        editBtnLore.add("§3§m                       ");
        editBtnLore.add(buyValueStr);
        editBtnLore.add(sellValueStr);
        editBtnLore.add(priceMinStr);
        editBtnLore.add(priceMaxStr);
        editBtnLore.add(medianStr);
        editBtnLore.add(stockStr);
        editBtnLore.add("§3§m                       ");

        double buyPrice;
        double sellPrice;
        if (dsItem.getMedian() <= 0 || dsItem.getStock() <= 0)
        {
            buyPrice = dsItem.getBuyValue();
            if (dsItem.getBuyValue() != dsItem.getSellValue())
            {
                editBtnLore.add("§7" + ChatColor.stripColor(t("ITEM_SETTING.TAX_IGNORED")));
                sellPrice = dsItem.getSellValue();
            } else
            {
                String taxStr = "§7" + ChatColor.stripColor(t("TAX.SALES_TAX")) + ": ";
                taxStr += Calc.getTaxRate(shopName) + "%";
                editBtnLore.add(taxStr);
                sellPrice = buyPrice - ((buyPrice / 100.0) * Calc.getTaxRate(shopName));
            }
        } else
        {
            buyPrice = (dsItem.getBuyValue() * dsItem.getMedian()) / dsItem.getStock();
            if (dsItem.getBuyValue() != dsItem.getSellValue()) // 판매가 별도설정
            {
                editBtnLore.add("§7" + ChatColor.stripColor(t("ITEM_SETTING.TAX_IGNORED")));
                sellPrice = (dsItem.getSellValue() * dsItem.getMedian()) / dsItem.getStock();
            } else
            {
                String taxStr = "§7" + ChatColor.stripColor(t("TAX.SALES_TAX")) + ": ";
                FileConfiguration config = ShopUtil.shopConfigFiles.get(shopName).get();
                if (config.contains("Options.SalesTax"))
                {
                    taxStr += config.getInt("Options.SalesTax") + "%";
                    sellPrice = buyPrice - ((buyPrice / 100.0) * config.getInt("Options.SalesTax"));
                } else
                {
                    taxStr += ConfigUtil.getCurrentTax() + "%";
                    sellPrice = buyPrice - ((buyPrice / 100.0) * ConfigUtil.getCurrentTax());
                }
                sellPrice = (Math.round(sellPrice * 100) / 100.0);

                editBtnLore.add(taxStr);
            }
        }

        editBtnLore.add(t("ITEM_SETTING.BUY").replace("{num}", df.format(buyPrice)));
        editBtnLore.add(t("ITEM_SETTING.SELL").replace("{num}", df.format(sellPrice)));

        CreateButton(RESET, white, "Reset", editBtnLore);
        CreateButton(ROUND_DOWN, white, t("ITEM_SETTING.ROUND_DOWN"), editBtnLore);
        CreateButton(DIVIDE, white, "/2", editBtnLore);
        CreateButton(MULTIPLY, white, "x2", editBtnLore);

        if (currentTab <= MAX_VALUE)
        {
            CreateButton(9, white, "-100", editBtnLore);
            CreateButton(10, white, "-10", editBtnLore);
            CreateButton(11, white, "-1", editBtnLore);
            CreateButton(12, white, "-0.1", editBtnLore);
            CreateButton(14, white, "+0.1", editBtnLore);
            CreateButton(15, white, "+1", editBtnLore);
            CreateButton(16, white, "+10", editBtnLore);
            CreateButton(17, white, "+100", editBtnLore);

            if (currentTab >= SELL_VALUE) CreateButton(SET_TO_OTHER, yellow, t("ITEM_SETTING.SET_TO_VALUE"), editBtnLore);
        } else
        {
            CreateButton(9, white, "-1000", editBtnLore);
            CreateButton(10, white, "-100", editBtnLore);
            CreateButton(11, white, "-10", editBtnLore);
            CreateButton(12, white, "-1", editBtnLore);
            CreateButton(14, white, "+1", editBtnLore);
            CreateButton(15, white, "+10", editBtnLore);
            CreateButton(16, white, "+100", editBtnLore);
            CreateButton(17, white, "+1000", editBtnLore);

            if (currentTab == MEDIAN) CreateButton(SET_TO_OTHER, yellow, t("ITEM_SETTING.SET_TO_STOCK"), editBtnLore);
            else if (currentTab == STOCK) CreateButton(SET_TO_OTHER, yellow, t("ITEM_SETTING.SET_TO_MEDIAN"), editBtnLore);
        }

        inventory.setItem(SAMPLE_ITEM, dsItem.getItemStack()); // 아이탬 견본

        double worth = TryGetWorth(dsItem.getItemStack().getType().name());
        String recommendLore;
        if (worth == 0)
        {
            recommendLore = t("ERR.NO_RECOMMEND_DATA");
        } else
        {
            int sugMid = ShopUtil.CalcRecommendedMedian(worth, DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer"));

            String worthChanged = (dsItem.getBuyValue() == worth) ? " ▶§f " : " ▶§a ";
            String worthChanged2 = (dsItem.getSellValue() == worth) ? " ▶§f " : " ▶§a ";
            //String minChanged = (dsItem.getMinPrice() == 0.01) ? " ▶§f " : " ▶§a ";
            //String maxChanged = (dsItem.getMaxPrice() == -1) ? " ▶§f " : " ▶§a ";
            String medianChanged = (dsItem.getMedian() == sugMid) ? " ▶§f " : " ▶§a ";
            String stockChanged = (dsItem.getStock() == sugMid) ? " ▶§f " : " ▶§a ";

            recommendLore = t("ITEM_SETTING.VALUE_BUY") + "\n"
                    + "§7 " + dsItem.getBuyValue() + worthChanged + worth + "\n"
                    + t("ITEM_SETTING.VALUE_SELL") + "\n"
                    + "§7 " + dsItem.getSellValue() + worthChanged2 + worth + "\n"
                    + t("ITEM_SETTING.PRICE_MIN") + "\n"
                    //+ "§7 " + dsItem.getMinPrice() + minChanged + 0.01 + "\n"
                    //+ t("ITEM_SETTING.PRICE_MAX") + "\n"
                    //+ "§7 " + dsItem.getMaxPrice() + maxChanged + -1 + "\n"
                    //+ t("ITEM_SETTING.MEDIAN") + "\n"
                    + "§7 " + dsItem.getMedian() + medianChanged + sugMid + "\n"
                    + t("ITEM_SETTING.STOCK") + "\n"
                    + "§7 " + dsItem.getStock() + stockChanged + sugMid;
        }

        CreateButton(RECOMMEND, Material.NETHER_STAR, t("ITEM_SETTING.RECOMMEND"), recommendLore); // 추천 버튼
        CreateButton(DONE, Material.STRUCTURE_VOID, t("ITEM_SETTING.DONE"), t("ITEM_SETTING.DONE_LORE")); // 완료 버튼
        CreateButton(CLOSE, Material.BARRIER, t("ITEM_SETTING.CLOSE"), t("ITEM_SETTING.CLOSE_LORE")); // 닫기 버튼
        CreateButton(REMOVE, Material.BONE, t("ITEM_SETTING.REMOVE"), t("ITEM_SETTING.REMOVE_LORE")); // 삭제 버튼

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        this.player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null)
            return;

        buyValue = dsItem.getBuyValue();
        sellValue = dsItem.getSellValue();
        minValue = dsItem.getMinPrice();
        if(minValue <= 0) minValue = 0.01;
        maxValue = dsItem.getMaxPrice();
        if(maxValue <= 0) maxValue = -1;
        median = dsItem.getMedian();
        stock = dsItem.getStock();

        oldSbSame = sellValue == buyValue;

        if (e.getSlot() == CLOSE) DynaShopAPI.openItemPalette(player, shopName, shopSlotIndex, 1, "");
        else if (e.getSlot() == REMOVE) RemoveItem();
        else if (e.getSlot() == RECOMMEND) SetToRecommend();
        else if (e.getSlot() >= BUY_VALUE && e.getSlot() <= STOCK) ChangeTab(e.getSlot());
        else if (e.getSlot() == RESET) Reset();
        else if (e.getSlot() >= 9 && e.getSlot() < 18) PlusMinus(e.isShiftClick(), e.getCurrentItem()); // RESET 이 13인것에 주의
        else if (e.getSlot() == DIVIDE) Divide(e.isShiftClick());
        else if (e.getSlot() == MULTIPLY) Multiply(e.isShiftClick());
        else if (e.getSlot() == ROUND_DOWN) RoundDown();
        else if (e.getSlot() == SET_TO_OTHER) SetEqualToOther();
        else if (e.getSlot() == DONE) SaveSetting();
    }

    private void SaveSetting()
    {
        // 유효성 검사
        if (maxValue > 0 && buyValue > maxValue)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (minValue > 0 && buyValue < minValue)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (maxValue > 0 && sellValue > maxValue)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (minValue > 0 && sellValue < minValue)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (maxValue > 0 && minValue > 0 && minValue >= maxValue)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.MAX_LOWER_THAN_MIN"));
            return;
        }

        int existSlot = ShopUtil.findItemFromShop(shopName, inventory.getItem(SAMPLE_ITEM));
        if (-1 != existSlot)
        {
            ShopUtil.editShopItem(shopName, existSlot, buyValue, sellValue, minValue, maxValue, median, stock);
            DynaShopAPI.openShopGui(player, shopName, existSlot / 45 + 1);
            SoundUtil.playerSoundEffect(player, "addItem");
        } else
        {
            int idx = -1;
            try{
                idx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);
            }catch (Exception ignore){}

            if (idx != -1)
            {
                ShopUtil.addItemToShop(shopName, idx, inventory.getItem(SAMPLE_ITEM), buyValue, sellValue, minValue, maxValue, median, stock);
                DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
                SoundUtil.playerSoundEffect(player, "addItem");
            }
        }
    }

    private void RemoveItem()
    {
        int idx = ShopUtil.findItemFromShop(shopName, inventory.getItem(SAMPLE_ITEM));
        if (idx != -1)
        {
            ShopUtil.removeItemFromShop(shopName, idx);
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_DELETED"));
            DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
            SoundUtil.playerSoundEffect(player, "deleteItem");
        }
    }

    private void SetToRecommend()
    {
        double worth = TryGetWorth(dsItem.getItemStack().getType().name());

        if (worth == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_RECOMMEND_DATA"));
        } else
        {
            int numberOfPlayer = DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer");
            int sugMid = ShopUtil.CalcRecommendedMedian(worth, numberOfPlayer);

            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.RECOMMEND_APPLIED").replace("{playerNum}", numberOfPlayer + ""));

            DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, currentTab, inventory.getItem(SAMPLE_ITEM),
                    worth, worth, minValue, maxValue, sugMid, sugMid);
        }
    }

    private double TryGetWorth(String itemName)
    {
        double worth = WorthUtil.ccWorth.get().getDouble(itemName);
        if (worth == 0)
        {
            itemName = itemName.replace("-", "");
            itemName = itemName.replace("_", "");
            itemName = itemName.toLowerCase();

            worth = WorthUtil.ccWorth.get().getDouble(itemName);
        }

        return worth;
    }

    private void ChangeTab(int tabIndex)
    {
        DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, tabIndex, dsItem);
    }

    private void Reset()
    {
        if (currentTab == BUY_VALUE) buyValue = 10;
        else if (currentTab == SELL_VALUE) sellValue = 10;
        else if (currentTab == MIN_VALUE) minValue = 0.01;
        else if (currentTab == MAX_VALUE) maxValue = -1;
        else if (currentTab == MEDIAN) median = 10000;
        else if (currentTab == STOCK) stock = 10000;

        RefreshWindow();
    }

    private void PlusMinus(boolean isShift, ItemStack clickedButton)
    {
        String s = clickedButton.getItemMeta().getDisplayName();
        double editNum = Double.parseDouble(s);
        if (isShift) editNum *= 5f;

        if (currentTab == BUY_VALUE)
        {
            buyValue += editNum;
            if (buyValue < 0.01) buyValue = 0.01f;

            if(oldSbSame)
                sellValue = buyValue;
        } else if (currentTab == SELL_VALUE)
        {
            sellValue += editNum;
            if (sellValue < 0.01) sellValue = 0.01f;
        } else if (currentTab == MIN_VALUE)
        {
            minValue += editNum;
            if (minValue < 0.01) minValue = 0.01f;
        } else if (currentTab == MAX_VALUE)
        {
            if (maxValue <= 0 && editNum > 0)
                maxValue = editNum;
            else
            {
                maxValue += editNum;
                if (maxValue < 0.01)
                    maxValue = -1;
            }
        } else if (currentTab == MEDIAN)
        {
            if(median <= 0 && editNum > 0)
            {
                median = (int)editNum;
            }
            else
            {
                median += editNum;
                if(median < 1)
                    median = -1;
            }
        } else if (currentTab == STOCK)
        {
            if (stock <= 0 && editNum > 0)
            {
                stock = (int)editNum;
            }
            else
            {
                stock += editNum;
                if(stock < 1)
                {
                    stock = -1;
                    median = -1;
                }
            }
        }

        RefreshWindow();
    }

    private void Divide(boolean isShift)
    {
        int div = 2;
        if (isShift) div = 10;

        if (currentTab == BUY_VALUE)
        {
            buyValue /= div;
            if(buyValue < 0.01) buyValue = 0.01;

            if(oldSbSame)
                sellValue = buyValue;
        } else if (currentTab == SELL_VALUE)
        {
            sellValue /= div;
            if(sellValue < 0.01) sellValue = 0.01;
        } else if (currentTab == MIN_VALUE)
        {
            minValue /= div;
            if(minValue < 0.01) minValue = 0.01;
        } else if (currentTab == MAX_VALUE)
        {
            maxValue /= div;
            if(maxValue < 0.01) maxValue = 0.01;
        } else if (currentTab == MEDIAN)
        {
            median /= div;
            if(median < 1) median = 1;
        } else if (currentTab == STOCK)
        {
            stock /= div;
            if(stock < 1) stock = 1;
        }

        RefreshWindow();
    }

    private void Multiply(boolean isShift)
    {
        int mul = 2;
        if (isShift) mul = 10;

        if (currentTab == BUY_VALUE)
        {
            buyValue *= mul;

            if(oldSbSame)
                sellValue = buyValue;
        }
        else if (currentTab == SELL_VALUE) sellValue *= mul;
        else if (currentTab == MIN_VALUE) minValue *= mul;
        else if (currentTab == MAX_VALUE) maxValue *= mul;
        else if (currentTab == MEDIAN) median *= mul;
        else if (currentTab == STOCK) stock *= mul;

        RefreshWindow();
    }

    private void RoundDown()
    {
        if (currentTab == BUY_VALUE)
        {
            buyValue = MathUtil.RoundDown(buyValue);

            if(oldSbSame)
                sellValue = buyValue;
        }
        else if (currentTab == SELL_VALUE) sellValue = MathUtil.RoundDown(sellValue);
        else if (currentTab == MIN_VALUE) minValue = MathUtil.RoundDown(minValue);
        else if (currentTab == MAX_VALUE) maxValue = MathUtil.RoundDown(maxValue);
        else if (currentTab == MEDIAN) median = MathUtil.RoundDown(median);
        else if (currentTab == STOCK) stock = MathUtil.RoundDown(stock);

        RefreshWindow();
    }

    private void SetEqualToOther()
    {
        if (currentTab == SELL_VALUE) sellValue = buyValue;
        else if (currentTab == MIN_VALUE) minValue = buyValue;
        else if (currentTab == MAX_VALUE) maxValue = buyValue;
        else if (currentTab == MEDIAN) median = stock;
        else if (currentTab == STOCK) stock = median;

        RefreshWindow();
    }

    private void RefreshWindow()
    {
        DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, currentTab, inventory.getItem(SAMPLE_ITEM), buyValue, sellValue, minValue, maxValue, median, stock);
        SoundUtil.playerSoundEffect(player, "editItem");
    }
}
