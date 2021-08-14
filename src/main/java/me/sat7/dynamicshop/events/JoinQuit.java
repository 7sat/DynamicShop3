package me.sat7.dynamicshop.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.UpdateCheck;

public class JoinQuit implements Listener {
	
	private JavaPlugin plugin;

	public JoinQuit(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
    	new BukkitRunnable() {
			
			@Override
			public void run() {
		        Player player = e.getPlayer();
		        FileConfiguration config = DynamicShop.ccUser.get(player);
		        config.set("tmpString","");
		        config.set("interactItem","");
		        config.set("lastJoin",System.currentTimeMillis());
		        config.addDefault("cmdHelp",true);
		        DynamicShop.ccUser.save(player);

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
		}.runTaskAsynchronously(plugin);
    }
}
