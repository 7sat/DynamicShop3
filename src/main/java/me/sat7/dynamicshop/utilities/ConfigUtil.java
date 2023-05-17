package me.sat7.dynamicshop.utilities;

import lombok.Getter;
import lombok.Setter;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Map;

import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ConfigUtil
{
    public final static int PluginConfigVersion = 7;

    public static int GetConfigVersion()
    {
        return DynamicShop.plugin.getConfig().getInt("Version");
    }

    public static void SetConfigVersion(int value)
    {
        DynamicShop.plugin.getConfig().set("Version", value);
    }

    public static FileConfiguration config;

    // ============================================================

    public static void Load()
    {
        ArrayList<String> header = new ArrayList<>();
        header.add("Language: ex) en-US,ko-KR");
        header.add("UseShopCommand: Set this to false if you want to disable '/shop'");
        header.add("DeliveryChargeScale: This is only used for shop with the 'delivery charge' flag. 0.01 ~");
        header.add("NumberOfPlayer: This is used to calculate the recommended median. 3~100");
        header.add("UseLegacyStockStabilization: false = Changed by n% of the gap with median. true = Changed by n% of median.");
        header.add("DisplayStockAsStack: ex) true: 10Stacks, false: 640");
        header.add("Version: Do NOT edit this");

        DynamicShop.plugin.saveDefaultConfig();
        DynamicShop.plugin.reloadConfig();

        config = DynamicShop.plugin.getConfig();

        try
        {
            if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17"))
            {
                StringBuilder temp = new StringBuilder();
                for (String s:header)
                {
                    temp.append(s);
                    temp.append("\n");
                }
                config.options().header(temp.toString());
            }
            else
            {
                config.options().setHeader(header);
            }
        }
        catch (Exception ignore){}

        ConvertV2toV3();
        ShopYMLUpdate();
        ValidateAndApply();
        SetConfigVersion(ConfigUtil.PluginConfigVersion);
        Save();
    }

    public static void Save()
    {
        DynamicShop.plugin.saveConfig();
    }

    public static String GetLanguage()
    {
        return config.getString("Language");
    }

    public static String GetPrefix()
    {
        return config.getString("Prefix");
    }

    // [ Command ] ==========

    public static boolean GetUseShopCommand()
    {
        return config.getBoolean("Command.UseShopCommand");
    }

    public static boolean GetOpenStartPageInsteadOfDefaultShop()
    {
        return config.getBoolean("Command.OpenStartPageInsteadOfDefaultShop");
    }

    public static String GetDefaultShopName()
    {
        return config.getString("Command.DefaultShopName");
    }

    public static void SetDefaultShopName(String value)
    {
        config.set("Command.DefaultShopName", value);
    }

    public static boolean GetPermissionCheckWhenCreatingAShopList()
    {
        return config.getBoolean("Command.PermissionCheckWhenCreatingAShopList");
    }

    // [ Shop ] ==========

    public static int GetSalesTax()
    {
        return config.getInt("Shop.SalesTax");
    }

    public static void SetSalesTax(int value)
    {
        config.set("Shop.SalesTax", value);
    }

    public static double GetDeliveryChargeScale()
    {
        return config.getDouble("Shop.DeliveryChargeScale");
    }

    public static void SetDeliveryChargeScale(double value)
    {
        config.set("Shop.DeliveryChargeScale", value);
    }

    public static int GetDeliveryChargeMin()
    {
        return config.getInt("Shop.DeliveryChargeMin");
    }

    public static void SetDeliveryChargeMin(int value)
    {
        config.set("Shop.DeliveryChargeMin", value);
    }

    public static int GetDeliveryChargeMax()
    {
        return config.getInt("Shop.DeliveryChargeMax");
    }

    public static void SetDeliveryChargeMax(int value)
    {
        config.set("Shop.DeliveryChargeMax", value);
    }

    public static int GetNumberOfPlayer()
    {
        return config.getInt("Shop.NumberOfPlayer");
    }

    public static void SetNumberOfPlayer(int value)
    {
        config.set("Shop.NumberOfPlayer", value);
    }

    public static boolean GetUseLegacyStockStabilization()
    {
        return config.getBoolean("Shop.UseLegacyStockStabilization");
    }

    public static boolean GetFastTrade() {
        return config.getBoolean("Shop.FastTrade");
    }

    // [ UI ] ==========

    public static boolean GetDisplayStockAsStack()
    {
        return config.getBoolean("UI.DisplayStockAsStack");
    }

    public static boolean GetOpenStartPageWhenClickCloseButton()
    {
        return config.getBoolean("UI.OpenStartPageWhenClickCloseButton");
    }

    public static String GetCloseButtonIcon()
    {
        return config.getString("UI.CloseButtonIcon");
    }

    public static void SetCloseButtonIcon(String value)
    {
        config.set("UI.CloseButtonIcon", value);
    }

    public static String GetPageButtonIcon()
    {
        return config.getString("UI.PageButtonIcon");
    }

    public static void SetPageButtonIcon(String value)
    {
        config.set("UI.PageButtonIcon", value);
    }

    public static String GetShopInfoButtonIcon()
    {
        return config.getString("UI.ShopInfoButtonIcon");
    }

    public static void SetShopInfoButtonIcon(String value)
    {
        config.set("UI.ShopInfoButtonIcon", value);
    }

    public static String GetIntFormat()
    {
        return config.getString("UI.IntFormat");
    }

    public static String GetDoubleFormat()
    {
        return config.getString("UI.DoubleFormat");
    }

    public static boolean GetLocalizedItemName()
    {
        return config.getBoolean("UI.LocalizedItemName");
    }

    public static boolean GetUsePlaceholderAPI()
    {
        return config.getBoolean("UI.UsePlaceholderAPI");
    }

    public static boolean GetUseHexColorCode()
    {
        return config.getBoolean("UI.UseHexColorCode");
    }

    public static boolean GetEnableInventoryClickSearch_StartPage()
    {
        return config.getBoolean("UI.EnableInventoryClickSearch.StartPage");
    }

    public static boolean GetEnableInventoryClickSearch_Shop()
    {
        return config.getBoolean("UI.EnableInventoryClickSearch.Shop");
    }

    // [ Log ] ==========

    public static boolean GetSaveLogs()
    {
        return config.getBoolean("Log.SaveLogs");
    }

    public static String GetLogFileNameFormat()
    {
        return config.getString("Log.LogFileNameFormat");
    }

    public static boolean GetCullLogs()
    {
        return config.getBoolean("Log.CullLogs");
    }

    public static int GetLogCullAgeMinutes()
    {
        return config.getInt("Log.LogCullAgeMinutes");
    }

    public static int GetLogCullTimeMinutes()
    {
        return config.getInt("Log.LogCullTimeMinutes");
    }

    // [ ShopYmlBackup ] ==========

    public static boolean GetShopYmlBackup_Enable()
    {
        return config.getBoolean("ShopYmlBackup.Enable");
    }

    public static int GetShopYmlBackup_IntervalMinutes()
    {
        return config.getInt("ShopYmlBackup.IntervalMinutes");
    }

    public static int GetShopYmlBackup_CullAgeMinutes()
    {
        return config.getInt("ShopYmlBackup.CullAgeMinutes");
    }

    // ============================================================

    private static void ValidateAndApply()
    {
        DynamicShop.dsPrefix_ = GetPrefix();

        SetSalesTax(Clamp(GetSalesTax(), 0, 99));
        setCurrentTax(GetSalesTax());

        if (GetDeliveryChargeScale() < 0.01)
            SetDeliveryChargeScale(0.01f);

        if (GetDeliveryChargeMin() < 1)
            SetDeliveryChargeMin(1);

        if (GetDeliveryChargeMax() < 1)
            SetDeliveryChargeMax(1);

        if (GetDeliveryChargeMax() < GetDeliveryChargeMin())
            SetDeliveryChargeMax(GetDeliveryChargeMin());

        SetNumberOfPlayer(Clamp(GetNumberOfPlayer(), 3, 100));
    }

    @Getter
    @Setter
    private static int currentTax;

    public static void resetTax()
    {
        currentTax = GetSalesTax();
    }

    // ============================================================

    private static void ConvertV2toV3()
    {
        if (config.get("ShowTax") != null)
        {
            config.set("ShowTax", null);
        }

        if (config.get("UseShopCommand") != null)
        {
            config.set("Command.UseShopCommand", config.get("UseShopCommand"));
            config.set("UseShopCommand", null);
        }

        if (config.get("OpenStartPageInsteadOfDefaultShop") != null)
        {
            config.set("Command.OpenStartPageInsteadOfDefaultShop", config.get("OpenStartPageInsteadOfDefaultShop"));
            config.set("OpenStartPageInsteadOfDefaultShop", null);
        }

        if (config.get("DefaultShopName") != null)
        {
            config.set("Command.DefaultShopName", config.get("DefaultShopName"));
            config.set("DefaultShopName", null);
        }

        if (config.get("SalesTax") != null)
        {
            config.set("Shop.SalesTax", config.get("SalesTax"));
            config.set("SalesTax", null);
        }

        if (config.get("DeliveryChargeScale") != null)
        {
            config.set("Shop.DeliveryChargeScale", config.get("DeliveryChargeScale"));
            config.set("DeliveryChargeScale", null);
        }

        if (config.get("NumberOfPlayer") != null)
        {
            config.set("Shop.NumberOfPlayer", config.get("NumberOfPlayer"));
            config.set("NumberOfPlayer", null);
        }

        if (config.get("DisplayStockAsStack") != null)
        {
            config.set("UI.DisplayStockAsStack", config.get("DisplayStockAsStack"));
            config.set("DisplayStockAsStack", null);
        }

        if (config.get("OnClickCloseButton_OpenStartPage") != null)
        {
            config.set("UI.OpenStartPageWhenClickCloseButton", config.get("OnClickCloseButton_OpenStartPage"));
            config.set("OnClickCloseButton_OpenStartPage", null);
        }

        if (config.get("ShopInfoButtonIcon") != null)
        {
            config.set("UI.ShopInfoButtonIcon", config.get("ShopInfoButtonIcon"));
            config.set("ShopInfoButtonIcon", null);
        }

        if (config.get("SaveLogs") != null)
        {
            config.set("Log.SaveLogs", config.get("SaveLogs"));
            config.set("SaveLogs", null);
        }

        if (config.get("CullLogs") != null)
        {
            config.set("Log.CullLogs", config.get("CullLogs"));
            config.set("CullLogs", null);
        }

        if (config.get("LogCullAgeMinutes") != null)
        {
            config.set("Log.LogCullAgeMinutes", config.get("LogCullAgeMinutes"));
            config.set("LogCullAgeMinutes", null);
        }

        if (config.get("LogCullTimeMinutes") != null)
        {
            config.set("Log.LogCullTimeMinutes", config.get("LogCullTimeMinutes"));
            config.set("LogCullTimeMinutes", null);
        }
    }

    private static void ShopYMLUpdate()
    {
        int userVersion = ConfigUtil.GetConfigVersion();
        if (userVersion == 3)
        {
            for(Map.Entry<String, CustomConfig> entry : ShopUtil.shopConfigFiles.entrySet())
            {
                ConfigurationSection cmdCS = entry.getValue().get().getConfigurationSection("Options.command");
                if(cmdCS == null)
                    continue;

                boolean somethingChanged = false;
                if(cmdCS.contains("sell") && !cmdCS.contains("sell.0"))
                {
                    cmdCS.set("sell.0", cmdCS.get("sell"));
                    somethingChanged = true;
                }
                if(cmdCS.contains("buy") && !cmdCS.contains("buy.0"))
                {
                    cmdCS.set("buy.0", cmdCS.get("buy"));
                    somethingChanged = true;
                }

                if(somethingChanged)
                    entry.getValue().save();
            }
        }
    }
}
