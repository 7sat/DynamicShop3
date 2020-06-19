package me.sat7.dynamicshop.transactions;

import java.util.HashMap;

import me.sat7.dynamicshop.events.ShopBuySellEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.LogUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import me.sat7.dynamicshop.utilities.SoundUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public final class Buy {
    private Buy() {

    }

    // 구매
    public static void buyItemCash(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, double deliverycharge, boolean infiniteStock)
    {
        Economy econ = DynamicShop.getEconomy();
        double priceBuyOld = Calc.getCurrentPrice(shopName,tradeIdx,true);
        double priceSellOld = DynaShopAPI.getSellPrice(shopName, tempIS);
        int stockOld = ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock");

        int actualAmount = 0;

        for (int i = 0; i<tempIS.getAmount(); i++)
        {
            if(!infiniteStock && stockOld <= actualAmount+1)
            {
                break;
            }

            double price = Calc.getCurrentPrice(shopName,tradeIdx,true);

            if(priceSum + price > econ.getBalance(player)) break;

            priceSum += price;

            if(!infiniteStock)
            {
                ShopUtil.ccShop.get().set(shopName+"." + tradeIdx + ".stock",
                        ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") - 1);
            }

            actualAmount++;
        }

        // 실 구매 가능량이 0이다 = 돈이 없다.
        if(actualAmount <= 0)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("NOT_ENOUGH_MONEY").replace("{bal}",econ.format(econ.getBalance(player))));
            ShopUtil.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 상점 재고 부족
        if(!infiniteStock && stockOld <= actualAmount)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("OUT_OF_STOCK"));
            ShopUtil.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 실 거래부-------
        if(econ.getBalance(player) >= priceSum)
        {
            EconomyResponse r = DynamicShop.getEconomy().withdrawPlayer(player, priceSum);

            if(r.transactionSuccess())
            {
                int leftAmount = actualAmount;
                while (leftAmount>0)
                {
                    int giveAmount = tempIS.getType().getMaxStackSize();
                    if(giveAmount > leftAmount) giveAmount = leftAmount;

                    ItemStack iStack = new ItemStack(tempIS.getType(),giveAmount);
                    iStack.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                    HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(iStack);
                    if(leftOver.size() != 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("INVEN_FULL"));
                        Location loc = player.getLocation();

                        ItemStack leftStack = new ItemStack(tempIS.getType(),leftOver.get(0).getAmount());
                        leftStack.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                        player.getWorld().dropItem(loc, leftStack);
                    }

                    leftAmount -= giveAmount;
                }

                //로그 기록
                LogUtil.addLog(shopName,tempIS.getType().toString(),actualAmount,priceSum,"vault",player.getName());

                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("BUY_SUCCESS")
                        .replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()))
                        .replace("{amount}", Integer.toString(actualAmount))
                        .replace("{price}",econ.format(r.amount))
                        .replace("{bal}",econ.format(econ.getBalance((player)))));
                SoundUtil.playerSoundEffect(player,"buy");

                if(deliverycharge > 0)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);
                }

                if(ShopUtil.ccShop.get().contains(shopName+".Options.Balance"))
                {
                    ShopUtil.addShopBalance(shopName,priceSum);
                }

                DynaShopAPI.openItemTradeGui(player,shopName, tradeIdx);
                ShopUtil.ccShop.save();

                ShopBuySellEvent event = new ShopBuySellEvent(true, priceBuyOld, Calc.getCurrentPrice(shopName,tradeIdx,true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player);
                Bukkit.getPluginManager().callEvent(event);
            }
            else
            {
                player.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("NOT_ENOUGH_MONEY").replace("{bal}",econ.format(econ.getBalance(player))));
        }
    }

    // 구매 jp
    public static void buyItemJobPoint(Player player, String shopName, String tradeIdx, ItemStack tempIS, double priceSum, double deliverycharge, boolean infiniteStock)
    {
        int actualAmount = 0;
        int stockOld = ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock");
        double priceBuyOld = Calc.getCurrentPrice(shopName,tradeIdx,true);
        double priceSellOld = DynaShopAPI.getSellPrice(shopName, tempIS);

        for (int i = 0; i<tempIS.getAmount(); i++)
        {
            if(!infiniteStock && stockOld <= actualAmount+1)
            {
                break;
            }

            double price = Calc.getCurrentPrice(shopName,tradeIdx,true);

            if(priceSum + price > JobsHook.getCurJobPoints(player)) break;

            priceSum += price;

            if(!infiniteStock)
            {
                ShopUtil.ccShop.get().set(shopName+"." + tradeIdx + ".stock",
                        ShopUtil.ccShop.get().getInt(shopName+"." + tradeIdx + ".stock") - 1);
            }

            actualAmount++;
        }

        // 실 구매 가능량이 0이다 = 돈이 없다.
        if(actualAmount <= 0)
        {
            player.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().getString("NOT_ENOUGH_POINT").replace("{bal}", DynaShopAPI.df.format(JobsHook.getCurJobPoints(player))));
            ShopUtil.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 상점 재고 부족
        if(!infiniteStock && stockOld <= actualAmount)
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("OUT_OF_STOCK"));
            ShopUtil.ccShop.get().set(shopName+"." + tradeIdx + ".stock", stockOld);
            return;
        }

        // 실 거래부-------
        if(JobsHook.getCurJobPoints(player) >= priceSum)
        {
            if(JobsHook.addJobsPoint(player,priceSum * -1))
            {
                int leftAmount = actualAmount;
                while (leftAmount>0)
                {
                    int giveAmount = tempIS.getType().getMaxStackSize();
                    if(giveAmount > leftAmount) giveAmount = leftAmount;

                    ItemStack iStack = new ItemStack(tempIS.getType(),giveAmount);
                    iStack.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                    HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(iStack);
                    if(leftOver.size() != 0)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("INVEN_FULL"));
                        Location loc = player.getLocation();

                        ItemStack leftStack = new ItemStack(tempIS.getType(),leftOver.get(0).getAmount());
                        leftStack.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                        player.getWorld().dropItem(loc, leftStack);
                    }

                    leftAmount -= giveAmount;
                }

                //로그 기록
                LogUtil.addLog(shopName,tempIS.getType().toString(),actualAmount,priceSum,"jobpoint",player.getName());

                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("BUY_SUCCESS_JP")
                        .replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()))
                        .replace("{amount}", Integer.toString(actualAmount))
                        .replace("{price}", DynaShopAPI.df.format(priceSum))
                        .replace("{bal}", DynaShopAPI.df.format(JobsHook.getCurJobPoints((player)))));
                SoundUtil.playerSoundEffect(player,"buy");

                if(deliverycharge > 0)
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("DELIVERYCHARGE")+": "+deliverycharge);
                }

                if(ShopUtil.ccShop.get().contains(shopName+".Options.Balance"))
                {
                    ShopUtil.addShopBalance(shopName,priceSum);
                }

                DynaShopAPI.openItemTradeGui(player,shopName, tradeIdx);
                ShopUtil.ccShop.save();

                ShopBuySellEvent event = new ShopBuySellEvent(true, priceBuyOld, Calc.getCurrentPrice(shopName,tradeIdx,true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }
}
