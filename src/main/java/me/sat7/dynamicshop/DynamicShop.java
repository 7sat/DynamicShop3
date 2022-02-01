package me.sat7.dynamicshop;

import me.sat7.dynamicshop.commands.Optional;
import me.sat7.dynamicshop.commands.Root;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.events.*;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.guis.UIManager;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.*;
import net.milkbowl.vault.economy.Economy;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;
import java.util.Random;

import static me.sat7.dynamicshop.UpdateChecker.getResourceUrl;

public final class DynamicShop extends JavaPlugin implements Listener {

    private static Economy econ = null; // 볼트에 물려있는 이코노미

    public static Economy getEconomy() {
        return econ;
    }

    public static DynamicShop plugin;
    public static ConsoleCommandSender console;
    public static String dsPrefix = "§3§l[dShop] §f";

    public static CustomConfig ccUser;
    public static CustomConfig ccSign;

    private BukkitTask randomChangeTask;
    private BukkitTask cullLogsTask;

    public static boolean updateAvailable = false;

    public static UIManager uiManager;

    @Override
    public void onEnable() {
        plugin = this;
        console = plugin.getServer().getConsoleSender();
        initCustomConfigs();

        // 볼트 이코노미 셋업
        if (!setupEconomy()) {
            console.sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerEvents();
        initCommands();
        setupConfigs();
        startRandomChangeTask();

        makeFolders();
        hookIntoJobs();

        startCullLogsTask();

        // 완료
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Enabled! :)");
        new UpdateChecker(this, UpdateChecker.PROJECT_ID).getVersion(version ->
        {
            try
            {
                if(DynamicShop.plugin.getDescription().getVersion().contains("SNAPSHOT")) {
                    DynamicShop.updateAvailable = false;
                    DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +" Plugin is running a dev build!");
                    DynamicShop.console.sendMessage("Be careful and monitor what happens!");
                    DynamicShop.console.sendMessage(getResourceUrl());
                    DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                }
                if (this.getDescription().getVersion().equals(version))
                {
                    DynamicShop.updateAvailable = false;
                    DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +" Plugin is up to date!");
                    DynamicShop.console.sendMessage("Please rate my plugin if you like it");
                    DynamicShop.console.sendMessage(getResourceUrl());
                    DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                }
                else
                {
                    DynamicShop.updateAvailable = true;
                    DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +"Plugin outdated!!");
                    DynamicShop.console.sendMessage(getResourceUrl());
                    DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                }
            }
            catch (Exception e)
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +"Failed to check update. Try again later.");
            }
        });

        // bstats
        //System.setProperty("bstats.relocatecheck", "false"); // 빌드가 외부로 나갈때는 이 라인이 주석처리되야함.

        try
        {
            int pluginId = 4258;
            Metrics metrics = new Metrics(this, pluginId);
        }
        catch (Exception e)
        {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +"Failed to Init bstats : " + e);
        }
    }

    public void startCullLogsTask() {
        if (getConfig().getBoolean("CullLogs")) {
            if (cullLogsTask != null) {
                cullLogsTask.cancel();
            }
            cullLogsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, LogUtil::cullLogs, 0L, (20L * 60L * (long) getConfig().getInt("LogCullTimeMinutes")));
        }
    }

    public void startRandomChangeTask() {
        if (randomChangeTask != null) {
            randomChangeTask.cancel();
        }
        randomChangeTask = Bukkit.getScheduler().runTaskTimer(DynamicShop.plugin, () -> ConfigUtil.randomChange(new Random()), 500, 500);
    }

    private void hookIntoJobs() {
        // Jobs
        if (getServer().getPluginManager().getPlugin("Jobs") == null) {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Jobs Reborn Not Found");
            JobsHook.jobsRebornActive = false;
        } else {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Jobs Reborn Found");
            JobsHook.jobsRebornActive = true;
        }
    }

    private void makeFolders() {
        // 컨버팅용 폴더 생성
        File folder1 = new File(getDataFolder(), "Convert");
        folder1.mkdir();
        File folder2 = new File(getDataFolder(), "Convert/Shop");
        folder2.mkdir();
        File folder3 = new File(getDataFolder(), "Log");
        folder3.mkdir();
    }

    private void initCommands() {
        // 명령어 등록 (개별 클레스로 되어있는것들)
        getCommand("DynamicShop").setExecutor(new Root());
        getCommand("shop").setExecutor(new Optional());

        // 자동완성
        getCommand("DynamicShop").setTabCompleter(this);
        getCommand("shop").setTabCompleter(this);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new JoinQuit(), this);
        getServer().getPluginManager().registerEvents(new OnClick(), this);
        getServer().getPluginManager().registerEvents(new OnSignClick(), this);
        getServer().getPluginManager().registerEvents(new OnChat(), this);

        uiManager = new UIManager();
        getServer().getPluginManager().registerEvents(uiManager, this);
    }

    private void initCustomConfigs() {
        LangUtil.ccLang = new CustomConfig();
        ShopUtil.ccShop = new CustomConfig();
        ccUser = new CustomConfig();
        StartPage.ccStartPage = new CustomConfig();
        ccSign = new CustomConfig();
        WorthUtil.ccWorth = new CustomConfig();
        SoundUtil.ccSound = new CustomConfig();
        LogUtil.ccLog = new CustomConfig();
    }

    private void setupConfigs() {
        // Config 셋업 (기본형)
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        ConfigUtil.configSetup(this);

        LangUtil.setupLangFile(getConfig().getString("Language"));
        ShopUtil.setupShopFile();
        setupUserFile();
        StartPage.setupStartPageFile();
        setupSignFile();
        WorthUtil.setupWorthFile();
        SoundUtil.setupSoundFile();
        LogUtil.setupLogFile();
    }

    private void setupUserFile() {
        ccUser.setup("User", null);
        ccUser.get().options().copyDefaults(true);
        ccUser.save();
    }

    private void setupSignFile() {
        ccSign.setup("Sign", null);
        ccSign.get().options().copyDefaults(true);
        ccSign.save();
    }

    // 명령어 자동완성
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return TabCompleteUtil.onTabCompleteBody(this, sender, cmd, commandLabel, args);
    }

    // 볼트 이코노미 초기화
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Vault Not Found");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " RSP is null!");
            return false;
        }
        econ = rsp.getProvider();
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Vault Found");
        return econ != null;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled");
    }
}
