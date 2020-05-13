package me.sat7.dynamicshop.guis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.transactions.Calc;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public class ItemTrade {

    public Inventory getGui(Player player, String shopName, String tradeIdx) {
        // UI 요소 생성
        String title = LangUtil.ccLang.get().getString("TRADE_TITLE");
        Inventory inven = Bukkit.createInventory(player,18,title);

        // 배달비
        ConfigurationSection optionS = ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options");
        int deliverycharge = 0;
        if(optionS.contains("world") && optionS.contains("pos1") && optionS.contains("pos2") && optionS.contains("flag.deliverycharge"))
        {
            boolean sameworld = true;
            boolean outside = false;
            if(!player.getWorld().getName().equals(optionS.getString("world"))) sameworld = false;

            String[] shopPos1 = optionS.getString("pos1").split("_");
            String[] shopPos2 = optionS.getString("pos2").split("_");
            int x1 = Integer.parseInt(shopPos1[0]);
            int y1 = Integer.parseInt(shopPos1[1]);
            int z1 = Integer.parseInt(shopPos1[2]);
            int x2 = Integer.parseInt(shopPos2[0]);
            int y2 = Integer.parseInt(shopPos2[1]);
            int z2 = Integer.parseInt(shopPos2[2]);

            if(!((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2)||
                    (x2 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x1))) outside = true;
            if(!((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2) ||
                    (y2 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y1))) outside = true;
            if(!((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2) ||
                    (z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1))) outside = true;

            if(!sameworld)
            {
                deliverycharge = -1;
            }
            else if(outside)
            {
                Location lo = new Location(player.getWorld(),x1,y1,z1);
                int dist = (int) (player.getLocation().distance(lo) * 0.1 * DynamicShop.plugin.getConfig().getDouble("DeliveryChargeScale"));
                deliverycharge = 1+dist;
            }
        }

        String buyStr = LangUtil.ccLang.get().getString("BUY");
        String sellStr = LangUtil.ccLang.get().getString("SELL");
        String stockStr = LangUtil.ccLang.get().getString("STOCK");
        String tradeStr = ShopUtil.ccShop.get().getString(shopName+"."+tradeIdx+".tradeType");
        if(tradeStr == null) tradeStr = "SB";

        ArrayList<String> sellLore = new ArrayList();
        if(tradeStr.equals("SellOnly")) sellLore.add(LangUtil.ccLang.get().getString("SELLONLY_LORE"));
        if(tradeStr.equals("BuyOnly")) sellLore.add(LangUtil.ccLang.get().getString("BUYONLY_LORE"));
        if(player.hasPermission("dshop.admin.shopedit")) sellLore.add(LangUtil.ccLang.get().getString("TOGGLE_SELLABLE"));

        ArrayList<String> buyLore = new ArrayList();
        if(tradeStr.equals("SellOnly")) buyLore.add(LangUtil.ccLang.get().getString("SELLONLY_LORE"));
        if(tradeStr.equals("BuyOnly")) buyLore.add(LangUtil.ccLang.get().getString("BUYONLY_LORE"));
        if(player.hasPermission("dshop.admin.shopedit")) buyLore.add(LangUtil.ccLang.get().getString("TOGGLE_BUYABLE"));

        ItemStack sellBtn = ItemsUtil.createItemStack(Material.GREEN_STAINED_GLASS,null, sellStr,sellLore,1);
        ItemStack buyBtn = ItemsUtil.createItemStack(Material.RED_STAINED_GLASS,null, buyStr,buyLore,1);
        inven.setItem(1,sellBtn);
        inven.setItem(10,buyBtn);

        String mat = ShopUtil.ccShop.get().getString(shopName+"."+tradeIdx+".mat");
        // 판매
        if(!tradeStr.equals("BuyOnly"))
        {
            int amount = 1;
            int idx = 2;
            for (int i = 1; i < 8; i++)
            {
                String priceStr = LangUtil.ccLang.get().getString("SELLPRICE");

                ItemStack sell = new ItemStack(Material.getMaterial(mat),amount);
                sell.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                ItemMeta meta = sell.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(sellStr + " x" + amount);
                lore.add(priceStr + Calc.calcTotalCost(shopName,tradeIdx,-amount));

                if(!ShopUtil.ccShop.get().getBoolean(shopName+".Options.hideStock"))
                {
                    if(ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") <= 0)
                    {
                        lore.add(stockStr+"INF");
                    }
                    else if(DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        lore.add(stockStr+(ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock")/64)+" Stacks");
                    }
                    else
                    {
                        lore.add(stockStr+ ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock"));
                    }
                }

                if(deliverycharge>0) lore.add(LangUtil.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);

                meta.setLore(lore);

                sell.setItemMeta(meta);

                inven.setItem(idx,sell);

                idx++;
                amount = amount * 2;
            }
        }

        // 구매
        if(!tradeStr.equals("SellOnly"))
        {
            String priceStr = LangUtil.ccLang.get().getString("PRICE");

            int amount = 1;
            int idx = 11;
            for (int i = 1; i < 8; i++)
            {
                ItemStack buy = new ItemStack(Material.getMaterial(mat),amount);
                buy.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                ItemMeta meta = buy.getItemMeta();

                ArrayList<String> lore = new ArrayList<>();
                lore.add(buyStr + " x" + amount);
                lore.add(priceStr + Calc.calcTotalCost(shopName,tradeIdx,amount));

                if(ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") != -1)
                {
                    if(ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") <= amount)
                    {
                        continue;
                    }
                }

                if(!ShopUtil.ccShop.get().getBoolean(shopName+".Options.hideStock"))
                {
                    if(ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") <= 0)
                    {
                        lore.add(stockStr+"INF");
                    }
                    else if(DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        lore.add(stockStr+(ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock")/64)+" Stacks");
                    }
                    else
                    {
                        lore.add(stockStr+ ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock"));
                    }
                }

                if(deliverycharge>0) lore.add(LangUtil.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);

                meta.setLore(lore);

                buy.setItemMeta(meta);

                inven.setItem(idx,buy);

                idx++;
                amount = amount * 2;
            }
        }

        // 잔액 버튼
        ArrayList<String> moneyLore = new ArrayList<>();
        if(optionS.contains("flag.jobpoint"))
        {
            DecimalFormat df = new DecimalFormat("0.00");
            moneyLore.add("§f" + df.format(JobsHook.getCurJobPoints(player)) + "Points");
        }
        else
        {
            moneyLore.add("§f" + DynamicShop.getEconomy().format(DynamicShop.getEconomy().getBalance(player)));
        }
        String balStr = "";
        if(ShopUtil.getShopBalance(shopName) >= 0)
        {
            double d = ShopUtil.getShopBalance(shopName);
            balStr = DynamicShop.getEconomy().format(d);
            if(optionS.contains("flag.jobpoint")) balStr += "Points";
        }
        else
        {
            balStr = LangUtil.ccLang.get().getString("SHOP_BAL_INF");
        }
        moneyLore.add("§3" + ChatColor.stripColor(LangUtil.ccLang.get().getString("SHOP_BAL")));
        moneyLore.add("§f" + balStr);

        ItemStack balBtn = ItemsUtil.createItemStack(Material.EMERALD,null,
                LangUtil.ccLang.get().getString("BALANCE"), moneyLore,1);

        inven.setItem(0,balBtn);

        // 닫기 버튼
        ItemStack closeBtn = ItemsUtil.createItemStack(Material.BARRIER,null,
                LangUtil.ccLang.get().getString("CLOSE"), new ArrayList<>(Arrays.asList(LangUtil.ccLang.get().getString("CLOSE_LORE"))),1);

        inven.setItem(9,closeBtn);
        return inven;
    }
}
