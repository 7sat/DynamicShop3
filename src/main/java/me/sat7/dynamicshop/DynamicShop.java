package me.sat7.dynamicshop;

import me.clip.placeholderapi.PlaceholderAPI;
import me.pikamug.localelib.LocaleManager;
import me.sat7.dynamicshop.commands.CMDManager;
import me.sat7.dynamicshop.commands.Optional;
import me.sat7.dynamicshop.commands.Root;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.economyhook.PlayerpointHook;
import me.sat7.dynamicshop.events.*;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.guis.QuickSell;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.guis.UIManager;
import me.sat7.dynamicshop.economyhook.JobsHook;
import me.sat7.dynamicshop.utilities.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
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

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class DynamicShop extends JavaPlugin implements Listener
{
    private static Economy econ = null; // 볼트에 물려있는 이코노미
    public static Economy getEconomy()
    {
        return econ;
    }

    public static PlayerPointsAPI ppAPI;

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

        if(ConfigUtil.GetUseHexColorCode())
            temp = LangUtil.TranslateHexColor(temp);

        if(isPapiExist && player != null && ConfigUtil.GetUsePlaceholderAPI())
            return PlaceholderAPI.setPlaceholders(player, temp);

        return temp;
    }

    public static String dsPrefix_ = "§3DShop3 §7| §f";

    public static CustomConfig ccSign = new CustomConfig();

    private BukkitTask periodicRepetitiveTask;
    private BukkitTask saveLogsTask;
    private BukkitTask cullLogsTask;
    private BukkitTask shopSaveTask;
    private BukkitTask userDataRepetitiveTask;

    public static boolean updateAvailable = false;
    public static String lastVersion = "";
    public static String yourVersion = "";

    public static UIManager uiManager;

    public static final LocaleManager localeManager = new LocaleManager();
    public static boolean isPapiExist;

    public static final boolean DEBUG_MODE = false;
    public static void DebugLog()
    {
        if(!DEBUG_MODE)
            return;

        console.sendMessage("========== DEBUG LOG ==========");

        console.sendMessage("userTempData: size: " + UserUtil.userTempData.size());
        int idx = 0;
        for(Map.Entry<UUID, String> entry : UserUtil.userTempData.entrySet())
        {
            console.sendMessage(entry.getKey() + ": " + entry.getValue());
            idx++;
            if (idx > 9)
                break;
        }

        console.sendMessage("---------------------");

        console.sendMessage("userInteractItem: size: " + UserUtil.userInteractItem.size());
        idx = 0;
        for(Map.Entry<UUID, String> entry : UserUtil.userInteractItem.entrySet())
        {
            console.sendMessage(entry.getKey() + ": " + entry.getValue());
            idx++;
            if (idx > 9)
                break;
        }

        console.sendMessage("---------------------");

        console.sendMessage("ShopUtil.shopConfigFiles: size: " + ShopUtil.shopConfigFiles.size());
        for(Map.Entry<String, CustomConfig> entry : ShopUtil.shopConfigFiles.entrySet())
            console.sendMessage(entry.getKey() + ": " + entry.getValue());

        console.sendMessage("---------------------");

        console.sendMessage("ShopUtil.ShopUtil.shopDirty: size: " + ShopUtil.shopDirty.size());
        for(Map.Entry<String, Boolean> entry : ShopUtil.shopDirty.entrySet())
            console.sendMessage(entry.getKey() + ": " + entry.getValue());

        console.sendMessage("---------------------");

        UIManager.DebugLog();

        //console.sendMessage("---------------------");

        //console.sendMessage("RotationTaskMap: size" + RotationUtil.RotationTaskMap.size());
        //for(Map.Entry<String, Integer> entry : RotationUtil.RotationTaskMap.entrySet())
        //    console.sendMessage(entry.getKey() + ": " + entry.getValue());

        console.sendMessage("---------------------");

        for (Map.Entry<String, HashMap<String, HashMap<UUID, Integer>>> entry : UserUtil.tradingVolume.entrySet())
        {
            console.sendMessage(entry.getKey());
            for (Map.Entry<String, HashMap<UUID, Integer>> entry1 : UserUtil.tradingVolume.get(entry.getKey()).entrySet())
            {
                console.sendMessage(" - " + entry1.getKey());
                for (Map.Entry<UUID, Integer> entry2 : UserUtil.tradingVolume.get(entry.getKey()).get(entry1.getKey()).entrySet())
                {
                    console.sendMessage(" --- " + entry2.getKey() + " : " + entry2.getValue());
                }
            }
        }

        console.sendMessage("========== DEBUG LOG END ==========");
    }

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
        startSaveLogsTask();
        startCullLogsTask();
        StartShopSaveTask();
        StartUserDataTask();
        hookIntoJobs();
        hookIntoPlayerPoints();
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
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " 'Vault' Found");
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
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " 'PlaceholderAPI' Found");
        }
        else
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " 'PlaceholderAPI' Not Found");
        }
    }

    public void startSaveLogsTask()
    {
        if (ConfigUtil.GetSaveLogs())
        {
            if (saveLogsTask != null)
            {
                saveLogsTask.cancel();
            }
            saveLogsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, LogUtil::SaveLogToCSV, 0L, (20L * 10L));
        }
    }

    public void startCullLogsTask()
    {
        if (ConfigUtil.GetCullLogs())
        {
            if (cullLogsTask != null)
            {
                cullLogsTask.cancel();
            }
            cullLogsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    this, LogUtil::cullLogs, 0L, (20L * 60L * (long) ConfigUtil.GetLogCullTimeMinutes())
            );
        }
    }

    public void StartUserDataTask()
    {
        if (userDataRepetitiveTask != null)
            userDataRepetitiveTask.cancel();

        userDataRepetitiveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this, UserUtil::RepetitiveTask, 0L, 20L * 60L * 60L
        );
    }

    public void PeriodicRepetitiveTask()
    {
        if (periodicRepetitiveTask != null)
        {
            periodicRepetitiveTask.cancel();
        }

        // 1000틱 = 50초 = 마인크래프트 1시간
        // 20틱 = 현실시간 1초
        periodicRepetitiveTask = Bukkit.getScheduler().runTaskTimer(DynamicShop.plugin, this::RepeatAction, 20, 20); 
    }

    private int repeatTaskCount = 0;
    private void RepeatAction()
    {
        repeatTaskCount++;

        //SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy,HH.mm.ss");
        //String time = sdf.format(System.currentTimeMillis());
        //console.sendMessage(time + " / " + repeatTaskCount);

        if (repeatTaskCount == 25) // 25초 = 500틱 = 마인크래프트 30분
        {
            ShopUtil.randomChange(new Random());
            repeatTaskCount = 0;
        }
        UIManager.RefreshUI();
    }

    public void StartShopSaveTask()
    {
        if (shopSaveTask != null)
        {
            shopSaveTask.cancel();
        }

        long interval = (20L * 10L);
        shopSaveTask = Bukkit.getScheduler().runTaskTimer(DynamicShop.plugin, ShopUtil::SaveDirtyShop, interval, interval);
    }

    private void hookIntoJobs()
    {
        // Jobs
        if (getServer().getPluginManager().getPlugin("Jobs") == null)
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " 'Jobs Reborn' Not Found");
            JobsHook.jobsRebornActive = false;
        } else
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " 'Jobs Reborn' Found");
            JobsHook.jobsRebornActive = true;
        }
    }

    private void hookIntoPlayerPoints()
    {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints"))
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " 'PlayerPoints' Found");
            ppAPI = PlayerPoints.getInstance().getAPI();
            PlayerpointHook.isPPActive = true;

        }
        else
        {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " 'PlayerPoints' Not Found");
            PlayerpointHook.isPPActive = false;
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
        UserUtil.Init();
        ShopUtil.Reload();
        ConfigUtil.Load();

        LangUtil.setupLangFile(ConfigUtil.GetLanguage());  // ConfigUtil.Load() 보다 밑에 있어야함.
        LayoutUtil.Setup();

        StartPage.setupStartPageFile();
        setupSignFile();
        WorthUtil.setupWorthFile();
        SoundUtil.setupSoundFile();

        QuickSell.quickSellGui = new CustomConfig();
        QuickSell.SetupQuickSellGUIFile();
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
        UserUtil.OnPluginDisable();
        ShopUtil.ForceSaveAllShop();

        Bukkit.getScheduler().cancelTasks(this);
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled");
    }

    public static void PaidOnlyMsg(Player p)
    {
        TextComponent text = new TextComponent("");
        text.addExtra(DynamicShop.dsPrefix(p) +  t(p, "PAID_VERSION.DESC"));
        text.addExtra(DynamicShop.CreateLink(t(p, "PAID_VERSION.GET_PREMIUM"), false, ChatColor.WHITE, "https://spigotmc.org/resources/100058"));

        p.spigot().sendMessage(text);
    }
}
