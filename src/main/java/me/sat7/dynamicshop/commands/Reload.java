package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.utilities.*;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.guis.StartPage;

import static me.sat7.dynamicshop.utilities.ConfigUtil.configVersion;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class Reload
{
    private Reload()
    {

    }

    static void reload(Player player)
    {
        if (!player.hasPermission("dshop.admin.reload"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
            return;
        }

        LangUtil.ccLang.reload();

        LayoutUtil.ccLayout.reload();
        LayoutUtil.Setup();

        ShopUtil.Reload();
        StartPage.ccStartPage.reload();
        DynamicShop.ccSign.reload();

        WorthUtil.ccWorth.reload();
        WorthUtil.setupWorthFile();

        SoundUtil.ccSound.reload();
        SoundUtil.setupSoundFile();

        DynamicShop.plugin.reloadConfig();
        ConfigUtil.configSetup(DynamicShop.plugin);
        DynamicShop.plugin.startRandomChangeTask();

        DynamicShop.plugin.startCullLogsTask();

        LangUtil.setupLangFile(DynamicShop.plugin.getConfig().getString("Language"));

        DynamicShop.plugin.getConfig().set("Version", configVersion);

        player.sendMessage(DynamicShop.dsPrefix + t("HELP.RELOADED"));
    }
}
