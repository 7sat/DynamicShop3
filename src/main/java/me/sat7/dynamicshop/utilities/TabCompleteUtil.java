package me.sat7.dynamicshop.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

public final class TabCompleteUtil {
    private TabCompleteUtil() {

    }

    public static List<String> onTabCompleteBody(DynamicShop dynamicShop, CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return null;

        String senderUuid = ((Player) sender).getUniqueId().toString();

        try
        {
            ArrayList<String> temp = new ArrayList<>();
            ArrayList<String> alist = new ArrayList<>();

            if(cmd.getName().equalsIgnoreCase("shop") && args.length == 1)
            {
                if(!dynamicShop.getConfig().getBoolean("UseShopCommand")) return alist;

                for (String s: ShopUtil.ccShop.get().getKeys(false))
                {
                    ConfigurationSection options = ShopUtil.ccShop.get().getConfigurationSection(s).getConfigurationSection("Options");

                    if(options.contains("flag.signshop") && !sender.hasPermission(Constants.REMOTE_ACCESS_PERMISSION)) continue;

                    temp.add(s);
                }

                for (String s:temp)
                {
                    if(s.startsWith(args[0]) || s.toLowerCase().startsWith(args[0])) alist.add(s);
                }
                return alist;
            }
            else if(cmd.getName().equalsIgnoreCase("DynamicShop"))
            {
                if(args.length == 1)
                {
                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("main"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","main");
                        Help.showHelp("main",(Player)sender,args);
                    }

                    temp.add("shop");
                    temp.add("qsell");
                    if(sender.hasPermission("dshop.admin.createshop")) temp.add("createshop");
                    if(sender.hasPermission("dshop.admin.deleteshop")) temp.add("deleteshop");
                    if(sender.hasPermission("dshop.admin.mergeshop")) temp.add("mergeshop");
                    if(sender.hasPermission("dshop.admin.renameshop")) temp.add("renameshop");
                    if(sender.hasPermission("dshop.admin.openshop")) temp.add("openshop");
                    if(sender.hasPermission("dshop.admin.settax")) temp.add("settax");
                    if(sender.hasPermission("dshop.admin.settax")) temp.add("settax temp");
                    if(sender.hasPermission("dshop.admin.setdefaultshop")) temp.add("setdefaultshop");
                    if(sender.hasPermission(Constants.DELETE_USER_PERMISSION)) temp.add("deleteOldUser");
                    if(sender.hasPermission("dshop.admin.convert")) temp.add("convert");
                    if(sender.hasPermission("dshop.admin.reload")) temp.add("reload");
                    temp.add("cmdHelp");

                    for (String s:temp)
                    {
                        if(s.startsWith(args[0])) alist.add(s);
                    }
                }
                else if(args.length >= 2 && args[0].equals("shop"))
                {
                    if(args.length == 2)
                    {
                        if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("shop"))
                        {
                            DynamicShop.ccUser.get().set(senderUuid + ".tmpString","shop");
                            Help.showHelp("shop",(Player)sender,args);
                        }

                        for (String s: ShopUtil.ccShop.get().getKeys(false))
                        {
                            ConfigurationSection options = ShopUtil.ccShop.get().getConfigurationSection(s).getConfigurationSection("Options");

                            if(options.contains("flag") && options.getConfigurationSection("flag").contains("signshop") && !sender.hasPermission(Constants.REMOTE_ACCESS_PERMISSION)) continue;

                            temp.add(s);
                        }

                        for (String s:temp)
                        {
                            if(s.startsWith(args[1]) || s.toLowerCase().startsWith(args[1])) alist.add(s);
                        }
                    }
                    else if(args.length >= 3 && (!ShopUtil.ccShop.get().contains(args[1]) || args[1].length() == 0))
                    {
                        return null;
                    }
                    else if(args.length == 3)
                    {
                        //add,addhand,edit,editall,permission,maxpage,flag
                        if(sender.hasPermission("dshop.admin.shopedit"))
                        {
                            temp.add("add");
                            temp.add("addhand");
                            temp.add("edit");
                            temp.add("editall");
                            temp.add("permission");
                            temp.add("maxpage");
                            temp.add("flag");
                            temp.add("position");
                            temp.add("shophours");
                            temp.add("fluctuation");
                            temp.add("stockStabilizing");
                            temp.add("account");
                            temp.add("hideStock");
                            temp.add("hidePricingType");
                            temp.add("sellbuy");
                            temp.add("log");
                        }

                        for (String s:temp)
                        {
                            if(s.startsWith(args[2])) alist.add(s);
                        }
                    }
                    else if(args.length >= 4)
                    {
                        if(args[2].equalsIgnoreCase("addhand") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("addhand"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","addhand");
                                Help.showHelp("addhand",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("add") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("add"))
                                {
                                    DynamicShop.ccUser.get().set(senderUuid + ".tmpString","add");
                                    Help.showHelp("add",(Player)sender,args);
                                }

                                for (Material m: Material.values())
                                {
                                    temp.add(m.name());
                                }

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3].toUpperCase())) alist.add(s);
                                }
                            }
                            else if(args.length == 5)
                            {
                                String mat = args[3].toUpperCase();
                                if(!(DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").contains("add") &&
                                        DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").length() > 3))
                                {
                                    if(Material.matchMaterial(mat) != null)
                                    {
                                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","add"+args[3]);
                                        Help.showHelp("add"+args[3],(Player)sender,args);
                                    }
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("edit") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("edit"))
                                {
                                    DynamicShop.ccUser.get().set(senderUuid + ".tmpString","edit");
                                    Help.showHelp("edit",(Player)sender,args);
                                }

                                String shopName = args[1];

                                for (String s: ShopUtil.ccShop.get().getConfigurationSection(shopName).getKeys(false))
                                {
                                    try
                                    {
                                        int i = Integer.parseInt(s);
                                        if(!ShopUtil.ccShop.get().contains(shopName+"."+s+".value")) continue; // 장식용임
                                        temp.add(ShopUtil.ccShop.get().getConfigurationSection(shopName+"."+s).getName()+"/"+ ShopUtil.ccShop.get().getString(shopName+"." + s +".mat"));
                                    }
                                    catch (Exception ignored){}
                                }

                                for (String s:temp)
                                {
                                    String upper = args[3].toUpperCase();

                                    if(s.startsWith(upper)) alist.add(s);
                                }
                            }
                            else if(args.length == 5)
                            {
                                String mat = args[3];
                                mat = mat.substring(mat.indexOf("/")+1);
                                mat = mat.toUpperCase();

                                if(!(DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("edit"+mat)))
                                {
                                    if(Material.matchMaterial(mat) != null)
                                    {
                                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","edit"+mat);
                                        Help.showHelp("edit"+mat,(Player)sender,args);
                                    }
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("editall") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("editall"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","editall");
                                Help.showHelp("editall",(Player)sender,args);
                            }
                            if(args.length == 4)
                            {
                                temp.add("value");
                                temp.add("valueMin");
                                temp.add("valueMax");
                                temp.add("stock");
                                temp.add("median");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }
                            else if(args.length == 5)
                            {
                                temp.add("=");
                                temp.add("+");
                                temp.add("-");
                                temp.add("/");
                                temp.add("*");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[4])) alist.add(s);
                                }
                            }
                            else if(args.length == 6)
                            {
                                if(args[4].equals("="))
                                {
                                    temp.add("value");
                                    temp.add("valueMin");
                                    temp.add("valueMax");
                                    temp.add("stock");
                                    temp.add("median");

                                    for (String s:temp)
                                    {
                                        if(s.startsWith(args[5])) alist.add(s);
                                    }
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("permission") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("permission"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","permission");
                                Help.showHelp("permission",(Player)sender,args);
                            }
                            if(args.length >= 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("maxpage") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("maxpage"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","maxpage");
                                Help.showHelp("maxpage",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("flag") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("signshop");
                                temp.add("localshop");
                                temp.add("deliverycharge");
                                temp.add("jobpoint");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }
                            else if(args.length > 4)
                            {
                                temp.add("set");
                                temp.add("unset");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[4])) alist.add(s);
                                }
                            }

                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("flag"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","flag");
                                Help.showHelp("flag",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("position") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length >= 4)
                            {
                                temp.add("pos1");
                                temp.add("pos2");
                                temp.add("clear");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }

                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("position"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","position");
                                Help.showHelp("position",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("shophours") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("shophours"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","shophours");
                                Help.showHelp("shophours",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("fluctuation") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("off");
                                temp.add("30m");
                                temp.add("1h");
                                temp.add("2h");
                                temp.add("4h");
                                temp.add("12h");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }

                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("fluctuation"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","fluctuation");
                                Help.showHelp("fluctuation",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("stockStabilizing") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("off");
                                temp.add("30m");
                                temp.add("1h");
                                temp.add("2h");
                                temp.add("4h");
                                temp.add("12h");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }

                            if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("stockStabilizing"))
                            {
                                DynamicShop.ccUser.get().set(senderUuid + ".tmpString","stockStabilizing");
                                Help.showHelp("stockStabilizing",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("account") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("set");
                                temp.add("linkto");
                                temp.add("transfer");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("account"))
                                {
                                    DynamicShop.ccUser.get().set(senderUuid + ".tmpString","account");
                                    Help.showHelp("account",(Player)sender,args);
                                }
                            }
                            else if(args.length == 5)
                            {
                                if(args[3].equals("linkto") || args[3].equals("transfer"))
                                {
                                    temp.addAll(ShopUtil.ccShop.get().getKeys(false));
                                }

                                if(args[3].equals("set"))
                                {
                                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("accountSet"))
                                    {
                                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","accountSet");
                                        Help.showHelp("accountSet",(Player)sender,args);
                                    }
                                }
                                else if(args[3].equals("transfer"))
                                {
                                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("accountTransfer"))
                                    {
                                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","accountTransfer");
                                        Help.showHelp("accountTransfer",(Player)sender,args);
                                    }

                                    for (Player p: Bukkit.getServer().getOnlinePlayers())
                                    {
                                        temp.add(p.getName());
                                    }
                                }
                                else if(args[3].equals("linkto"))
                                {
                                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("accountLinkto"))
                                    {
                                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","accountLinkto");
                                        Help.showHelp("accountLinkto",(Player)sender,args);
                                    }
                                }

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[4])) alist.add(s);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("hideStock") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("hideStock"))
                                {
                                    DynamicShop.ccUser.get().set(senderUuid + ".tmpString","hideStock");
                                    Help.showHelp("hideStock",(Player)sender,args);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("hidePricingType") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("hidePricingType"))
                                {
                                    DynamicShop.ccUser.get().set(senderUuid + ".tmpString","hidePricingType");
                                    Help.showHelp("hidePricingType",(Player)sender,args);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("sellbuy") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("SellOnly");
                                temp.add("BuyOnly");
                                temp.add("Clear");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("sellbuy"))
                                {
                                    DynamicShop.ccUser.get().set(senderUuid + ".tmpString","sellbuy");
                                    Help.showHelp("sellbuy",(Player)sender,args);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("log") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("enable");
                                temp.add("disable");
                                temp.add("clear");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("log"))
                                {
                                    DynamicShop.ccUser.get().set(senderUuid + ".tmpString","log");
                                    Help.showHelp("log",(Player)sender,args);
                                }
                            }
                        }
                    }
                }
                else if(args[0].equalsIgnoreCase("createshop") && sender.hasPermission("dshop.admin.createshop"))
                {
                    if (args.length == 3)
                    {
                        temp.add("true");
                        temp.add("false");

                        for (String s:temp)
                        {
                            if(s.startsWith(args[2])) alist.add(s);
                        }
                    }

                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("createshop"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","createshop");
                        Help.showHelp("createshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("deleteshop") && sender.hasPermission("dshop.admin.deleteshop"))
                {
                    temp.addAll(ShopUtil.ccShop.get().getKeys(false));

                    for (String s:temp)
                    {
                        if(s.startsWith(args[1])) alist.add(s);
                    }

                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("deleteshop"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","deleteshop");
                        Help.showHelp("deleteshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("mergeshop") && sender.hasPermission("dshop.admin.mergeshop"))
                {
                    if (args.length <= 3)
                    {
                        temp.addAll(ShopUtil.ccShop.get().getKeys(false));

                        for (String s:temp)
                        {
                            if(s.startsWith(args[args.length-1])) alist.add(s);
                        }
                    }

                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("mergeshop"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","mergeshop");
                        Help.showHelp("mergeshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("openshop") && sender.hasPermission("dshop.admin.openshop"))
                {
                    if (args.length == 2)
                    {
                        temp.addAll(ShopUtil.ccShop.get().getKeys(false));

                        for (String s:temp)
                        {
                            if(s.startsWith(args[args.length-1])) alist.add(s);
                        }
                    } else if (args.length == 3) {
                        temp.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));

                        for (String s:temp)
                        {
                            if(s.startsWith(args[args.length-1])) alist.add(s);
                        }
                    }

                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("openshop"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","openshop");
                        Help.showHelp("openshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("renameshop") && sender.hasPermission("dshop.admin.renameshop"))
                {
                    if(args.length == 2)
                    {
                        temp.addAll(ShopUtil.ccShop.get().getKeys(false));

                        for (String s:temp)
                        {
                            if(s.startsWith(args[1])) alist.add(s);
                        }
                    }

                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("renameshop"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","renameshop");
                        Help.showHelp("renameshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("cmdHelp"))
                {
                    if(args.length == 2)
                    {
                        alist.add("on");
                        alist.add("off");

                        if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("cmdHelp"))
                        {
                            DynamicShop.ccUser.get().set(senderUuid + ".tmpString","cmdHelp");
                            Help.showHelp("cmdHelp",(Player)sender,args);
                        }
                    }
                }
                else if(args[0].equalsIgnoreCase("settax"))
                {
                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("settax"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","settax");
                        Help.showHelp("settax",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("setdefaultshop"))
                {
                    temp.addAll(ShopUtil.ccShop.get().getKeys(false));

                    for (String s:temp)
                    {
                        if(s.startsWith(args[1])) alist.add(s);
                    }

                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("setdefaultshop"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","setdefaultshop");
                        Help.showHelp("setdefaultshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("deleteOldUser"))
                {
                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("deleteOldUser"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","deleteOldUser");
                        Help.showHelp("deleteOldUser",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("convert"))
                {
                    if(!sender.hasPermission("dshop.admin.convert")) return null;

                    if(args.length == 2)
                    {
                        temp.add("Shop");
                    }

                    for (String s:temp)
                    {
                        if(s.startsWith(args[1])) alist.add(s);
                    }

                    if(!DynamicShop.ccUser.get().getString(senderUuid + ".tmpString").equals("convert"))
                    {
                        DynamicShop.ccUser.get().set(senderUuid + ".tmpString","convert");
                        Help.showHelp("convert",(Player)sender,args);
                    }
                }

                return alist;
            }
        }catch (Exception e){
            return null;
        }

        return null;
    }
}
