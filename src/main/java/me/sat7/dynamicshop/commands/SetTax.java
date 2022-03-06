package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.utilities.ConfigUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import org.bukkit.scheduler.BukkitRunnable;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SET_TAX;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class SetTax extends DSCMD
{
    private static BukkitRunnable resetTaxTask = null;

    public SetTax()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SET_TAX;
        validArgCount.add(2);
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "settax"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds settax <value>");

        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "settax temp"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds settax temp <tax_value> <minutes_until_reset>");

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (args.length == 2)
        {
            try
            {
                int newValue = Clamp(Integer.parseInt(args[1]), 1, 99);

                DynamicShop.plugin.getConfig().set("Shop.SalesTax", newValue);
                DynamicShop.plugin.saveConfig();

                ConfigUtil.setCurrentTax(newValue);

                sender.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + newValue);
            } catch (Exception e)
            {
                sender.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
            }
        } else if (args.length == 4 && args[1].equals("temp"))
        {
            try
            {
                int newValue = Clamp(Integer.parseInt(args[2]), 1, 99);
                int tempTaxDurationMinutes = Integer.parseInt(args[3]);

                if (tempTaxDurationMinutes <= 1) tempTaxDurationMinutes = 1;

                ConfigUtil.setCurrentTax(newValue);

                class ResetTaxTask extends BukkitRunnable
                {
                    @Override
                    public void run()
                    {
                        ConfigUtil.resetTax();
                    }
                }

                if (resetTaxTask != null)
                {
                    resetTaxTask.cancel();
                    resetTaxTask = null;
                }
                resetTaxTask = new ResetTaxTask();
                resetTaxTask.runTaskLater(DynamicShop.plugin, 20L * 60L * tempTaxDurationMinutes);

                sender.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + newValue);
            } catch (Exception e)
            {
                sender.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
            }
        }
        else
        {
            sender.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
        }
    }
}
