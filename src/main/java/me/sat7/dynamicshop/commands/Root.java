package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Root implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            if (!args[0].equalsIgnoreCase("openshop"))
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " You can't run this command in console");
            } else
            {
                CMDManager.openShop.RunCMD(args, (Player) sender);
            }
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(Constants.P_USE))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
            return true;
        }

        if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission(Constants.P_ADMIN_CREATIVE))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.CREATIVE"));
            return true;
        }

        // user.yml 에 player가 없으면 재생성 시도. 실패시 리턴.
        if (!DynaShopAPI.recreateUserData(player))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_USER_ID"));
            return true;
        }

        // 스타트페이지
        if (args.length == 0)
        {
            DynaShopAPI.openStartPage(player);
        }
        else if (args[0].equalsIgnoreCase("close"))
        {
            player.closeInventory();
        }
        // ds shop [<shopName>]
        else if (args[0].equalsIgnoreCase("shop"))
        {
            Shop.shopCommand(args, player);
        }
        else if (args[0].equalsIgnoreCase("qsell"))
        {
            if (sender.hasPermission(Constants.P_USE_QSELL))
            {
                DynaShopAPI.openQuickSellGUI(player);
            }
        }
        else
        {
            CMDManager.RunCMD(args[0].toLowerCase(), args, player);
        }

        return true;
    }

}

