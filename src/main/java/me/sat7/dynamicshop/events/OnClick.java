package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.guis.UIManager;
import me.sat7.dynamicshop.utilities.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class OnClick implements Listener
{
    @EventHandler
    public void OnInventoryDragEvent(InventoryDragEvent e)
    {
        // UI 인벤토리에 드래그로 아이탬 올리는것을 막음
        if(UIManager.IsPlayerUsingPluginGUI((Player)e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler
    public void OnInventoryClickEvent(InventoryClickEvent e)
    {
        if (e.getClickedInventory() == null)
            return;

        Player player = (Player) e.getWhoClicked();

        // 클릭된 인벤토리가 내 인벤토리가 아님 (인벤 2개가 상하로 열려있을때, 위쪽 인벤을 클릭함)
        if (e.getClickedInventory() != player.getInventory())
        {
            // UUID 확인
            String pUuid = player.getUniqueId().toString();

            if (DynamicShop.ccUser.get().getConfigurationSection(pUuid) == null)
            {
                if (!DynaShopAPI.recreateUserData(player))
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_USER_ID"));
                    e.setCancelled(true);
                    return;
                }
            }
        }

        if(UIManager.IsPlayerUsingPluginGUI(player))
        {
            e.setCancelled(true);
            UIManager.OnClickLowerInventory(e);
        }
    }
}
