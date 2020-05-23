package me.sat7.dynamicshop.transactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 1);
        double amountSell = Calc.calcTotalCost("default", "COBBLESTONE", -1);
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 2);
        double amountBuy = Calc.calcTotalCost("default", "COBBLESTONE", 1);
        assertEquals(amountSell, amountBuy, 0.01);
    }

    @Test
    public void calcCostShouldBeTheSameWhenInStock() {
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 3);
        double amountSell = Calc.calcTotalCost("default", "COBBLESTONE", -1);
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 4);
        double amountBuy = Calc.calcTotalCost("default", "COBBLESTONE", 1);
        assertEquals(amountSell, amountBuy, 0.01);
    }

    @Test
    public void calcCostShouldBeTheSameWhenInStockWithTax() {
        ShopUtil.ccShop.get().set("default.Options.SalesTax", 25);
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 3);
        double amountSell = Calc.calcTotalCost("default", "COBBLESTONE", -1);
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", 4);
        double amountBuy = Calc.calcTotalCost("default", "COBBLESTONE", 1);
        assertEquals(amountSell, amountBuy * 0.75, 0.01);
    }

    @Test
    public void staticMedianOrStockShouldCalcPrice() {
        ShopUtil.ccShop.get().set("default.COBBLESTONE.stock", -1);
        ShopUtil.ccShop.get().set("default.COBBLESTONE.median", -1);
        double amountSell = Calc.calcTotalCost("default", "COBBLESTONE", -3);
        assertEquals(30, amountSell, 0.01);
        double amountBuy = Calc.calcTotalCost("default", "COBBLESTONE", 3);
        assertEquals(30, amountBuy, 0.01);
    }

}
