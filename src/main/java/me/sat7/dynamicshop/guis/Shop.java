package me.sat7.dynamicshop.guis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.transactions.Calc;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public class Shop {

    public Inventory getGui(Player player, String shopName, int page) {
        DecimalFormat df = new DecimalFormat("0.00");
        // jobreborn 플러그인 있는지 확인.
        if(!JobsHook.jobsRebornActive && ShopUtil.ccShop.get().contains(shopName+".Options.flag.jobpoint"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.JOBSREBORN_NOT_FOUND"));
            return null;
        }

        String uiName = "";
        if(ShopUtil.ccShop.get().contains(shopName+".Options.title"))
        {
            uiName = ShopUtil.ccShop.get().getString(shopName+".Options.title");
        }
        else
        {
            uiName = shopName;
        }
        Inventory inventory = Bukkit.createInventory(player,54,"§3"+uiName);

        // 닫기 버튼
        ItemStack closeBtn =  ItemsUtil.createItemStack(Material.BARRIER,null,
                LangUtil.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Arrays.asList(LangUtil.ccLang.get().getString("CLOSE_LORE"))),1);

        inventory.setItem(45,closeBtn);

        // 페이지 버튼
        ArrayList<String> pageLore = new ArrayList<>();
        pageLore.add(LangUtil.ccLang.get().getString("PAGE_LORE"));
        if(player.hasPermission("dshop.admin.shopedit"))
        {
            pageLore.add(LangUtil.ccLang.get().getString("PAGE_INSERT"));
            pageLore.add(LangUtil.ccLang.get().getString("PAGE_DELETE"));
        }

        ItemStack pageBtn =  ItemsUtil.createItemStack(Material.PAPER,null,
                page + "/" + ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options").getInt("page") + " " + LangUtil.ccLang.get().getString("PAGE"),
                pageLore,page);

        inventory.setItem(49,pageBtn);

        // 정보,설정 버튼
        ArrayList<String> infoLore = new ArrayList<>();
        if(ShopUtil.ccShop.get().contains(shopName+".Options.lore"))
        {
            String loreTxt = ShopUtil.ccShop.get().getString(shopName + ".Options.lore");
            if(loreTxt.length() > 0)
            {
                String[] loreArray = loreTxt.split(Pattern.quote("\\n"));
                for (String s: loreArray) {
                    infoLore.add("§f"+s);
                }
            }
        }
        // 권한
        String perm = ShopUtil.ccShop.get().getString(shopName+".Options.permission");
        if(!(perm.length()==0)) {
            infoLore.add(LangUtil.ccLang.get().getString("PERMISSION") + ":");
            infoLore.add("§7 - "+perm);
        }
        // 세금
        if(DynamicShop.plugin.getConfig().getBoolean("ShowTax")) {
            infoLore.add(LangUtil.ccLang.get().getString("TAX.SALESTAX")+":");
            infoLore.add("§7 - "+ Calc.getTaxRate(shopName) + "%");
        }
        // 플래그
        if(ShopUtil.ccShop.get().contains(shopName+".Options.flag") && ShopUtil.ccShop.get().getConfigurationSection(shopName+".Options.flag").getKeys(false).size() > 0)
        {
            infoLore.add(LangUtil.ccLang.get().getString("FLAG")+":");
            for (String s: ShopUtil.ccShop.get().getConfigurationSection(shopName+".Options.flag").getKeys(false))
            {
                infoLore.add("§7 - "+s);
            }
        }
        if(ShopUtil.ccShop.get().contains(shopName+".Options.pos1") && ShopUtil.ccShop.get().contains(shopName+".Options.pos2"))
        {
            infoLore.add(LangUtil.ccLang.get().getString("POSITION"));
            infoLore.add("§7 - "+ ShopUtil.ccShop.get().getString(shopName+".Options.world"));
            infoLore.add("§7 - "+ ShopUtil.ccShop.get().getString(shopName+".Options.pos1"));
            infoLore.add("§7 - "+ ShopUtil.ccShop.get().getString(shopName+".Options.pos2"));
        }
        if(ShopUtil.ccShop.get().contains(shopName+".Options.shophours"))
        {
            String[] temp = ShopUtil.ccShop.get().getString(shopName+".Options.shophours").split("~");
            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            infoLore.add(LangUtil.ccLang.get().getString("TIME.SHOPHOURS"));
            infoLore.add("§7 - " + LangUtil.ccLang.get().getString("TIME.OPEN") + ": " + open);
            infoLore.add("§7 - " + LangUtil.ccLang.get().getString("TIME.CLOSE") + ": " + close);
        }
        // 상점 잔액
        infoLore.add(LangUtil.ccLang.get().getString("SHOP_BAL"));
        if(ShopUtil.getShopBalance(shopName) >= 0)
        {
            String temp = df.format(ShopUtil.getShopBalance(shopName));
            if(ShopUtil.ccShop.get().contains(shopName+".Options.flag.jobpoint")) temp += "Points";

            infoLore.add("§7 - " + temp);
        }
        else
        {
            infoLore.add("§7 - " + ChatColor.stripColor(LangUtil.ccLang.get().getString("SHOP_BAL_INF")));
        }
        // 어드민이면 우클릭
        if(player.hasPermission("dshop.admin.shopedit")) infoLore.add(LangUtil.ccLang.get().getString("RMB_EDIT"));

        ItemStack infoBtn =  ItemsUtil.createItemStack(Material.OAK_SIGN,null, "§3"+shopName, infoLore,1);
        inventory.setItem(53,infoBtn);

        // 상품목록 등록
        for (String s: ShopUtil.ccShop.get().getConfigurationSection(shopName).getKeys(false))
        {
            try
            {
                // 현재 페이지에 해당하는 것들만 출력
                int idx = Integer.parseInt(s);
                idx -= ((page-1)*45);
                if(!(idx < 45 && idx >= 0)) continue;

                // 아이탬 생성
                String itemName = ShopUtil.ccShop.get().getString(shopName +"."+s+".mat"); // 메테리얼
                ItemStack itemStack = new ItemStack(Material.getMaterial(itemName),1); // 아이탬 생성
                itemStack.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + s + ".itemStack")); // 저장된 메타 적용

                // 커스텀 메타 설정
                ItemMeta meta = itemStack.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();

                // 상품
                if(ShopUtil.ccShop.get().contains(shopName+"."+s+".value"))
                {
                    String stockStr;

                    if(ShopUtil.ccShop.get().getInt(shopName+"." + s + ".stock") <= 0)
                    {
                        stockStr = "INF";
                    }
                    else if(DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        stockStr = (ShopUtil.ccShop.get().getInt(shopName+"." + s + ".stock")/64)+" Stacks";
                    }
                    else
                    {
                        stockStr = String.valueOf(ShopUtil.ccShop.get().getInt(shopName+"." + s + ".stock"));
                    }

                    double buyPrice = Calc.getCurrentPrice(shopName, s, true);
                    double sellPrice = Calc.getCurrentPrice(shopName, s, false);
                    /*  */
                    double buyPrice2 = ShopUtil.ccShop.get().getDouble(shopName+"." + s + ".value");
                    double priceSave1 = ((buyPrice/buyPrice2)*100)-100;
                    double priceSave2 = 100-((buyPrice/buyPrice2)*100);
                    
                    String valueChangedRange = null;
                	String valueChangedRange2 = null;
                	
                    if(buyPrice - buyPrice2 > 0) {
                    	valueChangedRange = "§a⬆ " + Math.round(priceSave1*100d)/100d + "%";
                    	valueChangedRange2 = "§a⬆ " + Math.round(priceSave1*100d)/100d + "%";
                    } else if (buyPrice - buyPrice2 < 0) {
                    	valueChangedRange = "§c⬇ " + Math.round(priceSave2*100d)/100d + "%";
                    	valueChangedRange2 = "§c⬇ " + Math.round(priceSave2*100d)/100d + "%";
                    } else if (buyPrice == buyPrice2) {
                    	valueChangedRange = "";
                    	valueChangedRange2 = "";
                    }

                    if(buyPrice == sellPrice) sellPrice = buyPrice - ((buyPrice / 100) * Calc.getTaxRate(shopName));

                    String tradeType = "default";
                    if(ShopUtil.ccShop.get().contains(shopName+"."+s+".tradeType")) tradeType = ShopUtil.ccShop.get().getString(shopName+"."+s+".tradeType");
                    if(!tradeType.equalsIgnoreCase("SellOnly")) lore.add(LangUtil.ccLang.get().getString("PRICE") + df.format(buyPrice) + " " + valueChangedRange);
                    if(!tradeType.equalsIgnoreCase("BuyOnly")) lore.add(LangUtil.ccLang.get().getString("SELLPRICE") + df.format(sellPrice) + " " + valueChangedRange2);
                    /* */
                    if(ShopUtil.ccShop.get().getInt(shopName+"." + s + ".stock") <= 0 || ShopUtil.ccShop.get().getInt(shopName+"." + s + ".median") <= 0)
                    {
                        if(!ShopUtil.ccShop.get().getBoolean(shopName+".Options.hidePricingType"))
                        {
                            lore.add("§7[" + ChatColor.stripColor(LangUtil.ccLang.get().getString("STATICPRICE")) + "]");
                        }
                    }
                    if(!ShopUtil.ccShop.get().getBoolean(shopName+".Options.hideStock"))
                    {
                        lore.add(LangUtil.ccLang.get().getString("STOCK") + stockStr);
                    }
                    if(LangUtil.ccLang.get().getString("TRADE_LORE").length() > 0) lore.add(LangUtil.ccLang.get().getString("TRADE_LORE"));

                    if(player.hasPermission("dshop.admin.shopedit"))
                    {
                        lore.add(LangUtil.ccLang.get().getString("ITEM_MOVE_LORE"));
                        lore.add(LangUtil.ccLang.get().getString("ITEM_EDIT_LORE"));
                    }
                }
                // 장식용
                else
                {
                    if(player.hasPermission("dshop.admin.shopedit"))
                    {
                        lore.add(LangUtil.ccLang.get().getString("ITEM_COPY_LORE"));
                        lore.add(LangUtil.ccLang.get().getString("DECO_DELETE_LORE"));
                    }

                    meta.setDisplayName(" ");
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                }

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                inventory.setItem(idx,itemStack);
            }
            catch (Exception e)
            {
                if(!s.equalsIgnoreCase("Options"))
                {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +"ERR.OpenShopGui/Failed to create itemstack. incomplete data. check yml.");
//                    for(StackTraceElement ste: e.getStackTrace())
//                    {
//                        DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server+ste);
//                    }
                }
            }
        }
        return inventory;
    }
}
