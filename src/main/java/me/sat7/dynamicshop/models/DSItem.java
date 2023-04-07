package me.sat7.dynamicshop.models;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DSItem
{
    public ItemStack itemStack;
    public double buyValue;
    public double sellValue;
    public double minPrice;
    public double maxPrice;
    public int median;
    public int stock;
    public int maxStock;
    public int discount;
    public int sellLimit;
    public int buyLimit;
    public long tradeLimitInterval; // ms
    public long tradeLimitNextTimer;

    public DSItem(ItemStack itemStack, double buyValue, double sellValue, double minPrice, double maxPrice, int median, int stock)
    {
        setItemStack(itemStack);
        setBuyValue(Math.round(buyValue * 100) / 100.0);
        setSellValue(Math.round(sellValue * 100) / 100.0);
        setMinPrice(Math.round(minPrice * 100) / 100.0);
        setMaxPrice(Math.round(maxPrice * 100) / 100.0);
        setMedian(median);
        setStock(stock);
        maxStock = -1;
    }
    public DSItem(ItemStack itemStack, double buyValue, double sellValue, double minPrice, double maxPrice, int median, int stock, int maxStock, int discount, int sellLimit, int buyLimit, long tradeLimitInterval, long tradeLimitNextTimer)
    {
        setItemStack(itemStack);
        setBuyValue(Math.round(buyValue * 100) / 100.0);
        setSellValue(Math.round(sellValue * 100) / 100.0);
        setMinPrice(Math.round(minPrice * 100) / 100.0);
        setMaxPrice(Math.round(maxPrice * 100) / 100.0);
        setMedian(median);
        setStock(stock);
        setMaxStock(maxStock);
        setDiscount(discount);
        setSellLimit(sellLimit);
        setBuyLimit(buyLimit);
        setTradeLimitInterval(tradeLimitInterval);
        setTradeLimitNextTimer(tradeLimitNextTimer);
    }
}
