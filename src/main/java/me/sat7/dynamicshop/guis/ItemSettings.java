package me.sat7.dynamicshop.guis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import me.sat7.dynamicshop.utilities.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.models.DSItem;
import me.sat7.dynamicshop.transactions.Calc;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public class ItemSettings {

    public Inventory getGui(Player player, int tab, DSItem dsItem) {
        String[] temp = DynamicShop.ccUser.get().getString(player.getUniqueId()+".interactItem").split("/");
        String shopName = temp[0];

        // UI 요소 생성
        String title = LangUtil.ccLang.get().getString("ITEM_SETTING_TITLE");
        Inventory inven = Bukkit.createInventory(player,36,title);

        String buyValueStr = LangUtil.ccLang.get().getString("VALUE_BUY")+dsItem.getBuyValue();
        String sellValueStr = LangUtil.ccLang.get().getString("VALUE_SELL")+dsItem.getSellValue();
        String priceMinStr = LangUtil.ccLang.get().getString("PRICE_MIN")+dsItem.getMinPrice();
        String priceMaxStr = LangUtil.ccLang.get().getString("PRICE_MAX")+dsItem.getMaxPrice();
        String medianStr = LangUtil.ccLang.get().getString("MEDIAN")+dsItem.getMedian();
        String stockStr = LangUtil.ccLang.get().getString("STOCK")+dsItem.getStock();

        ArrayList<String> sellValueLore = new ArrayList<>();
        ArrayList<String> medianLore = new ArrayList<>();
        ArrayList<String> stockLore = new ArrayList<>();
        ArrayList<String> maxPriceLore = new ArrayList<>();

        // 고정가, 무한재고, 별도판매가 안내 표시
        if(dsItem.getBuyValue() != dsItem.getSellValue())
        {
            sellValueLore.add("§7("+ LangUtil.ccLang.get().getString("TAXIGNORED")+")");
        }
        if(dsItem.getMedian() <= 0)
        {
            medianLore.add("§7("+ LangUtil.ccLang.get().getString("STATICPRICE")+")");
        }
        if(dsItem.getStock() <= 0)
        {
            stockLore.add("§7("+ LangUtil.ccLang.get().getString("INFSTOCK")+")");
        }
        if(dsItem.getMaxPrice() <=0)
        {
            maxPriceLore.add("§7("+ LangUtil.ccLang.get().getString("UNLIMITED")+")");
        }

        // 가격, 미디안, 스톡 버튼
        ItemStack buyValueBtn = ItemsUtil.createItemStack((tab==1) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE,null, buyValueStr,null,1);
        ItemStack sellValueBtn = ItemsUtil.createItemStack((tab==2) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,null, sellValueStr,sellValueLore,1);
        ItemStack minValueBtn = ItemsUtil.createItemStack((tab==3) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,null, priceMinStr,null,1);
        ItemStack maxValueBtn = ItemsUtil.createItemStack((tab==4) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,null, priceMaxStr,maxPriceLore,1);
        ItemStack medianBtn = ItemsUtil.createItemStack((tab==5) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE,null, medianStr,medianLore,1);
        ItemStack stockBtn = ItemsUtil.createItemStack((tab==6) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE,null, stockStr,stockLore,1);
        inven.setItem(2,buyValueBtn);
        inven.setItem(3,sellValueBtn);
        inven.setItem(4,minValueBtn);
        inven.setItem(5,maxValueBtn);
        inven.setItem(6,medianBtn);
        inven.setItem(7,stockBtn);

        ItemStack infoBtn = ItemsUtil.createItemStack(Material.BLACK_STAINED_GLASS_PANE,null, "Shift = x5",null,1);
        inven.setItem(22,infoBtn);

        // 조절버튼
        if(dsItem.getBuyValue() == dsItem.getSellValue()) sellValueStr = "§7"+ ChatColor.stripColor(sellValueStr);
        if(dsItem.getMinPrice() <= 0.01) priceMinStr = "§7"+ ChatColor.stripColor(priceMinStr);
        if(dsItem.getMaxPrice() <=0) priceMaxStr = "§7"+ChatColor.stripColor(LangUtil.ccLang.get().getString("PRICE_MAX") + LangUtil.ccLang.get().getString("UNLIMITED"));

        ArrayList<String> editBtnLore = new ArrayList<>();
        editBtnLore.add("§3§m                       ");
        if(tab == 1)
        {
            buyValueStr = "§3>" + buyValueStr;
        }
        else if(tab == 2)
        {
            sellValueStr = "§3>" + sellValueStr;
        }
        else if(tab == 3)
        {
            priceMinStr = "§3>" + priceMinStr;
        }
        else if(tab == 4)
        {
            priceMaxStr = "§3>" + priceMaxStr;
        }
        else if(tab == 5)
        {
            medianStr = "§3>" + medianStr;
        }
        else if(tab == 6)
        {
            stockStr = "§3>" + stockStr;
        }

        if(dsItem.getMedian() <= 0) medianStr = medianStr + "§7("+ LangUtil.ccLang.get().getString("STATICPRICE")+")";
        if(dsItem.getStock() <= 0) stockStr = stockStr + "§7("+ LangUtil.ccLang.get().getString("INFSTOCK")+")";

        editBtnLore.add(buyValueStr);
        editBtnLore.add(sellValueStr);
        editBtnLore.add(priceMinStr);
        editBtnLore.add(priceMaxStr);
        editBtnLore.add(medianStr);
        editBtnLore.add(stockStr);

        editBtnLore.add("§3§m                       ");
        double buyPrice = 0;
        double sellPrice = 0;
        if(dsItem.getMedian() <= 0 || dsItem.getStock() <= 0 )
        {
            buyPrice = dsItem.getBuyValue();
            if(dsItem.getBuyValue() != dsItem.getSellValue())
            {
                editBtnLore.add("§7"+ChatColor.stripColor(LangUtil.ccLang.get().getString("TAXIGNORED")));
                sellPrice = dsItem.getSellValue();
            }
            else
            {
                String taxStr = "§7"+ChatColor.stripColor(LangUtil.ccLang.get().getString("TAX.SALESTAX")) + ": ";
                taxStr += Calc.getTaxRate(shopName) + "%";
                editBtnLore.add(taxStr);
                sellPrice = buyPrice - ((buyPrice / 100) * Calc.getTaxRate(shopName));
            }
        }
        else
        {
            buyPrice = (dsItem.getBuyValue()*dsItem.getMedian())/dsItem.getStock();
            if(dsItem.getBuyValue() != dsItem.getSellValue()) // 판매가 별도설정
            {
                editBtnLore.add("§7"+ChatColor.stripColor(LangUtil.ccLang.get().getString("TAXIGNORED")));
                sellPrice = (dsItem.getSellValue()*dsItem.getMedian())/dsItem.getStock();
            }
            else
            {
                String taxStr = "§7"+ChatColor.stripColor(LangUtil.ccLang.get().getString("TAX.SALESTAX")) + ": ";
                if(ShopUtil.ccShop.get().contains(shopName+".Options.SalesTax"))
                {
                    taxStr += ShopUtil.ccShop.get().getInt(shopName+".Options.SalesTax") + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * ShopUtil.ccShop.get().getInt(shopName+".Options.SalesTax"));
                }
                else
                {
                    taxStr += ConfigUtil.getCurrentTax() + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * ConfigUtil.getCurrentTax());
                }
                sellPrice = (Math.round(sellPrice*100)/100.0);

                editBtnLore.add(taxStr);
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        editBtnLore.add("§3§l"+ChatColor.stripColor(LangUtil.ccLang.get().getString("BUY")) + ": " + df.format(buyPrice));
        editBtnLore.add("§3§l"+ChatColor.stripColor(LangUtil.ccLang.get().getString("SELL")) + ": " + df.format(sellPrice));

        ItemStack d2Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "/2",editBtnLore,1);
        ItemStack m1000Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-1000",editBtnLore,1);
        ItemStack m100Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-100",editBtnLore,1);
        ItemStack m10Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-10",editBtnLore,1);
        ItemStack m1Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-1",editBtnLore,1);
        ItemStack m01Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-0.1",editBtnLore,1);
        ItemStack reset = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "Reset",editBtnLore,1);
        ItemStack p01Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+0.1",editBtnLore,1);
        ItemStack p1Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+1",editBtnLore,1);
        ItemStack p10Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+10",editBtnLore,1);
        ItemStack p100Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+100",editBtnLore,1);
        ItemStack p1000Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+1000",editBtnLore,1);
        ItemStack m2Btn = ItemsUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "x2",editBtnLore,1);
        ItemStack roundBtn = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, LangUtil.ccLang.get().getString("ROUNDDOWN"),editBtnLore,1);
        ItemStack setToMedian = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, LangUtil.ccLang.get().getString("SETTOMEDIAN"),editBtnLore,1);
        ItemStack setToStock = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, LangUtil.ccLang.get().getString("SETTOSTOCK"),editBtnLore,1);
        ItemStack setToValue = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, LangUtil.ccLang.get().getString("SETTOVALUE"),editBtnLore,1);

        // 내림 버튼
        inven.setItem(20,roundBtn);

        // 리셋버튼
        inven.setItem(13,reset);

        // 곱하기,나누기
        inven.setItem(21,d2Btn);
        inven.setItem(23,m2Btn);

        // +, -, ~에 맞추기
        if(tab <= 4)
        {
            inven.setItem(9,m100Btn);
            inven.setItem(10,m10Btn);
            inven.setItem(11,m1Btn);
            inven.setItem(12,m01Btn);
            inven.setItem(14,p01Btn);
            inven.setItem(15,p1Btn);
            inven.setItem(16,p10Btn);
            inven.setItem(17,p100Btn);
            if(tab >= 2)
            {
                inven.setItem(24,setToValue);
            }
        }
        else
        {
            inven.setItem(9,m1000Btn);
            inven.setItem(10,m100Btn);
            inven.setItem(11,m10Btn);
            inven.setItem(12,m1Btn);
            inven.setItem(14,p1Btn);
            inven.setItem(15,p10Btn);
            inven.setItem(16,p100Btn);
            inven.setItem(17,p1000Btn);
            if(tab == 5)
            {
                inven.setItem(24,setToStock);
            }
            else if(tab == 6)
            {
                inven.setItem(24,setToMedian);
            }
        }

        // 아이탬 견본
        inven.setItem(0,dsItem.getItemStack());

        // 완료 버튼
        ItemStack doneBtn = ItemsUtil.createItemStack(Material.STRUCTURE_VOID,null,
                LangUtil.ccLang.get().getString("DONE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("DONE_LORE"))),1);

        inven.setItem(8,doneBtn);

        // 닫기 버튼
        ItemStack closeBtn = ItemsUtil.createItemStack(Material.BARRIER,null,
                LangUtil.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("CLOSE_LORE"))),1);
        inven.setItem(27,closeBtn);

        // 추천 버튼
        ItemStack recBtn = ItemsUtil.createItemStack(Material.NETHER_STAR,null,
                LangUtil.ccLang.get().getString("RECOMMEND"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("RECOMMEND_LORE"))),1);
        inven.setItem(31,recBtn);

        // 삭제 버튼
        ItemStack removeBtn = ItemsUtil.createItemStack(Material.BONE,null,
                LangUtil.ccLang.get().getString("REMOVE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("REMOVE_LORE"))),1);
        inven.setItem(35,removeBtn);
        return inven;
    }
}
