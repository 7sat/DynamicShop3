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

        if (!player.hasPermission(Constants.P_SELL))
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
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack == null || itemStack.getType().isAir())
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.HAND_EMPTY"));
                return false;
            }

            if (0 == DynaShopAPI.QuickSell(player, itemStack, player.getInventory().getHeldItemSlot()))
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL_2"));
                return false;
            }

            return true;
        }
        else if (args[0].equalsIgnoreCase("handall"))
        {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack == null || itemStack.getType().isAir())
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.HAND_EMPTY"));
                return false;
            }

            if (0 == DynaShopAPI.QuickSell(player, itemStack))
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL_2"));
                return false;
            }

            return true;
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            ArrayList<String> temp = new ArrayList<>();
            double sum = 0;

            for (ItemStack stack : player.getInventory().getStorageContents())
            {
                if (stack == null || stack.getType().isAir())
                    continue;

                String hash = HashUtil.GetItemHash(stack);
                if (temp.contains(hash))
                    continue;

                sum += DynaShopAPI.QuickSell(player, stack);
                temp.add(hash);
            }

            if (sum == 0)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL_2"));
                return false;
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
