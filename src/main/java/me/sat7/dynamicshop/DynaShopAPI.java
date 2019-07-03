package me.sat7.dynamicshop;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerPoints;
import me.sat7.dynamicshop.Files.CustomConfig;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DynaShopAPI {

    public static DecimalFormat df = new DecimalFormat("0.00");

    //[ UI ]=========================================================

    // 2틱 후 인벤토리 닫기
    public static void CloseInventoryWithDelay(Player player)
    {
        Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, player::closeInventory,2);
    }

    // 상점 UI생성 후 열기
    public static void OpenShopGUI(Player player, String shopName, int page)
    {
        // jobreborn 플러그인 있는지 확인.
        if(!DynamicShop.jobsRebornActive && DynamicShop.ccShop.get().contains(shopName+".Options.flag.jobpoint"))
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("ERR.JOBSREBORN_NOT_FOUND"));
            return;
        }

        String uiName = "";
        if(DynamicShop.ccShop.get().contains(shopName+".Options.title"))
        {
            uiName = DynamicShop.ccShop.get().getString(shopName+".Options.title");
        }
        else
        {
            uiName = shopName;
        }
        Inventory vault = Bukkit.createInventory(player,54,"§3"+uiName);

        // 닫기 버튼
        ItemStack closeBtn =  CreateItemStack(Material.BARRIER,null,
                DynamicShop.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("CLOSE_LORE"))),1);

        vault.setItem(45,closeBtn);

        // 페이지 버튼
        ArrayList<String> pageLore = new ArrayList<>();
        pageLore.add(DynamicShop.ccLang.get().getString("PAGE_LORE"));
        if(player.hasPermission("dshop.admin.shopedit"))
        {
            pageLore.add(DynamicShop.ccLang.get().getString("PAGE_INSERT"));
            pageLore.add(DynamicShop.ccLang.get().getString("PAGE_DELETE"));
        }

        ItemStack pageBtn =  CreateItemStack(Material.PAPER,null,
                page + "/" + DynamicShop.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options").getInt("page") + " " + DynamicShop.ccLang.get().getString("PAGE"),
                pageLore,page);

        vault.setItem(49,pageBtn);

        // 정보,설정 버튼
        ArrayList<String> infoLore = new ArrayList<>();
        // 권한
        String perm = DynamicShop.ccShop.get().getString(shopName+".Options.permission");
        if(perm.length()==0) perm = "§7(NULL)";
        infoLore.add(DynamicShop.ccLang.get().getString("PERMISSION") + ":");
        infoLore.add("§7 - "+perm);
        // 세금
        infoLore.add(DynamicShop.ccLang.get().getString("TAX.SALESTAX")+":");
        infoLore.add("§7 - "+ GetTaxRate(shopName) + "%");
        // 플래그
        if(DynamicShop.ccShop.get().contains(shopName+".Options.flag") && DynamicShop.ccShop.get().getConfigurationSection(shopName+".Options.flag").getKeys(false).size() > 0)
        {
            infoLore.add(DynamicShop.ccLang.get().getString("FLAG")+":");
            for (String s:DynamicShop.ccShop.get().getConfigurationSection(shopName+".Options.flag").getKeys(false))
            {
                infoLore.add("§7 - "+s);
            }
        }
        if(DynamicShop.ccShop.get().contains(shopName+".Options.pos1") && DynamicShop.ccShop.get().contains(shopName+".Options.pos2"))
        {
            infoLore.add(DynamicShop.ccLang.get().getString("POSITION"));
            infoLore.add("§7 - "+DynamicShop.ccShop.get().getString(shopName+".Options.world"));
            infoLore.add("§7 - "+DynamicShop.ccShop.get().getString(shopName+".Options.pos1"));
            infoLore.add("§7 - "+DynamicShop.ccShop.get().getString(shopName+".Options.pos2"));
        }
        if(DynamicShop.ccShop.get().contains(shopName+".Options.shophours"))
        {
            String[] temp = DynamicShop.ccShop.get().getString(shopName+".Options.shophours").split("~");
            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            infoLore.add(DynamicShop.ccLang.get().getString("TIME.SHOPHOURS"));
            infoLore.add("§7 - " + DynamicShop.ccLang.get().getString("TIME.OPEN") + ": " + open);
            infoLore.add("§7 - " + DynamicShop.ccLang.get().getString("TIME.CLOSE") + ": " + close);
        }
        // 상점 잔액
        infoLore.add(DynamicShop.ccLang.get().getString("SHOP_BAL"));
        if(GetShopBalance(shopName) >= 0)
        {
            String temp = df.format(GetShopBalance(shopName));
            if(DynamicShop.ccShop.get().contains(shopName+".Options.flag.jobpoint")) temp += "Points";

            infoLore.add("§7 - " + temp);
        }
        else
        {
            infoLore.add("§7 - " + ChatColor.stripColor(DynamicShop.ccLang.get().getString("SHOP_BAL_INF")));
        }
        // 어드민이면 우클릭
        if(player.hasPermission("dshop.admin.shopedit")) infoLore.add(DynamicShop.ccLang.get().getString("RMB_EDIT"));

        ItemStack infoBtn =  CreateItemStack(Material.LEGACY_SIGN,null, "§3"+shopName, infoLore,1);
        vault.setItem(53,infoBtn);

        // 상품목록 등록
        for (String s:DynamicShop.ccShop.get().getConfigurationSection(shopName).getKeys(false))
        {
            try
            {
                // 현재 페이지에 해당하는 것들만 출력
                int idx = Integer.parseInt(s);
                idx -= ((page-1)*45);
                if(!(idx < 45 && idx >= 0)) continue;

                // 아이탬 생성
                String itemName = DynamicShop.ccShop.get().getString(shopName +"."+s+".mat"); // 메테리얼
                ItemStack itemStack = new ItemStack(Material.getMaterial(itemName),1); // 아이탬 생성
                itemStack.setItemMeta((ItemMeta) DynamicShop.ccShop.get().get(shopName + "." + s + ".itemStack")); // 저장된 메타 적용

                // 커스텀 메타 설정
                ItemMeta meta = itemStack.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();

                // 상품
                if(DynamicShop.ccShop.get().contains(shopName+"."+s+".value"))
                {
                    String stockStr;

                    if(DynamicShop.ccShop.get().getInt(shopName+"." + s + ".stock") <= 0)
                    {
                        stockStr = "INF";
                    }
                    else if(DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        stockStr = (DynamicShop.ccShop.get().getInt(shopName+"." + s + ".stock")/64)+" Stacks";
                    }
                    else
                    {
                        stockStr = String.valueOf(DynamicShop.ccShop.get().getInt(shopName+"." + s + ".stock"));
                    }

                    double buyPrice = GetCurrentPrice(shopName, s, true);
                    double sellPrice = GetCurrentPrice(shopName, s, false);
                    if(buyPrice == sellPrice) sellPrice = buyPrice - ((buyPrice / 100) * GetTaxRate(shopName));

                    String tradeType = "default";
                    if(DynamicShop.ccShop.get().contains(shopName+"."+s+".tradeType")) tradeType = DynamicShop.ccShop.get().getString(shopName+"."+s+".tradeType");
                    if(!tradeType.equalsIgnoreCase("SellOnly")) lore.add(DynamicShop.ccLang.get().getString("PRICE") + df.format(buyPrice));
                    if(!tradeType.equalsIgnoreCase("BuyOnly")) lore.add(DynamicShop.ccLang.get().getString("SELLPRICE") + df.format(sellPrice));

                    if(DynamicShop.ccShop.get().getInt(shopName+"." + s + ".stock") <= 0 || DynamicShop.ccShop.get().getInt(shopName+"." + s + ".median") <= 0)
                    {
                        if(!DynamicShop.ccShop.get().getBoolean(shopName+".Options.hidePricingType"))
                        {
                            lore.add("§7[" + ChatColor.stripColor(DynamicShop.ccLang.get().getString("STATICPRICE")) + "]");
                        }
                    }
                    if(!DynamicShop.ccShop.get().getBoolean(shopName+".Options.hideStock"))
                    {
                        lore.add(DynamicShop.ccLang.get().getString("STOCK") + stockStr);
                    }
                    lore.add(DynamicShop.ccLang.get().getString("TRADE_LORE"));

                    if(player.hasPermission("dshop.admin.shopedit"))
                    {
                        lore.add(DynamicShop.ccLang.get().getString("ITEM_MOVE_LORE"));
                        lore.add(DynamicShop.ccLang.get().getString("ITEM_EDIT_LORE"));
                    }
                }
                // 장식용
                else
                {
                    if(player.hasPermission("dshop.admin.shopedit"))
                    {
                        lore.add(DynamicShop.ccLang.get().getString("ITEM_COPY_LORE"));
                        lore.add(DynamicShop.ccLang.get().getString("DECO_DELETE_LORE"));
                    }

                    meta.setDisplayName(" ");
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                }

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                vault.setItem(idx,itemStack);
            }
            catch (Exception e)
            {
                if(!s.equalsIgnoreCase("Options"))
                {
                    DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server+"ERR.OpenShopGui/Failed to create itemstack. incomplete data. check yml.");
//                    for(StackTraceElement ste: e.getStackTrace())
//                    {
//                        DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server+ste);
//                    }
                }
            }
        }

        player.openInventory(vault);
    }

    // 상점 설정 화면
    public static void OpenShopSettingUI(Player player,String shopName)
    {
        Inventory vault = Bukkit.createInventory(player,36,DynamicShop.ccLang.get().getString("SHOP_SETTING_TITLE"));

        // 닫기 버튼
        ItemStack closeBtn =  CreateItemStack(Material.BARRIER,null,
                DynamicShop.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("CLOSE_LORE"))),1);
        vault.setItem(27,closeBtn);

        ConfigurationSection confSec_Options = DynamicShop.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options");
        String permStr = confSec_Options.getString("permission");
        String permNew = "dshop.user.shop."+shopName;
        Material permIcon;
        if(permStr.isEmpty())
        {
            permStr = DynamicShop.ccLang.get().getString("NULL(OPEN)");
            permIcon = Material.IRON_BLOCK;
        }
        else
        {
            permNew = DynamicShop.ccLang.get().getString("NULL(OPEN)");
            permIcon = Material.GOLD_BLOCK;
        }

        // 권한 버튼
        ArrayList<String> permLore = new ArrayList<>();
        permLore.add("§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + permStr);
        if(!permStr.equalsIgnoreCase(DynamicShop.ccLang.get().getString("NULL(OPEN)")))
        {
            permLore.add("§9 - " + permStr + ".buy");
            permLore.add("§9 - " + permStr + ".sell");
        }
        permLore.add("§e"+DynamicShop.ccLang.get().getString("CLICK")+": " + permNew);

        ItemStack permBtn =  CreateItemStack(permIcon,null,
                DynamicShop.ccLang.get().getString("PERMISSION"),permLore,1);
        vault.setItem(0,permBtn);

        // 최대페이지 버튼
        ItemStack maxPageBtn =  CreateItemStack(Material.PAPER,null,
                DynamicShop.ccLang.get().getString("MAXPAGE"),
                new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("MAXPAGE_LORE"),DynamicShop.ccLang.get().getString("L_R_SHIFT"))),DynamicShop.ccShop.get().getInt(shopName+".Options.page"));
        vault.setItem(1,maxPageBtn);

        // 영업시간 버튼
        int curTime = (int)(player.getWorld().getTime())/1000 + 6;
        if(curTime>24) curTime -= 24;
        if(DynamicShop.ccShop.get().contains(shopName+".Options.shophours"))
        {
            String[] temp = DynamicShop.ccShop.get().getString(shopName+".Options.shophours").split("~");
            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            ItemStack open24Btn =  CreateItemStack(Material.CLOCK,null,
                    DynamicShop.ccLang.get().getString("TIME.SHOPHOURS"),
                    new ArrayList<>(Arrays.asList(
                            DynamicShop.ccLang.get().getString("TIME.CUR").replace("{time}",curTime+""),
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": ",
                            "§9 - " + DynamicShop.ccLang.get().getString("TIME.OPEN") + ": " + open,
                            "§9 - " + DynamicShop.ccLang.get().getString("TIME.CLOSE") + ": " + close,
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("TIME.OPEN24"))),
                    1);
            vault.setItem(6,open24Btn);
            ItemStack opentimeBtn =  CreateItemStack(Material.CLOCK,null,
                    "§f"+DynamicShop.ccLang.get().getString("TIME.OPEN"),
                    new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("TIME.OPEN_LORE"),DynamicShop.ccLang.get().getString("L_R_SHIFT"))), open);
            vault.setItem(7,opentimeBtn);
            ItemStack closetimeBtn =  CreateItemStack(Material.CLOCK,null,
                    "§f"+DynamicShop.ccLang.get().getString("TIME.CLOSE"),
                    new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("TIME.CLOSE_LORE"),DynamicShop.ccLang.get().getString("L_R_SHIFT"))), close);
            vault.setItem(8,closetimeBtn);
        }
        else
        {
            ItemStack open24Btn =  CreateItemStack(Material.CLOCK,null,
                    DynamicShop.ccLang.get().getString("TIME.SHOPHOURS"),
                    new ArrayList<>(Arrays.asList(
                            DynamicShop.ccLang.get().getString("TIME.CUR").replace("{time}",curTime+""),
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + DynamicShop.ccLang.get().getString("TIME.OPEN24"),
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("TIME.SET_SHOPHOURS"))),
                    1);
            vault.setItem(6,open24Btn);
        }

        // 랜덤스톡 버튼
        ConfigurationSection flucConf = DynamicShop.ccShop.get().getConfigurationSection(shopName+".Options.fluctuation");
        if(flucConf != null)
        {
            ItemStack flucToggleBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("FLUC.FLUCTUATION"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": "+DynamicShop.ccLang.get().getString("ON"),
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("OFF"))),
                    1);
            vault.setItem(15,flucToggleBtn);

            ItemStack flucIntervalBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("FLUC.INTERVAL"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + flucConf.getInt("interval")/2.0 + "h",
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("L_R_SHIFT"))),
                    flucConf.getInt("interval"));
            vault.setItem(16,flucIntervalBtn);

            ItemStack flucStrengthBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("FLUC.STRENGTH"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": ~" + flucConf.get("strength") + "%",
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("L_R_SHIFT"))),
                    flucConf.getInt("strength"));
            vault.setItem(17,flucStrengthBtn);
        }
        else
        {
            ItemStack flucToggleBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("FLUC.FLUCTUATION"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": "+DynamicShop.ccLang.get().getString("OFF"),
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("ON"))),
                    1);
            vault.setItem(15,flucToggleBtn);
        }

        // 재고 안정화 버튼
        ConfigurationSection stockStableConf = DynamicShop.ccShop.get().getConfigurationSection(shopName+".Options.stockStabilizing");
        if(stockStableConf != null)
        {
            ItemStack ssTogleBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("STOCKSTABILIZING.SS"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": "+DynamicShop.ccLang.get().getString("ON"),
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("OFF"))),
                    1);
            vault.setItem(24,ssTogleBtn);

            ItemStack intervalBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("FLUC.INTERVAL"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + stockStableConf.getInt("interval")/2.0 + "h",
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("L_R_SHIFT"))),
                    stockStableConf.getInt("interval"));
            vault.setItem(25,intervalBtn);

            ItemStack strengthBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("FLUC.STRENGTH"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": ~" + stockStableConf.get("strength") + "%",
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("STOCKSTABILIZING.L_R_SHIFT"))),
                    (int)(stockStableConf.getDouble("strength")*10));
            vault.setItem(26,strengthBtn);
        }
        else
        {
            ItemStack ssToggleBtn =  CreateItemStack(Material.COMPARATOR,null,
                    DynamicShop.ccLang.get().getString("STOCKSTABILIZING.SS"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": "+DynamicShop.ccLang.get().getString("OFF"),
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": "+DynamicShop.ccLang.get().getString("ON"))),
                    1);
            vault.setItem(24,ssToggleBtn);
        }

        // 세금
        int globalTax = DynamicShop.plugin.getConfig().getInt("SalesTax");
        if(DynamicShop.ccShop.get().contains(shopName+".Options.SalesTax"))
        {
            ItemStack taxToggleBtn =  CreateItemStack(Material.IRON_INGOT,null,
                    DynamicShop.ccLang.get().getString("TAX.SALESTAX"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + DynamicShop.ccLang.get().getString("TAX.USE_LOCAL"),
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": " +
                                    DynamicShop.ccLang.get().getString("TAX.USE_GLOBAL").replace("{tax}",globalTax+"")
                    )),1);
            vault.setItem(33,taxToggleBtn);

            ItemStack taxBtn =  CreateItemStack(Material.IRON_INGOT,null,
                    DynamicShop.ccLang.get().getString("TAX.SALESTAX"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + DynamicShop.ccShop.get().getInt(shopName + ".Options.SalesTax") + "%",
                            DynamicShop.ccLang.get().getString("L_R_SHIFT")
                    )),DynamicShop.ccShop.get().getInt(shopName+".Options.SalesTax"));
            vault.setItem(34,taxBtn);
        }
        else
        {
            ItemStack taxToggleBtn =  CreateItemStack(Material.IRON_INGOT,null,
                    DynamicShop.ccLang.get().getString("TAX.SALESTAX"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " +
                                    DynamicShop.ccLang.get().getString("TAX.USE_GLOBAL").replace("{tax}",globalTax+""),
                            "§e"+DynamicShop.ccLang.get().getString("CLICK")+": " + DynamicShop.ccLang.get().getString("TAX.USE_LOCAL")
                    )),1);
            vault.setItem(33,taxToggleBtn);
        }

        // 플래그 버튼들
        String cur1;
        String set1;
        Material icon1;
        if(confSec_Options.contains("flag.signshop"))
        {
            icon1 = Material.GREEN_STAINED_GLASS_PANE;
            cur1 = DynamicShop.ccLang.get().getString("SET");
            set1 = DynamicShop.ccLang.get().getString("UNSET");
        }
        else
        {
            icon1 = Material.BLACK_STAINED_GLASS_PANE;
            cur1 = DynamicShop.ccLang.get().getString("UNSET");
            set1 = DynamicShop.ccLang.get().getString("SET");
        }
        ArrayList<String> f1Lore = new ArrayList<>();
        f1Lore.add(DynamicShop.ccLang.get().getString("SIGNSHOP_LORE"));
        f1Lore.add("§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + cur1);
        f1Lore.add("§e"+DynamicShop.ccLang.get().getString("CLICK")+": " + set1);

        ItemStack flag1 =  CreateItemStack(icon1,null,
                DynamicShop.ccLang.get().getString("FLAG")+": signshop",
                f1Lore,1);
        vault.setItem(9,flag1);

        String cur2;
        String set2;
        Material icon2;
        if(confSec_Options.contains("flag.localshop"))
        {
            icon2 = Material.GREEN_STAINED_GLASS_PANE;
            cur2 = DynamicShop.ccLang.get().getString("SET");
            set2 = DynamicShop.ccLang.get().getString("UNSET");
        }
        else
        {
            icon2 = Material.BLACK_STAINED_GLASS_PANE;
            cur2 = DynamicShop.ccLang.get().getString("UNSET");
            set2 = DynamicShop.ccLang.get().getString("SET");
        }
        ArrayList<String> f2Lore = new ArrayList<>();
        f2Lore.add(DynamicShop.ccLang.get().getString("LOCALSHOP_LORE"));
        f2Lore.add(DynamicShop.ccLang.get().getString("LOCALSHOP_LORE2"));
        f2Lore.add("§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + cur2);
        f2Lore.add("§e"+DynamicShop.ccLang.get().getString("CLICK")+": " + set2);

        ItemStack flag2 =  CreateItemStack(icon2,null,
                DynamicShop.ccLang.get().getString("FLAG")+": localshop",
                f2Lore,1);
        vault.setItem(10,flag2);

        String cur3;
        String set3;
        Material icon3;
        if(confSec_Options.contains("flag.deliverycharge"))
        {
            icon3 = Material.GREEN_STAINED_GLASS_PANE;
            cur3 = DynamicShop.ccLang.get().getString("SET");
            set3 = DynamicShop.ccLang.get().getString("UNSET");
        }
        else
        {
            icon3 = Material.BLACK_STAINED_GLASS_PANE;
            cur3 = DynamicShop.ccLang.get().getString("UNSET");
            set3 = DynamicShop.ccLang.get().getString("SET");
        }
        ArrayList<String> f3Lore = new ArrayList<>();
        f3Lore.add(DynamicShop.ccLang.get().getString("DELIVERYCHARG_LORE"));
        f3Lore.add("§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + cur3);
        f3Lore.add("§e"+DynamicShop.ccLang.get().getString("CLICK")+": " + set3);

        ItemStack flag3 =  CreateItemStack(icon3,null,
                DynamicShop.ccLang.get().getString("FLAG")+": deliverycharge",
                f3Lore,1);
        vault.setItem(11,flag3);

        String cur4;
        String set4;
        Material icon4;
        if(confSec_Options.contains("flag.jobpoint"))
        {
            icon4 = Material.GREEN_STAINED_GLASS_PANE;
            cur4 = DynamicShop.ccLang.get().getString("SET");
            set4 = DynamicShop.ccLang.get().getString("UNSET");
        }
        else
        {
            icon4 = Material.BLACK_STAINED_GLASS_PANE;
            cur4 = DynamicShop.ccLang.get().getString("UNSET");
            set4 = DynamicShop.ccLang.get().getString("SET");
        }
        ArrayList<String> f4Lore = new ArrayList<>();
        f4Lore.add(DynamicShop.ccLang.get().getString("JOBPOINT_LORE"));
        f4Lore.add("§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + cur4);
        f4Lore.add("§e"+DynamicShop.ccLang.get().getString("CLICK")+": " + set4);

        ItemStack flag4 =  CreateItemStack(icon4,null,
                DynamicShop.ccLang.get().getString("FLAG")+": jobpoint",
                f4Lore,1);
        vault.setItem(12,flag4);

        // 로그 버튼
        String log_cur;
        String log_set;
        if(confSec_Options.contains("log"))
        {
            log_cur = DynamicShop.ccLang.get().getString("ON");
            log_set = DynamicShop.ccLang.get().getString("OFF");
        }
        else
        {
            log_cur = DynamicShop.ccLang.get().getString("OFF");
            log_set = DynamicShop.ccLang.get().getString("ON");
        }
        ArrayList<String> logLore = new ArrayList<>();
        logLore.add("§9"+DynamicShop.ccLang.get().getString("CUR_STATE")+": " + log_cur);
        logLore.add("§e"+DynamicShop.ccLang.get().getString("CLICK")+": " + log_set);
        ItemStack logToggleBtn =  CreateItemStack(Material.BOOK,null,
                DynamicShop.ccLang.get().getString("LOG.LOG"),
                logLore,1);
        vault.setItem(30,logToggleBtn);

        ItemStack logClearBtn =  CreateItemStack(Material.RED_STAINED_GLASS_PANE,null,
                DynamicShop.ccLang.get().getString("LOG.DELETE"),
                null,1);
        vault.setItem(31,logClearBtn);

        player.openInventory(vault);
    }

    // 거래화면 생성 및 열기
    public static void OpenItemTradeInven(Player player, String shopName, String tradeIdx)
    {
        // UI 요소 생성
        String title = DynamicShop.ccLang.get().getString("TRADE_TITLE");
        Inventory inven = Bukkit.createInventory(player,18,title);

        // 배달비
        ConfigurationSection optionS = DynamicShop.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options");
        int deliverycharge = 0;
        if(optionS.contains("world") && optionS.contains("pos1") && optionS.contains("pos2") && optionS.contains("flag.deliverycharge"))
        {
            boolean sameworld = true;
            boolean outside = false;
            if(!player.getWorld().getName().equals(optionS.getString("world"))) sameworld = false;

            String[] shopPos1 = optionS.getString("pos1").split("_");
            String[] shopPos2 = optionS.getString("pos2").split("_");
            int x1 = Integer.valueOf(shopPos1[0]);
            int y1 = Integer.valueOf(shopPos1[1]);
            int z1 = Integer.valueOf(shopPos1[2]);
            int x2 = Integer.valueOf(shopPos2[0]);
            int y2 = Integer.valueOf(shopPos2[1]);
            int z2 = Integer.valueOf(shopPos2[2]);

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

        String buyStr = DynamicShop.ccLang.get().getString("BUY");
        String sellStr = DynamicShop.ccLang.get().getString("SELL");
        String stockStr = DynamicShop.ccLang.get().getString("STOCK");
        String tradeStr = DynamicShop.ccShop.get().getString(shopName+"."+tradeIdx+".tradeType");
        if(tradeStr == null) tradeStr = "SB";

        ArrayList<String> sellLore = new ArrayList();
        if(tradeStr.equals("SellOnly")) sellLore.add(DynamicShop.ccLang.get().getString("SELLONLY_LORE"));
        if(tradeStr.equals("BuyOnly")) sellLore.add(DynamicShop.ccLang.get().getString("BUYONLY_LORE"));
        if(player.hasPermission("dshop.admin.shopedit")) sellLore.add(DynamicShop.ccLang.get().getString("TOGGLE_SELLABLE"));

        ArrayList<String> buyLore = new ArrayList();
        if(tradeStr.equals("SellOnly")) buyLore.add(DynamicShop.ccLang.get().getString("SELLONLY_LORE"));
        if(tradeStr.equals("BuyOnly")) buyLore.add(DynamicShop.ccLang.get().getString("BUYONLY_LORE"));
        if(player.hasPermission("dshop.admin.shopedit")) buyLore.add(DynamicShop.ccLang.get().getString("TOGGLE_BUYABLE"));

        ItemStack sellBtn = DynaShopAPI.CreateItemStack(Material.GREEN_STAINED_GLASS,null, sellStr,sellLore,1);
        ItemStack buyBtn = DynaShopAPI.CreateItemStack(Material.RED_STAINED_GLASS,null, buyStr,buyLore,1);
        inven.setItem(1,sellBtn);
        inven.setItem(10,buyBtn);

        String mat = DynamicShop.ccShop.get().getString(shopName+"."+tradeIdx+".mat");
        // 판매
        if(!tradeStr.equals("BuyOnly"))
        {
            int amount = 1;
            int idx = 2;
            for (int i = 1; i < 8; i++)
            {
                String priceStr = DynamicShop.ccLang.get().getString("SELLPRICE");

                ItemStack sell = new ItemStack(Material.getMaterial(mat),amount);
                sell.setItemMeta((ItemMeta) DynamicShop.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                ItemMeta meta = sell.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(sellStr + " x" + amount);
                lore.add(priceStr + DynaShopAPI.CalcTotalCost(shopName,tradeIdx,-amount));

                if(!DynamicShop.ccShop.get().getBoolean(shopName+".Options.hideStock"))
                {
                    if(DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") <= 0)
                    {
                        lore.add(stockStr+"INF");
                    }
                    else if(DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        lore.add(stockStr+(DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock")/64)+" Stacks");
                    }
                    else
                    {
                        lore.add(stockStr+DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock"));
                    }
                }

                if(deliverycharge>0) lore.add(DynamicShop.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);

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
            String priceStr = DynamicShop.ccLang.get().getString("PRICE");

            int amount = 1;
            int idx = 11;
            for (int i = 1; i < 8; i++)
            {
                ItemStack buy = new ItemStack(Material.getMaterial(mat),amount);
                buy.setItemMeta((ItemMeta) DynamicShop.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                ItemMeta meta = buy.getItemMeta();

                ArrayList<String> lore = new ArrayList<>();
                lore.add(buyStr + " x" + amount);
                lore.add(priceStr + DynaShopAPI.CalcTotalCost(shopName,tradeIdx,amount));

                if(DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") != -1)
                {
                    if(DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") <= amount)
                    {
                        continue;
                    }
                }

                if(!DynamicShop.ccShop.get().getBoolean(shopName+".Options.hideStock"))
                {
                    if(DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") <= 0)
                    {
                        lore.add(stockStr+"INF");
                    }
                    else if(DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        lore.add(stockStr+(DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock")/64)+" Stacks");
                    }
                    else
                    {
                        lore.add(stockStr+DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock"));
                    }
                }

                if(deliverycharge>0) lore.add(DynamicShop.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);

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
            moneyLore.add("§f" + df.format(GetCurJobPoints(player)) + "Points");
        }
        else
        {
            moneyLore.add("§f" + DynamicShop.getEconomy().format(DynamicShop.getEconomy().getBalance(player)));
        }
        String balStr = "";
        if(GetShopBalance(shopName) >= 0)
        {
            double d = GetShopBalance(shopName);
            balStr = DynamicShop.getEconomy().format(d);
            if(optionS.contains("flag.jobpoint")) balStr += "Points";
        }
        else
        {
            balStr = DynamicShop.ccLang.get().getString("SHOP_BAL_INF");
        }
        moneyLore.add("§3" + ChatColor.stripColor(DynamicShop.ccLang.get().getString("SHOP_BAL")));
        moneyLore.add("§f" + balStr);

        ItemStack balBtn = DynaShopAPI.CreateItemStack(Material.EMERALD,null,
                DynamicShop.ccLang.get().getString("BALANCE"), moneyLore,1);

        inven.setItem(0,balBtn);

        // 닫기 버튼
        ItemStack closeBtn = DynaShopAPI.CreateItemStack(Material.BARRIER,null,
                DynamicShop.ccLang.get().getString("CLOSE"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("CLOSE_LORE"))),1);

        inven.setItem(9,closeBtn);

        player.openInventory(inven);
    }

    // 파렛트 정렬용
    public static Comparator<Material> SortMat = new Comparator<Material>() {

        public int compare(Material m1, Material m2) {

            String name1 = m1.name();
            if(name1.startsWith("LIGHT_")) name1 = name1.substring(6);
            if(name1.startsWith("DARK_")) name1 = name1.substring(5);
            int idx1 = name1.indexOf("_");
            if(idx1 != -1)
            {
                name1 = name1.substring(idx1);
            }

            String name2 = m2.name();
            if(name2.startsWith("LIGHT_")) name2 = name2.substring(6);
            if(name2.startsWith("DARK_")) name2 = name2.substring(5);
            int idx2 = name2.indexOf("_");
            if(idx2 != -1)
            {
                name2 = name2.substring(idx2);
            }

            return name1.compareTo(name2);
        }};

    // 아이탬 파렛트 생성 및 열기
    static ArrayList<Material> sortedMat = new ArrayList<>();
    public static void OpenItemPalette(Player player, int page, String search)
    {
        Inventory inven = Bukkit.createInventory(player,54,DynamicShop.ccLang.get().getString("PALETTE_TITLE"));
        ArrayList<Material> paletteList = new ArrayList<>();

        if(search.length()>0)
        {
            Material[] allMat = Material.values();
            for (Material m: allMat)
            {
                if(m.name().contains(search.toUpperCase())) paletteList.add(m);
            }
        }
        else
        {
            if(sortedMat.size() == 0)
            {
                Material[] allMat = Material.values();
                ArrayList<Material> allMatList = new ArrayList<>();
                for (Material m: allMat)
                {
                    if(m.isItem()) allMatList.add(m);
                }
                //------------------------------------------
                allMatList.sort(SortMat);

                for (Material m: allMatList)
                {
                    if(m.isEdible()) sortedMat.add(m);
                }
                for (Material m: allMatList)
                {
                    if(m.name().contains("SPAWN_EGG")) sortedMat.add(m);
                }
                for (Material m: allMatList)
                {
                    if(!sortedMat.contains(m)) sortedMat.add(m);
                }
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server + " Sorting Items...This should run only once.");
            }

            paletteList = sortedMat;
        }

        // 닫기 버튼
        ItemStack closeBtn = DynaShopAPI.CreateItemStack(Material.BARRIER,null,
                DynamicShop.ccLang.get().getString("CLOSE"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("CLOSE_LORE"))),1);

        inven.setItem(45,closeBtn);

        // 페이지 버튼
        ItemStack pageBtn = DynaShopAPI.CreateItemStack(Material.PAPER,null,
                page + DynamicShop.ccLang.get().getString("PAGE"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("PAGE_LORE"))),page);

        inven.setItem(49,pageBtn);

        // 모두추가 버튼
        ItemStack addAllBtn = DynaShopAPI.CreateItemStack(Material.YELLOW_STAINED_GLASS_PANE,null,
                DynamicShop.ccLang.get().getString("ADDALL"), new ArrayList<>(Arrays.asList(search)),1);

        inven.setItem(51,addAllBtn);

        // 검색 버튼
        ItemStack searchBtn = DynaShopAPI.CreateItemStack(Material.COMPASS,null,
                DynamicShop.ccLang.get().getString("SEARCH"), new ArrayList<>(Arrays.asList(search)),1);

        inven.setItem(53,searchBtn);

        //45개씩 끊어서 표시.
        for (int i = 0; i<45; i++)
        {
            try {
                int idx = i + ((page-1) * 45);
                if(idx>=paletteList.size()) break;

                ItemStack btn = new ItemStack(paletteList.get(idx),1);

                ItemMeta btnMeta = btn.getItemMeta();
                ArrayList<String> btnlore = new ArrayList<>();
                btnlore.add(DynamicShop.ccLang.get().getString("PALETTE_LORE"));
                btnlore.add(DynamicShop.ccLang.get().getString("DECO_CREATE_LORE"));
                btnMeta.setLore(btnlore);
                btn.setItemMeta(btnMeta);

                inven.setItem(i,btn);
            }
            catch (Exception ignored){ }
        }

        player.openInventory(inven);
    }

    // 아이탬 셋팅창
    public static void OpenItemSettingGUI(Player player, ItemStack itemStack, int tab, double buyValue, double sellValue, double minPrice, double maxPrice, int median, int stock)
    {
        String[] temp = DynamicShop.ccUser.get().getString(player.getUniqueId()+".interactItem").split("/");
        String shopName = temp[0];

        // UI 요소 생성
        String title = DynamicShop.ccLang.get().getString("ITEM_SETTING_TITLE");
        Inventory inven = Bukkit.createInventory(player,36,title);

        buyValue = Math.round(buyValue*100)/100.0;
        sellValue = Math.round(sellValue*100)/100.0;
        minPrice = Math.round(minPrice*100)/100.0;
        maxPrice = Math.round(maxPrice*100)/100.0;
        String buyValueStr = "";
        String sellValueStr = "";
        String priceMinStr = "";
        String priceMaxStr = "";
        buyValueStr = DynamicShop.ccLang.get().getString("VALUE_BUY")+buyValue;
        sellValueStr = DynamicShop.ccLang.get().getString("VALUE_SELL")+sellValue;
        priceMinStr = DynamicShop.ccLang.get().getString("PRICE_MIN")+minPrice;
        priceMaxStr = DynamicShop.ccLang.get().getString("PRICE_MAX")+maxPrice;
        String medianStr = DynamicShop.ccLang.get().getString("MEDIAN")+median;
        String stockStr = DynamicShop.ccLang.get().getString("STOCK")+stock;

        ArrayList<String> sellValueLore = new ArrayList<>();
        ArrayList<String> medianLore = new ArrayList();
        ArrayList<String> stockLore = new ArrayList();
        ArrayList<String> maxPriceLore = new ArrayList();

        // 고정가, 무한재고, 별도판매가 안내 표시
        if(buyValue != sellValue)
        {
            sellValueLore.add("§7("+DynamicShop.ccLang.get().getString("TAXIGNORED")+")");
        }
        if(median <= 0)
        {
            medianLore.add("§7("+DynamicShop.ccLang.get().getString("STATICPRICE")+")");
        }
        if(stock <= 0)
        {
            stockLore.add("§7("+DynamicShop.ccLang.get().getString("INFSTOCK")+")");
        }
        if(maxPrice <=0)
        {
            maxPriceLore.add("§7("+DynamicShop.ccLang.get().getString("UNLIMITED")+")");
        }

        // 가격, 미디안, 스톡 버튼
        ItemStack buyValueBtn = DynaShopAPI.CreateItemStack((tab==1) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE,null, buyValueStr,null,1);
        ItemStack sellValueBtn = DynaShopAPI.CreateItemStack((tab==2) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,null, sellValueStr,sellValueLore,1);
        ItemStack minValueBtn = DynaShopAPI.CreateItemStack((tab==3) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,null, priceMinStr,null,1);
        ItemStack maxValueBtn = DynaShopAPI.CreateItemStack((tab==4) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,null, priceMaxStr,maxPriceLore,1);
        ItemStack medianBtn = DynaShopAPI.CreateItemStack((tab==5) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE,null, medianStr,medianLore,1);
        ItemStack stockBtn = DynaShopAPI.CreateItemStack((tab==6) ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE,null, stockStr,stockLore,1);
        inven.setItem(2,buyValueBtn);
        inven.setItem(3,sellValueBtn);
        inven.setItem(4,minValueBtn);
        inven.setItem(5,maxValueBtn);
        inven.setItem(6,medianBtn);
        inven.setItem(7,stockBtn);

        ItemStack infoBtn = DynaShopAPI.CreateItemStack(Material.BLACK_STAINED_GLASS_PANE,null, "Shift = x5",null,1);
        inven.setItem(22,infoBtn);

        // 조절버튼
        if(buyValue == sellValue) sellValueStr = "§7"+ ChatColor.stripColor(sellValueStr);
        if(minPrice <= 0.01) priceMinStr = "§7"+ ChatColor.stripColor(priceMinStr);
        if(maxPrice <=0) priceMaxStr = "§7"+ChatColor.stripColor(DynamicShop.ccLang.get().getString("PRICE_MAX") + DynamicShop.ccLang.get().getString("UNLIMITED"));

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

        if(median <= 0) medianStr = medianStr + "§7("+DynamicShop.ccLang.get().getString("STATICPRICE")+")";
        if(stock <= 0) stockStr = stockStr + "§7("+DynamicShop.ccLang.get().getString("INFSTOCK")+")";

        editBtnLore.add(buyValueStr);
        editBtnLore.add(sellValueStr);
        editBtnLore.add(priceMinStr);
        editBtnLore.add(priceMaxStr);
        editBtnLore.add(medianStr);
        editBtnLore.add(stockStr);

        editBtnLore.add("§3§m                       ");
        double buyPrice = 0;
        double sellPrice = 0;
        if(median <= 0 || stock <= 0 )
        {
            buyPrice = buyValue;
            if(buyValue != sellValue)
            {
                editBtnLore.add("§7"+ChatColor.stripColor(DynamicShop.ccLang.get().getString("TAXIGNORED")));
                sellPrice = sellValue;
            }
            else
            {
                String taxStr = "§7"+ChatColor.stripColor(DynamicShop.ccLang.get().getString("TAX.SALESTAX")) + ": ";
                taxStr += GetTaxRate(shopName) + "%";
                editBtnLore.add(taxStr);
                sellPrice = buyPrice - ((buyPrice / 100) * GetTaxRate(shopName));
            }
        }
        else
        {
            buyPrice = (buyValue*median)/stock;
            if(buyValue != sellValue) // 판매가 별도설정
            {
                editBtnLore.add("§7"+ChatColor.stripColor(DynamicShop.ccLang.get().getString("TAXIGNORED")));
                sellPrice = (sellValue*median)/stock;
            }
            else
            {
                String taxStr = "§7"+ChatColor.stripColor(DynamicShop.ccLang.get().getString("TAX.SALESTAX")) + ": ";
                if(DynamicShop.ccShop.get().contains(shopName+".Options.SalesTax"))
                {
                    taxStr += DynamicShop.ccShop.get().getInt(shopName+".Options.SalesTax") + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * DynamicShop.ccShop.get().getInt(shopName+".Options.SalesTax"));
                }
                else
                {
                    taxStr += DynamicShop.plugin.getConfig().getDouble("SalesTax") + "%";
                    sellPrice = buyPrice - ((buyPrice / 100) * DynamicShop.plugin.getConfig().getDouble("SalesTax"));
                }
                sellPrice = (Math.round(sellPrice*100)/100.0);

                editBtnLore.add(taxStr);
            }
        }

        editBtnLore.add("§3§l"+ChatColor.stripColor(DynamicShop.ccLang.get().getString("BUY")) + ": " + df.format(buyPrice));
        editBtnLore.add("§3§l"+ChatColor.stripColor(DynamicShop.ccLang.get().getString("SELL")) + ": " + df.format(sellPrice));

        ItemStack d2Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "/2",editBtnLore,1);
        ItemStack m1000Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-1000",editBtnLore,1);
        ItemStack m100Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-100",editBtnLore,1);
        ItemStack m10Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-10",editBtnLore,1);
        ItemStack m1Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-1",editBtnLore,1);
        ItemStack m01Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "-0.1",editBtnLore,1);
        ItemStack reset = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "Reset",editBtnLore,1);
        ItemStack p01Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+0.1",editBtnLore,1);
        ItemStack p1Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+1",editBtnLore,1);
        ItemStack p10Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+10",editBtnLore,1);
        ItemStack p100Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+100",editBtnLore,1);
        ItemStack p1000Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "+1000",editBtnLore,1);
        ItemStack m2Btn = DynaShopAPI.CreateItemStack(Material.WHITE_STAINED_GLASS_PANE,null, "x2",editBtnLore,1);
        ItemStack roundBtn = DynaShopAPI.CreateItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, DynamicShop.ccLang.get().getString("ROUNDDOWN"),editBtnLore,1);
        ItemStack setToMedian = DynaShopAPI.CreateItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, DynamicShop.ccLang.get().getString("SETTOMEDIAN"),editBtnLore,1);
        ItemStack setToStock = DynaShopAPI.CreateItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, DynamicShop.ccLang.get().getString("SETTOSTOCK"),editBtnLore,1);
        ItemStack setToValue = DynaShopAPI.CreateItemStack(Material.YELLOW_STAINED_GLASS_PANE,null, DynamicShop.ccLang.get().getString("SETTOVALUE"),editBtnLore,1);

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
        inven.setItem(0,itemStack);

        // 완료 버튼
        ItemStack doneBtn = DynaShopAPI.CreateItemStack(Material.STRUCTURE_VOID,null,
                DynamicShop.ccLang.get().getString("DONE"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("DONE_LORE"))),1);

        inven.setItem(8,doneBtn);

        // 닫기 버튼
        ItemStack closeBtn = DynaShopAPI.CreateItemStack(Material.BARRIER,null,
                DynamicShop.ccLang.get().getString("CLOSE"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("CLOSE_LORE"))),1);
        inven.setItem(27,closeBtn);

        // 추천 버튼
        ItemStack recBtn = DynaShopAPI.CreateItemStack(Material.NETHER_STAR,null,
                DynamicShop.ccLang.get().getString("RECOMMEND"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("RECOMMEND_LORE"))),1);
        inven.setItem(31,recBtn);

        // 삭제 버튼
        ItemStack removeBtn = DynaShopAPI.CreateItemStack(Material.BONE,null,
                DynamicShop.ccLang.get().getString("REMOVE"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("REMOVE_LORE"))),1);
        inven.setItem(35,removeBtn);

        player.openInventory(inven);
    }

    // 스타트 페이지 생성 및 열기
    public static void OpenStartPage(Player player)
    {
        Inventory ui = Bukkit.createInventory(player,DynamicShop.ccStartpage.get().getInt("Options.UiSlotCount"),DynamicShop.ccStartpage.get().getString("Options.Title"));

        DynamicShop.ccUser.get().set(player.getUniqueId()+".interactItem","");

        //아이콘, 이름, 로어, 인덱스, 커맨드
        ConfigurationSection cs = DynamicShop.ccStartpage.get().getConfigurationSection("Buttons");
        for (String s:cs.getKeys(false))
        {
            try {
                int idx = Integer.parseInt(s);

                String name = " ";
                if(cs.contains(s+".displayName"))
                {
                    name=cs.getConfigurationSection(s).getString("displayName");
                }

                ArrayList<String> tempList = new ArrayList<>();
                if(cs.contains(s+".lore"))
                {
                    String[] lore = cs.getConfigurationSection(s).getString("lore").split(DynamicShop.ccStartpage.get().getString("Options.LineBreak"));
                    tempList.addAll(Arrays.asList(lore));
                }

                if(player.hasPermission("dshop.admin.shopedit"))
                {
                    if(cs.getString(s+".action").length()>0)
                    {
                        tempList.add(DynamicShop.ccLang.get().getString("ITEM_MOVE_LORE"));
                    }
                    else
                    {
                        tempList.add(DynamicShop.ccLang.get().getString("ITEM_COPY_LORE"));
                    }
                    tempList.add(DynamicShop.ccLang.get().getString("ITEM_EDIT_LORE"));
                }

                ItemStack btn = new ItemStack(Material.getMaterial(cs.getConfigurationSection(s).getString("icon")));
                ItemMeta meta = btn.getItemMeta();
                meta.setDisplayName(name);
                meta.setLore(tempList);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                btn.setItemMeta(meta);
                ui.setItem(idx,btn);

            }catch (Exception e)
            {
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server+"Fail to create Start page button");
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server+e);
            }
        }

        player.openInventory(ui);
    }

    // 스타트페이지 셋팅창
    public static void OpenStartpageSettingGUI(Player player, int idx)
    {
        // UI 요소 생성
        String title = DynamicShop.ccLang.get().getString("STARTPAGE.EDITOR_TITLE");
        Inventory inven = Bukkit.createInventory(player,9,title);

        // 닫기 버튼
        ItemStack closeBtn = DynaShopAPI.CreateItemStack(Material.BARRIER,null,
                DynamicShop.ccLang.get().getString("CLOSE"), new ArrayList<>(Arrays.asList(DynamicShop.ccLang.get().getString("CLOSE_LORE"))),1);
        inven.setItem(0,closeBtn);

        // 이름 버튼
        ItemStack nameBtn = DynaShopAPI.CreateItemStack(Material.BOOK,null,
                DynamicShop.ccLang.get().getString("STARTPAGE.EDIT_NAME"), null,1);
        inven.setItem(2,nameBtn);

        // 설명 버튼
        ItemStack loreBtn = DynaShopAPI.CreateItemStack(Material.BOOK,null,
                DynamicShop.ccLang.get().getString("STARTPAGE.EDIT_LORE"), null,1);
        inven.setItem(3,loreBtn);

        // 아이콘 버튼
        String[] temp = DynamicShop.ccUser.get().getString(player.getUniqueId()+".interactItem").split("/");
        ItemStack iconBtn = DynaShopAPI.CreateItemStack(Material.getMaterial(DynamicShop.ccStartpage.get().getString("Buttons."+temp[1]+".icon")),null,
                DynamicShop.ccLang.get().getString("STARTPAGE.EDIT_ICON"), null,1);
        inven.setItem(4,iconBtn);

        // 액션 버튼
        ItemStack actionBtn = DynaShopAPI.CreateItemStack(Material.REDSTONE_TORCH,null,
                DynamicShop.ccLang.get().getString("STARTPAGE.EDIT_ACTION"), null,1);
        inven.setItem(5,actionBtn);

        // 상점 바로가기 생성 버튼
        ItemStack shopBtn = DynaShopAPI.CreateItemStack(Material.EMERALD,null,
                DynamicShop.ccLang.get().getString("STARTPAGE.SHOP_SHORTCUT"), null,1);
        inven.setItem(6,shopBtn);

        // 장식 버튼
        ItemStack deco = DynaShopAPI.CreateItemStack(Material.BLUE_STAINED_GLASS_PANE,null,
                DynamicShop.ccLang.get().getString("STARTPAGE.CREATE_DECO"), null,1);
        inven.setItem(7,deco);

        // 삭제 버튼
        ItemStack removeBtn = DynaShopAPI.CreateItemStack(Material.BONE,null,
                DynamicShop.ccLang.get().getString("REMOVE"), null,1);
        inven.setItem(8,removeBtn);

        player.openInventory(inven);
    }

    // 퀵셀 창
    public static void OpenQuickSellGUI(Player player)
    {
        Inventory inven = Bukkit.createInventory(player,9,DynamicShop.ccLang.get().getString("QUICKSELL_TITLE"));

        player.openInventory(inven);
    }

    //[ Shop ]=========================================================

    // 아이탬 정보 출력
    public static void SendItemInfo(Player player, String shopName, int idx, String msgType)
    {
        String info = " value:" + DynamicShop.ccShop.get().getDouble(shopName+"." + idx+ ".value");

        double valueMin = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".valueMin");
        if(valueMin > 0.01) info += " min:" + valueMin;
        double valueMax = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".valueMax");
        if(valueMax > 0) info += " max:" + valueMax;

        info += " median:" + DynamicShop.ccShop.get().getInt(shopName+"." + idx + ".median");
        info += " stock:" + DynamicShop.ccShop.get().getInt(shopName+"." + idx + ".stock");

        player.sendMessage(" - " + DynamicShop.ccLang.get().getString(msgType).
                replace("{item}",DynamicShop.ccShop.get().getString(shopName+"." + idx + ".mat")).
                replace("{info}",info)
        );
    }

    // 상점에서 빈 슬롯 찾기
    public static int FindEmptyShopSlot(String shopName)
    {
        ArrayList<Integer> banList = new ArrayList<>();

        for (String s:DynamicShop.ccShop.get().getConfigurationSection(shopName).getKeys(false))
        {
            try {
                banList.add(Integer.parseInt(s));
            }
            catch (Exception ignored) { }
        }

        for(int i = 0; i<45*DynamicShop.ccShop.get().getInt(shopName+".Options.page"); i++)
        {
            if(!banList.contains(i))
            {
                return i;
            }
        }

        return -1;
    }

    // 상점에서 아이탬타입 찾기
    public static int FindItemFromShop(String shopName, ItemStack item)
    {
        for (String s:DynamicShop.ccShop.get().getConfigurationSection(shopName).getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(s);
            }
            catch (Exception e)
            {
                continue;
            }

            if(!DynamicShop.ccShop.get().contains(shopName+"."+s+".value")) continue; // 장식용임

            if(DynamicShop.ccShop.get().getString(shopName+"."+s+".mat").equals(item.getType().toString()))
            {
                String metaStr = DynamicShop.ccShop.get().getString(shopName+"."+s+".itemStack");

                if(metaStr == null && !item.hasItemMeta())
                {
                    return Integer.parseInt(s);
                }

                if(metaStr != null && metaStr.equals(item.getItemMeta().toString()))
                {
                    return Integer.parseInt(s);
                }
            }
        }
        return -1;
    }

    // 상점에 아이탬 추가
    public static boolean AddItemToShop(String shopName, int idx, ItemStack item, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock)
    {
        try
        {
            DynamicShop.ccShop.get().set(shopName+"." + idx + ".mat",item.getType().toString());

            if(item.hasItemMeta())
            {
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".itemStack",item.getItemMeta());
            }
            else
            {
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".itemStack",null);
            }

            if(buyValue > 0)
            {
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".value",buyValue);
                if(buyValue == sellValue)
                {
                    DynamicShop.ccShop.get().set(shopName+"." + idx + ".value2",null);
                }
                else
                {
                    DynamicShop.ccShop.get().set(shopName+"." + idx + ".value2",sellValue);
                }

                if(minValue > 0.01)
                {
                    DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMin",minValue);
                }
                else
                {
                    DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMin",null);
                }

                if(maxValue > 0.01)
                {
                    DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMax",maxValue);
                }
                else
                {
                    DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMax",null);
                }

                DynamicShop.ccShop.get().set(shopName+"." + idx + ".median",median);
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".stock",stock);
            }
            else
            {
                // idx,null하면 안됨. 존재는 하되 하위 데이터만 없어야함.
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".value",null);
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".value2",null);
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMin",null);
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMax",null);
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".median",null);
                DynamicShop.ccShop.get().set(shopName+"." + idx + ".stock",null);
            }

            DynamicShop.ccShop.save();

            return  true;
        }
        catch (Exception e)
        {
            DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server + " ERR.AddItemToShop.");
            for (StackTraceElement s:e.getStackTrace())
            {
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server + " " + s.toString());
            }
            return false;
        }
    }

    // 상점 아이탬의 value, median, stock을 수정
    public static void EditShopItem(String shopName, int idx,  double buyValue, double sellValue, double minValue, double maxValue, int median, int stock)
    {
        DynamicShop.ccShop.get().set(shopName+"." + idx + ".value",buyValue);
        if(buyValue == sellValue)
        {
            DynamicShop.ccShop.get().set(shopName+"." + idx + ".value2",null);
        }
        else
        {
            DynamicShop.ccShop.get().set(shopName+"." + idx + ".value2",sellValue);
        }
        if(minValue > 0.01)
        {
            DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMin",minValue);
        }
        else
        {
            DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMin",null);
        }
        if(maxValue > 0.01)
        {
            DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMax",maxValue);
        }
        else
        {
            DynamicShop.ccShop.get().set(shopName+"." + idx + ".valueMax",null);
        }
        DynamicShop.ccShop.get().set(shopName+"." + idx + ".median",median);
        DynamicShop.ccShop.get().set(shopName+"." + idx + ".stock",stock);
        DynamicShop.ccShop.save();
    }

    // 상점에서 아이탬 제거
    public static void RemoveItemFromShop(String shopName, int idx)
    {
        DynamicShop.ccShop.get().set(shopName+"." + idx,null);
        DynamicShop.ccShop.save();
    }

    // 상점 페이지 삽입
    public static void InsetShopPage(String shopName, int page)
    {
        ConfigurationSection confSec = DynamicShop.ccShop.get().getConfigurationSection(shopName);
        confSec.set("Options.page", confSec.getInt("Options.page")+1);

        for (int i = confSec.getInt("Options.page")*45; i>=(page-1) * 45; i--)
        {
            ConfigurationSection temp = confSec.getConfigurationSection(String.valueOf(i));
            confSec.set(String.valueOf(i+45), temp);
            confSec.set(String.valueOf(i),null);
        }

        DynamicShop.ccShop.save();
        DynamicShop.ccShop.reload();
    }

    // 상점 페이지 삭제
    public static void DeleteShopPage(String shopName, int page)
    {
        ConfigurationSection confSec = DynamicShop.ccShop.get().getConfigurationSection(shopName);
        confSec.set("Options.page", confSec.getInt("Options.page")-1);

        for (String s:confSec.getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(s);

                if(i >= (page-1) * 45 && i < page*45)
                {
                    confSec.set(s,null);
                }
                else if(i >= page*45)
                {
                    ConfigurationSection temp = confSec.getConfigurationSection(s);
                    confSec.set(String.valueOf(i-45), temp);
                    confSec.set(s,null);
                }

            }catch (Exception ignored){}
        }

        DynamicShop.ccShop.save();
        DynamicShop.ccShop.reload();
    }

    // 상점 이름 바꾸기
    public static void RenameShop(String shopName, String newName)
    {
        ConfigurationSection old = DynamicShop.ccShop.get().getConfigurationSection(shopName);
        DynamicShop.ccShop.get().set(shopName,null);
        DynamicShop.ccShop.get().set(newName,old);
        DynamicShop.ccShop.save();
    }

    // 상점 병합
    public static void MergeShop(String shopA, String shopB)
    {
        ConfigurationSection confA = DynamicShop.ccShop.get().getConfigurationSection(shopA);
        ConfigurationSection confB = DynamicShop.ccShop.get().getConfigurationSection(shopB);

        int pg1 = confA.getInt("Options.page");
        int pg2 = confB.getInt("Options.page");

        confA.set("Options.page", pg1 + pg2);
        if(confA.contains("Options.Balance") || confB.contains("Options.Balance"))
        {
            double a = GetShopBalance(shopA);
            if(a == -1) a = 0;

            double b = 0;
            if(!(confA.getString("Options.Balance").equals(shopB) || confB.getString("Options.Balance").equals(shopA) ))
            {
                b = GetShopBalance(shopB);
            }

            if(b == -1) b = 0;

            if(a+b > 0)
            {
                confA.set("Options.Balance", a+b);
            }
            else
            {
                confA.set("Options.Balance", null);
            }
        }

        for(String item:confB.getKeys(false))
        {
            try
            {
                confA.set( String.valueOf(Integer.parseInt(item)+(pg1*45)), confB.get(item));
            }catch (Exception ignored){}
        }

        DynamicShop.ccShop.get().set(shopB,null);
        DynamicShop.ccShop.save();
        DynamicShop.ccShop.reload();
    }

    //[ Transaction ]=========================================================

    // 퀵판매
    public static void QuickSellItem(Player player, ItemStack myItem, String shopName, int tradeIdx)
    {
        double priceSum;

        // 실제 판매 가능량 확인
        int actualAmount = myItem.getAmount();

        HashMap<Integer,ItemStack> hashMap = player.getInventory().removeItem(myItem);
        player.updateInventory();
        if(!hashMap.isEmpty())
        {
            actualAmount -= hashMap.get(0).getAmount();
        }

        // 판매할 아이탬이 없음
        if(actualAmount == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("NO_ITEM_TO_SELL"));
            return;
        }

        priceSum = DynaShopAPI.CalcTotalCost(shopName,String.valueOf(tradeIdx),-actualAmount);

        // 재고 증가
        if(DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") > 0)
        {
            DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock",
                    DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") + actualAmount);
        }

        // 실제 거래부----------
        Economy econ = DynamicShop.getEconomy();
        EconomyResponse r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

        if(r.transactionSuccess())
        {
            DynamicShop.ccShop.save();

            //로그 기록
            AddLog(shopName,myItem.getType().toString(),-actualAmount,priceSum,"jobpoint",player.getName());

            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("SELL_SUCCESS")
                    .replace("{item}",myItem.getType().name())
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}",econ.format(r.amount))
                    .replace("{bal}",econ.format(econ.getBalance((player)))));
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"),1,1);

            if(DynamicShop.ccShop.get().contains(shopName+".Options.Balance"))
            {
                AddShopBalance(shopName,priceSum*-1);
            }
        }
        else
        {
            player.sendMessage(String.format("[Vault] An error occured: %s", r.errorMessage));
        }
    }

    // 판매
    public static void SellItem_cash(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, double deliverycharge, boolean infiniteStock)
    {
        // 상점에 돈이 없음
        if(DynaShopAPI.GetShopBalance(shopName) != -1 && DynaShopAPI.GetShopBalance(shopName) < DynaShopAPI.CalcTotalCost(shopName,tradeIdx,tempIS.getAmount()))
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("SHOP_BAL_LOW"));
            return;
        }

        // 실제 판매 가능량 확인
        int actualAmount = tempIS.getAmount();
        HashMap<Integer,ItemStack> hashMap = player.getInventory().removeItem(tempIS);
        player.updateInventory();
        if(!hashMap.isEmpty())
        {
            actualAmount -= hashMap.get(0).getAmount();
        }

        // 판매할 아이탬이 없음
        if(actualAmount == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("NO_ITEM_TO_SELL"));
            return;
        }

        priceSum += DynaShopAPI.CalcTotalCost(shopName,tradeIdx,-actualAmount);

        // 재고 증가
        if(!infiniteStock)
        {
            DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock",
                    DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") + actualAmount);
        }

        // 면제된 배달비 계산용
        double oldPriceSum = 0;
        if(priceSum <= 0)
        {
            oldPriceSum = priceSum;
            priceSum = 0;
        }

        // 실제 거래부----------
        Economy econ = DynamicShop.getEconomy();
        EconomyResponse r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

        if(r.transactionSuccess())
        {
            //로그 기록
            DynaShopAPI.AddLog(shopName,tempIS.getType().toString(),-actualAmount,priceSum,"vault",player.getName());

            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("SELL_SUCCESS")
                    .replace("{item}",DynaShopAPI.GetBeautifiedName(tempIS.getType()))
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}",econ.format(r.amount))
                    .replace("{bal}",econ.format(econ.getBalance((player)))));
            DynaShopAPI.PlayerSoundEffect(player,"sell");

            if(deliverycharge > 0)
            {
                if(priceSum == 0)
                {
                    player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("DELIVERYCHARGE_EXEMPTION").
                            replace("{fee}",""+deliverycharge).replace("{fee2}",(oldPriceSum-priceSum)*-1+""));
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("DELIVERYCHARGE") + ": " + deliverycharge);
                }
            }

            if(DynamicShop.ccShop.get().contains(shopName+".Options.Balance"))
            {
                DynaShopAPI.AddShopBalance(shopName, priceSum * -1);
            }

            DynamicShop.ccShop.save();
            DynaShopAPI.OpenItemTradeInven(player, shopName, tradeIdx);
        } else {
            player.sendMessage(String.format("[Vault] An error occured: %s", r.errorMessage));
        }
    }

    // 구매
    public static void BuyItem_cash(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, double deliverycharge, boolean infiniteStock)
    {
        Economy econ = DynamicShop.getEconomy();

        int actualAmount = 0;
        int stockOld = DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock");

        for (int i = 0; i<tempIS.getAmount(); i++)
        {
            if(!infiniteStock && stockOld <= actualAmount+1)
            {
                break;
            }

            double price = DynaShopAPI.GetCurrentPrice(shopName,tradeIdx,true);

            if(priceSum + price > econ.getBalance(player)) break;

            priceSum += price;

            if(!infiniteStock)
            {
                DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock",
                        DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") - 1);
            }

            actualAmount++;
        }

        // 실 구매 가능량이 0이다 = 돈이 없다.
        if(actualAmount <= 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("NOT_ENOUGH_MONEY").replace("{bal}",econ.format(econ.getBalance(player))));
            DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 상점 재고 부족
        if(!infiniteStock && stockOld <= actualAmount)
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("OUT_OF_STOCK"));
            DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 실 거래부-------
        if(econ.getBalance(player) >= priceSum)
        {
            EconomyResponse r = DynamicShop.getEconomy().withdrawPlayer(player, priceSum);

            if(r.transactionSuccess())
            {
                int leftAmount = actualAmount;
                while (leftAmount>0)
                {
                    int giveAmount = tempIS.getType().getMaxStackSize();
                    if(giveAmount > leftAmount) giveAmount = leftAmount;

                    ItemStack iStack = new ItemStack(tempIS.getType(),giveAmount);
                    iStack.setItemMeta((ItemMeta) DynamicShop.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                    HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(iStack);
                    if(leftOver.size() != 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("INVEN_FULL"));
                        Location loc = player.getLocation();

                        ItemStack leftStack = new ItemStack(tempIS.getType(),leftOver.get(0).getAmount());
                        leftStack.setItemMeta((ItemMeta) DynamicShop.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                        player.getWorld().dropItem(loc, leftStack);
                    }

                    leftAmount -= giveAmount;
                }

                //로그 기록
                DynaShopAPI.AddLog(shopName,tempIS.getType().toString(),actualAmount,priceSum,"vault",player.getName());

                player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("BUY_SUCCESS")
                        .replace("{item}",DynaShopAPI.GetBeautifiedName(tempIS.getType()))
                        .replace("{amount}", Integer.toString(actualAmount))
                        .replace("{price}",econ.format(r.amount))
                        .replace("{bal}",econ.format(econ.getBalance((player)))));
                DynaShopAPI.PlayerSoundEffect(player,"buy");

                if(deliverycharge > 0)
                {
                    player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);
                }

                if(DynamicShop.ccShop.get().contains(shopName+".Options.Balance"))
                {
                    DynaShopAPI.AddShopBalance(shopName,priceSum);
                }

                DynaShopAPI.OpenItemTradeInven(player,shopName, tradeIdx);
                DynamicShop.ccShop.save();
            }
            else
            {
                player.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("NOT_ENOUGH_MONEY").replace("{bal}",econ.format(econ.getBalance(player))));
        }
    }

    // 판매 jp
    public static void SellItem_jobPoint(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, double deliverycharge, boolean infiniteStock)
    {
        // 상점에 돈이 없음
        if(DynaShopAPI.GetShopBalance(shopName) != -1 && DynaShopAPI.GetShopBalance(shopName) < DynaShopAPI.CalcTotalCost(shopName,tradeIdx,tempIS.getAmount()))
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("SHOP_BAL_LOW"));
            return;
        }

        // 실제 판매 가능량 확인
        int actualAmount = tempIS.getAmount();
        HashMap<Integer,ItemStack> hashMap = player.getInventory().removeItem(tempIS);
        player.updateInventory();
        if(!hashMap.isEmpty())
        {
            actualAmount -= hashMap.get(0).getAmount();
        }

        // 판매할 아이탬이 없음
        if(actualAmount == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("NO_ITEM_TO_SELL"));
            return;
        }

        priceSum += DynaShopAPI.CalcTotalCost(shopName,tradeIdx,-actualAmount);

        // 재고 증가
        if(!infiniteStock)
        {
            DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock",
                    DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") + actualAmount);
        }

        // 면제된 배달비 계산용
        double oldPriceSum = 0;
        if(priceSum <= 0)
        {
            oldPriceSum = priceSum;
            priceSum = 0;
        }

        // 실제 거래부----------
        if(DynaShopAPI.AddJobsPoint(player, priceSum))
        {
            //로그 기록
            DynaShopAPI.AddLog(shopName,tempIS.getType().toString(),-actualAmount,priceSum,"jobpoint",player.getName());

            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("SELL_SUCCESS_JP")
                    .replace("{item}",DynaShopAPI.GetBeautifiedName(tempIS.getType()))
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}",df.format(priceSum))
                    .replace("{bal}",df.format(DynaShopAPI.GetCurJobPoints(player))));
            DynaShopAPI.PlayerSoundEffect(player,"sell");

            if(deliverycharge > 0)
            {
                if(priceSum == 0)
                {
                    player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("DELIVERYCHARGE_EXEMPTION").
                            replace("{fee}",""+deliverycharge).replace("{fee2}",(oldPriceSum-priceSum)*-1+""));
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("DELIVERYCHARGE") + ": " + deliverycharge);
                }
            }

            if(DynamicShop.ccShop.get().contains(shopName+".Options.Balance"))
            {
                DynaShopAPI.AddShopBalance(shopName, priceSum * -1);
            }

            DynamicShop.ccShop.save();
            DynaShopAPI.OpenItemTradeInven(player, shopName, tradeIdx);
        }
    }

    // 구매 jp
    public static void BuyItem_jobPoint(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, double deliverycharge, boolean infiniteStock)
    {
        int actualAmount = 0;
        int stockOld = DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock");

        for (int i = 0; i<tempIS.getAmount(); i++)
        {
            if(!infiniteStock && stockOld <= actualAmount+1)
            {
                break;
            }

            double price = DynaShopAPI.GetCurrentPrice(shopName,tradeIdx,true);

            if(priceSum + price > DynaShopAPI.GetCurJobPoints(player)) break;

            priceSum += price;

            if(!infiniteStock)
            {
                DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock",
                        DynamicShop.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") - 1);
            }

            actualAmount++;
        }

        // 실 구매 가능량이 0이다 = 돈이 없다.
        if(actualAmount <= 0)
        {
            player.sendMessage(DynamicShop.dsPrefix+DynamicShop.ccLang.get().getString("NOT_ENOUGH_POINT").replace("{bal}", df.format(DynaShopAPI.GetCurJobPoints(player))));
            DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 상점 재고 부족
        if(!infiniteStock && stockOld <= actualAmount)
        {
            player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("OUT_OF_STOCK"));
            DynamicShop.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 실 거래부-------
        if(DynaShopAPI.GetCurJobPoints(player) >= priceSum)
        {
            if(DynaShopAPI.AddJobsPoint(player,priceSum * -1))
            {
                int leftAmount = actualAmount;
                while (leftAmount>0)
                {
                    int giveAmount = tempIS.getType().getMaxStackSize();
                    if(giveAmount > leftAmount) giveAmount = leftAmount;

                    ItemStack iStack = new ItemStack(tempIS.getType(),giveAmount);
                    iStack.setItemMeta((ItemMeta) DynamicShop.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                    HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(iStack);
                    if(leftOver.size() != 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("INVEN_FULL"));
                        Location loc = player.getLocation();

                        ItemStack leftStack = new ItemStack(tempIS.getType(),leftOver.get(0).getAmount());
                        leftStack.setItemMeta((ItemMeta) DynamicShop.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                        player.getWorld().dropItem(loc, leftStack);
                    }

                    leftAmount -= giveAmount;
                }

                //로그 기록
                DynaShopAPI.AddLog(shopName,tempIS.getType().toString(),actualAmount,priceSum,"jobpoint",player.getName());

                player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("BUY_SUCCESS_JP")
                        .replace("{item}",DynaShopAPI.GetBeautifiedName(tempIS.getType()))
                        .replace("{amount}", Integer.toString(actualAmount))
                        .replace("{price}",df.format(priceSum))
                        .replace("{bal}",df.format(DynaShopAPI.GetCurJobPoints((player)))));
                DynaShopAPI.PlayerSoundEffect(player,"buy");

                if(deliverycharge > 0)
                {
                    player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);
                }

                if(DynamicShop.ccShop.get().contains(shopName+".Options.Balance"))
                {
                    DynaShopAPI.AddShopBalance(shopName,priceSum);
                }

                DynaShopAPI.OpenItemTradeInven(player,shopName, tradeIdx);
                DynamicShop.ccShop.save();
            }
        }
    }

    // 특정 아이탬의 현재 가치를 계산 (다이나믹 or 고정가)
    public static double GetCurrentPrice(String shopName, String idx, boolean buy)
    {
        double price;

        double value;
        if(!buy && DynamicShop.ccShop.get().contains(shopName+"."+idx+".value2"))
        {
            value = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".value2");
        }
        else
        {
            value = DynamicShop.ccShop.get().getDouble(shopName+"." + idx + ".value");
        }
        double min = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".valueMin");
        double max = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".valueMax");
        int median = DynamicShop.ccShop.get().getInt(shopName +"." + idx + ".median");
        int stock = DynamicShop.ccShop.get().getInt(shopName+"." + idx + ".stock");

        if(median <= 0 || stock <= 0)
        {
            price = value;
        }
        else
        {
            price = (median * value) / stock;
        }

        if(min != 0 && price < min)
        {
            price = min;
        }
        if(max != 0 && price > max)
        {
            price = max;
        }

        return price;
    }

    // 특정 아이탬의 앞으로 n개의 가치합을 계산 (다이나믹 or 고정가) (세금 반영)
    public static double CalcTotalCost(String shopName, String idx, int amount)
    {
        double total = 0;
        int median = DynamicShop.ccShop.get().getInt(shopName+"." + idx + ".median");
        int tempS = DynamicShop.ccShop.get().getInt(shopName+"." + idx + ".stock");

        double value;
        if(amount < 0 && DynamicShop.ccShop.get().contains(shopName+"."+idx+".value2"))
        {
            value = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".value2");
        }
        else
        {
            value = DynamicShop.ccShop.get().getDouble(shopName+"." + idx + ".value");
        }

        if(median <= 0 || tempS <= 0)
        {
            total = value * Math.abs(amount);
        }
        else
        {
            for (int i = 0; i<Math.abs(amount); i++)
            {
                double temp = median * value / tempS;
                double min = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".valueMin");
                double max = DynamicShop.ccShop.get().getDouble(shopName+"."+idx+".valueMax");

                if(min != 0 && temp < min)
                {
                    temp = min;
                }
                if(max != 0 && temp > max)
                {
                    temp = max;
                }

                total += temp;

                if(amount>0)
                {
                    tempS--;
                    if(tempS < 0) break;
                }
                else
                {
                    tempS++;
                }
            }
        }

        // 세금 적용 (판매가 별도지정시 세금계산 안함)
        if(amount < 0 && !DynamicShop.ccShop.get().contains(shopName+"."+idx+".value2"))
        {
            total = total - ((total / 100) * GetTaxRate(shopName));
        }

        return (Math.round(total*100)/100.0);
    }

    // 상점의 세율 반환
    public static int GetTaxRate(String shopName)
    {
        if(DynamicShop.ccShop.get().contains(shopName+".Options.SalesTax"))
        {
            return DynamicShop.ccShop.get().getInt(shopName+".Options.SalesTax");
        }
        else
        {
            return DynamicShop.plugin.getConfig().getInt("SalesTax");
        }
    }

    //[ Balance ]=========================================================

    // 상점의 잔액 확인
    public static double GetShopBalance(String shopName)
    {
        // 무한
        if(!DynamicShop.ccShop.get().contains(shopName+".Options.Balance")) return -1;

        double shopBal = 0;

        try
        {
            shopBal = Double.parseDouble(DynamicShop.ccShop.get().getString(shopName+".Options.Balance")); // 파싱에 실패하면 캐치로 가는 방식.
        }
        // 연동형
        catch (Exception ee)
        {
            String linkedShop = DynamicShop.ccShop.get().getString(shopName+".Options.Balance");

            // 그런 상점이 없음.
            if(!DynamicShop.ccShop.get().contains(linkedShop))
            {
                DynamicShop.ccShop.get().set(shopName+".Options.Balance",null);
                DynamicShop.ccShop.save();
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server +
                        shopName + ", " + linkedShop + "/ target shop not found");
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server + shopName + "/ balance has been reset");
                return -1;
            }

            // 연결 대상이 실제 계좌가 아님.
            try
            {
                if(DynamicShop.ccShop.get().contains(linkedShop+".Options.Balance"))
                {
                    double temp = Double.parseDouble(DynamicShop.ccShop.get().getString(linkedShop+".Options.Balance"));
                }
                else
                {
                    return -1;
                }
            }
            catch (Exception e)
            {
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server +
                        shopName + ", " + linkedShop + "/ " +
                        DynamicShop.ccLang.get().getString("ERR.SHOP_LINK_TARGET_ERR"));

                DynamicShop.ccShop.get().set(shopName+".Options.Balance",null);
                DynamicShop.ccShop.save();

                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server + shopName + "/ balance has been reset");
                return -1;
            }

            shopBal = DynamicShop.ccShop.get().getDouble(linkedShop+".Options.Balance");
        }

        return shopBal;
    }

    // 상점의 잔액 수정
    public static void AddShopBalance(String shopName, double amount)
    {
        double old = GetShopBalance(shopName);
        if(old < 0) return;

        double newValue = old + amount;
        newValue = (Math.round(newValue*100)/100.0);

        try
        {
            Double temp = Double.parseDouble(DynamicShop.ccShop.get().getString(shopName+".Options.Balance"));
            DynamicShop.ccShop.get().set(shopName+".Options.Balance",newValue);
        }
        // 연동형
        catch (Exception ee)
        {
            String linkedShop = DynamicShop.ccShop.get().getString(shopName+".Options.Balance");
            DynamicShop.ccShop.get().set(linkedShop+".Options.Balance",newValue);
        }
    }

    // JobsReborn의 points 수정
    public static boolean AddJobsPoint(Player p, double amount)
    {
        if(!DynamicShop.jobsRebornActive)
        {
            p.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("ERR.JOBSREBORN_NOT_FOUND"));
            return false;
        }

        PlayerPoints pp = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(p.getUniqueId());
        // 차감
        if(amount < 0.0)
        {
            if(pp.havePoints(amount * -1))
            {
                pp.takePoints(amount * -1);
                return true;
            }
            // 포인트 부족
            else
            {
                p.sendMessage(DynamicShop.dsPrefix+DynamicShop.ccLang.get().getString("NOT_ENOUGH_POINT").replace("{bal}", df.format(GetCurJobPoints(p))));
                return false;
            }
        }
        // 증가
        else
        {
            pp.addPoints(amount);
            return true;
        }
    }

    // JobsReborn. 플레이어의 잔액 확인
    public static double GetCurJobPoints(Player p)
    {
        return Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(p.getUniqueId()).getCurrentPoints();
    }

    //[ Util ]=========================================================

    // 인벤토리가 ui인지 확인
    public static Boolean CheckInvenIsShopUI(Inventory i)
    {

        return i.getSize() == 54 && i.getItem(53) != null &&
                DynamicShop.ccShop.get().contains(ChatColor.stripColor(i.getItem(53).getItemMeta().getDisplayName())) &&
                i.getItem(53).getType().name().contains("SIGN");
    }

    // 지정된 이름,lore,수량의 아이탬 스택 생성및 반환
    public static ItemStack CreateItemStack(Material material, ItemMeta _meta, String name, ArrayList<String> lore, int amount)
    {
        ItemStack istack = new ItemStack(material,amount);

        ItemMeta meta = _meta;
        if(_meta == null) meta = istack.getItemMeta();
        if(!name.equals("")) meta.setDisplayName(name);
        meta.setLore(lore);
        istack.setItemMeta(meta);
        return istack;
    }

    // 유저 데이터를 다시 만들고 만들어졌는지 확인함.
    public static boolean RecreateUserData(Player player)
    {
        if(DynamicShop.ccUser.get().contains(player.getUniqueId().toString()))
        {
            return true;
        }

        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".tmpString","");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".interactItem","");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".cmdHelp",true);
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".lastJoin",System.currentTimeMillis());
        DynamicShop.ccUser.save();

        return DynamicShop.ccUser.get().contains(player.getUniqueId().toString());
    }

    // Shop 플러그인에서 데이터 가져오기
    public static void ConvertDataFromShop(Player player)
    {
        File[] allFile = new File(Bukkit.getServer().getPluginManager().getPlugin("DynamicShop").getDataFolder() + "/Convert/Shop").listFiles();

        if(allFile.length == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + "There is no file to convert.");
            return;
        }

        for(File f:allFile)
        {
            try
            {
                CustomConfig cc = new CustomConfig();
                String filename = f.getName().replace(".yml","");
                if(cc.open(filename,"Convert/Shop"))
                {
                    ConfigurationSection confSec = cc.get().getConfigurationSection("data.inventory");

                    String shopname = filename.replace("/","");
                    DynamicShop.ccShop.get().set(shopname+".Options.page",2);
                    DynamicShop.ccShop.get().set(shopname+".Options.permission","");

                    String[] itemList =confSec.getString("items").split("},");

                    int idx = 0;
                    for (String s:itemList) {
                        //UNSPECIFIC_META:{meta-type=UNSPECIFIC, enchants={DURABILITY=3, KNOCKBACK=2}}
                        //{{v=1631, type=CHEST}=null
                        //{{v=1631, type=DIAMOND_SWORD}={meta-type=UNSPECIFIC, enchants={DURABILITY=3, KNOCKBACK=2}}}]
//                        String metaStr = null;
//                        if(s.contains("meta-type"))
//                        {
//                            metaStr = s.substring(s.indexOf("{meta-type"),s.length()-2); // {meta-type=UNSPECIFIC, enchants={DURABILITY=3, KNOCKBACK=2}}
//                        }

                        int start = s.indexOf("type=")+5;
                        int end = s.indexOf("}");
                        String temp = s.substring(start,end);

                        try
                        {
                            Material m = Material.getMaterial(temp);

//                            if(metaStr != null)
//                            {
//                                Map<String, Object> tempMap = Map.class.cast(metaStr);
//                                ItemMeta im = (ItemMeta) ConfigurationSerialization.deserializeObject(tempMap);
//                                DynamicShop.ccShop.get().set(shopname+"."+ idx +".itemStack",im);
//                            }

                            DynamicShop.ccShop.get().set(shopname+"."+ idx +".mat",m.name());
                            idx += 1;
                        }catch (Exception e)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + "fail to parse itemtype " + temp + ". skip to next");
                            for(StackTraceElement ste:e.getStackTrace())
                            {
                                DynamicShop.console.sendMessage(ste.toString());
                            }
                        }
                    }

                    idx = 0;
                    for(String s:confSec.getConfigurationSection("slotdata").getKeys(false))
                    {
                        if(DynamicShop.ccShop.get().contains(shopname+"."+idx+".mat"))
                        {
                            DynamicShop.ccShop.get().set(shopname+"."+idx+".value",confSec.getInt("slotdata."+s+".cost"));
                            DynamicShop.ccShop.get().set(shopname+"."+idx+".median",10000);
                            DynamicShop.ccShop.get().set(shopname+"."+idx+".stock",10000);

                        }
                        else
                        {
                            continue;
                        }

                        idx += 1;
                    }

                    player.sendMessage(DynamicShop.dsPrefix + "Converted: " + f.getName());
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + "Convert failed: " + f.getName());
                }
            }
            catch (Exception e1)
            {
                player.sendMessage(DynamicShop.dsPrefix + "Convert failed: " + f.getName());
            }
        }

        DynamicShop.ccShop.save();
    }

    // 아이탬 이름 정돈
    public static String GetBeautifiedName(Material mat)
    {
        String temp = mat.toString().replace("_"," ").toLowerCase();
        String[] temparr = temp.split(" ");

        StringBuilder finalStr = new StringBuilder();
        for (String s:temparr)
        {
            s = (""+s.charAt(0)).toUpperCase() + s.substring(1);
            finalStr.append(s).append(" ");
        }
        finalStr = new StringBuilder(finalStr.substring(0, finalStr.length() - 1));

        return finalStr.toString();
    }

    // 거래 로그 기록
    public static void AddLog(String shopName,String itemName,int amount,double value,String curr,String player)
    {
        if(DynamicShop.ccShop.get().contains(shopName+".Options.log") && DynamicShop.ccShop.get().getBoolean(shopName+".Options.log"))
        {
            SimpleDateFormat sdf = new SimpleDateFormat ( "yyMMdd,HHmmss");
            String timeStr = sdf.format (System.currentTimeMillis());

            int i = 0;
            if(DynamicShop.ccLog.get().contains(shopName)) i = DynamicShop.ccLog.get().getConfigurationSection(shopName).getKeys(false).size();

            DynamicShop.ccLog.get().set(shopName+"."+i,timeStr +","+itemName + "," + amount + "," + Math.round(value*100)/100.0 + "," + curr+","+player);
            DynamicShop.ccLog.save();
        }

        if(DynamicShop.ccLog.get().getKeys(true).size() > 500)
        {
            DynamicShop.SetupLogFile();
        }
    }

    // 내림
    public static int RoundDown(int old)
    {
        if(old < 10)
        {
            return  old;
        }

        if(old%10 != 0)
        {
            old = (old/10) * 10;
        }
        else if(old%100 != 0)
        {
            old = (old/100) * 100;
        }
        else if(old%1000 != 0)
        {
            old = (old/1000) * 1000;
        }
        else
        {
            old = (old/10000) * 10000;
        }

        if(old < 1) old = 1;

        return  old;
    }

    // 소리 재생
    public static void PlayerSoundEffect(Player player, String key)
    {
        try
        {
            player.playSound(player.getLocation(),Sound.valueOf(DynamicShop.ccSound.get().getString(key)),1,1);
        }
        catch (Exception e)
        {
            if(DynamicShop.ccSound.get().contains(key))
            {
                if(DynamicShop.ccSound.get().getString(key).length() > 1)
                {
                    DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server + " Sound play failed: " + key + "/" + DynamicShop.ccSound.get().getString(key));
                }
            }
            else
            {
                DynamicShop.console.sendMessage(DynamicShop.dsPrefix_server + " Sound play failed. Path is missing: " + key);
            }
        }
    }
}
