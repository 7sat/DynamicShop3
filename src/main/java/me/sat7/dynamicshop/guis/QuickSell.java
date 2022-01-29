package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.utilities.ItemsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.sat7.dynamicshop.utilities.LangUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class QuickSell {

    public Inventory getGui(Player player) {
        Inventory inven = Bukkit.createInventory(player,9, LangUtil.ccLang.get().getString("QUICKSELL_TITLE"));

        ItemStack infoBtn = ItemsUtil.createItemStack(Material.RED_STAINED_GLASS_PANE,
                null,
                LangUtil.ccLang.get().getString("QUICKSELL_GUIDE_TITLE"),
                new ArrayList<String>(Arrays.asList(LangUtil.ccLang.get().getString("QUICKSELL_GUIDE_LORE").split("\n"))),
                1);

        for (int i = 0; i < 9; i++)
            inven.setItem(i,infoBtn);

        return inven;
    }
}
