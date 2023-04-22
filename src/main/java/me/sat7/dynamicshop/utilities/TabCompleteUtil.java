package me.sat7.dynamicshop.utilities;

import java.util.*;
import java.util.stream.Collectors;

import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.Help;
import me.sat7.dynamicshop.constants.Constants;

import static me.sat7.dynamicshop.constants.Constants.*;

public final class TabCompleteUtil
{
    private TabCompleteUtil()
    {

    }

    public static final ArrayList<String> temp = new ArrayList<>();
    public static final ArrayList<String> autoCompleteList = new ArrayList<>();

    public static List<String> onTabCompleteBody(DynamicShop dynamicShop, CommandSender sender, Command cmd, String[] args)
    {
        if (!(sender instanceof Player)) return null;

        Player p = (Player) sender;
        UUID uuid = p.getUniqueId();

        try
        {
            temp.clear();
            autoCompleteList.clear();

            if (cmd.getName().equalsIgnoreCase("sell") && args.length == 1)
            {
                if (sender.hasPermission(P_SELL))
                {
                    temp.add("hand");
                    temp.add("handall");
                    temp.add("all");
                    AddToAutoCompleteIfValid(args[0]);
                }

                return autoCompleteList;
            }
            else if (cmd.getName().equalsIgnoreCase("shop") && args.length == 1)
            {
                if (!ConfigUtil.GetUseShopCommand()) return autoCompleteList;

                for (Map.Entry<String, CustomConfig> entry : ShopUtil.shopConfigFiles.entrySet())
                {
                    ConfigurationSection options = entry.getValue().get().getConfigurationSection("Options");

                    if (options.contains("flag.signshop") && !sender.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                        continue;

                    if (options.contains("flag.hiddenincommand") && !sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        continue;

                    String permission = options.getString("permission", "");
                    if (permission.isEmpty()
                            || !ConfigUtil.GetPermissionCheckWhenCreatingAShopList()
                            || p.hasPermission(permission))
                        temp.add(entry.getKey());
                }

                AddToAutoCompleteIfValid(args[0]);
                return autoCompleteList;
            } else if (cmd.getName().equalsIgnoreCase("DynamicShop"))
            {
                if (args.length == 1)
                {
                    Help.showHelp("main", (Player) sender, args);

                    temp.add("shop");
                    temp.add("qsell");
                    if (sender.hasPermission(P_ADMIN_CREATE_SHOP)) temp.add("createshop");
                    if (sender.hasPermission(P_ADMIN_DELETE_SHOP)) temp.add("deleteshop");
                    if (sender.hasPermission(P_ADMIN_MERGE_SHOP)) temp.add("mergeshop");
                    if (sender.hasPermission(P_ADMIN_RENAME_SHOP)) temp.add("renameshop");
                    if (sender.hasPermission(P_ADMIN_COPY_SHOP)) temp.add("copyshop");
                    if (sender.hasPermission(P_ADMIN_OPEN_SHOP)) temp.add("openshop");
                    if (sender.hasPermission(P_ADMIN_SET_TAX)) temp.add("settax");
                    if (sender.hasPermission(P_ADMIN_SET_TAX)) temp.add("settax temp");
                    if (sender.hasPermission(P_ADMIN_SET_DEFAULT_SHOP)) temp.add("setdefaultshop");
                    if (sender.hasPermission(P_ADMIN_DELETE_OLD_USER)) temp.add("deleteOldUser");
                    if (sender.hasPermission(P_ADMIN_RELOAD)) temp.add("reload");
                    if (sender.hasPermission(P_ADMIN_ITEM_INFO)) temp.add("iteminfo");
                    temp.add("cmdHelp");

                    AddToAutoCompleteIfValid(args[0]);
                } else if (args.length >= 2 && args[0].equals("shop"))
                {
                    CustomConfig data = ShopUtil.shopConfigFiles.get(args[1]);

                    if (args.length == 2)
                    {
                        Help.showHelp("shop", (Player) sender, args);

                        for (Map.Entry<String, CustomConfig> entry : ShopUtil.shopConfigFiles.entrySet())
                        {
                            ConfigurationSection options = entry.getValue().get().getConfigurationSection("Options");

                            if (options == null)
                                continue;

                            if (options.contains("flag") && options.getConfigurationSection("flag").contains("signshop") && !sender.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                                continue;

                            if (options.contains("flag.hiddenincommand") && !sender.hasPermission(P_ADMIN_SHOP_EDIT))
                                continue;

                            String permission = options.getString("permission", "");
                            if (permission.isEmpty()
                                    || !ConfigUtil.GetPermissionCheckWhenCreatingAShopList()
                                    || p.hasPermission(permission))
                                temp.add(entry.getKey());
                        }

                        AddToAutoCompleteIfValid(args[1]);
                    } else if (args.length >= 3 && (!ShopUtil.shopConfigFiles.containsKey(args[1]) || args[1].length() == 0))
                    {
                        return null;
                    } else if (args.length == 3)
                    {
                        if (sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            temp.add("enable");
                            temp.add("add");
                            temp.add("addhand");
                            temp.add("edit");
                            temp.add("editall");
                            temp.add("setToRecAll");
                            temp.add("permission");
                            temp.add("maxpage");
                            temp.add("currency");
                            temp.add("flag");
                            temp.add("position");
                            temp.add("shophours");
                            temp.add("fluctuation");
                            temp.add("stockStabilizing");
                            temp.add("command");
                            temp.add("account");
                            temp.add("sellbuy");
                            temp.add("log");
                            temp.add("resetTradingVolume");
                            temp.add("background");
                        }

                        AddToAutoCompleteIfValid(args[2]);
                    } else if (args.length >= 4)
                    {
                        if (args[2].equalsIgnoreCase("enable") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("enable", (Player) sender, args);

                            if (args.length == 4)
                            {
                                temp.add("true");
                                temp.add("false");
                            }

                            AddToAutoCompleteIfValid(args[3]);
                        } else if (args[2].equalsIgnoreCase("addhand") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("add_hand", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("add") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                Help.showHelp("add", (Player) sender, args);

                                for (Material m : Material.values())
                                {
                                    temp.add(m.name());
                                }

                                AddToAutoCompleteIfValid(args[3]);
                            } else if (args.length == 5)
                            {
                                String mat = args[3].toUpperCase();
                                String userTempStr = UserUtil.userTempData.get(uuid);

                                if (!(userTempStr.contains("add") && userTempStr.length() > 3))
                                {
                                    if (Material.matchMaterial(mat) != null)
                                    {
                                        Help.showHelp("add" + args[3], (Player) sender, args);
                                    }
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("edit") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                Help.showHelp("edit", (Player) sender, args);

                                for (String s : data.get().getKeys(false))
                                {
                                    try
                                    {
                                        int i = Integer.parseInt(s);
                                        if (!data.get().contains(s + ".value"))
                                            continue; // 장식용임
                                        temp.add(s + "/" + data.get().getString(s + ".mat"));
                                    } catch (Exception ignored)
                                    {
                                    }
                                }

                                AddToAutoCompleteIfValid(args[3]);
                            } else if (args.length == 5)
                            {
                                String mat = args[3];
                                mat = mat.substring(mat.indexOf("/") + 1);
                                mat = mat.toUpperCase();

                                if (Material.matchMaterial(mat) != null)
                                {
                                    Help.showHelp("edit" + mat, (Player) sender, args);
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("editall") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("edit_all", (Player) sender, args);

                            if (args.length == 4)
                            {
                                temp.add("purchaseValue");
                                temp.add("salesValue");
                                temp.add("valueMin");
                                temp.add("valueMax");
                                temp.add("stock");
                                temp.add("median");
                                temp.add("maxStock");
                                temp.add("discount");

                                AddToAutoCompleteIfValid(args[3]);
                            } else if (args.length == 5)
                            {
                                temp.add("=");
                                temp.add("+");
                                temp.add("-");
                                temp.add("/");
                                temp.add("*");

                                AddToAutoCompleteIfValid(args[4]);
                            } else if (args.length == 6)
                            {
                                if (args[4].equals("=") && !args[3].equals("discount"))
                                {
                                    temp.add("purchaseValue");
                                    temp.add("salesValue");
                                    temp.add("valueMin");
                                    temp.add("valueMax");
                                    temp.add("stock");
                                    temp.add("median");
                                    temp.add("maxStock");
                                    temp.add("discount");

                                    AddToAutoCompleteIfValid(args[5]);
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("setToRecAll") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("set_to_rec_all", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("permission") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("permission", (Player) sender, args);

                            if (args.length >= 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                AddToAutoCompleteIfValid(args[3]);
                            }
                        } else if (args[2].equalsIgnoreCase("maxpage") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("max_page", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("currency") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                temp.add("vault");
                                temp.add("exp");
                                temp.add("jobpoint");
                                temp.add("playerpoint");
                                AddToAutoCompleteIfValid(args[3]);
                            }

                            Help.showHelp("currency", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("flag") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                temp.add("signShop");
                                temp.add("localShop");
                                temp.add("deliveryCharge");
                                temp.add("showValueChange");
                                temp.add("hideStock");
                                temp.add("hidePricingType");
                                temp.add("hideShopBalance");
                                temp.add("showMaxStock");
                                temp.add("hiddenInCommand");
                                temp.add("integerOnly");

                                AddToAutoCompleteIfValid(args[3]);
                            } else if (args.length > 4)
                            {
                                temp.add("set");
                                temp.add("unset");

                                AddToAutoCompleteIfValid(args[4]);
                            }

                            Help.showHelp("flag", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("position") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length >= 4)
                            {
                                temp.add("pos1");
                                temp.add("pos2");
                                temp.add("clear");

                                AddToAutoCompleteIfValid(args[3]);
                            }

                            Help.showHelp("position", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("shophours") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("shophours", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("fluctuation") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("fluctuation", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("stockStabilizing") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            Help.showHelp("stock_stabilizing", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("command") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                temp.add("sell");
                                temp.add("buy");
                                temp.add("active");

                                AddToAutoCompleteIfValid(args[3]);
                            }
                            else if (args.length == 5 && args[3].equalsIgnoreCase("active"))
                            {
                                temp.add("true");
                                temp.add("false");

                                AddToAutoCompleteIfValid(args[4]);
                            }
                            else if (args[3].equalsIgnoreCase("sell") || args[3].equalsIgnoreCase("buy"))
                            {
                                if(args.length == 5)
                                {
                                    temp.add("add");
                                    temp.add("delete");

                                    AddToAutoCompleteIfValid(args[4]);
                                }
                            }

                            Help.showHelp("command", (Player) sender, args);
                        } else if (args[2].equalsIgnoreCase("account") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                temp.add("set");
                                temp.add("linkto");
                                temp.add("transfer");

                                AddToAutoCompleteIfValid(args[3]);

                                Help.showHelp("account", (Player) sender, args);
                            } else if (args.length == 5)
                            {
                                if (args[3].equals("linkto") || args[3].equals("transfer"))
                                {
                                    temp.addAll(ShopUtil.shopConfigFiles.keySet());
                                }

                                switch (args[3])
                                {
                                    case "set":
                                        Help.showHelp("account_set", (Player) sender, args);
                                        break;
                                    case "transfer":
                                        Help.showHelp("account_transfer", (Player) sender, args);

                                        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers())
                                        {
                                            temp.add(onlinePlayer.getName());
                                        }
                                        break;
                                    case "linkto":
                                        Help.showHelp("account_link_to", (Player) sender, args);
                                        break;
                                }

                                AddToAutoCompleteIfValid(args[4]);
                            }
                        } else if (args[2].equalsIgnoreCase("sellbuy") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                temp.add("sellOnly");
                                temp.add("buyOnly");
                                temp.add("clear");

                                AddToAutoCompleteIfValid(args[3]);

                                Help.showHelp("sellbuy", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("log") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                temp.add("enable");
                                temp.add("disable");
                                temp.add("clear");
                                temp.add("printToConsole");
                                temp.add("printToAdmin");

                                AddToAutoCompleteIfValid(args[3]);

                                Help.showHelp("log", (Player) sender, args);
                            }
                            else if (args.length == 5)
                            {
                                temp.add("on");
                                temp.add("off");

                                AddToAutoCompleteIfValid(args[4]);
                            }
                        } else if (args[2].equalsIgnoreCase("resetTradingVolume") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                for (OfflinePlayer players : Bukkit.getServer().getOfflinePlayers())
                                {
                                    temp.add(players.getName());
                                }

                                AddToAutoCompleteIfValid(args[3]);

                                Help.showHelp("resetTradingVolume", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("background") && sender.hasPermission(P_ADMIN_SHOP_EDIT))
                        {
                            if (args.length == 4)
                            {
                                temp.add("clear");
                                temp.add("black");
                                temp.add("gray");
                                temp.add("light_gray");
                                temp.add("white");
                                temp.add("cyan");
                                temp.add("light_blue");
                                temp.add("blue");
                                temp.add("brown");
                                temp.add("green");
                                temp.add("lime");
                                temp.add("yellow");
                                temp.add("orange");
                                temp.add("pink");
                                temp.add("magenta");
                                temp.add("purple");
                                temp.add("red");
                                AddToAutoCompleteIfValid(args[3]);
                            }

                            Help.showHelp("background", (Player) sender, args);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("createshop") && sender.hasPermission(P_ADMIN_CREATE_SHOP))
                {
                    if (args.length == 3)
                    {
                        temp.add("true");
                        temp.add("false");

                        AddToAutoCompleteIfValid(args[2]);
                    }

                    Help.showHelp("create_shop", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("deleteshop") && sender.hasPermission(P_ADMIN_DELETE_SHOP))
                {
                    temp.addAll(ShopUtil.shopConfigFiles.keySet());

                    AddToAutoCompleteIfValid(args[1]);

                    Help.showHelp("delete_shop", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("mergeshop") && sender.hasPermission(P_ADMIN_MERGE_SHOP))
                {
                    if (args.length <= 3)
                    {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet());

                        AddToAutoCompleteIfValid(args[args.length - 1]);
                    }

                    Help.showHelp("merge_shop", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("openshop") && sender.hasPermission(P_ADMIN_OPEN_SHOP))
                {
                    if (args.length == 2)
                    {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet());

                        AddToAutoCompleteIfValid(args[args.length - 1]);
                    } else if (args.length == 3)
                    {
                        temp.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));

                        AddToAutoCompleteIfValid(args[args.length - 1]);
                    }

                    Help.showHelp("open_shop", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("renameshop") && sender.hasPermission(P_ADMIN_RENAME_SHOP))
                {
                    if (args.length == 2)
                    {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet());

                        AddToAutoCompleteIfValid(args[1]);
                    }

                    Help.showHelp("rename_shop", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("copyshop") && sender.hasPermission(P_ADMIN_COPY_SHOP))
                {
                    if (args.length == 2)
                    {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet());

                        AddToAutoCompleteIfValid(args[1]);
                    }

                    Help.showHelp("copy_shop", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("cmdHelp"))
                {
                    if (args.length == 2)
                    {
                        autoCompleteList.add("on");
                        autoCompleteList.add("off");

                        Help.showHelp("cmd_help", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("iteminfo"))
                {
                    Help.showHelp("iteminfo", (Player) sender, args);
                }  else if (args[0].equalsIgnoreCase("settax"))
                {
                    Help.showHelp("set_tax", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("setdefaultshop"))
                {
                    temp.addAll(ShopUtil.shopConfigFiles.keySet());

                    AddToAutoCompleteIfValid(args[1]);

                    Help.showHelp("set_default_shop", (Player) sender, args);
                } else if (args[0].equalsIgnoreCase("deleteOldUser"))
                {
                    Help.showHelp("delete_old_user", (Player) sender, args);
                }

                return autoCompleteList;
            }
        } catch (Exception e)
        {
            return null;
        }

        return null;
    }

    private static void AddToAutoCompleteIfValid(String arg)
    {
        for (String s : temp)
        {
            if (s.toLowerCase().startsWith(arg.toLowerCase()))
                autoCompleteList.add(s);
        }
    }
}
