package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.UpdateCheck;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".tmpString","");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".interactItem","");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".lastJoin",System.currentTimeMillis());
        DynamicShop.ccUser.get().addDefault(player.getUniqueId().toString()+".cmdHelp",true);
        DynamicShop.ccUser.save();

        if(DynamicShop.updateAvailable)
        {
            if(e.getPlayer().hasPermission("dshop.admin.shopedit") ||
                    e.getPlayer().hasPermission("dshop.admin.reload"))
            {
                e.getPlayer().sendMessage(DynamicShop.dsPrefix+"New update available!");
                e.getPlayer().sendMessage(UpdateCheck.getResourceUrl());
            }
        }
    }
}
