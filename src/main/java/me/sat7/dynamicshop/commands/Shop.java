package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.files.CustomConfig;
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

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class Shop
{
    private Shop()
    {

    }

    static boolean shopCommand(String[] args, Player player)
    {
        String shopName = "";

        // ds shop (defaultShop)
        if (args.length == 1)
        {
            if (DynamicShop.plugin.getConfig().getBoolean("Command.OpenStartPageInsteadOfDefaultShop"))
            {
                DynaShopAPI.openStartPage(player);
                return true;
            }

            shopName = DynamicShop.plugin.getConfig().getString("Command.DefaultShopName");
        }
        // ds shop shopName
        else if (args.length >= 2)
        {
            shopName = args[1];
        }

        // 그런 이름을 가진 상점이 있는지 확인
        if (!ShopUtil.shopConfigFiles.containsKey(shopName))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
            return true;
        }

        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        // 상점 UI 열기
        if (args.length <= 2)
        {
            //권한 확인
            String s = shopData.get().getString("Options.permission");
            if (s != null && s.length() > 0)
            {
                if (!player.hasPermission(s) && !player.hasPermission(s + ".buy") && !player.hasPermission(s + ".sell"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }
            }

            // 플래그 확인
            ConfigurationSection shopConf = shopData.get().getConfigurationSection("Options");
            if (shopConf.contains("flag.signshop"))
            {
                if (!player.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.SIGN_SHOP_REMOTE_ACCESS"));
                    return true;
                }
            }
            if (shopConf.contains("flag.localshop") && !shopConf.contains("flag.deliverycharge") && shopConf.contains("world") && shopConf.contains("pos1") && shopConf.contains("pos2"))
            {
                boolean outside = !player.getWorld().getName().equals(shopConf.getString("world"));

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
                    outside = true;
                if (!((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2) ||
                        (y2 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y1)))
                    outside = true;
                if (!((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2) ||
                        (z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1)))
                    outside = true;

                if (outside && !player.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.LOCAL_SHOP_REMOTE_ACCESS"));

                    String posString = t("SHOP.SHOP_LOCATION");
                    posString = posString.replace("{x}", n(x1));
                    posString = posString.replace("{y}", n(y1));
                    posString = posString.replace("{z}", n(z1));
                    player.sendMessage(DynamicShop.dsPrefix + posString);
                    return true;
                }
            }
            if (shopConf.contains("shophours") && !player.hasPermission("dshop.admin.shopedit"))
            {
                int curTime = (int) (player.getWorld().getTime()) / 1000 + 6;
                if (curTime > 24) curTime -= 24;

                String[] temp = shopConf.getString("shophours").split("~");

                int open = Integer.parseInt(temp[0]);
                int close = Integer.parseInt(temp[1]);

                if (close > open)
                {
                    if (!(open <= curTime && curTime < close))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return true;
                    }
                } else
                {
                    if (!(open <= curTime || curTime < close))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return true;
                    }
                }
            }

            DynaShopAPI.openShopGui(player, shopName, 1);
            return true;
        }
        // ds shop shopName <add | addhand | edit | editall | maxstock>
        else if (args.length >= 3)
        {
            // ds shop shopName add <item> <value> <median> <stock>
            // ds shop shopName add <item> <value> <min value> <max value> <median> <stock>
            if (args[2].equalsIgnoreCase("add"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }
                // 인자 확인
                Material mat;
                double buyValue;
                double valueMin = 0.01;
                double valueMax = -1;
                int median;
                int stock;
                if (args.length != 7 && args.length != 9)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    if (args.length == 7)
                    {
                        mat = Material.getMaterial(args[3].toUpperCase());
                        buyValue = Double.parseDouble(args[4]);
                        median = Integer.parseInt(args[5]);
                        stock = Integer.parseInt(args[6]);
                    } else
                    {
                        mat = Material.getMaterial(args[3].toUpperCase());
                        buyValue = Double.parseDouble(args[4]);
                        valueMin = Double.parseDouble(args[5]);
                        valueMax = Double.parseDouble(args[6]);
                        median = Integer.parseInt(args[7]);
                        stock = Integer.parseInt(args[8]);

                        // 유효성 검사
                        if (valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.MAX_LOWER_THAN_MIN"));
                            return true;
                        }
                        if (valueMax > 0 && buyValue > valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                        if (valueMin > 0 && buyValue < valueMin)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                    }

                    if (buyValue < 0.01 || median == 0 || stock == 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.VALUE_ZERO"));
                        return true;
                    }
                } catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 금지품목
                if (Material.getMaterial(args[3]) == Material.AIR)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.ITEM_FORBIDDEN"));
                    return true;
                }

                // 상점에서 같은 아이탬 찾기
                ItemStack itemStack;
                try
                {
                    itemStack = new ItemStack(mat);
                } catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_ITEM_NAME"));
                    return true;
                }

                int idx = ShopUtil.findItemFromShop(shopName, itemStack);
                // 상점에 새 아이탬 추가
                if (idx == -1)
                {
                    idx = ShopUtil.findEmptyShopSlot(shopName, 1, true);
                    if (idx == -1)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_EMPTY_SLOT"));
                        return true;
                    } else if (ShopUtil.addItemToShop(shopName, idx, itemStack, buyValue, buyValue, valueMin, valueMax, median, stock)) // 아이탬 추가
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_ADDED"));
                        ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
                    }
                }
                // 기존 아이탬 수정
                else
                {
                    ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock);
                    player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_UPDATED"));
                    ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
                }
            }

            // ds shop shopName addhand <value> <median> <stock>
            // ds shop shopName addhand <value> <min value> <max value> <median> <stock>
            else if (args[2].equalsIgnoreCase("addhand"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }
                // 인자 확인
                double buyValue;
                double valueMin = 0.01;
                double valueMax = -1;
                int median;
                int stock;
                if (args.length != 6 && args.length != 8)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    if (args.length == 6)
                    {
                        buyValue = Double.parseDouble(args[3]);
                        median = Integer.parseInt(args[4]);
                        stock = Integer.parseInt(args[5]);
                    } else
                    {
                        buyValue = Double.parseDouble(args[3]);
                        valueMin = Double.parseDouble(args[4]);
                        valueMax = Double.parseDouble(args[5]);
                        median = Integer.parseInt(args[6]);
                        stock = Integer.parseInt(args[7]);

                        // 유효성 검사
                        if (valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.MAX_LOWER_THAN_MIN"));
                            return true;
                        }
                        if (valueMax > 0 && buyValue > valueMax)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                        if (valueMin > 0 && buyValue < valueMin)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                    }

                    if (buyValue < 0.01 || median == 0 || stock == 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.VALUE_ZERO"));
                        return true;
                    }
                } catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 손에 뭔가 들고있는지 확인
                if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.HAND_EMPTY"));
                    return true;
                }

                // 금지품목
                if (Material.getMaterial(player.getInventory().getItemInMainHand().getType().toString()) == Material.AIR)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.ITEM_FORBIDDEN"));
                    return true;
                }

                // 상점에서 같은 아이탬 찾기
                int idx = ShopUtil.findItemFromShop(shopName, player.getInventory().getItemInMainHand());
                // 상점에 새 아이탬 추가
                if (idx == -1)
                {
                    idx = ShopUtil.findEmptyShopSlot(shopName, 1, true);
                    if (idx == -1)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_EMPTY_SLOT"));
                        return true;
                    } else if (ShopUtil.addItemToShop(shopName, idx, player.getInventory().getItemInMainHand(), buyValue, buyValue, valueMin, valueMax, median, stock)) // 아이탬 추가
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_ADDED"));
                        ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
                    }
                }
                // 기존 아이탬 수정
                else
                {
                    ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock);
                    player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_UPDATED"));
                    ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
                }

                return true;
            }

            // ds shop shopName edit <value> <median> <stock>
            // ds shop shopName edit <value> <min value> <max value> <median> <stock>
            else if (args[2].equalsIgnoreCase("edit"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }
                // 인자 확인
                int idx;
                double buyValue;
                double valueMin = 0.01;
                double valueMax = -1;
                int median;
                int stock;
                int maxStock = -1;
                if (args.length < 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    String[] temp = args[3].split("/");
                    idx = Integer.parseInt(temp[0]);
                    if (!shopData.get().contains(temp[0]))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_ITEM_NAME"));
                        return true;
                    }
                    buyValue = Double.parseDouble(args[4]);

                    // 삭제
                    if (buyValue <= 0)
                    {
                        ShopUtil.removeItemFromShop(shopName, idx);
                        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_DELETED"));
                        return true;
                    } else
                    {
                        if (args.length != 7 && args.length != 9 && args.length != 10)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                            return true;
                        }

                        if (args.length == 7)
                        {
                            median = Integer.parseInt(args[5]);
                            stock = Integer.parseInt(args[6]);
                        } else
                        {
                            valueMin = Integer.parseInt(args[5]);
                            valueMax = Integer.parseInt(args[6]);
                            median = Integer.parseInt(args[7]);
                            stock = Integer.parseInt(args[8]);

                            if (args.length == 10)
                                maxStock = Integer.parseInt(args[9]);
                            if (maxStock < 1)
                                maxStock = -1;

                            // 유효성 검사
                            if (valueMax > 0 && valueMin > 0 && valueMin >= valueMax)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("ERR.MAX_LOWER_THAN_MIN"));
                                return true;
                            }
                            if (valueMax > 0 && buyValue > valueMax)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                                return true;
                            }
                            if (valueMin > 0 && buyValue < valueMin)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                                return true;
                            }
                        }
                    }
                } catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 수정
                ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock, maxStock);
                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_UPDATED"));
                ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
            }

            // ds shop shopname editall <m|s|v> <=|+|-|*|/> <value>
            else if (args[2].equalsIgnoreCase("editall"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }

                String mod;
                float value = 0;
                String dataType;

                if (args.length != 6)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
                try
                {
                    dataType = args[3];
                    if (!dataType.equals("stock") && !dataType.equals("median") && !dataType.equals("value") && !dataType.equals("valueMin") && !dataType.equals("valueMax") && !dataType.equals("maxStock"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    mod = args[4];
                    if (!mod.equals("=") &&
                            !mod.equals("+") && !mod.equals("-") &&
                            !mod.equals("*") && !mod.equals("/"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    if (!args[5].equals("stock") && !args[5].equals("median") && !args[5].equals("value") && !args[5].equals("valueMin") && !args[5].equals("valueMax") && !args[5].equals("maxStock"))
                        value = Float.parseFloat(args[5]);
                } catch (Exception e)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                    return true;
                }

                // 수정
                for (String s : shopData.get().getKeys(false))
                {
                    try
                    {
                        @SuppressWarnings("unused") int i = Integer.parseInt(s); // 의도적으로 넣은 코드임. 숫자가 아니면 건너뛰기 위함.
                        if (!shopData.get().contains(s + ".value")) continue; //장식용임
                    } catch (Exception e)
                    {
                        continue;
                    }

                    switch (args[5])
                    {
                        case "stock":
                            value = shopData.get().getInt(s + ".stock");
                            break;
                        case "median":
                            value = shopData.get().getInt(s + ".median");
                            break;
                        case "value":
                            value = shopData.get().getInt(s + ".value");
                            break;
                        case "valueMin":
                            value = shopData.get().getInt(s + ".valueMin");
                            break;
                        case "valueMax":
                            value = shopData.get().getInt(s + ".valueMax");
                            break;
                        case "maxStock":
                            value = shopData.get().getInt(s + ".maxStock");
                            break;
                    }

                    if (mod.equalsIgnoreCase("="))
                    {
                        shopData.get().set(s + "." + dataType, (int) value);
                    } else if (mod.equalsIgnoreCase("+"))
                    {
                        shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) + value));
                    } else if (mod.equalsIgnoreCase("-"))
                    {
                        shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) - value));
                    } else if (mod.equalsIgnoreCase("/"))
                    {
                        if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock"))
                        {
                            shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) / value));
                        }
                        else
                        {
                            shopData.get().set(s + "." + dataType, shopData.get().getDouble(s + "." + dataType) / value);
                        }
                    } else if (mod.equalsIgnoreCase("*"))
                    {
                        if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock"))
                        {
                            shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) * value));
                        }
                        else
                        {
                            shopData.get().set(s + "." + dataType, shopData.get().getDouble(s + "." + dataType) * value);
                        }
                    }

                    if (shopData.get().getDouble(s + ".valueMin") < 0)
                    {
                        shopData.get().set(s + ".valueMin", null);
                    }
                    if (shopData.get().getDouble(s + ".valueMax") < 0)
                    {
                        shopData.get().set(s + ".valueMax", null);
                    }
                    if (shopData.get().getDouble(s + ".maxStock") < 1)
                    {
                        shopData.get().set(s + ".maxStock", null);
                    }
                }
                shopData.save();
                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_UPDATED"));
            }

            // ds shop shopname permission [<new value>]
            else if (args[2].equalsIgnoreCase("enable"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }

                if (args.length != 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }

                if (args[3].equalsIgnoreCase("true"))
                {
                    shopData.get().set("Options.enable", true);
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + t("SHOP_SETTING.STATE") + ":" + args[3]);
                } else if (args[3].equalsIgnoreCase("false"))
                {
                    shopData.get().set("Options.enable", false);
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + t("SHOP_SETTING.STATE") + ":" + args[3]);
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;

                }
                shopData.save();
            }

            // ds shop shopname permission [<new value>]
            else if (args[2].equalsIgnoreCase("permission"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }

                if (args.length == 3)
                {
                    String s = shopData.get().getConfigurationSection("Options").getString("permission");
                    if (s == null || s.length() == 0) s = t("NULL(OPEN)");
                    player.sendMessage(DynamicShop.dsPrefix + s);
                } else if (args.length > 3)
                {
                    if (args[3].equalsIgnoreCase("true"))
                    {
                        shopData.get().set("Options.permission", "dshop.user.shop." + args[1]);
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + "dshop.user.shop." + args[1]);
                    } else if (args[3].equalsIgnoreCase("false"))
                    {
                        shopData.get().set("Options.permission", "");
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + t("NULL(OPEN)"));
                    } else
                    {
                        shopData.get().set("Options.permission", args[3]);
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + args[3]);
                    }
                    shopData.save();
                }
            }

            // ds shop shopname maxpage [<new value>]
            else if (args[2].equalsIgnoreCase("maxpage"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                } else if (args.length >= 4)
                {
                    int newValue;
                    try
                    {
                        newValue = Integer.parseInt(args[3]);
                    } catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    if (newValue <= 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.VALUE_ZERO"));
                        return true;
                    } else
                    {
                        shopData.get().set("Options.page", newValue);
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + args[3]);
                        shopData.save();
                    }
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname flag <flag> <set|unset>
            else if (args[2].equalsIgnoreCase("flag"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                } else if (args.length >= 5)
                {
                    boolean set;
                    if (args[4].equalsIgnoreCase("set"))
                    {
                        set = true;
                    } else if (args[4].equalsIgnoreCase("unset"))
                    {
                        set = false;
                    } else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                        return true;
                    }

                    if (args[3].equalsIgnoreCase("signshop") ||
                            args[3].equalsIgnoreCase("localshop") ||
                            args[3].equalsIgnoreCase("deliverycharge") ||
                            args[3].equalsIgnoreCase("jobpoint") ||
                            args[3].equalsIgnoreCase("showValueChange") ||
                            args[3].equalsIgnoreCase("hidestock") ||
                            args[3].equalsIgnoreCase("hidepricingtype") ||
                            args[3].equalsIgnoreCase("hideshopbalance") ||
                            args[3].equalsIgnoreCase("showmaxstock") ||
                            args[3].equalsIgnoreCase("hiddenincommand")
                    )
                    {
                        if (set)
                        {
                            if (args[3].equalsIgnoreCase("signshop"))
                            {
                                shopData.get().set("Options.flag.localshop", null);
                                shopData.get().set("Options.flag.deliverycharge", null);
                            }
                            if (args[3].equalsIgnoreCase("localshop"))
                            {
                                shopData.get().set("Options.flag.signshop", null);

                                if(!shopData.get().contains("Options.pos1") || !shopData.get().contains("Options.pos2") || !shopData.get().contains("Options.world"))
                                {
                                    shopData.get().set("Options.pos1", (player.getLocation().getBlockX() - 2) + "_" + (player.getLocation().getBlockY() - 1) + "_" + (player.getLocation().getBlockZ() - 2));
                                    shopData.get().set("Options.pos2", (player.getLocation().getBlockX() + 2) + "_" + (player.getLocation().getBlockY() + 1) + "_" + (player.getLocation().getBlockZ() + 2));
                                    shopData.get().set("Options.world", player.getWorld().getName());
                                }
                            }
                            if (args[3].equalsIgnoreCase("deliverycharge"))
                            {
                                shopData.get().set("Options.flag.signshop", null);

                                shopData.get().set("Options.flag.localshop", "");
                                if(!shopData.get().contains("Options.pos1") || !shopData.get().contains("Options.pos2") || !shopData.get().contains("Options.world"))
                                {
                                    shopData.get().set("Options.pos1", (player.getLocation().getBlockX() - 2) + "_" + (player.getLocation().getBlockY() - 1) + "_" + (player.getLocation().getBlockZ() - 2));
                                    shopData.get().set("Options.pos2", (player.getLocation().getBlockX() + 2) + "_" + (player.getLocation().getBlockY() + 1) + "_" + (player.getLocation().getBlockZ() + 2));
                                    shopData.get().set("Options.world", player.getWorld().getName());
                                }
                            }

                            shopData.get().set("Options.flag." + args[3].toLowerCase(), "");
                        } else
                        {
                            if (args[3].equalsIgnoreCase("localshop"))
                            {
                                shopData.get().set("Options.flag.deliverycharge", null);
                                shopData.get().set("Options.pos1", null);
                                shopData.get().set("Options.pos2", null);
                                shopData.get().set("Options.world", null);
                            }
                            shopData.get().set("Options.flag." + args[3].toLowerCase(), null);
                        }
                        shopData.save();
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + args[3] + ":" + args[4]);
                    } else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                        return true;
                    }
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname position <pos1|pos2|clear>
            else if (args[2].equalsIgnoreCase("position"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                } else if (args.length >= 4)
                {
                    if (args[3].equalsIgnoreCase("pos1"))
                    {
                        shopData.get().set("Options.world", player.getWorld().getName());
                        shopData.get().set("Options.pos1", player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
                        shopData.save();
                        player.sendMessage(DynamicShop.dsPrefix + "p1");
                    } else if (args[3].equalsIgnoreCase("pos2"))
                    {
                        shopData.get().set("Options.world", player.getWorld().getName());
                        shopData.get().set("Options.pos2", player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
                        shopData.save();
                        player.sendMessage(DynamicShop.dsPrefix + "p2");
                    } else if (args[3].equalsIgnoreCase("clear"))
                    {
                        shopData.get().set("Options.world", null);
                        shopData.get().set("Options.pos1", null);
                        shopData.get().set("Options.pos2", null);
                        shopData.save();
                        player.sendMessage(DynamicShop.dsPrefix + "clear");
                    } else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                        return true;
                    }
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname shophours <open> <close>
            else if (args[2].equalsIgnoreCase("shopHours"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                } else if (args.length >= 5)
                {
                    try
                    {
                        int start = Clamp(Integer.parseInt(args[3]),1,24);
                        int end = Clamp(Integer.parseInt(args[4]), 1, 24);

                        if (start == end)
                        {
                            shopData.get().set("Options.shophours", null);
                            shopData.save();

                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + "Open 24 hours");
                        } else
                        {
                            shopData.get().set("Options.shophours", start + "~" + end);
                            shopData.save();

                            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + start + "~" + end);
                        }
                    } catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }
            }

            // ds shop shopname fluctuation <interval> <strength>
            else if (args[2].equalsIgnoreCase("fluctuation"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                } else if (args.length < 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                } else if (args.length == 4)
                {
                    if (args[3].equals("off"))
                    {
                        shopData.get().set("Options.fluctuation", null);
                        shopData.save();
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + "Fluctuation Off");
                    } else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                        return true;
                    }
                } else if (args.length >= 5)
                {
                    int interval;
                    try
                    {
                        interval = Clamp(Integer.parseInt(args[3]), 1, 999);
                    } catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    try
                    {
                        double strength = Double.parseDouble(args[4]);
                        shopData.get().set("Options.fluctuation.interval", interval);
                        shopData.get().set("Options.fluctuation.strength", strength);
                        shopData.save();

                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
                    } catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }
                }
            }

            // ds shop shopname stockStabilizing <interval> <strength>
            else if (args[2].equalsIgnoreCase("stockStabilizing"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                } else if (args.length < 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                } else if (args.length == 4)
                {
                    if (args[3].equals("off"))
                    {
                        shopData.get().set("Options.stockStabilizing", null);
                        shopData.save();
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + "stockStabilizing Off");
                    } else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                        return true;
                    }
                } else if (args.length >= 5)
                {
                    int interval;
                    try
                    {
                        interval = Clamp(Integer.parseInt(args[3]), 1, 999);
                    } catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }

                    try
                    {
                        double strength = Double.parseDouble(args[4]);
                        shopData.get().set("Options.stockStabilizing.interval", interval);
                        shopData.get().set("Options.stockStabilizing.strength", strength);
                        shopData.save();

                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
                    } catch (Exception e)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                        return true;
                    }
                }
            }

            // ds shop shopname account <set | linkto | transfer>
            else if (args[2].equalsIgnoreCase("account"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }

                if (args.length < 5)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }

                CustomConfig targetShopData = ShopUtil.shopConfigFiles.get(args[4]);
                switch (args[3])
                {
                    case "set":
                        try
                        {
                            if (Double.parseDouble(args[4]) < 0)
                            {
                                shopData.get().set("Options.Balance", null);
                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + t("SHOP.SHOP_BAL_INF"));
                            } else
                            {
                                shopData.get().set("Options.Balance", Double.parseDouble(args[4]));
                                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("MESSAGE.CHANGES_APPLIED") + args[4]);
                            }
                            shopData.save();
                        } catch (Exception e)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                            return true;
                        }
                        break;
                    case "linkto":
                        // 그런 상점(타깃) 없음
                        if (!ShopUtil.shopConfigFiles.containsKey(args[4]))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
                            return true;
                        }

                        // 타깃 상점이 연동계좌임
                        if (targetShopData.get().contains("Options.Balance"))
                        {
                            try
                            {
                                // temp 를 직접 사용하지는 않지만 의도적으로 넣은 코드임. 숫자가 아니면 건너뛰기 위함.
                                Double temp = Double.parseDouble(targetShopData.get().getString("Options.Balance"));
                            } catch (Exception e)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_LINK_TARGET_ERR"));
                                return true;
                            }
                        }

                        // 출발상점을 타깃으로 하는 상점이 있음
                        for (CustomConfig tempShopData : ShopUtil.shopConfigFiles.values())
                        {
                            String temp = tempShopData.get().getString("Options.Balance");
                            try
                            {
                                if (temp != null && temp.equals(args[1]))
                                {
                                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NESTED_STRUCTURE"));
                                    return true;
                                }
                            } catch (Exception e)
                            {
                                DynamicShop.console.sendMessage(DynamicShop.dsPrefix + e);
                            }
                        }

                        // 출발 상점과 도착 상점이 같음
                        if (args[1].equals(args[4]))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                            return true;
                        }

                        // 출발 상점과 도착 상점의 통화 유형이 다름
                        if (shopData.get().contains("Options.flag.jobpoint") != targetShopData.get().contains("Options.flag.jobpoint"))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_DIFF_CURRENCY"));
                            return true;
                        }

                        shopData.get().set("Options.Balance", args[4]);
                        shopData.save();
                        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + args[4]);
                        break;
                    case "transfer":
                        //[4] 대상 [5] 금액
                        if (args.length < 6)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                            return true;
                        }

                        double amount;
                        // 마지막 인자가 숫자가 아님
                        try
                        {
                            amount = Double.parseDouble(args[5]);
                        } catch (Exception e)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                            return true;
                        }

                        // 출발 상점이 무한계좌임
                        if (!shopData.get().contains("Options.Balance"))
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_HAS_INF_BAL").replace("{shop}", args[1]));
                            return true;
                        }

                        // 출발 상점에 돈이 부족
                        if (ShopUtil.getShopBalance(args[1]) < amount)
                        {
                            if (shopData.get().contains("Options.flag.jobpoint"))
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.NOT_ENOUGH_POINT").
                                        replace("{bal}", n(ShopUtil.getShopBalance(args[1]))));
                            } else
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.NOT_ENOUGH_MONEY").
                                        replace("{bal}", n(ShopUtil.getShopBalance(args[1]))));
                            }
                            return true;
                        }

                        // 다른 상점으로 송금
                        if (ShopUtil.shopConfigFiles.containsKey(args[4]))
                        {
                            // 도착 상점이 무한계좌임
                            if (!targetShopData.get().contains("Options.Balance"))
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_HAS_INF_BAL").replace("{shop}", args[4]));
                                return true;
                            }

                            // 출발 상점과 도착 상점이 같음
                            if (args[1].equals(args[4]))
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                                return true;
                            }

                            // 출발 상점과 도착 상점의 통화 유형이 다름
                            if (shopData.get().contains("Options.flag.jobpoint") != targetShopData.get().contains("Options.flag.jobpoint"))
                            {
                                player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_DIFF_CURRENCY"));
                                return true;
                            }

                            // 송금.
                            ShopUtil.addShopBalance(args[1], amount * -1);
                            ShopUtil.addShopBalance(args[4], amount);

                            shopData.save();
                            targetShopData.save();

                            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.TRANSFER_SUCCESS"));
                        }
                        // 플레이어에게 송금
                        else
                        {
                            try
                            {
                                Player target = Bukkit.getPlayer(args[4]);

                                if (target == null)
                                {
                                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.PLAYER_NOT_EXIST"));
                                    return true;
                                }

                                if (shopData.get().contains("Options.flag.jobpoint"))
                                {
                                    JobsHook.addJobsPoint(target, amount);
                                    ShopUtil.addShopBalance(args[1], amount * -1);
                                    shopData.save();

                                    player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.TRANSFER_SUCCESS"));
                                } else
                                {
                                    Economy econ = DynamicShop.getEconomy();
                                    EconomyResponse er = econ.depositPlayer(target, amount);

                                    if (er.transactionSuccess())
                                    {
                                        ShopUtil.addShopBalance(args[1], amount * -1);
                                        shopData.save();

                                        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.TRANSFER_SUCCESS"));
                                    } else
                                    {
                                        player.sendMessage(DynamicShop.dsPrefix + "Transfer failed");
                                    }
                                }
                            } catch (Exception e)
                            {
                                player.sendMessage(DynamicShop.dsPrefix + "Transfer failed. /" + e);
                            }
                        }
                        break;
                }
            }

            // ds shop shopname sellbuy <SellOnly | BuyOnly | Clear>
            else if (args[2].equalsIgnoreCase("sellbuy"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }

                if (args.length != 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }

                // 수정
                String temp;
                if (args[3].equalsIgnoreCase("SellOnly"))
                {
                    temp = "SellOnly";
                } else if (args[3].equalsIgnoreCase("BuyOnly"))
                {
                    temp = "BuyOnly";
                } else
                {
                    temp = "SellBuy";
                }

                for (String s : shopData.get().getKeys(false))
                {
                    try
                    {
                        // i를 직접 사용하지는 않지만 의도적으로 넣은 코드임.
                        int i = Integer.parseInt(s);
                        if (!shopData.get().contains(s + ".value")) continue; //장식용임
                    } catch (Exception e)
                    {
                        continue;
                    }

                    if (temp.equalsIgnoreCase("SellBuy"))
                    {
                        shopData.get().set(s + ".tradeType", null);
                    } else
                    {
                        shopData.get().set(s + ".tradeType", temp);
                    }
                }

                shopData.save();
                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + temp);
            }

            // ds shop shopname log <enable | disable | clear>
            else if (args[2].equalsIgnoreCase("log"))
            {
                // 권한 확인
                if (!player.hasPermission("dshop.admin.shopedit"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                    return true;
                }

                if (args.length != 4)
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }

                if (args[3].equalsIgnoreCase("enable"))
                {
                    shopData.get().set("Options.log", true);
                    player.sendMessage(DynamicShop.dsPrefix + shopName + "/" + t("LOG.LOG") + ": " + args[3]);
                } else if (args[3].equalsIgnoreCase("disable"))
                {
                    shopData.get().set("Options.log", null);
                    player.sendMessage(DynamicShop.dsPrefix + shopName + "/" + t("LOG.LOG") + ": " + args[3]);
                } else if (args[3].equalsIgnoreCase("clear"))
                {
                    LogUtil.ccLog.get().set(shopName, null);
                    LogUtil.ccLog.save();
                    player.sendMessage(DynamicShop.dsPrefix + shopName + "/" + LangUtil.ccLang.get().getString("LOG.CLEAR"));
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    return true;
                }

                shopData.save();
            }
            // ds shop shopname setToRecommendedValueAll
            else if (args[2].equalsIgnoreCase("setToRecAll"))
            {
                SetToRecAll.setToRecAll(args, player);
            }
        }
        return false;
    }
}
