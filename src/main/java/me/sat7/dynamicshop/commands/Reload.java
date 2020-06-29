package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import me.sat7.dynamicshop.utilities.SoundUtil;
import me.sat7.dynamicshop.utilities.WorthUtil;

public final class Reload {
    private Reload() {

    }

    static void reload(Player player) {
        if(!player.hasPermission("dshop.admin.reload"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
            return;
        }

        LangUtil.ccLang.reload();
        ShopUtil.ccShop.reload();
        StartPage.ccStartPage.reload();
        DynamicShop.ccSign.reload();
        WorthUtil.ccWorth.reload();
        SoundUtil.ccSound.reload();

        DynamicShop.plugin.reloadConfig();
        ConfigUtil.configSetup(DynamicShop.plugin);
        DynamicShop.plugin.startRandomChangeTask();
        DynamicShop.plugin.startCullLogsTask();

        LangUtil.setupLangFile(DynamicShop.plugin.getConfig().getString("Language"));

        player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("HELP.RELOADED"));
    }
}
