package me.sat7.dynamicshop.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public final class FileUtil {
    private FileUtil() {

    }

    public static CustomConfig generateOutOfStockCustomConfig() {
        FileConfiguration config = new YamlConfiguration();

        config.set("default.COBBLESTONE.median", 10);
        config.set("default.COBBLESTONE.stock", 1);
//        config.set("default.COBBLESTONE.value2", 50.0);
        config.set("default.COBBLESTONE.value", 10);
        config.set("default.COBBLESTONE.valueMin", 0.0);
        config.set("default.COBBLESTONE.valueMax", 10000000000.0);
        config.set("default.Options.SalesTax", 0.0);

        CustomConfig customConfig = new CustomConfig();
        customConfig.customFile = config;
        return customConfig;
    }

    public static CustomConfig generate64StockCustomConfig() {
        FileConfiguration config = new YamlConfiguration();

        config.set("default.COBBLESTONE.median", 10000);
        config.set("default.COBBLESTONE.stock", 64);
//        config.set("default.COBBLESTONE.value2", 50.0);
        config.set("default.COBBLESTONE.value", 10000);
        config.set("default.COBBLESTONE.valueMin", 0.0);
        config.set("default.COBBLESTONE.valueMax", 100000.0);
        config.set("default.Options.SalesTax", 0.0);

        CustomConfig customConfig = new CustomConfig();
        customConfig.customFile = config;
        return customConfig;
    }

    public static CustomConfig setCustomConfig(FileConfiguration config) {
        CustomConfig customConfig = new CustomConfig();
        customConfig.customFile = config;
        return customConfig;
    }
}
