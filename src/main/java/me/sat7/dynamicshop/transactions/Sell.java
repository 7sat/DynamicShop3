package me.sat7.dynamicshop.transactions;

import java.util.HashMap;
import java.util.Map;

import me.sat7.dynamicshop.economyhook.PlayerpointHook;
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
import me.sat7.dynamicshop.economyhook.JobsHook;
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

    public static double quickSellItem(Player player, ItemStack itemStack, String shopName, int tradeIdx, boolean isShiftClick, int slot)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        ItemTrade.CURRENCY currencyType;
        if (data.get().contains("Options.flag.jobpoint"))
        {
            currencyType = ItemTrade.CURRENCY.JOB_POINT;
        }
        else if (data.get().contains("Options.flag.playerpoint"))
        {
            currencyType = ItemTrade.CURRENCY.PLAYER_POINT;
        }
        else
        {
            currencyType = ItemTrade.CURRENCY.VAULT;
        }

        double priceSellOld = DynaShopAPI.getSellPrice(shopName, itemStack);
        double priceBuyOld = Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true);
        int stockOld = data.get().getInt(tradeIdx + ".stock");
        int maxStock = data.get().getInt(tradeIdx + ".maxStock", -1);

        double deliveryCharge = ShopUtil.CalcShipping(shopName, player);
        double priceSum = -deliveryCharge;

        // 실제 판매 가능량 확인
        int tradeAmount;
        if (player != null)
        {
            if (isShiftClick)
            {
                int amount = 0;
                for (ItemStack item : player.getInventory().getStorageContents())
                {
                    if (item == null)
                        continue;

                    if (item.isSimilar(itemStack))
                    {
                        if (maxStock == -1)
                        {
                            amount += item.getAmount();
                            player.getInventory().removeItem(item);
                        }
                        else
                        {
                            int tempAmount = Clamp(itemStack.getAmount(), 0, maxStock - stockOld);
                            int itemLeft = item.getAmount() - tempAmount;
                            if (itemLeft <= 0)
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

                    if (maxStock != -1 && amount + stockOld <= maxStock)
                        break;
                }
                tradeAmount = amount;
            }
            else
            {
                if (maxStock == -1)
                {
                    tradeAmount = player.getInventory().getItem(slot).getAmount();
                    player.getInventory().setItem(slot, null);
                }
                else
                {
                    tradeAmount = Clamp(itemStack.getAmount(), 0, maxStock - stockOld);
                    int itemAmountOld = player.getInventory().getItem(slot).getAmount();
                    int itemLeft = itemAmountOld - tradeAmount;

                    if (itemLeft <= 0)
                        player.getInventory().setItem(slot, null);
                    else
                        player.getInventory().getItem(slot).setAmount(itemLeft);
                }
            }
            player.updateInventory();
        }
        else
        {
            tradeAmount = itemStack.getAmount();
        }

        // 판매할 아이탬이 없음
        if (tradeAmount == 0)
        {
            if (player != null)
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"));

            return 0;
        }

        double[] calcResult = Calc.calcTotalCost(shopName, String.valueOf(tradeIdx), -tradeAmount);
        priceSum += calcResult[0];

        Economy econ = DynamicShop.getEconomy();
        if (!CheckTransactionSuccess(currencyType, player, priceSum))
            return 0;

        //로그 기록
        LogUtil.addLog(shopName, itemStack.getType().toString(), -tradeAmount, priceSum, StringUtil.GetCurrencyString(currencyType), player != null ? player.getName() : shopName);

        if (player != null)
        {
            // 플레이어에게 메시지 출력
            SendSellMessage(currencyType, econ, player, tradeAmount, priceSum, itemStack);

            // 플레이어에게 소리 재생
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1, 1);
        }

        // 상점 계좌 잔액 수정
        if (data.get().contains("Options.Balance"))
        {
            ShopUtil.addShopBalance(shopName, priceSum * -1);
        }
        // 상점 재고 증가
        if (stockOld > 0)
        {
            data.get().set(tradeIdx + ".stock", MathUtil.SafeAdd(stockOld, tradeAmount));
        }

        // 커맨드 실행
        RunSellCommand(data, player, shopName, itemStack, tradeAmount, priceSum, calcResult[1]);

        ShopUtil.shopDirty.put(shopName, true);

        // 이벤트 호출
        if (player != null)
        {
            ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true),
                                                          priceSellOld,
                                                          DynaShopAPI.getSellPrice(shopName, itemStack),
                                                          stockOld,
                                                          DynaShopAPI.getStock(shopName, itemStack),
                                                          DynaShopAPI.getMedian(shopName, itemStack),
                                                          shopName, itemStack, player);
            Bukkit.getPluginManager().callEvent(event);
        }

        return priceSum;
    }

    public static void sell(ItemTrade.CURRENCY currency, Player player, String shopName, String tradeIdx, ItemStack itemStack, double priceSum, boolean infiniteStock)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        double priceSellOld = DynaShopAPI.getSellPrice(shopName, itemStack);
        double priceBuyOld = Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true);
        int stockOld = data.get().getInt(tradeIdx + ".stock");
        // 상점에 돈이 없음
        if (ShopUtil.getShopBalance(shopName) != -1 && ShopUtil.getShopBalance(shopName) < Calc.calcTotalCost(shopName, tradeIdx, itemStack.getAmount())[0])
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_BAL_LOW"));
            return;
        }

        // 실제 판매 가능량 확인
        int actualAmount = itemStack.getAmount();
        HashMap<Integer, ItemStack> hashMap = player.getInventory().removeItem(itemStack);
        player.updateInventory();
        if (!hashMap.isEmpty())
        {
            actualAmount -= hashMap.get(0).getAmount();
        }

        // 판매할 아이탬이 없음
        if (actualAmount == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"));
            return;
        }

        double[] calcResult = Calc.calcTotalCost(shopName, tradeIdx, -actualAmount);
        priceSum += calcResult[0];

        Economy econ = DynamicShop.getEconomy();
        if (!CheckTransactionSuccess(currency, player, priceSum))
            return;

        //로그 기록
        LogUtil.addLog(shopName, itemStack.getType().toString(), -actualAmount, priceSum, StringUtil.GetCurrencyString(currency), player.getName());

        // 메시지 출력
        SendSellMessage(currency, econ, player, actualAmount, priceSum, itemStack);

        // 플레이어에게 소리 재생
        SoundUtil.playerSoundEffect(player, "sell");

        // 상점 계좌 잔액 수정
        if (data.get().contains("Options.Balance"))
        {
            ShopUtil.addShopBalance(shopName, priceSum * -1);
        }
        // 상점 재고 증가
        if (!infiniteStock)
        {
            data.get().set(tradeIdx + ".stock", MathUtil.SafeAdd(stockOld, actualAmount));
        }

        // 커맨드 실행
        RunSellCommand(data, player, shopName, itemStack, actualAmount, priceSum, calcResult[1]);

        ShopUtil.shopDirty.put(shopName, true);
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);

        // 이벤트 호출
        ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true), priceSellOld, DynaShopAPI.getSellPrice(shopName, itemStack), stockOld, DynaShopAPI.getStock(shopName, itemStack), DynaShopAPI.getMedian(shopName, itemStack), shopName, itemStack, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static boolean CheckTransactionSuccess(ItemTrade.CURRENCY currencyType, Player player, double priceSum)
    {
        if (currencyType == ItemTrade.CURRENCY.VAULT)
        {
            EconomyResponse r = null;
            if (player != null)
                r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

            return r == null || r.transactionSuccess();
        }
        else if (currencyType == ItemTrade.CURRENCY.JOB_POINT)
        {
            return  JobsHook.addJobsPoint(player, priceSum);
        }
        else if (currencyType == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            return PlayerpointHook.addPP(player, priceSum);
        }

        return false;
    }

    private static void SendSellMessage(ItemTrade.CURRENCY currency, Economy econ, Player player, int actualAmount, double priceSum, ItemStack itemStack)
    {
        boolean itemHasCustomName = itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName();
        boolean useLocalizedName = !itemHasCustomName && ConfigUtil.GetLocalizedItemName();
        String message = "";
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(econ.getBalance(player)));
        }
        else if (currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS_JP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(JobsHook.getCurJobPoints(player)));
        }
        else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS_PP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(PlayerpointHook.getCurrentPP(player)));
        }

        if (useLocalizedName)
        {
            message = message.replace("{item}", "<item>");
            LangUtil.sendMessageWithLocalizedItemName(player, message, itemStack.getType());
        }
        else
        {
            String itemNameFinal = itemHasCustomName ? itemStack.getItemMeta().getDisplayName() : ItemsUtil.getBeautifiedName(itemStack.getType());
            message = message.replace("{item}", itemNameFinal);
            player.sendMessage(message);
        }
    }

    private static void RunSellCommand(CustomConfig data, Player player, String shopName, ItemStack tempIS, int actualAmount, double priceSum, double tax)
    {
        if (data.get().contains("Options.command.active") && data.get().getBoolean("Options.command.active") &&
                data.get().contains("Options.command.sell"))
        {
            if (data.get().getConfigurationSection("Options.command.sell") != null)
            {
                priceSum = Math.round(priceSum * 10000) / 10000.0;
                tax = Math.round(tax * 10000) / 10000.0;

                for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.sell").getValues(false).entrySet())
                {
                    String sellCmd = s.getValue().toString()
                            .replace("{player}", player.getName())
                            .replace("{shop}", shopName)
                            .replace("{itemType}", tempIS.getType().toString())
                            .replace("{amount}", actualAmount + "")
                            .replace("{priceSum}", priceSum + "")
                            .replace("{tax}", tax + "");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sellCmd);
                }
            }
        }
    }
}
