package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;

public class ItemPalette {

    private static ArrayList<Material> sortedMat = new ArrayList<>();

    // 파렛트 정렬용
    public static Comparator<Material> sortMat = (m1, m2) -> getMatName(m1).compareTo(
            getMatName(m2));

    private static String getMatName(Material m2) {
        String name2 = m2.name();
        if(name2.startsWith("LIGHT_")) name2 = name2.substring(6);
        if(name2.startsWith("DARK_")) name2 = name2.substring(5);
        int idx2 = name2.indexOf('_');
        if(idx2 != -1)
        {
            name2 = name2.substring(idx2);
        }
        return name2;
    }

    public Inventory getGui(Player player, int page, String search) {
        Inventory inven = Bukkit.createInventory(player,54, LangUtil.ccLang.get().getString("PALETTE_TITLE"));
        ArrayList<Material> paletteList = new ArrayList<>();

        if(search.length()>0)
        {
            Material[] allMat = Material.values();
            for (Material m: allMat)
            {
            	if(m.name().contains(search.toUpperCase())) paletteList.add(m);
            }
        }
        else
        {
            if(sortedMat.isEmpty())
            {
                Material[] allMat = Material.values();
                ArrayList<Material> allMatList = new ArrayList<>();
                for (Material m: allMat)
                {
                    if(m.isItem()) allMatList.add(m);
                }
                //------------------------------------------
                allMatList.sort(sortMat);

                for (Material m: allMatList)
                {
                    if(m.isEdible()) sortedMat.add(m);
                }
                for (Material m: allMatList)
                {
                    if(m.name().contains("SPAWN_EGG")) sortedMat.add(m);
                }
                for (Material m: allMatList)
                {
                    if(!sortedMat.contains(m)) sortedMat.add(m);
                }
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Sorting Items...This should run only once.");
            }

            paletteList = sortedMat;
        }

        // 닫기 버튼
        ItemStack closeBtn = ItemsUtil.createItemStack(Material.BARRIER,null,
                LangUtil.ccLang.get().getString("CLOSE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("CLOSE_LORE"))),1);

        inven.setItem(45,closeBtn);

        // 페이지 버튼
        ItemStack pageBtn = ItemsUtil.createItemStack(Material.PAPER,null,
                page + LangUtil.ccLang.get().getString("PAGE"),
                new ArrayList<>(Collections.singletonList(LangUtil.ccLang.get().getString("PAGE_LORE"))),page);

        inven.setItem(49,pageBtn);

        // 모두추가 버튼
        ItemStack addAllBtn = ItemsUtil.createItemStack(Material.YELLOW_STAINED_GLASS_PANE,null,
                LangUtil.ccLang.get().getString("ADDALL"),
                new ArrayList<>(Collections.singletonList(search)),1);

        inven.setItem(51,addAllBtn);

        // 검색 버튼
        ItemStack searchBtn = ItemsUtil.createItemStack(Material.COMPASS,null,
                LangUtil.ccLang.get().getString("SEARCH"),
                new ArrayList<>(Collections.singletonList(search)),1);

        inven.setItem(53,searchBtn);

        //45개씩 끊어서 표시.
        for (int i = 0; i<45; i++)
        {
            try {
                int idx = i + ((page-1) * 45);
                if(idx>=paletteList.size()) break;

                ItemStack btn = new ItemStack(paletteList.get(idx),1);

                ItemMeta btnMeta = btn.getItemMeta();
                ArrayList<String> btnlore = new ArrayList<>();
                btnlore.add(LangUtil.ccLang.get().getString("PALETTE_LORE"));
                btnlore.add(LangUtil.ccLang.get().getString("DECO_CREATE_LORE"));
                btnMeta.setLore(btnlore);
                btn.setItemMeta(btnMeta);

                inven.setItem(i,btn);
            }
            catch (Exception ignored){ }
        }
        return inven;
    }
}
