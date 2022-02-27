package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynamicShop;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class DSCMD
{
    public String permission;

    public final ArrayList<Integer> validArgCount = new ArrayList<>();

    public void SendHelpMessage(Player player){}

    public boolean CheckValid(String[] args, Player player)
    {
        if(!validArgCount.contains(args.length))
        {
            if(validArgCount.size() != 0 && Collections.max(validArgCount) >= args.length)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                return false;
            }
        }

        if (permission != null && !permission.isEmpty() && !player.hasPermission(permission))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
            return false;
        }

        return true;
    }

    public void RunCMD(String[] args, Player player){}
}
