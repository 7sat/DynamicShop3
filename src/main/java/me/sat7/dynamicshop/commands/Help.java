package me.sat7.dynamicshop.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class Help
{
    private Help()
    {

    }

    // 명령어 도움말 표시
    public static void showHelp(String helpcode, Player player, String[] args)
    {
        if (!DynamicShop.ccUser.get().getBoolean(player.getUniqueId() + ".cmdHelp")) return;

        if (helpcode.equals("main"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "main"));
            player.sendMessage(" - shop: " + t("HELP.SHOP"));
            player.sendMessage(" - qsell: " + t("HELP.QSELL"));
            player.sendMessage(" - cmdHelp: " + t("HELP.CMD"));
            if (player.hasPermission("dshop.admin.createshop"))
                player.sendMessage("§e - createshop: " + t("HELP.CREATE_SHOP"));
            if (player.hasPermission("dshop.admin.deleteshop"))
                player.sendMessage("§e - deleteshop: " + t("HELP.DELETE_SHOP"));
            if (player.hasPermission("dshop.admin.reload"))
                player.sendMessage("§e - reload: " + t("HELP.RELOAD"));
            player.sendMessage("");
        } else if (helpcode.equals("shop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "shop"));

            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop [<shopname>]");
            if (player.hasPermission("dshop.admin.shopedit") || player.hasPermission("dshop.admin.shopedit") || player.hasPermission("dshop.admin.editall"))
            {
                player.sendMessage(" - " + t("HELP.USAGE")
                        + ": /ds shop <shopname> <enable | addhand | add | edit | editall | setToRecAll | sellbuy | permission | maxpage | flag | position | shophours | fluctuation | stockStabilizing | account | log>");
            }

            if (player.hasPermission("dshop.admin.shopedit"))
            {
                player.sendMessage("§e - enable: " + t("HELP.SHOP_ENABLE"));
                player.sendMessage("§e - addhand: " + t("HELP.SHOP_ADD_HAND"));
                player.sendMessage("§e - add: " + t("HELP.SHOP_ADD_ITEM"));
                player.sendMessage("§e - edit: " + t("HELP.SHOP_EDIT"));
            }

            if (player.hasPermission("dshop.admin.editall"))
                player.sendMessage("§e - editall: " + t("HELP.EDIT_ALL"));
            if (player.hasPermission("dshop.admin.shopedit"))
                player.sendMessage("§e - setToRecAll: " + t("HELP.SET_TO_REC_ALL"));
            player.sendMessage("");
        } else if (helpcode.equals("open_shop") && player.hasPermission("dshop.admin.openshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "openshop"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds openshop [shopname] <playername>");
            player.sendMessage("");
        } else if (helpcode.equals("enable") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "enable"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> <true|false>");
            player.sendMessage("");
        } else if (helpcode.equals("add_hand") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "addhand"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> addhand <value> <median> <stock>");
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> addhand <value> <min value> <max value> <median> <stock>");
            player.sendMessage(" - " + t("HELP.SHOP_ADD_HAND"));
            player.sendMessage(" - " + t("HELP.PRICE"));
            player.sendMessage(" - " + t("HELP.INF_STATIC"));

            ItemStack tempItem = player.getInventory().getItemInMainHand();

            if (tempItem.getType() != Material.AIR)
            {
                int idx = ShopUtil.findItemFromShop(args[1], tempItem);
                if (idx != -1)
                {
                    player.sendMessage("");
                    ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_ALREADY_EXIST");
                }
            } else
            {
                player.sendMessage(" - " + t("ERR.HAND_EMPTY2"));
            }

            player.sendMessage("");
        } else if (helpcode.startsWith("add") && player.hasPermission("dshop.admin.shopedit"))
        {
            if (helpcode.length() > "add".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3]));
                    int idx = ShopUtil.findItemFromShop(args[1], tempItem);

                    if (idx != -1)
                    {
                        ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_ALREADY_EXIST");
                        player.sendMessage("");
                    }
                } catch (Exception ignored)
                {
                }
            } else
            {
                player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "add"));
                player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <median> <stock>");
                player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <min value> <max value> <median> <stock>");
                player.sendMessage(" - " + t("HELP.SHOP_ADD_ITEM"));
                player.sendMessage(" - " + t("HELP.PRICE"));
                player.sendMessage(" - " + t("HELP.INF_STATIC"));

                player.sendMessage("");
            }
        } else if (helpcode.contains("edit") && !helpcode.equals("edit_all") && player.hasPermission("dshop.admin.shopedit"))
        {
            if (helpcode.length() > "edit".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3].substring(args[3].indexOf("/") + 1)));
                    int idx = ShopUtil.findItemFromShop(args[1], tempItem);

                    if (idx != -1)
                    {
                        ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_INFO");
                        player.sendMessage(" - " + t("HELP.REMOVE_ITEM"));
                        player.sendMessage("");
                    }
                } catch (Exception ignored)
                {
                }
            } else
            {
                player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "edit"));
                player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <median> <stock>");
                player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <min value> <max value> <median> <stock> [<max stock>]");
                player.sendMessage(" - " + t("HELP.SHOP_EDIT"));
                player.sendMessage(" - " + t("HELP.PRICE"));
                player.sendMessage(" - " + t("HELP.INF_STATIC"));

                player.sendMessage("");
            }
        } else if (helpcode.equals("edit_all") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "editall"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> editall <value | median | stock | max stock> <= | + | - | * | /> <amount>");
            player.sendMessage(" - " + t("HELP.EDIT_ALL"));
            player.sendMessage(" - " + t("HELP.EDIT_ALL_2"));

            player.sendMessage("");
        } else if (helpcode.equals("set_to_rec_all") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "SetToRecAll"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> SetToRecAll");
            player.sendMessage(" - " + t("HELP.SET_TO_REC_ALL"));

            player.sendMessage("");
        } else if (helpcode.equals("cmd_help"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "cmdHelp"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds cmdHelp <on | off>");
            player.sendMessage(" - " + t("HELP.CMD"));

            player.sendMessage("");
        } else if (helpcode.equals("create_shop") && player.hasPermission("dshop.admin.createshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "createshop"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds create <shopname> [<permission>]");
            player.sendMessage(" - " + t("HELP.CREATE_SHOP_2"));

            player.sendMessage("");
        } else if (helpcode.equals("delete_shop") && player.hasPermission("dshop.admin.deleteshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "§c§ldeleteshop§f§r"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds deleteshop <shopname>");

            player.sendMessage("");
        } else if (helpcode.equals("merge_shop") && player.hasPermission("dshop.admin.mergeshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "mergeshop"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds mergeshop <shop1> <shop2>");

            player.sendMessage("");
        } else if (helpcode.equals("rename_shop") && player.hasPermission("dshop.admin.renameshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "renameshop"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds renameshop <old name> <new name>");

            player.sendMessage("");
        } else if (helpcode.equals("permission") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "permission"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> permission [<true | false | custom >]");

            player.sendMessage("");
        } else if (helpcode.equals("max_page") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "maxpage"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> maxpage <number>");

            player.sendMessage("");
        } else if (helpcode.equals("flag") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "flag"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> flag <flag> <set | unset>");

            player.sendMessage("");
        } else if (helpcode.equals("position") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "position"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> position <pos1 | pos2 | clear>");

            player.sendMessage("");
        } else if (helpcode.equals("shophours") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "shophours"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> shophours <open> <close>");

            player.sendMessage("");
        } else if (helpcode.equals("fluctuation") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "fluctuation"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> fluctuation <interval> <strength>");
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> fluctuation off");

            player.sendMessage("");
        } else if (helpcode.equals("stock_stabilizing") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "stockStabilizing"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> stockStabilizing <interval> <strength>");
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> stockStabilizing off");

            player.sendMessage("");
        }else if (helpcode.equals("account") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "account"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> account <set | linkto | transfer>");

            player.sendMessage("");
        } else if (helpcode.equals("account_set") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "account set"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> account set <amount>");
            player.sendMessage(" - " + t("HELP.ACCOUNT"));

            player.sendMessage("");
        } else if (helpcode.equals("account_link_to") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "account linkto"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> account linkto <shopname>");
            //player.sendMessage(" - " + ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        } else if (helpcode.equals("account_transfer") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "account transfer"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> account transfer <target> <amount>");
            //player.sendMessage(" - " + ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        } else if (helpcode.equals("set_tax") && player.hasPermission("dshop.admin.settax"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "settax"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds settax <value>");

            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "settax temp"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds settax temp <tax_value> <minutes_until_reset>");

            player.sendMessage("");
        } else if (helpcode.equals("set_default_shop") && player.hasPermission("dshop.admin.setdefaultshop"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "setdefaultshop"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds setdefaultshop <shop name>");

            player.sendMessage("");
        } else if (helpcode.equals("delete_old_user") && player.hasPermission(Constants.P_ADMIN_DELETE_OLD_USER))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "§c§ldeleteOldUser§f§r"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds deleteOldUser <days>");
            player.sendMessage(" - " + t("HELP.DELETE_OLD_USER"));
            player.sendMessage(" - " + t("MESSAGE.IRREVERSIBLE"));

            player.sendMessage("");
        } else if (helpcode.equals("sellbuy") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "sellbuy"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shop name> sellbuy < sellonly | buyonly | clear >");

            player.sendMessage("");
        } else if (helpcode.equals("log") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "log"));
            player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shop name> log < enable | disable | clear >");

            player.sendMessage("");
        }
    }
}
