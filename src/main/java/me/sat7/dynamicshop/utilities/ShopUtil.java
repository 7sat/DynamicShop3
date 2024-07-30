package me.sat7.dynamicshop.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.NonNull;
import me.sat7.dynamicshop.models.DSItem;
import me.sat7.dynamicshop.transactions.Calc;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;

import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ShopUtil
{
    public static final HashMap<String, CustomConfig> shopConfigFiles = new HashMap<>();
    public static final HashMap<String, Boolean> shopDirty = new HashMap<>();

    private ShopUtil()
    {

    }

    public static void Reload()
    {
        ReloadAllShop();
        ConvertOldShopData();
        BackwardCompatibility();
        SetupSampleShopFile();
        SortShopDataAll();
    }

    public static void ReloadAllShop()
    {
        shopConfigFiles.clear();
        shopDirty.clear();

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
                shopDirty.put(shopName, false);
            }
        }
    }

    public static void SetupSampleShopFile()
    {
        if(ShopUtil.shopConfigFiles.isEmpty())
        {
            CustomConfig data = new CustomConfig();
            data.setup("SampleShop", "Shop");

            data.get().options().header("Shop name can not contain formatting codes, '/' and ' '");
            data.get().options().copyHeader(true);

            data.get().set("Options.title", "Sample Shop");
            data.get().set("Options.lore", "This is sample shop");
            data.get().set("Options.permission", "");
            data.get().set("Options.page", 2);
            data.get().set("Options.currency", Constants.S_VAULT);
            data.get().set("0.mat", "DIRT");
            data.get().set("0.value", 1);
            data.get().set("0.median", 10000);
            data.get().set("0.stock", 10000);
            data.get().set("1.mat", "COBBLESTONE");
            data.get().set("1.value", 1.5);
            data.get().set("1.median", 10000);
            data.get().set("1.stock", 10000);

            shopConfigFiles.put("SampleShop", data);
            shopDirty.put("SampleShop", false);

            data.get().options().copyDefaults(true);
            data.save();
        }
    }

    // Shop.yml 한덩어리로 되있는 데이터를 새 버전 방식으로 변환함 (v2 -> v3)
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
                ShopUtil.shopDirty.put(oldShopName,false);
            }

            file.delete();
        }
    }

    public static void BackwardCompatibility()
    {
        int userVersion = ConfigUtil.GetConfigVersion();
        if (userVersion < 5)
        {
            for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
            {
                FileConfiguration fc = entry.getValue().get();
                for(String key : fc.getKeys(false))
                {
                    if (fc.contains(key + ".tradeLimitPerPlayer.value"))
                    {
                        int old = fc.getInt(key + ".tradeLimitPerPlayer.value");
                        if (old < 0)
                        {
                            fc.set(key + ".tradeLimitPerPlayer.sell", old * -1);
                        }
                        else if (old > 0)
                        {
                            fc.set(key + ".tradeLimitPerPlayer.buy", old);
                        }

                        fc.set(key + ".tradeLimitPerPlayer.value", null);
                    }
                }
            }
        }

        if (userVersion < 7)
        {
            for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
            {
                FileConfiguration fc = entry.getValue().get();
                if (fc.contains("Options.flag.jobpoint"))
                {
                    fc.set("Options.flag.jobpoint", null);
                    fc.set("Options.currency", Constants.S_JOBPOINT);
                }
                if (fc.contains("Options.flag.playerpoint"))
                {
                    fc.set("Options.flag.playerpoint", null);
                    fc.set("Options.currency", Constants.S_PLAYERPOINT);
                }

                if (!fc.contains("Options.currency"))
                {
                    fc.set("Options.currency", Constants.S_VAULT);
                }
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
        if (item == null || item.getType().isAir())
            return -1;

        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return -1;

        int idx = 0;
        for (String s : data.get().getKeys(false))
        {
            if (idx == 0) //Just skips the first data. (=Options). At least it should be much faster than 'tryCatch+parseInt'.
            {
                idx++;
                continue;
            }

            if (!data.get().contains(s + ".value"))
                continue; // 장식용임

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
    }

    public static boolean hashExist(String shopName, String hash)
    {
        if (hash == null || hash.isEmpty())
            return false;

        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return false;

        int idx = 0;
        for (String s : data.get().getKeys(false))
        {
            if (idx == 0)
            {
                idx++;
                continue;
            }

            if (!data.get().contains(s + ".value"))
                continue; // 장식용임

            String compare = HashUtil.CreateHashString(data.get().getString(s + ".mat"), data.get().getString(s + ".itemStack"));
            if (hash.equals(compare))
            {
                return true;
            }
        }

        return false;
    }

    // 상점에 아이탬 추가
    public static boolean addItemToShop(String shopName, int idx, DSItem dsItem)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return false;

        try
        {
            data.get().set(idx + ".mat", dsItem.itemStack.getType().toString());

            if (dsItem.itemStack.hasItemMeta())
            {
                data.get().set(idx + ".itemStack", dsItem.itemStack.getItemMeta());
            }
            else
            {
                data.get().set(idx + ".itemStack", null);
            }

            if (dsItem.buyValue > 0)
            {
                data.get().set(idx + ".value", dsItem.buyValue);
                if (dsItem.buyValue == dsItem.sellValue)
                {
                    data.get().set(idx + ".value2", null);
                }
                else
                {
                    data.get().set(idx + ".value2", dsItem.sellValue);
                }

                if (dsItem.minPrice > 0.0001)
                {
                    data.get().set(idx + ".valueMin", dsItem.minPrice);
                }
                else
                {
                    data.get().set(idx + ".valueMin", null);
                }

                if (dsItem.maxPrice > 0.0001)
                {
                    data.get().set(idx + ".valueMax", dsItem.maxPrice);
                }
                else
                {
                    data.get().set(idx + ".valueMax", null);
                }

                data.get().set(idx + ".median", dsItem.median);
                data.get().set(idx + ".stock", dsItem.stock);

                if (dsItem.maxStock > 0)
                {
                    data.get().set(idx + ".maxStock", dsItem.maxStock);
                }
                else
                {
                    data.get().set(idx + ".maxStock", null);
                }

                if (dsItem.discount > 0)
                {
                    data.get().set(idx + ".discount", dsItem.discount);
                }
                else
                {
                    data.get().set(idx + ".discount", null);
                }

                if (dsItem.sellLimit != 0 || dsItem.buyLimit != 0)
                {
                    data.get().set(idx + ".tradeLimitPerPlayer.sell", dsItem.sellLimit);
                    data.get().set(idx + ".tradeLimitPerPlayer.buy", dsItem.buyLimit);
                    data.get().set(idx + ".tradeLimitPerPlayer.interval", dsItem.tradeLimitInterval);
                    data.get().set(idx + ".tradeLimitPerPlayer.nextTimer", dsItem.tradeLimitNextTimer);
                }
                else
                {
                    data.get().set(idx + ".tradeLimitPerPlayer", null);
                }
            }
            else
            {
                // idx,null하면 안됨. 존재는 하되 하위 데이터만 없어야함.
                data.get().set(idx + ".value", null);
                data.get().set(idx + ".value2", null);
                data.get().set(idx + ".valueMin", null);
                data.get().set(idx + ".valueMax", null);
                data.get().set(idx + ".median", null);
                data.get().set(idx + ".stock", null);
                data.get().set(idx + ".maxStock", null);
                data.get().set(idx + ".discount", null);
                data.get().set(idx + ".tradeLimitPerPlayer", null);
            }

            data.save();

            RotationUtil.UpdateCurrentRotationData(shopName, idx);

            return true;
        }
        catch (Exception e)
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
    public static void editShopItem(String shopName, int idx, DSItem dsItem)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        data.get().set(idx + ".value", dsItem.buyValue);
        if (dsItem.buyValue == dsItem.sellValue)
        {
            data.get().set(idx + ".value2", null);
        }
        else
        {
            data.get().set(idx + ".value2", dsItem.sellValue);
        }

        if (dsItem.minPrice > 0.0001)
        {
            data.get().set(idx + ".valueMin", dsItem.minPrice);
        }
        else
        {
            data.get().set(idx + ".valueMin", null);
        }

        if (dsItem.maxPrice > 0.0001)
        {
            data.get().set(idx + ".valueMax", dsItem.maxPrice);
        }
        else
        {
            data.get().set(idx + ".valueMax", null);
        }

        if (dsItem.maxStock > 0)
        {
            data.get().set(idx + ".maxStock", dsItem.maxStock);
        }
        else
        {
            data.get().set(idx + ".maxStock", null);
        }

        data.get().set(idx + ".median", dsItem.median);
        data.get().set(idx + ".stock", dsItem.stock);

        if (dsItem.discount > 0)
            data.get().set(idx + ".discount", dsItem.discount);
        else
            data.get().set(idx + ".discount", null);

        if (dsItem.sellLimit != 0 || dsItem.buyLimit != 0)
        {
            data.get().set(idx + ".tradeLimitPerPlayer.sell", dsItem.sellLimit);
            data.get().set(idx + ".tradeLimitPerPlayer.buy", dsItem.buyLimit);
            data.get().set(idx + ".tradeLimitPerPlayer.interval", dsItem.tradeLimitInterval);
            data.get().set(idx + ".tradeLimitPerPlayer.nextTimer", dsItem.tradeLimitNextTimer);
        }
        else
        {
            data.get().set(idx + ".tradeLimitPerPlayer", null);
            UserUtil.ClearTradeLimitData(shopName, idx);
        }

        data.save();

        RotationUtil.UpdateCurrentRotationData(shopName, idx);
    }

    // 상점에서 아이탬 제거
    public static void removeItemFromShop(String shopName, int idx)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        UserUtil.ClearTradeLimitData(shopName, idx); // 상점에서 데이터 지우기 전에 실행.

        data.get().set(String.valueOf(idx), null);
        data.save();

        RotationUtil.UpdateCurrentRotationData(shopName, idx);
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

    // 상점 페이지 스왑
    public static boolean SwapPage(String shopName, int pageA, int pageB)
    {
        if(pageA == pageB)
            return true;

        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return false;

        HashMap<Integer, Object> tempA = new HashMap<>();
        HashMap<Integer, Object> tempB = new HashMap<>();

        for (String s : data.get().getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(s);
                if (i >= (pageA - 1) * 45 && i < pageA * 45)
                {
                    tempA.put(i, data.get().get(s));
                    data.get().set(s, null);
                }
                if (i >= (pageB - 1) * 45 && i < pageB * 45)
                {
                    tempB.put(i, data.get().get(s));
                    data.get().set(s, null);
                }

            } catch (Exception ignored)
            {
            }
        }

        tempA.forEach((key, value) ->
        {
            key += (pageB - pageA) * 45;
            data.get().set(String.valueOf(key), value);
        });
        tempB.forEach((key, value) ->
        {
            key += (pageA - pageB) * 45;
            data.get().set(String.valueOf(key), value);
        });

        tempA.clear();
        tempB.clear();

        data.save();
        data.reload();
        return true;
    }

    public static boolean IsPageEmpty(String shopName, int page)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return true;

        if (page < 1 || page > GetShopMaxPage(shopName))
            return true;

        for (String s : data.get().getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(s);
                if (i >= (page - 1) * 45 && i < page * 45)
                    return false;
            } catch (Exception ignore)
            {
            }
        }

        return true;
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
        shopDirty.put(newName, false);
        shopDirty.remove(shopName);

        RotationUtil.OnShopNameChanged(shopName, newName);
        UserUtil.OnRenameShop(shopName, newName);
    }

    public static void copyShop(String shopName, String newName)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if (data == null)
            return;

        data.copy(newName);
        data.get().set("Options.title", newName);
        shopConfigFiles.put(newName, data);
        shopDirty.put(newName, false);

        RotationUtil.OnShopCopy(shopName, newName);
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

        UserUtil.OnMergeShop(shopA, shopB);

        dataB.delete();
        shopConfigFiles.remove(shopB);
        shopDirty.remove(shopB);

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
        newValue = (Math.round(newValue * 10000) / 10000.0);

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
                    int discount = data.get().getInt(itemIndex + ".discount");
                    int sellLimit = data.get().getInt(itemIndex + ".tradeLimitPerPlayer.sell");
                    int buyLimit = data.get().getInt(itemIndex + ".tradeLimitPerPlayer.buy");
                    long tradeLimitInterval = data.get().getLong(itemIndex + ".tradeLimitPerPlayer.interval");
                    long tradeLimitNextTimer = data.get().getLong(itemIndex + ".tradeLimitPerPlayer.nextTimer");

                    int sugMid = CalcRecommendedMedian(worth, ConfigUtil.GetNumberOfPlayer());
                    DSItem temp = new DSItem(null, worth, worth, 0.0001f, -1, sugMid, sugMid, -1, discount,
                                             sellLimit, buyLimit, tradeLimitInterval, tradeLimitNextTimer);
                    ShopUtil.editShopItem(shop, i, temp);
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
        String topShopName = "";
        double bestPrice = -1;
        int tradeIdx = -1;

        String currency = "";
        ArrayList<String> failReason = new ArrayList<>();

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
                    if (DynamicShop.DEBUG_LOG_ENABLED)
                        failReason.add("shopName:" + entry.getKey() + "-no permission");
                    continue;
                }
            }

            // 비활성화된 상점
            boolean enable = data.get().getBoolean("Options.enable", true);
            if (!enable)
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-shop is disabled");
                continue;
            }

            // 표지판 전용 상점, 지역상점, 잡포인트 상점
            boolean outside = !CheckShopLocation(entry.getKey(), player);
            if (outside && data.get().contains("Options.flag.localshop") && !data.get().contains("Options.flag.deliverycharge")) {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-local shop");
                continue;
            }

            if (data.get().contains("Options.flag.signshop"))
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-sign shop");
                continue;
            }

            // 영업시간 확인
            if (player != null && !CheckShopHour(entry.getKey(), player))
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-not open hours");
                continue;
            }

            double deliveryCosts = CalcShipping(entry.getKey(), player);
            if (deliveryCosts == -1)
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-Infinite Delivery Fee (Other World)");
                continue;
            }

            int sameItemIdx = ShopUtil.findItemFromShop(entry.getKey(), itemStack);
            if (sameItemIdx != -1)
            {
                String tradeType = data.get().getString(sameItemIdx + ".tradeType");
                if (tradeType != null && tradeType.equalsIgnoreCase("BuyOnly"))
                {
                    if (DynamicShop.DEBUG_LOG_ENABLED)
                        failReason.add("shopName:" + entry.getKey() + "-buy only");
                    continue; // 구매만 가능함
                }

                // 여러 재화로 취급중인 경우 지원 안함.
                 if (currency.isEmpty())
                {
                    currency = ShopUtil.GetCurrency(data);
                }
                else if (!currency.equalsIgnoreCase(ShopUtil.GetCurrency(data)))
                {
                    if(player != null)
                    {
                        boolean useLocalizedName = ConfigUtil.GetLocalizedItemName();
                        if (useLocalizedName)
                        {
                            String message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.Q_SEARCH_FAIL_CURRENCY") + " - <item>";
                            LangUtil.sendMessageWithLocalizedItemName(player, message, itemStack.getType());
                        }
                        else
                        {
                            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.Q_SEARCH_FAIL_CURRENCY") + " - " + ItemsUtil.getBeautifiedName(itemStack.getType()));
                        }
                    }

                    return new String[]{"","-2"};
                }

                // 상점에 돈이 없음
                if (ShopUtil.getShopBalance(entry.getKey()) != -1 &&
                        ShopUtil.getShopBalance(entry.getKey()) < Calc.calcTotalCost(entry.getKey(), String.valueOf(sameItemIdx), itemStack.getAmount())[0])
                {
                    if (DynamicShop.DEBUG_LOG_ENABLED)
                        failReason.add("shopName:" + entry.getKey() + "-no money in shop");
                    continue;
                }

                // 최대 재고를 넘겨서 매입 거절
                int maxStock = data.get().getInt(sameItemIdx + ".maxStock", -1);
                int stock = data.get().getInt(sameItemIdx + ".stock");
                if (maxStock != -1 && maxStock <= stock)
                {
                    if (DynamicShop.DEBUG_LOG_ENABLED)
                        failReason.add("shopName:" + entry.getKey() + "-shop reaches max stock");
                    continue;
                }

                // 플레이어 당 거래량 제한 확인
                int sellLimit = ShopUtil.GetSellLimitPerPlayer(entry.getKey(), sameItemIdx);
                if (player != null && sellLimit != 0)
                {
                    int tradeAmount = UserUtil.CheckTradeLimitPerPlayer(player, entry.getKey(), sameItemIdx, HashUtil.GetItemHash(itemStack), itemStack.getAmount(), true);
                    if (tradeAmount == 0)
                    {
                        if (DynamicShop.DEBUG_LOG_ENABLED)
                            failReason.add("shopName:" + entry.getKey() + "-Reached trading volume limit");
                        continue;
                    }
                }

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

        if (DynamicShop.DEBUG_LOG_ENABLED && topShopName.equals("") && failReason.size() > 0)
        {
            DynamicShop.PrintConsoleDbgLog("No shops available for sell. player:" + player + " itemType:" + itemStack.getType() + " Details:");
            for (String s : failReason)
            {
                DynamicShop.PrintConsoleDbgLog(" - " + s);
            }
        }

        return new String[]{topShopName, Integer.toString(tradeIdx)};
    }

    public static String[] FindTheBestShopToBuy(Player player, ItemStack itemStack)
    {
        String topShopName = "";
        double bestPrice = Double.MAX_VALUE;
        int tradeIdx = -1;

        int currencyInt = -1;

        ArrayList<String> failReason = new ArrayList<>();

        // 접근가능한 상점중 최저가 찾기
        for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
        {
            CustomConfig data = entry.getValue();

            // 권한 없는 상점
            String permission = data.get().getString("Options.permission");
            if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".buy"))
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-no permission");
                continue;
            }

            // 비활성화된 상점
            boolean enable = data.get().getBoolean("Options.enable", true);
            if (!enable)
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-shop is disabled");
                continue;
            }

            // 표지판 전용 상점, 지역상점, 잡포인트 상점
            boolean outside = !CheckShopLocation(entry.getKey(), player);
            if (outside && data.get().contains("Options.flag.localshop") && !data.get().contains("Options.flag.deliverycharge")) {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-local shop");
                continue;
            }

            if (data.get().contains("Options.flag.signshop"))
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-sign shop");
                continue;
            }

            // 영업시간 확인
            if (!CheckShopHour(entry.getKey(), player))
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-not open hours");
                continue;
            }

            double deliveryCosts = CalcShipping(entry.getKey(), player);
            if (deliveryCosts == -1)
            {
                if (DynamicShop.DEBUG_LOG_ENABLED)
                    failReason.add("shopName:" + entry.getKey() + "-Infinite Delivery Fee (Other World)");
                continue;
            }

            int sameItemIdx = ShopUtil.findItemFromShop(entry.getKey(), itemStack);

            if (sameItemIdx != -1)
            {
                String tradeType = data.get().getString(sameItemIdx + ".tradeType");

                if (tradeType != null && tradeType.equalsIgnoreCase("SellOnly"))
                {
                    if (DynamicShop.DEBUG_LOG_ENABLED)
                        failReason.add("shopName:" + entry.getKey() + "-sell only");
                    continue;
                }

                // 재고가 없음
                int stock = data.get().getInt(sameItemIdx + ".stock");
                if (stock != -1 && stock < 2)
                {
                    if (DynamicShop.DEBUG_LOG_ENABLED)
                        failReason.add("shopName:" + entry.getKey() + "-Out of stock");
                    continue;
                }

                // 여러 재화로 취급중인 경우 지원 안함.
                int tempCurrencyIndex = 0;
                if (ShopUtil.GetCurrency(data).equalsIgnoreCase(Constants.S_JOBPOINT))
                {
                    tempCurrencyIndex = 1;
                }
                else if (ShopUtil.GetCurrency(data).equalsIgnoreCase(Constants.S_PLAYERPOINT))
                {
                    tempCurrencyIndex = 2;
                }
                else if (ShopUtil.GetCurrency(data).equalsIgnoreCase(Constants.S_EXP))
                {
                    tempCurrencyIndex = 3;
                }

                if (currencyInt == -1)
                {
                    currencyInt = tempCurrencyIndex;
                }
                else if (currencyInt != tempCurrencyIndex)
                {
                    if(player != null)
                    {
                        boolean useLocalizedName = ConfigUtil.GetLocalizedItemName();
                        if (useLocalizedName)
                        {
                            String message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.Q_SEARCH_FAIL_CURRENCY") + " - <item>";
                            LangUtil.sendMessageWithLocalizedItemName(player, message, itemStack.getType());
                        }
                        else
                        {
                            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.Q_SEARCH_FAIL_CURRENCY") + " - " + ItemsUtil.getBeautifiedName(itemStack.getType()));
                        }
                    }

                    return new String[]{"","-2"};
                }

                // 플레이어 당 거래량 제한 확인
                int buyLimit = ShopUtil.GetBuyLimitPerPlayer(entry.getKey(), sameItemIdx);
                if (player != null && buyLimit != 0)
                {
                    int tradeAmount = UserUtil.CheckTradeLimitPerPlayer(player, entry.getKey(), sameItemIdx, HashUtil.GetItemHash(itemStack), itemStack.getAmount(), false);
                    if (tradeAmount == 0)
                    {
                        if (DynamicShop.DEBUG_LOG_ENABLED)
                            failReason.add("shopName:" + entry.getKey() + "-Trading volume limit reached");
                        continue;
                    }
                }

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

        if (DynamicShop.DEBUG_LOG_ENABLED && topShopName.equals("") && failReason.size() > 0)
        {
            DynamicShop.PrintConsoleDbgLog("No shops available for purchase. player:" + player + " itemType:" + itemStack.getType() + " Details:");
            for (String s : failReason)
            {
                DynamicShop.PrintConsoleDbgLog(" - " + s);
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
        boolean legacyStabilizer = ConfigUtil.GetUseLegacyStockStabilization();

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

                //DynamicShop.console.sendMessage("debug... " + randomStockTimer + " % " + interval + " = " + randomStockTimer % interval);

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
        if (player == null)
            return 0;

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
                int dist = (int) (player.getLocation().distance(lo) * 0.1 * ConfigUtil.GetDeliveryChargeScale());
                deliverycharge = Clamp(dist, ConfigUtil.GetDeliveryChargeMin(), ConfigUtil.GetDeliveryChargeMax());
            }
        }

        return deliverycharge;
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

    public static void ShopYMLBackup()
    {
        File[] listOfFiles = new File(DynamicShop.plugin.getDataFolder() + "/Shop").listFiles();
        if(listOfFiles != null)
        {
            long time = System.currentTimeMillis();
            for (File f : listOfFiles)
            {
                String path = DynamicShop.plugin.getDataFolder() + "/Shop_Backup/" + time + "/";
                File newFile = new File(path + f.getName());
                try
                {
                    FileUtils.copyFile(f, newFile);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        ShopYMLBackupCull();
    }

    public static void ShopYMLBackupCull()
    {
        File[] listOfFiles = new File(DynamicShop.plugin.getDataFolder() + "/Shop_Backup").listFiles();
        if(listOfFiles != null)
        {
            for(File f : listOfFiles)
            {
                int ageMins = (int) (System.currentTimeMillis() - f.lastModified()) / 60000;
                if (ageMins >= ConfigUtil.GetShopYmlBackup_CullAgeMinutes())
                {
                    try
                    {
                        FileUtils.deleteDirectory(f);
                    } catch (IOException e)
                    {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }

    public static int GetShopItemCount(String shopName, Boolean excludeInfiniteStock, Boolean excludeFixedPrice)
    {
        CustomConfig data = shopConfigFiles.get(shopName);
        if(data == null)
            return -1;

        int count = 0;
        for (String item : data.get().getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.
                if (!data.get().contains(item + ".value")) continue; // 장식용은 스킵

                int stock = data.get().getInt(item + ".stock");
                if (excludeInfiniteStock && stock < 1) continue; // 무한재고에 대해서는 스킵
                int oldMedian = data.get().getInt(item + ".median");
                if (excludeFixedPrice && oldMedian < 1) continue; // 고정가 상품에 대해서는 스킵

                count++;
            }
            catch (Exception ignore){}
        }

        return count;
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

    public static void SetShopSellCommand(String shopName, int idx, String command)
    {
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        if (shopData == null)
            return;

        ConfigurationSection shopConf = shopData.get().getConfigurationSection("Options");
        if (shopConf == null)
            return;

        shopConf.set("command.sell." + idx, command);
        CleanupCommandIndex(shopName, "sell");

        shopData.save();
    }

    public static void SetShopBuyCommand(String shopName, int idx, String command)
    {
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        if (shopData == null)
            return;

        ConfigurationSection shopConf = shopData.get().getConfigurationSection("Options");
        if (shopConf == null)
            return;

        shopConf.set("command.buy." + idx, command);
        CleanupCommandIndex(shopName, "buy");

        shopData.save();
    }

    public static void CleanupCommandIndex(String shopName, String sellBuyString)
    {
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        if (shopData == null)
            return;

        ArrayList<Object> tempDatas = new ArrayList<>();
        if (shopData.get().getConfigurationSection("Options.command." + sellBuyString) != null)
        {
            for (Map.Entry<String, Object> s : shopData.get().getConfigurationSection("Options.command." + sellBuyString).getValues(false).entrySet())
            {
                tempDatas.add(s.getValue());
            }
        }
        shopData.get().set("Options.command." + sellBuyString, null);
        for (int i = 0; i < tempDatas.size(); i++)
        {
            shopData.get().set("Options.command." + sellBuyString + "." + i, tempDatas.get(i));
        }
    }

    public static void SaveDirtyShop()
    {
        for(Map.Entry<String, Boolean> entry : shopDirty.entrySet())
        {
            if(entry.getValue())
            {
                if(DynamicShop.DEBUG_MODE)
                {
                    DynamicShop.console.sendMessage("DirtyShop Saved: " + entry.getValue());
                }

                shopDirty.put(entry.getKey(), false);
                shopConfigFiles.get(entry.getKey()).save();
            }
        }
    }

    public static void ForceSaveAllShop()
    {
        for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
        {
            entry.getValue().save();
        }
    }

    public static int GetSellLimitPerPlayer(String shopName, int idx)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        if (!data.get().contains(idx + ".tradeLimitPerPlayer"))
        {
            return 0;
        }

        return data.get().getInt(idx + ".tradeLimitPerPlayer.sell");
    }

    public static int GetBuyLimitPerPlayer(String shopName, int idx)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        if (!data.get().contains(idx + ".tradeLimitPerPlayer"))
        {
            return 0;
        }

        return data.get().getInt(idx + ".tradeLimitPerPlayer.buy");
    }

    private final static SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    public static String GetTradeLimitNextResetTime(String shopName, int idx)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        boolean somethingChanged = false;

        long interval = data.get().getLong(idx + ".tradeLimitPerPlayer.interval");
        if (interval == 0)
        {
            interval = MathUtil.dayInMilliSeconds;
            data.get().set(idx + ".tradeLimitPerPlayer.interval", interval);
            somethingChanged = true;
        }

        long next = data.get().getLong(idx + ".tradeLimitPerPlayer.nextTimer");
        if (next == 0)
        {
            next = System.currentTimeMillis();
            next = MathUtil.RoundDown_Time_Hour(next);
            data.get().set(idx + ".tradeLimitPerPlayer.nextTimer", next);

            somethingChanged = true;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime > next)
        {
            while (currentTime > next)
            {
                next += interval;
            }

            next = MathUtil.RoundDown_Time_Hour(next);
            data.get().set(idx + ".tradeLimitPerPlayer.nextTimer", next);

            UserUtil.ClearTradeLimitData(shopName, idx);
            somethingChanged = true;
        }

        if (somethingChanged)
            ShopUtil.shopDirty.put(shopName, true);

        return sdf.format(next);
    }

    public static String GetCurrency(@NonNull CustomConfig config)
    {
        return GetCurrency(config.get());
    }
    public static String GetCurrency(@NonNull FileConfiguration fileConfiguration)
    {
        String temp = fileConfiguration.getString("Options.currency", "");

        if (temp.equalsIgnoreCase(Constants.S_EXP))
        {
            return Constants.S_EXP;
        }
        else if (temp.equalsIgnoreCase(Constants.S_PLAYERPOINT))
        {
            return Constants.S_PLAYERPOINT;
        }
        else if (temp.equalsIgnoreCase(Constants.S_JOBPOINT))
        {
            return Constants.S_JOBPOINT;
        }
        else
        {
            return Constants.S_VAULT;
        }
    }
}
