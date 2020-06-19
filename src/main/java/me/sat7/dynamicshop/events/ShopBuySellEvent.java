package me.sat7.dynamicshop.events;

import lombok.Getter;
import me.sat7.dynamicshop.DynaShopAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This event will fire every time a buy or sell is made in the DynamicShop plugin.
 */
public class ShopBuySellEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final boolean buy;
    @Getter
    private final double oldBuyPrice;
    @Getter
    private final double newBuyPrice;
    @Getter
    private final double oldSellPrice;
    @Getter
    private final double newSellPrice;
    @Getter
    private final int oldStock;
    @Getter
    private final int newStock;
    @Getter
    private final int median;
    @Getter
    private final String shopName;
    @Getter
    private final Material material;
    @Getter
    private final boolean jobPoint;

    public ShopBuySellEvent(boolean buy, double oldBuyPrice, double newBuyPrice, double oldSellPrice, double newSellPrice, int oldStock, int newStock, int median, String shopName, ItemStack merchandise, Player p) {
        super(p);
        this.buy = buy;
        this.oldBuyPrice = oldBuyPrice;
        this.newBuyPrice = newBuyPrice;
        this.oldSellPrice = oldSellPrice;
        this.newSellPrice = newSellPrice;
        this.oldStock = oldStock;
        this.newStock = newStock;
        this.median = median;
        this.shopName = shopName;
        this.material = merchandise.getType();
        this.jobPoint = DynaShopAPI.isJobsPointShop(shopName);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public String toString() {
        return "ShopBuySellEvent{" +
                "buy=" + buy +
                ", oldBuyPrice=" + oldBuyPrice +
                ", newBuyPrice=" + newBuyPrice +
                ", oldSellPrice=" + oldSellPrice +
                ", newSellPrice=" + newSellPrice +
                ", oldStock=" + oldStock +
                ", newStock=" + newStock +
                ", median=" + median +
                ", shopName='" + shopName + '\'' +
                ", material=" + material.toString() +
                ", jobPoint=" + jobPoint +
                ", player=" + player.toString() +
                '}';
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
