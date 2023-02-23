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

    public static double quickSellItem(Player player, ItemStack tempIS, String shopName, int tradeIdx, boolean isShiftClick, int slot)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        ItemTrade.CURRENCY currencyType;
        String currencyString;
        if(data.get().contains("Options.flag.jobpoint"))
        {
            currencyType = ItemTrade.CURRENCY.JOB_POINT;
            currencyString = "jobPoint";
        }
        else if(data.get().contains("Options.flag.playerpoint"))
        {
            currencyType = ItemTrade.CURRENCY.PLAYER_POINT;
            currencyString = "playerPoint";
        }
        else
        {
            currencyType = ItemTrade.CURRENCY.VAULT;
            currencyString = "vault";
        }

        double priceSellOld = DynaShopAPI.getSellPrice(shopName, tempIS);
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
        }
        else
        {
            tradeAmount = tempIS.getAmount();
        }

        // 판매할 아이탬이 없음
        if (tradeAmount == 0)
        {
            if(player != null)
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"));

            return 0;
        }

        double[] calcResult = Calc.calcTotalCost(shopName, String.valueOf(tradeIdx), -tradeAmount);
        priceSum += calcResult[0];

        // 재고 증가
        if (stockOld > 0)
        {
            data.get().set(tradeIdx + ".stock", MathUtil.SafeAdd(stockOld, tradeAmount));
        }

        // 실제 거래부----------
        Economy econ = null;
        EconomyResponse r = null;
        if (currencyType == ItemTrade.CURRENCY.VAULT)
        {
            econ = DynamicShop.getEconomy();
            if (player != null)
                r = DynamicShop.getEconomy().depositPlayer(player, priceSum);

            if (r != null && !r.transactionSuccess())
                return 0;
        }
        else if (currencyType == ItemTrade.CURRENCY.JOB_POINT)
        {
            if (!JobsHook.addJobsPoint(player, priceSum))
                return 0;
        }
        else if (currencyType == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            if (!PlayerpointHook.addPP(player, priceSum))
                return 0;
        }

        //로그 기록
        LogUtil.addLog(shopName, tempIS.getType().toString(), -tradeAmount, priceSum, currencyString, player != null ? player.getName() : shopName);

        if (player != null)
        {
            // 플레이어에게 메시지 출력
            SendSellMessage(currencyType, econ, r, player, tradeAmount, priceSum, tempIS);

            // 플레이어에게 소리 재생
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1, 1);
        }

        // 상점 계좌 잔액 수정
        if (data.get().contains("Options.Balance"))
        {
            ShopUtil.addShopBalance(shopName, priceSum * -1);
        }

        // 커맨드 실행
        RunSellCommand(data, player, shopName, tempIS, tradeAmount, priceSum, calcResult[1]);

        ShopUtil.shopDirty.put(shopName, true);

        // 이벤트 호출
        if (player != null)
        {
            ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true),
                                                          priceSellOld,
                                                          DynaShopAPI.getSellPrice(shopName, tempIS),
                                                          stockOld,
                                                          DynaShopAPI.getStock(shopName, tempIS),
                                                          DynaShopAPI.getMedian(shopName, tempIS),
                                                          shopName, tempIS, player);
            Bukkit.getPluginManager().callEvent(event);
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
        if (ShopUtil.getShopBalance(shopName) != -1 && ShopUtil.getShopBalance(shopName) < Calc.calcTotalCost(shopName, tradeIdx, tempIS.getAmount())[0])
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_BAL_LOW"));
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
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"));
            return;
        }

        double[] calcResult = Calc.calcTotalCost(shopName, tradeIdx, -actualAmount);
        priceSum += calcResult[0];

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
        } else if (currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            if (!PlayerpointHook.addPP(player, priceSum))
                return;
        }

        //로그 기록
        String currencyString = "";
        if(currency == ItemTrade.CURRENCY.VAULT)
        {
            currencyString = "vault";
        }
        else if(currency == ItemTrade.CURRENCY.JOB_POINT)
        {
            currencyString = "jobPoint";
        }
        else if(currency == ItemTrade.CURRENCY.PLAYER_POINT)
        {
            currencyString = "playerPoint";
        }
        LogUtil.addLog(shopName, tempIS.getType().toString(), -actualAmount, priceSum, currencyString, player.getName());

        // 메시지 출력
        SendSellMessage(currency, econ, r, player, actualAmount, priceSum, tempIS);

        // 플레이어에게 소리 재생
        SoundUtil.playerSoundEffect(player, "sell");

        // 상점 계좌 잔액 수정
        if (data.get().contains("Options.Balance"))
        {
            ShopUtil.addShopBalance(shopName, priceSum * -1);
        }

        // 커맨드 실행
        RunSellCommand(data, player, shopName, tempIS, actualAmount, priceSum, calcResult[1]);

        ShopUtil.shopDirty.put(shopName, true);
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);

        // 이벤트 호출
        ShopBuySellEvent event = new ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static void SendSellMessage(ItemTrade.CURRENCY currency, Economy econ, EconomyResponse r, Player player, int actualAmount, double priceSum, ItemStack tempIS)
    {
        boolean itemHasCustomName = tempIS.getItemMeta() != null && tempIS.getItemMeta().hasDisplayName();
        boolean useLocalizedName = !itemHasCustomName && DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName");
        String message = "";
        if (currency == ItemTrade.CURRENCY.VAULT)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(r.amount))
                    .replace("{bal}", n(econ.getBalance(player)));
        } else if (currency == ItemTrade.CURRENCY.JOB_POINT)
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
            LangUtil.sendMessageWithLocalizedItemName(player, message, tempIS.getType());
        } else
        {
            String itemNameFinal = itemHasCustomName ? tempIS.getItemMeta().getDisplayName() : ItemsUtil.getBeautifiedName(tempIS.getType());
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
                for (Map.Entry<String, Object> s : data.get().getConfigurationSection("Options.command.sell").getValues(false).entrySet())
                {
                    String sellCmd = s.getValue().toString()
                            .replace("{player}", player.getName())
                            .replace("{shop}", shopName)
                            .replace("{itemType}", tempIS.getType().toString())
                            .replace("{amount}", actualAmount+"")
                            .replace("{priceSum}", priceSum+"")
                            .replace("{tax}", tax+"");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sellCmd);
                }
            }
        }
    }
}
