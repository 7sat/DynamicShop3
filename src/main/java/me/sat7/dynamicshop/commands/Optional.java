package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.LangUtil;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Optional implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!DynamicShop.plugin.getConfig().getBoolean("UseShopCommand")) return true;

        if(sender instanceof Player)
        {
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

            if(args.length == 0)
            {
                Bukkit.dispatchCommand(sender, "DynamicShop shop");
            }
            else
            {
                Bukkit.dispatchCommand(sender, "DynamicShop shop " + args[0]);
            }
        }

        return true;
    }
}
