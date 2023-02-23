package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
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

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class StartPage extends InGameUI
{
    public StartPage()
    {
        uiType = UI_TYPE.StartPage;
    }

    public static CustomConfig ccStartPage;

    public static void setupStartPageFile()
    {
        ccStartPage.setup("Startpage", null);
        ccStartPage.get().options().header("LineBreak: Do not use \\, | and brackets. Recommended : /, _");
        ccStartPage.get().addDefault("Options.Title", "§3§lStart Page");
        ccStartPage.get().addDefault("Options.UiSlotCount", 27);
        ccStartPage.get().addDefault("Options.LineBreak", "/");

        if (ccStartPage.get().getKeys(false).size() == 0)
        {
            ccStartPage.get().set("Buttons.0.displayName", "§3§lExample Button");
            ccStartPage.get().set("Buttons.0.lore", "§fThis is Example Button/§aClick empty slot to create new button");
            ccStartPage.get().set("Buttons.0.icon", "SUNFLOWER");
            ccStartPage.get().set("Buttons.0.action", "");
        }
        ccStartPage.get().options().copyDefaults(true);
        ccStartPage.save();
    }

    private int selectedIndex = -1;

    public Inventory getGui(Player player)
    {
        selectedIndex = -1;

        inventory = Bukkit.createInventory(player, ccStartPage.get().getInt("Options.UiSlotCount"), ccStartPage.get().getString("Options.Title"));

        //아이콘, 이름, 로어, 인덱스, 커맨드
        ConfigurationSection cs = ccStartPage.get().getConfigurationSection("Buttons");
        for (String s : cs.getKeys(false))
        {
            try
            {
                int idx = Integer.parseInt(s);

                String name = " ";
                if (cs.contains(s + ".displayName"))
                {
                    name = cs.getConfigurationSection(s).getString("displayName");
                }

                ArrayList<String> tempList = new ArrayList<>();
                if (cs.contains(s + ".lore"))
                {
                    String[] lore = cs.getConfigurationSection(s).getString("lore").split(ccStartPage.get().getString("Options.LineBreak"));
                    tempList.addAll(Arrays.asList(lore));
                }

                if (player.hasPermission(P_ADMIN_SHOP_EDIT))
                {
                    String cmd = cs.getString(s + ".action");
                    if (cmd != null && cmd.length() > 0)
                    {
                        tempList.add(t(player, "START_PAGE.ITEM_MOVE_LORE"));
                    } else
                    {
                        tempList.add(t(player, "START_PAGE.ITEM_REMOVE_LORE"));
                        tempList.add(t(player, "START_PAGE.ITEM_COPY_LORE"));
                    }
                    tempList.add(t(player, "START_PAGE.ITEM_EDIT_LORE"));
                }

                ItemStack btn = new ItemStack(Material.getMaterial(cs.getConfigurationSection(s).getString("icon")));
                ItemMeta meta = null;

                if (cs.contains(s + ".itemStack"))
                {
                    ItemMeta tempMeta = (ItemMeta) cs.get(s + ".itemStack"); // 저장된 메타 적용
                    meta = tempMeta.clone();
                }
                else
                {
                    meta = btn.getItemMeta();
                }

                meta.setDisplayName(name);
                meta.setLore(tempList);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                btn.setItemMeta(meta);
                inventory.setItem(idx, btn);

            } catch (Exception e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Fail to create Start page button");
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + e);
            }
        }
        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        if (e.isLeftClick())
        {
            if(e.isShiftClick() && player.hasPermission(P_ADMIN_SHOP_EDIT))
            {
                if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR)
                {
                    String actionString = StartPage.ccStartPage.get().getString("Buttons." + e.getSlot() + ".action");
                    if(actionString == null || actionString.isEmpty())
                    {
                        StartPage.ccStartPage.get().set("Buttons." + e.getSlot(), null);
                        StartPage.ccStartPage.save();
                        DynaShopAPI.openStartPage(player);
                    }
                }
            }
            else
            {
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                {
                    // 새 버튼 추가
                    if (player.hasPermission(P_ADMIN_SHOP_EDIT))
                    {
                        StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".displayName", "§3New Button");
                        StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".lore", "§fnew button");
                        StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".icon", Material.SUNFLOWER.name());
                        StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".action", "");
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

                    for (String s : action)
                    {
                        Bukkit.dispatchCommand(player, s);
                    }
                }
            }
        }
        // 우클릭
        else if (player.hasPermission(P_ADMIN_SHOP_EDIT))
        {
            // 편집
            if (e.isShiftClick())
            {
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

                selectedIndex = e.getSlot();
                DynaShopAPI.openStartPageSettingGui(player, selectedIndex);
            }
            // 이동
            else
            {
                if (selectedIndex == -1)
                {
                    if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

                    selectedIndex = e.getSlot();
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "SHOP.ITEM_MOVE_SELECTED"));
                } else
                {
                    if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) return;

                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".displayName", StartPage.ccStartPage.get().get("Buttons." + selectedIndex + ".displayName"));
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".lore", StartPage.ccStartPage.get().get("Buttons." + selectedIndex + ".lore"));
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".icon", StartPage.ccStartPage.get().get("Buttons." + selectedIndex + ".icon"));
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".itemStack", StartPage.ccStartPage.get().get("Buttons." + selectedIndex + ".itemStack"));
                    StartPage.ccStartPage.get().set("Buttons." + e.getSlot() + ".action", StartPage.ccStartPage.get().get("Buttons." + selectedIndex + ".action"));

                    if (StartPage.ccStartPage.get().getString("Buttons." + selectedIndex + ".action").length() > 0)
                    {
                        StartPage.ccStartPage.get().set("Buttons." + selectedIndex, null);
                    }

                    StartPage.ccStartPage.save();

                    DynaShopAPI.openStartPage(player);
                }
            }
        }
    }

    @Override
    public void OnClickLowerInventory(InventoryClickEvent e)
    {
        if(!DynamicShop.plugin.getConfig().getBoolean("UI.EnableInventoryClickSearch.StartPage"))
            return;

        Player player = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();

        if(itemStack == null || itemStack.getType().isAir())
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CLICK_YOUR_ITEM_START_PAGE"));
            return;
        }

        if(!e.isLeftClick() && !e.isRightClick())
            return;

        boolean isSell = e.isRightClick();

        String[] ret;

        if(isSell)
        {
            ret = ShopUtil.FindTheBestShopToSell(player, e.getCurrentItem());
        }
        else
        {
            ret = ShopUtil.FindTheBestShopToBuy(player, e.getCurrentItem());
        }

        if(ret[1].equals("-2"))
        {
            return;
        }

        if(ret[0].isEmpty())
            return;

        DynaShopAPI.openShopGui(player, ret[0], Integer.parseInt(ret[1]) / 45 + 1);

        boolean useLocalizedName = DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName");
        String message;
        if(isSell)
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.MOVE_TO_BEST_SHOP_SELL", !useLocalizedName);
        }
        else
        {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.MOVE_TO_BEST_SHOP_BUY", !useLocalizedName);
        }

        if (useLocalizedName)
        {
            message = message.replace("{item}", "<item>");
            LangUtil.sendMessageWithLocalizedItemName(player, message, e.getCurrentItem().getType());
        }
        else
        {
            String itemName = ItemsUtil.getBeautifiedName(e.getCurrentItem().getType());
            player.sendMessage(message.replace("{item}", itemName));
        }
    }
}
