package me.sat7.dynamicshop.commands;

import co.aikar.commands.PaperCommandManager;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TabCompletions {
    private final DynamicShop plugin;

    public TabCompletions(DynamicShop instance) {
        plugin = instance;
    }

    public void register() {
        PaperCommandManager mgr = plugin.getCommandManager();

        mgr.getCommandCompletions().registerAsyncCompletion("minutes", c -> Arrays.asList(
                "10", "15", "30", "45", "60", "90", "120", "240",
                "300", "360", "420", "480"));

        mgr.getCommandCompletions().registerAsyncCompletion("dsShops", c -> new ArrayList<>(ShopUtil.ccShop.get().getKeys(false)));
    }
}