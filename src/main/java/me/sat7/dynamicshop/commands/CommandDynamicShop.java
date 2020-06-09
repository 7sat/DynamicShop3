package me.sat7.dynamicshop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("dynamicshop|dshop|ds|shop")
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
            if (newValue <= 2) newValue = 2;
            if (newValue > 99) newValue = 99;

            DynamicShop.plugin.getConfig().set("SalesTax", newValue);
            DynamicShop.plugin.saveConfig();

            sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
        }

        @Subcommand("temp")
        @Description("Set the sales tax temporarily")
        public class Temp extends BaseCommand {
            @Default
            @CommandCompletion("@range:100 @minutes")
            @Syntax("<tax> <minutes>")
            public void onTempSetTax(CommandSender sender, int tax, int minutes) {
                int newValue = tax;
                int tempTaxDurationMinutes = minutes;
                if (newValue <= 2) newValue = 2;
                if (newValue > 99) newValue = 99;
                if (tempTaxDurationMinutes <= 1) tempTaxDurationMinutes = 1;

                ConfigUtil.setCurrentTax(newValue);
                Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, ConfigUtil::resetTax, 20L * 60L * tempTaxDurationMinutes);

                sender.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
            }
        }
    }



    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " You can't run this command in console");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission(Constants.USE_SHOP_PERMISSION)) {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.PERMISSION"));
            return true;
        }
        if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission(Constants.ADMIN_CREATIVE_PERMISSION)) {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.CREATIVE"));
            return true;
        }

        // user.yml 에 player가 없으면 재생성 시도. 실패시 리턴.
        if (!DynaShopAPI.recreateUserData(player)) {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_USER_ID"));
            return true;
        }

        // 스타트페이지
        if (args.length == 0) {
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".interactItem", "");
            DynaShopAPI.openStartPage(player);
            return true;
        }
        // Start page 버튼 테스트용 함수
        else if (args[0].equalsIgnoreCase("Testfunction")) {
            player.sendMessage(DynamicShop.dsPrefix + "button clicked!");
            return true;
        } else if (args[0].equalsIgnoreCase("close")) {
            player.closeInventory();
            return true;
        }
        // ds shop [<shopName>]
        else if (args[0].equalsIgnoreCase("shop") && Shop.shopCommand(args, player)) {
            return true;
        }

        // qsell
        else if (args[0].equalsIgnoreCase("qsell")) {
            DynaShopAPI.openQuickSellGUI(player);

            return true;
        }

        // cmdhelp
        else if (args[0].equalsIgnoreCase("cmdhelp")) {
            return _CommandHelp.commandHelp(args, player);
        }

        // createShop
        else if (args[0].equalsIgnoreCase("createshop")) {
            return CreateShop.createShop(args, player);
        }

        // deleteShop
        else if (args[0].equalsIgnoreCase("deleteshop")) {
            if (DeleteShop.deleteShop(args, player)) {
                return true;
            }
        }

        // RenameShop
        else if (args[0].equalsIgnoreCase("renameshop")) {
            if (RenameShop.renameShop(args, player)) {
                return true;
            }
        }

        // MergeShop
        else if (args[0].equalsIgnoreCase("mergeshop")) {
            if (MergeShop.mergeShop(args, player)) {
                return true;
            }
        }

        // setdefaultshop
        else if (args[0].equalsIgnoreCase("setdefaultshop")) {
            if (SetDefaultShop.setDefaultShop(args, player)) {
                return true;
            }
        }


        // deleteOldUser <day>
        else if (args[0].equalsIgnoreCase("deleteOldUser")) {
            DeleteUser.deleteUser(args, player);
            return true;
        }

        // convert Shop
        else if (args[0].equalsIgnoreCase("convert")) {
            return Convert.convert(args, player);
        }

        return true;
    }

}

