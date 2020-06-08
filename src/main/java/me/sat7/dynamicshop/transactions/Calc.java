package me.sat7.dynamicshop.transactions;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public final class Calc {
    private Calc() {

    }

    // 특정 아이탬의 현재 가치를 계산 (다이나믹 or 고정가)
    public static double getCurrentPrice(String shopName, String idx, boolean buy)
    {
        double price;

        double value;
        if(!buy && ShopUtil.ccShop.get().contains(shopName+"."+idx+".value2"))
        {
            value = ShopUtil.ccShop.get().getDouble(shopName+"."+idx+".value2");
        }
        else
        {
            value = ShopUtil.ccShop.get().getDouble(shopName+"." + idx + ".value");
        }
        double min = ShopUtil.ccShop.get().getDouble(shopName+"."+idx+".valueMin");
        double max = ShopUtil.ccShop.get().getDouble(shopName+"."+idx+".valueMax");
        int median = ShopUtil.ccShop.get().getInt(shopName +"." + idx + ".median");
        int stock = ShopUtil.ccShop.get().getInt(shopName+"." + idx + ".stock");

        if(median <= 0 || stock <= 0)
        {
            price = value;
        }
        else
        {
            price = (median * value) / stock;
        }

        if(min != 0 && price < min)
        {
            price = min;
        }
        if(max != 0 && price > max)
        {
            price = max;
        }

        return price;
    }

    // 특정 아이탬의 앞으로 n개의 가치합을 계산 (다이나믹 or 고정가) (세금 반영)
    public static double calcTotalCost(String shopName, String idx, int amount)
    {
        double total = 0;
        int median = ShopUtil.ccShop.get().getInt(shopName+"." + idx + ".median");
        int tempStock = ShopUtil.ccShop.get().getInt(shopName+"." + idx + ".stock");

        double value;
        if(amount < 0 && ShopUtil.ccShop.get().contains(shopName+"."+idx+".value2"))
        {
            value = ShopUtil.ccShop.get().getDouble(shopName+"."+idx+".value2");
        }
        else
        {
            value = ShopUtil.ccShop.get().getDouble(shopName+"." + idx + ".value");
        }

        if (median <= 0 || tempStock <= 0) {
            total = value * Math.abs(amount);
        } else {
            for (int i = 0; i < Math.abs(amount); i++) {
                if (amount < 0) {
                    tempStock++;
                }
                double temp = median * value / tempStock;
                double min = ShopUtil.ccShop.get().getDouble(shopName + "." + idx + ".valueMin");
                double max = ShopUtil.ccShop.get().getDouble(shopName + "." + idx + ".valueMax");

                if (min != 0 && temp < min) {
                    temp = min;
                }
                if (max != 0 && temp > max) {
                    temp = max;
                }

                total += temp;

                if (amount > 0) {
                    tempStock--;
                    if (tempStock < 2) {
                        break;
                    }
                }
            }
        }

        // 세금 적용 (판매가 별도지정시 세금계산 안함)
        if(amount < 0 && !ShopUtil.ccShop.get().contains(shopName+"."+idx+".value2"))
        {
            total = total - ((total / 100) * getTaxRate(shopName));
        }

        return (Math.round(total*100)/100.0);
    }

    // 상점의 세율 반환
    public static int getTaxRate(String shopName)
    {
        if(ShopUtil.ccShop.get().contains(shopName+".Options.SalesTax"))
        {
            return ShopUtil.ccShop.get().getInt(shopName+".Options.SalesTax");
        }
        else
        {
            return ConfigUtil.getCurrentTax();
        }
    }
}
