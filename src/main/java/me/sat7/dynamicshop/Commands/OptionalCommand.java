package me.sat7.dynamicshop.Commands;

import me.sat7.dynamicshop.DynamicShop;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OptionalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!DynamicShop.plugin.getConfig().getBoolean("UseShopCommand")) return true;

        if(sender instanceof Player)
        {
            Player player = (Player)sender;
            if(player.getGameMode() == GameMode.CREATIVE)
            {
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
