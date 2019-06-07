package me.sat7.dynamicshop.Events;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.DynaShopAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnChat implements Listener {

    public static Map<UUID, Integer> runnableMap = new HashMap<>();

    public static void WaitForInput(Player player)
    {
        if(runnableMap.containsKey(player.getUniqueId()))
        {
            cancelRunnable(player);
        }

        BukkitTask taskID = Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, () -> {

            if(DynamicShop.ccUser.get().getString(player.getUniqueId()+".tmpString").equals("waitforPalette"))
            {
                DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
                player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("SEARCH_CANCELED"));
            }
            else if(DynamicShop.ccUser.get().getString(player.getUniqueId()+".tmpString").contains("waitforInput"))
            {
                DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
                player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("INPUT_CANCELED"));
            }
            else if(DynamicShop.ccUser.get().getString(player.getUniqueId()+".tmpString").equals("waitforPageDelete"))
            {
                DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
                player.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("INPUT_CANCELED"));
            }

        },200);
        runnableMap.put(player.getUniqueId(), taskID.getTaskId());
    }

    public static void cancelRunnable(Player player) {
        if(runnableMap.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(runnableMap.get(player.getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e)
    {
        Player p = e.getPlayer();

        if(DynamicShop.ccUser.get().getString(p.getUniqueId()+".tmpString").equals("waitforPalette"))
        {
            e.setCancelled(true);

            DynamicShop.ccUser.get().set(p.getUniqueId()+".tmpString","");
            DynaShopAPI.OpenItemPalette(p,1,e.getMessage());
            cancelRunnable(p);
        }
        else if(DynamicShop.ccUser.get().getString(p.getUniqueId()+".tmpString").contains("waitforInput"))
        {
            e.setCancelled(true);

            String s = DynamicShop.ccUser.get().getString(p.getUniqueId()+".tmpString").replace("waitforInput","");
            String[] temp = DynamicShop.ccUser.get().getString(p.getUniqueId()+".interactItem").split("/");

            if(s.equals("btnName"))
            {
                DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".displayName",e.getMessage());
            }
            else if(s.equals("btnLore"))
            {
                DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".lore",e.getMessage());
            }
            else if(s.equals("btnIcon"))
            {
                try
                {
                    Material tempMat = Material.getMaterial(e.getMessage().toUpperCase());
                    DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".icon",tempMat.name());
                }
                catch (Exception exception)
                {
                    p.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("ERR.WRONG_ITEMNAME"));
                }
            }
            else if(s.equals("btnAction"))
            {
                DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".action",e.getMessage());
            }
            else if(s.equals("shopname"))
            {
                ConfigurationSection btnSec = DynamicShop.ccStartpage.get().getConfigurationSection("Buttons");

                if(!DynamicShop.ccShop.get().contains(e.getMessage()))
                {
                    p.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
                    return;
                }

                DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".displayName", "§3"+e.getMessage());
                DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".lore",DynamicShop.ccLang.get().getString("STARTPAGE.DEFAULT_SHOP_LORE"));
                DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".icon",Material.EMERALD.name());
                DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".action","ds shop " + e.getMessage());
            }
            else if(s.equals("deco"))
            {
                try
                {
                    //Material.RED_STAINED_GLASS_PANE;
                    Material mat = Material.valueOf(e.getMessage().toUpperCase()+"_STAINED_GLASS_PANE");
                    DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".displayName",null);
                    DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".lore",null);
                    DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".icon",mat.name());
                    DynamicShop.ccStartpage.get().set("Buttons." + temp[1] + ".action","");
                }
                catch (Exception exception)
                {
                    p.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("ERR.WRONG_USAGE"));
                }
            }

            DynamicShop.ccStartpage.save();

            DynamicShop.ccUser.get().set(p.getUniqueId()+".tmpString","");
            DynaShopAPI.OpenStartPage(p);
            cancelRunnable(p);
        }
        else if(DynamicShop.ccUser.get().getString(p.getUniqueId()+".tmpString").contains("waitforPageDelete"))
        {
            e.setCancelled(true);

            if(e.getMessage().equals("delete"))
            {
                String[] temp = DynamicShop.ccUser.get().getString(p.getUniqueId()+".interactItem").split("/");
                DynaShopAPI.DeleteShopPage(temp[0],Integer.parseInt(temp[1]));
                DynaShopAPI.OpenShopGUI(p,temp[0],1);
            }
            else
            {
                p.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("INPUT_CANCELED"));
            }

            DynamicShop.ccUser.get().set(p.getUniqueId()+".interactItem","");
            DynamicShop.ccUser.get().set(p.getUniqueId()+".tmpString","");
            cancelRunnable(p);
        }
    }
}
