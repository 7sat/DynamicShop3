package me.sat7.dynamicshop.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.sat7.dynamicshop.utilities.LangUtil;

public class QuickSell {

    public Inventory getGui(Player player) {
        return Bukkit.createInventory(player,9, LangUtil.ccLang.get().getString("QUICKSELL_TITLE"));
    }
}
