package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;

import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.HashUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Sell implements CommandExecutor
{
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "sell"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": sell <hand | handall | all>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (!player.hasPermission(Constants.P_USE))
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_PERMISSION"));
            return true;
        }

        if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission(Constants.P_ADMIN_CREATIVE))
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.CREATIVE"));
            return true;
        }

        if (args.length != 1)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_USAGE"));
            SendHelpMessage(player);
            return false;
        }

        if (args[0].equalsIgnoreCase("hand"))
        {
            DynaShopAPI.QuickSell(player, player.getInventory().getItemInMainHand(), player.getInventory().getHeldItemSlot());
            return true;
        }
        else if (args[0].equalsIgnoreCase("handall"))
        {
            DynaShopAPI.QuickSell(player, player.getInventory().getItemInMainHand());
            return true;
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            ArrayList<String> temp = new ArrayList<>();

            for (ItemStack stack : player.getInventory().getStorageContents())
            {
                if (stack == null || stack.getType().isAir())
                    continue;

                String hash = HashUtil.GetItemHash(stack);
                if (temp.contains(hash))
                    continue;

                DynaShopAPI.QuickSell(player, stack);
                temp.add(hash);
            }

            return true;
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_USAGE"));
            SendHelpMessage(player);
            return false;
        }
    }
}
