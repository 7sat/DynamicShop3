package me.sat7.dynamicshop.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.LangUtil;

public final class DeleteUser {
    private DeleteUser() {

    }

    static void deleteUser(String[] args, Player player) {
        if(!player.hasPermission(Constants.DELETE_USER_PERMISSION))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
            return;
        }

        if(args.length != 2)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
            return;
        }

        long day = 99999L;

        try
        {
            day = Long.parseLong(args[1]);
        }catch (Exception e){
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
            return;
        }

        if(day <= 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.VALUE_ZERO"));
            return;
        }

        int count = 0;
        for (String s:DynamicShop.ccUser.get().getKeys(false))
        {
            try
            {
                long lastJoinLong = DynamicShop.ccUser.get().getLong(s+".lastJoin");

                long dayPassed = (System.currentTimeMillis() - lastJoinLong) / 86400000L;

                // 마지막으로 접속한지 입력한 일보다 더 지남.
                if(dayPassed > day)
                {
                    player.sendMessage(DynamicShop.dsPrefix + Bukkit.getOfflinePlayer(UUID.fromString(s)).getName() + " Deleted");
                    DynamicShop.ccUser.get().set(s,null);
                    count += 1;
                }
            }catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix + e + "/" + s);
            }

            DynamicShop.ccUser.save();
        }

        player.sendMessage(DynamicShop.dsPrefix + count + " Items Removed");
    }
}
