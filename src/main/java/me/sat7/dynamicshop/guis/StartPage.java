package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;

import me.sat7.dynamicshop.DynaShopAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;

public class StartPage extends InGameUI {

    public StartPage()
    {
        uiType = UI_TYPE.StartPage;
    }

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

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player)e.getWhoClicked();
        if(player == null)
            return;

        if (e.isLeftClick())
        {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
            {
                // 새 버튼 추가
                if (player.hasPermission("dshop.admin.shopedit"))
                {
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".displayName", "New Button");
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".lore", "new button");
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".icon", Material.SUNFLOWER.name());
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".action", "ds");
                    StartPage.ccStartPage.save();

                    DynaShopAPI.openStartPage(player);
                } else
                {
                    return;
                }
            }

            String actionStr = StartPage.ccStartPage.get().getString("Buttons." + e.getSlot() + ".action");
            if (actionStr != null && actionStr.length() > 0)
            {
                String[] action = actionStr.split(StartPage.ccStartPage.get().getString("Options.LineBreak"));

                //player.closeInventory();

                for (String s : action)
                {
                    Bukkit.dispatchCommand(player, s);
                }
            }
        }
        // 우클릭
        else if (player.hasPermission("dshop.admin.shopedit"))
        {
            // 편집
            if (e.isShiftClick())
            {
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

                DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "startpage/" + e.getSlot()); // 선택한 아이탬의 인덱스 저장
                DynaShopAPI.openStartPageSettingGui(player);
            }
            // 이동
            else
            {
                String itemtoMove = "";
                try
                {
                    String[] temp = DynamicShop.ccUser.get().getString(player.getUniqueId() + ".interactItem").split("/");
                    itemtoMove = temp[1];
                } catch (Exception ignored)
                {
                }

                if (itemtoMove.length() == 0)
                {
                    if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

                    DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "startpage/" + e.getSlot()); // 선택한 아이탬의 인덱스 저장
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_MOVE_SELECTED"));
                } else
                {
                    if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) return;

                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".displayName", StartPage.ccStartPage.get().get("Buttons." + itemtoMove + ".displayName"));
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".lore", StartPage.ccStartPage.get().get("Buttons." + itemtoMove + ".lore"));
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".icon", StartPage.ccStartPage.get().get("Buttons." + itemtoMove + ".icon"));
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".action", StartPage.ccStartPage.get().get("Buttons." + itemtoMove + ".action"));

                    if (StartPage.ccStartPage.get().getString("Buttons." + itemtoMove + ".action").length() > 0)
                    {
                        StartPage.ccStartPage.get().set("Buttons." + itemtoMove, null);
                    }

                    StartPage.ccStartPage.save();

                    DynaShopAPI.openStartPage(player);
                    DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "");
                }
            }
        }
    }
}
