package me.sat7.dynamicshop.transactions;

import java.util.HashMap;
import java.util.Map;

import me.sat7.dynamicshop.economyhook.PlayerpointHook;
import me.sat7.dynamicshop.events.ShopBuySellEvent;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.guis.ItemTrade;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.economyhook.JobsHook;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import static me.sat7.dynamicshop.utilities.LangUtil.*;

public final class Buy
{
    private Buy()
    {

    }

    public static void buy(ItemTrade.CURRENCY currency, Player player, String shopName, String tradeIdx, ItemStack itemStack, double priceSum, boolean infiniteStock)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        Economy econ = null;
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            econ = DynamicShop.getEconomy();
        }

        int tradeAmount = 0;
        int stockOld = data.get().getInt(tradeIdx + ".stock");
        double priceBuyOld = Calc.getCurrentPrice(shopName, tradeIdx, true);
        double priceSellOld = DynaShopAPI.getSellPrice(shopName, itemStack);

        double playerBalance = 0;
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            playerBalance = econ.getBalance(player);
        }
        else if (currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            playerBalance = JobsHook.getCurJobPoints(player);
        }
        else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            playerBalance = PlayerpointHook.getCurrentPP(player);
        }

        // 플레이어 당 거래량 제한 확인
        int tradeIdxInt = Integer.parseInt(tradeIdx);
        int tradeLimitPerPlayer = ShopUtil.GetTradeLimitPerPlayer(shopName, tradeIdxInt);
        int playerTradingVolume = UserUtil.GetPlayerTradingVolume(player, shopName, HashUtil.GetItemHash(itemStack));

        for (int i = 0; i < itemStack.getAmount(); i++)
        {
            if (tradeLimitPerPlayer > 0 && tradeLimitPerPlayer <= playerTradingVolume + tradeAmount)
            {
                if (tradeAmount == 0)
                {
                    return;
                }

                break;
            }

            if (!infiniteStock && stockOld <= tradeAmount + 1)
                break;

            double price = Calc.getCurrentPrice(shopName, tradeIdx, true, true);
            if (priceSum + price > playerBalance)
                break;

            priceSum += price;

            if (!infiniteStock)
            {
                data.get().set(tradeIdx + ".stock", data.get().getInt(tradeIdx + ".stock") - 1);
            }

            tradeAmount++;
        }

        // 실 구매 가능량이 0이다 = 돈이 없다.
        if (tradeAmount <= 0)
        {
            String message = "";
            if (currency == ItemTrade.CURRENCY.VAULT)
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_MONEY").replace("{bal}", n(playerBalance));
            }
            else if (currency == ItemTrade.CURRENCY.JOB_POINT)
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_POINT").replace("{bal}", n(playerBalance));
            }
            else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_PLAYER_POINT").replace("{bal}", n(playerBalance));
            }

            player.sendMessage(message);
            data.get().set(tradeIdx + ".stock", stockOld);
            return;
        }

        // 상점 재고 부족
        if (!infiniteStock && stockOld <= tradeAmount)
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.OUT_OF_STOCK"));
            data.get().set(tradeIdx + ".stock", stockOld);
            return;
        }

        if (data.get().contains("Options.flag.integeronly"))
        {
            priceSum = Math.ceil(priceSum);
        }

        EconomyResponse r = null;
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            r = DynamicShop.getEconomy().withdrawPlayer(player, priceSum);
            if (!r.transactionSuccess())
            {
                player.sendMessage(String.format("An error occured: %s", r.errorMessage));
                return;
            }
        }
        else if (currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            if (!JobsHook.addJobsPoint(player, priceSum * -1))
                return;
        }
        else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            if (!PlayerpointHook.addPP(player, priceSum * -1))
                return;
        }

        int leftAmount = tradeAmount;
        int maxStackSize = itemStack.getType().getMaxStackSize();
        while (leftAmount > 0)
        {
            int giveAmount = maxStackSize;
            if (giveAmount > leftAmount)
                giveAmount = leftAmount;

            itemStack.setAmount(giveAmount);

            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(itemStack);
            if (leftOver.size() != 0)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.INVENTORY_FULL"));
                Location loc = player.getLocation();

                itemStack.setAmount(leftOver.get(0).getAmount());

                player.getWorld().dropItem(loc, itemStack);
            }

            leftAmount -= giveAmount;
        }

        // 플레이어 당 거래량 제한 아이템에 대한 처리.
        if (tradeLimitPerPlayer < Integer.MAX_VALUE)
        {
            UserUtil.OnPlayerTradeLimitedItem(player, shopName, HashUtil.GetItemHash(itemStack), tradeAmount, false);
        }

        //로그 기록
        LogUtil.addLog(shopName, itemStack.getType().toString(), tradeAmount, priceSum, StringUtil.GetCurrencyString(currency), player.getName());

        // 메시지 출력
        SendBuyMessage(currency, econ, player, tradeAmount, priceSum, itemStack);

        // 플레이어에게 소리 재생
        SoundUtil.playerSoundEffect(player, "buy");

        // 상점 계좌 잔액 수정
        if (data.get().contains("Options.Balance"))
        {
            ShopUtil.addShopBalance(shopName, priceSum);
        }

        // 커맨드 실행
        RunBuyCommand(data, player, shopName, itemStack, tradeAmount, priceSum);

        ShopUtil.shopDirty.put(shopName, true);
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);

        // 이벤트 호출
        ShopBuySellEvent event = new ShopBuySellEvent(true, priceBuyOld, Calc.getCurrentPrice(shopName, tradeIdx, true), priceSellOld, DynaShopAPI.getSellPrice(shopName, itemStack), stockOld, DynaShopAPI.getStock(shopName, itemStack), DynaShopAPI.getMedian(shopName, itemStack), shopName, itemStack, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static void SendBuyMessage(ItemTrade.CURRENCY currency, Economy econ, Player player, int actualAmount, double priceSum, ItemStack itemStack)
    {
        String message = "";
        boolean itemHasCustomName = itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName();
        boolean useLocalizedName = !itemHasCustomName && ConfigUtil.GetLocalizedItemName();
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(econ.getBalance(player)));
        }
        else if (currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS_JP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(JobsHook.getCurJobPoints(player)));
        }
        else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS_PP", !useLocalizedName)
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

    private static void RunBuyCommand(CustomConfig data, Player player, String shopName, ItemStack tempIS, int actualAmount, double priceSum)
    {
        if (data.get().contains("Options.command.active") && data.get().getBoolean("Options.command.active") &&
                data.get().contains("Options.command.buy"))
        {
            if (data.get().getConfigurationSection("Options.command.buy") != null)
            {
                priceSum = Math.round(priceSum * 10000) / 10000.0;

                for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.buy").getValues(false).entrySet())
                {
                    String buyCmd = s.getValue().toString()
                            .replace("{player}", player.getName())
                            .replace("{shop}", shopName)
                            .replace("{itemType}", tempIS.getType().toString())
                            .replace("{amount}", actualAmount + "")
                            .replace("{priceSum}", priceSum + "");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), buyCmd);
                }
            }
        }
    }
}
