package me.sat7.dynamicshop.transactions;

import java.util.Map;

import me.sat7.dynamicshop.constants.Constants;
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

public final class Sell
{
    private Sell()
    {

    }

    public static double quickSellItem(Player player, ItemStack itemStack, String shopName, int tradeIdx, boolean isShiftClick, int slot)
    {
        return quickSellItem(player, itemStack, shopName, tradeIdx, isShiftClick, slot, true);
    }

    public static double quickSellItem(Player player, ItemStack itemStack, String shopName, int tradeIdx, boolean isShiftClick, int slot, boolean playSound)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        String currencyType = ShopUtil.GetCurrency(data);

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
                tradeAmount = GetPlayerItemCount(player, itemStack);
            }
            else
            {
                tradeAmount = player.getInventory().getItem(slot).getAmount();
            }
        }
        else
        {
            tradeAmount = itemStack.getAmount();
        }

        if (maxStock != -1 && stockOld + tradeAmount > maxStock)
        {
            tradeAmount -= stockOld + tradeAmount - maxStock;
            tradeAmount = Math.max(0, tradeAmount);
        }

        // 판매할 아이탬이 없음
        if (tradeAmount == 0)
        {
            if (player != null)
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"));

            return 0;
        }

        // 플레이어 당 거래량 제한 확인
        int sellLimit = ShopUtil.GetSellLimitPerPlayer(shopName, tradeIdx);
        if (player != null && sellLimit != 0)
        {
            tradeAmount = UserUtil.CheckTradeLimitPerPlayer(player, shopName, tradeIdx, HashUtil.GetItemHash(itemStack), tradeAmount, true);
            if (tradeAmount == 0)
                return 0;
        }

        // 비용 계산
        double[] calcResult = Calc.calcTotalCost(shopName, String.valueOf(tradeIdx), -tradeAmount);
        priceSum += calcResult[0];

        // 계산된 비용에 대한 처리 시도
        Economy econ = DynamicShop.getEconomy();
        if (!CheckTransactionSuccess(currencyType, player, priceSum))
            return 0;

        // 플레이어 인벤토리에서 아이템 제거
        if (player != null)
        {
            if (isShiftClick)
            {
                int tempCount = 0;
                for (ItemStack item : player.getInventory().getStorageContents())
                {
                    if (item == null)
                        continue;

                    if (item.isSimilar(itemStack))
                    {
                        if (tempCount + item.getAmount() > tradeAmount)
                        {
                            int itemLeft = item.getAmount() - (tradeAmount - tempCount);
                            if (itemLeft <= 0)
                            {
                                player.getInventory().removeItem(item);
                            }
                            else
                            {
                                item.setAmount(itemLeft);
                            }
                            break;
                        }
                        else
                        {
                            player.getInventory().removeItem(item);
                        }

                        tempCount += item.getAmount();
                    }
                }
            }
            else
            {
                int itemAmountOld = player.getInventory().getItem(slot).getAmount();
                int itemLeft = itemAmountOld - tradeAmount;

                if (itemLeft <= 0)
                    player.getInventory().setItem(slot, null);
                else
                    player.getInventory().getItem(slot).setAmount(itemLeft);
            }

            player.updateInventory();
        }

        // 플레이어 당 거래량 제한 아이템에 대한 처리.
        if (player != null & sellLimit != Integer.MIN_VALUE)
        {
            UserUtil.OnPlayerTradeLimitedItem(player, shopName, HashUtil.GetItemHash(itemStack), tradeAmount, true);
        }

        // 로그 기록
        LogUtil.addLog(shopName, itemStack.getType().toString(), -tradeAmount, priceSum, currencyType, player != null ? player.getName() : shopName);

        if (player != null)
        {
            // 플레이어에게 메시지 출력
            SendSellMessage(currencyType, econ, player, tradeAmount, priceSum, itemStack);

            // 플레이어에게 소리 재생
            if (playSound)
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

        // 더티
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

    public static void sell(String currency, Player player, String shopName, String tradeIdx, ItemStack itemStack, double priceSum, boolean infiniteStock)
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

        // 상점이 매입을 거절.
        int stock = data.get().getInt(tradeIdx + ".stock");
        int maxStock = data.get().getInt(tradeIdx + ".maxStock", -1);
        if (maxStock != -1 && maxStock <= stock)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.PURCHASE_REJECTED"));
            return;
        }

        // 실제 판매 가능량 확인
        int tradeAmount = itemStack.getAmount();
        int playerHas = GetPlayerItemCount(player, itemStack);
        if (tradeAmount > playerHas)
        {
            tradeAmount = playerHas;
        }

        // 판매할 아이탬이 없음
        if (tradeAmount == 0)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"));
            return;
        }

        if (maxStock != -1 && stock + tradeAmount > maxStock)
        {
            tradeAmount = maxStock - stock;
            tradeAmount = Math.max(0, tradeAmount);
        }

        // 플레이어 당 거래량 제한 확인
        int tradeIdxInt = Integer.parseInt(tradeIdx);
        int tradeLimitPerPlayer = ShopUtil.GetSellLimitPerPlayer(shopName, tradeIdxInt);
        if (tradeLimitPerPlayer != 0)
        {
            tradeAmount = UserUtil.CheckTradeLimitPerPlayer(player, shopName, tradeIdxInt, HashUtil.GetItemHash(itemStack), tradeAmount, true);
            if (tradeAmount == 0)
                return;
        }

        // 비용 계산
        double[] calcResult = Calc.calcTotalCost(shopName, tradeIdx, -tradeAmount);
        priceSum += calcResult[0];

        // 계산된 비용에 대한 처리 시도
        Economy econ = DynamicShop.getEconomy();
        if (!CheckTransactionSuccess(currency, player, priceSum))
            return;

        // 플레이어 인벤토리에서 아이템 제거
        ItemStack delete = new ItemStack(itemStack);
        delete.setAmount(tradeAmount);
        player.getInventory().removeItem(delete);
        player.updateInventory();

        // 플레이어 당 거래량 제한 아이템에 대한 처리.
        if (tradeLimitPerPlayer != Integer.MIN_VALUE)
        {
            UserUtil.OnPlayerTradeLimitedItem(player, shopName, HashUtil.GetItemHash(itemStack), tradeAmount, true);
        }

        // 로그 기록
        LogUtil.addLog(shopName, itemStack.getType().toString(), -tradeAmount, priceSum, currency, player.getName());

        // 메시지 출력
        SendSellMessage(currency, econ, player, tradeAmount, priceSum, itemStack);

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
            data.get().set(tradeIdx + ".stock", MathUtil.SafeAdd(stockOld, tradeAmount));
        }

        // 커맨드 실행
        RunSellCommand(data, player, shopName, itemStack, tradeAmount, priceSum, calcResult[1]);

        // 더티
        ShopUtil.shopDirty.put(shopName, true);

        // 이벤트 호출
        ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, tradeIdx, true), priceSellOld, DynaShopAPI.getSellPrice(shopName, itemStack), stockOld, DynaShopAPI.getStock(shopName, itemStack), DynaShopAPI.getMedian(shopName, itemStack), shopName, itemStack, player);
        Bukkit.getPluginManager().callEvent(event);

        // UI 갱신
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);
    }

    private static boolean CheckTransactionSuccess(String currencyType, Player player, double priceSum)
    {
        if (currencyType.equalsIgnoreCase(Constants.S_JOBPOINT))
        {
            return  JobsHook.addJobsPoint(player, priceSum);
        }
        else if (currencyType.equalsIgnoreCase(Constants.S_PLAYERPOINT))
        {
            return PlayerpointHook.addPP(player, priceSum);
        }
        else if (currencyType.equalsIgnoreCase(Constants.S_EXP))
        {
            player.giveExp((int)priceSum);
            return true;
        }
        else
        {
            EconomyResponse r = null;
            if (player != null)
                r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

            return r == null || r.transactionSuccess();
        }
    }

    private static void SendSellMessage(String currency, Economy econ, Player player, int actualAmount, double priceSum, ItemStack itemStack)
    {
        boolean itemHasCustomName = itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName();
        boolean useLocalizedName = !itemHasCustomName && ConfigUtil.GetLocalizedItemName();
        String message = "";
        if (currency.equalsIgnoreCase(Constants.S_JOBPOINT))
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS_JP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(JobsHook.getCurJobPoints(player)));
        }
        else if (currency.equalsIgnoreCase(Constants.S_PLAYERPOINT))
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS_PP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum, true))
                    .replace("{bal}", n(PlayerpointHook.getCurrentPP(player)));
        }
        else if (currency.equalsIgnoreCase(Constants.S_EXP))
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS_EXP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum, true))
                    .replace("{bal}", n(player.getTotalExperience()));
        }
        else
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(econ.getBalance(player)));
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
                            .replace("{amount}", String.valueOf(actualAmount))
                            .replace("{priceSum}", String.valueOf(priceSum))
                            .replace("{tax}", String.valueOf(tax));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sellCmd);
                }
            }
        }
    }

    private static int GetPlayerItemCount(Player player, ItemStack itemStack)
    {
        int count = 0;

        for (ItemStack stack : player.getInventory().getStorageContents())
        {
            if (stack != null && stack.isSimilar(itemStack))
            {
                count += stack.getAmount();
            }
        }

        return count;
    }
}
