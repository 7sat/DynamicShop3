package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.utilities.ShopUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

            if (userData.contains("waitforPalette"))
            {
                DynamicShop.userTempData.put(uuid, "");
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SEARCH_CANCELED"));
            } else if (userData.contains("waitforInput"))
            {
                DynamicShop.userTempData.put(uuid, "");
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.INPUT_CANCELED"));
            } else if (userData.equals("waitforPageDelete") || userData.equals("sellCmd") || userData.equals("buyCmd"))
            {
                DynamicShop.userTempData.put(uuid, "");
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.INPUT_CANCELED"));
            }

        }, 600);
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

        if (userData.contains("waitforPalette"))
        {
            e.setCancelled(true);

            String s = userData.replace("waitforPalette", "");
            int subType = Integer.parseInt(s);

            String[] userInteractData = DynamicShop.userInteractItem.get(p.getUniqueId()).split("/");
            DynamicShop.userTempData.put(uuid, "");
            DynaShopAPI.openItemPalette(p, subType, userInteractData[0], Integer.parseInt(userInteractData[1]), 1, e.getMessage());
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
                case "btnAction":
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", ChatColor.stripColor(e.getMessage()));
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
                int targetPage = Integer.parseInt(temp[1]);
                ShopUtil.deleteShopPage(temp[0], targetPage);

                int openPage = Clamp(targetPage, 1, ShopUtil.GetShopMaxPage(temp[0]));
                DynamicShop.userInteractItem.put(uuid, temp[0] + "/" + openPage);

                DynaShopAPI.openPageEditor(p, temp[0], openPage);
            } else
            {
                p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "MESSAGE.INPUT_CANCELED"));
            }

            DynamicShop.userTempData.put(uuid, "");
            cancelRunnable(p);
        } else if (userData.equals("sellCmd") || userData.equals("buyCmd"))
        {
            e.setCancelled(true);

            String[] userInteractData = DynamicShop.userInteractItem.get(p.getUniqueId()).split("/");
            String shopName = userInteractData[0];
            DynamicShop.userTempData.put(uuid, "");

            String[] input = e.getMessage().split("/");
            if(input.length == 2)
            {
                int idx = 0;
                try
                {
                    idx = Integer.parseInt(input[0]);
                }catch (Exception ignore)
                {
                    p.sendMessage(t(p,"ERR.WRONG_DATATYPE"));
                    DynaShopAPI.openShopSettingGui(p, shopName);
                    cancelRunnable(p);
                    return;
                }

                if (userData.equals("sellCmd"))
                {
                    ShopUtil.SetShopSellCommand(shopName, idx, input[1]);
                }
                else
                {
                    ShopUtil.SetShopBuyCommand(shopName, idx, input[1]);
                }
            }
            else
            {
                p.sendMessage(t(p,"ERR.WRONG_USAGE"));
            }

            DynaShopAPI.openShopSettingGui(p, shopName);

            cancelRunnable(p);
        }
    }
}
