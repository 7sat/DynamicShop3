package me.sat7.dynamicshop.utilities;

import java.util.*;
import java.util.stream.Collectors;

import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.Help;
import me.sat7.dynamicshop.constants.Constants;

public final class TabCompleteUtil
{
    private TabCompleteUtil()
    {

    }

    public static List<String> onTabCompleteBody(DynamicShop dynamicShop, CommandSender sender, Command cmd, String[] args)
    {
        if (!(sender instanceof Player)) return null;

        Player p = (Player)sender;
        UUID uuid = p.getUniqueId();

        try
        {
            ArrayList<String> temp = new ArrayList<>();
            ArrayList<String> alist = new ArrayList<>();

            if (cmd.getName().equalsIgnoreCase("shop") && args.length == 1)
            {
                if (!dynamicShop.getConfig().getBoolean("Command.UseShopCommand")) return alist;

                for (Map.Entry<String, CustomConfig> entry : ShopUtil.shopConfigFiles.entrySet())
                {
                    ConfigurationSection options = entry.getValue().get().getConfigurationSection("Options");

                    if (options.contains("flag.signshop") && !sender.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                        continue;

                    temp.add(entry.getKey());
                }

                for (String s : temp)
                {
                    if (s.startsWith(args[0]) || s.toLowerCase().startsWith(args[0])) alist.add(s);
                }
                return alist;
            } else if (cmd.getName().equalsIgnoreCase("DynamicShop"))
            {
                if (args.length == 1)
                {
                    if (!DynamicShop.userTempData.get(uuid).equals("main"))
                    {
                        DynamicShop.userTempData.put(uuid,"main");
                        Help.showHelp("main", (Player) sender, args);
                    }

                    temp.add("shop");
                    temp.add("qsell");
                    if (sender.hasPermission("dshop.admin.createshop")) temp.add("createshop");
                    if (sender.hasPermission("dshop.admin.deleteshop")) temp.add("deleteshop");
                    if (sender.hasPermission("dshop.admin.mergeshop")) temp.add("mergeshop");
                    if (sender.hasPermission("dshop.admin.renameshop")) temp.add("renameshop");
                    if (sender.hasPermission("dshop.admin.openshop")) temp.add("openshop");
                    if (sender.hasPermission("dshop.admin.settax")) temp.add("settax");
                    if (sender.hasPermission("dshop.admin.settax")) temp.add("settax temp");
                    if (sender.hasPermission("dshop.admin.setdefaultshop")) temp.add("setdefaultshop");
                    if (sender.hasPermission(Constants.P_ADMIN_DELETE_OLD_USER)) temp.add("deleteOldUser");
                    if (sender.hasPermission("dshop.admin.reload")) temp.add("reload");
                    temp.add("cmdHelp");

                    for (String s : temp)
                    {
                        if (s.startsWith(args[0])) alist.add(s);
                    }
                } else if (args.length >= 2 && args[0].equals("shop"))
                {
                    CustomConfig data = ShopUtil.shopConfigFiles.get(args[1]);

                    if (args.length == 2)
                    {
                        if (!DynamicShop.userTempData.get(uuid).equals("shop"))
                        {
                            DynamicShop.userTempData.put(uuid,"shop");
                            Help.showHelp("shop", (Player) sender, args);
                        }

                        for (Map.Entry<String, CustomConfig> entry : ShopUtil.shopConfigFiles.entrySet())
                        {
                            ConfigurationSection options = entry.getValue().get().getConfigurationSection("Options");

                            if (options == null)
                                continue;

                            if (options.contains("flag") && options.getConfigurationSection("flag").contains("signshop") && !sender.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                                continue;

                            temp.add(entry.getKey());
                        }

                        for (String s : temp)
                        {
                            if (s.startsWith(args[1]) || s.toLowerCase().startsWith(args[1])) alist.add(s);
                        }
                    } else if (args.length >= 3 && (!ShopUtil.shopConfigFiles.containsKey(args[1]) || args[1].length() == 0))
                    {
                        return null;
                    } else if (args.length == 3)
                    {
                        //add,addhand,edit,editall,permission,maxpage,flag
                        if (sender.hasPermission("dshop.admin.shopedit"))
                        {
                            temp.add("add");
                            temp.add("addhand");
                            temp.add("edit");
                            temp.add("editall");
                            temp.add("setToRecAll");
                            temp.add("permission");
                            temp.add("maxpage");
                            temp.add("flag");
                            temp.add("position");
                            temp.add("shophours");
                            temp.add("fluctuation");
                            temp.add("stockStabilizing");
                            temp.add("account");
                            temp.add("sellbuy");
                            temp.add("log");
                        }

                        for (String s : temp)
                        {
                            if (s.toLowerCase().startsWith(args[2].toLowerCase())) alist.add(s);
                        }
                    } else if (args.length >= 4)
                    {
                        if (args[2].equalsIgnoreCase("addhand") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equals("addhand"))
                            {
                                DynamicShop.userTempData.put(uuid,"addhand");
                                Help.showHelp("add_hand", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("add") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (args.length == 4)
                            {
                                if (!DynamicShop.userTempData.get(uuid).equals("add"))
                                {
                                    DynamicShop.userTempData.put(uuid,"add");
                                    Help.showHelp("add", (Player) sender, args);
                                }

                                for (Material m : Material.values())
                                {
                                    temp.add(m.name());
                                }

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3].toUpperCase())) alist.add(s);
                                }
                            } else if (args.length == 5)
                            {
                                String mat = args[3].toUpperCase();
                                String userTempStr = DynamicShop.userTempData.get(uuid);

                                if (!(userTempStr.contains("add") && userTempStr.length() > 3))
                                {
                                    if (Material.matchMaterial(mat) != null)
                                    {
                                        DynamicShop.userTempData.put(uuid,"add" + args[3]);
                                        Help.showHelp("add" + args[3], (Player) sender, args);
                                    }
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("edit") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (args.length == 4)
                            {
                                if (!DynamicShop.userTempData.get(uuid).equals("edit"))
                                {
                                    DynamicShop.userTempData.put(uuid,"edit");
                                    Help.showHelp("edit", (Player) sender, args);
                                }

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

                                for (String s : temp)
                                {
                                    String upper = args[3].toUpperCase();

                                    if (s.startsWith(upper)) alist.add(s);
                                }
                            } else if (args.length == 5)
                            {
                                String mat = args[3];
                                mat = mat.substring(mat.indexOf("/") + 1);
                                mat = mat.toUpperCase();

                                if (!(DynamicShop.userTempData.get(uuid).equals("edit" + mat)))
                                {
                                    if (Material.matchMaterial(mat) != null)
                                    {
                                        DynamicShop.userTempData.put(uuid,"edit" + mat);
                                        Help.showHelp("edit" + mat, (Player) sender, args);
                                    }
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("editall") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equals("editall"))
                            {
                                DynamicShop.userTempData.put(uuid,"editall");
                                Help.showHelp("edit_all", (Player) sender, args);
                            }
                            if (args.length == 4)
                            {
                                temp.add("value");
                                temp.add("valueMin");
                                temp.add("valueMax");
                                temp.add("stock");
                                temp.add("median");
                                temp.add("maxStock");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3])) alist.add(s);
                                }
                            } else if (args.length == 5)
                            {
                                temp.add("=");
                                temp.add("+");
                                temp.add("-");
                                temp.add("/");
                                temp.add("*");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[4])) alist.add(s);
                                }
                            } else if (args.length == 6)
                            {
                                if (args[4].equals("="))
                                {
                                    temp.add("value");
                                    temp.add("valueMin");
                                    temp.add("valueMax");
                                    temp.add("stock");
                                    temp.add("median");
                                    temp.add("maxStock");

                                    for (String s : temp)
                                    {
                                        if (s.startsWith(args[5])) alist.add(s);
                                    }
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("setToRecAll") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equalsIgnoreCase("setToRecAll"))
                            {
                                DynamicShop.userTempData.put(uuid,"edit");
                                Help.showHelp("set_to_rec_all", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("permission") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equals("permission"))
                            {
                                DynamicShop.userTempData.put(uuid,"permission");
                                Help.showHelp("permission", (Player) sender, args);
                            }
                            if (args.length >= 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3])) alist.add(s);
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("maxpage") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equals("maxpage"))
                            {
                                DynamicShop.userTempData.put(uuid,"maxpage");
                                Help.showHelp("max_page", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("flag") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (args.length == 4)
                            {
                                temp.add("signshop");
                                temp.add("localshop");
                                temp.add("deliverycharge");
                                temp.add("jobpoint");
                                temp.add("showvaluechange");
                                temp.add("hidestock");
                                temp.add("hidepricingtype");
                                temp.add("hideshopbalance");
                                temp.add("showmaxstock");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3])) alist.add(s);
                                }
                            } else if (args.length > 4)
                            {
                                temp.add("set");
                                temp.add("unset");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[4])) alist.add(s);
                                }
                            }

                            if (!DynamicShop.userTempData.get(uuid).equals("flag"))
                            {
                                DynamicShop.userTempData.put(uuid,"flag");
                                Help.showHelp("flag", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("position") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (args.length >= 4)
                            {
                                temp.add("pos1");
                                temp.add("pos2");
                                temp.add("clear");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3])) alist.add(s);
                                }
                            }

                            if (!DynamicShop.userTempData.get(uuid).equals("position"))
                            {
                                DynamicShop.userTempData.put(uuid,"position");
                                Help.showHelp("position", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("shophours") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equals("shophours"))
                            {
                                DynamicShop.userTempData.put(uuid,"shophours");
                                Help.showHelp("shophours", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("fluctuation") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equals("fluctuation"))
                            {
                                DynamicShop.userTempData.put(uuid,"fluctuation");
                                Help.showHelp("fluctuation", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("stockStabilizing") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (!DynamicShop.userTempData.get(uuid).equals("stockStabilizing"))
                            {
                                DynamicShop.userTempData.put(uuid,"stockStabilizing");
                                Help.showHelp("stock_stabilizing", (Player) sender, args);
                            }
                        } else if (args[2].equalsIgnoreCase("account") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (args.length == 4)
                            {
                                temp.add("set");
                                temp.add("linkto");
                                temp.add("transfer");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3])) alist.add(s);
                                }

                                if (!DynamicShop.userTempData.get(uuid).equals("account"))
                                {
                                    DynamicShop.userTempData.put(uuid,"account");
                                    Help.showHelp("account", (Player) sender, args);
                                }
                            } else if (args.length == 5)
                            {
                                if (args[3].equals("linkto") || args[3].equals("transfer"))
                                {
                                    temp.addAll(ShopUtil.shopConfigFiles.keySet());
                                }

                                switch (args[3])
                                {
                                    case "set":
                                        if (!DynamicShop.userTempData.get(uuid).equals("accountSet"))
                                        {
                                            DynamicShop.userTempData.put(uuid, "accountSet");
                                            Help.showHelp("account_set", (Player) sender, args);
                                        }
                                        break;
                                    case "transfer":
                                        if (!DynamicShop.userTempData.get(uuid).equals("accountTransfer"))
                                        {
                                            DynamicShop.userTempData.put(uuid, "accountTransfer");
                                            Help.showHelp("account_transfer", (Player) sender, args);
                                        }

                                        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers())
                                        {
                                            temp.add(onlinePlayer.getName());
                                        }
                                        break;
                                    case "linkto":
                                        if (!DynamicShop.userTempData.get(uuid).equals("accountLinkto"))
                                        {
                                            DynamicShop.userTempData.put(uuid, "accountLinkto");
                                            Help.showHelp("account_link_to", (Player) sender, args);
                                        }
                                        break;
                                }

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[4])) alist.add(s);
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("sellbuy") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (args.length == 4)
                            {
                                temp.add("SellOnly");
                                temp.add("BuyOnly");
                                temp.add("Clear");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3])) alist.add(s);
                                }

                                if (!DynamicShop.userTempData.get(uuid).equals("sellbuy"))
                                {
                                    DynamicShop.userTempData.put(uuid,"sellbuy");
                                    Help.showHelp("sellbuy", (Player) sender, args);
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("log") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if (args.length == 4)
                            {
                                temp.add("enable");
                                temp.add("disable");
                                temp.add("clear");

                                for (String s : temp)
                                {
                                    if (s.startsWith(args[3])) alist.add(s);
                                }

                                if (!DynamicShop.userTempData.get(uuid).equals("log"))
                                {
                                    DynamicShop.userTempData.put(uuid,"log");
                                    Help.showHelp("log", (Player) sender, args);
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("createshop") && sender.hasPermission("dshop.admin.createshop"))
                {
                    if (args.length == 3)
                    {
                        temp.add("true");
                        temp.add("false");

                        for (String s : temp)
                        {
                            if (s.startsWith(args[2])) alist.add(s);
                        }
                    }

                    if (!DynamicShop.userTempData.get(uuid).equals("createshop"))
                    {
                        DynamicShop.userTempData.put(uuid,"createshop");
                        Help.showHelp("create_shop", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("deleteshop") && sender.hasPermission("dshop.admin.deleteshop"))
                {
                    temp.addAll(ShopUtil.shopConfigFiles.keySet());

                    for (String s : temp)
                    {
                        if (s.startsWith(args[1])) alist.add(s);
                    }

                    if (!DynamicShop.userTempData.get(uuid).equals("deleteshop"))
                    {
                        DynamicShop.userTempData.put(uuid,"deleteshop");
                        Help.showHelp("delete_shop", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("mergeshop") && sender.hasPermission("dshop.admin.mergeshop"))
                {
                    if (args.length <= 3)
                    {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet());

                        for (String s : temp)
                        {
                            if (s.startsWith(args[args.length - 1])) alist.add(s);
                        }
                    }

                    if (!DynamicShop.userTempData.get(uuid).equals("mergeshop"))
                    {
                        DynamicShop.userTempData.put(uuid,"mergeshop");
                        Help.showHelp("merge_shop", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("openshop") && sender.hasPermission("dshop.admin.openshop"))
                {
                    if (args.length == 2)
                    {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet());

                        for (String s : temp)
                        {
                            if (s.startsWith(args[args.length - 1])) alist.add(s);
                        }
                    } else if (args.length == 3)
                    {
                        temp.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));

                        for (String s : temp)
                        {
                            if (s.startsWith(args[args.length - 1])) alist.add(s);
                        }
                    }

                    if (!DynamicShop.userTempData.get(uuid).equals("openshop"))
                    {
                        DynamicShop.userTempData.put(uuid,"openshop");
                        Help.showHelp("open_shop", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("renameshop") && sender.hasPermission("dshop.admin.renameshop"))
                {
                    if (args.length == 2)
                    {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet());

                        for (String s : temp)
                        {
                            if (s.startsWith(args[1])) alist.add(s);
                        }
                    }

                    if (!DynamicShop.userTempData.get(uuid).equals("renameshop"))
                    {
                        DynamicShop.userTempData.put(uuid,"renameshop");
                        Help.showHelp("rename_shop", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("cmdHelp"))
                {
                    if (args.length == 2)
                    {
                        alist.add("on");
                        alist.add("off");

                        if (!DynamicShop.userTempData.get(uuid).equals("cmdHelp"))
                        {
                            DynamicShop.userTempData.put(uuid,"cmdHelp");
                            Help.showHelp("cmd_help", (Player) sender, args);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("settax"))
                {
                    if (!DynamicShop.userTempData.get(uuid).equals("settax"))
                    {
                        DynamicShop.userTempData.put(uuid,"settax");
                        Help.showHelp("set_tax", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("setdefaultshop"))
                {
                    temp.addAll(ShopUtil.shopConfigFiles.keySet());

                    for (String s : temp)
                    {
                        if (s.startsWith(args[1])) alist.add(s);
                    }

                    if (!DynamicShop.userTempData.get(uuid).equals("setdefaultshop"))
                    {
                        DynamicShop.userTempData.put(uuid,"setdefaultshop");
                        Help.showHelp("set_default_shop", (Player) sender, args);
                    }
                } else if (args[0].equalsIgnoreCase("deleteOldUser"))
                {
                    if (!DynamicShop.userTempData.get(uuid).equals("deleteOldUser"))
                    {
                        DynamicShop.userTempData.put(uuid,"deleteOldUser");
                        Help.showHelp("delete_old_user", (Player) sender, args);
                    }
                }

                return alist;
            }
        } catch (Exception e)
        {
            return null;
        }

        return null;
    }
}
