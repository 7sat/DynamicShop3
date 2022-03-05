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

import static me.sat7.dynamicshop.utilities.LangUtil.t;

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

    private int slotIndex;

    public Inventory getGui(Player player, int slotIndex)
    {
        this.slotIndex = slotIndex;
        DynamicShop.userInteractItem.put(player.getUniqueId(), "startPage/" + slotIndex);

        inventory = Bukkit.createInventory(player, 9, t("START_PAGE.EDITOR_TITLE"));

        CreateCloseButton(CLOSE); // 닫기 버튼

        CreateButton(NAME, Material.BOOK, t("START_PAGE.EDIT_NAME"), ""); // 이름 버튼
        CreateButton(LORE, Material.BOOK, t("START_PAGE.EDIT_LORE"), ""); // 설명 버튼

        // 아이콘 버튼
        CreateButton(ICON, Material.getMaterial(StartPage.ccStartPage.get().getString("Buttons." + slotIndex + ".icon")), t("START_PAGE.EDIT_ICON"), "");

        String cmdString = StartPage.ccStartPage.get().getString("Buttons." + slotIndex + ".action");
        CreateButton(CMD, Material.REDSTONE_TORCH, t("START_PAGE.EDIT_ACTION"), cmdString == null || cmdString.isEmpty() ? null : "§7/" + cmdString); // 액션 버튼
        CreateButton(SHOP_SHORTCUT, Material.EMERALD, t("START_PAGE.SHOP_SHORTCUT"), ""); // 상점 바로가기 생성 버튼
        CreateButton(DECO, Material.BLUE_STAINED_GLASS_PANE, t("START_PAGE.CREATE_DECO"), ""); // 장식 버튼
        CreateButton(DELETE, Material.BONE, t("START_PAGE.REMOVE"), t("START_PAGE.REMOVE_LORE")); // 삭제 버튼

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        // 돌아가기
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openStartPage(player);
        }
        // 버튼 삭제
        else if (e.getSlot() == DELETE)
        {
            StartPage.ccStartPage.get().set("Buttons." + slotIndex, null);
            StartPage.ccStartPage.save();

            DynaShopAPI.openStartPage(player);
        }
        //이름
        else if (e.getSlot() == NAME)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("START_PAGE.ENTER_NAME"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnName");
            OnChat.WaitForInput(player);
        }
        //설명
        else if (e.getSlot() == LORE)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("START_PAGE.ENTER_LORE"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnLore");
            OnChat.WaitForInput(player);
        }
        //아이콘
        else if (e.getSlot() == ICON)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("START_PAGE.ENTER_ICON"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnIcon");
            OnChat.WaitForInput(player);
        }
        //액션
        else if (e.getSlot() == CMD)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("START_PAGE.ENTER_ACTION"));
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "btnAction");
            OnChat.WaitForInput(player);
        }
        // 상점 숏컷
        else if (e.getSlot() == SHOP_SHORTCUT)
        {
            DynaShopAPI.openShopListUI(player, 1, slotIndex);
        }
        // 장식
        else if (e.getSlot() == DECO)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("START_PAGE.ENTER_COLOR"));
            player.sendMessage(DynamicShop.dsPrefix + "BLACK, GRAY, LIGHT_GRAY, WHITE, CYAN, LIGHT_BLUE, BLUE, BROWN, " +
                    "GREEN, LIME, YELLOW, ORANGE, PINK, MAGENTA, PURPLE, RED");
            ShopUtil.closeInventoryWithDelay(player);
            DynamicShop.userTempData.put(uuid,"waitforInput" + "deco");
            OnChat.WaitForInput(player);
        }
    }
}
