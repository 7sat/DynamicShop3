package me.sat7.dynamicshop;

import me.clip.placeholderapi.PlaceholderAPI;
import me.pikamug.localelib.LocaleManager;
import me.sat7.dynamicshop.commands.CMDManager;
import me.sat7.dynamicshop.commands.Optional;
import me.sat7.dynamicshop.commands.Root;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.events.*;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.gui.impl.QuickSell;
import me.sat7.dynamicshop.gui.impl.StartPage;
import me.sat7.dynamicshop.gui.UIManager;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

import static me.sat7.dynamicshop.utilities.ConfigUtil.configVersion;

public final class DynamicShop extends JavaPlugin implements Listener
{
    private static Economy econ = null; // 볼트에 물려있는 이코노미
    public static Economy getEconomy()
    {
        return econ;
    }

    public static DynamicShop plugin;
    public static ConsoleCommandSender console;

    public static String dsPrefix(CommandSender commandSender)
    {
        Player player = null;
        if(commandSender instanceof Player)
            player = (Player) commandSender;

        return dsPrefix(player);
    }

    public static String dsPrefix(Player player)
    {
        String temp = dsPrefix_;

        if(plugin.getConfig().getBoolean("UI.UseHexColorCode"))
            temp = LangUtil.TranslateHexColor(temp);

        if(isPapiExist && player != null && plugin.getConfig().getBoolean("UI.UsePlaceholderAPI"))
            return PlaceholderAPI.setPlaceholders(player, temp);

        return temp;
    }

    public static String dsPrefix_ = "§3DShop3 §7| §f";

    public static CustomConfig ccUser;
    public static CustomConfig ccSign;

    private BukkitTask periodicRepetitiveTask;
    private BukkitTask cullLogsTask;

    public static boolean updateAvailable = false;
    public static String lastVersion = "";
    public static String yourVersion = "";

    public static UIManager uiManager;
    public static final HashMap<UUID, String> userTempData = new HashMap<>();
    public static final HashMap<UUID, String> userInteractItem = new HashMap<>();

    public static final LocaleManager localeManager = new LocaleManager();
    public static boolean isPapiExist;

    @Override
    public void onEnable()
    {
        plugin = this;
        console = plugin.getServer().getConsoleSender();

        SetupVault();
    }

    private void Init()
    {
        CMDManager.Init();

        registerEvents();
        initCommands();

        makeFolders();
        InitConfig();

        PeriodicRepetitiveTask();
        startCullLogsTask();
        hookIntoJobs();
        InitPapi();

        // 완료
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Enabled! :)");

        CheckUpdate();
        InitBstats();
    }

    // 볼트 이코노미 초기화
    private void SetupVault()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Vault Found");
        }

        SetupRSP();
    }

    private int setupRspRetryCount = 0;
    private void SetupRSP()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null)
        {
            econ = rsp.getProvider();

            Init();
        }
        else
        {
            if(setupRspRetryCount >= 3)
            {
                console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled due to no Vault dependency found!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            setupRspRetryCount++;
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Economy provider not found. Retry... " + setupRspRetryCount + "/3");

            Bukkit.getScheduler().runTaskLater(this, this::SetupRSP, 30L);
        }
    }

    private int ConvertVersionStringToNumber(String string)
    {
        String[] temp = string.replace("-snapshot","").split("\\.");
        if(temp.length != 3)
            return 1;

        try
        {
            int ret = Integer.parseInt(temp[0]) * 10000;
            ret += Integer.parseInt(temp[1]) * 100;
            ret += Integer.parseInt(temp[2]);

            return ret;
        }
        catch (Exception e)
        {
            return 1;
        }
    }

    private void CheckUpdate()
    {
        new UpdateChecker(this, UpdateChecker.PROJECT_ID).getVersion(version ->
        {
            try
            {
                lastVersion = version;
                yourVersion = getDescription().getVersion();

                int you = ConvertVersionStringToNumber(yourVersion);
                int last = ConvertVersionStringToNumber(lastVersion);

                if (last <= you)
                {
                    DynamicShop.updateAvailable = false;
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Plugin is up to date!");
                } else
                {
                    DynamicShop.updateAvailable = true;
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Plugin outdated!");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + UpdateChecker.getResourceUrl());
                }
            } catch (Exception e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Failed to check update. Try again later.");
            }
        });
    }

    public static TextComponent CreateLink(final String text, boolean bold, ChatColor color, final String link) {
        final TextComponent component = new TextComponent(text);
        component.setBold(bold);
        component.setUnderlined(true);
        component.setColor(color);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(link).create()));
        return component;
    }

    private void InitBstats()
    {
        try
        {
            int pluginId = 4258;
            Metrics metrics = new Metrics(this, pluginId);
        } catch (Exception e)
        {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Failed to Init bstats : " + e);
        }
    }

    private void InitPapi()
    {
        isPapiExist = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if(isPapiExist)
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " PlaceholderAPI Found");
        }
        else
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " PlaceholderAPI Not Found");
        }
    }

    public void startCullLogsTask()
    {
        if (getConfig().getBoolean("Log.CullLogs"))
        {
            if (cullLogsTask != null)
            {
                cullLogsTask.cancel();
            }
            cullLogsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, LogUtil::cullLogs, 0L, (20L * 60L * (long) getConfig().getInt("Log.LogCullTimeMinutes")));
        }
    }

    public void PeriodicRepetitiveTask()
    {
        if (periodicRepetitiveTask != null)
        {
            periodicRepetitiveTask.cancel();
        }
        periodicRepetitiveTask = Bukkit.getScheduler().runTaskTimer(DynamicShop.plugin, this::RepeatAction, 100, 100); // 1000틱 = 50초/25/12.5
    }

    private int repeatTaskCount = 0;
    private void RepeatAction()
    {
        repeatTaskCount++;

        //SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy,HH.mm.ss");
        //String time = sdf.format(System.currentTimeMillis());
        //console.sendMessage(time + " / " + repeatTaskCount);

        if(repeatTaskCount == 5)
        {
            ShopUtil.randomChange(new Random());
            repeatTaskCount = 0;
        }
        UIManager.RefreshUI();
    }

    private void hookIntoJobs()
    {
        // Jobs
        if (getServer().getPluginManager().getPlugin("Jobs") == null)
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Jobs Reborn Not Found");
            JobsHook.jobsRebornActive = false;
        } else
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Jobs Reborn Found");
            JobsHook.jobsRebornActive = true;
        }
    }

    private void initCommands()
    {
        // 명령어 등록 (개별 클레스로 되어있는것들)
        getCommand("DynamicShop").setExecutor(new Root());
        getCommand("shop").setExecutor(new Optional());

        // 자동완성
        getCommand("DynamicShop").setTabCompleter(this);
        getCommand("shop").setTabCompleter(this);
    }

    private void registerEvents()
    {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new JoinQuit(), this);
        getServer().getPluginManager().registerEvents(new OnClick(), this);
        getServer().getPluginManager().registerEvents(new OnSignClick(), this);
        getServer().getPluginManager().registerEvents(new OnChat(), this);

        uiManager = new UIManager();
        getServer().getPluginManager().registerEvents(uiManager, this);
    }

    private void makeFolders()
    {
        File shopFolder = new File(getDataFolder(), "Shop");
        shopFolder.mkdir(); // new 하고 같은줄에서 바로 하면 폴더 안만들어짐.

        File LogFolder = new File(getDataFolder(), "Log");
        LogFolder.mkdir();
    }

    private void InitConfig()
    {
        LangUtil.ccLang = new CustomConfig();
        LayoutUtil.ccLayout = new CustomConfig();
        ccUser = new CustomConfig();
        StartPage.ccStartPage = new CustomConfig();
        ccSign = new CustomConfig();
        WorthUtil.ccWorth = new CustomConfig();
        SoundUtil.ccSound = new CustomConfig();
        LogUtil.ccLog = new CustomConfig();

        ShopUtil.Reload();

        ConfigUtil.configSetup(this);

        LangUtil.setupLangFile(getConfig().getString("Language"));
        LayoutUtil.Setup();

        setupUserFile();
        StartPage.setupStartPageFile();
        setupSignFile();
        WorthUtil.setupWorthFile();
        SoundUtil.setupSoundFile();
        LogUtil.setupLogFile();

        QuickSell.quickSellGui = new CustomConfig();
        QuickSell.SetupQuickSellGUIFile();

        getConfig().set("Version", configVersion);
        saveConfig();
    }

    private void setupUserFile()
    {
        ccUser.setup("User", null);
        ccUser.get().options().copyDefaults(true);

        int userVersion = getConfig().getInt("Version");
        if (userVersion < 3)
        {
            for (String s : ccUser.get().getKeys(false))
            {
                ccUser.get().getConfigurationSection(s).set("tmpString", null);
                ccUser.get().getConfigurationSection(s).set("interactItem", null);
            }
        }

        ccUser.save();
    }

    private void setupSignFile()
    {
        ccSign.setup("Sign", null);
        ccSign.get().options().copyDefaults(true);
        ccSign.save();
    }

    // 명령어 자동완성
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return TabCompleteUtil.onTabCompleteBody(this, sender, cmd, args);
    }

    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled");
    }
}
