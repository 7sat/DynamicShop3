package me.sat7.dynamicshop.transactions;

import java.util.HashMap;

import me.sat7.dynamicshop.events.ShopBuySellEvent;
import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LogUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import me.sat7.dynamicshop.utilities.SoundUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class Sell
{
    private Sell()
    {

    }

    // 퀵판매
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
                    if(maxStock == -1)
                    {
                        amount += item.getAmount();
                        player.getInventory().removeItem(item);
                    }
                    else
                    {
                        int tempAmount = Clamp(tempIS.getAmount(), 0, maxStock - stockOld);
                        int itemLeft = item.getAmount() - tempAmount;
                        if(itemLeft <= 0)
                        {
                            player.getInventory().removeItem(item);
                        }
                        else
                        {
                            item.setAmount(itemLeft);
                        }
                        amount += tempAmount;
                    }
                }

                if(maxStock != -1 && amount + stockOld <= maxStock)
                    break;
            }
            tradeAmount = amount;
        } else
        {
            if(maxStock == -1)
            {
                tradeAmount = player.getInventory().getItem(slot).getAmount();
                player.getInventory().setItem(slot, null);
            }
            else
            {
                tradeAmount = Clamp(tempIS.getAmount(), 0, maxStock - stockOld);
                int itemAmountOld = player.getInventory().getItem(slot).getAmount();
                int itemLeft = itemAmountOld - tradeAmount;

                if(itemLeft <= 0)
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
            data.get().set(tradeIdx + ".stock", stockOld + tradeAmount);
        }

        // 실제 거래부----------
        Economy econ = DynamicShop.getEconomy();
        EconomyResponse r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

        if (r.transactionSuccess())
        {
            data.save();

            //로그 기록
            LogUtil.addLog(shopName, tempIS.getType().toString(), -tradeAmount, priceSum, "vault", player.getName());

            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.SELL_SUCCESS")
                    .replace("{item}", tempIS.getType().name())
                    .replace("{amount}", Integer.toString(tradeAmount))
                    .replace("{price}", econ.format(r.amount))
                    .replace("{bal}", econ.format(econ.getBalance((player)))));
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

    // 판매
    public static void sellItemCash(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, boolean infiniteStock)
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
            data.get().set(tradeIdx + ".stock",
                    data.get().getInt(tradeIdx + ".stock") + actualAmount);
        }

        // 실제 거래부----------
        Economy econ = DynamicShop.getEconomy();
        EconomyResponse r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

        if (r.transactionSuccess())
        {
            //로그 기록
            LogUtil.addLog(shopName, tempIS.getType().toString(), -actualAmount, priceSum, "vault", player.getName());

            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.SELL_SUCCESS")
                    .replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()))
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", econ.format(r.amount))
                    .replace("{bal}", econ.format(econ.getBalance((player)))));
            SoundUtil.playerSoundEffect(player, "sell");

            if (data.get().contains("Options.Balance"))
            {
                ShopUtil.addShopBalance(shopName, priceSum * -1);
            }

            data.save();
            DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);

            ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player);
            Bukkit.getPluginManager().callEvent(event);
        } else
        {
            player.sendMessage(String.format("[Vault] An error occured: %s", r.errorMessage));
        }
    }

    // 판매 jp
    public static void sellItemJobPoint(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, boolean infiniteStock)
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
            data.get().set(tradeIdx + ".stock",
                    data.get().getInt(tradeIdx + ".stock") + actualAmount);
        }

        // 실제 거래부----------
        if (JobsHook.addJobsPoint(player, priceSum))
        {
            //로그 기록
            LogUtil.addLog(shopName, tempIS.getType().toString(), -actualAmount, priceSum, "jobpoint", player.getName());

            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.SELL_SUCCESS_JP")
                    .replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()))
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", DynaShopAPI.df.format(priceSum))
                    .replace("{bal}", DynaShopAPI.df.format(JobsHook.getCurJobPoints(player))));
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
}
