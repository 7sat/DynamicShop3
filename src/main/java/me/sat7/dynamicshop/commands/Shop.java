package me.sat7.dynamicshop.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.LogUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public final class Shop {
    private Shop() {

    }

    static boolean shopCommand(String[] args, Player player) {
        String shopName = "";

        // ds shop (defaultShop)
        if(args.length == 1)
        {
            if(DynamicShop.plugin.getConfig().getBoolean("OpenStartPageInsteadOfDefaultShop"))
            {
                DynamicShop.ccUser.get().set(player.getUniqueId()+".interactItem","");
                DynaShopAPI.openStartPage(player);
                return true;
            }

            shopName = DynamicShop.plugin.getConfig().getString("DefaultShopName");
        }
        // ds shop shopName
        else if(args.length >= 2)
        {
            shopName = args[1];
        }

        // 그런 이름을 가진 상점이 있는지 확인
        if(!ShopUtil.ccShop.get().contains(shopName))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
            return true;
        }

        // 상점 UI 열기
        if(args.length <= 2)
        {
            //권한 확인
            String s = ShopUtil.ccShop.get().getString(shopName+".Options.permission");
            if(s != null && s.length()>0)
            {
                if(!player.hasPermission(s) && !player.hasPermission(s+".buy") && !player.hasPermission(s+".sell"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
            }

            // 플래그 확인
            ConfigurationSection shopConf = ShopUtil.ccShop.get().getConfigurationSection(shopName+".Options");
            if (shopConf.contains("flag.signshop"))
            {
                if(!player.hasPermission(Constants.REMOTE_ACCESS_PERMISSION))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SIGNSHOP_REMOTE_ACCESS"));
                    return true;
                }
            }
            if (shopConf.contains("flag.localshop") && !shopConf.contains("flag.deliverycharge") && shopConf.contains("world") && shopConf.contains("pos1") && shopConf.contains("pos2"))
            {
                boolean outside = false;
                if(!player.getWorld().getName().equals(shopConf.getString("world"))) outside = true;

                String[] shopPos1 = shopConf.getString("pos1").split("_");
                String[] shopPos2 = shopConf.getString("pos2").split("_");
                int x1 = Integer.parseInt(shopPos1[0]);
                int y1 = Integer.parseInt(shopPos1[1]);
                int z1 = Integer.parseInt(shopPos1[2]);
                int x2 = Integer.parseInt(shopPos2[0]);
                int y2 = Integer.parseInt(shopPos2[1]);
                int z2 = Integer.parseInt(shopPos2[2]);

                if(!((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2)||
                        (x2 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x1))) outside = true;
                if(!((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2) ||
                    (y2 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y1))) outside = true;
                if(!((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2) ||
                        (z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1))) outside = true;

                if(outside && !player.hasPermission(Constants.REMOTE_ACCESS_PERMISSION))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.LOCALSHOP_REMOTE_ACCESS"));
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("POSITION")+"X"+x1+" Y"+y1+" Z"+z1);
                    return true;
                }
            }
            if(shopConf.contains("shophours") && !player.hasPermission("dshop.admin.shopedit"))
            {
                int curTime = (int)(player.getWorld().getTime())/1000 + 6;
                if(curTime>24) curTime -= 24;

                String[] temp = shopConf.getString("shophours").split("~");

                int open = Integer.parseInt(temp[0]);
                int close = Integer.parseInt(temp[1]);

                if(close>open)
                {
                    if(!(open <= curTime && curTime < close))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TIME.SHOP_IS_CLOSED").
                                replace("{time}",open+"").replace("{curTime}",curTime+""));
                        return true;
                    }
                }
                else
                {
                    if(!(open <= curTime || curTime < close))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TIME.SHOP_IS_CLOSED").
                                replace("{time}",open+"").replace("{curTime}",curTime+""));
                        return true;
                    }
                }
            }

            DynamicShop.ccUser.get().set(player.getUniqueId() + ".tmpString","");
            DynamicShop.ccUser.get().set(player.getUniqueId()+".interactItem","");
            DynaShopAPI.openShopGui(player,shopName,1);
            return true;
        }
        // ds shop shopName <add | addhand | edit | editall>
        else if(args.length >= 3)
        {
            // ds shop shopName add <item> <value> <median> <stock>
            // ds shop shopName add <item> <value> <min value> <max value> <median> <stock>
            if(args[2].equalsIgnoreCase("add"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                // 인자 확인
                Material mat;
                double buyValue;
                double valueMin = 0.01;
                double valueMax = -1;
                int median;
                int stock;
                if(args.length != 7 && args.length != 9)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    if(args.length == 7)
                    {
                        mat = Material.getMaterial(args[3].toUpperCase());
                        buyValue = Double.parseDouble(args[4]);
                        median = Integer.parseInt(args[5]);
                        stock = Integer.parseInt(args[6]);
                    }
                    else
                    {
                        mat = Material.getMaterial(args[3].toUpperCase());
                        buyValue = Double.parseDouble(args[4]);
                        valueMin = Double.parseDouble(args[5]);
                        valueMax = Double.parseDouble(args[6]);
                        median = Integer.parseInt(args[7]);
                        stock = Integer.parseInt(args[8]);

                        // 유효성 검사
                        if(valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.MAX_LOWER_THAN_MIN"));
                            return true;
                        }
                        if(valueMax > 0 && buyValue > valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                        if(valueMin > 0 && buyValue < valueMin)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                    }

                    if(buyValue < 0.01 || median == 0 || stock == 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.VALUE_ZERO"));
                        return true;
                    }
                }
                catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 금지품목
                if(Material.getMaterial(args[3]) == Material.AIR)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.ITEM_FORBIDDEN"));
                    return true;
                }

                // 상점에서 같은 아이탬 찾기
                ItemStack itemStack;
                try
                {
                    itemStack = new ItemStack(mat);
                }catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_ITEMNAME"));
                    return true;
                }

                int idx = ShopUtil.findItemFromShop(shopName, itemStack);
                // 상점에 새 아이탬 추가
                if(idx == -1)
                {
                    idx = ShopUtil.findEmptyShopSlot(shopName);
                    if(idx == -1)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_EMPTY_SLOT"));
                        return true;
                    }
                    else if(ShopUtil.addItemToShop(shopName, idx,itemStack,buyValue,buyValue,valueMin,valueMax,median,stock)) // 아이탬 추가
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_ADDED"));
                        ItemsUtil.sendItemInfo(player,shopName,idx,"HELP.ITEM_INFO");
                    }
                }
                // 기존 아이탬 수정
                else
                {
                    ShopUtil.editShopItem(shopName, idx,buyValue,buyValue,valueMin,valueMax,median,stock);
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
                    ItemsUtil.sendItemInfo(player,shopName,idx,"HELP.ITEM_INFO");
                }
            }

            // ds shop shopName addhand <value> <median> <stock>
            // ds shop shopName addhand <value> <min value> <max value> <median> <stock>
            else if(args[2].equalsIgnoreCase("addhand"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                // 인자 확인
                double buyValue;
                double valueMin = 0.01;
                double valueMax = -1;
                int median;
                int stock;
                if(args.length != 6 && args.length != 8)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    if(args.length == 6)
                    {
                        buyValue = Double.parseDouble(args[3]);
                        median = Integer.parseInt(args[4]);
                        stock = Integer.parseInt(args[5]);
                    }
                    else
                    {
                        buyValue = Double.parseDouble(args[3]);
                        valueMin = Double.parseDouble(args[4]);
                        valueMax = Double.parseDouble(args[5]);
                        median = Integer.parseInt(args[6]);
                        stock = Integer.parseInt(args[7]);

                        // 유효성 검사
                        if(valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.MAX_LOWER_THAN_MIN"));
                            return true;
                        }
                        if(valueMax > 0 && buyValue > valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                        if(valueMin > 0 && buyValue < valueMin)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                    }

                    if(buyValue < 0.01 || median == 0 || stock == 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.VALUE_ZERO"));
                        return true;
                    }
                }
                catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 손에 뭔가 들고있는지 확인
                if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.HAND_EMPTY"));
                    return true;
                }

                // 금지품목
                if(Material.getMaterial(player.getInventory().getItemInMainHand().getType().toString()) == Material.AIR)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.ITEM_FORBIDDEN"));
                    return true;
                }

                // 상점에서 같은 아이탬 찾기
                int idx = ShopUtil.findItemFromShop(shopName, player.getInventory().getItemInMainHand());
                // 상점에 새 아이탬 추가
                if(idx == -1)
                {
                    idx = ShopUtil.findEmptyShopSlot(shopName);
                    if(idx == -1)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_EMPTY_SLOT"));
                        return true;
                    }
                    else if(ShopUtil.addItemToShop(shopName, idx,player.getInventory().getItemInMainHand(),buyValue,buyValue,valueMin,valueMax,median,stock)) // 아이탬 추가
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_ADDED"));
                        ItemsUtil.sendItemInfo(player,shopName,idx,"HELP.ITEM_INFO");
                    }
                }
                // 기존 아이탬 수정
                else
                {
                    ShopUtil.editShopItem(shopName, idx,buyValue,buyValue,valueMin,valueMax,median,stock);
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
                    ItemsUtil.sendItemInfo(player,shopName,idx,"HELP.ITEM_INFO");
                }

                return true;
            }

            // ds shop shopName edit <value> <median> <stock>
            // ds shop shopName edit <value> <min value> <max value> <median> <stock>
            else if(args[2].equalsIgnoreCase("edit"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                // 인자 확인
                int idx;
                double buyValue;
                double valueMin = 0.01;
                double valueMax = -1;
                int median;
                int stock;
                if(args.length < 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    String[] temp = args[3].split("/");
                    idx = Integer.parseInt( temp[0] );
                    if(!ShopUtil.ccShop.get().getConfigurationSection(shopName).contains(temp[0]))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_ITEMNAME"));
                        return true;
                    }
                    buyValue = Double.parseDouble(args[4]);

                    // 삭제
                    if(buyValue <= 0)
                    {
                        ShopUtil.removeItemFromShop(shopName, idx);
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_DELETED"));
                        return true;
                    }
                    else
                    {
                        if(args.length != 7 && args.length != 9)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                            return true;
                        }

                        if(args.length == 7)
                        {
                            median = Integer.parseInt(args[5]);
                            stock = Integer.parseInt(args[6]);
                        }
                        else
                        {
                            valueMin = Integer.parseInt(args[5]);
                            valueMax = Integer.parseInt(args[6]);
                            median = Integer.parseInt(args[7]);
                            stock = Integer.parseInt(args[8]);

                            // 유효성 검사
                            if(valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.MAX_LOWER_THAN_MIN"));
                                return true;
                            }
                            if(valueMax > 0 && buyValue > valueMax)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                                return true;
                            }
                            if(valueMin > 0 && buyValue < valueMin)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                                return true;
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 수정
                ShopUtil.editShopItem(shopName, idx,buyValue,buyValue,valueMin,valueMax,median,stock);
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
                ItemsUtil.sendItemInfo(player,shopName,idx,"HELP.ITEM_INFO");
            }

            // ds shop shopname editall <m|s|v> <=|+|-|*|/> <value>
            else if(args[2].equalsIgnoreCase("editall"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                String mod;
                float value = 0;
                String dataType;

                if(args.length != 6)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    dataType = args[3];
                    if(!dataType.equals("stock") && !dataType.equals("median") && !dataType.equals("value") && !dataType.equals("valueMin") && !dataType.equals("valueMax"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    mod = args[4];
                    if(!mod.equals("=") &&
                            !mod.equals("+") && !mod.equals("-") &&
                            !mod.equals("*") && !mod.equals("/") )
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    if(!args[5].equals("stock") && !args[5].equals("median") && !args[5].equals("value") && !args[5].equals("valueMin") && !args[5].equals("valueMax")) value = Float.parseFloat(args[5]);
                }
                catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 수정
                for (String s: ShopUtil.ccShop.get().getConfigurationSection(shopName).getKeys(false))
                {
                    try
                    {
                        int i = Integer.parseInt(s);
                        if(!ShopUtil.ccShop.get().contains(shopName+"."+s+".value")) continue; //장식용임
                    }
                    catch (Exception e)
                    {
                        continue;
                    }

                    if(args[5].equals("stock")) value = ShopUtil.ccShop.get().getInt(shopName+"."+s+".stock");
                    else if(args[5].equals("median")) value = ShopUtil.ccShop.get().getInt(shopName+"."+s+".median");
                    else if(args[5].equals("value")) value = ShopUtil.ccShop.get().getInt(shopName+"."+s+".value");
                    else if(args[5].equals("valueMin")) value = ShopUtil.ccShop.get().getInt(shopName+"."+s+".valueMin");
                    else if(args[5].equals("valueMax")) value = ShopUtil.ccShop.get().getInt(shopName+"."+s+".valueMax");

                    if(mod.equalsIgnoreCase("="))
                    {
                        ShopUtil.ccShop.get().set(shopName+"."+s+"."+dataType, (int)value);
                    }
                    else if(mod.equalsIgnoreCase("+"))
                    {
                        ShopUtil.ccShop.get().set(shopName+"."+s+"."+dataType, (int)(ShopUtil.ccShop.get().getInt(shopName+"."+s+"."+dataType)+value));
                    }
                    else if(mod.equalsIgnoreCase("-"))
                    {
                        ShopUtil.ccShop.get().set(shopName+"."+s+"."+dataType, (int)(ShopUtil.ccShop.get().getInt(shopName+"."+s+"."+dataType)-value));
                    }
                    else if(mod.equalsIgnoreCase("/"))
                    {
                        ShopUtil.ccShop.get().set(shopName+"."+s+"."+dataType, (int)(ShopUtil.ccShop.get().getInt(shopName+"."+s+"."+dataType)/value));
                    }
                    else if(mod.equalsIgnoreCase("*"))
                    {
                        ShopUtil.ccShop.get().set(shopName+"."+s+"."+dataType, (int)(ShopUtil.ccShop.get().getInt(shopName+"."+s+"."+dataType)*value));
                    }

                    if(ShopUtil.ccShop.get().getDouble(shopName + "."+s+".valueMin")<0)
                    {
                        ShopUtil.ccShop.get().set(shopName + "."+s+".valueMin",null);
                    }
                    if(ShopUtil.ccShop.get().getDouble(shopName + "."+s+".valueMax")<0)
                    {
                        ShopUtil.ccShop.get().set(shopName + "."+s+".valueMax",null);
                    }
                }
                ShopUtil.ccShop.save();
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
            }

            // ds shop shopname permission [<new value>]
            else if(args[2].equalsIgnoreCase("permission"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                if(args.length == 3)
                {
                    String s = ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options").getString("permission");
                    if(s == null || s.length() == 0) s = LangUtil.ccLang.get().getString("NULL(OPEN)");
                    player.sendMessage(DynamicShop.dsPrefix+s);
                }
                else if(args.length>3)
                {
                    if(args[3].equalsIgnoreCase("true"))
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.permission","dshop.user.shop."+args[1]);
                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "dshop.user.shop."+args[1]);
                    }
                    else if(args[3].equalsIgnoreCase("false"))
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.permission","");
                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + LangUtil.ccLang.get().getString("NULL(OPEN)"));
                    }
                    else
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.permission",args[3]);
                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + args[3]);
                    }
                    ShopUtil.ccShop.save();
                }
            }

            // ds shop shopname maxpage [<new value>]
            else if(args[2].equalsIgnoreCase("maxpage"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                else if(args.length>=4)
                {
                    int newValue;
                    try
                    {
                        newValue = Integer.parseInt(args[3]);
                    }catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    if(newValue<=0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.VALUE_ZERO"));
                        return true;
                    }
                    else
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.page",newValue);
                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + args[3]);
                        ShopUtil.ccShop.save();
                    }
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname flag <flag> <set|unset>
            else if(args[2].equalsIgnoreCase("flag"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                else if(args.length>=5)
                {
                    boolean set;
                    if(args[4].equalsIgnoreCase("set"))
                    {
                        set = true;
                    }
                    else if(args[4].equalsIgnoreCase("unset"))
                    {
                        set = false;
                    }
                    else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }

                    if(args[3].equalsIgnoreCase("signshop") ||
                            args[3].equalsIgnoreCase("localshop") ||
                            args[3].equalsIgnoreCase("deliverycharge")||
                            args[3].equalsIgnoreCase("jobpoint"))
                    {
                        if(set)
                        {
                            if(args[3].equalsIgnoreCase("signshop"))
                            {
                                ShopUtil.ccShop.get().set(args[1]+".Options.flag.localshop",null);
                                ShopUtil.ccShop.get().set(args[1]+".Options.flag.deliverycharge",null);
                            }
                            if(args[3].equalsIgnoreCase("localshop"))
                            {
                                ShopUtil.ccShop.get().set(args[1]+".Options.flag.signshop",null);
                            }
                            if(args[3].equalsIgnoreCase("deliverycharge"))
                            {
                                ShopUtil.ccShop.get().set(args[1]+".Options.flag.localshop","");
                                ShopUtil.ccShop.get().set(args[1]+".Options.flag.signshop",null);
                            }

                            ShopUtil.ccShop.get().set(args[1]+".Options.flag."+args[3].toLowerCase(),"");
                        }
                        else
                        {
                            if(args[3].equalsIgnoreCase("localshop"))
                            {
                                ShopUtil.ccShop.get().set(args[1]+".Options.flag.deliverycharge",null);
                            }
                            ShopUtil.ccShop.get().set(args[1]+".Options.flag."+args[3].toLowerCase(),null);
                        }
                        ShopUtil.ccShop.save();
                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + args[3] + ":" + args[4]);
                    }
                    else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname position <pos1|pos2|clear>
            else if(args[2].equalsIgnoreCase("position"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                else if(args.length>=4)
                {
                    if(args[3].equalsIgnoreCase("pos1"))
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.world",player.getWorld().getName());
                        ShopUtil.ccShop.get().set(args[1]+".Options.pos1",player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
                        ShopUtil.ccShop.save();
                        player.sendMessage(DynamicShop.dsPrefix + "p1");
                    }
                    else if(args[3].equalsIgnoreCase("pos2"))
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.world",player.getWorld().getName());
                        ShopUtil.ccShop.get().set(args[1]+".Options.pos2",player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
                        ShopUtil.ccShop.save();
                        player.sendMessage(DynamicShop.dsPrefix + "p2");
                    }
                    else if(args[3].equalsIgnoreCase("clear"))
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.world",null);
                        ShopUtil.ccShop.get().set(args[1]+".Options.pos1",null);
                        ShopUtil.ccShop.get().set(args[1]+".Options.pos2",null);
                        ShopUtil.ccShop.save();
                        player.sendMessage(DynamicShop.dsPrefix + "clear");
                    }
                    else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname shophours <open> <close>
            else if(args[2].equalsIgnoreCase("shopHours"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                else if(args.length>=5)
                {
                    try
                    {
                        Integer start = Integer.parseInt(args[3]);
                        int end = Integer.parseInt(args[4]);
                        if(start > 24) start = 24;
                        else if(start < 1) start = 1;
                        if(end > 24) end = 24;
                        else if(end < 1) end = 1;

                        if(start.equals(end))
                        {
                            ShopUtil.ccShop.get().set(args[1]+".Options.shophours",null);
                            ShopUtil.ccShop.save();

                            player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Open 24 hours");
                        }
                        else
                        {
                            ShopUtil.ccShop.get().set(args[1]+".Options.shophours",start+"~"+end);
                            ShopUtil.ccShop.save();

                            player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + start + "~" + end);
                        }
                    }
                    catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname fluctuation <interval> <strength>
            else if(args[2].equalsIgnoreCase("fluctuation"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                else if (args.length == 4)
                {
                    if(args[3].equals("off"))
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.fluctuation",null);
                        ShopUtil.ccShop.save();
                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Fluctuation Off");
                    }
                    else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }
                }
                else if(args.length>=5)
                {
                    int interval;
                    if(args[3].equals("30m"))
                    {
                        interval = 1;
                    }
                    else if(args[3].equals("1h"))
                    {
                        interval = 2;
                    }
                    else if(args[3].equals("2h"))
                    {
                        interval = 4;
                    }
                    else if(args[3].equals("4h"))
                    {
                        interval = 8;
                    }
                    else if(args[3].equals("12h"))
                    {
                        interval = 24;
                    }
                    else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }

                    try
                    {
                        double strength = Double.parseDouble(args[4]);
                        ShopUtil.ccShop.get().set(args[1]+".Options.fluctuation.interval",interval);
                        ShopUtil.ccShop.get().set(args[1]+".Options.fluctuation.strength",strength);
                        ShopUtil.ccShop.save();

                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
                    }
                    catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }
                }
            }

            // ds shop shopname stockStabilizing <interval> <strength>
            else if(args[2].equalsIgnoreCase("stockStabilizing"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }
                else if (args.length == 4)
                {
                    if(args[3].equals("off"))
                    {
                        ShopUtil.ccShop.get().set(args[1]+".Options.stockStabilizing",null);
                        ShopUtil.ccShop.save();
                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "stockStabilizing Off");
                    }
                    else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }
                }
                else if(args.length>=5)
                {
                    int interval;
                    if(args[3].equals("30m"))
                    {
                        interval = 1;
                    }
                    else if(args[3].equals("1h"))
                    {
                        interval = 2;
                    }
                    else if(args[3].equals("2h"))
                    {
                        interval = 4;
                    }
                    else if(args[3].equals("4h"))
                    {
                        interval = 8;
                    }
                    else if(args[3].equals("12h"))
                    {
                        interval = 24;
                    }
                    else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }

                    try
                    {
                        double strength = Double.parseDouble(args[4]);
                        ShopUtil.ccShop.get().set(args[1]+".Options.stockStabilizing.interval",interval);
                        ShopUtil.ccShop.get().set(args[1]+".Options.stockStabilizing.strength",strength);
                        ShopUtil.ccShop.save();

                        player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
                    }
                    catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }
                }
            }

            // ds shop shopname account <set | linkto | transfer>
            else if(args[2].equalsIgnoreCase("account"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                if(args.length < 5)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }

                if(args[3].equals("set"))
                {
                    try
                    {
                        if(Double.parseDouble(args[4]) < 0)
                        {
                            ShopUtil.ccShop.get().set(args[1]+".Options.Balance",null);
                            player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + LangUtil.ccLang.get().getString("SHOP_BAL_INF"));
                        }
                        else
                        {
                            ShopUtil.ccShop.get().set(args[1]+".Options.Balance",Double.parseDouble(args[4]));
                            player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + args[4]);
                        }
                        ShopUtil.ccShop.save();
                    }
                    catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }
                }
                else if(args[3].equals("linkto"))
                {
                    // 그런 상점(타깃) 없음
                    if(!ShopUtil.ccShop.get().contains(args[4]))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
                        return true;
                    }

                    // 타깃 상점이 연동계좌임
                    if(ShopUtil.ccShop.get().contains(args[4]+".Options.Balance"))
                    {
                        try{
                            Double temp = Double.parseDouble(ShopUtil.ccShop.get().getString(args[4]+".Options.Balance"));
                        }catch (Exception e)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_LINK_TARGET_ERR"));
                            return true;
                        }
                    }

                    // 출발상점을 타깃으로 하는 상점이 있음
                    for(String s: ShopUtil.ccShop.get().getKeys(false))
                    {
                        String temp = ShopUtil.ccShop.get().getString(s+".Options.Balance");
                        try
                        {
                            if(temp != null && temp.equals(args[1]))
                            {
                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NESTED_STRUCTURE"));
                                return true;
                            }
                        }catch (Exception e){
                            DynamicShop.console.sendMessage(DynamicShop.dsPrefix + e);
                        }
                    }

                    // 출발 상점과 도착 상점이 같음
                    if(args[1].equals(args[4]))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }

                    // 출발 상점과 도착 상점의 통화 유형이 다름
                    if(ShopUtil.ccShop.get().contains(args[1]+".Options.flag.jobpoint") != ShopUtil.ccShop.get().contains(args[4]+".Options.flag.jobpoint"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_DIFF_CURRENCY"));
                        return true;
                    }

                    ShopUtil.ccShop.get().set(args[1]+".Options.Balance", args[4]);
                    ShopUtil.ccShop.save();
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + args[4]);
                }
                else if(args[3].equals("transfer"))
                {
                    //[4] 대상 [5] 금액
                    if(args.length < 6)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return true;
                    }

                    double amount = 0;
                    // 마지막 인자가 숫자가 아님
                    try {
                        amount = Double.parseDouble(args[5]);
                    }catch (Exception e){
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    // 출발 상점이 무한계좌임
                    if(!ShopUtil.ccShop.get().contains(args[1]+".Options.Balance"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_HAS_INF_BAL").replace("{shop}",args[1]));
                        return true;
                    }

                    // 출발 상점에 돈이 부족
                    if(ShopUtil.getShopBalance(args[1]) < amount)
                    {
                        if(ShopUtil.ccShop.get().contains(args[1]+".Options.flag.jobpoint"))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("NOT_ENOUGH_POINT").
                                    replace("{bal}", DynaShopAPI.df.format(ShopUtil.getShopBalance(args[1]))));
                        }
                        else
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("NOT_ENOUGH_MONEY").
                                    replace("{bal}",DynaShopAPI.df.format(ShopUtil.getShopBalance(args[1]))));
                        }
                        return true;
                    }

                    // 다른 상점으로 송금
                    if(ShopUtil.ccShop.get().contains(args[4]))
                    {
                        // 도착 상점이 무한계좌임
                        if(!ShopUtil.ccShop.get().contains(args[4]+".Options.Balance"))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_HAS_INF_BAL").replace("{shop}",args[4]));
                            return true;
                        }

                        // 출발 상점과 도착 상점이 같음
                        if(args[1].equals(args[4]))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                            return true;
                        }

                        // 출발 상점과 도착 상점의 통화 유형이 다름
                        if(ShopUtil.ccShop.get().contains(args[1]+".Options.flag.jobpoint") != ShopUtil.ccShop.get().contains(args[4]+".Options.flag.jobpoint"))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_DIFF_CURRENCY"));
                            return true;
                        }

                        // 송금.
                        ShopUtil.addShopBalance(args[1],amount * -1);
                        ShopUtil.addShopBalance(args[4],amount);
                        ShopUtil.ccShop.save();
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TRANSFER_SUCCESS"));
                    }
                    // 플레이어에게 송금
                    else
                    {
                        try
                        {
                            Player target = Bukkit.getPlayer(args[4]);

                            if(target == null)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.PLAYER_NOT_EXIST"));
                                return true;
                            }

                            if(ShopUtil.ccShop.get().contains(args[1]+".Options.flag.jobpoint"))
                            {
                                JobsHook.addJobsPoint(target,amount);
                                ShopUtil.addShopBalance(args[1],amount * -1);
                                ShopUtil.ccShop.save();

                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TRANSFER_SUCCESS"));
                            }
                            else
                            {
                                Economy econ = DynamicShop.getEconomy();
                                EconomyResponse er = econ.depositPlayer(target, amount);

                                if(er.transactionSuccess())
                                {
                                    ShopUtil.addShopBalance(args[1],amount * -1);
                                    ShopUtil.ccShop.save();

                                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TRANSFER_SUCCESS"));
                                }
                                else
                                {
                                    player.sendMessage(DynamicShop.dsPrefix + "Transfer failed");
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + "Transfer failed. /" + e);
                        }
                    }
                }
            }

            // ds shop shopname hideStock <true | false>
            else if(args[2].equalsIgnoreCase("hideStock"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                if(args.length != 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }

                if(args[3].equals("true"))
                {
                    ShopUtil.ccShop.get().set(args[1]+".Options.hideStock",true);
                    player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hideStock true");
                }
                else
                {
                    ShopUtil.ccShop.get().set(args[1]+".Options.hideStock",null);
                    player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hideStock false");
                }
                ShopUtil.ccShop.save();
            }

            // ds shop shopname hidePricingType <true | false>
            else if(args[2].equalsIgnoreCase("hidePricingType"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                if(args.length != 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }

                if(args[3].equals("true"))
                {
                    ShopUtil.ccShop.get().set(args[1]+".Options.hidePricingType",true);
                    player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hidePricingType true");
                }
                else
                {
                    ShopUtil.ccShop.get().set(args[1]+".Options.hidePricingType",null);
                    player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hidePricingType false");
                }
                ShopUtil.ccShop.save();
            }

            // ds shop shopname sellbuy <SellOnly | BuyOnly | Clear>
            else if(args[2].equalsIgnoreCase("sellbuy"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                if(args.length != 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }

                // 수정
                String temp = "";
                if(args[3].equalsIgnoreCase("SellOnly"))
                {
                    temp = "SellOnly";
                }
                else if(args[3].equalsIgnoreCase("BuyOnly"))
                {
                    temp = "BuyOnly";
                }
                else
                {
                    temp = "SellBuy";
                }

                for (String s: ShopUtil.ccShop.get().getConfigurationSection(shopName).getKeys(false))
                {
                    try
                    {
                        int i = Integer.parseInt(s);
                        if(!ShopUtil.ccShop.get().contains(shopName+"."+s+".value")) continue; //장식용임
                    }
                    catch (Exception e)
                    {
                        continue;
                    }

                    if(temp.equalsIgnoreCase("SellBuy"))
                    {
                        ShopUtil.ccShop.get().set(shopName+"."+s+".tradeType", null);
                    }
                    else
                    {
                        ShopUtil.ccShop.get().set(shopName+"."+s+".tradeType", temp);
                    }
                }

                ShopUtil.ccShop.save();
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + temp);
            }

            // ds shop shopname log <enable | disable | clear>
            else if(args[2].equalsIgnoreCase("log"))
            {
                // 권한 확인
                if(!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return true;
                }

                if(args.length != 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }

                if(args[3].equalsIgnoreCase("enable"))
                {
                    ShopUtil.ccShop.get().set(shopName+".Options.log",true);
                    player.sendMessage(DynamicShop.dsPrefix + shopName+"/"+ LangUtil.ccLang.get().getString("LOG.LOG") + ": " + args[3]);
                }
                else if(args[3].equalsIgnoreCase("disable"))
                {
                    ShopUtil.ccShop.get().set(shopName+".Options.log",null);
                    player.sendMessage(DynamicShop.dsPrefix + shopName+"/"+ LangUtil.ccLang.get().getString("LOG.LOG") + ": " + args[3]);
                }
                else if(args[3].equalsIgnoreCase("clear"))
                {
                    LogUtil.ccLog.get().set(shopName,null);
                    LogUtil.ccLog.save();
                    player.sendMessage(DynamicShop.dsPrefix + shopName+"/"+ LangUtil.ccLang.get().getString("LOG.CLEAR"));
                }
                else
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return true;
                }

                ShopUtil.ccShop.save();
            }
        }
        return false;
    }
}
