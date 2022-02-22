package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.guis.InGameUI;
import me.sat7.dynamicshop.guis.UIManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class OnClick implements Listener
{
    @EventHandler
    public void OnInventoryDragEvent(InventoryDragEvent e)
    {
        // UI 인벤토리에 드래그로 아이탬 올리는것을 막음
        if (UIManager.IsPlayerUsingPluginGUI((Player) e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler
    public void OnInventoryClickEvent(InventoryClickEvent e)
    {
        if (e.getClickedInventory() == null)
            return;

        Player player = (Player) e.getWhoClicked();

        // 위쪽 인벤토리를 클릭함 (= 내 인벤이 아님)
        if (e.getClickedInventory() != player.getInventory())
        {
            // UUID 확인 // todo 이게 왜 필요하지?
            String pUuid = player.getUniqueId().toString();

            if (DynamicShop.ccUser.get().getConfigurationSection(pUuid) == null)
            {
                if (!DynaShopAPI.recreateUserData(player))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_USER_ID"));
                    e.setCancelled(true);
                    return;
                }
            }

            if (UIManager.IsPlayerUsingPluginGUI(player))
            {
                e.setCancelled(true);
                UIManager.OnClickUpperInventory(e);
            }
        }
        // 아래쪽 인벤토리를 클릭함
        else
        {
            if (UIManager.GetPlayerCurrentUIType(player) == InGameUI.UI_TYPE.ItemPalette ||
                UIManager.GetPlayerCurrentUIType(player) == InGameUI.UI_TYPE.QuickSell ||
                UIManager.GetPlayerCurrentUIType(player) == InGameUI.UI_TYPE.ItemSettings ||
                UIManager.GetPlayerCurrentUIType(player) == InGameUI.UI_TYPE.Shop ||
                UIManager.GetPlayerCurrentUIType(player) == InGameUI.UI_TYPE.StartPage)
            {
                e.setCancelled(true);
                UIManager.OnClickLowerInventory(e);
            }
            // Shift클릭으로 상단의 UI인벤토리로 아이템 올리는것을 막음
            else if (e.isShiftClick() && UIManager.IsPlayerUsingPluginGUI(player))
            {
                e.setCancelled(true);
            }
        }
    }
}
