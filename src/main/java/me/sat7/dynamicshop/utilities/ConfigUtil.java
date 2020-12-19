package me.sat7.dynamicshop.utilities;

import lombok.Getter;
import lombok.Setter;
import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Random;

public final class ConfigUtil {
    private static int randomStockCount = 1;
    @Getter @Setter
    private static int currentTax;

    private ConfigUtil() {

    }

    public static void randomChange(Random generator) {
        // 인게임 30분마다 실행됨 (500틱)
        randomStockCount += 1;
        if (randomStockCount > 24) {
            randomStockCount = 0;
            ShopUtil.ccShop.save();
        }

        boolean needToUpdateUI = false;

        for (String shop : ShopUtil.ccShop.get().getKeys(false)) {
            // fluctuation
            ConfigurationSection confSec = ShopUtil.ccShop.get().getConfigurationSection(shop + ".Options.fluctuation");
            if (confSec != null) {
                int interval = confSec.getInt("interval");

                if (interval != 1 && interval != 2 && interval != 4 && interval != 8 && interval != 24) {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Wrong value at " + shop + ".Options.fluctuation.interval");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Reset to 2");
                    confSec.set("interval", 2);
                    interval = 2;
                    ShopUtil.ccShop.save();
                }

                if (randomStockCount % interval != 0) continue;

                for (String item : ShopUtil.ccShop.get().getConfigurationSection(shop).getKeys(false)) {
                    try {
                        int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.
                        if (!ShopUtil.ccShop.get().contains(shop + "." + item + ".value")) continue; // 장식용은 스킵

                        int oldStock = ShopUtil.ccShop.get().getInt(shop + "." + item + ".stock");
                        if (oldStock <= 1) continue; // 무한재고에 대해서는 스킵
                        int oldMedian = ShopUtil.ccShop.get().getInt(shop + "." + item + ".median");
                        if (oldMedian <= 1) continue; // 고정가 상품에 대해서는 스킵

                        boolean dir = generator.nextBoolean();
                        float amount = generator.nextFloat() * (float) confSec.getDouble("strength");
                        if (dir) amount *= -1;

                        oldStock += oldMedian * (amount / 100.0);

                        if (oldStock < 2) oldStock = 2;

                        ShopUtil.ccShop.get().set(shop + "." + item + ".stock", oldStock);
                        needToUpdateUI = true;
                    } catch (Exception ignored) {
                    }
                }
            }

            // stock stabilizing
            ConfigurationSection confSec2 = ShopUtil.ccShop.get().getConfigurationSection(shop + ".Options.stockStabilizing");
            if (confSec2 != null) {
                int interval = confSec2.getInt("interval");

                if (interval != 1 && interval != 2 && interval != 4 && interval != 8 && interval != 24) {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Wrong value at " + shop + ".Options.stockStabilizing.interval");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Reset to 24");
                    confSec2.set("interval", 24);
                    interval = 24;
                    ShopUtil.ccShop.save();
                }

                if (randomStockCount % interval != 0) continue;

                for (String item : ShopUtil.ccShop.get().getConfigurationSection(shop).getKeys(false)) {
                    try {
                        int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.
                        if (!ShopUtil.ccShop.get().contains(shop + "." + item + ".value")) continue; // 장식용은 스킵

                        int oldStock = ShopUtil.ccShop.get().getInt(shop + "." + item + ".stock");
                        if (oldStock < 1) continue; // 무한재고에 대해서는 스킵
                        int oldMedian = ShopUtil.ccShop.get().getInt(shop + "." + item + ".median");
                        if (oldMedian < 1) continue; // 고정가 상품에 대해서는 스킵

                        double amount = oldMedian * (confSec2.getDouble("strength") / 100.0);
                        if (oldStock < oldMedian) {
                            oldStock += (int) (amount);
                            if (oldStock > oldMedian) oldStock = oldMedian;
                        } else if (oldStock > oldMedian) {
                            oldStock -= (int) (amount);
                            if (oldStock < oldMedian) oldStock = oldMedian;
                        }

                        ShopUtil.ccShop.get().set(shop + "." + item + ".stock", oldStock);
                        needToUpdateUI = true;
                    } catch (Exception e) {
//                        for (StackTraceElement ste:e.getStackTrace()) {
//                            console.sendMessage(ste.toString());
//                        }
                    }
                }
            }
        }

        if (needToUpdateUI) {
            for (Player p : DynamicShop.plugin.getServer().getOnlinePlayers()) {
                if (p.getOpenInventory().getTitle().equalsIgnoreCase(LangUtil.ccLang.get().getString("TRADE_TITLE"))) {
                    String[] temp = DynamicShop.ccUser.get().getString(p.getUniqueId() + ".interactItem").split("/");
                    DynaShopAPI.openItemTradeGui(p, temp[0], temp[1]);
                }
            }
        }
    }

    public static void configSetup(DynamicShop dynamicShop) {
        dynamicShop.getConfig().options().copyHeader(true);
        dynamicShop.getConfig().options().header(
                "Language: ex) en-US,ko-KR,zh-CN" + "\nPrefix: Prefix of plugin messages" + "\nSalesTax: ~99%"
                        + "\nUseShopCommand: Set this to false if you want to disable /shop command"
                        + "\nDefaultShopName: This shop will open when player run /shop or /ds shop command"
                        + "\nDisplayStockAsStack: ex) true: 10Stacks, false: 640"
                        + "\nDeliveryChargeScale: 0.01~"
                        + "\nNumberOfPlayer: This number is used to calculate the recommended median. 3~100"
        );

        double salesTax;
        if (dynamicShop.getConfig().contains("SaleTax")) {
            salesTax = dynamicShop.getConfig().getDouble("SaleTax");
            dynamicShop.getConfig().set("SaleTax", null);
        } else {
            salesTax = dynamicShop.getConfig().getDouble("SalesTax");
        }
        if (salesTax < 0) salesTax = 0;
        if (salesTax > 99) salesTax = 99;
        dynamicShop.getConfig().set("SalesTax", salesTax);
        setCurrentTax((int) salesTax);
        dynamicShop.getConfig().set("ShowTax", dynamicShop.getConfig().getBoolean("ShowTax"));

        dynamicShop.getConfig().set("Language", dynamicShop.getConfig().get("Language"));
        dynamicShop.getConfig().set("Prefix", dynamicShop.getConfig().get("Prefix"));
        DynamicShop.dsPrefix = dynamicShop.getConfig().getString("Prefix");
        dynamicShop.getConfig().set("UseShopCommand", dynamicShop.getConfig().getBoolean("UseShopCommand"));
        dynamicShop.getConfig().set("DefaultShopName", dynamicShop.getConfig().getString("DefaultShopName"));

        double DeliveryChargeScale = dynamicShop.getConfig().getDouble("DeliveryChargeScale");
        if (DeliveryChargeScale <= 0.01) DeliveryChargeScale = 0.01;
        dynamicShop.getConfig().set("DeliveryChargeScale", DeliveryChargeScale);

        dynamicShop.getConfig().set("DisplayStockAsStack", dynamicShop.getConfig().getBoolean("DisplayStockAsStack"));

        int numPlayer = dynamicShop.getConfig().getInt("NumberOfPlayer");
        if (numPlayer <= 3) numPlayer = 3;
        if (numPlayer > 100) numPlayer = 100;
        dynamicShop.getConfig().set("NumberOfPlayer", numPlayer);

        dynamicShop.getConfig().set("SaveLogs", dynamicShop.getConfig().getBoolean("SaveLogs"));
        dynamicShop.getConfig().set("CullLogs", dynamicShop.getConfig().getBoolean("CullLogs"));
        dynamicShop.getConfig().set("LogCullAgeMinutes", dynamicShop.getConfig().getInt("LogCullAgeMinutes"));
        dynamicShop.getConfig().set("LogCullTimeMinutes", dynamicShop.getConfig().getInt("LogCullTimeMinutes"));

        dynamicShop.getConfig().set("OnClickCloseButton_OpenStartPage", dynamicShop.getConfig().getBoolean("OnClickCloseButton_OpenStartPage"));
        dynamicShop.getConfig().set("OpenStartPageInsteadOfDefaultShop", dynamicShop.getConfig().getBoolean("OpenStartPageInsteadOfDefaultShop"));

        dynamicShop.saveConfig();
    }

    public static void resetTax() {
        currentTax = DynamicShop.plugin.getConfig().getInt("SalesTax");
    }
}
