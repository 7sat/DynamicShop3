package me.sat7.dynamicshop.guis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ItemSettings extends InGameUI
{
    public ItemSettings()
    {
        uiType = UI_TYPE.ItemSettings;
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

    private final int SAMPLE_ITEM = 0;

    private final int DONE = 8;
    private final int CLOSE = 45;
    private final int TRADE_LIMIT_AMOUNT = 46;
    private final int TRADE_LIMIT_INTERVAL = 47;
    private final int TRADE_LIMIT_INTERVAL_TIMER = 48;
    private final int RECOMMEND = 49;
    private final int DISCOUNT = 50;
    private final int REMOVE = 53;

    private final int BUY_VALUE = 1;
    private final int SELL_VALUE = 2;
    private final int MIN_VALUE = 3;
    private final int MAX_VALUE = 4;
    private final int MEDIAN = 5;
    private final int STOCK = 6;
    private final int MAX_STOCK = 7;
    private final int TAB_START = BUY_VALUE;
    private final int TAB_END = MAX_STOCK;

    private final int RESET = 22;
    private final int ROUND_DOWN = 29;
    private final int DIVIDE = 30;
    private final int SHIFT = 31;
    private final int MULTIPLY = 32;
    private final int SET_TO_OTHER = 33;

    private Player player;
    private String shopName;
    private int shopSlotIndex;
    private DSItem dsItem;
    private int currentTab;

    private int timerOffset;

    private boolean oldSbSame;

    public Inventory getGui(Player player, String shopName, int shopSlotIndex, int tab, DSItem dsItem, int timerOffset)
    {
        this.player = player;
        this.shopName = shopName;
        this.shopSlotIndex = shopSlotIndex;
        this.dsItem = dsItem;
        this.currentTab = Clamp(tab, TAB_START, TAB_END);
        this.timerOffset = timerOffset;

        inventory = Bukkit.createInventory(player, 54, t(player, "ITEM_SETTING_TITLE") + "§7 | §8" + shopName);

        String buyValueStr = t(null, "ITEM_SETTING.VALUE_BUY") + n(dsItem.getBuyValue());
        String sellValueStr = t(null, "ITEM_SETTING.VALUE_SELL") + n(dsItem.getSellValue());
        String priceMinStr = t(null, "ITEM_SETTING.PRICE_MIN") + n(dsItem.getMinPrice());
        String priceMaxStr = t(null, "ITEM_SETTING.PRICE_MAX") + n(dsItem.getMaxPrice());
        String medianStr = t(null, "ITEM_SETTING.MEDIAN") + n(dsItem.getMedian());
        String stockStr = t(null, "ITEM_SETTING.STOCK") + n(dsItem.getStock());
        String maxStockStr = t(null, "ITEM_SETTING.MAX_STOCK") + n(dsItem.getMaxStock());

        String sellValueLore = (dsItem.getBuyValue() != dsItem.getSellValue()) ? "§8(" + t(null, "ITEM_SETTING.TAX_IGNORED") + ")" : "";
        String medianLore = (dsItem.getMedian() <= 0) ? "§8(" + t(null, "ITEM_SETTING.STATIC_PRICE") + ")\n" : "";
        medianLore += t(null, "ITEM_SETTING.MEDIAN_HELP");
        String stockLore = (dsItem.getStock() <= 0) ? "§8(" + t(null, "ITEM_SETTING.INF_STOCK") + ")" : "";
        String maxPriceLore = (dsItem.getMaxPrice() <= 0) ? "§8(" + t(null, "ITEM_SETTING.UNLIMITED") + ")" : "";
        String maxStockLore = (dsItem.getMaxStock() <= 0) ? "§8(" + t(null, "ITEM_SETTING.UNLIMITED") + ")" : "";
        maxStockLore += "\n" + t(null, "ITEM_SETTING.MAX_STOCK_LORE");

        Material red = Material.RED_STAINED_GLASS_PANE;
        Material gray = Material.GRAY_STAINED_GLASS_PANE;
        Material blue = Material.BLUE_STAINED_GLASS_PANE;
        Material white = Material.WHITE_STAINED_GLASS_PANE;
        Material yellow = Material.YELLOW_STAINED_GLASS_PANE;

        CreateButton(BUY_VALUE, (currentTab == BUY_VALUE) ? red : blue, buyValueStr, "");
        CreateButton(SELL_VALUE, (currentTab == SELL_VALUE) ? red : gray, sellValueStr, sellValueLore);
        CreateButton(MIN_VALUE, (currentTab == MIN_VALUE) ? red : gray, priceMinStr, "");
        CreateButton(MAX_VALUE, (currentTab == MAX_VALUE) ? red : gray, priceMaxStr, maxPriceLore);
        CreateButton(MEDIAN, (currentTab == MEDIAN) ? red : blue, medianStr, medianLore);
        CreateButton(STOCK, (currentTab == STOCK) ? red : blue, stockStr, stockLore);
        CreateButton(MAX_STOCK, (currentTab == MAX_STOCK) ? red : gray, maxStockStr, maxStockLore);

        CreateButton(SHIFT, Material.BLACK_STAINED_GLASS_PANE, "Shift = x5", "");

        // 조절버튼
        if (dsItem.getBuyValue() == dsItem.getSellValue())
            sellValueStr = "§8" + ChatColor.stripColor(sellValueStr);
        if (dsItem.getMinPrice() <= 0.01)
            priceMinStr = "§8" + ChatColor.stripColor(priceMinStr);
        if (dsItem.getMaxPrice() <= 0)
            priceMaxStr = "§8" + ChatColor.stripColor(priceMaxStr);
        if (dsItem.getMaxStock() <= 0)
            maxStockStr = "§8" + ChatColor.stripColor(maxStockStr);

        if (currentTab == BUY_VALUE) buyValueStr = "§3>" + buyValueStr;
        else if (currentTab == SELL_VALUE) sellValueStr = "§3>" + sellValueStr;
        else if (currentTab == MIN_VALUE) priceMinStr = "§3>" + priceMinStr;
        else if (currentTab == MAX_VALUE) priceMaxStr = "§3>" + priceMaxStr;
        else if (currentTab == MEDIAN) medianStr = "§3>" + medianStr;
        else if (currentTab == STOCK) stockStr = "§3>" + stockStr;
        else if (currentTab == MAX_STOCK) maxStockStr = "§3>" + maxStockStr;

        if (dsItem.getMaxPrice() <= 0)
            priceMaxStr = priceMaxStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.UNLIMITED")) + ")";
        if (dsItem.getMedian() <= 0)
            medianStr = medianStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.STATIC_PRICE")) + ")";
        if (dsItem.getStock() <= 0)
            stockStr = stockStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.INF_STOCK")) + ")";
        if (dsItem.getMaxStock() <= 0)
            maxStockStr = maxStockStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.UNLIMITED")) + ")";

        ArrayList<String> editBtnLore = new ArrayList<>();
        editBtnLore.add("§3§m                       ");
        editBtnLore.add(buyValueStr);
        editBtnLore.add(sellValueStr);
        editBtnLore.add(priceMinStr);
        editBtnLore.add(priceMaxStr);
        editBtnLore.add(medianStr);
        editBtnLore.add(stockStr);
        editBtnLore.add(maxStockStr);
        editBtnLore.add("§3§m                       ");

        double buyPrice;
        double sellPrice;
        if (dsItem.getMedian() <= 0 || dsItem.getStock() <= 0)
        {
            buyPrice = dsItem.getBuyValue();

            if (dsItem.getBuyValue() != dsItem.getSellValue())
            {
                editBtnLore.add("§7" + ChatColor.stripColor(t(null, "ITEM_SETTING.TAX_IGNORED")));
                sellPrice = dsItem.getSellValue();
            } else
            {
                String taxStr = "§7" + ChatColor.stripColor(t(null, "TAX.SALES_TAX")) + ": ";
                taxStr += Calc.getTaxRate(shopName) + "%";
                editBtnLore.add(taxStr);
                sellPrice = buyPrice - ((buyPrice / 100.0) * Calc.getTaxRate(shopName));
            }
        } else
        {
            buyPrice = (dsItem.getBuyValue() * dsItem.getMedian()) / dsItem.getStock();
            if(buyPrice < 0.01)
                buyPrice = 0.01;

            if (dsItem.getBuyValue() != dsItem.getSellValue()) // 판매가 별도설정
            {
                editBtnLore.add("§7" + ChatColor.stripColor(t(null, "ITEM_SETTING.TAX_IGNORED")));
                sellPrice = (dsItem.getSellValue() * dsItem.getMedian()) / dsItem.getStock();
            } else
            {
                String taxStr = "§7" + ChatColor.stripColor(t(null, "TAX.SALES_TAX")) + ": ";
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

        editBtnLore.add(t(null, "ITEM_SETTING.BUY").replace("{num}", n(buyPrice)));
        editBtnLore.add(t(null, "ITEM_SETTING.SELL").replace("{num}", n(sellPrice)));

        CreateButton(RESET, white, "Reset", editBtnLore);
        CreateButton(ROUND_DOWN, white, t(null, "ITEM_SETTING.ROUND_DOWN"), editBtnLore);
        CreateButton(DIVIDE, white, "/2", editBtnLore);
        CreateButton(MULTIPLY, white, "x2", editBtnLore);

        if (currentTab <= MAX_VALUE)
        {
            CreateButton(18, white, "-100", editBtnLore);
            CreateButton(19, white, "-10", editBtnLore);
            CreateButton(20, white, "-1", editBtnLore);
            CreateButton(21, white, "-0.1", editBtnLore);
            CreateButton(23, white, "+0.1", editBtnLore);
            CreateButton(24, white, "+1", editBtnLore);
            CreateButton(25, white, "+10", editBtnLore);
            CreateButton(26, white, "+100", editBtnLore);

            if (currentTab >= SELL_VALUE) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_VALUE"), editBtnLore);
        } else
        {
            CreateButton(18, white, "-1000", editBtnLore);
            CreateButton(19, white, "-100", editBtnLore);
            CreateButton(20, white, "-10", editBtnLore);
            CreateButton(21, white, "-1", editBtnLore);
            CreateButton(23, white, "+1", editBtnLore);
            CreateButton(24, white, "+10", editBtnLore);
            CreateButton(25, white, "+100", editBtnLore);
            CreateButton(26, white, "+1000", editBtnLore);

            if (currentTab == MEDIAN) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_STOCK"), editBtnLore);
            else if (currentTab == STOCK) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_MEDIAN"), editBtnLore);
            else if (currentTab == MAX_STOCK) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_STOCK"), editBtnLore);
        }

        inventory.setItem(SAMPLE_ITEM, dsItem.getItemStack()); // 아이탬 견본
        inventory.getItem(SAMPLE_ITEM).setAmount(1);

        double worth = TryGetWorth(dsItem.getItemStack().getType().name());
        String recommendLore;
        if (worth == 0)
        {
            recommendLore = t(player, "ERR.NO_RECOMMEND_DATA");
        } else
        {
            int sugMid = ShopUtil.CalcRecommendedMedian(worth, ConfigUtil.GetNumberOfPlayer());

            String worthChanged = (dsItem.getBuyValue() == worth) ? " ▶§f " : " ▶§a ";
            String worthChanged2 = (dsItem.getSellValue() == worth) ? " ▶§f " : " ▶§a ";
            //String minChanged = (dsItem.getMinPrice() == 0.01) ? " ▶§f " : " ▶§a ";
            //String maxChanged = (dsItem.getMaxPrice() == -1) ? " ▶§f " : " ▶§a ";
            String medianChanged = (dsItem.getMedian() == sugMid) ? " ▶§f " : " ▶§a ";
            String stockChanged = (dsItem.getStock() == sugMid) ? " ▶§f " : " ▶§a ";

            recommendLore = t(null, "ITEM_SETTING.VALUE_BUY") + "\n"
                    + "§7 " + dsItem.getBuyValue() + worthChanged + worth + "\n"
                    + t(null, "ITEM_SETTING.VALUE_SELL") + "\n"
                    + "§7 " + dsItem.getSellValue() + worthChanged2 + worth + "\n"
                    //+ t(null, "ITEM_SETTING.PRICE_MIN") + "\n"
                    //+ "§7 " + dsItem.getMinPrice() + minChanged + 0.01 + "\n"
                    //+ t(null, "ITEM_SETTING.PRICE_MAX") + "\n"
                    //+ "§7 " + dsItem.getMaxPrice() + maxChanged + -1 + "\n"
                    + t(null, "ITEM_SETTING.MEDIAN") + "\n"
                    + "§7 " + dsItem.getMedian() + medianChanged + sugMid + "\n"
                    + t(null, "ITEM_SETTING.STOCK") + "\n"
                    + "§7 " + dsItem.getStock() + stockChanged + sugMid;
        }

        Material discountMat = dsItem.discount == 0 ? Material.IRON_NUGGET : Material.GOLD_NUGGET;
        int discountMatAmount = dsItem.discount/10;
        if (discountMatAmount < 1)
            discountMatAmount = 1;
        CreateButton(DISCOUNT, discountMat, t(player, "ITEM_SETTING.DISCOUNT"), t(player, "ITEM_SETTING.DISCOUNT_LORE").replace("{num}", dsItem.discount+""), discountMatAmount);

        String tradeLimitIntervalString = dsItem.tradeLimitInterval / 1000 / 60 / 60 + "";
        String tradeLimitNextTimerString = sdf.format(dsItem.tradeLimitNextTimer);
        CreateButton(TRADE_LIMIT_AMOUNT, Material.PLAYER_HEAD,
                     t(player, "ITEM_SETTING.TRADE_LIMIT_AMOUNT"),
                     t(player, "ITEM_SETTING.TRADE_LIMIT_AMOUNT_LORE").replace("{num}", dsItem.tradeLimit + ""));

        if (dsItem.tradeLimit != 0)
        {
            CreateButton(TRADE_LIMIT_INTERVAL, Material.CLOCK,
                         t(player, "ITEM_SETTING.TRADE_LIMIT_INTERVAL"),
                         t(player, "ITEM_SETTING.TRADE_LIMIT_INTERVAL_LORE").replace("{interval}", tradeLimitIntervalString).replace("{time}", tradeLimitNextTimerString));

            CreateButton(TRADE_LIMIT_INTERVAL_TIMER, Material.CLOCK,
                         t(player, "ITEM_SETTING.TRADE_LIMIT_TIMER"),
                         t(player, "ITEM_SETTING.TRADE_LIMIT_TIMER_LORE").replace("{num}", timerOffset / 1000 / 60 / 60 + "").replace("{time}", tradeLimitNextTimerString));
        }

        CreateButton(RECOMMEND, Material.NETHER_STAR, t(player, "ITEM_SETTING.RECOMMEND"), recommendLore); // 추천 버튼

        CreateButton(DONE, Material.STRUCTURE_VOID, t(player, "ITEM_SETTING.DONE"), t(player, "ITEM_SETTING.DONE_LORE")); // 완료 버튼
        CreateButton(CLOSE, Material.BARRIER, t(player, "ITEM_SETTING.CLOSE"), t(player, "ITEM_SETTING.CLOSE_LORE")); // 닫기 버튼
        CreateButton(REMOVE, Material.BONE, t(player, "ITEM_SETTING.REMOVE"), t(player, "ITEM_SETTING.REMOVE_LORE")); // 삭제 버튼

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        this.player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null)
            return;

        if(dsItem.minPrice <= 0) dsItem.minPrice = 0.01;
        if(dsItem.maxPrice <= 0) dsItem.maxPrice = -1;

        oldSbSame = dsItem.sellValue == dsItem.buyValue;

        if (e.getSlot() == CLOSE) DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
        else if (e.getSlot() == REMOVE) RemoveItem();
        else if (e.getSlot() == RECOMMEND) SetToRecommend();
        else if (e.getSlot() == DISCOUNT) OnDiscountButtonClick(e.isLeftClick());
        else if (e.getSlot() == TRADE_LIMIT_AMOUNT) OnTradeLimitAmountButtonClick(e.isLeftClick(), e.isShiftClick());
        else if (e.getSlot() == TRADE_LIMIT_INTERVAL) OnTradeLimitIntervalButtonClick(e.isLeftClick(), e.isShiftClick());
        else if (e.getSlot() == TRADE_LIMIT_INTERVAL_TIMER) OnTradeLimitTimerAdjustButtonClick(e.isLeftClick(), e.isShiftClick());
        else if (e.getSlot() >= TAB_START && e.getSlot() <= TAB_END) ChangeTab(e.getSlot());
        else if (e.getSlot() == RESET) Reset();
        else if (e.getSlot() >= 18 && e.getSlot() < 27) PlusMinus(e.isShiftClick(), e.getCurrentItem()); // RESET 이 22인것에 주의
        else if (e.getSlot() == DIVIDE) Divide(e.isShiftClick());
        else if (e.getSlot() == MULTIPLY) Multiply(e.isShiftClick());
        else if (e.getSlot() == ROUND_DOWN) RoundDown();
        else if (e.getSlot() == SET_TO_OTHER) SetEqualToOther();
        else if (e.getSlot() == DONE) SaveSetting();
    }

    private void SaveSetting()
    {
        // 유효성 검사
        if (dsItem.maxPrice > 0 && dsItem.buyValue > dsItem.maxPrice)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (dsItem.minPrice > 0 && dsItem.buyValue < dsItem.minPrice)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (dsItem.maxPrice > 0 && dsItem.sellValue > dsItem.maxPrice)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (dsItem.minPrice > 0 && dsItem.sellValue < dsItem.minPrice)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
            return;
        }
        if (dsItem.maxPrice > 0 && dsItem.minPrice > 0 && dsItem.minPrice >= dsItem.maxPrice)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.MAX_LOWER_THAN_MIN"));
            return;
        }

        int existSlot = ShopUtil.findItemFromShop(shopName, inventory.getItem(SAMPLE_ITEM));

        if (-1 != existSlot)
        {
            ShopUtil.editShopItem(shopName, existSlot, dsItem);
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
                ShopUtil.addItemToShop(shopName, idx, dsItem);
                DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
                SoundUtil.playerSoundEffect(player, "addItem");
            }
        }
    }

    private void RemoveItem()
    {
        ShopUtil.removeItemFromShop(shopName, shopSlotIndex);
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_DELETED"));
        DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
        SoundUtil.playerSoundEffect(player, "deleteItem");
    }

    private void OnDiscountButtonClick(boolean isLeftClick)
    {
        if (isLeftClick)
            dsItem.discount += 10;
        else
            dsItem.discount -= 10;

        RefreshWindow();
    }

    private void OnTradeLimitAmountButtonClick(boolean isLeftClick, boolean isShift)
    {
        int mod;
        if (isLeftClick)
        {
            mod = 1;
        }
        else
        {
            mod = -1;
        }

        if (isShift)
            mod *= 10;

        dsItem.tradeLimit += mod;
        if (dsItem.tradeLimit != 0 && dsItem.tradeLimitNextTimer == 0)
        {
            CalcTradeLimitNextTimer();
        }
        RefreshWindow();
    }

    private void OnTradeLimitIntervalButtonClick(boolean isLeftClick, boolean isShift)
    {
        if (dsItem.tradeLimit == 0)
            return;

        long mod = 1000 * 60 * 60;
        if (!isLeftClick)
        {
            mod *= -1;
        }
        if (isShift)
        {
            mod *= 12;
        }

        dsItem.tradeLimitInterval += mod; // (ms)
        if (dsItem.tradeLimitInterval < 1000 * 60 * 60)
        {
            dsItem.tradeLimitInterval = 1000 * 60 * 60;
        }
        CalcTradeLimitNextTimer();
        RefreshWindow();
    }

    private void OnTradeLimitTimerAdjustButtonClick(boolean isLeftClick, boolean isShift)
    {
        if (dsItem.tradeLimit == 0)
            return;

        long mod = 1000 * 60 * 60;
        if (!isLeftClick)
        {
            mod *= -1;
        }
        if (isShift)
        {
            mod *= 12;
        }

        timerOffset += mod;
        CalcTradeLimitNextTimer();
        RefreshWindow();
    }

    private void CalcTradeLimitNextTimer()
    {
        dsItem.tradeLimitNextTimer = System.currentTimeMillis() + dsItem.tradeLimitInterval + timerOffset;
        dsItem.tradeLimitNextTimer = MathUtil.RoundDown_Time_Hour(dsItem.tradeLimitNextTimer);
    }

    private void SetToRecommend()
    {
        double worth = TryGetWorth(dsItem.getItemStack().getType().name());

        if (worth == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_RECOMMEND_DATA"));
        } else
        {
            int sugMid = ShopUtil.CalcRecommendedMedian(worth, ConfigUtil.GetNumberOfPlayer());
            DSItem newDSItem = new DSItem(inventory.getItem(SAMPLE_ITEM), worth, worth, dsItem.minPrice, dsItem.maxPrice, sugMid, sugMid, dsItem.maxStock, dsItem.discount,
                                          dsItem.tradeLimit, dsItem.tradeLimitInterval, dsItem.tradeLimitNextTimer);
            DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, currentTab, newDSItem, timerOffset);

            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.RECOMMEND_APPLIED").replace("{playerNum}", ConfigUtil.GetNumberOfPlayer() + ""));
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
        DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, tabIndex, dsItem, timerOffset);
    }

    private void Reset()
    {
        if (currentTab == BUY_VALUE) dsItem.buyValue = 10;
        else if (currentTab == SELL_VALUE) dsItem.sellValue = 10;
        else if (currentTab == MIN_VALUE) dsItem.minPrice = 0.01;
        else if (currentTab == MAX_VALUE) dsItem.maxPrice = -1;
        else if (currentTab == MEDIAN) dsItem.median = 10000;
        else if (currentTab == STOCK) dsItem.stock = 10000;
        else if (currentTab == MAX_STOCK) dsItem.maxStock = -1;

        RefreshWindow();
    }

    private void PlusMinus(boolean isShift, ItemStack clickedButton)
    {
        String s = clickedButton.getItemMeta().getDisplayName();
        double editNum = Double.parseDouble(s);
        if (isShift) editNum *= 5f;

        if (currentTab == BUY_VALUE)
        {
            dsItem.buyValue += editNum;
            if (dsItem.buyValue < 0.01) dsItem.buyValue = 0.01f;

            if(oldSbSame)
                dsItem.sellValue = dsItem.buyValue;
        } else if (currentTab == SELL_VALUE)
        {
            dsItem.sellValue += editNum;
            if (dsItem.sellValue < 0.01) dsItem.sellValue = 0.01f;
        } else if (currentTab == MIN_VALUE)
        {
            dsItem.minPrice += editNum;
            if (dsItem.minPrice < 0.01) dsItem.minPrice = 0.01f;
        } else if (currentTab == MAX_VALUE)
        {
            if (dsItem.maxPrice <= 0 && editNum > 0)
                dsItem.maxPrice = editNum;
            else
            {
                dsItem.maxPrice += editNum;
                if (dsItem.maxPrice < 0.01)
                    dsItem.maxPrice = -1;
            }
        } else if (currentTab == MEDIAN)
        {
            if(dsItem.median <= 0 && editNum > 0)
            {
                dsItem.median = (int)editNum;
                if(dsItem.stock == -1)
                    dsItem.stock = 1;
            }
            else
            {
                dsItem.median += editNum;
                if(dsItem.median < 1)
                    dsItem.median = -1;
            }
        } else if (currentTab == STOCK)
        {
            if (dsItem.stock <= 0 && editNum > 0)
            {
                dsItem.stock = (int)editNum;
            }
            else
            {
                dsItem.stock += editNum;
                if(dsItem.stock < 1)
                {
                    dsItem.stock = -1;
                    dsItem.median = -1;
                    dsItem.maxStock = -1;
                }
            }
        } else if (currentTab == MAX_STOCK)
        {
            if(dsItem.maxStock <= 0 && editNum > 0)
            {
                dsItem.maxStock = (int)editNum;
            }
            else
            {
                dsItem.maxStock += editNum;
                if(dsItem.maxStock < 1)
                    dsItem.maxStock = -1;
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
            dsItem.buyValue /= div;
            if(dsItem.buyValue < 0.01) dsItem.buyValue = 0.01;

            if(oldSbSame)
                dsItem.sellValue = dsItem.buyValue;
        } else if (currentTab == SELL_VALUE)
        {
            dsItem.sellValue /= div;
            if(dsItem.sellValue < 0.01) dsItem.sellValue = 0.01;
        } else if (currentTab == MIN_VALUE)
        {
            dsItem.minPrice /= div;
            if(dsItem.minPrice < 0.01) dsItem.minPrice = 0.01;
        } else if (currentTab == MAX_VALUE)
        {
            dsItem.maxPrice /= div;
            if(dsItem.maxPrice < 0.01) dsItem.maxPrice = 0.01;
        } else if (currentTab == MEDIAN)
        {
            if(dsItem.median > 1)
            {
                dsItem.median /= div;
                if(dsItem.median < 1) dsItem.median = 1;
            }
        } else if (currentTab == STOCK)
        {
            if (dsItem.stock > 1)
            {
                dsItem.stock /= div;
                if (dsItem.stock < 1) dsItem.stock = 1;
            }
        } else if (currentTab == MAX_STOCK)
        {
            if (dsItem.maxStock > 1)
            {
                dsItem.maxStock /= div;
                if(dsItem.maxStock < 1) dsItem.maxStock = 1;
            }
        }

        RefreshWindow();
    }

    private void Multiply(boolean isShift)
    {
        int mul = 2;
        if (isShift) mul = 10;

        if (currentTab == BUY_VALUE)
        {
            dsItem.buyValue *= mul;

            if(oldSbSame)
                dsItem.sellValue = dsItem.buyValue;
        }
        else if (currentTab == SELL_VALUE) dsItem.sellValue *= mul;
        else if (currentTab == MIN_VALUE) dsItem.minPrice *= mul;
        else if (currentTab == MAX_VALUE) dsItem.maxPrice *= mul;
        else if (currentTab == MEDIAN) dsItem.median *= mul;
        else if (currentTab == STOCK) dsItem.stock *= mul;
        else if (currentTab == MAX_STOCK) dsItem.maxStock *= mul;

        RefreshWindow();
    }

    private void RoundDown()
    {
        if (currentTab == BUY_VALUE)
        {
            dsItem.buyValue = MathUtil.RoundDown(dsItem.buyValue);

            if(oldSbSame)
                dsItem.sellValue = dsItem.buyValue;
        }
        else if (currentTab == SELL_VALUE) dsItem.sellValue = MathUtil.RoundDown(dsItem.sellValue);
        else if (currentTab == MIN_VALUE) dsItem.minPrice = MathUtil.RoundDown(dsItem.minPrice);
        else if (currentTab == MAX_VALUE) dsItem.maxPrice = MathUtil.RoundDown(dsItem.maxPrice);
        else if (currentTab == MEDIAN) dsItem.median = MathUtil.RoundDown(dsItem.median);
        else if (currentTab == STOCK) dsItem.stock = MathUtil.RoundDown(dsItem.stock);
        else if (currentTab == MAX_STOCK) dsItem.maxStock = MathUtil.RoundDown(dsItem.maxStock);

        RefreshWindow();
    }

    private void SetEqualToOther()
    {
        if (currentTab == SELL_VALUE) dsItem.sellValue = dsItem.buyValue;
        else if (currentTab == MIN_VALUE) dsItem.minPrice = dsItem.buyValue;
        else if (currentTab == MAX_VALUE) dsItem.maxPrice = dsItem.buyValue;
        else if (currentTab == MEDIAN) dsItem.median = dsItem.stock;
        else if (currentTab == STOCK) dsItem.stock = dsItem.median;
        else if (currentTab == MAX_STOCK) dsItem.maxStock = dsItem.stock;

        RefreshWindow();
    }

    private void ValueValidation()
    {
        if (dsItem.buyValue < 0.01)
            dsItem.buyValue = 0.01;
        if (dsItem.sellValue < 0.01)
            dsItem.sellValue = 0.01;
        if (dsItem.minPrice < 0.01)
            dsItem.minPrice = 0.01;
        if (dsItem.maxPrice < -1)
            dsItem.maxPrice = -1;
        if (dsItem.median < -1)
            dsItem.median = -1;
        if (dsItem.stock < -1)
            dsItem.stock = -1;
        if (dsItem.maxStock < -1)
            dsItem.maxStock = -1;
        if (dsItem.discount < 0)
            dsItem.discount = 0;
        if (dsItem.discount > 90)
            dsItem.discount = 90;
    }

    private void RefreshWindow()
    {
        ValueValidation();
        DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, currentTab, dsItem, timerOffset);
        SoundUtil.playerSoundEffect(player, "editItem");
    }
}
