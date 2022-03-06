package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class DSCMD
{
    public boolean inGameUseOnly = true;
    public String permission;

    public final ArrayList<Integer> validArgCount = new ArrayList<>();

    public void SendHelpMessage(Player player)
    {
    }

    public boolean CheckValid(String[] args, CommandSender sender)
    {
        if (inGameUseOnly && !(sender instanceof Player))
        {
            sender.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " You can't run this command in console");
            return false;
        }

        if (!validArgCount.contains(args.length))
        {
            if (validArgCount.size() != 0 && Collections.max(validArgCount) >= args.length)
            {
                sender.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                return false;
            }
        }

        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission))
        {
            sender.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
            return false;
        }

        return true;
    }

    public void RunCMD(String[] args, CommandSender sender)
    {
    }
}
