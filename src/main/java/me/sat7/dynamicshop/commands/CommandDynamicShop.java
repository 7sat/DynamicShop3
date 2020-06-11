package me.sat7.dynamicshop.commands;

import co.aikar.commands.*;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@CommandPermission(Constants.USE_SHOP_PERMISSION)
@CommandAlias("dynamicshop|dshop|ds")
public class CommandDynamicShop extends BaseCommand {
    private static BukkitRunnable resetTaxTask = null;

    public CommandDynamicShop() {
    }

    @Default
    @HelpCommand
    @Description("%HELP.HELP")
    @Syntax("<command>")
    public void onHelp(CommandSender sender, CommandHelp help) {
        sender.sendMessage(LangUtil.ccLang.get().getString("HELP.TITLE"));
        help.showHelp();
    }

    @Override
    public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
        super.showSyntax(issuer, cmd);
        ArrayList<String> subText = new ArrayList<>();
        String command = cmd.getCommand().replace(" ", "_").toUpperCase();
        if (LangUtil.ccLang.get().isConfigurationSection("HELP." + command)) {
            LangUtil.ccLang.get().getConfigurationSection("HELP." + command).getKeys(false).forEach(key -> {
                subText.add(
                        "  §d- §f" +
                                LangUtil.ccLang.get().getString("HELP." + command + "." + key, "MISSING_STRING")
                                        .replace("{IRREVERSIBLE}", LangUtil.ccLang.get().getString("IRREVERSIBLE"))
                                        .replace("{HELP.PRICE}", LangUtil.ccLang.get().getString("HELP.PRICE"))
                                        .replace("{HELP.INF_STATIC}", LangUtil.ccLang.get().getString("HELP.INF_STATIC"))
                );
            });
        }
        subText.forEach(issuer::sendMessage);
    }

    @Private
    @Subcommand("testfunction")
    @CommandPermission(Constants.SHOP_EDIT_PERMISSION)
    public void onTestFunction(Player player) {
        player.sendMessage(DynamicShop.dsPrefix+" Button clicked!");
    }

    @Subcommand("reload")
    @CommandPermission(Constants.DYNAMIC_SHOP_RELOAD_PERMISSION)
    @Description("%HELP.RELOAD")
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

        // Reload the tab completions in case the shops list changed
        DynamicShop.plugin.getCommandHelper().register();

        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.RELOADED"));
    }

    @Subcommand("settax")
    @CommandPermission(Constants.SET_TAX_PERMISSION)
    public class SetTax extends BaseCommand {

        @Default
        @Description("%HELP.SETTAX")
        @CommandCompletion("@range:100")
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
        @Description("%HELP.SETTAX_TEMP")
        @CommandCompletion("@range:100 @minutes")
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

            class ResetTaxTask extends BukkitRunnable {
                @Override
                public void run() {
                    ConfigUtil.resetTax();
                }
            }
            if (resetTaxTask != null) {
                resetTaxTask.cancel();
                resetTaxTask = null;
            }
            resetTaxTask = new ResetTaxTask();
            resetTaxTask.runTaskLater(DynamicShop.plugin, 20L * 60L * tempTaxDurationMinutes);

            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
        }
    }

    @Subcommand("quicksell|qsell")
    @CommandAlias("quicksell|qsell")
    @Description("%HELP.QSELL")
    public void onQuickSell(Player player) {
        DynaShopAPI.openQuickSellGUI(player);
    }

    @Subcommand("createshop|create")
    @Description("%HELP.CREATESHOP")
    @CommandCompletion("shopName true|false|my.permission")
    @CommandPermission(Constants.CREATE_SHOP_PERMISSION)
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
    @Description("%HELP.DELETESHOP")
    @CommandCompletion("@dsShops")
    @CommandPermission(Constants.DELETE_SHOP_PERMISSION)
    public void onDeleteShop(CommandSender sender, @Values("@dsShops") String shopName) {
        ShopUtil.ccShop.get().set(shopName, null);
        ShopUtil.ccShop.save();
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("SHOP_DELETED"));
    }

    @Subcommand("renameshop|rename")
    @Description("%HELP.RENAME_SHOP")
    @CommandCompletion("@dsShops newName")
    @CommandPermission(Constants.RENAME_SHOP_PERMISSION)
    public void onRenameShop(CommandSender sender, @Values("@dsShops") String oldName, String newName) {
        ShopUtil.renameShop(oldName, newName);
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newName);
    }

    @Subcommand("mergeshop|merge")
    @Description("%HELP.MERGE_SHOP")
    @CommandCompletion("@dsShops @dsShops")
    @CommandPermission(Constants.MERGE_SHOP_PERMISSION)
    public void onMergeShops(CommandSender sender, @Values("@dsShops") String shopOne, @Values("@dsShops") String shopTwo) {
        ShopUtil.mergeShop(shopOne, shopTwo);
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + shopOne);
    }

    @Subcommand("convertshop")
    @Description("%HELP.CONVERT")
    @CommandCompletion("Shop")
    @CommandPermission(Constants.CONVERT_SHOP_PERMISSION)
    public void onConvertShop(Player player, @Values("Shop") String pluginName) {
        ShopUtil.convertDataFromShop(player);
    }

    @Subcommand("setdefaultshop|setdefault")
    @Description("%HELP.SET_DEFAULT_SHOP")
    @CommandCompletion("@dsShops")
    @CommandPermission(Constants.SET_DEFAULT_SHOP_PERMISSION)
    public void onSetDefaultShop(CommandSender sender, @Values("@dsShops") String shopName) {
        DynamicShop.plugin.getConfig().set("DefaultShopName", shopName);
        DynamicShop.plugin.saveConfig();
        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + shopName);
    }

    @Subcommand("deleteOldUser|deleteOldUsers")
    @Description("%HELP.DELETE_OLD_USER")
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
    @Description("%HELP.SHOP_DESCRIPTION")
    @CommandCompletion("@dsShops")
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
        if (shopConf.contains("shophours") && !player.hasPermission(Constants.SHOP_EDIT_PERMISSION)) {
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

    @Subcommand("shopedit")
    @CommandPermission(Constants.SHOP_EDIT_PERMISSION)
    public class ShopEditCommand extends BaseCommand {
        @Description("%HELP.SHOPEDIT")
        @CommandCompletion("@dsShops @range:100 @range:1000 @dsMin @dsMax @range:1000 @range:1000")
        @Subcommand("edit")
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

        @Description("%HELP.SHOPADDITEM")
        @CommandCompletion("@dsShops * @range:1000 @dsMin @dsMax @range:1000 @range:1000")
        @Subcommand("add")
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

        @Description("%HELP.SHOPADDHAND")
        @CommandCompletion("@dsShops @range:1000 @dsMin @dsMax @range:1000 @range:1000")
        @Subcommand("addhand")
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

        @Description("%HELP.EDITALL")
        @CommandCompletion("@dsShops stock|median|value|valueMin|valueMax @dsoperators @range:1000")
        @Subcommand("editall")
        @CommandPermission("dshop.admin.editall")
        public void onEditAll(CommandSender sender, @Values("@dsShops") String shopName, @Values("stock|median|value|valueMin|valueMax") String dataType, @Values("@dsoperators") String operator, double value) {
            // 수정
            for (String s : ShopUtil.ccShop.get().getConfigurationSection(shopName).getKeys(false)) {
                try {
                    int i = Integer.parseInt(s);
                    if (!ShopUtil.ccShop.get().contains(shopName + "." + s + ".value")) {
                        continue; //장식용임
                    }
                } catch (Exception e) {
                    continue;
                }

                final double temp = ShopUtil.ccShop.get().getDouble(shopName + "." + s + "." + dataType);

                if (operator.equalsIgnoreCase("=")) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + "." + dataType, value);
                } else if (operator.equalsIgnoreCase("+")) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + "." + dataType, temp + value);
                } else if (operator.equalsIgnoreCase("-")) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + "." + dataType, temp - value);
                } else if (operator.equalsIgnoreCase("/")) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + "." + dataType, temp / value);
                } else if (operator.equalsIgnoreCase("*")) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + "." + dataType, temp * value);
                }

                if (ShopUtil.ccShop.get().getDouble(shopName + "." + s + ".valueMin") < 0) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + ".valueMin", null);
                }
                if (ShopUtil.ccShop.get().getDouble(shopName + "." + s + ".valueMax") < 0) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + ".valueMax", null);
                }
            }
            ShopUtil.ccShop.save();
            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ITEM_UPDATED"));
        }

        @Description("%HELP.SHOP_PERMISSION")
        @CommandCompletion("@dsShops true|false|my.permission")
        @Subcommand("permission")
        public void onPermission(CommandSender sender, @Values("@dsShops") String shopName, @Optional String newPermission) {
            if (newPermission == null) {
                String s = ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options").getString("permission");
                if (s == null || s.length() == 0) {
                    s = LangUtil.ccLang.get().getString("NULL(OPEN)");
                }
                sender.sendMessage(DynamicShop.dsPrefix + s);
            } else {
                if (newPermission.equalsIgnoreCase("true")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.permission", "dshop.user.shop." + shopName);
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "dshop.user.shop." + shopName);
                } else if (newPermission.equalsIgnoreCase("false")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.permission", "");
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + LangUtil.ccLang.get().getString("NULL(OPEN)"));
                } else {
                    ShopUtil.ccShop.get().set(shopName + ".Options.permission", newPermission);
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + newPermission);
                }
                ShopUtil.ccShop.save();
            }
        }

        @Description("%HELP.MAXPAGES")
        @CommandCompletion("@dsShops @range:20")
        @Subcommand("maxpages")
        public void onMaxPages(CommandSender sender, @Values("@dsShops") String shopName, int newValue) {
            if (newValue <= 0) {
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.VALUE_ZERO"));
            } else {
                ShopUtil.ccShop.get().set(shopName + ".Options.page", newValue);
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + newValue);
                ShopUtil.ccShop.save();
            }
        }

        @Description("%HELP.SETFLAG")
        @CommandCompletion("@dsShops signshop|localshop|deliverycharge|jobpoint set|unset")
        @Subcommand("flag")
        @Syntax("<shopName> <flag> <set|unset>")
        public void onFlag(CommandSender sender, @Values("@dsShops") String shopName, @Values("signshop|localshop|deliverycharge|jobpoint") String flag, @Values("set|unset") String setunset) {
            boolean set = setunset.equals("set");
            if (set) {
                if (flag.equalsIgnoreCase("signshop")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.flag.localshop", null);
                    ShopUtil.ccShop.get().set(shopName + ".Options.flag.deliverycharge", null);
                }
                if (flag.equalsIgnoreCase("localshop")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.flag.signshop", null);
                }
                if (flag.equalsIgnoreCase("deliverycharge")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.flag.localshop", "");
                    ShopUtil.ccShop.get().set(shopName + ".Options.flag.signshop", null);
                }

                ShopUtil.ccShop.get().set(shopName + ".Options.flag." + flag.toLowerCase(), "");
            } else {
                if (flag.equalsIgnoreCase("localshop")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.flag.deliverycharge", null);
                }
                ShopUtil.ccShop.get().set(shopName + ".Options.flag." + flag.toLowerCase(), null);
            }
            ShopUtil.ccShop.save();
            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + flag + ":" + setunset);
        }

        @Description("%HELP.SET_POSITION")
        @CommandCompletion("@dsShops pos1|pos2|clear")
        @Subcommand("position")
        @Syntax("<shopName> <pos1|pos2|clear>")
        public void onPosition(Player player, @Values("@dsShops") String shopName, @Values("pos1|pos2|clear") String value) {
            if (value.equalsIgnoreCase("pos1")) {
                ShopUtil.ccShop.get().set(shopName + ".Options.world", player.getWorld().getName());
                ShopUtil.ccShop.get().set(shopName + ".Options.pos1", player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
                ShopUtil.ccShop.save();
                player.sendMessage(DynamicShop.dsPrefix + "p1");
            } else if (value.equalsIgnoreCase("pos2")) {
                ShopUtil.ccShop.get().set(shopName + ".Options.world", player.getWorld().getName());
                ShopUtil.ccShop.get().set(shopName + ".Options.pos2", player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY() + "_" + player.getLocation().getBlockZ());
                ShopUtil.ccShop.save();
                player.sendMessage(DynamicShop.dsPrefix + "p2");
            } else if (value.equalsIgnoreCase("clear")) {
                ShopUtil.ccShop.get().set(shopName + ".Options.world", null);
                ShopUtil.ccShop.get().set(shopName + ".Options.pos1", null);
                ShopUtil.ccShop.get().set(shopName + ".Options.pos2", null);
                ShopUtil.ccShop.save();
                player.sendMessage(DynamicShop.dsPrefix + "clear");
            }
        }

        @Description("%TIME.SET_SHOPHOURS")
        @CommandCompletion("@dsShops @range:24 @range:24")
        @Subcommand("shophours")
        public void onShopHours(CommandSender sender, @Values("@dsShops") String shopName, @Values("@range:24") int open, @Values("@range:24") int close) {
            if (open > 24) {
                open = 24;
            } else if (open < 1) {
                open = 1;
            }
            if (close > 24) {
                close = 24;
            } else if (close < 1) {
                close = 1;
            }

            if (open == close) {
                ShopUtil.ccShop.get().set(shopName + ".Options.shophours", null);
                ShopUtil.ccShop.save();

                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Open 24 hours");
            } else {
                ShopUtil.ccShop.get().set(shopName + ".Options.shophours", open + "~" + close);
                ShopUtil.ccShop.save();

                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + open + "~" + close);
            }
        }

        @Description("%HELP.FLUCTUATION")
        @CommandCompletion("@dsShops 30m|1h|2h|4h|off @range:50")
        @Subcommand("fluctuation")
        public void onFluctuation(CommandSender sender, @Values("@dsShops") String shopName, @Values("30m|1h|2h|4h|off") String interval, @Optional Integer strength) {
            if (strength == null) {
                if (interval.equals("off")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.fluctuation", null);
                    ShopUtil.ccShop.save();
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Fluctuation Off");
                } else {
                    throw new InvalidCommandArgument(true);
                }
            } else {
                ShopUtil.ccShop.get().set(shopName + ".Options.fluctuation.interval", interval);
                ShopUtil.ccShop.get().set(shopName + ".Options.fluctuation.strength", strength);
                ShopUtil.ccShop.save();

                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
            }
        }

        @Description("%HELP.STABILIZATION")
        @CommandCompletion("@dsShops")
        @Subcommand("stockstabilizing")
        public void onStockStabilizing(CommandSender sender, @Values("@dsShops") String shopName, @Values("30m|1h|2h|4h|off") String interval, @Optional Double strength) {
            if (strength == null) {
                if (interval.equals("off")) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.stockStabilizing", null);
                    ShopUtil.ccShop.save();
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "stockStabilizing Off");
                } else {
                    throw new InvalidCommandArgument(true);
                }
            } else {
                ShopUtil.ccShop.get().set(shopName + ".Options.stockStabilizing.interval", interval);
                ShopUtil.ccShop.get().set(shopName + ".Options.stockStabilizing.strength", strength);
                ShopUtil.ccShop.save();

                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "Interval " + interval + ", strength " + strength);
            }
        }

        @Subcommand("account")
        public class AccountCommand extends BaseCommand {
            @Description("%HELP.ACCOUNT")
            @CommandCompletion("@dsShops @range:1000")
            @Subcommand("set")
            public void onSet(CommandSender sender, @Values("@dsShops") String shopName, double balance) {
                if (balance < 0) {
                    ShopUtil.ccShop.get().set(shopName + ".Options.Balance", null);
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + LangUtil.ccLang.get().getString("SHOP_BAL_INF"));
                } else {
                    ShopUtil.ccShop.get().set(shopName + ".Options.Balance", balance);
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + balance);
                }
                ShopUtil.ccShop.save();
            }

            @Description("%HELP.ACCOUNT_LINK")
            @CommandCompletion("@dsShops @dsShops")
            @Subcommand("linkto")
            @Syntax("<shopName> <shopName>")
            public void onLinkTo(CommandSender sender, @Values("@dsShops") String shopName, @Values("@dsShops") String shopName2) {
                // 타깃 상점이 연동계좌임
                if (ShopUtil.ccShop.get().contains(shopName2 + ".Options.Balance")) {
                    try {
                        Double temp = Double.parseDouble(ShopUtil.ccShop.get().getString(shopName2 + ".Options.Balance"));
                    } catch (Exception e) {
                        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_LINK_TARGET_ERR"));
                        return;
                    }
                }

                // 출발상점을 타깃으로 하는 상점이 있음
                for (String s : ShopUtil.ccShop.get().getKeys(false)) {
                    String temp = ShopUtil.ccShop.get().getString(s + ".Options.Balance");
                    try {
                        if (temp != null && temp.equals(shopName)) {
                            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NESTED_STRUCTURE"));
                            return;
                        }
                    } catch (Exception e) {
                        DynamicShop.console.sendMessage(DynamicShop.dsPrefix + e);
                    }
                }

                // 출발 상점과 도착 상점이 같음
                if (shopName.equals(shopName2)) {
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    return;
                }

                // 출발 상점과 도착 상점의 통화 유형이 다름
                if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint") != ShopUtil.ccShop.get().contains(shopName2 + ".Options.flag.jobpoint")) {
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_DIFF_CURRENCY"));
                    return;
                }

                ShopUtil.ccShop.get().set(shopName + ".Options.Balance", shopName2);
                ShopUtil.ccShop.save();
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + shopName2);
            }

            @Description("%HELP.ACCOUNT_TRANSFER")
            @CommandCompletion("@dsShops @shopsAndPlayers @range:1000")
            @Subcommand("transfer")
            public void onTransfer(CommandSender sender, @Values("@dsShops") String shopName, @Values("@shopsAndPlayers") String target, double amount) {
                // 출발 상점이 무한계좌임
                if (!ShopUtil.ccShop.get().contains(shopName + ".Options.Balance")) {
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_HAS_INF_BAL").replace("{shop}", shopName));
                    return;
                }

                // 출발 상점에 돈이 부족
                if (ShopUtil.getShopBalance(shopName) < amount) {
                    if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint")) {
                        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("NOT_ENOUGH_POINT").
                                replace("{bal}", DynaShopAPI.df.format(ShopUtil.getShopBalance(shopName))));
                    } else {
                        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("NOT_ENOUGH_MONEY").
                                replace("{bal}", DynaShopAPI.df.format(ShopUtil.getShopBalance(shopName))));
                    }
                    return;
                }

                // 다른 상점으로 송금
                if (ShopUtil.ccShop.get().contains(target)) {
                    // 도착 상점이 무한계좌임
                    if (!ShopUtil.ccShop.get().contains(target + ".Options.Balance")) {
                        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_HAS_INF_BAL").replace("{shop}", target));
                        return;
                    }

                    // 출발 상점과 도착 상점이 같음
                    if (shopName.equals(target)) {
                        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                        return;
                    }

                    // 출발 상점과 도착 상점의 통화 유형이 다름
                    if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint") != ShopUtil.ccShop.get().contains(target + ".Options.flag.jobpoint")) {
                        sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_DIFF_CURRENCY"));
                        return;
                    }

                    // 송금.
                    ShopUtil.addShopBalance(shopName, amount * -1);
                    ShopUtil.addShopBalance(target, amount);
                    ShopUtil.ccShop.save();
                    sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TRANSFER_SUCCESS"));
                }
                // 플레이어에게 송금
                else {
                    try {
                        Player targetPlayer = Bukkit.getPlayer(target);

                        if (!Bukkit.getOnlinePlayers().contains(targetPlayer)) {
                            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.PLAYER_NOT_EXIST"));
                            return;
                        }

                        if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint")) {
                            JobsHook.addJobsPoint(targetPlayer, amount);
                            ShopUtil.addShopBalance(shopName, amount * -1);
                            ShopUtil.ccShop.save();

                            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TRANSFER_SUCCESS"));
                        } else {
                            Economy econ = DynamicShop.getEconomy();
                            EconomyResponse er = econ.depositPlayer(targetPlayer, amount);

                            if (er.transactionSuccess()) {
                                ShopUtil.addShopBalance(shopName, amount * -1);
                                ShopUtil.ccShop.save();

                                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("TRANSFER_SUCCESS"));
                            } else {
                                sender.sendMessage(DynamicShop.dsPrefix + "Transfer failed");
                            }
                        }
                    } catch (Exception e) {
                        sender.sendMessage(DynamicShop.dsPrefix + "Transfer failed. /" + e);
                    }
                }
            }
        }

        @Description("%HELP.HIDE_STOCK")
        @CommandCompletion("@dsShops true|false")
        @Subcommand("hidestock")
        @Syntax("<shopName> <true|false>")
        public void onHideStock(CommandSender sender, @Values("@dsShops") String shopName, @Values("true|false") boolean bool) {
            if (bool) {
                ShopUtil.ccShop.get().set(shopName + ".Options.hideStock", true);
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hideStock true");
            } else {
                ShopUtil.ccShop.get().set(shopName + ".Options.hideStock", null);
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hideStock false");
            }
            ShopUtil.ccShop.save();
        }

        @Description("%HELP.HIDE_PRICE")
        @CommandCompletion("@dsShops true|false")
        @Subcommand("hidepricingtype")
        @Syntax("<shopName> <true|false>")
        public void onHidePricingType(CommandSender sender, @Values("@dsShops") String shopName, @Values("true|false") boolean bool) {
            if (bool) {
                ShopUtil.ccShop.get().set(shopName + ".Options.hidePricingType", true);
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hidePricingType true");
            } else {
                ShopUtil.ccShop.get().set(shopName + ".Options.hidePricingType", null);
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().get("CHANGES_APPLIED") + "hidePricingType false");
            }
            ShopUtil.ccShop.save();
        }

        @Description("%HELP.SELL_BUY")
        @CommandCompletion("@dsShops SellOnly|BuyOnly|SellBuy")
        @Subcommand("sellbuy")
        @Syntax("<shopName> <SellOnly|BuyOnly|SellBuy>")
        public void onSellBuy(CommandSender sender, @Values("@dsShops") String shopName, @Values("SellOnly|BuyOnly|SellBuy") String type) {
            for (String s : ShopUtil.ccShop.get().getConfigurationSection(shopName).getKeys(false)) {
                try {
                    int i = Integer.parseInt(s);
                    if (!ShopUtil.ccShop.get().contains(shopName + "." + s + ".value")) {
                        continue; //장식용임
                    }
                } catch (Exception e) {
                    continue;
                }

                if (type.equalsIgnoreCase("SellBuy")) {
                    ShopUtil.ccShop.get().set(shopName + "." + s + ".tradeType", null);
                } else {
                    ShopUtil.ccShop.get().set(shopName + "." + s + ".tradeType", type);
                }
            }

            ShopUtil.ccShop.save();
            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + type);
        }

        @Description("%HELP.ON_LOG")
        @CommandCompletion("@dsShops enable|disable|clear")
        @Subcommand("log")
        @Syntax("<shopName> <enable|disable|clear>")
        public void onLog(CommandSender sender, @Values("@dsShops") String shopName, @Values("enable|disable|clear") String type) {
            if (type.equalsIgnoreCase("enable")) {
                ShopUtil.ccShop.get().set(shopName + ".Options.log", true);
                sender.sendMessage(DynamicShop.dsPrefix + shopName + "/" + LangUtil.ccLang.get().getString("LOG.LOG") + ": " + type);
            } else if (type.equalsIgnoreCase("disable")) {
                ShopUtil.ccShop.get().set(shopName + ".Options.log", null);
                sender.sendMessage(DynamicShop.dsPrefix + shopName + "/" + LangUtil.ccLang.get().getString("LOG.LOG") + ": " + type);
            } else if (type.equalsIgnoreCase("clear")) {
                LogUtil.ccLog.get().set(shopName, null);
                LogUtil.ccLog.save();
                sender.sendMessage(DynamicShop.dsPrefix + shopName + "/" + LangUtil.ccLang.get().getString("LOG.CLEAR"));
            } else {
                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                return;
            }

            ShopUtil.ccShop.save();
        }
    }
}

