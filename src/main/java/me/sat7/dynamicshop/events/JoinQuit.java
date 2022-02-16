package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.UpdateChecker;

import me.sat7.dynamicshop.guis.UIManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
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

        //if (DynamicShop.updateAvailable)
        {
            if (e.getPlayer().hasPermission("dshop.admin.shopedit") || e.getPlayer().hasPermission("dshop.admin.reload"))
            {
                TextComponent text = new TextComponent("");
                text.addExtra(DynamicShop.CreateLink("DShop3", false, ChatColor.DARK_AQUA, UpdateChecker.getResourceUrl()));
                text.addExtra(" ");
                text.addExtra(DynamicShop.CreateLink("Download", false, ChatColor.WHITE, UpdateChecker.getResourceUrl()));
                text.addExtra(" ");
                text.addExtra(DynamicShop.CreateLink("Donate", false, ChatColor.WHITE, "https://www.paypal.com/paypalme/7sat"));

                e.getPlayer().sendMessage("");
                e.getPlayer().spigot().sendMessage(text);
                e.getPlayer().sendMessage("§7Latest version: §f" + DynamicShop.lastVersion);
                e.getPlayer().sendMessage("§7Your version: §f" + DynamicShop.yourVersion);
                e.getPlayer().sendMessage("");
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
