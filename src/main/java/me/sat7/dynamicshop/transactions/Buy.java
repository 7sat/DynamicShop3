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
import org.bukkit.inventory.meta.ItemMeta;

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

    public static void buy(ItemTrade.CURRENCY currency, Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, boolean infiniteStock)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        Economy econ = null;
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            econ = DynamicShop.getEconomy();
        }

        int actualAmount = 0;
        int stockOld = data.get().getInt(tradeIdx + ".stock");
        double priceBuyOld = Calc.getCurrentPrice(shopName, tradeIdx, true);
        double priceSellOld = DynaShopAPI.getSellPrice(shopName, tempIS);

        for (int i = 0; i < tempIS.getAmount(); i++)
        {
            if (!infiniteStock && stockOld <= actualAmount + 1)
            {
                break;
            }

            double price = Calc.getCurrentPrice(shopName, tradeIdx, true, true);

            if (currency == ItemTrade.CURRENCY.VAULT)
            {
                if (priceSum + price > econ.getBalance(player)) break;
            } else if (currency == ItemTrade.CURRENCY.JOB_POINT)
            {
                if (priceSum + price > JobsHook.getCurJobPoints(player)) break;
            } else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
            {
                if (priceSum + price > PlayerpointHook.getCurrentPP(player)) break;
            }

            priceSum += price;

            if (!infiniteStock)
            {
                data.get().set(tradeIdx + ".stock", data.get().getInt(tradeIdx + ".stock") - 1);
            }

            actualAmount++;
        }

        // 실 구매 가능량이 0이다 = 돈이 없다.
        if (actualAmount <= 0)
        {
            String message = "";
            if (currency == ItemTrade.CURRENCY.VAULT)
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_MONEY").replace("{bal}", n(econ.getBalance(player)));
            } else if (currency == ItemTrade.CURRENCY.JOB_POINT)
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_POINT").replace("{bal}", n(JobsHook.getCurJobPoints(player)));
            } else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
            {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_PLAYER_POINT").replace("{bal}", n(PlayerpointHook.getCurrentPP(player)));
            }

            player.sendMessage(message);
            data.get().set(tradeIdx + ".stock", stockOld);
            return;
        }

        // 상점 재고 부족
        if (!infiniteStock && stockOld <= actualAmount)
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
            if (econ.getBalance(player) < priceSum)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_MONEY").replace("{bal}", n(econ.getBalance(player))));
                return;
            }

            r = DynamicShop.getEconomy().withdrawPlayer(player, priceSum);
            if (!r.transactionSuccess())
            {
                player.sendMessage(String.format("An error occured: %s", r.errorMessage));
                return;
            }
        } else if (currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            if (JobsHook.getCurJobPoints(player) < priceSum)
                return;

            if (!JobsHook.addJobsPoint(player, priceSum * -1))
                return;
        }
        else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            if (PlayerpointHook.getCurrentPP(player) < priceSum)
                return;

            if (!PlayerpointHook.addPP(player, priceSum * -1))
                return;
        }

        int leftAmount = actualAmount;
        while (leftAmount > 0)
        {
            int giveAmount = tempIS.getType().getMaxStackSize();
            if (giveAmount > leftAmount) giveAmount = leftAmount;

            ItemStack iStack = new ItemStack(tempIS.getType(), giveAmount);
            iStack.setItemMeta((ItemMeta) data.get().get(tradeIdx + ".itemStack"));

            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(iStack);
            if (leftOver.size() != 0)
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.INVENTORY_FULL"));
                Location loc = player.getLocation();

                ItemStack leftStack = new ItemStack(tempIS.getType(), leftOver.get(0).getAmount());
                leftStack.setItemMeta((ItemMeta) data.get().get(tradeIdx + ".itemStack"));

                player.getWorld().dropItem(loc, leftStack);
            }

            leftAmount -= giveAmount;
        }

        //로그 기록
        String currencyString = "";
        if(currency == ItemTrade.CURRENCY.VAULT)
        {
            currencyString = "vault";
        }
        else if(currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            currencyString = "jobpoint";
        }
        else if(currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            currencyString = "playerPoint";
        }
        LogUtil.addLog(shopName, tempIS.getType().toString(), actualAmount, priceSum, currencyString, player.getName());

        // 메시지 출력
        SendBuyMessage(currency, econ, r, player, actualAmount, priceSum, tempIS);

        // 플레이어에게 소리 재생
        SoundUtil.playerSoundEffect(player, "buy");

        // 상점 계좌 잔액 수정
        if (data.get().contains("Options.Balance"))
        {
            ShopUtil.addShopBalance(shopName, priceSum);
        }

        // 커맨드 실행
        RunBuyCommand(data, player, shopName, tempIS, actualAmount, priceSum);

        ShopUtil.shopDirty.put(shopName, true);
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);

        // 이벤트 호출
        ShopBuySellEvent event = new ShopBuySellEvent(true, priceBuyOld, Calc.getCurrentPrice(shopName, tradeIdx, true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static void SendBuyMessage(ItemTrade.CURRENCY currency, Economy econ, EconomyResponse r, Player player, int actualAmount, double priceSum, ItemStack tempIS)
    {
        String message = "";
        boolean itemHasCustomName = tempIS.getItemMeta() != null && tempIS.getItemMeta().hasDisplayName();
        boolean useLocalizedName = !itemHasCustomName && DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName");
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(r.amount))
                    .replace("{bal}", n(econ.getBalance(player)));
        } else if (currency == ItemTrade.CURRENCY.JOB_POINT)
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
            LangUtil.sendMessageWithLocalizedItemName(player, message, tempIS.getType());
        } else
        {
            String itemNameFinal = itemHasCustomName ? tempIS.getItemMeta().getDisplayName() : ItemsUtil.getBeautifiedName(tempIS.getType());
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
                for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.buy").getValues(false).entrySet())
                {
                    String buyCmd = s.getValue().toString()
                            .replace("{player}", player.getName())
                            .replace("{shop}", shopName)
                            .replace("{itemType}", tempIS.getType().toString())
                            .replace("{amount}", actualAmount+"")
                            .replace("{priceSum}", priceSum+"");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), buyCmd);
                }
            }
        }
    }
}
