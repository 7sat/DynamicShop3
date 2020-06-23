package me.sat7.dynamicshop.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public final class Help {
    private Help() {

    }

    // 명령어 도움말 표시
    public static void showHelp(String helpcode, Player player, String[] args)
    {
        if(!DynamicShop.ccUser.get().getBoolean(player.getUniqueId() + ".cmdHelp")) return;

        if(helpcode.equals("main"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","main"));
            player.sendMessage(" - shop: " + LangUtil.ccLang.get().getString("HELP.SHOP"));
            player.sendMessage(" - qsell: " + LangUtil.ccLang.get().getString("HELP.QSELL"));
            player.sendMessage(" - cmdHelp: " + LangUtil.ccLang.get().getString("HELP.CMD"));
            if(player.hasPermission("dshop.admin.createshop"))player.sendMessage("§e - createshop: "+ LangUtil.ccLang.get().getString("HELP.CREATESHOP"));
            if(player.hasPermission("dshop.admin.deleteshop"))player.sendMessage("§e - deleteshop: "+ LangUtil.ccLang.get().getString("HELP.DELETESHOP"));
            if(player.hasPermission("dshop.admin.mergeshop"))player.sendMessage("§e - mergeshop");
            if(player.hasPermission("dshop.admin.openshop"))player.sendMessage("§e - openshop");
            if(player.hasPermission("dshop.admin.renameshop"))player.sendMessage("§e - renameshop");
            if(player.hasPermission("dshop.admin.setdefaultshop"))player.sendMessage("§e - setdefaultshop ");
            if(player.hasPermission("dshop.admin.settax")) {
                player.sendMessage("§e - settax: "+ LangUtil.ccLang.get().getString("HELP.SETTAX"));
                player.sendMessage("§e - settax temp: "+ LangUtil.ccLang.get().getString("HELP.SETTAX_TEMP"));
            }
            if(player.hasPermission(Constants.DELETE_USER_PERMISSION))player.sendMessage("§e - deleteOldUser: " + LangUtil.ccLang.get().getString("HELP.DELETE_OLD_USER"));
            if(player.hasPermission("dshop.admin.convert"))player.sendMessage("§e - convert: " + LangUtil.ccLang.get().getString("HELP.CONVERT"));
            if(player.hasPermission("dshop.admin.reload"))player.sendMessage("§e - reload: " + LangUtil.ccLang.get().getString("HELP.RELOAD"));
            player.sendMessage("");
        }
        else if(helpcode.equals("shop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","shop"));

            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop [<shopname>]");
            if(player.hasPermission("dshop.admin.shopedit")||player.hasPermission("dshop.admin.shopedit")||player.hasPermission("dshop.admin.editall"))
            {
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE")
                        + ": /ds shop <shopname> <addhand | add | edit | editall | sellbuy | permission | maxpage | flag | position | shophours | fluctuation | stockStabilizing | hideStock | account | log>");
            }

            if(player.hasPermission("dshop.admin.shopedit")) player.sendMessage("§e - addhand: " + LangUtil.ccLang.get().getString("HELP.SHOPADDHAND"));
            if(player.hasPermission("dshop.admin.shopedit")) player.sendMessage("§e - add: " + LangUtil.ccLang.get().getString("HELP.SHOPADDITEM"));
            if(player.hasPermission("dshop.admin.shopedit")) player.sendMessage("§e - edit: " + LangUtil.ccLang.get().getString("HELP.SHOPEDIT"));
            if(player.hasPermission("dshop.admin.editall")) player.sendMessage("§e - editall: " + LangUtil.ccLang.get().getString("HELP.EDITALL"));
            player.sendMessage("");
        }
        else if(helpcode.equals("openshop") && player.hasPermission("dshop.admin.openshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","openshop"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds openshop [shopname] <playername>");
            player.sendMessage("");
        }
        else if(helpcode.equals("addhand") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","addhand"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> addhand <value> <median> <stock>");
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> addhand <value> <min value> <max value> <median> <stock>");
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.SHOPADDHAND"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.PRICE"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.INF_STATIC"));

            ItemStack tempItem = player.getInventory().getItemInMainHand();

            if(tempItem != null && tempItem.getType() != Material.AIR)
            {
                int idx = ShopUtil.findItemFromShop(args[1], tempItem);
                if(idx != -1)
                {
                    player.sendMessage("");
                    ItemsUtil.sendItemInfo(player,args[1],idx,"HELP.ITEM_ALREADY_EXIST");
                }
            }
            else
            {
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("ERR.HAND_EMPTY2"));
            }

            player.sendMessage("");
        }
        else if(helpcode.startsWith("add") && player.hasPermission("dshop.admin.shopedit"))
        {
            if(helpcode.length() > "add".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3]));
                    int idx = ShopUtil.findItemFromShop(args[1], tempItem);

                    if(idx != -1)
                    {
                        ItemsUtil.sendItemInfo(player,args[1],idx,"HELP.ITEM_ALREADY_EXIST");
                        player.sendMessage("");
                    }
                }catch (Exception ignored){}
            }
            else
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","add"));
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <median> <stock>");
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <min value> <max value> <median> <stock>");
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.SHOPADDITEM"));
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.PRICE"));
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.INF_STATIC"));

                player.sendMessage("");
            }
        }
        else if(helpcode.contains("edit") && !helpcode.equals("editall") && player.hasPermission("dshop.admin.shopedit"))
        {
            if(helpcode.length() > "edit".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3].substring(args[3].indexOf("/")+1)));
                    int idx = ShopUtil.findItemFromShop(args[1], tempItem);

                    if(idx != -1)
                    {
                        ItemsUtil.sendItemInfo(player,args[1],idx,"HELP.ITEM_INFO");
                        player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.REMOVE_ITEM"));
                        player.sendMessage("");
                    }
                }catch (Exception ignored){}
            }
            else
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","edit"));
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <median> <stock>");
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <min value> <max value> <median> <stock>");
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.SHOPEDIT"));
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.PRICE"));
                player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.INF_STATIC"));

                player.sendMessage("");
            }
        }
        else if(helpcode.equals("editall") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","editall"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> editall <value | median | stock> <= | + | - | * | /> <amount>");
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.EDITALL"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.EDITALL2"));

            player.sendMessage("");
        }
        else if(helpcode.equals("cmdHelp"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","cmdHelp"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds cmdHelp <on | off>");
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.CMD"));

            player.sendMessage("");
        }
        else if(helpcode.equals("createshop") && player.hasPermission("dshop.admin.createshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","createshop"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds create <shopname> [<permission>]");
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.CREATESHOP2"));

            player.sendMessage("");
        }
        else if(helpcode.equals("deleteshop") && player.hasPermission("dshop.admin.deleteshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","§c§ldeleteshop§f§r"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds deleteshop <shopname>");

            player.sendMessage("");
        }
        else if(helpcode.equals("mergeshop") && player.hasPermission("dshop.admin.mergeshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","mergeshop"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds mergeshop <shop1> <shop2>");

            player.sendMessage("");
        }
        else if(helpcode.equals("renameshop") && player.hasPermission("dshop.admin.renameshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","renameshop"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds renameshop <old name> <new name>");

            player.sendMessage("");
        }
        else if(helpcode.equals("permission") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","permission"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> permission [<true | false | custom >]");

            player.sendMessage("");
        }
        else if(helpcode.equals("maxpage") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","maxpage"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> maxpage <number>");

            player.sendMessage("");
        }
        else if(helpcode.equals("flag") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","flag"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> flag <flag> <set | unset>");

            player.sendMessage("");
        }
        else if(helpcode.equals("position") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","position"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> position <pos1 | pos2 | clear>");

            player.sendMessage("");
        }
        else if(helpcode.equals("shophours") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","shophours"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> shophours <open> <close>");

            player.sendMessage("");
        }
        else if(helpcode.equals("fluctuation") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","fluctuation"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> fluctuation <interval> <strength>");

            player.sendMessage("");
        }
        else if(helpcode.equals("stockStabilizing") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","stockStabilizing"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> stockStabilizing <interval> <strength>");

            player.sendMessage("");
        }
        else if(helpcode.equals("hideStock") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","hideStock"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> hideStock <true | false>");

            player.sendMessage("");
        }
        else if(helpcode.equals("hidePricingType") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","hidePricingType"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> hidePricingType <true | false>");

            player.sendMessage("");
        }
        else if(helpcode.equals("account") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","account"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account <set | linkto | transfer>");

            player.sendMessage("");
        }
        else if(helpcode.equals("accountSet") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","account set"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account set <amount>");
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        }
        else if(helpcode.equals("accountLinkto") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","account linkto"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account linkto <shopname>");
            //player.sendMessage(" - " + ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        }
        else if(helpcode.equals("accountTransfer") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","account transfer"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account transfer <target> <amount>");
            //player.sendMessage(" - " + ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        }
        else if(helpcode.equals("settax") && player.hasPermission("dshop.admin.settax"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","settax"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds settax <value>");

            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","settax temp"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds settax temp <tax_value> <minutes_until_reset>");

            player.sendMessage("");
        }
        else if(helpcode.equals("setdefaultshop") && player.hasPermission("dshop.admin.setdefaultshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","setdefaultshop"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds setdefaultshop <shop name>");

            player.sendMessage("");
        }
        else if(helpcode.equals("deleteOldUser") && player.hasPermission(Constants.DELETE_USER_PERMISSION))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","§c§ldeleteOldUser§f§r"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds deleteOldUser <days>");
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.DELETE_OLD_USER"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("IRREVERSIBLE"));

            player.sendMessage("");
        }
        else if(helpcode.equals("convert") && player.hasPermission("dshop.admin.convert"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","convert"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds convert <plugin name>");
            player.sendMessage(" - " + "This is beta feature. Currently only support 'Shop'");
            player.sendMessage(" - " + "You need to Copy pages yml file to DynamicShop/Convert/Shop");
            player.sendMessage(" - " + "Item meta will be lost");

            player.sendMessage("");
        }
        else if(helpcode.equals("sellbuy") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","sellbuy"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shop name> sellbuy < sellonly | buyonly | clear >");

            player.sendMessage("");
        }
        else if(helpcode.equals("log") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.TITLE").replace("{command}","log"));
            player.sendMessage(" - " + LangUtil.ccLang.get().getString("HELP.USAGE") + ": /ds shop <shop name> log < enable | disable | clear >");

            player.sendMessage("");
        }
    }
}
