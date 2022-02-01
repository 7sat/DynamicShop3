package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public class ShopSettings extends InGameUI {

    public ShopSettings()
    {
        uiType = UI_TYPE.ShopSettings;
    }

    public Inventory getGui(Player player, String shopName) {
        Inventory inventory = Bukkit.createInventory(player,36, LangUtil.ccLang.get().getString("SHOP_SETTING_TITLE"));

        // 닫기 버튼
        ItemStack closeBtn =  ItemsUtil.createItemStack(Material.BARRIER,null,
                LangUtil.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Arrays.asList(LangUtil.ccLang.get().getString("CLOSE_LORE"))),1);
        inventory.setItem(27,closeBtn);

        ConfigurationSection confSec_Options = ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options");
        String permStr = confSec_Options.getString("permission");
        String permNew = "dshop.user.shop."+shopName;
        Material permIcon;
        if(permStr.isEmpty())
        {
            permStr = LangUtil.ccLang.get().getString("NULL(OPEN)");
            permIcon = Material.IRON_BLOCK;
        }
        else
        {
            permNew = LangUtil.ccLang.get().getString("NULL(OPEN)");
            permIcon = Material.GOLD_BLOCK;
        }

        // 권한 버튼
        ArrayList<String> permLore = new ArrayList<>();
        permLore.add("§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + permStr);
        if(!permStr.equalsIgnoreCase(LangUtil.ccLang.get().getString("NULL(OPEN)")))
        {
            permLore.add("§9 - " + permStr + ".buy");
            permLore.add("§9 - " + permStr + ".sell");
        }
        permLore.add("§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + permNew);

        ItemStack permBtn =  ItemsUtil.createItemStack(permIcon,null,
                LangUtil.ccLang.get().getString("PERMISSION"),permLore,1);
        inventory.setItem(0,permBtn);

        // 최대페이지 버튼
        ItemStack maxPageBtn =  ItemsUtil.createItemStack(Material.PAPER,null,
                LangUtil.ccLang.get().getString("MAXPAGE"),
                new ArrayList<>(Arrays.asList(LangUtil.ccLang.get().getString("MAXPAGE_LORE"), LangUtil.ccLang.get().getString("L_R_SHIFT"))), ShopUtil.ccShop.get().getInt(shopName+".Options.page"));
        inventory.setItem(1,maxPageBtn);

        // 영업시간 버튼
        int curTime = (int)(player.getWorld().getTime())/1000 + 6;
        if(curTime>24) curTime -= 24;
        if(ShopUtil.ccShop.get().contains(shopName+".Options.shophours"))
        {
            String[] temp = ShopUtil.ccShop.get().getString(shopName+".Options.shophours").split("~");
            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            ItemStack open24Btn =  ItemsUtil.createItemStack(Material.CLOCK,null,
                    LangUtil.ccLang.get().getString("TIME.SHOPHOURS"),
                    new ArrayList<>(Arrays.asList(
                            LangUtil.ccLang.get().getString("TIME.CUR").replace("{time}",curTime+""),
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": ",
                            "§9 - " + LangUtil.ccLang.get().getString("TIME.OPEN") + ": " + open,
                            "§9 - " + LangUtil.ccLang.get().getString("TIME.CLOSE") + ": " + close,
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("TIME.OPEN24"))),
                    1);
            inventory.setItem(6,open24Btn);
            ItemStack opentimeBtn =  ItemsUtil.createItemStack(Material.CLOCK,null,
                    "§f"+ LangUtil.ccLang.get().getString("TIME.OPEN"),
                    new ArrayList<>(Arrays.asList(LangUtil.ccLang.get().getString("TIME.OPEN_LORE"), LangUtil.ccLang.get().getString("L_R_SHIFT"))), open);
            inventory.setItem(7,opentimeBtn);
            ItemStack closetimeBtn =  ItemsUtil.createItemStack(Material.CLOCK,null,
                    "§f"+ LangUtil.ccLang.get().getString("TIME.CLOSE"),
                    new ArrayList<>(Arrays.asList(LangUtil.ccLang.get().getString("TIME.CLOSE_LORE"), LangUtil.ccLang.get().getString("L_R_SHIFT"))), close);
            inventory.setItem(8,closetimeBtn);
        }
        else
        {
            ItemStack open24Btn =  ItemsUtil.createItemStack(Material.CLOCK,null,
                    LangUtil.ccLang.get().getString("TIME.SHOPHOURS"),
                    new ArrayList<>(Arrays.asList(
                            LangUtil.ccLang.get().getString("TIME.CUR").replace("{time}",curTime+""),
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + LangUtil.ccLang.get().getString("TIME.OPEN24"),
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("TIME.SET_SHOPHOURS"))),
                    1);
            inventory.setItem(6,open24Btn);
        }

        // 랜덤스톡 버튼
        ConfigurationSection flucConf = ShopUtil.ccShop.get().getConfigurationSection(shopName+".Options.fluctuation");
        if(flucConf != null)
        {
            ItemStack flucToggleBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("FLUC.FLUCTUATION"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": "+ LangUtil.ccLang.get().getString("ON"),
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("OFF"))),
                    1);
            inventory.setItem(15,flucToggleBtn);

            int tempCount = flucConf.getInt("interval")/2;
            if(tempCount < 1) tempCount = 1;
            if(tempCount > 64) tempCount = 64;

            ItemStack flucIntervalBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("FLUC.INTERVAL"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + flucConf.getInt("interval")/2.0 + "h",
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("L_R_SHIFT"))),
                    tempCount);
            inventory.setItem(16,flucIntervalBtn);

            tempCount = (int)(flucConf.getDouble("strength") * 10);
            if(tempCount < 1) tempCount = 1;
            if(tempCount > 64) tempCount = 64;

            ItemStack flucStrengthBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("FLUC.STRENGTH"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": ~" + flucConf.get("strength") + "%",
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("STOCKSTABILIZING.L_R_SHIFT"))),
                    tempCount);
            inventory.setItem(17,flucStrengthBtn);
        }
        else
        {
            ItemStack flucToggleBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("FLUC.FLUCTUATION"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": "+ LangUtil.ccLang.get().getString("OFF"),
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("ON"))),
                    1);
            inventory.setItem(15,flucToggleBtn);
        }

        // 재고 안정화 버튼
        ConfigurationSection stockStableConf = ShopUtil.ccShop.get().getConfigurationSection(shopName+".Options.stockStabilizing");
        if(stockStableConf != null)
        {
            ItemStack ssTogleBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("STOCKSTABILIZING.SS"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": "+ LangUtil.ccLang.get().getString("ON"),
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("OFF"))),
                    1);
            inventory.setItem(24,ssTogleBtn);

            int tempCount = stockStableConf.getInt("interval")/2;
            if(tempCount < 1) tempCount = 1;
            if(tempCount > 64) tempCount = 64;

            ItemStack intervalBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("FLUC.INTERVAL"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + stockStableConf.getInt("interval")/2.0 + "h",
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("L_R_SHIFT"))),
                    tempCount);
            inventory.setItem(25,intervalBtn);

            tempCount = (int)(stockStableConf.getDouble("strength") * 10);
            if(tempCount < 1) tempCount = 1;
            if(tempCount > 64) tempCount = 64;

            ItemStack strengthBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("FLUC.STRENGTH"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": ~" + stockStableConf.get("strength") + "%",
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("STOCKSTABILIZING.L_R_SHIFT"))),
                    tempCount);
            inventory.setItem(26,strengthBtn);
        }
        else
        {
            ItemStack ssToggleBtn =  ItemsUtil.createItemStack(Material.COMPARATOR,null,
                    LangUtil.ccLang.get().getString("STOCKSTABILIZING.SS"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": "+ LangUtil.ccLang.get().getString("OFF"),
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": "+ LangUtil.ccLang.get().getString("ON"))),
                    1);
            inventory.setItem(24,ssToggleBtn);
        }

        // 세금
        int globalTax = ConfigUtil.getCurrentTax();
        if(ShopUtil.ccShop.get().contains(shopName+".Options.SalesTax"))
        {
            ItemStack taxToggleBtn =  ItemsUtil.createItemStack(Material.IRON_INGOT,null,
                    LangUtil.ccLang.get().getString("TAX.SALESTAX"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + LangUtil.ccLang.get().getString("TAX.USE_LOCAL"),
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": " +
                                    LangUtil.ccLang.get().getString("TAX.USE_GLOBAL").replace("{tax}",globalTax+"")
                    )),1);
            inventory.setItem(33,taxToggleBtn);

            int temp = ShopUtil.ccShop.get().getInt(shopName+".Options.SalesTax");
            if(temp == 0) temp = 1;

            ItemStack taxBtn =  ItemsUtil.createItemStack(Material.IRON_INGOT,null,
                    LangUtil.ccLang.get().getString("TAX.SALESTAX"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + ShopUtil.ccShop.get().getInt(shopName + ".Options.SalesTax") + "%",
                            LangUtil.ccLang.get().getString("L_R_SHIFT")
                    )), temp);
            inventory.setItem(34,taxBtn);
        }
        else
        {
            ItemStack taxToggleBtn =  ItemsUtil.createItemStack(Material.IRON_INGOT,null,
                    LangUtil.ccLang.get().getString("TAX.SALESTAX"),
                    new ArrayList<>(Arrays.asList(
                            "§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " +
                                    LangUtil.ccLang.get().getString("TAX.USE_GLOBAL").replace("{tax}",globalTax+""),
                            "§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + LangUtil.ccLang.get().getString("TAX.USE_LOCAL")
                    )),1);
            inventory.setItem(33,taxToggleBtn);
        }

        // 플래그 버튼들
        String cur1;
        String set1;
        Material icon1;
        if(confSec_Options.contains("flag.signshop"))
        {
            icon1 = Material.GREEN_STAINED_GLASS_PANE;
            cur1 = LangUtil.ccLang.get().getString("SET");
            set1 = LangUtil.ccLang.get().getString("UNSET");
        }
        else
        {
            icon1 = Material.BLACK_STAINED_GLASS_PANE;
            cur1 = LangUtil.ccLang.get().getString("UNSET");
            set1 = LangUtil.ccLang.get().getString("SET");
        }
        ArrayList<String> f1Lore = new ArrayList<>();
        f1Lore.add(LangUtil.ccLang.get().getString("SIGNSHOP_LORE"));
        f1Lore.add("§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + cur1);
        f1Lore.add("§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + set1);

        ItemStack flag1 =  ItemsUtil.createItemStack(icon1,null,
                LangUtil.ccLang.get().getString("FLAG")+": signshop",
                f1Lore,1);
        inventory.setItem(9,flag1);

        String cur2;
        String set2;
        Material icon2;
        if(confSec_Options.contains("flag.localshop"))
        {
            icon2 = Material.GREEN_STAINED_GLASS_PANE;
            cur2 = LangUtil.ccLang.get().getString("SET");
            set2 = LangUtil.ccLang.get().getString("UNSET");
        }
        else
        {
            icon2 = Material.BLACK_STAINED_GLASS_PANE;
            cur2 = LangUtil.ccLang.get().getString("UNSET");
            set2 = LangUtil.ccLang.get().getString("SET");
        }
        ArrayList<String> f2Lore = new ArrayList<>();
        f2Lore.add(LangUtil.ccLang.get().getString("LOCALSHOP_LORE"));
        f2Lore.add(LangUtil.ccLang.get().getString("LOCALSHOP_LORE2"));
        f2Lore.add("§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + cur2);
        f2Lore.add("§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + set2);

        ItemStack flag2 =  ItemsUtil.createItemStack(icon2,null,
                LangUtil.ccLang.get().getString("FLAG")+": localshop",
                f2Lore,1);
        inventory.setItem(10,flag2);

        String cur3;
        String set3;
        Material icon3;
        if(confSec_Options.contains("flag.deliverycharge"))
        {
            icon3 = Material.GREEN_STAINED_GLASS_PANE;
            cur3 = LangUtil.ccLang.get().getString("SET");
            set3 = LangUtil.ccLang.get().getString("UNSET");
        }
        else
        {
            icon3 = Material.BLACK_STAINED_GLASS_PANE;
            cur3 = LangUtil.ccLang.get().getString("UNSET");
            set3 = LangUtil.ccLang.get().getString("SET");
        }
        ArrayList<String> f3Lore = new ArrayList<>();
        f3Lore.add(LangUtil.ccLang.get().getString("DELIVERYCHARG_LORE"));
        f3Lore.add("§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + cur3);
        f3Lore.add("§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + set3);

        ItemStack flag3 =  ItemsUtil.createItemStack(icon3,null,
                LangUtil.ccLang.get().getString("FLAG")+": deliverycharge",
                f3Lore,1);
        inventory.setItem(11,flag3);

        String cur4;
        String set4;
        Material icon4;
        if(confSec_Options.contains("flag.jobpoint"))
        {
            icon4 = Material.GREEN_STAINED_GLASS_PANE;
            cur4 = LangUtil.ccLang.get().getString("SET");
            set4 = LangUtil.ccLang.get().getString("UNSET");
        }
        else
        {
            icon4 = Material.BLACK_STAINED_GLASS_PANE;
            cur4 = LangUtil.ccLang.get().getString("UNSET");
            set4 = LangUtil.ccLang.get().getString("SET");
        }
        ArrayList<String> f4Lore = new ArrayList<>();
        f4Lore.add(LangUtil.ccLang.get().getString("JOBPOINT_LORE"));
        f4Lore.add("§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + cur4);
        f4Lore.add("§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + set4);

        ItemStack flag4 =  ItemsUtil.createItemStack(icon4,null,
                LangUtil.ccLang.get().getString("FLAG")+": jobpoint",
                f4Lore,1);
        inventory.setItem(12,flag4);

        String cur5;
        String set5;
        Material icon5;
        if(confSec_Options.contains("flag.showvaluechange"))
        {
            icon5 = Material.GREEN_STAINED_GLASS_PANE;
            cur5 = LangUtil.ccLang.get().getString("SET");
            set5 = LangUtil.ccLang.get().getString("UNSET");
        }
        else
        {
            icon5 = Material.BLACK_STAINED_GLASS_PANE;
            cur5 = LangUtil.ccLang.get().getString("UNSET");
            set5 = LangUtil.ccLang.get().getString("SET");
        }
        ArrayList<String> f5Lore = new ArrayList<>();
        f5Lore.add(LangUtil.ccLang.get().getString("SHOW_VALUE_CHANGE_LORE"));
        f5Lore.add("§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + cur5);
        f5Lore.add("§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + set5);

        ItemStack flag5 =  ItemsUtil.createItemStack(icon5,null,
                LangUtil.ccLang.get().getString("FLAG")+": showvaluechange",
                f5Lore,1);
        inventory.setItem(13,flag5);

        // 로그 버튼
        String log_cur;
        String log_set;
        if(confSec_Options.contains("log"))
        {
            log_cur = LangUtil.ccLang.get().getString("ON");
            log_set = LangUtil.ccLang.get().getString("OFF");
        }
        else
        {
            log_cur = LangUtil.ccLang.get().getString("OFF");
            log_set = LangUtil.ccLang.get().getString("ON");
        }
        ArrayList<String> logLore = new ArrayList<>();
        logLore.add("§9"+ LangUtil.ccLang.get().getString("CUR_STATE")+": " + log_cur);
        logLore.add("§e"+ LangUtil.ccLang.get().getString("CLICK")+": " + log_set);
        ItemStack logToggleBtn =  ItemsUtil.createItemStack(Material.BOOK,null,
                LangUtil.ccLang.get().getString("LOG.LOG"),
                logLore,1);
        inventory.setItem(30,logToggleBtn);

        ItemStack logClearBtn =  ItemsUtil.createItemStack(Material.RED_STAINED_GLASS_PANE,null,
                LangUtil.ccLang.get().getString("LOG.DELETE"),
                null,1);
        inventory.setItem(31,logClearBtn);
        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        if (player == null)
            return;

        String[] temp = DynamicShop.ccUser.get().getString(player.getUniqueId() + ".interactItem").split("/");
        String shopName = temp[0];

        // 닫기버튼
        if (e.getSlot() == 27)
        {
            DynaShopAPI.openShopGui(player, temp[0], 1);
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "");
        }
        // 권한
        else if (e.getSlot() == 0)
        {
            if (ShopUtil.ccShop.get().getString(shopName + ".Options.permission").isEmpty())
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " permission true");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " permission false");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 최대 페이지
        else if (e.getSlot() == 1)
        {
            int oldvalue = ShopUtil.ccShop.get().getInt(shopName + ".Options.page");
            int targetValue;

            if (e.isRightClick())
            {
                targetValue = oldvalue + 1;
                if (e.isShiftClick()) targetValue += 4;
                if (targetValue >= 20) targetValue = 20;
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " maxpage " + targetValue);
            } else
            {
                targetValue = oldvalue - 1;
                if (e.isShiftClick()) targetValue -= 4;
                if (targetValue <= 1) targetValue = 1;
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " maxpage " + targetValue);
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 영업시간
        else if (e.getSlot() >= 6 && e.getSlot() <= 8)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.shophours"))
            {
                String[] shopHour = ShopUtil.ccShop.get().getString(shopName + ".Options.shophours").split("~");
                Integer open = Integer.parseInt(shopHour[0]);
                int close = Integer.parseInt(shopHour[1]);
                int edit = -1;
                if (e.isRightClick()) edit = 1;
                if (e.isShiftClick()) edit *= 5;

                if (e.getSlot() == 6)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours 0 0");
                } else if (e.getSlot() == 7)
                {
                    open += edit;

                    if (open.equals(close))
                    {
                        if (e.isRightClick())
                        {
                            open += 1;
                        } else
                        {
                            open -= 1;
                        }
                    }

                    if (open < 1) open = 1;
                    if (open > 24) open = 24;

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours " + open + " " + close);
                } else if (e.getSlot() == 8)
                {
                    close += edit;

                    if (open.equals(close))
                    {
                        if (e.isRightClick())
                        {
                            close += 1;
                        } else
                        {
                            close -= 1;
                        }
                    }

                    if (close < 1) close = 1;
                    if (close > 24) close = 24;

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours " + open + " " + close);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == 6)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours 20 6");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 랜덤스톡
        else if (e.getSlot() >= 15 && e.getSlot() <= 17)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.fluctuation"))
            {
                int interval = ShopUtil.ccShop.get().getInt(shopName + ".Options.fluctuation.interval");
                double strength = ShopUtil.ccShop.get().getDouble(shopName + ".Options.fluctuation.strength");

                if (e.getSlot() == 15)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation off");
                } else if (e.getSlot() == 16)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    interval += edit;

                    if (interval < 1) interval = 1;
                    if (interval > 999) interval = 999;

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation " + interval + " " + strength);
                } else if (e.getSlot() == 17)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    strength += edit;

                    if (strength < 0.1) strength = 0.1;
                    if (strength > 64) strength = 64;

                    strength = Math.round(strength * 100) / 100.0;

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation " + interval + " " + strength);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == 15)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation 12 0.1");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 스톡 안정화
        else if (e.getSlot() >= 24 && e.getSlot() <= 26)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.stockStabilizing"))
            {
                int interval = ShopUtil.ccShop.get().getInt(shopName + ".Options.stockStabilizing.interval");
                double strength = ShopUtil.ccShop.get().getDouble(shopName + ".Options.stockStabilizing.strength");

                if (e.getSlot() == 24)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing off");
                } else if (e.getSlot() == 25)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    interval += edit;
                    if (interval < 1) interval = 1;
                    if (interval > 999) interval = 999;

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing " + interval + " " + strength);
                } else if (e.getSlot() == 26)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    strength += edit;

                    if (strength < 0.1) strength = 0.1;
                    if (strength > 25) strength = 25;

                    strength = (Math.round(strength * 100) / 100.0);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing " + interval + " " + strength);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == 24)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing 24 0.1");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 세금
        else if (e.getSlot() == 33 || e.getSlot() == 34)
        {
            // 전역,지역 토글
            if (e.getSlot() == 33)
            {
                if (ShopUtil.ccShop.get().contains(shopName + ".Options.SalesTax"))
                {
                    ShopUtil.ccShop.get().set(shopName + ".Options.SalesTax", null);
                } else
                {
                    ShopUtil.ccShop.get().set(shopName + ".Options.SalesTax", DynamicShop.plugin.getConfig().getInt("SalesTax"));
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            // 수치설정
            else if (ShopUtil.ccShop.get().contains(shopName + ".Options.SalesTax"))
            {
                int edit = -1;
                if (e.isRightClick()) edit = 1;
                if (e.isShiftClick()) edit *= 5;

                int result = ShopUtil.ccShop.get().getInt(shopName + ".Options.SalesTax") + edit;
                if (result < 0) result = 0;
                if (result > 99) result = 99;

                ShopUtil.ccShop.get().set(shopName + ".Options.SalesTax", result);

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            ShopUtil.ccShop.save();
        }
        // signshop
        else if (e.getSlot() == 9)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.signshop"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag signshop unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag signshop set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // localshop
        else if (e.getSlot() == 10)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.localshop"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag localshop unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag localshop set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // deliverycharge
        else if (e.getSlot() == 11)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.deliverycharge"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag deliverycharge unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag deliverycharge set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // jobpoint
        else if (e.getSlot() == 12)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag jobpoint unset");
            } else
            {
                if (!JobsHook.jobsRebornActive)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.JOBSREBORN_NOT_FOUND"));
                    return;
                }

                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag jobpoint set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // showValueChange
        else if (e.getSlot() == 13)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.showvaluechange"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showvaluechange unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showvaluechange set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // log
        else if (e.getSlot() >= 30 && e.getSlot() <= 31)
        {
            if (e.getSlot() == 30)
            {
                if (ShopUtil.ccShop.get().contains(shopName + ".Options.log") && ShopUtil.ccShop.get().getBoolean(shopName + ".Options.log"))
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log disable");
                } else
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log enable");
                }
            } else if (e.getSlot() == 31)
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log clear");
            }

            DynaShopAPI.openShopSettingGui(player, shopName);
        }
    }
}
