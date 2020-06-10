package me.sat7.dynamicshop.commands;

import co.aikar.commands.*;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class CommandHelper {
    private final DynamicShop plugin;

    public CommandHelper(DynamicShop instance) {
        plugin = instance;
    }

    public void register() {
        PaperCommandManager mgr = plugin.getCommandManager();

        //Set Colors
        mgr.setFormat(MessageType.ERROR, new BukkitMessageFormatter(ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.RED));
        mgr.setFormat(MessageType.SYNTAX, new BukkitMessageFormatter(ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.WHITE));
        mgr.setFormat(MessageType.INFO, new BukkitMessageFormatter(ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.WHITE));
        mgr.setFormat(MessageType.HELP, new BukkitMessageFormatter(ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.WHITE));

        //Command Completions
        mgr.getCommandCompletions().registerAsyncCompletion("minutes", c -> Arrays.asList(
                "10", "15", "30", "45", "60", "90", "120", "240",
                "300", "360", "420", "480"));

        mgr.getCommandCompletions().registerAsyncCompletion("dsShops", c -> new ArrayList<>(ShopUtil.ccShop.get().getKeys(false)));

        mgr.getCommandCompletions().registerAsyncCompletion("shopsAndPlayers", c -> {
            ArrayList<String> l = new ArrayList<>(ShopUtil.ccShop.get().getKeys(false));
            Bukkit.getOnlinePlayers().forEach(player -> l.add(player.getName()));
            return l;
        });

        mgr.getCommandCompletions().registerAsyncCompletion("dsMin", c -> {
            ArrayList<String> list = new ArrayList<>(Collections.singletonList("0.01"));
            IntStream.range(0, 100).forEach(i -> list.add(String.valueOf(i) + ".00"));
            return list;
        });

        mgr.getCommandCompletions().registerAsyncCompletion("dsoperators", c -> Arrays.asList("=", "+", "-", "*", "/"));

        mgr.getCommandCompletions().registerAsyncCompletion("dsMax", c -> {
            ArrayList<String> list = new ArrayList<>(Collections.singletonList("-1"));
            IntStream.range(5, 120).forEach(i -> list.add(String.valueOf(i) + ".00"));
            return list;
        });

        //Command Conditions
        mgr.getCommandConditions().addCondition("creativeCheck", context -> {
            if (context.getIssuer().isPlayer()) {
                Player p = context.getIssuer().getPlayer();
                if (p.getGameMode() == GameMode.CREATIVE && !p.hasPermission(Constants.ADMIN_CREATIVE_PERMISSION)) {
                    throw new ConditionFailedException(LangUtil.ccLang.get().getString("ERR.CREATIVE"));
                }
            }
        });

        //Annotation Placeholders
        CommandReplacements replacements = mgr.getCommandReplacements();
        replacements.addReplacements(
                "HELP.SHOP_DESCRIPTION", LangUtil.ccLang.get().getString("HELP.SHOP_DESCRIPTION"),
                "HELP.CREATESHOP", LangUtil.ccLang.get().getString("HELP.CREATESHOP"),
                "HELP.DELETESHOP", LangUtil.ccLang.get().getString("HELP.DELETESHOP"),
                "HELP.SETTAX", LangUtil.ccLang.get().getString("HELP.SETTAX"),
                "HELP.SETTAX_TEMP", LangUtil.ccLang.get().getString("HELP.SETTAX_TEMP"),
                "HELP.SHOPADDHAND", LangUtil.ccLang.get().getString("HELP.SHOPADDHAND"),
                "HELP.SHOPADDITEM", LangUtil.ccLang.get().getString("HELP.SHOPADDITEM"),
                "HELP.SHOPEDIT", LangUtil.ccLang.get().getString("HELP.SHOPEDIT"),
                "HELP.EDITALL", LangUtil.ccLang.get().getString("HELP.EDITALL"),
                "HELP.RELOAD", LangUtil.ccLang.get().getString("HELP.RELOAD"),
                "HELP.QSELL", LangUtil.ccLang.get().getString("HELP.QSELL"),
                "HELP.DELETE_OLD_USER", LangUtil.ccLang.get().getString("HELP.DELETE_OLD_USER"),
                "HELP.CONVERT", LangUtil.ccLang.get().getString("HELP.CONVERT"),
                "HELP.ACCOUNT", LangUtil.ccLang.get().getString("HELP.ACCOUNT"),
                "HELP.HELP", LangUtil.ccLang.get().getString("HELP.HELP"),
                "HELP.RENAME_SHOP", LangUtil.ccLang.get().getString("HELP.RENAME_SHOP"),
                "HELP.MERGE_SHOP", LangUtil.ccLang.get().getString("HELP.MERGE_SHOP"),
                "HELP.SET_DEFAULT_SHOP", LangUtil.ccLang.get().getString("HELP.SET_DEFAULT_SHOP"),
                "HELP.SHOP_PERMISSION", LangUtil.ccLang.get().getString("HELP.SHOP_PERMISSION"),
                "TIME.SET_SHOPHOURS", LangUtil.ccLang.get().getString("TIME.SET_SHOPHOURS"),
                "HELP.MAXPAGES", LangUtil.ccLang.get().getString("HELP.MAXPAGES"),
                "HELP.SETFLAG", LangUtil.ccLang.get().getString("HELP.SETFLAG"),
                "HELP.SET_POSITION", LangUtil.ccLang.get().getString("HELP.SET_POSITION"),
                "HELP.FLUCTUATION", LangUtil.ccLang.get().getString("HELP.FLUCTUATION"),
                "HELP.STABILIZATION", LangUtil.ccLang.get().getString("HELP.STABILIZATION"),
                "HELP.ACCOUNT_LINK", LangUtil.ccLang.get().getString("HELP.ACCOUNT_LINK"),
                "HELP.ACCOUNT_TRANSFER", LangUtil.ccLang.get().getString("HELP.ACCOUNT_TRANSFER"),
                "HELP.HIDE_STOCK", LangUtil.ccLang.get().getString("HELP.HIDE_STOCK"),
                "HELP.HIDE_PRICE", LangUtil.ccLang.get().getString("HELP.HIDE_PRICE"),
                "HELP.SELL_BUY", LangUtil.ccLang.get().getString("HELP.SELL_BUY"),
                "HELP.ON_LOG", LangUtil.ccLang.get().getString("HELP.ON_LOG")
        );
    }

}