package me.sat7.dynamicshop.utilities;

import java.io.File;
import java.text.SimpleDateFormat;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;

public final class LogUtil
{
    public static CustomConfig ccLog;

    private LogUtil()
    {

    }

    public static void setupLogFile()
    {
        if (DynamicShop.plugin.getConfig().getBoolean("Log.SaveLogs"))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
            String timeStr = sdf.format(System.currentTimeMillis());
            ccLog.setup("Log_" + timeStr, "Log");
            ccLog.get().options().copyDefaults(true);
            ccLog.save();
        }
    }

    // 거래 로그 기록
    public static void addLog(String shopName, String itemName, int amount, double value, String curr, String player)
    {
        if (DynamicShop.plugin.getConfig().getBoolean("Log.SaveLogs"))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
            if (data.get().contains("Options.log") && data.get().getBoolean("Options.log"))
            {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
                String timeStr = sdf.format(System.currentTimeMillis());

                int i = 0;
                if (ccLog.get().contains(shopName))
                    i = ccLog.get().getConfigurationSection(shopName).getKeys(false).size();

                ccLog.get().set(shopName + "." + i, timeStr + "," + itemName + "," + amount + "," + Math.round(value * 100) / 100.0 + "," + curr + "," + player);
                ccLog.save();
            }

            if (ccLog.get().getKeys(true).size() > 1000)
            {
                setupLogFile();
            }
        }
    }

    public static void cullLogs()
    {
        File[] logs = new File(DynamicShop.plugin.getDataFolder() + "/Log").listFiles();
        if(logs == null)
            return;

        if (logs.length > 0)
        {
            int deleted = 0;
            for (File l : logs)
            {
                int ageMins = (int) (System.currentTimeMillis() - l.lastModified()) / 60000;
                if (ageMins > DynamicShop.plugin.getConfig().getInt("Log.LogCullAgeMinutes"))
                {
                    l.delete();
                    deleted++;
                }
            }
            if (deleted > 0)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +
                        " Found and deleted " + deleted + " log file(s) older than " + DynamicShop.plugin.getConfig().getInt("Log.LogCullAgeMinutes") +
                        " minutes. Checking again in " + DynamicShop.plugin.getConfig().getInt("Log.LogCullTimeMinutes") + " minutes.");
            }
        }
    }
}
