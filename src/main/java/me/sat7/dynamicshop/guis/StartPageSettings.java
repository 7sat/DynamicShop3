package me.sat7.dynamicshop.guis;

import java.util.UUID;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.events.OnChat;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import me.sat7.dynamicshop.DynamicShop;

public final class StartPageSettings extends InGameUI
{
    public StartPageSettings()
    {
        uiType = UI_TYPE.StartPageSettings;
    }

    private final int CLOSE = 0;
    private final int NAME = 2;
    private final int LORE = 3;
    private final int ICON = 4;
    private final int CMD = 5;
    private final int SHOP_SHORTCUT = 6;
    private final int DECO = 7;
    private final int DELETE = 8;

    public Inventory getGui(Player player)
    {
        inventory = Bukkit.createInventory(player, 9, t("STARTPAGE.EDITOR_TITLE"));

        CreateCloseButton(CLOSE); // 닫기 버튼

        CreateButton(NAME, Material.BOOK, t("STARTPAGE.EDIT_NAME"), ""); // 이름 버튼
        CreateButton(LORE, Material.BOOK, t("STARTPAGE.EDIT_LORE"), ""); // 설명 버튼

        // 아이콘 버튼
        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        CreateButton(ICON, Material.getMaterial(StartPage.ccStartPage.get().getString("Buttons." + temp[1] + ".icon")), t("STARTPAGE.EDIT_ICON"), "");

        CreateButton(CMD, Material.REDSTONE_TORCH, t("STARTPAGE.EDIT_ACTION"), ""); // 액션 버튼
        CreateButton(SHOP_SHORTCUT, Material.EMERALD, t("STARTPAGE.SHOP_SHORTCUT"), ""); // 상점 바로가기 생성 버튼
        CreateButton(DECO, Material.BLUE_STAINED_GLASS_PANE, t("STARTPAGE.CREATE_DECO"), ""); // 장식 버튼
        CreateButton(DELETE, Material.BONE, t("REMOVE"), ""); // 삭제 버튼

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");

        // 돌아가기
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openStartPage(player);
        }
        // 버튼 삭제
        else if (e.getSlot() == DELETE)
        {
            StartPage.ccStartPage.get().set("Buttons." + temp[1], null);
            StartPage.ccStartPage.save();

            DynamicShop.userInteractItem.put(player.getUniqueId(), "");

            DynaShopAPI.openStartPage(player);
        }
        //이름
        else if (e.getSlot() == NAME)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("STARTPAGE.ENTER_NAME"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnName");
            OnChat.WaitForInput(player);
        }
        //설명
        else if (e.getSlot() == LORE)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("STARTPAGE.ENTER_LORE"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnLore");
            OnChat.WaitForInput(player);
        }
        //아이콘
        else if (e.getSlot() == ICON)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("STARTPAGE.ENTER_ICON"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnIcon");
            OnChat.WaitForInput(player);
        }
        //액션
        else if (e.getSlot() == CMD)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("STARTPAGE.ENTER_ACTION"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnAction");
            OnChat.WaitForInput(player);
        }
        // 상점 숏컷
        else if (e.getSlot() == SHOP_SHORTCUT)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("STARTPAGE.ENTER_SHOPNAME"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "shopname");

            StringBuilder shopList = new StringBuilder(t("SHOP_LIST") + ": ");
            for (String s : ShopUtil.shopConfigFiles.keySet())
            {
                shopList.append(s).append(", ");
            }
            shopList = new StringBuilder(shopList.substring(0, shopList.length() - 2));
            player.sendMessage(DynamicShop.dsPrefix + shopList);

            OnChat.WaitForInput(player);
        }
        // 장식
        else if (e.getSlot() == DECO)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("STARTPAGE.ENTER_COLOR"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "deco");
            OnChat.WaitForInput(player);
        }
    }
}
