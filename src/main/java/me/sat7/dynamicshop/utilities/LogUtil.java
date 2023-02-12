package me.sat7.dynamicshop.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.opencsv.CSVWriter;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class LogUtil
{
    private LogUtil()
    {

    }

    // <로그파일 경로 + 이름, 로그데이타>
    public static final ConcurrentHashMap<String, ArrayList<String>> log = new ConcurrentHashMap<>(); // 아직 파일에 저장되지 않은 로그들 // todo 서버가 꺼질때 저장해야함.

    // 거래 로그 기록
    public static void addLog(String shopName, String itemName, int amount, double value, String curr, String player)
    {
        if (DynamicShop.plugin.getConfig().getBoolean("Log.SaveLogs"))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

            if (data.get().contains("Options.log") && data.get().getBoolean("Options.log")) // 옛날엔 이렇게 저장했음.
            {
                data.get().set("Options.log.active", true);
            }

            if (data.get().contains("Options.log.active") && data.get().getBoolean("Options.log.active"))
            {
                SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy,HH.mm.ss");
                String time = sdf.format(System.currentTimeMillis());

                String keyString = CreatePath(shopName);
                String valueString = time + "," + shopName + "," + itemName + "," + amount + "," + (Math.round(value * 100) / 100.0) + "," + curr + "," + player;

                if (log.get(keyString) == null)
                {
                    log.put(keyString, new ArrayList<>(Collections.singletonList(valueString)));
                } else
                {
                    log.get(keyString).add(valueString);
                }

                String message = DynamicShop.dsPrefix(null) + t(null, amount > 0 ? "LOG.BUY" : "LOG.SELL")
                        .replace("{player}", player)
                        .replace("{shop}", shopName)
                        .replace("{item}", ItemsUtil.getBeautifiedName( itemName))
                        .replace("{amount}", Math.abs(amount) + "");

                if(data.get().contains("Options.log.printToConsole") && data.get().getBoolean("Options.log.printToConsole"))
                {
                    DynamicShop.console.sendMessage(message);
                }
                if(data.get().contains("Options.log.printToAdmin") && data.get().getBoolean("Options.log.printToAdmin"))
                {
                    Bukkit.getServer().broadcast(message, Constants.P_ADMIN_SHOP_EDIT);
                }


                //boolean useLocalizedName = DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName");
                //String message = DynamicShop.dsPrefix(null) + t(null, "LOG.SELL", !useLocalizedName)
                //        .replace("{player}", player)
                //        .replace("{shop}", shopName)
                //        .replace("{amount}", amount + "");

                //if (useLocalizedName)
                //{
                //    message = message.replace("{item}", "<item>");
                //
                //    LangUtil.sendMessageWithLocalizedItemName(player, message, Material.getMaterial(itemName));
                //} else
                //{
                //    message = message.replace("{item}", itemName);
                //}
            }
        }
    }

    public static void SaveLogToCSV()
    {
        if (log.keySet().size() == 0)
            return;

        File LogFolder = new File(DynamicShop.plugin.getDataFolder(), "Log");
        LogFolder.mkdir();

        for (Map.Entry<String, ArrayList<String>> entry : log.entrySet())
        {
            try
            {
                File myFile = new File(entry.getKey());
                File directory = new File(myFile.getParent());
                if (!directory.exists())
                {
                    directory.mkdir();
                }

                CSVWriter csvWriter = new CSVWriter(new FileWriter(entry.getKey(), true));
                for (String data : entry.getValue())
                {
                    csvWriter.writeNext(data.split(","));
                }
                csvWriter.close();

                log.remove(entry.getKey());
            } catch (IOException e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Failed to save CSV file. Is the file open? Unsaved data is kept internally until the plugin is closed.");
            }
        }
    }

    private static String CreatePath(String shopName)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DynamicShop.plugin.getConfig().getString("Log.LogFileNameFormat"));
        String timeForFileName = sdf.format(System.currentTimeMillis());
        return DynamicShop.plugin.getDataFolder() + "/Log/" + shopName + "/" + timeForFileName + ".csv";
    }

    public static void cullLogs()
    {
        File[] logFolders = new File(DynamicShop.plugin.getDataFolder() + "/Log").listFiles();
        if(logFolders == null)
            return;

        if (logFolders.length > 0)
        {
            int deleted = 0;
            for (File folder : logFolders)
            {
                File[] logfiles = new File(folder.getPath()).listFiles();
                if (logfiles == null)
                    continue;

                if (logfiles.length == 0)
                {
                    folder.delete();
                    continue;
                }

                for (File log : logfiles)
                {
                    if (log.isDirectory())
                        continue;

                    int ageMins = (int) (System.currentTimeMillis() - log.lastModified()) / 60000;
                    if (ageMins > DynamicShop.plugin.getConfig().getInt("Log.LogCullAgeMinutes"))
                    {
                        if (log.delete())
                            deleted++;
                    }
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

    public static void DeleteShopLog(String shopName)
    {
        File directory = new File(DynamicShop.plugin.getDataFolder() + "/Log/" + shopName + "/");
        if (directory.exists())
        {
            try
            {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Failed to delete csv.");
            }
        }
    }
}
