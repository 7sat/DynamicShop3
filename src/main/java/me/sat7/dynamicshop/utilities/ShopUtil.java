package me.sat7.dynamicshop.utilities;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.sat7.dynamicshop.transactions.Calc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ShopUtil
{
    public static final HashMap<String, CustomConfig> shopConfigFiles = new HashMap<>();

    private ShopUtil()
    {

    }

    public static void Reload()
    {
        ReloadAllShop();
        ConvertOldShopData();
        SetupSampleShopFile();
        SortShopDataAll();
    }

    public static void ReloadAllShop()
    {
        shopConfigFiles.clear();

        File[] listOfFiles = new File(DynamicShop.plugin.getDataFolder() + "/Shop").listFiles();
        if(listOfFiles != null)
        {
            for (File f : listOfFiles)
            {
                CustomConfig shopCC = new CustomConfig();

                int idx = f.getName().lastIndexOf( "." );
                String shopName = f.getName().substring(0, idx );
                shopCC.setup(shopName, "Shop");
                shopConfigFiles.put(shopName, shopCC);
            }
        }
    }

    // 상점에서 빈 슬롯 찾기
    public static int findEmptyShopSlot(String shopName, int startIdx, boolean addPage)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if(data == null)
            return -1;

        if(startIdx < 0)
            startIdx = 0;

        int idx = startIdx;
        while(data.get().contains(String.valueOf(idx)))
            idx++;

        if (data.get().getInt("Options.page") < idx / 45 + 1)
        {
            if(addPage)
            {
                data.get().set("Options.page", idx / 45 + 1);
                data.save();
                return idx;
            }

            return -1;
        }

        return idx;
    }

    // 상점에서 아이탬타입 찾기
    public static int findItemFromShop(String shopName, ItemStack item)
    {
        try {
            return asyncFindItem(shopName, item).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static CompletableFuture<Integer> asyncFindItem(String shopName, ItemStack item) {
        return CompletableFuture.supplyAsync(() -> {
            if (item == null || item.getType().isAir())
                return -1;

            CustomConfig data = shopConfigFiles.get(shopName);
            if (data == null)
                return -1;

            for (String s : data.get().getKeys(false))
            {
                try
                {
                    int i = Integer.parseInt(s);
                } catch (Exception e)
                {
                    continue;
                }

                if (!data.get().contains(s + ".value")) continue; // 장식용임

                if (data.get().getString(s + ".mat").equals(item.getType().toString()))
                {
                    String metaStr = data.get().getString(s + ".itemStack");

                    if (metaStr == null && !item.hasItemMeta())
                    {
                        return Integer.parseInt(s);
                    }

                    if (metaStr != null && metaStr.equals(item.getItemMeta().toString()))
                    {
                        return Integer.parseInt(s);
                    }
                }
            }
            return -1;
        });
    }

    // 상점에 아이탬 추가
    public static boolean addItemToShop(String shopName, int idx, ItemStack item, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock)
    {
        return addItemToShop(shopName, idx, item, buyValue, sellValue, minValue, maxValue, median, stock, -1);
    }
    public static boolean addItemToShop(String shopName, int idx, ItemStack item, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock, int maxStock)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return false;

        try
        {
            data.get().set(idx + ".mat", item.getType().toString());

            if (item.hasItemMeta())
            {
                data.get().set(idx + ".itemStack", item.getItemMeta());
            } else
            {
                data.get().set(idx + ".itemStack", null);
            }

            if (buyValue > 0)
            {
                data.get().set(idx + ".value", buyValue);
                if (buyValue == sellValue)
                {
                    data.get().set(idx + ".value2", null);
                } else
                {
                    data.get().set(idx + ".value2", sellValue);
                }

                if (minValue > 0.01)
                {
                    data.get().set(idx + ".valueMin", minValue);
                } else
                {
                    data.get().set(idx + ".valueMin", null);
                }

                if (maxValue > 0.01)
                {
                    data.get().set(idx + ".valueMax", maxValue);
                } else
                {
                    data.get().set(idx + ".valueMax", null);
                }

                data.get().set(idx + ".median", median);
                data.get().set(idx + ".stock", stock);

                if(maxStock > 0)
                {
                    data.get().set(idx + ".maxStock", maxStock);
                }
                else
                {
                    data.get().set(idx + ".maxStock", null);
                }

            } else
            {
                // idx,null하면 안됨. 존재는 하되 하위 데이터만 없어야함.
                data.get().set(idx + ".value", null);
                data.get().set(idx + ".value2", null);
                data.get().set(idx + ".valueMin", null);
                data.get().set(idx + ".valueMax", null);
                data.get().set(idx + ".median", null);
                data.get().set(idx + ".stock", null);
                data.get().set(idx + ".maxStock", null);
            }

            data.save();

            return true;
        } catch (Exception e)
        {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " ERR.AddItemToShop.");
            for (StackTraceElement s : e.getStackTrace())
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " " + s.toString());
            }
            return false;
        }
    }

    // 상점 아이탬의 value, median, stock을 수정
    public static void editShopItem(String shopName, int idx, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock)
    {
        editShopItem(shopName, idx, buyValue, sellValue, minValue, maxValue, median, stock, -1);
    }
    public static void editShopItem(String shopName, int idx, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock, int maxStock)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        data.get().set(idx + ".value", buyValue);
        if (buyValue == sellValue)
        {
            data.get().set(idx + ".value2", null);
        } else
        {
            data.get().set(idx + ".value2", sellValue);
        }
        if (minValue > 0.01)
        {
            data.get().set(idx + ".valueMin", minValue);
        } else
        {
            data.get().set(idx + ".valueMin", null);
        }
        if (maxValue > 0.01)
        {
            data.get().set(idx + ".valueMax", maxValue);
        } else
        {
            data.get().set(idx + ".valueMax", null);
        }
        if(maxStock > 0)
        {
            data.get().set(idx + ".maxStock", maxStock);
        }
        else
        {
            data.get().set(idx + ".maxStock", null);
        }
        data.get().set(idx + ".median", median);
        data.get().set(idx + ".stock", stock);
        data.save();
    }

    // 상점에서 아이탬 제거
    public static void removeItemFromShop(String shopName, int idx)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        data.get().set(String.valueOf(idx), null);
        data.save();
    }

    // 상점 페이지 삽입
    public static void insetShopPage(String shopName, int page)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        data.get().set("Options.page", data.get().getInt("Options.page") + 1);

        for (int i = data.get().getInt("Options.page") * 45; i >= (page - 1) * 45; i--)
        {
            ConfigurationSection temp = data.get().getConfigurationSection(String.valueOf(i));
            data.get().set(String.valueOf(i + 45), temp);
            data.get().set(String.valueOf(i), null);
        }

        data.save();
        data.reload();
    }

    // 상점 페이지 삭제
    public static void deleteShopPage(String shopName, int page)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        data.get().set("Options.page", data.get().getInt("Options.page") - 1);

        for (String s : data.get().getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(s);

                if (i >= (page - 1) * 45 && i < page * 45)
                {
                    data.get().set(s, null);
                } else if (i >= page * 45)
                {
                    ConfigurationSection temp = data.get().getConfigurationSection(s);
                    data.get().set(String.valueOf(i - 45), temp);
                    data.get().set(s, null);
                }

            } catch (Exception ignored)
            {
            }
        }

        data.save();
        data.reload();
    }

    // 상점 이름 바꾸기
    public static void renameShop(String shopName, String newName)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        data.rename(newName);
        data.get().set("Options.title", newName);
        shopConfigFiles.put(newName, data);
        shopConfigFiles.remove(shopName);
    }

    // 상점 병합
    public static void mergeShop(String shopA, String shopB)
    {
        // A에 B의 아이템을 다 밀어넣는 방식임.
        // 상점잔액 합쳐주는것 말고는 별도의 처리 없음
        CustomConfig dataA = shopConfigFiles.get(shopA);
        CustomConfig dataB = shopConfigFiles.get(shopB);

        int pg1 = dataA.get().getInt("Options.page");
        int pg2 = dataB.get().getInt("Options.page");

        dataA.get().set("Options.page", pg1 + pg2);
        if (dataA.get().contains("Options.Balance") || dataB.get().contains("Options.Balance"))
        {
            double a = getShopBalance(shopA);
            if (a == -1) a = 0;

            double b = 0;
            if (!(dataA.get().getString("Options.Balance").equals(shopB) || dataB.get().getString("Options.Balance").equals(shopA)))
            {
                b = getShopBalance(shopB);
            }

            if (b == -1) b = 0;

            if (a + b > 0)
            {
                dataA.get().set("Options.Balance", a + b);
            } else
            {
                dataA.get().set("Options.Balance", null);
            }
        }

        for (String item : dataB.get().getKeys(false))
        {
            try
            {
                dataA.get().set(String.valueOf(Integer.parseInt(item) + (pg1 * 45)), dataB.get().get(item));
            } catch (Exception ignored)
            {
            }
        }

        dataB.delete();
        shopConfigFiles.remove(shopB);

        dataA.save();
        dataA.reload();
    }

    // 상점의 잔액 확인
    public static double getShopBalance(String shopName)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return -1;

        // 무한
        if (!data.get().contains("Options.Balance")) return -1;

        double shopBal;

        try
        {
            shopBal = Double.parseDouble(data.get().getString("Options.Balance")); // 파싱에 실패하면 캐치로 가는 방식.
        }
        // 연동형
        catch (Exception ee)
        {
            String linkedShop = data.get().getString("Options.Balance");

            // 그런 상점이 없음.
            CustomConfig linkedShopData = shopConfigFiles.get(linkedShop);
            if (linkedShopData == null)
            {
                data.get().set("Options.Balance", null);
                data.save();
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + shopName + ", " + linkedShop + "/ target shop not found");
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + shopName + "/ balance has been reset");
                return -1;
            }

            // 연결 대상이 실제 계좌가 아님.
            try
            {
                if (linkedShopData.get().contains("Options.Balance"))
                {
                    double temp = Double.parseDouble(linkedShopData.get().getString( "Options.Balance"));
                } else
                {
                    return -1;
                }
            } catch (Exception e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +
                        shopName + ", " + linkedShop + "/ " +
                        t(null, "ERR.SHOP_LINK_TARGET_ERR"));

                data.get().set("Options.Balance", null);
                data.save();

                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + shopName + "/ balance has been reset");
                return -1;
            }

            shopBal = linkedShopData.get().getDouble("Options.Balance");
        }

        return shopBal;
    }

    // 상점의 잔액 수정
    public static void addShopBalance(String shopName, double amount)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        double old = getShopBalance(shopName);
        if (old < 0) return;

        double newValue = old + amount;
        newValue = (Math.round(newValue * 100) / 100.0);

        try
        {
            Double temp = Double.parseDouble(data.get().getString("Options.Balance"));
            data.get().set("Options.Balance", newValue);
        }
        // 연동형
        catch (Exception ee)
        {
            String linkedShop = data.get().getString("Options.Balance");
            CustomConfig linkedShopData = shopConfigFiles.get(linkedShop);
            if (linkedShopData != null)
                linkedShopData.get().set("Options.Balance", newValue);
        }
    }

    // 2틱 후 인벤토리 닫기
    public static void closeInventoryWithDelay(Player player)
    {
        //todo 왜 이렇게 만들었을까??? 2틱 딜레이가 필요한 이유가 뭐지?
        Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, player::closeInventory, 2);
    }

    public static void SetToRecommendedValueAll(String shop, CommandSender sender)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shop);
        if (data == null)
            return;

        for (String itemIndex : data.get().getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(itemIndex); // options에 대해 적용하지 않기 위해.

                if (!data.get().contains(itemIndex + ".value"))
                    continue; // 장식용은 스킵

                String itemName = data.get().getString(itemIndex + ".mat");

                double worth = WorthUtil.ccWorth.get().getDouble(itemName);
                if (worth == 0)
                {
                    itemName = itemName.replace("-", "");
                    itemName = itemName.replace("_", "");
                    itemName = itemName.toLowerCase();

                    worth = WorthUtil.ccWorth.get().getDouble(itemName);
                }

                if (worth != 0)
                {
                    int numberOfPlayer = DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer");
                    int sugMid = CalcRecommendedMedian(worth, numberOfPlayer);

                    ShopUtil.editShopItem(shop, i, worth, worth, 0.01f, -1, sugMid, sugMid);
                } else
                {
                    if (sender != null)
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.NO_RECOMMEND_DATA") + " : " + itemName);
                }
            } catch (Exception ignored)
            {
            }
        }
    }

    public static int CalcRecommendedMedian(double worth, int numberOfPlayer)
    {
        return (int) (4 / (Math.pow(worth, 0.35)) * 1000 * numberOfPlayer);
    }

    public static String[] FindTheBestShopToSell(Player player, ItemStack itemStack)
    {
        try {
            return asyncFindBestShoptoSell(player, itemStack).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CompletableFuture<String[]> asyncFindBestShoptoSell(Player player, ItemStack itemStack) {
        return CompletableFuture.supplyAsync(() -> {
            String topShopName = "";
            double bestPrice = -1;
            int tradeIdx = -1;

            // 접근가능한 상점중 최고가 찾기
            for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
            {
                CustomConfig data = entry.getValue();

                // 권한 없는 상점
                if(player != null)
                {
                    String permission = data.get().getString("Options.permission");
                    if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".sell"))
                    {
                        continue;
                    }
                }

                // 비활성화된 상점
                boolean enable = data.get().getBoolean("Options.enable", true);
                if (!enable)
                    continue;

            // 표지판 전용 상점, 지역상점, 잡포인트 상점
            boolean outside = !CheckShopLocation(entry.getKey(), player);
            if (outside && data.get().contains("Options.flag.localshop") && !data.get().contains("Options.flag.deliverycharge")) {
                continue;
            }

            if (data.get().contains("Options.flag.signshop") || data.get().contains("Options.flag.jobpoint"))
                continue;

                // 영업시간 확인
                if (player != null && !CheckShopHour(entry.getKey(), player))
                    continue;

            double deliveryCosts = CalcShipping(entry.getKey(), player);

            if (deliveryCosts == -1)
                continue;

            int sameItemIdx = ShopUtil.findItemFromShop(entry.getKey(), itemStack);

                if (sameItemIdx != -1)
                {
                    String tradeType = data.get().getString(sameItemIdx + ".tradeType");

                    if (tradeType != null && tradeType.equalsIgnoreCase("BuyOnly")) continue; // 구매만 가능함

                    // 상점에 돈이 없음
                    if (ShopUtil.getShopBalance(entry.getKey()) != -1 && ShopUtil.getShopBalance(entry.getKey()) < Calc.calcTotalCost(entry.getKey(), String.valueOf(sameItemIdx), itemStack.getAmount()))
                    {
                        continue;
                    }

                    // 최대 재고를 넘겨서 매입 거절
                    int maxStock = data.get().getInt(sameItemIdx + ".maxStock", -1);
                    int stock = data.get().getInt(sameItemIdx + ".stock");
                    if (maxStock != -1 && maxStock <= stock)
                        continue;

                double value = Calc.getCurrentPrice(entry.getKey(), String.valueOf(sameItemIdx), false);

                value -= deliveryCosts;

                if (topShopName.isEmpty() || bestPrice < value)
                {
                    topShopName = entry.getKey();
                    bestPrice = value;
                    tradeIdx = sameItemIdx;
                }
            }
        }

            return new String[]{topShopName, Integer.toString(tradeIdx)};
        });
    }

    public static String[] FindTheBestShopToBuy(Player player, ItemStack itemStack)
    {
        String topShopName = "";
        double bestPrice = Double.MAX_VALUE;
        int tradeIdx = -1;

        // 접근가능한 상점중 최저가 찾기
        for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
        {
            CustomConfig data = entry.getValue();

            // 권한 없는 상점
            String permission = data.get().getString("Options.permission");
            if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".buy"))
            {
                continue;
            }

            // 비활성화된 상점
            boolean enable = data.get().getBoolean("Options.enable", true);
            if (!enable)
                continue;

            // 표지판 전용 상점, 지역상점, 잡포인트 상점
            boolean outside = !CheckShopLocation(entry.getKey(), player);
            if (outside && data.get().contains("Options.flag.localshop") && !data.get().contains("Options.flag.deliverycharge")) {
                continue;
            }

            if (data.get().contains("Options.flag.signshop") || data.get().contains("Options.flag.jobpoint"))
                continue;

            // 영업시간 확인
            if (!CheckShopHour(entry.getKey(), player))
                continue;

            double deliveryCosts = CalcShipping(entry.getKey(), player);
            if (deliveryCosts == -1)
                continue;

            int sameItemIdx = ShopUtil.findItemFromShop(entry.getKey(), itemStack);

            if (sameItemIdx != -1)
            {
                String tradeType = data.get().getString(sameItemIdx + ".tradeType");

                if (tradeType != null && tradeType.equalsIgnoreCase("SellOnly")) continue;

                // 재고가 없음
                int stock = data.get().getInt(sameItemIdx + ".stock");
                if (stock != -1 && stock < 2)
                    continue;

                double value = Calc.getCurrentPrice(entry.getKey(), String.valueOf(sameItemIdx), true);

                value += deliveryCosts;

                if (bestPrice > value)
                {
                    topShopName = entry.getKey();
                    bestPrice = value;
                    tradeIdx = sameItemIdx;
                }
            }
        }

        return new String[]{topShopName, Integer.toString(tradeIdx)};
    }

    public static int GetShopMaxPage(String shopName)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return 0;

        return data.get().getConfigurationSection("Options").getInt("page");
    }

    private static int randomStockTimer = 1;
    public static void randomChange(Random generator)
    {
        boolean legacyStabilizer = DynamicShop.plugin.getConfig().getBoolean("Shop.UseLegacyStockStabilization");

        // 인게임 30분마다 실행됨 (500틱)
        randomStockTimer += 1;
        if (randomStockTimer >= Integer.MAX_VALUE)
        {
            randomStockTimer = 0;
        }
        //DynamicShop.console.sendMessage("debug... " + randomStockTimer);

        for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
        {
            boolean somethingIsChanged = false;

            CustomConfig data = entry.getValue();

            // fluctuation
            ConfigurationSection confSec = data.get().getConfigurationSection("Options.fluctuation");
            if (confSec != null)
            {
                int interval = confSec.getInt("interval");

                if (interval < 1 || interval > 999)
                {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Wrong value at " + entry.getKey() + ".Options.fluctuation.interval");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Reset to 48");
                    confSec.set("interval", 48);
                    interval = 48;
                }

                if (randomStockTimer % interval != 0) continue;

                for (String item : data.get().getKeys(false))
                {
                    try
                    {
                        int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.
                        if (!data.get().contains(item + ".value")) continue; // 장식용은 스킵

                        int stock = data.get().getInt(item + ".stock");
                        if (stock <= 1) continue; // 무한재고에 대해서는 스킵
                        int median = data.get().getInt(item + ".median");
                        if (median <= 1) continue; // 고정가 상품에 대해서는 스킵

                        stock = RandomStockFluctuation(generator, stock, median, confSec.getDouble("strength"));

                        data.get().set(item + ".stock", stock);
                        somethingIsChanged = true;
                    } catch (Exception ignored)
                    {
                    }
                }
            }

            // stock stabilizing
            ConfigurationSection confSec2 = data.get().getConfigurationSection("Options.stockStabilizing");
            if (confSec2 != null)
            {
                int interval = confSec2.getInt("interval");

                if (interval < 1 || interval > 999)
                {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Wrong value at " + entry.getKey() + ".Options.stockStabilizing.interval");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Reset to 48");
                    confSec2.set("interval", 48);
                    interval = 48;
                }

                if (randomStockTimer % interval != 0) continue;

                for (String item : data.get().getKeys(false))
                {
                    try
                    {
                        int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.
                        if (!data.get().contains(item + ".value")) continue; // 장식용은 스킵

                        int stock = data.get().getInt(item + ".stock");
                        if (stock < 1) continue; // 무한재고에 대해서는 스킵
                        int median = data.get().getInt(item + ".median");
                        if (median < 1) continue; // 고정가 상품에 대해서는 스킵

                        if (stock == median)
                            continue; // 이미 같으면 스킵

                        stock = StockStabilizing(legacyStabilizer, generator, stock, median, confSec2.getDouble("strength"));

                        data.get().set(item + ".stock", stock);
                        somethingIsChanged = true;
                    } catch (Exception ignored)
                    {
                    }
                }
            }

            if(somethingIsChanged)
                data.save();
        }
    }

    public static boolean CheckShopLocation(String shopName, Player player)
    {
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        if (shopData == null)
            return true;

        ConfigurationSection shopConf = shopData.get().getConfigurationSection("Options");
        if (shopConf == null)
            return true;

        if (!shopConf.contains("flag.localshop") || !shopConf.contains("world") || !shopConf.contains("pos1") || !shopConf.contains("pos2")) {
            return true;
        }

        boolean inside = player.getWorld().getName().equals(shopConf.getString("world"));

        String[] shopPos1 = shopConf.getString("pos1").split("_");
        String[] shopPos2 = shopConf.getString("pos2").split("_");
        int x1 = Integer.parseInt(shopPos1[0]);
        int y1 = Integer.parseInt(shopPos1[1]);
        int z1 = Integer.parseInt(shopPos1[2]);
        int x2 = Integer.parseInt(shopPos2[0]);
        int y2 = Integer.parseInt(shopPos2[1]);
        int z2 = Integer.parseInt(shopPos2[2]);

        if (!((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2) ||
                (x2 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x1)))
            inside = false;
        if (!((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2) ||
                (y2 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y1)))
            inside = false;
        if (!((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2) ||
                (z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1)))
            inside = false;

        return inside;
    }

    public static int CalcShipping(String shopName, Player player)
    {
        int deliverycharge = 0;

        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        if (shopData == null)
            return 0;

        ConfigurationSection shopConf = shopData.get().getConfigurationSection("Options");
        if (shopConf == null)
            return 0;


        if (shopConf.contains("world") && shopConf.contains("pos1") && shopConf.contains("flag.deliverycharge"))
        {
            boolean sameworld = true;
            boolean outside = !CheckShopLocation(shopName, player);

            if (!player.getWorld().getName().equals(shopConf.getString("world"))) sameworld = false;

            String[] shopPos1 = shopConf.getString("pos1").split("_");
            int x1 = Integer.parseInt(shopPos1[0]);
            int y1 = Integer.parseInt(shopPos1[1]);
            int z1 = Integer.parseInt(shopPos1[2]);

            if (!sameworld)
            {
                deliverycharge = -1;
            } else if (outside)
            {
                Location lo = new Location(player.getWorld(), x1, y1, z1);
                int dist = (int) (player.getLocation().distance(lo) * 0.1 * DynamicShop.plugin.getConfig().getDouble("Shop.DeliveryChargeScale"));
                deliverycharge = Clamp(dist, DynamicShop.plugin.getConfig().getInt("Shop.DeliveryChargeMin"), DynamicShop.plugin.getConfig().getInt("Shop.DeliveryChargeMax"));
            }
        }

        return deliverycharge;
    }

    public static void SetupSampleShopFile()
    {
        if(ShopUtil.shopConfigFiles.isEmpty())
        {
            CustomConfig data = new CustomConfig();
            data.setup("SampleShop", "Shop");

            data.get().options().header("Shop name can not contain formatting codes, '/' and ' '");
            data.get().options().copyHeader(true);

            data.get().set("Options.page", 2);
            data.get().set("Options.title", "Sample Shop");
            data.get().set("Options.lore", "This is sample shop");
            data.get().set("Options.permission", "");
            data.get().set("0.mat", "DIRT");
            data.get().set("0.value", 1);
            data.get().set("0.median", 10000);
            data.get().set("0.stock", 10000);
            data.get().set("1.mat", "COBBLESTONE");
            data.get().set("1.value", 1.5);
            data.get().set("1.median", 10000);
            data.get().set("1.stock", 10000);

            shopConfigFiles.put("SampleShop", data);

            data.get().options().copyDefaults(true);
            data.save();
        }
    }

    // Shop.yml 한덩어리로 되있는 데이터를 새 버전 방식으로 변환함
    public static void ConvertOldShopData()
    {
        File file = new File(DynamicShop.plugin.getDataFolder(), "Shop.yml");
        if (file.exists())
        {
            CustomConfig oldShopData = new CustomConfig();
            oldShopData.setup("Shop", null);

            for(String oldShopName : oldShopData.get().getKeys(false))
            {
                ConfigurationSection oldData = oldShopData.get().getConfigurationSection(oldShopName);

                CustomConfig data = new CustomConfig();
                data.setup(oldShopName, "Shop");

                for(String s : oldData.getKeys(false))
                {
                    data.get().set(s, oldData.get(s));
                }

                if(data.get().contains("Options.hideStock"))
                {
                    data.get().set("Options.flag.hidestock", "");
                    data.get().set("Options.hideStock", null);
                }
                if(data.get().contains("Options.hidePricingType"))
                {
                    data.get().set("Options.flag.hidepricingtype", "");
                    data.get().set("Options.hidePricingType", null);
                }
                if(!data.get().contains("Options.lore"))
                {
                    data.get().set("Options.lore","");
                }

                data.save();

                ShopUtil.shopConfigFiles.put(oldShopName, data);
            }

            file.delete();
        }
    }

    public static void SortShopDataAll()
    {
        for(String s : shopConfigFiles.keySet())
        {
            SortShopData(s);
        }
    }

    // yml 파일 안의 거래 인덱스들을 정렬해서 다시 작성함
    public static void SortShopData(String shopName)
    {
        CustomConfig data = shopConfigFiles.get(shopName);

        HashMap<Integer, Object> sortData = new HashMap<>();

        for(String s : data.get().getKeys(false))
        {
            try
            {
                int dummy = Integer.parseInt(s); // 아이템 데이터가 아닌걸 건너뛰기 위함

                sortData.put(dummy, data.get().get(s));
                data.get().set(s, null);
            }
            catch(Exception ignore){}
        }

        Object[] keys = sortData.keySet().toArray();
        Arrays.sort(keys);

        for(Object o : keys)
        {
            data.get().set(o.toString(), sortData.get(o));
        }

        data.save();
    }

    public static int RandomStockFluctuation(Random generator, int stock, int median, double strength)
    {
        boolean down = generator.nextBoolean();
        double rate = stock / (double)median;
        if(rate < 0.5 && generator.nextBoolean())
            down = false;
        else if(rate > 2 && generator.nextBoolean())
            down = true;

        int amount = (int)(median * (strength / 100.0) * generator.nextFloat());
        if(down)
            amount *= -1;

        stock += amount;
        if (stock < 2) stock = 2;

        return stock;
    }

    public static int StockStabilizing(Boolean isLegacyMode, Random generator,int stock, int median, double strength)
    {
        if (isLegacyMode)
        {
            int amount = (int)(median * (strength / 100.0));
            if (stock < median)
            {
                stock += amount;
                if (stock > median) stock = median;
            } else
            {
                stock -= amount;
                if (stock < median) stock = median;
            }
        }
        else
        {
            int amount = (int)((median - stock) * (strength / 100.0));
            if (amount == 0)
            {
                if (generator.nextInt() % 2 == 0)
                {
                    amount = (stock > median) ? -1 : 1;
                }
            }

            stock += amount;
        }

        return stock;
    }

    public static boolean CheckShopHour(String shopName, Player player)
    {
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        if (shopData == null)
            return true;

        ConfigurationSection shopConf = shopData.get().getConfigurationSection("Options");
        if (shopConf == null)
            return true;

        if (shopConf.contains("shophours"))
        {
            int curTimeHour = (int) (player.getWorld().getTime()) / 1000 + 6;
            if (curTimeHour > 24) curTimeHour -= 24;

            String[] temp = shopConf.getString("shophours").split("~");

            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            if (close > open)
            {
                return open <= curTimeHour && curTimeHour < close;
            } else
            {
                return open <= curTimeHour || curTimeHour < close;
            }
        }
        else
        {
            return true;
        }
    }
}
