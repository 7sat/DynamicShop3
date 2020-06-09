package me.sat7.dynamicshop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@CommandPermission(Constants.USE_SHOP_PERMISSION)
@CommandAlias("dynamicshop|dshop|ds")
public class CommandDynamicShop extends BaseCommand {
    private final DynamicShop plugin;

    public CommandDynamicShop(DynamicShop ds) {
        this.plugin = ds;
    }

    @Default
    @HelpCommand
    @Description("DynamicShop Help")
    @Syntax("<command>")
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("dshop.admin.reload")
    @Description("Reloads all config files for DynamicShop")
    public void onReload(CommandSender sender) {
        LangUtil.ccLang.reload();
        ShopUtil.ccShop.reload();
        StartPage.ccStartPage.reload();
        DynamicShop.ccSign.reload();
        WorthUtil.ccWorth.reload();
        SoundUtil.ccSound.reload();

        DynamicShop.plugin.reloadConfig();
        ConfigUtil.configSetup(DynamicShop.plugin);

        LangUtil.setupLangFile(DynamicShop.plugin.getConfig().getString("Language"));

        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.RELOADED"));
    }

    @Subcommand("settax")
    @CommandPermission("dshop.admin.settax")
    public class SetTax extends BaseCommand {

        @Default
        @Description("Set the sales tax")
        @CommandCompletion("@range:100")
        @Syntax("<tax>")
        public void onSetTax(CommandSender sender, int tax) {
            int newValue = tax;
            if (newValue <= 2) {
                newValue = 2;
            }
            if (newValue > 99) {
                newValue = 99;
            }

            DynamicShop.plugin.getConfig().set("SalesTax", newValue);
            DynamicShop.plugin.saveConfig();

            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
        }

        @Subcommand("temp")
        @Description("Set the sales tax temporarily")
        @CommandCompletion("@range:100 @minutes")
        @Syntax("<tax> <minutes>")
        public void onTempSetTax(CommandSender sender, int tax, int minutes) {
            int newValue = tax;
            int tempTaxDurationMinutes = minutes;
            if (newValue <= 2) {
                newValue = 2;
            }
            if (newValue > 99) {
                newValue = 99;
            }
            if (tempTaxDurationMinutes <= 1) {
                tempTaxDurationMinutes = 1;
            }

            ConfigUtil.setCurrentTax(newValue);
            Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, ConfigUtil::resetTax, 20L * 60L * tempTaxDurationMinutes);

            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
        }
    }

    @Subcommand("quicksell|qsell")
    @CommandAlias("quicksell|qsell")
    @Description("Open the Quick Sell menu")
    public void onQuickSell(Player player) {
        DynaShopAPI.openQuickSellGUI(player);
    }

    @Subcommand("createshop|create")
    @Description("Create a new shop")
    @Syntax("<shopName> <permission>")
    @CommandCompletion("shopName true|false|my.permission")
    @CommandPermission("dshop.admin.createshop")
    public void onCreateShop(Player player, String shopName, @Optional String permission) {

        if (ShopUtil.ccShop.get().contains(shopName)) {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_EXIST"));
            return;
        }

        ShopUtil.ccShop.get().set(shopName + ".Options.title", shopName);
        ShopUtil.ccShop.get().set(shopName + ".Options.lore", "");
        ShopUtil.ccShop.get().set(shopName + ".Options.page", 2);
        if (permission != null) {
            if (permission.equalsIgnoreCase("true")) {
                ShopUtil.ccShop.get().set(shopName + ".Options.permission", "dshop.user.shop." + shopName);
            } else if (permission.equalsIgnoreCase("false")) {
                ShopUtil.ccShop.get().set(shopName + ".Options.permission", "");
            } else {
                ShopUtil.ccShop.get().set(shopName + ".Options.permission", permission);
            }
        } else {
            ShopUtil.ccShop.get().set(shopName + ".Options.permission", "");
        }

        ShopUtil.ccShop.get().set(shopName + ".0.mat", "DIRT");
        ShopUtil.ccShop.get().set(shopName + ".0.value", 1);
        ShopUtil.ccShop.get().set(shopName + ".0.median", 10000);
        ShopUtil.ccShop.get().set(shopName + ".0.stock", 10000);
        ShopUtil.ccShop.save();
        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("SHOP_CREATED"));
        DynaShopAPI.openShopGui(player, shopName, 1);
    }

    @Subcommand("deleteshop|delete")
    @Description("Delete a shop")
    @Syntax("<shopName>")
    @CommandCompletion("@dsShops")
    @CommandPermission("dshop.admin.deleteshop")
    public void onDeleteShop(CommandSender sender, @Values("@dsShops") String shopName) {
        ShopUtil.ccShop.get().set(shopName, null);
        ShopUtil.ccShop.save();
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("SHOP_DELETED"));
    }

    @Subcommand("renameshop|rename")
    @Description("Rename a shop")
    @Syntax("<oldShopName> <newName>")
    @CommandCompletion("@dsShops newName")
    @CommandPermission("dshop.admin.renameshop")
    public void onRenameShop(CommandSender sender, @Values("@dsShops") String oldName, String newName) {
        ShopUtil.renameShop(oldName, newName);
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newName);
    }

    @Subcommand("mergeshop|merge")
    @Description("Merge two shops")
    @Syntax("<shopName> <shopName>")
    @CommandCompletion("@dsShops @dsShops")
    @CommandPermission("dshop.admin.mergeshop")
    public void onMergeShops(CommandSender sender, @Values("@dsShops") String shopOne, @Values("@dsShops") String shopTwo) {
        ShopUtil.mergeShop(shopOne, shopTwo);
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + shopOne);
    }

    @Subcommand("convertshop")
    @Description("Convert a shop")
    @Syntax("<pluginName>")
    @CommandCompletion("Shop")
    @CommandPermission("dshop.admin.convert")
    public void onConvertShop(Player player, @Values("Shop") String shopName) {
        ShopUtil.convertDataFromShop(player);
    }

    @Subcommand("setdefaultshop|setdefault")
    @Description("Set the default shop")
    @Syntax("<shopName>")
    @CommandCompletion("@dsShops")
    @CommandPermission("dshop.admin.setdefaultshop")
    public void onSetDefaultShop(CommandSender sender, @Values("@dsShops") String shopName) {
        DynamicShop.plugin.getConfig().set("DefaultShopName", shopName);
        DynamicShop.plugin.saveConfig();
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + shopName);
    }

    @Subcommand("deleteOldUser|deleteOldUsers")
    @Description("Delete inactive users")
    @Syntax("<days>")
    @CommandCompletion("@range:360")
    @CommandPermission(Constants.DELETE_USER_PERMISSION)
    public void onDeleteOldUsers(CommandSender sender, int days) {
        int count = 0;
        for (String s : DynamicShop.ccUser.get().getKeys(false)) {
            try {
                long lastJoinLong = DynamicShop.ccUser.get().getLong(s + ".lastJoin");

                long dayPassed = (System.currentTimeMillis() - lastJoinLong) / 86400000L;

                // 마지막으로 접속한지 입력한 일보다 더 지남.
                if (dayPassed > days) {
                    sender.sendMessage(DynamicShop.dsPrefix + Bukkit.getOfflinePlayer(UUID.fromString(s)).getName() + " Deleted");
                    DynamicShop.ccUser.get().set(s, null);
                    count += 1;
                }
            } catch (Exception e) {
                sender.sendMessage(DynamicShop.dsPrefix + e + "/" + s);
            }

            DynamicShop.ccUser.save();
        }

        sender.sendMessage(DynamicShop.dsPrefix + count + " Items Removed");
    }

    @Subcommand("shop")
    @Conditions("creativeCheck")
    public class ShopCommand extends BaseCommand {
        @Description("Open the Shop menu")
        @CommandCompletion("@dsShops")
        @Syntax("<shop>")
        @Default
        @CommandAlias("shop")
        public void onShop(Player player, @Values("@dsShops") @Optional String shop) {
            // user.yml 에 player가 없으면 재생성 시도. 실패시 리턴.
            if (!DynaShopAPI.recreateUserData(player)) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_USER_ID"));
                return;
            }

            String shopName;

            if (shop == null) {
                if (DynamicShop.plugin.getConfig().getBoolean("OpenStartPageInsteadOfDefaultShop")) {
                    DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "");
                    DynaShopAPI.openStartPage(player);
                    return;
                }

                shopName = DynamicShop.plugin.getConfig().getString("DefaultShopName");
            } else {
                shopName = shop;
            }

            if (!ShopUtil.ccShop.get().contains(shopName)) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
                return;
            }

            //권한 확인
            String s = ShopUtil.ccShop.get().getString(shopName + ".Options.permission");
            if (s != null && s.length() > 0) {
                if (!player.hasPermission(s) && !player.hasPermission(s + ".buy") && !player.hasPermission(s + ".sell")) {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
                    return;
                }
            }

            // 플래그 확인
            ConfigurationSection shopConf = ShopUtil.ccShop.get().getConfigurationSection(shopName + ".Options");
            if (shopConf.contains("flag.signshop")) {
                if (!player.hasPermission(Constants.REMOTE_ACCESS_PERMISSION)) {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SIGNSHOP_REMOTE_ACCESS"));
                    return;
                }
            }

            if (shopConf.contains("flag.localshop") && !shopConf.contains("flag.deliverycharge") && shopConf.contains("world") && shopConf.contains("pos1") && shopConf.contains("pos2")) {
                boolean outside = false;
                if (!player.getWorld().getName().equals(shopConf.getString("world"))) {
                    outside = true;
                }

                String[] shopPos1 = shopConf.getString("pos1").split("_");
                String[] shopPos2 = shopConf.getString("pos2").split("_");
                int x1 = Integer.parseInt(shopPos1[0]);
                int y1 = Integer.parseInt(shopPos1[1]);
                int z1 = Integer.parseInt(shopPos1[2]);
                int x2 = Integer.parseInt(shopPos2[0]);
                int y2 = Integer.parseInt(shopPos2[1]);
                int z2 = Integer.parseInt(shopPos2[2]);

                if (!((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2) ||
                        (x2 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x1))) {
                    outside = true;
                }
                if (!((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2) ||
                        (y2 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y1))) {
                    outside = true;
                }
                if (!((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2) ||
                        (z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1))) {
                    outside = true;
                }

                if (outside && !player.hasPermission(Constants.REMOTE_ACCESS_PERMISSION)) {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.LOCALSHOP_REMOTE_ACCESS"));
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("POSITION") + "X" + x1 + " Y" + y1 + " Z" + z1);
                    return;
                }
            }
            if (shopConf.contains("shophours") && !player.hasPermission("dshop.admin.shopedit")) {
                int curTime = (int) (player.getWorld().getTime()) / 1000 + 6;
                if (curTime > 24) {
                    curTime -= 24;
                }

                String[] temp = shopConf.getString("shophours").split("~");

                int open = Integer.parseInt(temp[0]);
                int close = Integer.parseInt(temp[1]);

                if (close > open) {
                    if (!(open <= curTime && curTime < close)) {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return;
                    }
                } else {
                    if (!(open <= curTime || curTime < close)) {
                        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return;
                    }
                }
            }

            DynamicShop.ccUser.get().set(player.getUniqueId() + ".tmpString", "");
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "");
            DynaShopAPI.openShopGui(player, shopName, 1);
        }

        @Description("Edit items in a shop")
        @CommandCompletion("@dsShops @range:100 @range:1000 @dsMin @dsMax @range:1000 @range:1000")
        @Subcommand("edit")
        @Syntax("<shopName> <item> <price> <minPrice> <maxPrice> <medianStock> <stock>")
        @CommandPermission("dshop.admin.shopedit")
        public void onEdit(Player player, @Values("@dsShops") String shopName, int item, double price, double minPrice, double maxPrice, int medianStock, int stock) {
            // 인자 확인
            if (!ShopUtil.ccShop.get().getConfigurationSection(shopName).contains(String.valueOf(item))) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_ITEMNAME"));
                return;
            }

            // 삭제
            if (price <= 0) {
                ShopUtil.removeItemFromShop(shopName, item);
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_DELETED"));
                return;
            }

            if (maxPrice > 0 && minPrice > 0 && minPrice >= maxPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.MAX_LOWER_THAN_MIN"));
                return;
            }
            if (maxPrice > 0 && price > maxPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (minPrice > 0 && price < minPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }

            // 수정
            ShopUtil.editShopItem(shopName, item, price, price, minPrice, maxPrice, medianStock, stock);
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
            ItemsUtil.sendItemInfo(player, shopName, item, "HELP.ITEM_INFO");
        }

        @Description("Add items to a shop")
        @CommandCompletion("@dsShops * @range:1000 @dsMin @dsMax @range:1000 @range:1000")
        @Subcommand("add")
        @Syntax("<shopName> <material> <price> <minPrice> <maxPrice> <medianStock> <stock>")
        @CommandPermission("dshop.admin.shopedit")
        public void onAdd(Player player, @Values("@dsShops") String shopName, Material material, double price, double minPrice, double maxPrice, int medianStock, int stock) {
            // 유효성 검사
            if (maxPrice > 0 && minPrice > 0 && minPrice >= maxPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.MAX_LOWER_THAN_MIN"));
                return;
            }
            if (maxPrice > 0 && price > maxPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (minPrice > 0 && price < minPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }

            if (price < 0.01 || medianStock == 0 || stock == 0) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.VALUE_ZERO"));
                return;
            }

            // 금지품목
            if (Material.AIR.equals(material)) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.ITEM_FORBIDDEN"));
                return;
            }

            // 상점에서 같은 아이탬 찾기
            ItemStack itemStack;
            try {
                itemStack = new ItemStack(material);
            } catch (Exception e) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_ITEMNAME"));
                return;
            }

            int idx = ShopUtil.findItemFromShop(shopName, itemStack);
            // 상점에 새 아이탬 추가
            if (idx == -1) {
                idx = ShopUtil.findEmptyShopSlot(shopName);
                if (idx == -1) {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_EMPTY_SLOT"));
                    return;
                } else if (ShopUtil.addItemToShop(shopName, idx, itemStack, price, price, minPrice, maxPrice, medianStock, stock)) // 아이탬 추가
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_ADDED"));
                    ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
                }
            }
            // 기존 아이탬 수정
            else {
                ShopUtil.editShopItem(shopName, idx, price, price, minPrice, maxPrice, medianStock, stock);
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
                ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
            }
        }

        @Description("Add hand item to a shop")
        @CommandCompletion("@dsShops @range:1000 @dsMin @dsMax @range:1000 @range:1000")
        @Subcommand("addhand")
        @Syntax("<shopName> <price> <minPrice> <maxPrice> <medianStock> <stock>")
        @CommandPermission("dshop.admin.shopedit")
        public void onAddHand(Player player, @Values("@dsShops") String shopName, double price, double minPrice, double maxPrice, int medianStock, int stock) {
            // 유효성 검사
            if (maxPrice > 0 && minPrice > 0 && minPrice >= maxPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.MAX_LOWER_THAN_MIN"));
                return;
            }
            if (maxPrice > 0 && price > maxPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (minPrice > 0 && price < minPrice) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.DEFAULT_VALUE_OUT_OF_RANGE"));
                return;
            }
            if (price < 0.01 || medianStock == 0 || stock == 0) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.VALUE_ZERO"));
                return;
            }

            // 손에 뭔가 들고있는지 확인
            if (player.getInventory().getItemInMainHand() == null || Material.AIR.equals(player.getInventory().getItemInMainHand().getType())) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.HAND_EMPTY"));
                return;
            }

            // 금지품목
            if (Material.AIR.equals(Material.getMaterial(player.getInventory().getItemInMainHand().getType().toString()))) {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.ITEM_FORBIDDEN"));
                return;
            }

            // 상점에서 같은 아이탬 찾기
            int idx = ShopUtil.findItemFromShop(shopName, player.getInventory().getItemInMainHand());
            // 상점에 새 아이탬 추가
            if (idx == -1) {
                idx = ShopUtil.findEmptyShopSlot(shopName);
                if (idx == -1) {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_EMPTY_SLOT"));
                    return;
                } else if (ShopUtil.addItemToShop(shopName, idx, player.getInventory().getItemInMainHand(), price, price, minPrice, maxPrice, medianStock, stock)) // 아이탬 추가
                {
                    player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_ADDED"));
                    ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
                }
            }
            // 기존 아이탬 수정
            else {
                ShopUtil.editShopItem(shopName, idx, price, price, minPrice, maxPrice, medianStock, stock);
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
                ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO");
            }
        }
    }
}

