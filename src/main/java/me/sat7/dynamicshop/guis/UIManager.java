package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class UIManager implements Listener
{
    private static final HashMap<Player, InGameUI> currentUI = new HashMap<>();

    public static void DebugLog()
    {
        DynamicShop.console.sendMessage("currentUI: size" + currentUI.size());
        for(Map.Entry<Player, InGameUI> entry : currentUI.entrySet())
            DynamicShop.console.sendMessage(entry.getKey() + ": " + entry.getValue().uiType);
    }

    @SuppressWarnings("EmptyMethod")
    @EventHandler
    public void OnOpen(InventoryOpenEvent e)
    {

    }

    @EventHandler
    public void OnClose(InventoryCloseEvent e)
    {
        // 기존에 인벤토리가 열려있는 상태에서 다른것을 열면 close가 먼저 불림.
        Player player = (Player) e.getPlayer();
        currentUI.remove(player);
    }

    public static void OnPlayerQuit(Player p)
    {
        currentUI.remove(p);
    }

    public static void Open(Player player, Inventory inventory, InGameUI inGameUI)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                player.openInventory(inventory); // 가장 먼저 불려야함. (버킷에서 새 인벤이 열릴때 기존의 것이 닫힘처리됨)

                currentUI.put(player, inGameUI);
            }
        }.runTask(DynamicShop.plugin);
    }

    public static boolean IsPlayerUsingPluginGUI(Player player)
    {
        if (player == null)
            return false;

        return currentUI.get(player) != null;
    }

    public static InGameUI.UI_TYPE GetPlayerCurrentUIType(Player player)
    {
        if (player == null)
            return null;

        if (currentUI.get(player) == null)
            return null;

        return currentUI.get(player).uiType;
    }

    public static void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        InGameUI inGameUI = currentUI.get(player);
        if (inGameUI == null)
            return;

        SoundUtil.playerSoundEffect(player, "click");
        inGameUI.OnClickUpperInventory(e);
    }

    public static void OnClickLowerInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        InGameUI inGameUI = currentUI.get(player);
        if (inGameUI == null)
            return;

        SoundUtil.playerSoundEffect(player, "click");
        inGameUI.OnClickLowerInventory(e);
    }

    public static void RefreshUI()
    {
        HashMap<Player, InGameUI> clone = (HashMap<Player, InGameUI>)currentUI.clone();
        for (Map.Entry<Player, InGameUI> entry : clone.entrySet())
        {
            Player p = entry.getKey();
            InGameUI ui = entry.getValue();

            if(p == null || ui == null)
                continue;

            if (ui.uiType == InGameUI.UI_TYPE.ItemTrade
                || ui.uiType == InGameUI.UI_TYPE.Shop)
            {
                ui.RefreshUI();
            }
        }
    }
}
