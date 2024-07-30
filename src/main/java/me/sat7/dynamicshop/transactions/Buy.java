package me.sat7.dynamicshop.transactions;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.economyhook.JobsHook;
import me.sat7.dynamicshop.economyhook.PlayerpointHook;
import me.sat7.dynamicshop.events.ShopBuySellEvent;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.guis.ItemTrade;
import me.sat7.dynamicshop.utilities.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class Buy
{
    private Buy()
    {

    }

    public static void buy(String currency, Player player, String shopName, String tradeIdx, ItemStack itemStack, double priceSum, boolean infiniteStock)
    {
        Economy econ = null;
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        int tradeAmount = 0;
        int stockOld = data.get().getInt(tradeIdx + ".stock");
        double priceBuyOld = Calc.getCurrentPrice(shopName, tradeIdx, true);
        double priceSellOld = DynaShopAPI.getSellPrice(shopName, itemStack);

        double playerBalance = 0;
        if (currency.equalsIgnoreCase(Constants.S_EXP))
        {
            playerBalance = player.getTotalExperience();
        }
        else if (currency.equalsIgnoreCase(Constants.S_JOBPOINT))
        {
            playerBalance = JobsHook.getCurJobPoints(player);
        }
        else if (currency.equalsIgnoreCase(Constants.S_PLAYERPOINT))
        {
            playerBalance = PlayerpointHook.getCurrentPP(player);
        }
        else
        {
            econ = DynamicShop.getEconomy();
            playerBalance = econ.getBalance(player);
        }

            // 플레이어 당 거래량 제한 확인
        int tradeIdxInt = Integer.parseInt(tradeIdx);
        int tradeLimitPerPlayer = ShopUtil.GetBuyLimitPerPlayer(shopName, tradeIdxInt);
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
            if (currency.equalsIgnoreCase(Constants.S_JOBPOINT))
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_POINT").replace("{bal}", n(playerBalance));
            }
            else if (currency.equalsIgnoreCase(Constants.S_PLAYERPOINT))
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_PLAYER_POINT").replace("{bal}", n(playerBalance));
            }
            else if (currency.equalsIgnoreCase(Constants.S_EXP))
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_EXP_POINT").replace("{bal}", n(playerBalance));
            }
            else
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_MONEY").replace("{bal}", n(playerBalance));
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

        EconomyResponse r;
        if (currency.equalsIgnoreCase(Constants.S_JOBPOINT))
        {
            if (!JobsHook.addJobsPoint(player, priceSum * -1))
                return;
        }
        else if (currency.equalsIgnoreCase(Constants.S_PLAYERPOINT))
        {
            if (!PlayerpointHook.addPP(player, priceSum * -1))
                return;
        }
        else if (currency.equalsIgnoreCase(Constants.S_EXP))
        {
            player.giveExp((int)(priceSum * -1));
        }
        else
        {
            r = DynamicShop.getEconomy().withdrawPlayer(player, priceSum);
            if (!r.transactionSuccess())
            {
                player.sendMessage(String.format("An error occured: %s", r.errorMessage));
                return;
            }
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
        LogUtil.addLog(shopName, itemStack.getType().toString(), tradeAmount, priceSum, currency, player.getName());

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

    private static void SendBuyMessage(String currency, Economy econ, Player player, int actualAmount, double priceSum, ItemStack itemStack)
    {
        String message = "";
        boolean itemHasCustomName = itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName();
        boolean useLocalizedName = !itemHasCustomName && ConfigUtil.GetLocalizedItemName();
        if (currency.equalsIgnoreCase(Constants.S_EXP))
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS_EXP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum, true))
                    .replace("{bal}", n(player.getTotalExperience()));
        }
        else if (currency.equalsIgnoreCase(Constants.S_JOBPOINT))
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS_JP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(JobsHook.getCurJobPoints(player)));
        }
        else if (currency.equalsIgnoreCase(Constants.S_PLAYERPOINT))
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS_PP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum, true))
                    .replace("{bal}", n(PlayerpointHook.getCurrentPP(player)));
        }
        else
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS", !useLocalizedName)
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
                            .replace("{amount}", String.valueOf(actualAmount))
                            .replace("{priceSum}", String.valueOf(priceSum));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), buyCmd);
                }
            }
        }
    }
}
