package me.sat7.dynamicshop;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.guis.ItemPalette;
import me.sat7.dynamicshop.guis.ItemSettings;
import me.sat7.dynamicshop.guis.ItemTrade;
import me.sat7.dynamicshop.guis.QuickSell;
import me.sat7.dynamicshop.guis.Shop;
import me.sat7.dynamicshop.guis.ShopSettings;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.guis.StartPageSettings;
import me.sat7.dynamicshop.models.DSItem;

public final class DynaShopAPI {
    private DynaShopAPI() {

    }

    public static DecimalFormat df = new DecimalFormat("0.00");

    //[ UI ]=========================================================

    // 상점 UI생성 후 열기
    public static void openShopGui(Player player, String shopName, int page)
    {
        Inventory shopGui = new Shop().getGui(player, shopName, page);
        if (shopGui != null) {
            player.openInventory(shopGui);
        }
    }

    // 상점 설정 화면
    public static void openShopSettingGui(Player player, String shopName)
    {
        player.openInventory(new ShopSettings().getGui(player, shopName));
    }

    // 거래화면 생성 및 열기
    public static void openItemTradeGui(Player player, String shopName, String tradeIdx)
    {
        player.openInventory(new ItemTrade().getGui(player, shopName, tradeIdx));
    }


    // 아이탬 파렛트 생성 및 열기
    public static void openItemPalette(Player player, int page, String search)
    {
        player.openInventory(new ItemPalette().getGui(player, page, search));
    }

    // 아이탬 셋팅창
    public static void openItemSettingGui(Player player, ItemStack itemStack, int tab, double buyValue, double sellValue, double minPrice, double maxPrice, int median, int stock) {
        DSItem dsItem = new DSItem(itemStack, buyValue, sellValue, minPrice, maxPrice, median, stock);

        openItemSettingGui(player, tab, dsItem);
    }

    private static void openItemSettingGui(Player player, int tab, DSItem dsItem) {
        player.openInventory(new ItemSettings().getGui(player, tab, dsItem));
    }

    // 스타트 페이지 생성 및 열기
    public static void openStartPage(Player player)
    {
        player.openInventory(new StartPage().getGui(player));
    }

    // 퀵셀 창
    public static void openQuickSellGUI(Player player)
    {
        player.openInventory(new QuickSell().getGui(player));
    }

    // 유저 데이터를 다시 만들고 만들어졌는지 확인함.
    public static boolean recreateUserData(Player player)
    {
        if(DynamicShop.ccUser.get().contains(player.getUniqueId().toString()))
        {
            return true;
        }

        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".tmpString","");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".interactItem","");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".cmdHelp",true);
        DynamicShop.ccUser.get().set(player.getUniqueId().toString()+".lastJoin",System.currentTimeMillis());
        DynamicShop.ccUser.save();

        return DynamicShop.ccUser.get().contains(player.getUniqueId().toString());
    }

    // 스타트페이지 셋팅창
    public static void openStartPageSettingGui(Player player)
    {
        player.openInventory(new StartPageSettings().getGui(player));
    }
}
