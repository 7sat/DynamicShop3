package me.sat7.dynamicshop.guis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

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

public class ItemSettings extends InGameUI
{

    public ItemSettings()
    {
        uiType = UI_TYPE.ItemSettings;
    }

    public Inventory getGui(Player player, int tab, DSItem dsItem)
    {
        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        String shopName = temp[0];

        // UI 요소 생성
        String title = LangUtil.ccLang.get().getString("ITEM_SETTING_TITLE");
        Inventory inven = Bukkit.createInventory(player, 36, title);

        String buyValueStr = LangUtil.ccLang.get().getString("VALUE_BUY") + dsItem.getBuyValue();
        String sellValueStr = LangUtil.ccLang.get().getString("VALUE_SELL") + dsItem.getSellValue();
        String priceMinStr = LangUtil.ccLang.get().getString("PRICE_MIN") + dsItem.getMinPrice();
        String priceMaxStr = LangUtil.ccLang.get().getString("PRICE_MAX") + dsItem.getMaxPrice();
        String medianStr = LangUtil.ccLang.get().getString("MEDIAN") + dsItem.getMedian();
        String stockStr = LangUtil.ccLang.get().getString("STOCK") + dsItem.getStock();

        ArrayList<String> sellValueLore = new ArrayList<>();
        ArrayList<String> medianLore = new ArrayList<>();
        ArrayList<String> stockLore = new ArrayList<>();
        ArrayList<String> maxPriceLore = new ArrayList<>();

        // 고정가, 무한재고, 별도판매가 안내 표시
        if (dsItem.getBuyValue() != dsItem.getSellValue())
        {
            sellValueLore.add("§7(" + LangUtil.ccLang.get().getString("TAXIGNORED") + ")");
        }
        if (dsItem.getMedian() <= 0)
        {
            medianLore.add("§7(" + LangUtil.ccLang.get().getString("STATICPRICE") + ")");
        }
        if (dsItem.getStock() <= 0)
        {
            stockLore.add("§7(" + LangUtil.ccLang.get().getString("INFSTOCK") + ")");
        }
        if (dsItem.getMaxPrice() <= 0)
        {
            maxPriceLore.add("§7(" + LangUtil.ccLang.get().getString("UNLIMITED") + ")");
        }

        // 가격, 미디안, 스톡 버튼
        ItemStack buyValueBtn = ItemsUtil.createItemStack((tab == 1) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, null, buyValueStr, null, 1);
        ItemStack sellValueBtn = ItemsUtil.createItemStack((tab == 2) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE, null, sellValueStr, sellValueLore, 1);
        ItemStack minValueBtn = ItemsUtil.createItemStack((tab == 3) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE, null, priceMinStr, null, 1);
        ItemStack maxValueBtn = ItemsUtil.createItemStack((tab == 4) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE, null, priceMaxStr, maxPriceLore, 1);
        ItemStack medianBtn = ItemsUtil.createItemStack((tab == 5) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, null, medianStr, medianLore, 1);
        ItemStack stockBtn = ItemsUtil.createItemStack((tab == 6) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, null, stockStr, stockLore, 1);
        inven.setItem(2, buyValueBtn);
        inven.setItem(3, sellValueBtn);
        inven.setItem(4, minValueBtn);
        inven.setItem(5, maxValueBtn);
        inven.setItem(6, medianBtn);
        inven.setItem(7, stockBtn);

        ItemStack infoBtn = ItemsUtil.createItemStack(Material.BLACK_STAINED_GLASS_PANE, null, "Shift = x5", null, 1);
        inven.setItem(22, infoBtn);

        // 조절버튼
        if (dsItem.getBuyValue() == dsItem.getSellValue()) sellValueStr = "§7" + ChatColor.stripColor(sellValueStr);
        if (dsItem.getMinPrice() <= 0.01) priceMinStr = "§7" + ChatColor.stripColor(priceMinStr);
        if (dsItem.getMaxPrice() <= 0)
            priceMaxStr = "§7" + ChatColor.stripColor(LangUtil.ccLang.get().getString("PRICE_MAX") + LangUtil.ccLang.get().getString("UNLIMITED"));

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
            medianStr = medianStr + "§7(" + LangUtil.ccLang.get().getString("STATICPRICE") + ")";
        if (dsItem.getStock() <= 0) stockStr = stockStr + "§7(" + LangUtil.ccLang.get().getString("INFSTOCK") + ")";

        editBtnLore.add(buyValueStr);
        editBtnLore.add(sellValueStr);
        editBtnLore.add(priceMinStr);
        editBtnLore.add(priceMaxStr);
        editBtnLore.add(medianStr);
        editBtnLore.add(stockStr);

        editBtnLore.add("§3§m                       ");
        double buyPrice = 0;
        double sellPrice = 0;
        if (dsItem.getMedian() <= 0 || dsItem.getStock() <= 0)
        {
            buyPrice = dsItem.getBuyValue();
            if (dsItem.getBuyValue() != dsItem.getSellValue())
            {
                editBtnLore.add("§7" + ChatColor.stripColor(LangUtil.ccLang.get().getString("TAXIGNORED")));
                sellPrice = dsItem.getSellValue();
            } else
            {
                String taxStr = "§7" + ChatColor.stripColor(LangUtil.ccLang.get().getString("TAX.SALESTAX")) + ": ";
                taxStr += Calc.getTaxRate(shopName) + "%";
                editBtnLore.add(taxStr);
                sellPrice = buyPrice - ((buyPrice / 100) * Calc.getTaxRate(shopName));
            }
        } else
        {
            buyPrice = (dsItem.getBuyValue() * dsItem.getMedian()) / dsItem.getStock();
            if (dsItem.getBuyValue() != dsItem.getSellValue()) // 판매가 별도설정
            {
                editBtnLore.add("§7" + ChatColor.stripColor(LangUtil.ccLang.get().getString("TAXIGNORED")));
                sellPrice = (dsItem.getSellValue() * dsItem.getMedian()) / dsItem.getStock();
            } else
            {
                String taxStr = "§7" + ChatColor.stripColor(LangUtil.ccLang.get().getString("TAX.SALESTAX")) + ": ";
                if (ShopUtil.ccShop.get().contains(shopName + ".Options.SalesTax"))
                {
                    taxStr += ShopUtil.ccShop.get().getInt(shopName + ".Options.SalesTax") + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * ShopUtil.ccShop.get().getInt(shopName + ".Options.SalesTax"));
                } else
                {
                    taxStr += ConfigUtil.getCurrentTax() + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * ConfigUtil.getCurrentTax());
                }
                sellPrice = (Math.round(sellPrice * 100) / 100.0);

                editBtnLore.add(taxStr);
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        editBtnLore.add("§3§l" + ChatColor.stripColor(LangUtil.ccLang.get().getString("BUY")) + ": " + df.format(buyPrice));
        editBtnLore.add("§3§l" + ChatColor.stripColor(LangUtil.ccLang.get().getString("SELL")) + ": " + df.format(sellPrice));

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
        ItemStack roundBtn = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, LangUtil.ccLang.get().getString("ROUNDDOWN"), editBtnLore, 1);
        ItemStack setToMedian = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, LangUtil.ccLang.get().getString("SETTOMEDIAN"), editBtnLore, 1);
        ItemStack setToStock = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, LangUtil.ccLang.get().getString("SETTOSTOCK"), editBtnLore, 1);
        ItemStack setToValue = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE, null, LangUtil.ccLang.get().getString("SETTOVALUE"), editBtnLore, 1);

        // 내림 버튼
        inven.setItem(20, roundBtn);

        // 리셋버튼
        inven.setItem(13, reset);

        // 곱하기,나누기
        inven.setItem(21, d2Btn);
        inven.setItem(23, m2Btn);

        // +, -, ~에 맞추기
        if (tab <= 4)
        {
            inven.setItem(9, m100Btn);
            inven.setItem(10, m10Btn);
            inven.setItem(11, m1Btn);
            inven.setItem(12, m01Btn);
            inven.setItem(14, p01Btn);
            inven.setItem(15, p1Btn);
            inven.setItem(16, p10Btn);
            inven.setItem(17, p100Btn);
            if (tab >= 2)
            {
                inven.setItem(24, setToValue);
            }
        } else
        {
            inven.setItem(9, m1000Btn);
            inven.setItem(10, m100Btn);
            inven.setItem(11, m10Btn);
            inven.setItem(12, m1Btn);
            inven.setItem(14, p1Btn);
            inven.setItem(15, p10Btn);
            inven.setItem(16, p100Btn);
            inven.setItem(17, p1000Btn);
            if (tab == 5)
            {
                inven.setItem(24, setToStock);
            } else if (tab == 6)
            {
                inven.setItem(24, setToMedian);
            }
        }

        // 아이탬 견본
        inven.setItem(0, dsItem.getItemStack());

        // 완료 버튼
        ItemStack doneBtn = ItemsUtil.createItemStack(Material.STRUCTURE_VOID, null,
                LangUtil.ccLang.get().getString("DONE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("DONE_LORE"))), 1);

        inven.setItem(8, doneBtn);

        // 닫기 버튼
        ItemStack closeBtn = ItemsUtil.createItemStack(Material.BARRIER, null,
                LangUtil.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("CLOSE_LORE"))), 1);
        inven.setItem(27, closeBtn);

        // 추천 버튼
        ItemStack recBtn = ItemsUtil.createItemStack(Material.NETHER_STAR, null,
                LangUtil.ccLang.get().getString("RECOMMEND"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("RECOMMEND_LORE"))), 1);
        inven.setItem(31, recBtn);

        // 삭제 버튼
        ItemStack removeBtn = ItemsUtil.createItemStack(Material.BONE, null,
                LangUtil.ccLang.get().getString("REMOVE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("REMOVE_LORE"))), 1);
        inven.setItem(35, removeBtn);
        return inven;
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
        if (e.getSlot() == 27)
        {
            SoundUtil.playerSoundEffect(player, "click");
            DynaShopAPI.openItemPalette(player, 1, "");
            return;
        }
        //삭제 버튼
        else if (e.getSlot() == 35)
        {
            int idx = ShopUtil.findItemFromShop(shopName, e.getClickedInventory().getItem(0));
            if (idx != -1)
            {
                ShopUtil.removeItemFromShop(shopName, idx);
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_DELETED"));
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

        valueBuy = valueBuy.replace(ChatColor.stripColor(LangUtil.ccLang.get().getString("VALUE_BUY")), "");
        valueSell = valueSell.replace(ChatColor.stripColor(LangUtil.ccLang.get().getString("VALUE_SELL")), "");
        valueMin = valueMin.replace(ChatColor.stripColor(LangUtil.ccLang.get().getString("PRICE_MIN")), "");
        valueMax = valueMax.replace(ChatColor.stripColor(LangUtil.ccLang.get().getString("PRICE_MAX")), "");

        median = median.replace(ChatColor.stripColor(LangUtil.ccLang.get().getString("MEDIAN")), "");
        stock = stock.replace(ChatColor.stripColor(LangUtil.ccLang.get().getString("STOCK")), "");
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
        if (e.getSlot() == 31)
        {
            String sug = e.getClickedInventory().getItem(0).getType().name();

            double worth = WorthUtil.ccWorth.get().getDouble(sug);
            if (worth == 0)
            {
                sug = sug.replace("-", "");
                sug = sug.replace("_", "");
                sug = sug.toLowerCase();

                worth = WorthUtil.ccWorth.get().getDouble(sug);
            }

            if (worth == 0)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_RECOMMAND_DATA"));
            } else
            {
                int numberOfPlayer = DynamicShop.plugin.getConfig().getInt("NumberOfPlayer");
                int sugMid = ShopUtil.CalcRecommendedMedian(worth, numberOfPlayer);

                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("RECOMMAND_APPLIED").replace("{playerNum}", numberOfPlayer + ""));

                DynaShopAPI.openItemSettingGui(player, e.getInventory().getItem(0), tab,
                        worth, worth, newValueMin, newValueMax, sugMid, sugMid);

                SoundUtil.playerSoundEffect(player, "editItem");
            }
            return;
        }

        // 가격,미디언,스톡 탭 이동
        if (e.getSlot() >= 2 && e.getSlot() <= 7)
        {
            SoundUtil.playerSoundEffect(player, "click");
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
        else if (e.getSlot() == 8)
        {
            // 유효성 검사
            if (valueMaxD > 0 && valueBuyD > valueMaxD)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMinD > 0 && valueBuyD < valueMinD)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMaxD > 0 && valueSellD > valueMaxD)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMinD > 0 && valueSellD < valueMinD)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (valueMaxD > 0 && valueMinD > 0 && valueMinD >= valueMaxD)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.MAX_LOWER_THAN_MIN"));
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
                int idx = ShopUtil.findEmptyShopSlot(shopName);

                try
                {
                    idx = Integer.parseInt(DynamicShop.userInteractItem.get(player.getUniqueId()).split("/")[1]);
                } catch (Exception ignored)
                {
                }

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
}
