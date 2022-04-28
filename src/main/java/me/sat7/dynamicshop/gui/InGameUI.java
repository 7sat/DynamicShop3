package me.sat7.dynamicshop.gui;

import lombok.*;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public abstract class InGameUI {
    public enum UI_TYPE {
        ItemPalette,
        ItemSettings,
        ItemTrade,
        QuickSell,
        Shop,
        ShopSettings,
        StartPage,
        StartPageSettings,
        StartPage_ShopList,
        StartPage_ColorList,
    }

    public UI_TYPE uiType;

    public void onClickUpperInventory(InventoryClickEvent e) {}

    public void onClickLowerInventory(InventoryClickEvent e) {}

    public void refreshUI() {}

    protected Inventory inventory;

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack createButton(int slotIndex, Material material, String name, String lore) {
        return createButton(slotIndex, material, name, parseStringLore(lore));
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack createButton(int slotIndex, Material material, String name, @Nullable ArrayList<String> lore) {
        return createButton(slotIndex, material, name, lore, 1);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack createButton(int slotIndex, Material material, String name, @Nullable ArrayList<String> lore, int amount) {
        return createButton(slotIndex, new UIIcon(material, null), name, lore, amount);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack createButton(int slotIndex, UIIcon icon, String name, String lore) {
        return createButton(slotIndex, icon, name, lore, 1);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack createButton(int slotIndex, UIIcon icon, String name, String lore, int amount) {
        if (lore != null && lore.isEmpty()) {
            lore = null;
        }

        if (lore == null) {
            return createButton(slotIndex, icon, name, (ArrayList<String>) null, amount);
        } else {
            return createButton(slotIndex, icon, name, parseStringLore(lore), amount);
        }
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected ItemStack createButton(int slotIndex, UIIcon icon, String name, @Nullable ArrayList<String> lore, int amount) {
        ItemStack itemStack = ItemsUtil.createItemStack(icon.getMaterial(), null, name, lore, amount, icon.getCustomModelData());
        inventory.setItem(slotIndex, itemStack);
        return itemStack;
    }

    private static ArrayList<String> parseStringLore(String lore) {
        if (lore.contains("\n")) {
            return new ArrayList<>(Arrays.asList(lore.split("\n")));
        } else {
            return new ArrayList<>(Collections.singletonList(lore));
        }
    }

    protected void createCloseButton(Player player, int slotIndex) {
        createButton(slotIndex, UIIcons.CLOSE.get(), t(player, "CLOSE"), t(player, "CLOSE_LORE"));
    }

    public static UIIcon getPageButtonIcon() {
        return UIIcons.PAGE.get();
    }

    public static UIIcon getShopInfoButtonIcon() {
        return UIIcons.SHOP_INFO.get();
    }

    @Data
    static class UIIcon {
        final Material material;
        final Integer customModelData;
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    protected enum UIIcons {
        PAGE("CloseButton", Material.BARRIER),
        CLOSE("PageButton", Material.PAPER),
        SHOP_INFO("ShopInfoButton", Material.OAK_SIGN);

        @Getter
        final String configPath;
        @Getter
        final Material material;

        String configSection = "UI.Icons";

        public UIIcon get() {
            return readIcon(getConfigSection(), this.getMaterial());
        }

        private String getConfigSection() {
            String configPath = this.configSection + "." + this.getConfigPath();

            // legacy support start
            if (DynamicShop.plugin.getConfig().getConfigurationSection(this.configSection) == null) {
                configPath = "UI." + this.getConfigPath() + "Icon";
            }
            // legacy support end
            return configPath;
        }

        private static UIIcon readIcon(String configPath, Material defaultMaterial) {
            String[] args = DynamicShop.plugin.getConfig().getString(configPath).split(":");
            Material material = Material.getMaterial(args[0].toUpperCase().replace("-", "_").replace(" ", "_"));
            if (material == null) {
                material = defaultMaterial;
                DynamicShop.plugin.getConfig().set(configPath, defaultMaterial);
                DynamicShop.plugin.saveConfig();
            }
            if (args.length > 1) {
                return new UIIcon(material, Integer.parseInt(args[1]));
            } else {
                return new UIIcon(material, null);
            }
        }
    }
}
