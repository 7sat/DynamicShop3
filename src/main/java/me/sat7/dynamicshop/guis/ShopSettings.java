package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.commands.shop.Command;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.economyhook.PlayerpointHook;
import me.sat7.dynamicshop.events.OnChat;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.economyhook.JobsHook;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ShopSettings extends InGameUI
{
    public ShopSettings()
    {
        uiType = UI_TYPE.ShopSettings;
    }

    private final int ENABLE_TOGGLE = 0;
    private final int PERMISSION = 1;
    private final int MAX_PAGE = 2;
    private final int BACKGROUND = 3;
    private final int ROTATION_EDITOR = 4;
    private final int SHOP_HOUR = 6;
    private final int SHOP_HOUR_OPEN = 7;
    private final int SHOP_HOUR_CLOSE = 8;
    private final int FLUC = 15;
    private final int FLUC_INTERVAL = 16;
    private final int FLUC_STRENGTH = 17;
    private final int STABLE = 24;
    private final int STABLE_INTERVAL = 25;
    private final int STABLE_STRENGTH = 26;

    private final int FLAG_SIGNSHOP = 9;
    private final int FLAG_LOCALSHOP = 10;
    private final int FLAG_DELIVERY = 11;
    private final int FLAG_SHOW_VALUE_CHANGE = 12;
    private final int FLAG_HIDE_STOCK = 13;
    private final int FLAG_HIDE_PRICING_TYPE = 18;
    private final int FLAG_HIDE_SHOP_BALANCE = 19;
    private final int FLAG_SHOW_MAX_STOCK = 20;
    private final int FLAG_HIDDEN_IN_COMMAND = 21;
    private final int FLAG_INT_ONLY = 22;

    private final int CURRENCY_VAULT = 27;
    private final int CURRENCY_EXP = 28;
    private final int CURRENCY_JP = 29;
    private final int CURRENCY_PP = 30;

    private final int CMD_TOGGLE = 33;
    private final int CMD_SELL = 34;
    private final int CMD_BUY = 35;
    private final int TAX_TOGGLE = 42;
    private final int TAX_AMOUNT = 43;
    private final int LOG_TOGGLE = 51;
    private final int LOG_PRINT_CONSOLE = 52;
    private final int LOG_PRINT_ADMIN = 53;

    private final int TRADE_UI_SETTING = 36;

    private final int CLOSE = 45;

    private String shopName;
    private String currency;

    public Inventory getGui(Player player, String shopName)
    {
        this.shopName = shopName;

        inventory = Bukkit.createInventory(player, 54, t(player, "SHOP_SETTING_TITLE") + "§7 | §8" + shopName);

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        ConfigurationSection confSec_Options = data.get().getConfigurationSection("Options");

        // 닫기 버튼
        CreateCloseButton(player, CLOSE);

        // 활성화 버튼
        boolean isShopEnable = confSec_Options.getBoolean("enable", true);
        Material enableToggleMat = isShopEnable ? Material.GREEN_STAINED_GLASS : Material.RED_STAINED_GLASS;
        String current = isShopEnable ? t(player, "SHOP_SETTING.STATE_ENABLE") : t(player, "SHOP_SETTING.STATE_DISABLE");
        String set = isShopEnable ? t(player, "SHOP_SETTING.STATE_DISABLE") : t(player, "SHOP_SETTING.STATE_ENABLE");
        String enableToggleLore = "§9" + t(player, "CUR_STATE") + ": " + current;
        enableToggleLore += "\n§e" + t(player, "CLICK") + ": " + set;
        CreateButton(ENABLE_TOGGLE, enableToggleMat, t(player, "SHOP_SETTING.STATE"), enableToggleLore);

        // 권한 버튼
        String permStr = confSec_Options.getString("permission");
        String permNew = "dshop.user.shop." + shopName;
        Material permIcon;
        if (permStr == null || permStr.isEmpty())
        {
            permStr = t(player, "NULL(OPEN)");
            permIcon = Material.IRON_BLOCK;
        } else
        {
            permNew = t(player, "NULL(OPEN)");
            permIcon = Material.GOLD_BLOCK;
        }

        ArrayList<String> permLore = new ArrayList<>();
        permLore.add("§9" + t(player, "CUR_STATE") + ": " + permStr);
        if (!permStr.equalsIgnoreCase(t(player, "NULL(OPEN)")))
        {
            permLore.add("§9 - " + permStr + ".buy");
            permLore.add("§9 - " + permStr + ".sell");
        }
        permLore.add("§e" + t(player, "CLICK") + ": " + permNew);
        CreateButton(PERMISSION, permIcon, t(player, "SHOP_SETTING.PERMISSION"), permLore);

        //최대 페이지 버튼
        CreateButton(MAX_PAGE, InGameUI.GetPageButtonIconMat(), t(player, "SHOP_SETTING.MAX_PAGE"), new ArrayList<>(Arrays.asList(t(player, "SHOP_SETTING.MAX_PAGE_LORE"), t(player, "SHOP_SETTING.L_R_SHIFT"))), data.get().getInt("Options.page"));

        // 배경 색상
        String color = confSec_Options.getString("background","");
        Material mat = Material.getMaterial( color.toUpperCase() + "_CONCRETE");
        if (mat == null)
            mat = Material.LIGHT_GRAY_CONCRETE;
        CreateButton(BACKGROUND, mat, t(player, "SHOP_SETTING.BACKGROUND"), "§7" + ChatColor.stripColor(t(player, "SHOP_SETTING.BACKGROUND_LORE")));

        // 로테이션 에디터
        int currentRotation = confSec_Options.getInt("Rotation.Current", -1);
        String rotationString = currentRotation == -1 ? t(player, "ROTATION_EDITOR.DISABLED") : String.valueOf(currentRotation + 1);
        CreateButton(ROTATION_EDITOR, Material.CLOCK, t(player, "SHOP_SETTING.ROTATION_EDITOR") + rotationString, "§7" + ChatColor.stripColor(t(player, "SHOP_SETTING.ROTATION_EDITOR_LORE")));

        // 영업시간 버튼
        int curTime = (int) (player.getWorld().getTime()) / 1000 + 6;
        if (curTime > 24) curTime -= 24;
        if (data.get().contains("Options.shophours"))
        {
            String[] temp = data.get().getString("Options.shophours").split("~");
            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            ArrayList<String> shopHourLore = new ArrayList<>(Arrays.asList(
                    t(player, "TIME.CUR").replace("{time}", String.valueOf(curTime)),
                    "§9" + t(player, "CUR_STATE") + ": ",
                    "§9 - " + t(player, "TIME.OPEN") + ": " + open,
                    "§9 - " + t(player, "TIME.CLOSE") + ": " + close,
                    "§e" + t(player, "CLICK") + ": " + t(player, "TIME.OPEN24")));
            CreateButton(SHOP_HOUR, Material.CLOCK, t(player, "TIME.SHOPHOURS"), shopHourLore);
            CreateButton(SHOP_HOUR_OPEN, Material.CLOCK, "§f" + t(player, "TIME.OPEN"), new ArrayList<>(Arrays.asList(t(player, "TIME.OPEN_LORE"), t(player, "SHOP_SETTING.L_R_SHIFT"))), open);
            CreateButton(SHOP_HOUR_CLOSE, Material.CLOCK, "§f" + t(player, "TIME.CLOSE"), new ArrayList<>(Arrays.asList(t(player, "TIME.CLOSE_LORE"), t(player, "SHOP_SETTING.L_R_SHIFT"))), close);
        } else
        {
            ArrayList<String> shopHourLore = new ArrayList<>(Arrays.asList(
                    t(player, "TIME.CUR").replace("{time}", String.valueOf(curTime)),
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "TIME.OPEN24"),
                    "§e" + t(player, "CLICK") + ": " + t(player, "TIME.SET_SHOPHOURS")));
            CreateButton(SHOP_HOUR, Material.CLOCK, t(player, "TIME.SHOPHOURS"), shopHourLore);
        }

        // 랜덤스톡 버튼
        ConfigurationSection flucConf = data.get().getConfigurationSection("Options.fluctuation");
        if (flucConf != null)
        {
            ArrayList<String> fluctuationLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "ON"),
                    "§e" + t(player, "LMB") + ": " + t(player, "OFF"),
                    "§7" + ChatColor.stripColor(t(player, "STOCK_SIMULATOR.SIMULATOR_BUTTON_LORE"))
                    ));
            CreateButton(FLUC, Material.COMPARATOR, t(player, "FLUCTUATION.FLUCTUATION"), fluctuationLore);

            ArrayList<String> fluctuation_interval_lore = new ArrayList<>(Arrays.asList(
                    t(player, "FLUCTUATION.INTERVAL_LORE"),
                    "§9" + t(player, "CUR_STATE") + ": " + flucConf.getInt("interval") / 2.0 + "h",
                    "§e" + t(player, "CLICK") + ": " + t(player, "SHOP_SETTING.L_R_SHIFT")));
            CreateButton(FLUC_INTERVAL, Material.COMPARATOR, t(player, "FLUCTUATION.INTERVAL"), fluctuation_interval_lore, Clamp(flucConf.getInt("interval") / 2, 1, 64));

            ArrayList<String> fluctuation_strength_lore = new ArrayList<>(Arrays.asList(
                    t(player, "FLUCTUATION.STRENGTH_LORE"),
                    "§9" + t(player, "CUR_STATE") + ": ~" + flucConf.get("strength") + "%",
                    "§e" + t(player, "CLICK") + ": " + t(player, "STOCK_STABILIZING.L_R_SHIFT")));
            CreateButton(FLUC_STRENGTH, Material.COMPARATOR, t(player, "FLUCTUATION.STRENGTH"), fluctuation_strength_lore, Clamp((int) (flucConf.getDouble("strength") * 10), 1, 64));
        } else
        {
            ItemStack flucToggleBtn = ItemsUtil.createItemStack(Material.COMPARATOR, null,
                    t(player, "FLUCTUATION.FLUCTUATION"),
                    new ArrayList<>(Arrays.asList(
                            "§9" + t(player, "CUR_STATE") + ": " + t(player, "OFF"),
                            "§e" + t(player, "LMB") + ": " + t(player, "ON"),
                            "§7" + ChatColor.stripColor(t(player, "STOCK_SIMULATOR.SIMULATOR_BUTTON_LORE"))
                            )),
                    1);
            inventory.setItem(FLUC, flucToggleBtn);
        }

        // 재고 안정화 버튼
        ConfigurationSection stockStableConf = data.get().getConfigurationSection("Options.stockStabilizing");
        if (stockStableConf != null)
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "ON"),
                    "§e" + t(player, "LMB") + ": " + t(player, "OFF"),
                    "§7" + ChatColor.stripColor(t(player, "STOCK_SIMULATOR.SIMULATOR_BUTTON_LORE"))
            ));
            CreateButton(STABLE, Material.COMPARATOR, t(player, "STOCK_STABILIZING.SS"), stableLore);

            ArrayList<String> stable_interval_Lore = new ArrayList<>(Arrays.asList(
                    t(player, "FLUCTUATION.INTERVAL_LORE"),
                    "§9" + t(player, "CUR_STATE") + ": " + stockStableConf.getInt("interval") / 2.0 + "h",
                    "§e" + t(player, "CLICK") + ": " + t(player, "SHOP_SETTING.L_R_SHIFT")));
            CreateButton(STABLE_INTERVAL, Material.COMPARATOR, t(player, "FLUCTUATION.INTERVAL"), stable_interval_Lore, Clamp(stockStableConf.getInt("interval") / 2, 1, 64));

            ArrayList<String> stable_strength_Lore = new ArrayList<>(Arrays.asList(
                    ConfigUtil.GetUseLegacyStockStabilization() ? t(player, "STOCK_STABILIZING.STRENGTH_LORE_A") : t(player, "STOCK_STABILIZING.STRENGTH_LORE_B"),
                    "§9" + t(player, "CUR_STATE") + ": ~" + stockStableConf.get("strength") + "%",
                    "§e" + t(player, "CLICK") + ": " + t(player, "STOCK_STABILIZING.L_R_SHIFT")));
            CreateButton(STABLE_STRENGTH, Material.COMPARATOR, t(player, "FLUCTUATION.STRENGTH"), stable_strength_Lore, Clamp((int) (stockStableConf.getDouble("strength") * 10), 1, 64));
        } else
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "OFF"),
                    "§e" + t(player, "LMB") + ": " + t(player, "ON"),
                    "§7" + ChatColor.stripColor(t(player, "STOCK_SIMULATOR.SIMULATOR_BUTTON_LORE"))
                    ));
            CreateButton(STABLE, Material.COMPARATOR, t(player, "STOCK_STABILIZING.SS"), stableLore);
        }

        // 커맨드 버튼
        boolean cmdEnabled = confSec_Options.contains("command.active") && confSec_Options.getBoolean("command.active");
        ArrayList<String> CmdToggleLore = new ArrayList<>(Arrays.asList(
                t(player, "SHOP_SETTING.COMMAND_TOGGLE_LORE"),
                "§9" + t(player, "CUR_STATE") + ": " + (cmdEnabled ? t(player, "ON") : t(player,"OFF")),
                "§e" + t(player, "CLICK") + ": " + (cmdEnabled ? t(player, "OFF") : t(player,"ON"))
        ));
        CreateButton(CMD_TOGGLE, Material.COMMAND_BLOCK, t(player, "SHOP_SETTING.COMMAND_TOGGLE"), CmdToggleLore);

        boolean sellCmdValid = confSec_Options.contains("command.sell");
        ArrayList<String> sellCmdLore = new ArrayList<>(Collections.singletonList(
                "§9" + t(player, "CUR_STATE") + ": "
        ));
        if(sellCmdValid)
        {
            if (data.get().getConfigurationSection("Options.command.sell") != null)
            {
                for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.sell").getValues(false).entrySet())
                {
                    sellCmdLore.add("§f/" + s.getValue());
                }
            }
        }
        else
        {
            sellCmdLore.add("§f(empty)");
        }
        sellCmdLore.add(t(player, "SHOP_SETTING.COMMAND_LORE1"));
        sellCmdLore.add(t(player, "SHOP_SETTING.COMMAND_LORE3"));

        boolean buyCmdValid = confSec_Options.contains("command.buy");
        ArrayList<String> buyCmdLore = new ArrayList<>(Collections.singletonList(
                "§9" + t(player, "CUR_STATE") + ": "
        ));
        if(buyCmdValid)
        {
            if (data.get().getConfigurationSection("Options.command.buy") != null)
            {
                for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.buy").getValues(false).entrySet())
                {
                    buyCmdLore.add("§f/" + s.getValue());
                }
            }
        }
        else
        {
            buyCmdLore.add("§f(empty)");
        }
        buyCmdLore.add(t(player, "SHOP_SETTING.COMMAND_LORE1"));
        buyCmdLore.add(t(player, "SHOP_SETTING.COMMAND_LORE3"));

        CreateButton(CMD_SELL, sellCmdValid ? Material.GREEN_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, t(player, "SHOP_SETTING.COMMAND_SELL"), sellCmdLore);
        CreateButton(CMD_BUY, buyCmdValid ? Material.RED_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, t(player, "SHOP_SETTING.COMMAND_BUY"), buyCmdLore);

        // 세금
        int globalTax = ConfigUtil.getCurrentTax();
        if (data.get().contains("Options.SalesTax"))
        {
            ArrayList<String> taxLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "TAX.USE_LOCAL"),
                    "§e" + t(player, "CLICK") + ": " +
                            t(player, "TAX.USE_GLOBAL").replace("{tax}", String.valueOf(globalTax))));
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, t(player, "TAX.SALES_TAX"), taxLore);

            ArrayList<String> taxLore2 = new ArrayList<>(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + data.get().getInt("Options.SalesTax") + "%",
                    t(player, "SHOP_SETTING.L_R_SHIFT")));
            CreateButton(TAX_AMOUNT, Material.IRON_INGOT, t(player, "TAX.SALES_TAX"), taxLore2, Clamp(data.get().getInt("Options.SalesTax"), 1, 64));
        } else
        {
            ArrayList<String> taxLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " +
                            t(player, "TAX.USE_GLOBAL").replace("{tax}", String.valueOf(globalTax)),
                    "§e" + t(player, "CLICK") + ": " + t(player, "TAX.USE_LOCAL")));
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, t(player, "TAX.SALES_TAX"), taxLore);
        }

        // 플래그 버튼들
        CreateFlagButton(FLAG_SIGNSHOP, confSec_Options.contains("flag.signshop"), "signShop", t(player, "SHOP_SETTING.SIGN_SHOP_LORE"));
        CreateFlagButton(FLAG_LOCALSHOP, confSec_Options.contains("flag.localshop"), "localShop", t(player, "SHOP_SETTING.LOCAL_SHOP_LORE"));
        CreateFlagButton(FLAG_DELIVERY, confSec_Options.contains("flag.deliverycharge"), "deliveryCharge", t(player, "SHOP_SETTING.DELIVERY_CHARGE_LORE"));
        CreateFlagButton(FLAG_SHOW_VALUE_CHANGE, confSec_Options.contains("flag.showvaluechange"), "showValueChange", t(player, "SHOP_SETTING.SHOW_VALUE_CHANGE_LORE"));
        CreateFlagButton(FLAG_HIDE_STOCK, confSec_Options.contains("flag.hidestock"), "hideStock", t(player, "SHOP_SETTING.HIDE_STOCK"));
        CreateFlagButton(FLAG_HIDE_PRICING_TYPE, confSec_Options.contains("flag.hidepricingtype"), "hidePricingType", t(player, "SHOP_SETTING.HIDE_PRICING_TYPE"));
        CreateFlagButton(FLAG_HIDE_SHOP_BALANCE, confSec_Options.contains("flag.hideshopbalance"), "hideShopBalance", t(player, "SHOP_SETTING.HIDE_SHOP_BALANCE"));
        CreateFlagButton(FLAG_SHOW_MAX_STOCK, confSec_Options.contains("flag.showmaxstock"), "showMaxStock", t(player, "SHOP_SETTING.SHOW_MAX_STOCK"));
        CreateFlagButton(FLAG_HIDDEN_IN_COMMAND, confSec_Options.contains("flag.hiddenincommand"), "hiddenInCommand", t(player, "SHOP_SETTING.HIDDEN_IN_COMMAND"));
        CreateFlagButton(FLAG_INT_ONLY, confSec_Options.contains("flag.integeronly"), "integerOnly", t(player, "SHOP_SETTING.INTEGER_ONLY"));

        currency = confSec_Options.getString("currency", "");
        if (!currency.equalsIgnoreCase(Constants.S_JOBPOINT) &&
            !currency.equalsIgnoreCase(Constants.S_PLAYERPOINT) &&
            !currency.equalsIgnoreCase(Constants.S_EXP))
            currency = Constants.S_VAULT;

        CreateCurrencyButton(CURRENCY_VAULT, currency.equalsIgnoreCase(Constants.S_VAULT), t(player, "SHOP_SETTING.CURRENCY") + "Vault", t(player, "SHOP_SETTING.VAULT_LORE"));
        CreateCurrencyButton(CURRENCY_EXP, currency.equalsIgnoreCase(Constants.S_EXP), t(player, "SHOP_SETTING.CURRENCY") + "Exp", t(player, "SHOP_SETTING.EXP_LORE"));
        CreateCurrencyButton(CURRENCY_JP, currency.equalsIgnoreCase(Constants.S_JOBPOINT), t(player, "SHOP_SETTING.CURRENCY") + "JobPoint", t(player, "SHOP_SETTING.JOB_POINT_LORE"));
        CreateCurrencyButton(CURRENCY_PP, currency.equalsIgnoreCase(Constants.S_PLAYERPOINT), t(player, "SHOP_SETTING.CURRENCY") + "PlayerPoint", t(player, "SHOP_SETTING.PLAYER_POINT_LORE"));

        // 로그 버튼
        String log_cur;
        String log_set;
        if (confSec_Options.contains("log.active") && confSec_Options.getBoolean("log.active"))
        {
            log_cur = t(player, "ON");
            log_set = t(player, "OFF");
        } else
        {
            log_cur = t(player, "OFF");
            log_set = t(player, "ON");
        }
        ArrayList<String> logLore = new ArrayList<>();
        logLore.add("§9" + t(player, "CUR_STATE") + ": " + log_cur);
        logLore.add("§e" + t(player, "LMB") + ": " + log_set);
        logLore.add(t(player, "§7" + ChatColor.stripColor(t(player, "SHOP_SETTING.LOG_TOGGLE_LORE"))));
        CreateButton(LOG_TOGGLE, Material.BOOK, t(player, "LOG.LOG"), logLore);

        boolean printToConsoleActive = data.get().contains("Options.log.printToConsole") && data.get().getBoolean("Options.log.printToConsole");
        ArrayList<String> logLore_2 = new ArrayList<>();
        logLore_2.add("§9" + t(player, "CUR_STATE") + ": " + (printToConsoleActive ? t(player, "ON") : t(player, "OFF")));
        logLore_2.add("§e" + t(player, "LMB") + ": " + (printToConsoleActive ? t(player, "OFF") : t(player, "ON")));
        CreateButton(LOG_PRINT_CONSOLE, printToConsoleActive ? Material.GREEN_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, t(player, "SHOP_SETTING.LOG_PRINT_CONSOLE"), logLore_2);

        boolean printToAdminActive = data.get().contains("Options.log.printToAdmin") && data.get().getBoolean("Options.log.printToAdmin");
        ArrayList<String> logLore_3 = new ArrayList<>();
        logLore_3.add("§9" + t(player, "CUR_STATE") + ": " + (printToAdminActive ? t(player, "ON") : t(player, "OFF")));
        logLore_3.add("§e" + t(player, "LMB") + ": " + (printToAdminActive ? t(player, "OFF") : t(player, "ON")));
        CreateButton(LOG_PRINT_ADMIN, printToAdminActive ? Material.GREEN_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, t(player, "SHOP_SETTING.LOG_PRINT_ADMIN"), logLore_3);

        // 거래 UI 설정
        ArrayList<String> tradeUILore = new ArrayList<>();
        tradeUILore.add("§9" + t(null, "CUR_STATE") + ": " + (confSec_Options.contains("tradeUI") ? confSec_Options.get("tradeUI") : t(player,"NULL")));
        tradeUILore.add(t(player, "SHOP_SETTING.TRADE_UI_LORE_2"));
        CreateButton(TRADE_UI_SETTING, Material.EMERALD, t(player, "SHOP_SETTING.TRADE_UI"), tradeUILore);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        // 닫기버튼
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 활성화
        if (e.getSlot() == ENABLE_TOGGLE)
        {
            if (data.get().getBoolean("Options.enable", true))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " enable false");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " enable true");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 권한
        else if (e.getSlot() == PERMISSION)
        {
            if (data.get().getString("Options.permission").isEmpty())
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " permission true");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " permission false");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 최대 페이지
        else if (e.getSlot() == MAX_PAGE)
        {
            int oldvalue = data.get().getInt("Options.page");
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
        // 배경 색상 변경
        else if (e.getSlot() == BACKGROUND)
        {
            DynamicShop.PaidOnlyMsg(player);
        }
        // 로테이션 편집기
        else if (e.getSlot() == ROTATION_EDITOR)
        {
            DynaShopAPI.OpenRotationEditor(player, shopName);
        }
        // 영업시간
        else if (e.getSlot() == SHOP_HOUR || e.getSlot() == SHOP_HOUR_OPEN || e.getSlot() == SHOP_HOUR_CLOSE)
        {
            if (data.get().contains("Options.shophours"))
            {
                String[] shopHour = data.get().getString("Options.shophours").split("~");
                Integer open = Integer.parseInt(shopHour[0]);
                int close = Integer.parseInt(shopHour[1]);
                int edit = -1;
                if (e.isRightClick()) edit = 1;
                if (e.isShiftClick()) edit *= 5;

                if (e.getSlot() == SHOP_HOUR)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours 0 0");
                } else if (e.getSlot() == SHOP_HOUR_OPEN)
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

                    open = Clamp(open, 1, 24);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours " + open + " " + close);
                } else if (e.getSlot() == SHOP_HOUR_CLOSE)
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

                    close = Clamp(close, 1, 24);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours " + open + " " + close);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == SHOP_HOUR)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours 20 6");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 랜덤스톡
        else if (e.getSlot() == FLUC || e.getSlot() == FLUC_INTERVAL || e.getSlot() == FLUC_STRENGTH)
        {
            if (data.get().contains("Options.fluctuation"))
            {
                int interval = data.get().getInt("Options.fluctuation.interval");
                double strength = data.get().getDouble("Options.fluctuation.strength");

                if (e.getSlot() == FLUC)
                {
                    if(e.isLeftClick())
                    {
                        Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation off");
                    }
                    else if(e.isRightClick())
                    {
                        DynaShopAPI.openStockSimulator(player, shopName);
                        return;
                    }
                } else if (e.getSlot() == FLUC_INTERVAL)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    interval += edit;
                    interval = Clamp(interval, 1, 999);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation " + interval + " " + strength);
                } else if (e.getSlot() == FLUC_STRENGTH)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    strength += edit;
                    strength = Clamp(strength, 0.1, 64);
                    strength = Math.round(strength * 100) / 100.0;

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation " + interval + " " + strength);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == FLUC)
                {
                    if(e.isLeftClick())
                    {
                        Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation 48 0.1");
                        DynaShopAPI.openShopSettingGui(player, shopName);
                    }
                    else if(e.isRightClick())
                    {
                        DynaShopAPI.openStockSimulator(player, shopName);
                    }
                }
            }
        }
        // 스톡 안정화
        else if (e.getSlot() == STABLE || e.getSlot() == STABLE_INTERVAL || e.getSlot() == STABLE_STRENGTH)
        {
            if (data.get().contains("Options.stockStabilizing"))
            {
                int interval = data.get().getInt("Options.stockStabilizing.interval");
                double strength = data.get().getDouble("Options.stockStabilizing.strength");

                if (e.getSlot() == STABLE)
                {
                    if(e.isLeftClick())
                    {
                        Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing off");
                    }
                    else if(e.isRightClick())
                    {
                        DynaShopAPI.openStockSimulator(player, shopName);
                        return;
                    }
                } else if (e.getSlot() == STABLE_INTERVAL)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    interval += edit;
                    interval = Clamp(interval, 1, 999);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing " + interval + " " + strength);
                } else if (e.getSlot() == STABLE_STRENGTH)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    strength += edit;
                    strength = Clamp(strength, 0.1, 25);
                    strength = (Math.round(strength * 100) / 100.0);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing " + interval + " " + strength);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == STABLE)
                {
                    if(e.isLeftClick())
                    {
                        Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing 48 0.5");
                        DynaShopAPI.openShopSettingGui(player, shopName);
                    }
                    else if(e.isRightClick())
                    {
                        DynaShopAPI.openStockSimulator(player, shopName);
                    }
                }
            }
        }
        // 세금
        else if (e.getSlot() == TAX_TOGGLE || e.getSlot() == TAX_AMOUNT)
        {
            // 전역,지역 토글
            if (e.getSlot() == TAX_TOGGLE)
            {
                if (data.get().contains("Options.SalesTax"))
                {
                    data.get().set("Options.SalesTax", null);
                } else
                {
                    data.get().set("Options.SalesTax", ConfigUtil.GetSalesTax());
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            // 수치설정
            else if (data.get().contains("Options.SalesTax"))
            {
                int edit = -1;
                if (e.isRightClick()) edit = 1;
                if (e.isShiftClick()) edit *= 5;

                int result = Clamp(data.get().getInt("Options.SalesTax") + edit, 0, 99);
                data.get().set("Options.SalesTax", result);

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            data.save();
        }
        // signshop
        else if (e.getSlot() == FLAG_SIGNSHOP)
        {
            if (data.get().contains("Options.flag.signshop"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag signShop unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag signShop set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // localshop
        else if (e.getSlot() == FLAG_LOCALSHOP)
        {
            if (data.get().contains("Options.flag.localshop"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag localShop unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag localShop set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // deliverycharge
        else if (e.getSlot() == FLAG_DELIVERY)
        {
            if (data.get().contains("Options.flag.deliverycharge"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag deliveryCharge unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag deliveryCharge set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // showValueChange
        else if (e.getSlot() == FLAG_SHOW_VALUE_CHANGE)
        {
            if (data.get().contains("Options.flag.showvaluechange"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showValueChange unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showValueChange set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDE_STOCK
        else if (e.getSlot() == FLAG_HIDE_STOCK)
        {
            if (data.get().contains("Options.flag.hidestock"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideStock unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideStock set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDE_PRICING_TYPE
        else if (e.getSlot() == FLAG_HIDE_PRICING_TYPE)
        {
            if (data.get().contains("Options.flag.hidepricingtype"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hidePricingType unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hidePricingType set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDE_SHOP_BALANCE
        else if (e.getSlot() == FLAG_HIDE_SHOP_BALANCE)
        {
            if (data.get().contains("Options.flag.hideshopbalance"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideShopBalance unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideShopBalance set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // SHOW_MAX_STOCK
        else if (e.getSlot() == FLAG_SHOW_MAX_STOCK)
        {
            if (data.get().contains("Options.flag.showmaxstock"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showMaxStock unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showMaxStock set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDDEN_IN_COMMAND
        else if (e.getSlot() == FLAG_HIDDEN_IN_COMMAND)
        {
            if (data.get().contains("Options.flag.hiddenincommand"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hiddenInCommand unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hiddenInCommand set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // log
        else if (e.getSlot() == LOG_TOGGLE)
        {
            if (e.isLeftClick())
            {
                if (data.get().contains("Options.log.active") && data.get().getBoolean("Options.log.active"))
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log disable");
                } else
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log enable");
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else if (e.isRightClick())
            {
                DynaShopAPI.openLogViewer(player, shopName);
            }
        } else if (e.getSlot() == LOG_PRINT_CONSOLE)
        {
            boolean active = data.get().contains("Options.log.printToConsole") && data.get().getBoolean("Options.log.printToConsole");

            Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log printToConsole " + (active ? "off" : "on"));

            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        else if (e.getSlot() == LOG_PRINT_ADMIN)
        {
            boolean active = data.get().contains("Options.log.printToAdmin") && data.get().getBoolean("Options.log.printToAdmin");

            Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log printToAdmin " + (active ? "off" : "on"));

            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 명령어
        else if (e.getSlot() == CMD_TOGGLE)
        {
            boolean active = data.get().contains("Options.command.active") && data.get().getBoolean("Options.command.active");

            Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " command active " + (active ? "false" : "true"));

            DynaShopAPI.openShopSettingGui(player, shopName);
        } else if (e.getSlot() == CMD_SELL)
        {
            if (e.isShiftClick() && e.isRightClick())
            {
                if (data.get().getConfigurationSection("Options.command.sell") != null)
                {
                    Object[] indexList = data.get().getConfigurationSection("Options.command.sell").getKeys(false).toArray();
                    int idx = Integer.parseInt(indexList[indexList.length-1].toString());
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " command sell delete " + idx);

                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
            else if(e.isLeftClick())
            {
                player.closeInventory();
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ENTER_COMMAND_2"));

                UserUtil.userTempData.put(player.getUniqueId(), "sellCmd");
                Command.PrintCurrentState(player, shopName, true, false);
                OnChat.WaitForInput(player);
            }
        } else if (e.getSlot() == CMD_BUY)
        {
            if (e.isShiftClick() && e.isRightClick())
            {
                if (data.get().getConfigurationSection("Options.command.buy") != null)
                {
                    Object[] indexList = data.get().getConfigurationSection("Options.command.buy").getKeys(false).toArray();
                    int idx = Integer.parseInt(indexList[indexList.length-1].toString());
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " command buy delete " + idx);

                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
            else if(e.isLeftClick())
            {
                player.closeInventory();
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ENTER_COMMAND_2"));

                UserUtil.userTempData.put(player.getUniqueId(), "buyCmd");
                Command.PrintCurrentState(player, shopName, false, true);
                OnChat.WaitForInput(player);
            }
        }
        // INTEGER_ONLY
        else if (e.getSlot() == FLAG_INT_ONLY)
        {
            if (data.get().contains("Options.flag.integeronly"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag integerOnly unset");

                if(ShopUtil.GetCurrency(data).equalsIgnoreCase(Constants.S_PLAYERPOINT) ||
                   ShopUtil.GetCurrency(data).equalsIgnoreCase(Constants.S_EXP))
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " currency " + Constants.S_VAULT);
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag integerOnly set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // vault
        else if (e.getSlot() == CURRENCY_VAULT)
        {
            if (!currency.equalsIgnoreCase(Constants.S_VAULT))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " currency " + Constants.S_VAULT);
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // exp
        else if (e.getSlot() == CURRENCY_EXP)
        {
            if (!currency.equalsIgnoreCase(Constants.S_EXP))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " currency " + Constants.S_EXP);
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag integerOnly set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // jobpoint
        else if (e.getSlot() == CURRENCY_JP)
        {
            if (!JobsHook.jobsRebornActive)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.JOBS_REBORN_NOT_FOUND"));
                return;
            }

            if (!currency.equalsIgnoreCase(Constants.S_JOBPOINT))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " currency " + Constants.S_JOBPOINT);
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // Player point
        else if (e.getSlot() == CURRENCY_PP)
        {
            if (!PlayerpointHook.isPPActive)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.PLAYER_POINTS_NOT_FOUND"));
                return;
            }

            if (!currency.equalsIgnoreCase(Constants.S_PLAYERPOINT))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " currency " + Constants.S_PLAYERPOINT);
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag integerOnly set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        else if (e.getSlot() == TRADE_UI_SETTING)
        {
            if (e.isLeftClick())
            {
                player.closeInventory();
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "TRADE.WAIT_FOR_INPUT"));

                UserUtil.userInteractItem.put(player.getUniqueId(), shopName + "/-1");
                UserUtil.userTempData.put(player.getUniqueId(), "waitForTradeUI");

                OnChat.WaitForInput(player);
            }
            if (e.isRightClick())
            {
                data.get().set("Options.tradeUI", null);
                data.save();

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
        }
    }

    private void CreateFlagButton(int buttonPosition, boolean isEnable, String title, String lore)
    {
        String current;
        String set;
        Material icon;
        if (isEnable)
        {
            icon = Material.GREEN_STAINED_GLASS_PANE;
            current = t(null, "SET");
            set = t(null, "UNSET");
        } else
        {
            icon = Material.BLACK_STAINED_GLASS_PANE;
            current = t(null, "UNSET");
            set = t(null, "SET");
        }

        ArrayList<String> loreArray = new ArrayList<>();
        if(lore.contains("\n"))
        {
            String[] temp = lore.split("\n");
            Collections.addAll(loreArray, temp);
        }
        else
        {
            loreArray.add(lore);
        }
        loreArray.add("§9" + t(null, "CUR_STATE") + ": " + current);
        loreArray.add("§e" + t(null, "CLICK") + ": " + set);
        CreateButton(buttonPosition, icon, t(null, "SHOP_SETTING.FLAG") + ": " + title, loreArray);
    }

    private void CreateCurrencyButton(int buttonPosition, boolean isEnable, String title, String lore)
    {
        ArrayList<String> loreArray = new ArrayList<>();
        if(lore.contains("\n"))
        {
            String[] temp = lore.split("\n");
            Collections.addAll(loreArray, temp);
        }
        else
        {
            loreArray.add(lore);
        }

        Material icon;
        if (isEnable)
        {
            icon = Material.YELLOW_STAINED_GLASS_PANE;
            loreArray.add(t(null, "SHOP_SETTING.SELECTED"));

        } else
        {
            icon = Material.WHITE_STAINED_GLASS_PANE;
        }
        loreArray.add("§e" + t(null, "CLICK") + ": " + t(null, "SET"));
        CreateButton(buttonPosition, icon, title, loreArray);
    }
}
