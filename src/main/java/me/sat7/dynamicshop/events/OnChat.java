package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.utilities.ShopUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public class OnChat implements Listener
{

    private static final Map<UUID, Integer> runnableMap = new HashMap<>();

    public static void WaitForInput(Player player)
    {
        if (runnableMap.containsKey(player.getUniqueId()))
        {
            cancelRunnable(player);
        }

        BukkitTask taskID = Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, () ->
        {
            UUID uuid = player.getUniqueId();
            String userData = DynamicShop.userTempData.get(uuid);

            if (userData.equals("waitforPalette"))
            {
                DynamicShop.userTempData.put(uuid, "");
                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.SEARCH_CANCELED"));
            } else if (userData.contains("waitforInput"))
            {
                DynamicShop.userTempData.put(uuid, "");
                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.INPUT_CANCELED"));
            } else if (userData.equals("waitforPageDelete"))
            {
                DynamicShop.userTempData.put(uuid, "");
                player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.INPUT_CANCELED"));
            }

        }, 400);
        runnableMap.put(player.getUniqueId(), taskID.getTaskId());
    }

    private static void cancelRunnable(Player player)
    {
        if (runnableMap.containsKey(player.getUniqueId()))
        {
            Bukkit.getScheduler().cancelTask(runnableMap.get(player.getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if(!DynamicShop.userTempData.containsKey(uuid))
            return;

        String userData = DynamicShop.userTempData.get(uuid);

        if (userData.equals("waitforPalette"))
        {
            e.setCancelled(true);

            String[] userInteractData = DynamicShop.userInteractItem.get(p.getUniqueId()).split("/");
            DynamicShop.userTempData.put(uuid, "");
            DynaShopAPI.openItemPalette(p, userInteractData[0], Integer.parseInt(userInteractData[1]), 1, e.getMessage());
            cancelRunnable(p);
        } else if (userData.contains("waitforInput"))
        {
            e.setCancelled(true);

            String s = userData.replace("waitforInput", "");
            String[] temp = DynamicShop.userInteractItem.get(uuid).split("/");

            switch (s)
            {
                case "btnName":
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".displayName", "§3" + e.getMessage());
                    break;
                case "btnLore":
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".lore", "§f" + e.getMessage());
                    break;
                case "btnIcon":
                    try
                    {
                        Material tempMat = Material.getMaterial(ChatColor.stripColor(e.getMessage()).toUpperCase());
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".icon", tempMat.name());
                    } catch (Exception exception)
                    {
                        p.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_ITEM_NAME"));
                    }
                    break;
                case "btnAction":
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", ChatColor.stripColor(e.getMessage()));
                    break;
                case "shopname":
                    if (!ShopUtil.shopConfigFiles.containsKey(e.getMessage()))
                    {
                        p.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
                        return;
                    }

                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".displayName", "§3" + e.getMessage());
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".lore", t("START_PAGE.DEFAULT_SHOP_LORE"));
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", "ds shop " + e.getMessage());
                    break;
                case "deco":
                    try
                    {
                        //Material.RED_STAINED_GLASS_PANE;
                        Material mat = Material.valueOf(e.getMessage().toUpperCase() + "_STAINED_GLASS_PANE");
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".displayName", null);
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".lore", null);
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".icon", mat.name());
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", "");
                    } catch (Exception exception)
                    {
                        p.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                    }
                    break;
            }

            StartPage.ccStartPage.save();

            DynamicShop.userTempData.put(uuid, "");
            DynaShopAPI.openStartPage(p);
            cancelRunnable(p);
        } else if (userData.contains("waitforPageDelete"))
        {
            e.setCancelled(true);

            if (e.getMessage().equals("delete"))
            {
                String[] temp = DynamicShop.userInteractItem.get(uuid).split("/");
                ShopUtil.deleteShopPage(temp[0], Integer.parseInt(temp[1]));
                DynaShopAPI.openShopGui(p, temp[0], 1);
            } else
            {
                p.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.INPUT_CANCELED"));
            }

            DynamicShop.userTempData.put(uuid, "");
            cancelRunnable(p);
        }
    }
}
