package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.LangUtil;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Root implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            if (!args[0].equalsIgnoreCase("openshop")) {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " You can't run this command in console");
                return true;
            } else {
                return OpenShop.openShop(args, sender);
            }
        }
        Player player = (Player)sender;

        if (!player.hasPermission(Constants.USE_SHOP_PERMISSION)) {
            player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().getString("ERR.PERMISSION"));
            return true;
        }
        if(player.getGameMode() == GameMode.CREATIVE  && !player.hasPermission(Constants.ADMIN_CREATIVE_PERMISSION))
        {
            player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().getString("ERR.CREATIVE"));
            return true;
        }

        // user.yml 에 player가 없으면 재생성 시도. 실패시 리턴.
        if(!DynaShopAPI.recreateUserData(player))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_USER_ID"));
            return true;
        }

        // 스타트페이지
        if(args.length == 0)
        {
            DynamicShop.ccUser.get().set(player.getUniqueId()+".interactItem","");
            DynaShopAPI.openStartPage(player);
            return true;
        }
        // Start page 버튼 테스트용 함수
        else if(args[0].equalsIgnoreCase("Testfunction"))
        {
            player.sendMessage(DynamicShop.dsPrefix+"button clicked!");
            return true;
        }
        else if(args[0].equalsIgnoreCase("close"))
        {
            player.closeInventory();
            return true;
        }
        // ds shop [<shopName>]
        else if(args[0].equalsIgnoreCase("shop") && Shop.shopCommand(args, player)) {
            return true;
        }

        // qsell
        else if(args[0].equalsIgnoreCase("qsell"))
        {
            if (sender.hasPermission(Constants.USE_QSELL_PERMISSION)) {
                DynaShopAPI.openQuickSellGUI(player);
            }
            return true;
        }

        // cmdhelp
        else if(args[0].equalsIgnoreCase("cmdhelp"))
        {
            return CommandHelp.commandHelp(args, player);
        }

        // createShop
        else if(args[0].equalsIgnoreCase("createshop"))
        {
            return CreateShop.createShop(args, player);
        }

        if (args[0].equalsIgnoreCase("openshop")) {
            return OpenShop.openShop(args, sender);
        }

        // deleteShop
        else if(args[0].equalsIgnoreCase("deleteshop"))
        {
            if (DeleteShop.deleteShop(args, player)) return true;
        }

        // RenameShop
        else if(args[0].equalsIgnoreCase("renameshop"))
        {
            if (RenameShop.renameShop(args, player)) return true;
        }

        // MergeShop
        else if(args[0].equalsIgnoreCase("mergeshop"))
        {
            if (MergeShop.mergeShop(args, player)) return true;
        }

        // setdefaultshop
        else if(args[0].equalsIgnoreCase("setdefaultshop"))
        {
            if (SetDefaultShop.setDefaultShop(args, player)) return true;
        }

        // Settax
        else if(args[0].equalsIgnoreCase("settax"))
        {
            if (SetTax.setTax(args, player)) return true;
        }

        // Reload
        else if(args[0].equalsIgnoreCase("reload"))
        {
            Reload.reload(player);
            return true;
        }

        // deleteOldUser <day>
        else if(args[0].equalsIgnoreCase("deleteOldUser"))
        {
            DeleteUser.deleteUser(args, player);
            return true;
        }

        // convert Shop
        else if(args[0].equalsIgnoreCase("convert"))
        {
            return Convert.convert(args, player);
        }

        return true;
    }

}

