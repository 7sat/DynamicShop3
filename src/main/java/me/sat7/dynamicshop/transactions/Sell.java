package me.sat7.dynamicshop.transactions;

import java.util.HashMap;

import me.sat7.dynamicshop.events.ShopBuySellEvent;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.guis.ItemTrade;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.jobshook.JobsHook;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class Sell
{
    private Sell()
    {

    }

    public static double quickSellItem(Player player, ItemStack tempIS, String shopName, int tradeIdx, boolean isShiftClick, int slot)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        double priceSellOld = DynaShopAPI.getSellPrice(shopName, tempIS);
        double priceBuyOld = Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true);
        int stockOld = data.get().getInt(tradeIdx + ".stock");
        int maxStock = data.get().getInt(tradeIdx + ".maxStock", -1);
        double priceSum;

        // 실제 판매 가능량 확인
        int tradeAmount;
        if (isShiftClick)
        {
            int amount = 0;
            for (ItemStack item : player.getInventory().getStorageContents())
            {
                if (item == null)
                    continue;

                if (item.isSimilar(tempIS))
                {
                    if (maxStock == -1)
                    {
                        amount += item.getAmount();
                        player.getInventory().removeItem(item);
                    } else
                    {
                        int tempAmount = Clamp(tempIS.getAmount(), 0, maxStock - stockOld);
                        int itemLeft = item.getAmount() - tempAmount;
                        if (itemLeft <= 0)
                        {
                            player.getInventory().removeItem(item);
                        } else
                        {
                            item.setAmount(itemLeft);
                        }
                        amount += tempAmount;
                    }
                }

                if (maxStock != -1 && amount + stockOld <= maxStock)
                    break;
            }
            tradeAmount = amount;
        } else
        {
            if (maxStock == -1)
            {
                tradeAmount = player.getInventory().getItem(slot).getAmount();
                player.getInventory().setItem(slot, null);
            } else
            {
                tradeAmount = Clamp(tempIS.getAmount(), 0, maxStock - stockOld);
                int itemAmountOld = player.getInventory().getItem(slot).getAmount();
                int itemLeft = itemAmountOld - tradeAmount;

                if (itemLeft <= 0)
                    player.getInventory().setItem(slot, null);
                else
                    player.getInventory().getItem(slot).setAmount(itemLeft);
            }
        }
        player.updateInventory();

        // 판매할 아이탬이 없음
        if (tradeAmount == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.NO_ITEM_TO_SELL"));
            return 0;
        }

        priceSum = Calc.calcTotalCost(shopName, String.valueOf(tradeIdx), -tradeAmount);

        // 재고 증가
        if (stockOld > 0)
        {
            data.get().set(tradeIdx + ".stock", MathUtil.SafeAdd(stockOld, tradeAmount));
        }

        // 실제 거래부----------
        Economy econ = DynamicShop.getEconomy();
        EconomyResponse r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

        if (r.transactionSuccess())
        {
            data.save();

            //로그 기록
            LogUtil.addLog(shopName, tempIS.getType().toString(), -tradeAmount, priceSum, "vault", player.getName());

            String message = DynamicShop.dsPrefix + t("MESSAGE.SELL_SUCCESS")
                    .replace("{amount}", Integer.toString(tradeAmount))
                    .replace("{price}", n(r.amount))
                    .replace("{bal}", n(econ.getBalance((player))));

            if (DynamicShop.localeManager == null || !DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName"))
            {
                message = message.replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()));
                player.sendMessage(message);
            } else
            {
                message = message.replace("{item}", "<item>");
                DynamicShop.localeManager.sendMessage(player, message, tempIS.getType(), (short) 0, null);
            }

            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1, 1);

            if (data.get().contains("Options.Balance"))
            {
                ShopUtil.addShopBalance(shopName, priceSum * -1);
            }

            ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player);
            Bukkit.getPluginManager().callEvent(event);
        } else
        {
            player.sendMessage(String.format("[Vault] An error occured: %s", r.errorMessage));
        }

        return priceSum;
    }

    public static void sell(ItemTrade.CURRENCY currency, Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, boolean infiniteStock)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        double priceSellOld = DynaShopAPI.getSellPrice(shopName, tempIS);
        double priceBuyOld = Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true);
        int stockOld = data.get().getInt(tradeIdx + ".stock");
        // 상점에 돈이 없음
        if (ShopUtil.getShopBalance(shopName) != -1 && ShopUtil.getShopBalance(shopName) < Calc.calcTotalCost(shopName, tradeIdx, tempIS.getAmount()))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.SHOP_BAL_LOW"));
            return;
        }

        // 실제 판매 가능량 확인
        int actualAmount = tempIS.getAmount();
        HashMap<Integer, ItemStack> hashMap = player.getInventory().removeItem(tempIS);
        player.updateInventory();
        if (!hashMap.isEmpty())
        {
            actualAmount -= hashMap.get(0).getAmount();
        }

        // 판매할 아이탬이 없음
        if (actualAmount == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.NO_ITEM_TO_SELL"));
            return;
        }

        priceSum += Calc.calcTotalCost(shopName, tradeIdx, -actualAmount);

        // 재고 증가
        if (!infiniteStock)
        {
            data.get().set(tradeIdx + ".stock", MathUtil.SafeAdd(stockOld, actualAmount));
        }

        Economy econ = null;
        EconomyResponse r = null;
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            econ = DynamicShop.getEconomy();
            r = DynamicShop.getEconomy().depositPlayer(player, priceSum);
            if (!r.transactionSuccess())
                return;
        } else if (currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            if (!JobsHook.addJobsPoint(player, priceSum))
                return;
        }

        //로그 기록
        String currencyString = currency == ItemTrade.CURRENCY.VAULT ? "vault" : "jobpoint";
        LogUtil.addLog(shopName, tempIS.getType().toString(), -actualAmount, priceSum, currencyString, player.getName());

        String message = "";
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            message = DynamicShop.dsPrefix + t("MESSAGE.SELL_SUCCESS")
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(r.amount))
                    .replace("{bal}", n(econ.getBalance((player))));
        } else if (currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            message = DynamicShop.dsPrefix + t("MESSAGE.SELL_SUCCESS_JP")
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(JobsHook.getCurJobPoints(player)));
        }

        if (DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName"))
        {
            message = message.replace("{item}", "<item>");
            DynamicShop.localeManager.sendMessage(player, message, tempIS.getType(), (short) 0, null);
        } else
        {
            message = message.replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()));
            player.sendMessage(message);
        }

        SoundUtil.playerSoundEffect(player, "sell");

        if (data.get().contains("Options.Balance"))
        {
            ShopUtil.addShopBalance(shopName, priceSum * -1);
        }

        data.save();
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);

        ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player);
        Bukkit.getPluginManager().callEvent(event);
    }
}
