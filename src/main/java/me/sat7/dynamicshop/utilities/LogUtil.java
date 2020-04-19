package me.sat7.dynamicshop.utilities;

import java.text.SimpleDateFormat;

import me.sat7.dynamicshop.files.CustomConfig;

public final class LogUtil {
    public static CustomConfig ccLog;

    private LogUtil() {

    }

    public static void setupLogFile()
    {
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyMMdd-HHmmss");
        String timeStr = sdf.format (System.currentTimeMillis());
        ccLog.setup("Log_"+timeStr,"Log");
        ccLog.get().options().copyDefaults(true);
        ccLog.save();
    }

    // 거래 로그 기록
    public static void addLog(String shopName, String itemName, int amount, double value, String curr, String player)
    {
        if(ShopUtil.ccShop.get().contains(shopName+".Options.log") && ShopUtil.ccShop.get().getBoolean(shopName+".Options.log"))
        {
            SimpleDateFormat sdf = new SimpleDateFormat ( "yyMMdd,HHmmss");
            String timeStr = sdf.format (System.currentTimeMillis());

            int i = 0;
            if(ccLog.get().contains(shopName)) i = ccLog.get().getConfigurationSection(shopName).getKeys(false).size();

            ccLog.get().set(shopName+"."+i,timeStr +","+itemName + "," + amount + "," + Math.round(value*100)/100.0 + "," + curr+","+player);
            ccLog.save();
        }

        if(ccLog.get().getKeys(true).size() > 500)
        {
            setupLogFile();
        }
    }
}
