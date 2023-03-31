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
    public final static int PluginConfigVersion = 4;

    public static int GetConfigVersion()
    {
        return DynamicShop.plugin.getConfig().getInt("Version");
    }

    public static void SetConfigVersion(int value)
    {
        DynamicShop.plugin.getConfig().set("Version", value);
    }

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
                DynamicShop.plugin.getConfig().options().header(temp.toString());
            }
            else
            {
                DynamicShop.plugin.getConfig().options().setHeader(header);
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
        return DynamicShop.plugin.getConfig().getString("Language");
    }

    public static String GetPrefix()
    {
        return DynamicShop.plugin.getConfig().getString("Prefix");
    }

    // [ Command ] ==========

    public static boolean GetUseShopCommand()
    {
        return DynamicShop.plugin.getConfig().getBoolean("Command.UseShopCommand");
    }

    public static boolean GetOpenStartPageInsteadOfDefaultShop()
    {
        return DynamicShop.plugin.getConfig().getBoolean("Command.OpenStartPageInsteadOfDefaultShop");
    }

    public static String GetDefaultShopName()
    {
        return DynamicShop.plugin.getConfig().getString("Command.DefaultShopName");
    }

    public static void SetDefaultShopName(String value)
    {
        DynamicShop.plugin.getConfig().set("Command.DefaultShopName", value);
    }

    public static boolean GetPermissionCheckWhenCreatingAShopList()
    {
        return DynamicShop.plugin.getConfig().getBoolean("Command.PermissionCheckWhenCreatingAShopList");
    }

    // [ Shop ] ==========

    public static int GetSalesTax()
    {
        return DynamicShop.plugin.getConfig().getInt("Shop.SalesTax");
    }

    public static void SetSalesTax(int value)
    {
        DynamicShop.plugin.getConfig().set("Shop.SalesTax", value);
    }

    public static double GetDeliveryChargeScale()
    {
        return DynamicShop.plugin.getConfig().getDouble("Shop.DeliveryChargeScale");
    }

    public static void SetDeliveryChargeScale(double value)
    {
        DynamicShop.plugin.getConfig().set("Shop.DeliveryChargeScale", value);
    }

    public static int GetDeliveryChargeMin()
    {
        return DynamicShop.plugin.getConfig().getInt("Shop.DeliveryChargeMin");
    }

    public static void SetDeliveryChargeMin(int value)
    {
        DynamicShop.plugin.getConfig().set("Shop.DeliveryChargeMin", value);
    }

    public static int GetDeliveryChargeMax()
    {
        return DynamicShop.plugin.getConfig().getInt("Shop.DeliveryChargeMax");
    }

    public static void SetDeliveryChargeMax(int value)
    {
        DynamicShop.plugin.getConfig().set("Shop.DeliveryChargeMax", value);
    }

    public static int GetNumberOfPlayer()
    {
        return DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer");
    }

    public static void SetNumberOfPlayer(int value)
    {
        DynamicShop.plugin.getConfig().set("Shop.NumberOfPlayer", value);
    }

    public static boolean GetUseLegacyStockStabilization()
    {
        return DynamicShop.plugin.getConfig().getBoolean("Shop.UseLegacyStockStabilization");
    }

    // [ UI ] ==========

    public static boolean GetDisplayStockAsStack()
    {
        return DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack");
    }

    public static boolean GetOpenStartPageWhenClickCloseButton()
    {
        return DynamicShop.plugin.getConfig().getBoolean("UI.OpenStartPageWhenClickCloseButton");
    }

    public static String GetCloseButtonIcon()
    {
        return DynamicShop.plugin.getConfig().getString("UI.CloseButtonIcon");
    }

    public static void SetCloseButtonIcon(String value)
    {
        DynamicShop.plugin.getConfig().set("UI.CloseButtonIcon", value);
    }

    public static String GetPageButtonIcon()
    {
        return DynamicShop.plugin.getConfig().getString("UI.PageButtonIcon");
    }

    public static void SetPageButtonIcon(String value)
    {
        DynamicShop.plugin.getConfig().set("UI.PageButtonIcon", value);
    }

    public static String GetShopInfoButtonIcon()
    {
        return DynamicShop.plugin.getConfig().getString("UI.ShopInfoButtonIcon");
    }

    public static void SetShopInfoButtonIcon(String value)
    {
        DynamicShop.plugin.getConfig().set("UI.ShopInfoButtonIcon", value);
    }

    public static String GetIntFormat()
    {
        return DynamicShop.plugin.getConfig().getString("UI.IntFormat");
    }

    public static String GetDoubleFormat()
    {
        return DynamicShop.plugin.getConfig().getString("UI.DoubleFormat");
    }

    public static boolean GetLocalizedItemName()
    {
        return DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName");
    }

    public static boolean GetUsePlaceholderAPI()
    {
        return DynamicShop.plugin.getConfig().getBoolean("UI.UsePlaceholderAPI");
    }

    public static boolean GetUseHexColorCode()
    {
        return DynamicShop.plugin.getConfig().getBoolean("UI.UseHexColorCode");
    }

    public static boolean GetEnableInventoryClickSearch_StartPage()
    {
        return DynamicShop.plugin.getConfig().getBoolean("UI.EnableInventoryClickSearch.StartPage");
    }

    public static boolean GetEnableInventoryClickSearch_Shop()
    {
        return DynamicShop.plugin.getConfig().getBoolean("UI.EnableInventoryClickSearch.Shop");
    }

    // [ Log ] ==========

    public static boolean GetSaveLogs()
    {
        return DynamicShop.plugin.getConfig().getBoolean("Log.SaveLogs");
    }

    public static String GetLogFileNameFormat()
    {
        return DynamicShop.plugin.getConfig().getString("Log.LogFileNameFormat");
    }

    public static boolean GetCullLogs()
    {
        return DynamicShop.plugin.getConfig().getBoolean("Log.CullLogs");
    }

    public static int GetLogCullAgeMinutes()
    {
        return DynamicShop.plugin.getConfig().getInt("Log.LogCullAgeMinutes");
    }

    public static int GetLogCullTimeMinutes()
    {
        return DynamicShop.plugin.getConfig().getInt("Log.LogCullTimeMinutes");
    }

    // [ ShopYmlBackup ] ==========

    public static boolean GetShopYmlBackup_Enable()
    {
        return DynamicShop.plugin.getConfig().getBoolean("ShopYmlBackup.Enable");
    }

    public static int GetShopYmlBackup_IntervalMinutes()
    {
        return DynamicShop.plugin.getConfig().getInt("ShopYmlBackup.IntervalMinutes");
    }

    public static int GetShopYmlBackup_CullAgeMinutes()
    {
        return DynamicShop.plugin.getConfig().getInt("ShopYmlBackup.CullAgeMinutes");
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
        if (DynamicShop.plugin.getConfig().get("ShowTax") != null)
        {
            DynamicShop.plugin.getConfig().set("ShowTax", null);
        }

        if (DynamicShop.plugin.getConfig().get("UseShopCommand") != null)
        {
            DynamicShop.plugin.getConfig().set("Command.UseShopCommand", DynamicShop.plugin.getConfig().get("UseShopCommand"));
            DynamicShop.plugin.getConfig().set("UseShopCommand", null);
        }

        if (DynamicShop.plugin.getConfig().get("OpenStartPageInsteadOfDefaultShop") != null)
        {
            DynamicShop.plugin.getConfig().set("Command.OpenStartPageInsteadOfDefaultShop", DynamicShop.plugin.getConfig().get("OpenStartPageInsteadOfDefaultShop"));
            DynamicShop.plugin.getConfig().set("OpenStartPageInsteadOfDefaultShop", null);
        }

        if (DynamicShop.plugin.getConfig().get("DefaultShopName") != null)
        {
            DynamicShop.plugin.getConfig().set("Command.DefaultShopName", DynamicShop.plugin.getConfig().get("DefaultShopName"));
            DynamicShop.plugin.getConfig().set("DefaultShopName", null);
        }

        if (DynamicShop.plugin.getConfig().get("SalesTax") != null)
        {
            DynamicShop.plugin.getConfig().set("Shop.SalesTax", DynamicShop.plugin.getConfig().get("SalesTax"));
            DynamicShop.plugin.getConfig().set("SalesTax", null);
        }

        if (DynamicShop.plugin.getConfig().get("DeliveryChargeScale") != null)
        {
            DynamicShop.plugin.getConfig().set("Shop.DeliveryChargeScale", DynamicShop.plugin.getConfig().get("DeliveryChargeScale"));
            DynamicShop.plugin.getConfig().set("DeliveryChargeScale", null);
        }

        if (DynamicShop.plugin.getConfig().get("NumberOfPlayer") != null)
        {
            DynamicShop.plugin.getConfig().set("Shop.NumberOfPlayer", DynamicShop.plugin.getConfig().get("NumberOfPlayer"));
            DynamicShop.plugin.getConfig().set("NumberOfPlayer", null);
        }

        if (DynamicShop.plugin.getConfig().get("DisplayStockAsStack") != null)
        {
            DynamicShop.plugin.getConfig().set("UI.DisplayStockAsStack", DynamicShop.plugin.getConfig().get("DisplayStockAsStack"));
            DynamicShop.plugin.getConfig().set("DisplayStockAsStack", null);
        }

        if (DynamicShop.plugin.getConfig().get("OnClickCloseButton_OpenStartPage") != null)
        {
            DynamicShop.plugin.getConfig().set("UI.OpenStartPageWhenClickCloseButton", DynamicShop.plugin.getConfig().get("OnClickCloseButton_OpenStartPage"));
            DynamicShop.plugin.getConfig().set("OnClickCloseButton_OpenStartPage", null);
        }

        if (DynamicShop.plugin.getConfig().get("ShopInfoButtonIcon") != null)
        {
            DynamicShop.plugin.getConfig().set("UI.ShopInfoButtonIcon", DynamicShop.plugin.getConfig().get("ShopInfoButtonIcon"));
            DynamicShop.plugin.getConfig().set("ShopInfoButtonIcon", null);
        }

        if (DynamicShop.plugin.getConfig().get("SaveLogs") != null)
        {
            DynamicShop.plugin.getConfig().set("Log.SaveLogs", DynamicShop.plugin.getConfig().get("SaveLogs"));
            DynamicShop.plugin.getConfig().set("SaveLogs", null);
        }

        if (DynamicShop.plugin.getConfig().get("CullLogs") != null)
        {
            DynamicShop.plugin.getConfig().set("Log.CullLogs", DynamicShop.plugin.getConfig().get("CullLogs"));
            DynamicShop.plugin.getConfig().set("CullLogs", null);
        }

        if (DynamicShop.plugin.getConfig().get("LogCullAgeMinutes") != null)
        {
            DynamicShop.plugin.getConfig().set("Log.LogCullAgeMinutes", DynamicShop.plugin.getConfig().get("LogCullAgeMinutes"));
            DynamicShop.plugin.getConfig().set("LogCullAgeMinutes", null);
        }

        if (DynamicShop.plugin.getConfig().get("LogCullTimeMinutes") != null)
        {
            DynamicShop.plugin.getConfig().set("Log.LogCullTimeMinutes", DynamicShop.plugin.getConfig().get("LogCullTimeMinutes"));
            DynamicShop.plugin.getConfig().set("LogCullTimeMinutes", null);
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
