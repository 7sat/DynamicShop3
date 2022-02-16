package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.models.DSItem;
import me.sat7.dynamicshop.transactions.Calc;

import static me.sat7.dynamicshop.DynaShopAPI.df;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class ItemSettings extends InGameUI
{
    public ItemSettings()
    {
        uiType = UI_TYPE.ItemSettings;
    }

    private final int DONE = 8;
    private final int CLOSE = 27;
    private final int RECOMMEND = 31;
    private final int REMOVE = 35;

    public Inventory getGui(Player player, int tab, DSItem dsItem)
    {
        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        String shopName = temp[0];

        inventory = Bukkit.createInventory(player, 36, t("ITEM_SETTING_TITLE") + "§7 | §8" + shopName);

        String buyValueStr = t("ITEM_SETTING.VALUE_BUY") + dsItem.getBuyValue();
        String sellValueStr = t("ITEM_SETTING.VALUE_SELL") + dsItem.getSellValue();
        String priceMinStr = t("ITEM_SETTING.PRICE_MIN") + dsItem.getMinPrice();
        String priceMaxStr = t("ITEM_SETTING.PRICE_MAX") + dsItem.getMaxPrice();
        String medianStr = t("ITEM_SETTING.MEDIAN") + dsItem.getMedian();
        String stockStr = t("ITEM_SETTING.STOCK") + dsItem.getStock();

        ArrayList<String> sellValueLore = new ArrayList<>();
        ArrayList<String> medianLore = new ArrayList<>();
        medianLore.addAll(Arrays.asList(t("ITEM_SETTING.MEDIAN_HELP").split("\n")));
        ArrayList<String> stockLore = new ArrayList<>();
        ArrayList<String> maxPriceLore = new ArrayList<>();

        // 고정가, 무한재고, 별도판매가 안내 표시
        if (dsItem.getBuyValue() != dsItem.getSellValue())
        {
            sellValueLore.add("§7(" + t("ITEM_SETTING.TAX_IGNORED") + ")");
        }
        if (dsItem.getMedian() <= 0)
        {
            medianLore.add("§7(" + t("ITEM_SETTING.STATIC_PRICE") + ")");
        }
        if (dsItem.getStock() <= 0)
        {
            stockLore.add("§7(" + t("ITEM_SETTING.INF_STOCK") + ")");
        }
        if (dsItem.getMaxPrice() <= 0)
        {
            maxPriceLore.add("§7(" + t("ITEM_SETTING.UNLIMITED") + ")");
        }

        // 가격, 미디안, 스톡 버튼
        ItemStack buyValueBtn = ItemsUtil.createItemStack((tab == 1) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, null, buyValueStr, null, 1);
        ItemStack sellValueBtn = ItemsUtil.createItemStack((tab == 2) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE, null, sellValueStr, sellValueLore, 1);
        ItemStack minValueBtn = ItemsUtil.createItemStack((tab == 3) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE, null, priceMinStr, null, 1);
        ItemStack maxValueBtn = ItemsUtil.createItemStack((tab == 4) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE, null, priceMaxStr, maxPriceLore, 1);
        ItemStack medianBtn = ItemsUtil.createItemStack((tab == 5) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, null, medianStr, medianLore, 1);
        ItemStack stockBtn = ItemsUtil.createItemStack((tab == 6) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, null, stockStr, stockLore, 1);
        inventory.setItem(2, buyValueBtn);
        inventory.setItem(3, sellValueBtn);
        inventory.setItem(4, minValueBtn);
        inventory.setItem(5, maxValueBtn);
        inventory.setItem(6, medianBtn);
        inventory.setItem(7, stockBtn);

        ItemStack infoBtn = ItemsUtil.createItemStack(Material.BLACK_STAINED_GLASS_PANE, null, "Shift = x5", null, 1);
        inventory.setItem(22, infoBtn);

        // 조절버튼
        if (dsItem.getBuyValue() == dsItem.getSellValue()) sellValueStr = "§7" + ChatColor.stripColor(sellValueStr);
        if (dsItem.getMinPrice() <= 0.01) priceMinStr = "§7" + ChatColor.stripColor(priceMinStr);
        if (dsItem.getMaxPrice() <= 0)
            priceMaxStr = "§7" + ChatColor.stripColor(t("ITEM_SETTING.PRICE_MAX") + t("ITEM_SETTING.UNLIMITED"));

        ArrayList<String> editBtnLore = new ArrayList<>();
        editBtnLore.add("§3§m                       ");
        if (tab == 1)
        {
            buyValueStr = "§3>" + buyValueStr;
        } else if (tab == 2)
        {
            sellValueStr = "§3>" + sellValueStr;
        } else if (tab == 3)
        {
            priceMinStr = "§3>" + priceMinStr;
        } else if (tab == 4)
        {
            priceMaxStr = "§3>" + priceMaxStr;
        } else if (tab == 5)
        {
            medianStr = "§3>" + medianStr;
        } else if (tab == 6)
        {
            stockStr = "§3>" + stockStr;
        }

        if (dsItem.getMedian() <= 0)
            medianStr = medianStr + "§7(" + t("ITEM_SETTING.STATIC_PRICE") + ")";
        if (dsItem.getStock() <= 0) stockStr = stockStr + "§7(" + t("ITEM_SETTING.INF_STOCK") + ")";

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
                sellPrice = buyPrice - ((buyPrice / 100) * Calc.getTaxRate(shopName));
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
                if (ShopUtil.shopConfigFiles.get(shopName).get().contains("Options.SalesTax"))
                {
                    taxStr += ShopUtil.shopConfigFiles.get(shopName).get().getInt("Options.SalesTax") + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * ShopUtil.shopConfigFiles.get(shopName).get().getInt("Options.SalesTax"));
                } else
                {
                    taxStr += ConfigUtil.getCurrentTax() + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * ConfigUtil.getCurrentTax());
                }
                sellPrice = (Math.round(sellPrice * 100) / 100.0);

                editBtnLore.add(taxStr);
            }
        }

        editBtnLore.add(t("ITEM_SETTING.BUY").replace("{num}", df.format(buyPrice)));
        editBtnLore.add(t("ITEM_SETTING.SELL").replace("{num}", df.format(sellPrice)));

        ItemStack d2Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "/2", editBtnLore, 1);
        ItemStack m1000Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "-1000", editBtnLore, 1);
        ItemStack m100Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "-100", editBtnLore, 1);
        ItemStack m10Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "-10", editBtnLore, 1);
        ItemStack m1Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "-1", editBtnLore, 1);
        ItemStack m01Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "-0.1", editBtnLore, 1);
        ItemStack reset = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "Reset", editBtnLore, 1);
        ItemStack p01Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "+0.1", editBtnLore, 1);
        ItemStack p1Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "+1", editBtnLore, 1);
        ItemStack p10Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "+10", editBtnLore, 1);
        ItemStack p100Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "+100", editBtnLore, 1);
        ItemStack p1000Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "+1000", editBtnLore, 1);
        ItemStack m2Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, null, "x2", editBtnLore, 1);
        ItemStack roundBtn = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, t("ITEM_SETTING.ROUND_DOWN"), editBtnLore, 1);
        ItemStack setToMedian = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, t("ITEM_SETTING.SET_TO_MEDIAN"), editBtnLore, 1);
        ItemStack setToStock = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, t("ITEM_SETTING.SET_TO_STOCK"), editBtnLore, 1);
        ItemStack setToValue = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, t("ITEM_SETTING.SET_TO_VALUE"), editBtnLore, 1);

        // 내림 버튼
        inventory.setItem(20, roundBtn);

        // 리셋버튼
        inventory.setItem(13, reset);

        // 곱하기,나누기
        inventory.setItem(21, d2Btn);
        inventory.setItem(23, m2Btn);

        // +, -, ~에 맞추기
        if (tab <= 4)
        {
            inventory.setItem(9, m100Btn);
            inventory.setItem(10, m10Btn);
            inventory.setItem(11, m1Btn);
            inventory.setItem(12, m01Btn);
            inventory.setItem(14, p01Btn);
            inventory.setItem(15, p1Btn);
            inventory.setItem(16, p10Btn);
            inventory.setItem(17, p100Btn);
            if (tab >= 2)
            {
                inventory.setItem(24, setToValue);
            }
        } else
        {
            inventory.setItem(9, m1000Btn);
            inventory.setItem(10, m100Btn);
            inventory.setItem(11, m10Btn);
            inventory.setItem(12, m1Btn);
            inventory.setItem(14, p1Btn);
            inventory.setItem(15, p10Btn);
            inventory.setItem(16, p100Btn);
            inventory.setItem(17, p1000Btn);
            if (tab == 5)
            {
                inventory.setItem(24, setToStock);
            } else if (tab == 6)
            {
                inventory.setItem(24, setToMedian);
            }
        }

        inventory.setItem(0, dsItem.getItemStack()); // 아이탬 견본

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
        Player player = (Player) e.getWhoClicked();

        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        String shopName = temp[0];

        if (e.getCurrentItem() == null)
        {
            return;
        }

        // 닫기 버튼
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openItemPalette(player, 1, "");
            return;
        }
        //삭제 버튼
        else if (e.getSlot() == REMOVE)
        {
            int idx = ShopUtil.findItemFromShop(shopName, e.getClickedInventory().getItem(0));
            if (idx != -1)
            {
                ShopUtil.removeItemFromShop(shopName, idx);
                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_DELETED"));
                DynaShopAPI.openShopGui(player, shopName, Integer.parseInt(temp[1]) / 45 + 1);
                DynamicShop.userInteractItem.put(player.getUniqueId(), "");
                SoundUtil.playerSoundEffect(player, "deleteItem");
                return;
            }
        }

        String valueBuy = e.getClickedInventory().getItem(2).getItemMeta().getDisplayName();
        String valueSell = e.getClickedInventory().getItem(3).getItemMeta().getDisplayName();
        String valueMin = e.getClickedInventory().getItem(4).getItemMeta().getDisplayName();
        String valueMax = e.getClickedInventory().getItem(5).getItemMeta().getDisplayName();
        String median = e.getClickedInventory().getItem(6).getItemMeta().getDisplayName();
        String stock = e.getClickedInventory().getItem(7).getItemMeta().getDisplayName();

        valueBuy = valueBuy.replace(ChatColor.stripColor(t("ITEM_SETTING.VALUE_BUY")), "");
        valueSell = valueSell.replace(ChatColor.stripColor(t("ITEM_SETTING.VALUE_SELL")), "");
        valueMin = valueMin.replace(ChatColor.stripColor(t("ITEM_SETTING.PRICE_MIN")), "");
        valueMax = valueMax.replace(ChatColor.stripColor(t("ITEM_SETTING.PRICE_MAX")), "");

        median = median.replace(ChatColor.stripColor(t("ITEM_SETTING.MEDIAN")), "");
        stock = stock.replace(ChatColor.stripColor(t("ITEM_SETTING.STOCK")), "");
        double valueBuyD = Double.parseDouble(ChatColor.stripColor(valueBuy));
        double valueSellD = Double.parseDouble(ChatColor.stripColor(valueSell));
        double valueMinD = Double.parseDouble(ChatColor.stripColor(valueMin));
        if (valueMinD <= 0) valueMinD = 0.01;
        double valueMaxD = Double.parseDouble(ChatColor.stripColor(valueMax));
        if (valueMaxD <= 0) valueMaxD = -1;
        int medianI = Integer.parseInt(ChatColor.stripColor(median));
        int stockI = Integer.parseInt(ChatColor.stripColor(stock));

        double newBuyValue = valueBuyD;
        double newSellValue = valueSellD;
        double newValueMin = valueMinD;
        double newValueMax = valueMaxD;
        int newMedian = medianI;
        int newStock = stockI;

        int tab = 1;
        for (int i = 1; i <= 6; i++)
        {
            if (e.getClickedInventory().getItem(i + 1).getType() == Material.RED_STAINED_GLASS_PANE)
            {
                tab = i;
                break;
            }
        }

        // 추천 버튼
        if (e.getSlot() == RECOMMEND)
        {
            double worth = TryGetWorth(e.getClickedInventory().getItem(0).getType().name());

            if (worth == 0)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_RECOMMEND_DATA"));
            } else
            {
                int numberOfPlayer = DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer");
                int sugMid = ShopUtil.CalcRecommendedMedian(worth, numberOfPlayer);

                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.RECOMMEND_APPLIED").replace("{playerNum}", numberOfPlayer + ""));

                DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab,
                        worth, worth, newValueMin, newValueMax, sugMid, sugMid);

                SoundUtil.playerSoundEffect(player, "editItem");
            }
            return;
        }

        // 가격,미디언,스톡 탭 이동
        if (e.getSlot() >= 2 && e.getSlot() <= 7)
        {
            DynaShopAPI.openItemSettingGui(player, e.getClickedInventory().getItem(0), e.getSlot() - 1, valueBuyD, valueSellD, valueMinD, valueMaxD, medianI, stockI);
        }
        // + - 버튼들
        else if (e.getSlot() >= 9 && e.getSlot() < 18)
        {
            if (e.getSlot() != 13)
            {
                String s = e.getCurrentItem().getItemMeta().getDisplayName();
                double editNum = Double.parseDouble(s);
                if (e.isShiftClick()) editNum *= 5f;

                if (tab == 1)
                {
                    newBuyValue = valueBuyD + editNum;
                    if (newBuyValue < 0.01) newBuyValue = 0.01f;

                    if (valueBuyD == valueSellD) newSellValue = newBuyValue;
                } else if (tab == 2)
                {
                    newSellValue = valueSellD + editNum;
                    if (newSellValue < 0.01) newSellValue = 0.01f;
                } else if (tab == 3)
                {
                    if (valueMinD <= 0.01) valueMinD = 0;
                    newValueMin = valueMinD + editNum;
                    if (newValueMin < 0.01) newValueMin = 0.01f;
                } else if (tab == 4)
                {
                    if (valueMaxD < 0) valueMaxD = 0;
                    newValueMax = valueMaxD + editNum;
                    if (newValueMax < -1) newValueMax = -1;
                } else if (tab == 5)
                {
                    newMedian = medianI + (int) editNum;
                    if (newMedian == 0 && medianI == -1) newMedian = 1;
                    else if (newMedian <= 0) newMedian = -1;
                } else if (tab == 6)
                {
                    newStock = stockI + (int) editNum;
                    if (newStock == 0 && stockI == -1) newStock = 1;
                    else if (newStock <= 0)
                    {
                        newStock = -1;
                        newMedian = -1;
                    }
                }

                DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab, newBuyValue, newSellValue, newValueMin, newValueMax, newMedian, newStock);
            } else
            {
                if (tab == 1)
                {
                    newBuyValue = 10;
                    if (valueBuyD == valueSellD) newSellValue = newBuyValue;
                } else if (tab == 2)
                {
                    newSellValue = valueBuyD;
                } else if (tab == 3)
                {
                    newValueMin = 0.01;
                } else if (tab == 4)
                {
                    newValueMax = -1;
                } else if (tab == 5)
                {
                    newMedian = 10000;
                } else if (tab == 6)
                {
                    newStock = 10000;
                }

                DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab, newBuyValue, newSellValue, newValueMin, newValueMax, newMedian, newStock);
            }
            SoundUtil.playerSoundEffect(player, "editItem");
        }
        // 나누기
        else if (e.getSlot() == 21)
        {
            int div = 2;
            if (e.isShiftClick()) div = 10;

            if (tab == 1)
            {
                if (valueBuyD <= 0.01) return;
                newBuyValue = valueBuyD / div;

                if (valueBuyD == valueSellD) newSellValue = newBuyValue;
            } else if (tab == 2)
            {
                if (valueSellD <= 0.01) return;
                newSellValue = valueSellD / div;
            } else if (tab == 3)
            {
                if (valueMinD <= 0.01) return;
                newValueMin = valueMinD / div;
            } else if (tab == 4)
            {
                if (valueMaxD <= 0.01) return;
                newValueMax = valueMaxD / div;
            } else if (tab == 5)
            {
                if (medianI <= 1) return;
                newMedian = medianI / div;
            } else if (tab == 6)
            {
                if (stockI <= 1) return;
                newStock = stockI / div;
            }

            DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab, newBuyValue, newSellValue, newValueMin, newValueMax, newMedian, newStock);
            SoundUtil.playerSoundEffect(player, "editItem");
        }
        // 곱하기
        else if (e.getSlot() == 23)
        {
            int mul = 2;
            if (e.isShiftClick()) mul = 10;

            if (tab == 1)
            {
                if (valueBuyD <= 0) return;
                newBuyValue = valueBuyD * mul;

                if (valueBuyD == valueSellD) newSellValue = newBuyValue;
            } else if (tab == 2)
            {
                if (valueSellD <= 0) return;
                newSellValue = valueSellD * mul;
            } else if (tab == 3)
            {
                if (valueMinD <= 0) return;
                newValueMin = valueMinD * mul;
            } else if (tab == 4)
            {
                if (valueMaxD <= 0) return;
                newValueMax = valueMaxD * mul;
            } else if (tab == 5)
            {
                if (medianI <= 0) return;
                newMedian = medianI * mul;
            } else if (tab == 6)
            {
                if (stockI <= 0) return;
                newStock = stockI * mul;
            }

            DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab, newBuyValue, newSellValue, newValueMin, newValueMax, newMedian, newStock);
            SoundUtil.playerSoundEffect(player, "editItem");
        }
        // 내림 버튼
        else if (e.getSlot() == 20)
        {
            if (tab == 1)
            {
                newBuyValue = MathUtil.RoundDown((int) valueBuyD);
                if (valueBuyD == valueSellD) newSellValue = newBuyValue;
            } else if (tab == 2)
            {
                newSellValue = MathUtil.RoundDown((int) valueSellD);
            } else if (tab == 3)
            {
                newValueMin = MathUtil.RoundDown((int) valueMinD);
            } else if (tab == 4)
            {
                newValueMax = MathUtil.RoundDown((int) valueMaxD);
            } else if (tab == 5)
            {
                newMedian = MathUtil.RoundDown(medianI);
            } else if (tab == 6)
            {
                newStock = MathUtil.RoundDown(stockI);
            }

            DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab, newBuyValue, newSellValue, newValueMin, newValueMax, newMedian, newStock);
            SoundUtil.playerSoundEffect(player, "editItem");
        }
        // 스톡을 미디안에 맞춤. 또는 그 반대
        else if (e.getSlot() == 24)
        {
            if (tab == 2)
            {
                newSellValue = valueBuyD;
            } else if (tab == 3)
            {
                newValueMin = valueBuyD;
            } else if (tab == 4)
            {
                newValueMax = valueBuyD;
            } else if (tab == 5)
            {
                newMedian = stockI;
            } else if (tab == 6)
            {
                newStock = medianI;
            }

            DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab, newBuyValue, newSellValue, newValueMin, newValueMax, newMedian, newStock);
            SoundUtil.playerSoundEffect(player, "editItem");
        }
        //완료
        else if (e.getSlot() == DONE)
        {
            // 유효성 검사
            if (valueMaxD > 0 && valueBuyD > valueMaxD)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMinD > 0 && valueBuyD < valueMinD)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMaxD > 0 && valueSellD > valueMaxD)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMinD > 0 && valueSellD < valueMinD)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMaxD > 0 && valueMinD > 0 && valueMinD >= valueMaxD)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.MAX_LOWER_THAN_MIN"));
                return;
            }

            int existSlot = ShopUtil.findItemFromShop(shopName, e.getClickedInventory().getItem(0));
            if (-1 != existSlot)
            {
                ShopUtil.editShopItem(shopName, existSlot, valueBuyD, valueSellD, valueMinD, valueMaxD, medianI, stockI);
                DynaShopAPI.openShopGui(player, shopName, existSlot / 45 + 1);
                DynamicShop.userInteractItem.put(player.getUniqueId(), "");
                SoundUtil.playerSoundEffect(player, "addItem");
            } else
            {
                int idx = -1;
                try{
                    idx = ShopUtil.findEmptyShopSlot(shopName, Integer.parseInt(DynamicShop.userInteractItem.get(player.getUniqueId()).split("/")[1]), true);
                }catch (Exception ignore){}

                if (idx != -1)
                {
                    ShopUtil.addItemToShop(shopName, idx, e.getClickedInventory().getItem(0), valueBuyD, valueSellD, valueMinD, valueMaxD, medianI, stockI);
                    DynaShopAPI.openShopGui(player, shopName, Integer.parseInt(temp[1]) / 45 + 1);
                    DynamicShop.userInteractItem.put(player.getUniqueId(), "");
                    SoundUtil.playerSoundEffect(player, "addItem");
                }
            }
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
}
