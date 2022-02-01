package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.UpdateChecker;

import me.sat7.dynamicshop.guis.UIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuit implements Listener
{

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        DynamicShop.userTempData.put(player.getUniqueId(), "");
        DynamicShop.userInteractItem.put(player.getUniqueId(), "");
        DynamicShop.ccUser.get().set(player.getUniqueId() + ".lastJoin", System.currentTimeMillis());
        DynamicShop.ccUser.get().addDefault(player.getUniqueId() + ".cmdHelp", true);
        DynamicShop.ccUser.save();

        if (DynamicShop.updateAvailable)
        {
            if (e.getPlayer().hasPermission("dshop.admin.shopedit") ||
                    e.getPlayer().hasPermission("dshop.admin.reload"))
            {
                e.getPlayer().sendMessage(DynamicShop.dsPrefix + "New update available!");
                e.getPlayer().sendMessage(UpdateChecker.getResourceUrl());
            }
        }
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e)
    {
        UIManager.OnPlayerQuit(e.getPlayer());
        DynamicShop.userTempData.remove(e.getPlayer().getUniqueId());
        DynamicShop.userInteractItem.remove(e.getPlayer().getUniqueId());
    }
}
