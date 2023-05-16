package me.sat7.dynamicshop;

import lombok.NonNull;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.guis.*;
import me.sat7.dynamicshop.models.DSItem;
import me.sat7.dynamicshop.transactions.Calc;
import me.sat7.dynamicshop.transactions.Sell;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import me.sat7.dynamicshop.utilities.UserUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class DynaShopAPI
{
    private DynaShopAPI()
    {

    }

    public static boolean IsShopEnable(String shopName)
    {
        if(!ShopUtil.shopConfigFiles.containsKey(shopName))
            return false;

        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);
        return shopData.get().getBoolean("Options.enable", true);
    }

    // 상점 UI생성 후 열기
    public static void openShopGui(Player player, String shopName, int page)
    {
        if(!IsShopEnable(shopName))
        {
            if(player.hasPermission(P_ADMIN_SHOP_EDIT))
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_DISABLED"));
            }
            else
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_IS_CLOSED_BY_ADMIN"));
                return;
            }
        }

        Shop uiClass = new Shop();
        Inventory inventory = uiClass.getGui(player, shopName, page);
        if (inventory != null)
        {
            UIManager.Open(player, inventory, uiClass);
        }
    }

    // 상점 설정 화면
    public static void openShopSettingGui(Player player, String shopName)
    {
        ShopSettings uiClass = new ShopSettings();
        Inventory inventory = uiClass.getGui(player, shopName);
        UIManager.Open(player, inventory, uiClass);
    }

    // 상점 로테이트 편집기
    public static void OpenRotationEditor(Player player, String shopName)
    {
        DynamicShop.PaidOnlyMsg(player);
    }

    // 거래화면 생성 및 열기
    public static void openItemTradeGui(Player player, String shopName, String tradeIdx)
    {
        if(!IsShopEnable(shopName))
        {
            if(!player.hasPermission(P_ADMIN_SHOP_EDIT))
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_IS_CLOSED_BY_ADMIN"));
                return;
            }
        }

        ItemTrade uiClass = new ItemTrade();
        Inventory inventory = uiClass.getGui(player, shopName, tradeIdx);
        UIManager.Open(player, inventory, uiClass);
    }


    // 아이탬 파렛트 생성 및 열기
    public static void openItemPalette(Player player, int uiSubType, String shopName, int targetSlot, int page, String search)
    {
        ItemPalette uiClass = new ItemPalette();
        Inventory inventory = uiClass.getGui(player, uiSubType, shopName, targetSlot, page, search);
        UIManager.Open(player, inventory, uiClass);
    }

    public static void openItemSettingGui(Player player, String shopName, int shopSlotIndex, int tab, DSItem dsItem)
    {
        openItemSettingGui(player, shopName, shopSlotIndex, tab, dsItem, 0);
    }

    public static void openItemSettingGui(Player player, String shopName, int shopSlotIndex, int tab, DSItem dsItem, int timerOffset)
    {
        ItemSettings uiClass = new ItemSettings();
        Inventory inventory = uiClass.getGui(player, shopName, shopSlotIndex, tab, dsItem, timerOffset);
        UIManager.Open(player, inventory, uiClass);
    }

    // 페이지 에디터 열기
    public static void openPageEditor(Player player, String shopName, int page)
    {
        PageEditor uiClass = new PageEditor();
        Inventory inventory = uiClass.getGui(player, shopName, page);
        UIManager.Open(player, inventory, uiClass);
    }

    // 로그뷰어 열기
    public static void openLogViewer(Player player, String shopName)
    {
        DynamicShop.PaidOnlyMsg(player);
    }

    // 재고 시뮬레이터 열기
    public static void openStockSimulator(Player player, String shopName)
    {
        DynamicShop.PaidOnlyMsg(player);
    }

    // 스타트 페이지
    public static void openStartPage(Player player)
    {
        StartPage uiClass = new StartPage();
        Inventory inventory = uiClass.getGui(player);
        UIManager.Open(player, inventory, uiClass);
    }

    // 상점 목록창
    public static void openShopListUI(Player player, int page, int slotIndex)
    {
        ShopList uiClass = new ShopList();
        Inventory inventory = uiClass.getGui(player, page, slotIndex);
        UIManager.Open(player, inventory, uiClass);
    }

    // 컬러 픽커
    public static void openColorPicker(Player player, int slotIndex)
    {
        ColorPicker uiClass = new ColorPicker();
        Inventory inventory = uiClass.getGui(player, slotIndex);
        UIManager.Open(player, inventory, uiClass);
    }

    // 퀵셀 창
    public static void openQuickSellGUI(Player player)
    {
        QuickSell uiClass = new QuickSell();
        Inventory inventory = uiClass.getGui(player);
        UIManager.Open(player, inventory, uiClass);
    }

    // 유저 데이터를 다시 만들고 만들어졌는지 확인함.
    public static boolean recreateUserData(Player player)
    {
        return UserUtil.RecreateUserData(player);
    }

    // 스타트페이지 셋팅창
    public static void openStartPageSettingGui(Player player, int slotIndex)
    {
        StartPageSettings uiClass = new StartPageSettings();
        Inventory inventory = uiClass.getGui(player, slotIndex);
        UIManager.Open(player, inventory, uiClass);
    }

    /**
     * Get the tax rate for a shop or the global tax rate if none is set.
     * Will return the global tax if shopName is null.
     *
     * @param shopName The shop name to check tax for or null
     * @return The tax rate
     * @throws IllegalArgumentException When the shop does not exist and is not null
     */
    public static int getTaxRate(String shopName)
    {
        if (shopName != null)
        {
            if (validateShopName(shopName))
            {
                return Calc.getTaxRate(shopName);
            } else
            {
                throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
            }
        } else
        {
            return ConfigUtil.getCurrentTax();
        }
    }

    /**
     * Get the list of shops
     *
     * @return ArrayList of String containing the list of names of loaded shops
     */
    public static ArrayList<String> getShops()
    {
        return new ArrayList<>(ShopUtil.shopConfigFiles.keySet());
    }

    /**
     * Get the items in a shop
     *
     * @param shopName The name of the shop to get the items from
     * @return ArrayList of ItemStack containing the items for sale in the shop
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static ArrayList<ItemStack> getShopItems(@NonNull String shopName)
    {
        if (validateShopName(shopName))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

            ArrayList<ItemStack> list = new ArrayList<>();
            for (String s : data.get().getKeys(false))
            {
                try
                {
                    int i = Integer.parseInt(s);
                } catch (Exception e)
                {
                    continue;
                }

                if (!data.get().contains(s + ".value"))
                {
                    continue; // 장식용임
                }

                String itemName = data.get().getString(s + ".mat"); // 메테리얼
                try
                {
                    Material mat = Material.getMaterial(itemName);
                    list.add(new ItemStack(mat));
                } catch (Exception ignored)
                {
                }
            }
            return list;
        } else
        {
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
    public static double getBuyPrice(@NonNull String shopName, @NonNull ItemStack itemStack)
    {
        if (validateShopName(shopName))
        {
            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1)
            {
                return Calc.getCurrentPrice(shopName, String.valueOf(idx), true);
            } else
            {
                return idx;
            }
        } else
        {
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
    public static double getSellPrice(@NonNull String shopName, @NonNull ItemStack itemStack)
    {
        if (validateShopName(shopName))
        {
            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1)
            {
                return Calc.getCurrentPrice(shopName, String.valueOf(idx), false);
            } else
            {
                return idx;
            }
        } else
        {
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
    public static int getStock(@NonNull String shopName, @NonNull ItemStack itemStack)
    {
        if (validateShopName(shopName))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1)
            {
                return data.get().getInt(idx + ".stock");
            } else
            {
                return idx;
            }
        } else
        {
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
    public static int getMedian(@NonNull String shopName, @NonNull ItemStack itemStack) throws IllegalArgumentException
    {
        if (validateShopName(shopName))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            if (idx != -1)
            {
                return data.get().getInt(idx + ".median");
            } else
            {
                return idx;
            }
        } else
        {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Check if a shop exists
     *
     * @param shopName The shop name to check for
     * @return True if it exists
     */
    public static boolean validateShopName(@NonNull String shopName)
    {
        return getShops().contains(shopName);
    }

    /**
     * Find the best shop to sell.
     * Depending on the player's permission and the state of the store, there may not be an appropriate target.
     *
     * @param player seller
     * @return [0]shopName. return "" if null. [1]tradeIdx. Return -1 if null. Returns -2 if selling in multiple currencies.
     */
    public static String[] FindTheBestShopToSell(Player player, ItemStack itemStack, boolean openTradeView)
    {
        String[] ret = ShopUtil.FindTheBestShopToSell(player, itemStack);

        if (openTradeView)
        {
            openItemTradeGui(player, ret[0], ret[1]);
        }

        return ret;
    }

    /**
     * Quick Sell
     *
     * @param player seller. This can be null. If null, permission and time are not checked.
     * @param itemStack Item to sell.
     * @return price sum.
     */
    public static double QuickSell(Player player, ItemStack itemStack)
    {
        return QuickSell(player, itemStack, -1, true);
    }
    public static double QuickSell(Player player, ItemStack itemStack, boolean playSound)
    {
        return QuickSell(player, itemStack, -1, playSound);
    }
    public static double QuickSell(Player player, ItemStack itemStack, int slot)
    {
        return QuickSell(player,itemStack, slot, true);
    }
    public static double QuickSell(Player player, ItemStack itemStack, int slot, boolean playSound)
    {
        if (itemStack == null || itemStack.getType().isAir())
            return 0;

        String[] ret = ShopUtil.FindTheBestShopToSell(player, itemStack);

        if (ret[1].equals("-2"))
            return 0;

        if (!validateShopName(ret[0]))
            return 0;

        return Sell.quickSellItem(player, itemStack, ret[0], Integer.parseInt(ret[1]), slot == -1, slot, playSound);
    }

    /**
     * Search for empty slots in a specific shop.
     *
     * @param shopName shop name
     * @return Returns the index of an empty slot. (first slot only). Returns -1 if there is no empty slot.
     */
    public static int FindEmptySlot(String shopName)
    {
        return ShopUtil.findEmptyShopSlot(shopName, 0, false);
    }

    //------------
    /**
     * Get whether a shop is for Vault money or Jobs points
     *
     * @param shopName The shop to check the type of
     * @return True if it is a Job Point shop.
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static boolean isJobsPointShop(@NonNull String shopName) throws IllegalArgumentException
    {
        if (validateShopName(shopName))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
            return ShopUtil.GetCurrency(data).equalsIgnoreCase(Constants.S_JOBPOINT);
        } else
        {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    /**
     * Get whether a shop is for Vault money or Player points
     *
     * @param shopName The shop to check the type of
     * @return True if it is a Player Point shop
     * @throws IllegalArgumentException When the shop does not exist
     */
    public static boolean isPlayerPointShop(@NonNull String shopName) throws IllegalArgumentException
    {
        if (validateShopName(shopName))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
            return ShopUtil.GetCurrency(data).equalsIgnoreCase(Constants.S_PLAYERPOINT);
        } else
        {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }

    public static String GetShopCurrency(@NonNull String shopName)
    {
        if (validateShopName(shopName))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
            return ShopUtil.GetCurrency(data);
        } else
        {
            throw new IllegalArgumentException("Shop: " + shopName + " does not exist");
        }
    }
}
