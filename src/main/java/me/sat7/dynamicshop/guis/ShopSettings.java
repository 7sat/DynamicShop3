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
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ShopSettings extends InGameUI
{
    public ShopSettings()
    {
        uiType = UI_TYPE.ShopSettings;
    }

    private final int CLOSE = 27;
    private final int PERMISSION = 0;
    private final int MAX_PAGE = 1;
    private final int SHOP_HOUR = 6;
    private final int SHOP_HOUR_OPEN = 7;
    private final int SHOP_HOUR_CLOSE = 8;
    private final int FLUC = 15;
    private final int FLUC_INTERVAL = 16;
    private final int FLUC_STRENGTH = 17;
    private final int STABLE = 24;
    private final int STABLE_INTERVAL = 25;
    private final int STABLE_STRENGTH = 26;
    private final int TAX_TOGGLE = 33;
    private final int TAX_AMOUNT = 34;
    private final int FLAG1 = 9;
    private final int FLAG2 = 10;
    private final int FLAG3 = 11;
    private final int FLAG4 = 12;
    private final int FLAG5 = 13;
    private final int LOG_TOGGLE = 30;
    private final int LOG_DELETE = 31;

    public Inventory getGui(Player player, String shopName)
    {
        inventory = Bukkit.createInventory(player, 36, t("SHOP_SETTING_TITLE"));

        // 닫기 버튼
        CreateCloseButton(CLOSE);

        // 권한 버튼
        ConfigurationSection confSec_Options = ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options");
        String permStr = confSec_Options.getString("permission");
        String permNew = "dshop.user.shop." + shopName;
        Material permIcon;
        if (permStr == null || permStr.isEmpty())
        {
            permStr = t("NULL(OPEN)");
            permIcon = Material.IRON_BLOCK;
        } else
        {
            permNew = t("NULL(OPEN)");
            permIcon = Material.GOLD_BLOCK;
        }

        ArrayList<String> permLore = new ArrayList<>();
        permLore.add("§9" + t("CUR_STATE") + ": " + permStr);
        if (!permStr.equalsIgnoreCase(t("NULL(OPEN)")))
        {
            permLore.add("§9 - " + permStr + ".buy");
            permLore.add("§9 - " + permStr + ".sell");
        }
        permLore.add("§e" + t("CLICK") + ": " + permNew);
        CreateButton(PERMISSION, permIcon, t("PERMISSION"), permLore);

        //최대 페이지 버튼
        CreateButton(MAX_PAGE, Material.PAPER, t("MAXPAGE"), new ArrayList<>(Arrays.asList(t("MAXPAGE_LORE"), t("L_R_SHIFT"))), ShopUtil.ccShop.get().getInt(shopName + ".Options.page"));

        // 영업시간 버튼
        int curTime = (int) (player.getWorld().getTime()) / 1000 + 6;
        if (curTime > 24) curTime -= 24;
        if (ShopUtil.ccShop.get().contains(shopName + ".Options.shophours"))
        {
            String[] temp = ShopUtil.ccShop.get().getString(shopName + ".Options.shophours").split("~");
            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            ArrayList<String> shopHourLore = new ArrayList<>(Arrays.asList(
                    t("TIME.CUR").replace("{time}", curTime + ""),
                    "§9" + t("CUR_STATE") + ": ",
                    "§9 - " + t("TIME.OPEN") + ": " + open,
                    "§9 - " + t("TIME.CLOSE") + ": " + close,
                    "§e" + t("CLICK") + ": " + t("TIME.OPEN24")));
            CreateButton(SHOP_HOUR, Material.CLOCK, t("TIME.SHOPHOURS"), shopHourLore);
            CreateButton(SHOP_HOUR_OPEN, Material.CLOCK, "§f" + t("TIME.OPEN"), new ArrayList<>(Arrays.asList(t("TIME.OPEN_LORE"), t("L_R_SHIFT"))), open);
            CreateButton(SHOP_HOUR_CLOSE, Material.CLOCK, "§f" + t("TIME.CLOSE"), new ArrayList<>(Arrays.asList(t("TIME.CLOSE_LORE"), t("L_R_SHIFT"))), close);
        } else
        {
            ArrayList<String> shopHourLore = new ArrayList<>(Arrays.asList(
                            t("TIME.CUR").replace("{time}", curTime + ""),
                            "§9" + t("CUR_STATE") + ": " + t("TIME.OPEN24"),
                            "§e" + t("CLICK") + ": " + t("TIME.SET_SHOPHOURS")));
            CreateButton(SHOP_HOUR, Material.CLOCK, t("TIME.SHOPHOURS"), shopHourLore);
        }

        // 랜덤스톡 버튼
        ConfigurationSection flucConf = ShopUtil.ccShop.get().getConfigurationSection(shopName + ".Options.fluctuation");
        if (flucConf != null)
        {
            ArrayList<String> fluctuationLore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " + t("ON"),
                    "§e" + t("CLICK") + ": " + t("OFF")));
            CreateButton(FLUC, Material.COMPARATOR, t("FLUC.FLUCTUATION"), fluctuationLore);

            ArrayList<String> fluctuation_interval_lore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " + flucConf.getInt("interval") / 2.0 + "h",
                    "§e" + t("CLICK") + ": " + t("L_R_SHIFT")));
            CreateButton(FLUC_INTERVAL, Material.COMPARATOR, t("FLUC.INTERVAL"), fluctuation_interval_lore, Clamp(flucConf.getInt("interval") / 2, 1, 64));

            ArrayList<String> fluctuation_strength_lore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": ~" + flucConf.get("strength") + "%",
                    "§e" + t("CLICK") + ": " + t("STOCKSTABILIZING.L_R_SHIFT")));
            CreateButton(FLUC_STRENGTH, Material.COMPARATOR, t("FLUC.STRENGTH"), fluctuation_strength_lore, Clamp((int) (flucConf.getDouble("strength") * 10), 1, 64));
        } else
        {
            ItemStack flucToggleBtn = ItemsUtil.createItemStack(Material.COMPARATOR, null,
                    t("FLUC.FLUCTUATION"),
                    new ArrayList<>(Arrays.asList(
                            "§9" + t("CUR_STATE") + ": " + t("OFF"),
                            "§e" + t("CLICK") + ": " + t("ON"))),
                    1);
            inventory.setItem(FLUC, flucToggleBtn);
        }

        // 재고 안정화 버튼
        ConfigurationSection stockStableConf = ShopUtil.ccShop.get().getConfigurationSection(shopName + ".Options.stockStabilizing");
        if (stockStableConf != null)
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " + t("ON"),
                    "§e" + t("CLICK") + ": " + t("OFF")));
            CreateButton(STABLE, Material.COMPARATOR, t("STOCKSTABILIZING.SS"), stableLore);

            ArrayList<String> stable_interval_Lore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " + stockStableConf.getInt("interval") / 2.0 + "h",
                    "§e" + t("CLICK") + ": " + t("L_R_SHIFT")));
            CreateButton(STABLE_INTERVAL, Material.COMPARATOR, t("FLUC.INTERVAL"), stable_interval_Lore, Clamp(stockStableConf.getInt("interval") / 2, 1, 64));

            ArrayList<String> stable_strength_Lore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": ~" + stockStableConf.get("strength") + "%",
                    "§e" + t("CLICK") + ": " + t("STOCKSTABILIZING.L_R_SHIFT")));
            CreateButton(STABLE_STRENGTH, Material.COMPARATOR, t("FLUC.STRENGTH"), stable_strength_Lore, Clamp((int) (stockStableConf.getDouble("strength") * 10), 1, 64));
        } else
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " + t("OFF"),
                    "§e" + t("CLICK") + ": " + t("ON")));
            CreateButton(STABLE, Material.COMPARATOR, t("STOCKSTABILIZING.SS"), stableLore);
        }

        // 세금
        int globalTax = ConfigUtil.getCurrentTax();
        if (ShopUtil.ccShop.get().contains(shopName + ".Options.SalesTax"))
        {
            ArrayList<String> taxLore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " + t("TAX.USE_LOCAL"),
                    "§e" + t("CLICK") + ": " +
                            t("TAX.USE_GLOBAL").replace("{tax}", globalTax + "")));
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, t("TAX.SALESTAX"), taxLore);

            ArrayList<String> taxLore2 = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " + ShopUtil.ccShop.get().getInt(shopName + ".Options.SalesTax") + "%",
                    t("L_R_SHIFT")));
            CreateButton(TAX_AMOUNT, Material.IRON_INGOT, t("TAX.SALESTAX"), taxLore2, Clamp(ShopUtil.ccShop.get().getInt(shopName + ".Options.SalesTax"), 1, 64));
        } else
        {
            ArrayList<String> taxLore = new ArrayList<>(Arrays.asList(
                    "§9" + t("CUR_STATE") + ": " +
                            t("TAX.USE_GLOBAL").replace("{tax}", globalTax + ""),
                    "§e" + t("CLICK") + ": " + t("TAX.USE_LOCAL")));
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, t("TAX.SALESTAX"), taxLore);
        }

        // 플래그 버튼들
        String cur1;
        String set1;
        Material icon1;
        if (confSec_Options.contains("flag.signshop"))
        {
            icon1 = Material.GREEN_STAINED_GLASS_PANE;
            cur1 = t("SET");
            set1 = t("UNSET");
        } else
        {
            icon1 = Material.BLACK_STAINED_GLASS_PANE;
            cur1 = t("UNSET");
            set1 = t("SET");
        }
        ArrayList<String> f1Lore = new ArrayList<>();
        f1Lore.add(t("SIGNSHOP_LORE"));
        f1Lore.add("§9" + t("CUR_STATE") + ": " + cur1);
        f1Lore.add("§e" + t("CLICK") + ": " + set1);
        CreateButton(FLAG1, icon1, t("FLAG") + ": signshop", f1Lore);

        String cur2;
        String set2;
        Material icon2;
        if (confSec_Options.contains("flag.localshop"))
        {
            icon2 = Material.GREEN_STAINED_GLASS_PANE;
            cur2 = t("SET");
            set2 = t("UNSET");
        } else
        {
            icon2 = Material.BLACK_STAINED_GLASS_PANE;
            cur2 = t("UNSET");
            set2 = t("SET");
        }
        ArrayList<String> f2Lore = new ArrayList<>();
        f2Lore.add(t("LOCALSHOP_LORE"));
        f2Lore.add(t("LOCALSHOP_LORE2"));
        f2Lore.add("§9" + t("CUR_STATE") + ": " + cur2);
        f2Lore.add("§e" + t("CLICK") + ": " + set2);
        CreateButton(FLAG2, icon2, t("FLAG") + ": localshop", f2Lore);

        String cur3;
        String set3;
        Material icon3;
        if (confSec_Options.contains("flag.deliverycharge"))
        {
            icon3 = Material.GREEN_STAINED_GLASS_PANE;
            cur3 = t("SET");
            set3 = t("UNSET");
        } else
        {
            icon3 = Material.BLACK_STAINED_GLASS_PANE;
            cur3 = t("UNSET");
            set3 = t("SET");
        }
        ArrayList<String> f3Lore = new ArrayList<>();
        f3Lore.add(t("DELIVERYCHARG_LORE"));
        f3Lore.add("§9" + t("CUR_STATE") + ": " + cur3);
        f3Lore.add("§e" + t("CLICK") + ": " + set3);
        CreateButton(FLAG3, icon3, t("FLAG") + ": deliverycharge", f3Lore);

        String cur4;
        String set4;
        Material icon4;
        if (confSec_Options.contains("flag.jobpoint"))
        {
            icon4 = Material.GREEN_STAINED_GLASS_PANE;
            cur4 = t("SET");
            set4 = t("UNSET");
        } else
        {
            icon4 = Material.BLACK_STAINED_GLASS_PANE;
            cur4 = t("UNSET");
            set4 = t("SET");
        }
        ArrayList<String> f4Lore = new ArrayList<>();
        f4Lore.add(t("JOBPOINT_LORE"));
        f4Lore.add("§9" + t("CUR_STATE") + ": " + cur4);
        f4Lore.add("§e" + t("CLICK") + ": " + set4);
        CreateButton(FLAG4, icon4, t("FLAG") + ": jobpoint", f4Lore);

        String cur5;
        String set5;
        Material icon5;
        if (confSec_Options.contains("flag.showvaluechange"))
        {
            icon5 = Material.GREEN_STAINED_GLASS_PANE;
            cur5 = t("SET");
            set5 = t("UNSET");
        } else
        {
            icon5 = Material.BLACK_STAINED_GLASS_PANE;
            cur5 = t("UNSET");
            set5 = t("SET");
        }
        ArrayList<String> f5Lore = new ArrayList<>();
        f5Lore.add(t("SHOW_VALUE_CHANGE_LORE"));
        f5Lore.add("§9" + t("CUR_STATE") + ": " + cur5);
        f5Lore.add("§e" + t("CLICK") + ": " + set5);
        CreateButton(FLAG5, icon5, t("FLAG") + ": showvaluechange", f5Lore);

        // 로그 버튼
        String log_cur;
        String log_set;
        if (confSec_Options.contains("log"))
        {
            log_cur = t("ON");
            log_set = t("OFF");
        } else
        {
            log_cur = t("OFF");
            log_set = t("ON");
        }
        ArrayList<String> logLore = new ArrayList<>();
        logLore.add("§9" + t("CUR_STATE") + ": " + log_cur);
        logLore.add("§e" + t("CLICK") + ": " + log_set);
        CreateButton(LOG_TOGGLE, Material.BOOK, t("LOG.LOG"), logLore);
        CreateButton(LOG_DELETE, Material.RED_STAINED_GLASS_PANE, t("LOG.DELETE"), "");
        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        String shopName = temp[0];

        // 닫기버튼
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openShopGui(player, temp[0], 1);
            DynamicShop.userInteractItem.put(player.getUniqueId(), "");
        }
        // 권한
        else if (e.getSlot() == PERMISSION)
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
        else if (e.getSlot() == MAX_PAGE)
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
        else if (e.getSlot() == SHOP_HOUR || e.getSlot() == SHOP_HOUR_OPEN || e.getSlot() == SHOP_HOUR_CLOSE)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.shophours"))
            {
                String[] shopHour = ShopUtil.ccShop.get().getString(shopName + ".Options.shophours").split("~");
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
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.fluctuation"))
            {
                int interval = ShopUtil.ccShop.get().getInt(shopName + ".Options.fluctuation.interval");
                double strength = ShopUtil.ccShop.get().getDouble(shopName + ".Options.fluctuation.strength");

                if (e.getSlot() == FLUC)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation off");
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
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation 12 0.1");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 스톡 안정화
        else if (e.getSlot() == STABLE || e.getSlot() == STABLE_INTERVAL || e.getSlot() == STABLE_STRENGTH)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.stockStabilizing"))
            {
                int interval = ShopUtil.ccShop.get().getInt(shopName + ".Options.stockStabilizing.interval");
                double strength = ShopUtil.ccShop.get().getDouble(shopName + ".Options.stockStabilizing.strength");

                if (e.getSlot() == STABLE)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing off");
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
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing 24 0.1");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 세금
        else if (e.getSlot() == TAX_TOGGLE || e.getSlot() == TAX_AMOUNT)
        {
            // 전역,지역 토글
            if (e.getSlot() == TAX_TOGGLE)
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

                int result = Clamp(ShopUtil.ccShop.get().getInt(shopName + ".Options.SalesTax") + edit, 0, 99);
                ShopUtil.ccShop.get().set(shopName + ".Options.SalesTax", result);

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            ShopUtil.ccShop.save();
        }
        // signshop
        else if (e.getSlot() == FLAG1)
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
        else if (e.getSlot() == FLAG2)
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
        else if (e.getSlot() == FLAG3)
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
        else if (e.getSlot() == FLAG4)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag jobpoint unset");
            } else
            {
                if (!JobsHook.jobsRebornActive)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.JOBSREBORN_NOT_FOUND"));
                    return;
                }

                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag jobpoint set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // showValueChange
        else if (e.getSlot() == FLAG5)
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
        else if (e.getSlot() == LOG_TOGGLE)
        {
            if (ShopUtil.ccShop.get().contains(shopName + ".Options.log") && ShopUtil.ccShop.get().getBoolean(shopName + ".Options.log"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log disable");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log enable");
            }

            DynaShopAPI.openShopSettingGui(player, shopName);
        } else if (e.getSlot() == LOG_DELETE)
        {
            Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log clear");

            DynaShopAPI.openShopSettingGui(player, shopName);
        }
    }
}
