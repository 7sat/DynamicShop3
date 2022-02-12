package me.sat7.dynamicshop.utilities;

import me.sat7.dynamicshop.files.CustomConfig;

public final class LayoutUtil
{
    public static CustomConfig ccLayout;

    private LayoutUtil()
    {

    }

    public static void Setup()
    {
        ccLayout.setup("Layout", null);
        ccLayout.get().options().header(
                "SHOP.INFO: [ShopLore][Permission][Tax][ShopBalance][ShopHour][ShopPosition]" +
                "\nSHOP.ITEM_INFO: {Buy} {BuyArrow} {Sell} {SellArrow} {Stock} [PricingType][TradeLore][ItemMetaLore]" +
                "\nTRADE_VIEW.BUY: {TradeLore} {Price} {Stock}[DeliveryCharge]" +
                "\nTRADE_VIEW.SELL: {TradeLore} {Price} {Stock}[DeliveryCharge]" +
                "\nTRADE_VIEW.BALANCE: [{]PlayerBalance][ShopBalance]"
        );

        ccLayout.get().addDefault("SHOP.INFO", "§f[ShopLore][Permission][Tax][ShopBalance][ShopHour][ShopPosition]");
        ccLayout.get().addDefault("SHOP.ITEM_INFO", "§f{Buy} {BuyArrow}\n{Sell} {SellArrow}\n{Stock}[PricingType][TradeLore][ItemMetaLore]");
        ccLayout.get().addDefault("TRADE_VIEW.BUY", "§f{TradeLore}\n{Price}\n{Stock}[DeliveryCharge]");
        ccLayout.get().addDefault("TRADE_VIEW.SELL", "§f{TradeLore}\n{Price}\n{Stock}[DeliveryCharge]");
        ccLayout.get().addDefault("TRADE_VIEW.BALANCE", "§f[PlayerBalance][ShopBalance]");

        ccLayout.get().options().copyDefaults(true);
        ccLayout.save();
    }

    public static String l(String key)
    {
        return ccLayout.get().getString(key);
    }
}
