package me.sat7.dynamicshop.utilities;

import java.io.File;
import java.util.*;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.guis.InGameUI;
import me.sat7.dynamicshop.guis.UIManager;
import me.sat7.dynamicshop.transactions.Calc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;

public final class ShopUtil
{
    public static HashMap<String, CustomConfig> shopConfigFiles = new HashMap<>();

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
        for (File f : listOfFiles)
        {
            CustomConfig shopCC = new CustomConfig();

            int idx = f.getName().lastIndexOf( "." );
            String shopName = f.getName().substring(0, idx );
            shopCC.setup(shopName, "Shop");
            shopConfigFiles.put(shopName, shopCC);
        }
    }

    // 상점에서 빈 슬롯 찾기
    public static int findEmptyShopSlot(String shopName)
    {
        ArrayList<Integer> banList = new ArrayList<>();

        CustomConfig data = shopConfigFiles.get(shopName);
        if(data == null)
            return -1;

        for (String s : data.get().getKeys(false))
        {
            try
            {
                banList.add(Integer.parseInt(s));
            } catch (Exception ignored)
            {
            }
        }

        for (int i = 0; i < 45 * data.get().getInt("Options.page"); i++)
        {
            if (!banList.contains(i))
            {
                return i;
            }
        }

        return -1;
    }

    // 상점에서 아이탬타입 찾기
    public static int findItemFromShop(String shopName, ItemStack item)
    {
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
    }

    // 상점에 아이탬 추가
    public static boolean addItemToShop(String shopName, int idx, ItemStack item, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock)
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
            } else
            {
                // idx,null하면 안됨. 존재는 하되 하위 데이터만 없어야함.
                data.get().set(idx + ".value", null);
                data.get().set(idx + ".value2", null);
                data.get().set(idx + ".valueMin", null);
                data.get().set(idx + ".valueMax", null);
                data.get().set(idx + ".median", null);
                data.get().set(idx + ".stock", null);
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

        data.get().set(String.valueOf(idx), null); // todo 이게 안전한가?
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

        double shopBal = 0;

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
                        LangUtil.ccLang.get().getString("ERR.SHOP_LINK_TARGET_ERR"));

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

    public static String GetShopInfoIconMat()
    {
        String infoBtnIconName = DynamicShop.plugin.getConfig().getString("UI.ShopInfoButtonIcon");
        Material mat = Material.getMaterial(infoBtnIconName);
        if (mat == null)
        {
            DynamicShop.plugin.getConfig().set("UI.ShopInfoButtonIcon", "GOLD_BLOCK");
            DynamicShop.plugin.saveConfig();
            infoBtnIconName = "GOLD_BLOCK";
        }
        return infoBtnIconName;
    }

    // 2틱 후 인벤토리 닫기
    public static void closeInventoryWithDelay(Player player)
    {
        //todo 왜 이렇게 만들었을까??? 2틱 딜레이가 필요한 이유가 뭐지?
        Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, player::closeInventory, 2);
    }

    public static void SetToRecommendedValueAll(String shop, Player player)
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
                    if (player != null)
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_RECOMMAND_DATA") + " : " + itemName);
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
        double topPrice = -1;
        int tradeIdx = -1;

        // 접근가능한 상점중 최고가 찾기
        for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
        {
            CustomConfig data = entry.getValue();

            // 권한 없는 상점
            String permission = data.get().getString("Options.permission");
            if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".sell"))
            {
                continue;
            }

            // 표지판 전용 상점, 지역상점, 잡포인트 상점
            if (data.get().contains("Options.flag.localshop") || data.get().contains("Options.flag.signshop") || data.get().contains("Options.flag.jobpoint"))
                continue;

            int sameItemIdx = ShopUtil.findItemFromShop(entry.getKey(), itemStack);

            if (sameItemIdx != -1)
            {
                String tradeType = data.get().getString(sameItemIdx + ".tradeType");
                if (tradeType != null && tradeType.equals("BuyOnly")) continue; // 구매만 가능함

                // 상점에 돈이 없음
                if (ShopUtil.getShopBalance(entry.getKey()) != -1 && ShopUtil.getShopBalance(entry.getKey()) < Calc.calcTotalCost(entry.getKey(), String.valueOf(sameItemIdx), itemStack.getAmount()))
                {
                    continue;
                }

                double value = data.get().getDouble(sameItemIdx + ".value");

                int tax = ConfigUtil.getCurrentTax();
                if (data.get().contains("Options.SalesTax"))
                {
                    tax = data.get().getInt("Options.SalesTax");
                }

                if (topPrice < value - ((value / 100) * tax))
                {
                    topShopName = entry.getKey();
                    topPrice = data.get().getDouble(sameItemIdx + ".value");
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
        for(Map.Entry<String, CustomConfig> entry : shopConfigFiles.entrySet())
        {
            boolean somethingIsChanged = false;

            CustomConfig data = entry.getValue();

            // 인게임 30분마다 실행됨 (500틱)
            randomStockTimer += 1;
            if (randomStockTimer >= Integer.MAX_VALUE)
            {
                randomStockTimer = 0;
            }
            //DynamicShop.console.sendMessage("debug... " + randomStockTimer);

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

                        boolean dir = generator.nextBoolean();
                        int amount = (int)(median * (confSec.getDouble("strength") / 100.0) * generator.nextFloat());
                        if(dir)
                            amount *= -1;

                        stock += amount;
                        if (stock < 2) stock = 2;

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

                        //DynamicShop.console.sendMessage("DEBUG: " + data.get().get(item + ".mat") + " / " + oldMedian + "/" + stock);

                        if (stock == median)
                            continue; // 이미 같으면 스킵

                        if (DynamicShop.plugin.getConfig().getBoolean("Shop.UseLegacyStockStabilization"))
                        {
                            int amount = (int)(median * (confSec2.getDouble("strength") / 100.0));
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
                            int amount = (int)((median - stock) * (confSec2.getDouble("strength") / 100.0));
                            if (amount == 0)
                            {
                                if (generator.nextInt() % 2 == 0)
                                {
                                    amount = (stock > median) ? -1 : 1;
                                }
                            }

                            if (amount == 0)
                                continue;

                            stock += amount;
                        }

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

        // 무작위,안정화에 의한 변화가 없더라도 갱신. 다른 유저의 거래에 의해 변동되었을 수 있음.
        for (Player p : DynamicShop.plugin.getServer().getOnlinePlayers())
        {
            if (UIManager.GetPlayerCurrentUIType(p) == InGameUI.UI_TYPE.ItemTrade)
            {
                String[] temp = DynamicShop.userInteractItem.get(p.getUniqueId()).split("/");
                DynaShopAPI.openItemTradeGui(p, temp[0], temp[1]);
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

            data.get().set("Options.page", 2);
            data.get().set("Options.title", "Main");
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
}
