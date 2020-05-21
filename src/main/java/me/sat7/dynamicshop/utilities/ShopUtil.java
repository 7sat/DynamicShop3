package me.sat7.dynamicshop.utilities;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;

public final class ShopUtil {
    public static CustomConfig ccShop;

    private ShopUtil() {

    }

    // 상점에서 빈 슬롯 찾기
    public static int findEmptyShopSlot(String shopName)
    {
        ArrayList<Integer> banList = new ArrayList<>();

        for (String s: ccShop.get().getConfigurationSection(shopName).getKeys(false))
        {
            try {
                banList.add(Integer.parseInt(s));
            }
            catch (Exception ignored) { }
        }

        for(int i = 0; i<45* ccShop.get().getInt(shopName+".Options.page"); i++)
        {
            if(!banList.contains(i))
            {
                return i;
            }
        }

        return -1;
    }

    // 상점에서 아이탬타입 찾기
    public static int findItemFromShop(String shopName, ItemStack item)
    {
        for (String s: ccShop.get().getConfigurationSection(shopName).getKeys(false))
        {
            try
            {
                int i = Integer.parseInt(s);
            }
            catch (Exception e)
            {
                continue;
            }

            if(!ccShop.get().contains(shopName+"."+s+".value")) continue; // 장식용임

            if(ccShop.get().getString(shopName+"."+s+".mat").equals(item.getType().toString()))
            {
                String metaStr = ccShop.get().getString(shopName+"."+s+".itemStack");

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
    public static boolean addItemToShop(String shopName, int idx, ItemStack item, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock)
    {
        try
        {
            ccShop.get().set(shopName+"." + idx + ".mat",item.getType().toString());

            if(item.hasItemMeta())
            {
                ccShop.get().set(shopName+"." + idx + ".itemStack",item.getItemMeta());
            }
            else
            {
                ccShop.get().set(shopName+"." + idx + ".itemStack",null);
            }

            if(buyValue > 0)
            {
                ccShop.get().set(shopName+"." + idx + ".value",buyValue);
                if(buyValue == sellValue)
                {
                    ccShop.get().set(shopName+"." + idx + ".value2",null);
                }
                else
                {
                    ccShop.get().set(shopName+"." + idx + ".value2",sellValue);
                }

                if(minValue > 0.01)
                {
                    ccShop.get().set(shopName+"." + idx + ".valueMin",minValue);
                }
                else
                {
                    ccShop.get().set(shopName+"." + idx + ".valueMin",null);
                }

                if(maxValue > 0.01)
                {
                    ccShop.get().set(shopName+"." + idx + ".valueMax",maxValue);
                }
                else
                {
                    ccShop.get().set(shopName+"." + idx + ".valueMax",null);
                }

                ccShop.get().set(shopName+"." + idx + ".median",median);
                ccShop.get().set(shopName+"." + idx + ".stock",stock);
            }
            else
            {
                // idx,null하면 안됨. 존재는 하되 하위 데이터만 없어야함.
                ccShop.get().set(shopName+"." + idx + ".value",null);
                ccShop.get().set(shopName+"." + idx + ".value2",null);
                ccShop.get().set(shopName+"." + idx + ".valueMin",null);
                ccShop.get().set(shopName+"." + idx + ".valueMax",null);
                ccShop.get().set(shopName+"." + idx + ".median",null);
                ccShop.get().set(shopName+"." + idx + ".stock",null);
            }

            ccShop.save();

            return  true;
        }
        catch (Exception e)
        {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " ERR.AddItemToShop.");
            for (StackTraceElement s:e.getStackTrace())
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " " + s.toString());
            }
            return false;
        }
    }

    // 상점 아이탬의 value, median, stock을 수정
    public static void editShopItem(String shopName, int idx, double buyValue, double sellValue, double minValue, double maxValue, int median, int stock)
    {
        ccShop.get().set(shopName+"." + idx + ".value",buyValue);
        if(buyValue == sellValue)
        {
            ccShop.get().set(shopName+"." + idx + ".value2",null);
        }
        else
        {
            ccShop.get().set(shopName+"." + idx + ".value2",sellValue);
        }
        if(minValue > 0.01)
        {
            ccShop.get().set(shopName+"." + idx + ".valueMin",minValue);
        }
        else
        {
            ccShop.get().set(shopName+"." + idx + ".valueMin",null);
        }
        if(maxValue > 0.01)
        {
            ccShop.get().set(shopName+"." + idx + ".valueMax",maxValue);
        }
        else
        {
            ccShop.get().set(shopName+"." + idx + ".valueMax",null);
        }
        ccShop.get().set(shopName+"." + idx + ".median",median);
        ccShop.get().set(shopName+"." + idx + ".stock",stock);
        ccShop.save();
    }

    // 상점에서 아이탬 제거
    public static void removeItemFromShop(String shopName, int idx)
    {
        ccShop.get().set(shopName+"." + idx,null);
        ccShop.save();
    }

    // 상점 페이지 삽입
    public static void insetShopPage(String shopName, int page)
    {
        ConfigurationSection confSec = ccShop.get().getConfigurationSection(shopName);
        confSec.set("Options.page", confSec.getInt("Options.page")+1);

        for (int i = confSec.getInt("Options.page")*45; i>=(page-1) * 45; i--)
        {
            ConfigurationSection temp = confSec.getConfigurationSection(String.valueOf(i));
            confSec.set(String.valueOf(i+45), temp);
            confSec.set(String.valueOf(i),null);
        }

        ccShop.save();
        ccShop.reload();
    }

    // 상점 페이지 삭제
    public static void deleteShopPage(String shopName, int page)
    {
        ConfigurationSection confSec = ccShop.get().getConfigurationSection(shopName);
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

        ccShop.save();
        ccShop.reload();
    }

    // 상점 이름 바꾸기
    public static void renameShop(String shopName, String newName)
    {
        ConfigurationSection old = ccShop.get().getConfigurationSection(shopName);
        ccShop.get().set(shopName,null);
        ccShop.get().set(newName,old);
        ccShop.save();
    }

    // 상점 병합
    public static void mergeShop(String shopA, String shopB)
    {
        ConfigurationSection confA = ccShop.get().getConfigurationSection(shopA);
        ConfigurationSection confB = ccShop.get().getConfigurationSection(shopB);

        int pg1 = confA.getInt("Options.page");
        int pg2 = confB.getInt("Options.page");

        confA.set("Options.page", pg1 + pg2);
        if(confA.contains("Options.Balance") || confB.contains("Options.Balance"))
        {
            double a = getShopBalance(shopA);
            if(a == -1) a = 0;

            double b = 0;
            if(!(confA.getString("Options.Balance").equals(shopB) || confB.getString("Options.Balance").equals(shopA) ))
            {
                b = getShopBalance(shopB);
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

        ccShop.get().set(shopB,null);
        ccShop.save();
        ccShop.reload();
    }

    // 상점의 잔액 확인
    public static double getShopBalance(String shopName)
    {
        // 무한
        if(!ccShop.get().contains(shopName+".Options.Balance")) return -1;

        double shopBal = 0;

        try
        {
            shopBal = Double.parseDouble(ccShop.get().getString(shopName+".Options.Balance")); // 파싱에 실패하면 캐치로 가는 방식.
        }
        // 연동형
        catch (Exception ee)
        {
            String linkedShop = ccShop.get().getString(shopName+".Options.Balance");

            // 그런 상점이 없음.
            if(!ccShop.get().contains(linkedShop))
            {
                ccShop.get().set(shopName+".Options.Balance",null);
                ccShop.save();
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +
                        shopName + ", " + linkedShop + "/ target shop not found");
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + shopName + "/ balance has been reset");
                return -1;
            }

            // 연결 대상이 실제 계좌가 아님.
            try
            {
                if(ccShop.get().contains(linkedShop+".Options.Balance"))
                {
                    double temp = Double.parseDouble(ccShop.get().getString(linkedShop+".Options.Balance"));
                }
                else
                {
                    return -1;
                }
            }
            catch (Exception e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +
                        shopName + ", " + linkedShop + "/ " +
                        LangUtil.ccLang.get().getString("ERR.SHOP_LINK_TARGET_ERR"));

                ccShop.get().set(shopName+".Options.Balance",null);
                ccShop.save();

                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + shopName + "/ balance has been reset");
                return -1;
            }

            shopBal = ccShop.get().getDouble(linkedShop+".Options.Balance");
        }

        return shopBal;
    }

    // 상점의 잔액 수정
    public static void addShopBalance(String shopName, double amount)
    {
        double old = getShopBalance(shopName);
        if(old < 0) return;

        double newValue = old + amount;
        newValue = (Math.round(newValue*100)/100.0);

        try
        {
            Double temp = Double.parseDouble(ccShop.get().getString(shopName+".Options.Balance"));
            ccShop.get().set(shopName+".Options.Balance",newValue);
        }
        // 연동형
        catch (Exception ee)
        {
            String linkedShop = ccShop.get().getString(shopName+".Options.Balance");
            ccShop.get().set(linkedShop+".Options.Balance",newValue);
        }
    }

    // 인벤토리가 ui인지 확인
    public static Boolean checkInvenIsShopUI(Inventory i)
    {
        if(i.getSize() == 54 && i.getItem(53) != null && i.getItem(53).getType().name().contains("SIGN"))
        {
            String temp = ChatColor.stripColor(i.getItem(53).getItemMeta().getDisplayName());
            return temp.length() > 0 && ccShop.get().contains(temp);
        }
        else
        {
            return false;
        }
    }

    // Shop 플러그인에서 데이터 가져오기
    public static void convertDataFromShop(Player player)
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
                    ccShop.get().set(shopname+".Options.page",2);
                    ccShop.get().set(shopname+".Options.permission","");

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

                            ccShop.get().set(shopname+"."+ idx +".mat",m.name());
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
                        if(ccShop.get().contains(shopname+"."+idx+".mat"))
                        {
                            ccShop.get().set(shopname+"."+idx+".value",confSec.getInt("slotdata."+s+".cost"));
                            ccShop.get().set(shopname+"."+idx+".median",10000);
                            ccShop.get().set(shopname+"."+idx+".stock",10000);

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

        ccShop.save();
    }

    // 2틱 후 인벤토리 닫기
    public static void closeInventoryWithDelay(Player player)
    {
        Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, player::closeInventory,2);
    }

    public static void setupShopFile()
    {
        ccShop.setup("Shop",null);
        ccShop.get().options().header("Shop name can not contain formatting codes, '/' and ' '");
        ccShop.get().options().copyHeader(true);

        if(ccShop.get().getKeys(false).size() == 0)
        {
            ccShop.get().set("Main.Options.page",2);
            ccShop.get().set("Main.Options.title","Main");
            ccShop.get().set("Main.Options.lore","");
            ccShop.get().set("Main.Options.permission","");
            ccShop.get().set("Main.0.mat","DIRT");
            ccShop.get().set("Main.0.value",1);
            ccShop.get().set("Main.0.median",10000);
            ccShop.get().set("Main.0.stock",10000);
            ccShop.get().set("Main.1.mat","COBBLESTONE");
            ccShop.get().set("Main.1.value",1.5);
            ccShop.get().set("Main.1.median",10000);
            ccShop.get().set("Main.1.stock",10000);
            ccShop.get().set("OreShop.Options.page",2);
            ccShop.get().set("OreShop.Options.title","OreShop");
            ccShop.get().set("OreShop.Options.lore","");
            ccShop.get().set("OreShop.Options.permission","");
            ccShop.get().set("OreShop.1.mat","DIAMOND");
            ccShop.get().set("OreShop.1.value",3000);
            ccShop.get().set("OreShop.1.median",1000);
            ccShop.get().set("OreShop.1.stock",1000);
        }

        for (String s: ccShop.get().getKeys(false))
        {
            if(!ccShop.get().getConfigurationSection(s).contains("Options"))
            {
                ccShop.get().set(s+".Options.page",2);
                ccShop.get().set(s+".Options.permission","");
            }
        }

        ccShop.get().options().copyDefaults(true);
        ccShop.save();
    }
}
