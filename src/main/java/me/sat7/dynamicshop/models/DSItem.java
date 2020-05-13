package me.sat7.dynamicshop.models;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DSItem {
    private ItemStack itemStack;
    private double buyValue;
    private double sellValue;
    private double minPrice;
    private double maxPrice;
    private int median;
    private int stock;

    public DSItem(ItemStack itemStack, double buyValue, double sellValue, double minPrice, double maxPrice, int median, int stock) {
        setItemStack(itemStack);
        setBuyValue(Math.round(buyValue*100)/100.0);
        setSellValue(Math.round(sellValue*100)/100.0);
        setMinPrice(Math.round(minPrice*100)/100.0);
        setMaxPrice(Math.round(maxPrice*100)/100.0);
        setMedian(median);
        setStock(stock);
    }
}
