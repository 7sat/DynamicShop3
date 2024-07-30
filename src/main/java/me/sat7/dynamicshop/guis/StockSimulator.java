package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public class StockSimulator extends InGameUI
{
    public StockSimulator()
    {
        uiType = UI_TYPE.StockSimulator;
    }

    private final int ITEM_1 = 0;
    private final int ITEM_2 = 9;
    private final int ITEM_3 = 18;
    private final int ITEM_4 = 27;
    private final int ITEM_5 = 36;

    private final int CLOSE = 45;

    private final int FLUC = 47;
    private final int FLUC_INTERVAL = 48;
    private final int FLUC_STRENGTH = 49;
    private final int STABLE = 50;
    private final int STABLE_INTERVAL = 51;
    private final int STABLE_STRENGTH = 52;

    private final int RUN = 53;

    private String shopName;
    private int shopItemCount;
    private int sampleStartIdx;

    private boolean fluc;
    private int flucInterval;
    private double flucStrength;
    private boolean stable;
    private int stableInterval;
    private double stableStrength;

    public static class SimulData
    {
        public String mat;
        public int stock;
        public int median;
        public String tradeType;
        public double value;
        public double valueMin;
        public double valueMax;
    }

    final ArrayList<SimulData> tempDatas = new ArrayList<>();

    public Inventory getGui(Player player, String shopName)
    {
        inventory = Bukkit.createInventory(player, 54, t(player, "STOCK_SIMULATOR_TITLE") + "§7 | §8" + shopName);

        this.shopName = shopName;
        this.shopItemCount = ShopUtil.GetShopItemCount(shopName, true, true);

        for (int i = 0; i < 6; i++)
        {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(" ");
            itemStack.setItemMeta(meta);
            inventory.setItem((i * 9) + 1, itemStack);
        }

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        fluc = data.get().contains("Options.fluctuation");
        if (fluc)
        {
            flucInterval = data.get().getInt("Options.fluctuation.interval");
            flucStrength = data.get().getDouble("Options.fluctuation.strength");
        }
        stable = data.get().contains("Options.stockStabilizing");
        if (stable)
        {
            stableInterval = data.get().getInt("Options.stockStabilizing.interval");
            stableStrength = data.get().getDouble("Options.stockStabilizing.strength");
        }

        CreateSampleDatas(0);
        UpdateBottomButtons();
        CreateCloseButton(player, CLOSE);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        // 닫기 버튼
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 샘플 아이템 변경
        else if (e.getSlot() == ITEM_1 || e.getSlot() == ITEM_2 || e.getSlot() == ITEM_3 || e.getSlot() == ITEM_4 || e.getSlot() == ITEM_5)
        {
            if (e.isLeftClick())
                sampleStartIdx -= 5;
            else if (e.isRightClick())
                sampleStartIdx += 5;

            sampleStartIdx = Clamp(sampleStartIdx, 0, shopItemCount - 5);
            CreateSampleDatas(sampleStartIdx);
        }
        // 무작위 재고
        else if (e.getSlot() == FLUC || e.getSlot() == FLUC_INTERVAL || e.getSlot() == FLUC_STRENGTH)
        {
            if (fluc)
            {
                if (e.getSlot() == FLUC)
                {
                    fluc = false;
                } else if (e.getSlot() == FLUC_INTERVAL)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    flucInterval += edit;
                    flucInterval = Clamp(flucInterval, 1, 999);
                } else if (e.getSlot() == FLUC_STRENGTH)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    flucStrength += edit;
                    flucStrength = Clamp(flucStrength, 0.1, 64);
                    flucStrength = Math.round(flucStrength * 100) / 100.0;
                }
            } else
            {
                if (e.getSlot() == FLUC)
                {
                    fluc = true;
                    flucInterval = 48;
                    flucStrength = 0.1;
                }
            }

            UpdateBottomButtons();
        }
        // 스톡 안정화
        else if (e.getSlot() == STABLE || e.getSlot() == STABLE_INTERVAL || e.getSlot() == STABLE_STRENGTH)
        {
            if (stable)
            {
                if (e.getSlot() == STABLE)
                {
                    stable = false;
                } else if (e.getSlot() == STABLE_INTERVAL)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    stableInterval += edit;
                    stableInterval = Clamp(stableInterval, 1, 999);
                } else if (e.getSlot() == STABLE_STRENGTH)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    stableStrength += edit;
                    stableStrength = Clamp(stableStrength, 0.1, 25);
                    stableStrength = (Math.round(stableStrength * 100) / 100.0);
                }
            } else
            {
                if (e.getSlot() == STABLE)
                {
                    stable = true;
                    stableInterval = 48;
                    stableStrength = 0.5;
                }
            }

            UpdateBottomButtons();
        }
        // 시뮬레이션 실행
        else if (e.getSlot() == RUN)
        {
            if (e.isLeftClick())
            {
                Bukkit.getScheduler().runTaskAsynchronously(DynamicShop.plugin, this::RunSimulation);
            } else if (e.isRightClick())
            {
                ApplySettings();
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CHANGES_APPLIED_2"));
            }
        }
    }

    private void CreateSampleDatas(int startIdx)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        tempDatas.clear();

        int skipIdx = 0;
        for (String item : data.get().getKeys(false))
        {
            try
            {
                if (skipIdx < startIdx)
                {
                    skipIdx++;
                    continue;
                }

                int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.

                if (!data.get().contains(item + ".value")) continue; // 장식용은 스킵

                int stock = data.get().getInt(item + ".stock");
                if (stock < 1) continue; // 무한재고에 대해서는 스킵
                int oldMedian = data.get().getInt(item + ".median");
                if (oldMedian < 1) continue; // 고정가 상품에 대해서는 스킵

                SimulData simulData = new SimulData();
                simulData.mat = data.get().getString(item + ".mat");
                simulData.stock = data.get().getInt(item + ".stock");
                simulData.median = data.get().getInt(item + ".median");
                simulData.value = data.get().getDouble(item + ".value");
                simulData.valueMax = data.get().getDouble(item + ".valueMax", 0);
                simulData.valueMin = data.get().getDouble(item + ".valueMin", 0);
                simulData.tradeType = data.get().getString(item + ".tradeType");
                tempDatas.add(simulData);
                if (tempDatas.size() >= 5)
                    break;
            } catch (Exception ignore)
            {
            }
        }

        int i = 0;
        for (SimulData simulData : tempDatas)
        {
            Material mat = Material.getMaterial(simulData.mat);
            if (mat == null)
                continue;

            ItemStack itemStack = new ItemStack(mat);
            ItemMeta meta = itemStack.getItemMeta();

            ArrayList<String> lore = CreateItemLore(simulData, null);
            lore.add(t(null, "STOCK_SIMULATOR.CHANGE_SAMPLE_LORE"));
            meta.setLore(lore);
            itemStack.setItemMeta(meta);

            inventory.setItem(ITEM_1 + (i * 9), itemStack);

            i++;
        }
    }

    private void UpdateBottomButtons()
    {
        for (int i = 47; i < 54; i++)
            inventory.setItem(i, null);

        // 랜덤
        if (fluc)
        {
            ArrayList<String> fluctuationLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(null, "CUR_STATE") + ": " + t(null, "ON"),
                    "§e" + t(null, "CLICK") + ": " + t(null, "OFF")
            ));
            CreateButton(FLUC, Material.COMPARATOR, t(null, "FLUCTUATION.FLUCTUATION"), fluctuationLore);

            ArrayList<String> fluctuation_interval_lore = new ArrayList<>(Arrays.asList(
                    t(null, "FLUCTUATION.INTERVAL_LORE"),
                    "§9" + t(null, "CUR_STATE") + ": " + flucInterval / 2.0 + "h",
                    "§e" + t(null, "CLICK") + ": " + t(null, "STOCK_SIMULATOR.L_R_SHIFT")));
            CreateButton(FLUC_INTERVAL, Material.COMPARATOR, t(null, "FLUCTUATION.INTERVAL"), fluctuation_interval_lore, Clamp(flucInterval / 2, 1, 64));

            ArrayList<String> fluctuation_strength_lore = new ArrayList<>(Arrays.asList(
                    t(null, "FLUCTUATION.STRENGTH_LORE"),
                    "§9" + t(null, "CUR_STATE") + ": ~" + flucStrength + "%",
                    "§e" + t(null, "CLICK") + ": " + t(null, "STOCK_STABILIZING.L_R_SHIFT")));
            CreateButton(FLUC_STRENGTH, Material.COMPARATOR, t(null, "FLUCTUATION.STRENGTH"), fluctuation_strength_lore, Clamp((int) (flucStrength * 10), 1, 64));
        } else
        {
            ItemStack flucToggleBtn = ItemsUtil.createItemStack(Material.COMPARATOR, null,
                    t(null, "FLUCTUATION.FLUCTUATION"),
                    new ArrayList<>(Arrays.asList(
                            "§9" + t(null, "CUR_STATE") + ": " + t(null, "OFF"),
                            "§e" + t(null, "CLICK") + ": " + t(null, "ON")
                    )),
                    1);
            inventory.setItem(FLUC, flucToggleBtn);
        }

        // 안정화
        if (stable)
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(null, "CUR_STATE") + ": " + t(null, "ON"),
                    "§e" + t(null, "CLICK") + ": " + t(null, "OFF")
            ));
            CreateButton(STABLE, Material.COMPARATOR, t(null, "STOCK_STABILIZING.SS"), stableLore);

            ArrayList<String> stable_interval_Lore = new ArrayList<>(Arrays.asList(
                    t(null, "FLUCTUATION.INTERVAL_LORE"),
                    "§9" + t(null, "CUR_STATE") + ": " + stableInterval / 2.0 + "h",
                    "§e" + t(null, "CLICK") + ": " + t(null, "STOCK_SIMULATOR.L_R_SHIFT")));
            CreateButton(STABLE_INTERVAL, Material.COMPARATOR, t(null, "FLUCTUATION.INTERVAL"), stable_interval_Lore, Clamp(stableInterval / 2, 1, 64));

            ArrayList<String> stable_strength_Lore = new ArrayList<>(Arrays.asList(
                    ConfigUtil.GetUseLegacyStockStabilization() ? t(null, "STOCK_STABILIZING.STRENGTH_LORE_A") : t(null, "STOCK_STABILIZING.STRENGTH_LORE_B"),
                    "§9" + t(null, "CUR_STATE") + ": ~" + stableStrength + "%",
                    "§e" + t(null, "CLICK") + ": " + t(null, "STOCK_STABILIZING.L_R_SHIFT")));
            CreateButton(STABLE_STRENGTH, Material.COMPARATOR, t(null, "FLUCTUATION.STRENGTH"), stable_strength_Lore, Clamp((int) (stableStrength * 10), 1, 64));
        } else
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + t(null, "CUR_STATE") + ": " + t(null, "OFF"),
                    "§e" + t(null, "CLICK") + ": " + t(null, "ON")
            ));
            CreateButton(STABLE, Material.COMPARATOR, t(null, "STOCK_STABILIZING.SS"), stableLore);
        }

        CreateButton(RUN, Material.REDSTONE_BLOCK, t(null, "STOCK_SIMULATOR.RUN_TITLE"), t(null, "STOCK_SIMULATOR.RUN_LORE"));
    }

    private void RunSimulation()
    {
        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j <= 6; j++)
                inventory.setItem(ITEM_1 + (9 * i) + j + 2, null);
        }

        final int[] time = new int[]{
                48, 48 * 2, 48 * 3, //48 = 20분(마크 1일)
                144 * 2, 144 * 4, 144 * 6, // 144 = 1시간
                144 * 8, 144 * 10, 144 * 12,
                144 * 14, 144 * 16, 144 * 18,
                144 * 20, 144 * 22, 144 * 24,
                3456 * 2, 3456 * 3, 3456 * 4, // 3456 = 1일
                3456 * 5, 3456 * 6, 3456 * 7};

        Random generator = new Random();
        boolean useLegacyStockStabilization = ConfigUtil.GetUseLegacyStockStabilization();

        int itemIndex = 0;
        int timeIndex;

        for (SimulData simulData : tempDatas)
        {
            SimulData tempData = new SimulData();
            tempData.mat = simulData.mat;
            tempData.stock = simulData.stock;
            tempData.median = simulData.median;
            tempData.tradeType = simulData.tradeType;
            tempData.value = simulData.value;
            tempData.valueMin = simulData.valueMin;

            timeIndex = 0;
            int stock = tempData.stock;
            int median = tempData.median;

            ArrayList<String> timeString = new ArrayList<>();
            timeString.add(t(null, "STOCK_SIMULATOR.MEDIAN").replace("{num}", n(median)));
            timeString.add("\n");

            int dataCount = 0;

            for (int i = 0; i <= time[time.length - 1]; i++)
            {
                if (fluc && i % flucInterval == 0)
                {
                    stock = ShopUtil.RandomStockFluctuation(generator, stock, median, flucStrength);
                }
                if (stable && i % stableInterval == 0 && stock != median)
                {
                    stock = ShopUtil.StockStabilizing(useLegacyStockStabilization, generator, stock, median, stableStrength);
                }

                if (ArrayUtils.contains(time, i))
                {
                    String temp;
                    if(i == 48)
                    {
                        temp = t(null, "STOCK_SIMULATOR.AFTER_M").replace("{0}", String.valueOf(20)) + " " + t(null, "STOCK_SIMULATOR.REAL_TIME");
                    }
                    else if (i <= 144)
                    {
                        temp = t(null, "STOCK_SIMULATOR.AFTER_M").replace("{0}", String.valueOf((int)(i/2.4)));
                    }
                    else if (i <= 3456)
                    {
                        temp = t(null, "STOCK_SIMULATOR.AFTER_H").replace("{0}", String.valueOf((i/144)));
                    }
                    else
                    {
                        temp = t(null, "STOCK_SIMULATOR.AFTER_D").replace("{0}", String.valueOf(i/3456));
                    }

                    tempData.stock = stock;
                    tempData.median = median;
                    timeString.addAll(CreateItemLore(tempData, temp));
                    timeString.add("\n");

                    if ((dataCount + 1) % 3 == 0)
                    {
                        ItemStack item = new ItemStack(Material.valueOf(tempData.mat));
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(timeString);
                        item.setItemMeta(meta);
                        inventory.setItem(ITEM_1 + (9 * itemIndex) + timeIndex + 2, item);

                        timeString.clear();
                        timeString.add(t(null, "STOCK_SIMULATOR.MEDIAN").replace("{num}", n(median)));
                        timeString.add("\n");

                        timeIndex++;
                    }

                    dataCount++;
                }
            }

            itemIndex++;
        }
    }

    private ArrayList<String> CreateItemLore(SimulData simulData, String timeData)
    {
        ArrayList<String> lore = new ArrayList<>();

        if (timeData != null && !timeData.isEmpty())
            lore.add(timeData);

        lore.add(t(null, "STOCK_SIMULATOR.STOCK").replace("{num}", n(simulData.stock)));

        double price = CalcPrice(simulData);
        double basePrice = simulData.value;
        double priceSave1 = (price / basePrice) - 1;
        double priceSave2 = 1 - (price / basePrice);
        String arrow = "";
        if (price - basePrice > 0.005)
        {
            arrow = t(null, "ARROW.UP_2") + n(priceSave1 * 100) + "%";
        } else if (price - basePrice < -0.005)
        {
            arrow = t(null, "ARROW.DOWN_2") + n(priceSave2 * 100) + "%";
        }

        lore.add(t(null, "STOCK_SIMULATOR.PRICE").replace("{num}", n(price)) + " " + arrow);

        return lore;
    }

    private double CalcPrice(SimulData data)
    {
        double price;

        double value = data.value;
        double min = data.valueMin;
        double max = data.valueMax;
        int median = data.median;
        int stock = data.stock;

        price = (median * value) / stock;

        if (min != 0 && price < min)
        {
            price = min;
        }
        if (max != 0 && price > max)
        {
            price = max;
        }

        return price;
    }

    private void ApplySettings()
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        if (fluc)
        {
            data.get().set("Options.fluctuation.interval", flucInterval);
            data.get().set("Options.fluctuation.strength", flucStrength);
        } else
        {
            data.get().set("Options.fluctuation", null);
        }
        if (stable)
        {
            data.get().set("Options.stockStabilizing.interval", stableInterval);
            data.get().set("Options.stockStabilizing.strength", stableStrength);
        } else
        {
            data.get().set("Options.stockStabilizing", null);
        }

    }
}
