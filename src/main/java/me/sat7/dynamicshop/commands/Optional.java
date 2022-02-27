package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Optional implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!DynamicShop.plugin.getConfig().getBoolean("Command.UseShopCommand")) return true;

        if (sender instanceof Player)
        {
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

            if (args.length == 0)
            {
                Bukkit.dispatchCommand(sender, "DynamicShop shop");
            } else
            {
                Bukkit.dispatchCommand(sender, "DynamicShop shop " + args[0]);
            }
        }

        return true;
    }
}
