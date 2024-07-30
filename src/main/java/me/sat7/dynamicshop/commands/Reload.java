package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.guis.QuickSell;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.guis.StartPage;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_RELOAD;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class Reload extends DSCMD
{
    public Reload()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_RELOAD;
        validArgCount.add(1);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "reload"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds reload");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

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

        ConfigUtil.Load();

        DynamicShop.plugin.PeriodicRepetitiveTask();

        DynamicShop.plugin.startSaveLogsTask();
        DynamicShop.plugin.startCullLogsTask();

        DynamicShop.plugin.StartBackupTask();
        DynamicShop.plugin.StartShopSaveTask();

        DynamicShop.plugin.StartUserDataTask();

        QuickSell.quickSellGui.reload();
        QuickSell.SetupQuickSellGUIFile();

        LangUtil.setupLangFile(ConfigUtil.GetLanguage()); // ConfigUtil.Load() 보다 밑에 있어야함.

        RotationUtil.Reload();

        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "HELP.RELOADED"));
    }
}
