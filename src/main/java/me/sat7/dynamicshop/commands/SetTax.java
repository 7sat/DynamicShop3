package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.utilities.ConfigUtil;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;
import org.bukkit.scheduler.BukkitRunnable;

public final class SetTax {
    private static BukkitRunnable resetTaxTask = null;

    private SetTax() {

    }

    static boolean setTax(String[] args, Player player) {
        if(!player.hasPermission("dshop.admin.settax"))
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.NO_PERMISSION"));
            return true;
        }

        if(args.length == 2)
        {
            try
            {
                int newValue = Integer.parseInt(args[1]);
                if(newValue <= 2) newValue = 2;
                if(newValue > 99) newValue = 99;

                DynamicShop.plugin.getConfig().set("SalesTax",newValue);
                DynamicShop.plugin.saveConfig();

                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
                return true;
            }
            catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
            }
        }
        else if (args.length == 4 && args[1].equals("temp")) {
            try
            {
                int newValue = Integer.parseInt(args[2]);
                int tempTaxDurationMinutes = Integer.parseInt(args[3]);
                if(newValue <= 2) newValue = 2;
                if(newValue > 99) newValue = 99;
                if(tempTaxDurationMinutes <= 1) tempTaxDurationMinutes = 1;

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

                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("CHANGES_APPLIED") + newValue);
                return true;
            }
            catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_DATATYPE"));
            }
        }
        else
        {
            player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
