package me.sat7.dynamicshop.transactions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import me.sat7.dynamicshop.files.FileUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

public class TransactionTests {

    @Before
    public void setup() {
        ShopUtil.ccShop = FileUtil.generateOutOfStockCustomConfig();
    }

    @Test
    public void calcCostWhenOutOfStockShouldBeSameAsInStock() {
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 0);
        double amountSell = Calc.calcTotalCost("default", "COBBLESTONE", -1);
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 1);
        double amountBuy = Calc.calcTotalCost("default", "COBBLESTONE", 1);
        assertEquals(amountSell, amountBuy, 0.01);
    }

    @Test
    public void calcCostShouldBeTheSameWhenInStock() {
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 1);
        double amountSell = Calc.calcTotalCost("default", "COBBLESTONE", -1);
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 2);
        double amountBuy = Calc.calcTotalCost("default", "COBBLESTONE", 1);
        assertEquals(amountSell, amountBuy, 0.01);
    }

}
