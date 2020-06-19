package me.sat7.dynamicshop;

import lombok.NonNull;
import me.sat7.dynamicshop.guis.*;
import me.sat7.dynamicshop.models.DSItem;
import me.sat7.dynamicshop.transactions.Calc;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;

public final class DynaShopAPI {
    public static DecimalFormat df = new DecimalFormat("0.00");

    private DynaShopAPI() {

    }

    //[ UI ]=========================================================

    // 상점 UI생성 후 열기
    public static void openShopGui(Player player, String shopName, int page) {
        Inventory shopGui = new Shop().getGui(player, shopName, page);
        if (shopGui != null) {
            player.openInventory(shopGui);
        }
    }

    // 상점 설정 화면
    public static void openShopSettingGui(Player player, String shopName) {
        player.openInventory(new ShopSettings().getGui(player, shopName));
    }

    // 거래화면 생성 및 열기
    public static void openItemTradeGui(Player player, String shopName, String tradeIdx) {
        player.openInventory(new ItemTrade().getGui(player, shopName, tradeIdx));
    }


    // 아이탬 파렛트 생성 및 열기
    public static void openItemPalette(Player player, int page, String search) {
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
    public static void openStartPage(Player player) {
        player.openInventory(new StartPage().getGui(player));
    }

    // 퀵셀 창
    public static void openQuickSellGUI(Player player) {
        player.openInventory(new QuickSell().getGui(player));
    }

    // 유저 데이터를 다시 만들고 만들어졌는지 확인함.
    public static boolean recreateUserData(Player player) {
        if (DynamicShop.ccUser.get().contains(player.getUniqueId().toString())) {
            return true;
        }

        DynamicShop.ccUser.get().set(player.getUniqueId().toString() + ".tmpString", "");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString() + ".interactItem", "");
        DynamicShop.ccUser.get().set(player.getUniqueId().toString() + ".cmdHelp", true);
        DynamicShop.ccUser.get().set(player.getUniqueId().toString() + ".lastJoin", System.currentTimeMillis());
        DynamicShop.ccUser.save();

        return DynamicShop.ccUser.get().contains(player.getUniqueId().toString());
    }

    // 스타트페이지 셋팅창
    public static void openStartPageSettingGui(Player player) {
        player.openInventory(new StartPageSettings().getGui(player));
    }

    /**
     * Get the tax rate for a shop or the global tax rate if none is set.
     * Will return the global tax if shopName is null.
     *
     * @param shopName The shop name to check tax for or null
     * @return The tax rate
     * @throws IllegalArgumentException When the shop does not exist and is not null
     */
    public static int getTaxRate(String shopName) {
        if (shopName != null) {
            if (validateShopName(shopName)) {
                return Calc.getTaxRate(shopName);
            } else {
                throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
            }
        } else {
            return ConfigUtil.getCurrentTax();
        }
    }

    /**
     * Get the list of shops
     *
     * @return ArrayList of String containing the list of names of loaded shops
     */
    public static ArrayList<String> getShops() {
        return new ArrayList<>(ShopUtil.ccShop.get().getKeys(false));
    }

    /**
     * Get the items in a shop
     *
     * @param shopName The name of the shop to get the items from
     * @return ArrayList of ItemStack containing the items for sale in the shop
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static ArrayList<ItemStack> getShopItems(@NonNull String shopName) {
        if (validateShopName(shopName)) {
            ArrayList<ItemStack> list = new ArrayList<>();
            for (String s : ShopUtil.ccShop.get().getConfigurationSection(shopName).getKeys(false)) {
                try {
                    int i = Integer.parseInt(s);
                } catch (Exception e) {
                    continue;
                }

                if (!ShopUtil.ccShop.get().contains(shopName + "." + s + ".value")) {
                    continue; // 장식용임
                }

                Material m;
                String itemName = ShopUtil.ccShop.get().getString(shopName + "." + s + ".mat"); // 메테리얼
                try {
                    Material mat = Material.getMaterial(itemName);
                    list.add(new ItemStack(mat));
                } catch (Exception e) {
                    continue;
                }
            }
            return list;
        } else {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Get the buy price of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the price of
     * @return The buy price of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static double getBuyPrice(@NonNull String shopName, @NonNull ItemStack itemStack) {
        if (validateShopName(shopName)) {
            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1) {
                return Calc.getCurrentPrice(shopName, String.valueOf(idx), true);
            } else {
                return idx;
            }
        } else {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Get the sell price of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the price of
     * @return The sell price of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static double getSellPrice(@NonNull String shopName, @NonNull ItemStack itemStack) {
        if (validateShopName(shopName)) {
            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1) {
                double price = Calc.getCurrentPrice(shopName, String.valueOf(idx), false);
                return price - ((price/100) * getTaxRate(shopName));
            } else {
                return idx;
            }
        } else {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Get the current stock of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the stock of
     * @return The stock of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static int getStock(@NonNull String shopName, @NonNull ItemStack itemStack) {
        if (validateShopName(shopName)) {
            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1) {
                return ShopUtil.ccShop.get().getInt(shopName + "." + idx + ".stock");
            } else {
                return idx;
            }
        } else {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Get the median stock of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the median stock of
     * @return The median stock of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static int getMedian(@NonNull String shopName, @NonNull ItemStack itemStack) throws IllegalArgumentException {
        if (validateShopName(shopName)) {
            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1) {
                return ShopUtil.ccShop.get().getInt(shopName + "." + idx + ".median");
            } else {
                return idx;
            }
        } else {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Get whether a shop is for Vault money or Jobs points
     *
     * @param shopName The shop to check the type of
     * @return True if it is a Job Point shop, False if it is a Vault economy money shop
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static boolean isJobsPointShop(@NonNull String shopName) throws IllegalArgumentException {
        if (validateShopName(shopName)) {
            return ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint");
        } else {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Check if a shop exists
     *
     * @param shopName The shop name to check for
     * @return True if it exists
     */
    public static boolean validateShopName(@NonNull String shopName) {
        return getShops().contains(shopName);
    }
}
