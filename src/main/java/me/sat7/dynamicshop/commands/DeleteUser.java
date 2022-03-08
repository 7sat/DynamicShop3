package me.sat7.dynamicshop.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_DELETE_OLD_USER;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class DeleteUser extends DSCMD
{
    public DeleteUser()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_DELETE_OLD_USER;
        validArgCount.add(2);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "§c§ldeleteOldUser§f§r"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds deleteOldUser <days>");
        player.sendMessage(" - " + t(player, "HELP.DELETE_OLD_USER"));
        player.sendMessage(" - " + t(player, "MESSAGE.IRREVERSIBLE"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        long day;

        try
        {
            day = Long.parseLong(args[1]);
        } catch (Exception e)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
            return;
        }

        if (day <= 0)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.VALUE_ZERO"));
            return;
        }

        int count = 0;
        for (String s : DynamicShop.ccUser.get().getKeys(false))
        {
            try
            {
                long lastJoinLong = DynamicShop.ccUser.get().getLong(s + ".lastJoin");

                long dayPassed = (System.currentTimeMillis() - lastJoinLong) / 86400000L;

                // 마지막으로 접속한지 입력한 일보다 더 지남.
                if (dayPassed > day)
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + Bukkit.getOfflinePlayer(UUID.fromString(s)).getName() + " Deleted");
                    DynamicShop.ccUser.get().set(s, null);
                    count += 1;
                }
            } catch (Exception e)
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + e + "/" + s);
            }

            DynamicShop.ccUser.save();
        }

        sender.sendMessage(DynamicShop.dsPrefix(sender) + count + " Items Removed");
    }
}
