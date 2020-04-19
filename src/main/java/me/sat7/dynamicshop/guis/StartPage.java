package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;

public class StartPage {

    public static CustomConfig ccStartPage;

    public static void setupStartPageFile()
    {
        ccStartPage.setup("Startpage",null);
        ccStartPage.get().options().header("LineBreak: \\, |, bracket is NOT working. Recommended character: /, _, ;, ※");
        ccStartPage.get().addDefault("Options.Title", "§3§lStart Page");
        ccStartPage.get().addDefault("Options.UiSlotCount", 27);
        ccStartPage.get().addDefault("Options.LineBreak","/");

        if(ccStartPage.get().getKeys(false).size() == 0)
        {
            ccStartPage.get().set("Buttons.0.displayName", "§3§lExample Button");
            ccStartPage.get().set("Buttons.0.lore", "§fThis is Example Button/§aClick empty slot to create new button");
            ccStartPage.get().set("Buttons.0.icon","SUNFLOWER");
            ccStartPage.get().set("Buttons.0.action","Dynamicshop Testfunction/Dynamicshop Testfunction");
        }
        ccStartPage.get().options().copyDefaults(true);
        ccStartPage.save();
    }

    public Inventory getGui(Player player) {
        Inventory ui = Bukkit.createInventory(player, ccStartPage.get().getInt("Options.UiSlotCount"), ccStartPage.get().getString("Options.Title"));

        DynamicShop.ccUser.get().set(player.getUniqueId()+".interactItem","");

        //아이콘, 이름, 로어, 인덱스, 커맨드
        ConfigurationSection cs = ccStartPage.get().getConfigurationSection("Buttons");
        for (String s:cs.getKeys(false))
        {
            try {
                int idx = Integer.parseInt(s);

                String name = " ";
                if(cs.contains(s+".displayName"))
                {
                    name=cs.getConfigurationSection(s).getString("displayName");
                }

                ArrayList<String> tempList = new ArrayList<>();
                if(cs.contains(s+".lore"))
                {
                    String[] lore = cs.getConfigurationSection(s).getString("lore").split(ccStartPage.get().getString("Options.LineBreak"));
                    tempList.addAll(Arrays.asList(lore));
                }

                if(player.hasPermission("dshop.admin.shopedit"))
                {
                    if(cs.getString(s+".action").length()>0)
                    {
                        tempList.add(LangUtil.ccLang.get().getString("ITEM_MOVE_LORE"));
                    }
                    else
                    {
                        tempList.add(LangUtil.ccLang.get().getString("ITEM_COPY_LORE"));
                    }
                    tempList.add(LangUtil.ccLang.get().getString("ITEM_EDIT_LORE"));
                }

                ItemStack btn = new ItemStack(Material.getMaterial(cs.getConfigurationSection(s).getString("icon")));
                ItemMeta meta = btn.getItemMeta();
                meta.setDisplayName(name);
                meta.setLore(tempList);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                btn.setItemMeta(meta);
                ui.setItem(idx,btn);

            }catch (Exception e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +"Fail to create Start page button");
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +e);
            }
        }
        return ui;
    }
}
