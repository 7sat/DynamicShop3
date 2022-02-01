package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.events.OnChat;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;

public class StartPageSettings extends InGameUI
{

    public StartPageSettings()
    {
        uiType = UI_TYPE.StartPageSettings;
    }

    public Inventory getGui(Player player)
    {
        // UI 요소 생성
        String title = LangUtil.ccLang.get().getString("STARTPAGE.EDITOR_TITLE");
        Inventory inven = Bukkit.createInventory(player, 9, title);

        // 닫기 버튼
        ItemStack closeBtn = ItemsUtil.createItemStack(Material.BARRIER, null,
                LangUtil.ccLang.get().getString("CLOSE"), new ArrayList<>(Arrays.asList(LangUtil.ccLang.get().getString("CLOSE_LORE"))), 1);
        inven.setItem(0, closeBtn);

        // 이름 버튼
        ItemStack nameBtn = ItemsUtil.createItemStack(Material.BOOK, null,
                LangUtil.ccLang.get().getString("STARTPAGE.EDIT_NAME"), null, 1);
        inven.setItem(2, nameBtn);

        // 설명 버튼
        ItemStack loreBtn = ItemsUtil.createItemStack(Material.BOOK, null,
                LangUtil.ccLang.get().getString("STARTPAGE.EDIT_LORE"), null, 1);
        inven.setItem(3, loreBtn);

        // 아이콘 버튼
        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        ItemStack iconBtn = ItemsUtil.createItemStack(Material.getMaterial(StartPage.ccStartPage.get().getString("Buttons." + temp[1] + ".icon")), null,
                LangUtil.ccLang.get().getString("STARTPAGE.EDIT_ICON"), null, 1);
        inven.setItem(4, iconBtn);

        // 액션 버튼
        ItemStack actionBtn = ItemsUtil.createItemStack(Material.REDSTONE_TORCH, null,
                LangUtil.ccLang.get().getString("STARTPAGE.EDIT_ACTION"), null, 1);
        inven.setItem(5, actionBtn);

        // 상점 바로가기 생성 버튼
        ItemStack shopBtn = ItemsUtil.createItemStack(Material.EMERALD, null,
                LangUtil.ccLang.get().getString("STARTPAGE.SHOP_SHORTCUT"), null, 1);
        inven.setItem(6, shopBtn);

        // 장식 버튼
        ItemStack deco = ItemsUtil.createItemStack(Material.BLUE_STAINED_GLASS_PANE, null,
                LangUtil.ccLang.get().getString("STARTPAGE.CREATE_DECO"), null, 1);
        inven.setItem(7, deco);

        // 삭제 버튼
        ItemStack removeBtn = ItemsUtil.createItemStack(Material.BONE, null,
                LangUtil.ccLang.get().getString("REMOVE"), null, 1);
        inven.setItem(8, removeBtn);
        return inven;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        if (player == null)
            return;

        UUID uuid = player.getUniqueId();

        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");

        // 돌아가기
        if (e.getSlot() == 0)
        {
            DynaShopAPI.openStartPage(player);
        }
        // 버튼 삭제
        else if (e.getSlot() == 8)
        {
            StartPage.ccStartPage.get().set("Buttons." + temp[1], null);
            StartPage.ccStartPage.save();

            DynamicShop.userInteractItem.put(player.getUniqueId(), "");

            DynaShopAPI.openStartPage(player);
        }
        //이름
        else if (e.getSlot() == 2)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("STARTPAGE.ENTER_NAME"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnName");
            OnChat.WaitForInput(player);
        }
        //설명
        else if (e.getSlot() == 3)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("STARTPAGE.ENTER_LORE"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnLore");
            OnChat.WaitForInput(player);
        }
        //아이콘
        else if (e.getSlot() == 4)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("STARTPAGE.ENTER_ICON"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnIcon");
            OnChat.WaitForInput(player);
        }
        //액션
        else if (e.getSlot() == 5)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("STARTPAGE.ENTER_ACTION"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnAction");
            OnChat.WaitForInput(player);
        }
        // 상점 숏컷
        else if (e.getSlot() == 6)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("STARTPAGE.ENTER_SHOPNAME"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "shopname");

            StringBuilder shopList = new StringBuilder(LangUtil.ccLang.get().getString("SHOP_LIST") + ": ");
            for (String s : ShopUtil.ccShop.get().getKeys(false))
            {
                shopList.append(s).append(", ");
            }
            shopList = new StringBuilder(shopList.substring(0, shopList.length() - 2));
            player.sendMessage(DynamicShop.dsPrefix + shopList);

            OnChat.WaitForInput(player);
        }
        // 장식
        else if (e.getSlot() == 7)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("STARTPAGE.ENTER_COLOR"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "deco");
            OnChat.WaitForInput(player);
        }
    }
}
