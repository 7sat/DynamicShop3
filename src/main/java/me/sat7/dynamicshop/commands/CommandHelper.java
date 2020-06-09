package me.sat7.dynamicshop.commands;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandHelper {
    private final DynamicShop plugin;

    public CommandHelper(DynamicShop instance) {
        plugin = instance;
    }

    public void register() {
        PaperCommandManager mgr = plugin.getCommandManager();

        mgr.getCommandCompletions().registerAsyncCompletion("minutes", c -> Arrays.asList(
                "10", "15", "30", "45", "60", "90", "120", "240",
                "300", "360", "420", "480"));

        mgr.getCommandCompletions().registerAsyncCompletion("dsShops", c -> new ArrayList<>(ShopUtil.ccShop.get().getKeys(false)));

        mgr.getCommandConditions().addCondition("creativeCheck", context -> {
            if(context.getIssuer().isPlayer()) {
                Player p = context.getIssuer().getPlayer();
                if (p.getGameMode() == GameMode.CREATIVE && !p.hasPermission(Constants.ADMIN_CREATIVE_PERMISSION)) {
                    throw new ConditionFailedException(LangUtil.ccLang.get().getString("ERR.CREATIVE"));
                }
            }
        });
    }

}