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
    public void calcCostWithStock1ShouldNotBeMoreThanStockNegative64() {
        double amount = Calc.calcTotalCost("default",
                "COBBLESTONE", -64);
//        assertEquals(10673.75, amount, 0.01);
        assertEquals(640000.0, amount, 0.01);
        ShopUtil.ccShop = FileUtil.generate64StockCustomConfig();
        amount = Calc.calcTotalCost("default", "COBBLESTONE", 64);
        assertEquals(6400000.0, amount, 0.01);
//        assertEquals(9389.18, amount, 0.01);
    }

}
