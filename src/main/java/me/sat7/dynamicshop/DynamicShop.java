package me.sat7.dynamicshop;

import me.sat7.dynamicshop.Commands.*;
import me.sat7.dynamicshop.Events.JoinQuit;
import me.sat7.dynamicshop.Events.OnChat;
import me.sat7.dynamicshop.Events.OnClick;
import me.sat7.dynamicshop.Events.OnSignClick;
import me.sat7.dynamicshop.Files.CustomConfig;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DynamicShop extends JavaPlugin implements Listener {

    private static Economy econ = null; // 볼트에 물려있는 이코노미
    public static Economy getEconomy() {
        return econ;
    }

    public static DynamicShop plugin;
    public static ConsoleCommandSender console;
    public static String dsPrefix = "§3§l[dShop] §f";
    public static String dsPrefix_server = "§3[DynamicShop]§f";

    public static CustomConfig ccLang;
    public static CustomConfig ccShop;
    public static CustomConfig ccUser;
    public static CustomConfig ccStartpage;
    public static CustomConfig ccSign;
    public static CustomConfig ccWorth;
    public static CustomConfig ccSound;
    public static CustomConfig ccLog;

    private Random generator = new Random();

    public static boolean updateAvailable = false;
    public static boolean jobsRebornActive = false;

    @Override
    public void onEnable() {
        plugin = this;
        console = plugin.getServer().getConsoleSender();
        ccLang = new CustomConfig();
        ccShop = new CustomConfig();
        ccUser = new CustomConfig();
        ccStartpage = new CustomConfig();
        ccSign = new CustomConfig();
        ccWorth = new CustomConfig();
        ccSound = new CustomConfig();
        ccLog = new CustomConfig();

        // 볼트 이코노미 셋업
        if (!setupEconomy() ) {
            console.sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 이벤트 등록
        getServer().getPluginManager().registerEvents(this,this);
        getServer().getPluginManager().registerEvents(new JoinQuit(),this);
        getServer().getPluginManager().registerEvents(new OnClick(),this);
        getServer().getPluginManager().registerEvents(new OnSignClick(),this);
        getServer().getPluginManager().registerEvents(new OnChat(),this);

        // 명령어 등록 (개별 클레스로 되어있는것들)
        getCommand("DynamicShop").setExecutor(new RootCommand());
        getCommand("shop").setExecutor(new OptionalCommand());

        // 자동완성
        getCommand("DynamicShop").setTabCompleter(this);
        getCommand("shop").setTabCompleter(this);

        // Config 셋업 (기본형)
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        ConfigSetup();

        // Config 셋업 (커스텀)
        SetupLangFile(getConfig().getString("Language"));
        SetupShopFile();
        SetupUserFile();
        SetupStartpageFile();
        SetupSignFile();
        SetupWorthFile();
        SetupSoundFile();
        SetupLogFile();

        // 컨버팅용 폴더 생성
        File folder1 = new File(getDataFolder(), "Convert");
        File folder2 = new File(getDataFolder(), "Convert/Shop");
        folder1.mkdir();
        folder2.mkdir();
        File folder3 = new File(getDataFolder(), "Log");
        folder3.mkdir();

        if (getServer().getPluginManager().getPlugin("Jobs") == null) {
            console.sendMessage(dsPrefix_server + " Jobs Reborn Not Found");
            jobsRebornActive = false;
        }
        else
        {
            console.sendMessage(dsPrefix_server + " Jobs Reborn Found");
            jobsRebornActive = true;
        }

        // 완료
        console.sendMessage(dsPrefix_server + " Enabled! :)");

        // 업데이트 확인
        UpdateCheck updater = new UpdateCheck(plugin, 65603);
        try {
            if(updater.checkForUpdates()) {
                // this will print when haves update
                updateAvailable = true;
                console.sendMessage("§3-------------------------------------------------------");
                console.sendMessage(dsPrefix_server+"Plugin outdated!!");
                console.sendMessage("https://www.spigotmc.org/resources/65603/");
                console.sendMessage("§3-------------------------------------------------------");
            }else{
                // this will print when no updates
                updateAvailable = false;
                console.sendMessage("§3-------------------------------------------------------");
                console.sendMessage(dsPrefix_server+" Plugin is up to date!");
                console.sendMessage("Please rate my plugin if you like it");
                console.sendMessage("https://www.spigotmc.org/resources/65603/");
                console.sendMessage("§3-------------------------------------------------------");
            }
        } catch (Exception e) {
            console.sendMessage(dsPrefix_server+"Failed to check update. Try again later.");
        }

        // bstats
        Metrics metrics = new Metrics(this);

        // Optional: Add custom charts
        // todo: 이거 지워야함... 그냥 지우니까 에러뜸.
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
    }

    private static int randomStockCount = 1;
    private static BukkitTask randomStocktask;
    private void StartTaskTimer()
    {
        randomStocktask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            RandomChange();

        }, 500, 500);

        //Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

    private void RandomChange()
    {
        // 인게임 30분마다 실행됨 (500틱)
        //console.sendMessage(dsPrefix_server+" fluctuating...");
        randomStockCount += 1;
        if(randomStockCount > 24)
        {
            randomStockCount = 0;
            ccShop.save();
        }

        boolean needToUpdateUI = false;

        for (String shop:ccShop.get().getKeys(false))
        {
            // fluctuation
            ConfigurationSection confSec = ccShop.get().getConfigurationSection(shop+".Options.fluctuation");
            if(confSec!=null)
            {
                int interval = confSec.getInt("interval");

                if(interval != 1 && interval != 2  && interval != 4  && interval != 8  && interval != 24)
                {
                    console.sendMessage(dsPrefix_server+" Wrong value at " + shop + ".Options.fluctuation.interval");
                    console.sendMessage(dsPrefix_server+" Reset to 2");
                    confSec.set("interval",2);
                    interval = 2;
                    ccShop.save();
                }

                if(randomStockCount % interval != 0) continue;

                for (String item:ccShop.get().getConfigurationSection(shop).getKeys(false))
                {
                    try
                    {
                        int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.
                        if(!ccShop.get().contains(shop+"."+item+".value")) continue; // 장식용은 스킵

                        int oldStock = ccShop.get().getInt(shop+"."+item+".stock");
                        if(oldStock <= 1) continue; // 무한재고에 대해서는 스킵
                        int oldMedian = ccShop.get().getInt(shop+"."+item+".median");
                        if(oldMedian <= 1) continue; // 고정가 상품에 대해서는 스킵

                        boolean dir = generator.nextBoolean();
                        float amount = generator.nextFloat() * confSec.getInt("strength");
                        if(dir) amount *= -1;

                        oldStock += oldMedian * (amount/100.0);

                        if(oldStock < 2) oldStock = 2;

                        ccShop.get().set(shop+"."+item+".stock", oldStock);
                        needToUpdateUI = true;
                    }
                    catch (Exception ignored){}
                }
            }

            // stock stabilizing
            ConfigurationSection confSec2 = ccShop.get().getConfigurationSection(shop+".Options.stockStabilizing");
            if(confSec2!=null)
            {
                int interval = confSec2.getInt("interval");

                if(interval != 1 && interval != 2  && interval != 4  && interval != 8  && interval != 24)
                {
                    console.sendMessage(dsPrefix_server+" Wrong value at " + shop + ".Options.stockStabilizing.interval");
                    console.sendMessage(dsPrefix_server+" Reset to 24");
                    confSec2.set("interval",24);
                    interval = 24;
                    ccShop.save();
                }

                if(randomStockCount % interval != 0) continue;

                for (String item:ccShop.get().getConfigurationSection(shop).getKeys(false))
                {
                    try
                    {
                        int i = Integer.parseInt(item); // options에 대해 적용하지 않기 위해.
                        if(!ccShop.get().contains(shop+"."+item+".value")) continue; // 장식용은 스킵

                        int oldStock = ccShop.get().getInt(shop+"."+item+".stock");
                        if(oldStock < 1) continue; // 무한재고에 대해서는 스킵
                        int oldMedian = ccShop.get().getInt(shop+"."+item+".median");
                        if(oldMedian < 1) continue; // 고정가 상품에 대해서는 스킵

                        double amount = oldMedian * (confSec2.getDouble("strength")/100.0);
                        if(oldStock < oldMedian)
                        {
                            oldStock += (int)(amount);
                            if(oldStock > oldMedian) oldStock = oldMedian;
                        }
                        else if(oldStock > oldMedian)
                        {
                            oldStock -= (int)(amount);
                            if(oldStock < oldMedian) oldStock = oldMedian;
                        }

                        ccShop.get().set(shop+"."+item+".stock", oldStock);
                        needToUpdateUI = true;
                    }
                    catch (Exception e)
                    {
//                        for (StackTraceElement ste:e.getStackTrace()) {
//                            console.sendMessage(ste.toString());
//                        }
                    }
                }
            }
        }

        if(needToUpdateUI)
        {
            for(Player p : plugin.getServer().getOnlinePlayers()) {
                if(p.getOpenInventory().getTitle().equalsIgnoreCase(ccLang.get().getString("TRADE_TITLE")))
                {
                    String[] temp = DynamicShop.ccUser.get().getString(p.getUniqueId()+".interactItem").split("/");
                    DynaShopAPI.OpenItemTradeInven(p,temp[0],temp[1]);
                }
            }
        }
    }

    public void ConfigSetup()
    {
        getConfig().options().copyHeader(true);
        getConfig().options().header(
                "Language: ex) en-US,ko-KR" + "\nPrefix: Prefix of plugin messages" + "\nSalesTax: 2~99%"
                + "\nUseShopCommand: Set this to false if you want to disable /shop command"
                + "\nDefaultShopName: This shop will open when player run /shop or /ds shop command"
                + "\nDisplayStockAsStack: ex) true: 10Stacks, false: 640"
                + "\nDeliveryChargeScale: 0.01~"
                + "\nNumberOfPlayer: This number is used to calculate the recommended median. 3~100"
        );

        double salesTax;
        if(getConfig().contains("SaleTax"))
        {
            salesTax = getConfig().getDouble("SaleTax");
            getConfig().set("SaleTax",null);
        }
        else
        {
            salesTax = getConfig().getDouble("SalesTax");
        }
        if(salesTax <= 2) salesTax = 2;
        if(salesTax > 99) salesTax = 99;
        getConfig().set("SalesTax",salesTax);

        getConfig().set("Language",getConfig().get("Language"));
        getConfig().set("Prefix",getConfig().get("Prefix"));
        dsPrefix = getConfig().getString("Prefix");
        getConfig().set("UseShopCommand",getConfig().getBoolean("UseShopCommand"));
        getConfig().set("DefaultShopName",getConfig().getString("DefaultShopName"));

        try {
            Bukkit.getScheduler().cancelTask(randomStocktask.getTaskId());
        }
        catch (Exception e)
        {
            //console.sendMessage(dsPrefix_server+" Task Not found.");
        }

        double DeliveryChargeScale = getConfig().getDouble("DeliveryChargeScale");
        if(DeliveryChargeScale <= 0.01) DeliveryChargeScale = 0.01;
        getConfig().set("DeliveryChargeScale",DeliveryChargeScale);

        getConfig().set("DisplayStockAsStack",getConfig().getBoolean("DisplayStockAsStack"));

        int numPlayer = getConfig().getInt("NumberOfPlayer");
        if(numPlayer <= 3) numPlayer = 3;
        if(numPlayer > 100) numPlayer = 100;
        getConfig().set("NumberOfPlayer",numPlayer);

        getConfig().set("OnClickCloseButton_OpenStartPage",getConfig().getBoolean("OnClickCloseButton_OpenStartPage"));
        getConfig().set("OpenStartPageInsteadOfDefaultShop",getConfig().getBoolean("OpenStartPageInsteadOfDefaultShop"));

        saveConfig();

        StartTaskTimer();
    }

    public void SetupLangFile(String lang)
    {
        // 한국어
        {
            ccLang.setup("Lang_v2_ko-KR",null);
            ccLang.get().addDefault("STARTPAGE.EDITOR_TITLE", "§3시작 화면 편집");
            ccLang.get().addDefault("STARTPAGE.EDIT_NAME", "이름 바꾸기");
            ccLang.get().addDefault("STARTPAGE.EDIT_LORE", "설명 바꾸기");
            ccLang.get().addDefault("STARTPAGE.EDIT_ICON", "아이콘 바꾸기");
            ccLang.get().addDefault("STARTPAGE.EDIT_ACTION", "실행 명령어 바꾸기");
            ccLang.get().addDefault("STARTPAGE.SHOP_SHORTCUT", "상점으로 가는 버튼 만들기");
            ccLang.get().addDefault("STARTPAGE.CREATE_DECO", "장식 버튼 만들기");
            ccLang.get().addDefault("STARTPAGE.ENTER_SHOPNAME", "상점 이름을 입력하세요.");
            ccLang.get().addDefault("STARTPAGE.ENTER_NAME", "버튼의 새 이름을 입력하세요.");
            ccLang.get().addDefault("STARTPAGE.ENTER_LORE", "버튼의 새 설명을 입력하세요.");
            ccLang.get().addDefault("STARTPAGE.ENTER_ICON", "버튼의 아이콘으로 사용할 아이탬 이름을 입력하세요. (영문. 대소문자 구분없음)");
            ccLang.get().addDefault("STARTPAGE.ENTER_ACTION", "명령어를 '/' 제외하고 입력하세요. 버튼을 눌렀을때 이 명령어가 실행됩니다.");
            ccLang.get().addDefault("STARTPAGE.ENTER_COLOR", "장식 버튼의 색상을 입력하세요. (영문)");
            ccLang.get().addDefault("STARTPAGE.DEFAULT_SHOP_LORE", "§f상점으로 가기");

            ccLang.get().addDefault("TRADE_TITLE", "§3아이탬 거래");
            ccLang.get().addDefault("PALETTE_TITLE", "§3판매할 아이탬 선택");
            ccLang.get().addDefault("PALETTE_LORE", "§e좌클릭: 이 아이탬을 상점에 등록");
            ccLang.get().addDefault("ITEM_SETTING_TITLE", "§3아이탬 셋팅");
            ccLang.get().addDefault("QUICKSELL_TITLE", "§3빠른 판매");
            ccLang.get().addDefault("TRADE_LORE", "§f클릭: 거래화면");
            ccLang.get().addDefault("BUY", "§c구매");
            ccLang.get().addDefault("BUYONLY_LORE", "§f구매만 가능한 아이탬");
            ccLang.get().addDefault("SELL", "§2판매");
            ccLang.get().addDefault("SELLONLY_LORE", "§f판매만 가능한 아이탬");
            ccLang.get().addDefault("VALUE_BUY", "§f구매가치: ");
            ccLang.get().addDefault("VALUE_SELL", "§f판매가치: ");
            ccLang.get().addDefault("PRICE", "§f구매: ");
            ccLang.get().addDefault("SELLPRICE", "§f판매: ");
            ccLang.get().addDefault("PRICE_MIN", "§f최소 가격: ");
            ccLang.get().addDefault("PRICE_MAX", "§f최대 가격: ");
            ccLang.get().addDefault("MEDIAN", "§f중앙값: ");
            ccLang.get().addDefault("MEDIAN_HELP", "§f중앙값이 작을수록 가격이 급격이 변화합니다.");
            ccLang.get().addDefault("STOCK", "§f재고: ");
            ccLang.get().addDefault("INFSTOCK", "무한 재고");
            ccLang.get().addDefault("STATICPRICE", "고정 가격");
            ccLang.get().addDefault("UNLIMITED", "무제한");
            ccLang.get().addDefault("TAXIGNORED", "판매세 설정이 무시됩니다.");
            ccLang.get().addDefault("TOGGLE_SELLABLE", "§e클릭: 판매전용 토글");
            ccLang.get().addDefault("TOGGLE_BUYABLE", "§e클릭: 구매전용 토글");
            ccLang.get().addDefault("BALANCE", "§3내 잔액");
            ccLang.get().addDefault("ITEM_MOVE_LORE", "§e우클릭: 이동");
            ccLang.get().addDefault("ITEM_COPY_LORE", "§e우클릭: 복사");
            ccLang.get().addDefault("ITEM_EDIT_LORE", "§eShift우클릭: 편집");
            ccLang.get().addDefault("DECO_CREATE_LORE", "§e우클릭: 장식 버튼으로 추가");
            ccLang.get().addDefault("DECO_DELETE_LORE", "§eShift우클릭: 삭제");
            ccLang.get().addDefault("RECOMMEND", "§f추천 값 적용");
            ccLang.get().addDefault("RECOMMEND_LORE", "§f가격, 중앙값, 재고를 자동으로 설정합니다.");
            ccLang.get().addDefault("DONE", "§f완료");
            ccLang.get().addDefault("DONE_LORE", "§f완료!");
            ccLang.get().addDefault("ROUNDDOWN", "§f내림");
            ccLang.get().addDefault("SETTOMEDIAN", "§f중앙값에 맞춤");
            ccLang.get().addDefault("SETTOSTOCK", "§f재고에 맞춤");
            ccLang.get().addDefault("SETTOVALUE", "§f가격에 맞춤");
            ccLang.get().addDefault("SAVE_CLOSE", "§f저장 후 닫기");
            ccLang.get().addDefault("CLOSE", "§f닫기");
            ccLang.get().addDefault("CLOSE_LORE", "§f이 창을 닫습니다.");
            ccLang.get().addDefault("REMOVE", "§f제거");
            ccLang.get().addDefault("REMOVE_LORE", "§f이 아이탬을 상점에서 제거합니다.");
            ccLang.get().addDefault("PAGE", "§f페이지");
            ccLang.get().addDefault("PAGE_LORE", "§f좌클릭: 이전페이지 / 우클릭: 다음페이지");
            ccLang.get().addDefault("PAGE_INSERT", "§eShift+좌: 페이지 삽입");
            ccLang.get().addDefault("PAGE_DELETE", "§eShift+우: 페이지 §c삭제");
            ccLang.get().addDefault("ITEM_MOVE_SELECTED", "아이탬 선택됨. 비어있는 칸을 우클릭하면 이동합니다.");
            ccLang.get().addDefault("SHOP_SETTING_TITLE", "§3상점 설정");
            ccLang.get().addDefault("SHOP_INFO", "§3상점 정보");
            ccLang.get().addDefault("PERMISSION", "§f퍼미션");
            ccLang.get().addDefault("CUR_STATE", "현재상태");
            ccLang.get().addDefault("CLICK", "클릭");
            ccLang.get().addDefault("MAXPAGE", "§f최대 페이지");
            ccLang.get().addDefault("MAXPAGE_LORE", "§f상점의 최대 페이지를 설정합니다");
            ccLang.get().addDefault("L_R_SHIFT", "§e좌: -1 우: +1 Shift: x5");
            ccLang.get().addDefault("FLAG", "§f플래그");
            ccLang.get().addDefault("RMB_EDIT", "§e우클릭: 편집");
            ccLang.get().addDefault("SIGNSHOP_LORE", "§f표지판을 통해서만 접근할 수 있습니다.");
            ccLang.get().addDefault("LOCALSHOP_LORE", "§f실제 상점 위치를 방문해야 합니다.");
            ccLang.get().addDefault("LOCALSHOP_LORE2", "§f상점의 위치를 설정해야만 합니다.");
            ccLang.get().addDefault("DELIVERYCHARG_LORE", "§f배달비를 지불하고 localshop에서 원격으로 거래합니다.");
            ccLang.get().addDefault("JOBPOINT_LORE", "§fJobs 플러그인의 job point로 거래합니다.");
            ccLang.get().addDefault("SEARCH", "§f찾기");
            ccLang.get().addDefault("SEARCH_ITEM", "§f찾으려는 아이템의 이름을 입력하세요.");
            ccLang.get().addDefault("SEARCH_CANCELED", "§f검색 취소됨.");
            ccLang.get().addDefault("INPUT_CANCELED", "§f입력 취소됨.");
            ccLang.get().addDefault("ADDALL", "§f모두 추가");
            ccLang.get().addDefault("RUSURE", "§f정말로 페이지를 삭제할까요? 'delete' 를 입력하면 삭제합니다.");
            ccLang.get().addDefault("CANT_DELETE_LAST_PAGE", "§f마지막 남은 페이지를 삭제할 수 없습니다.");
            ccLang.get().addDefault("SHOP_BAL_INF", "§f상점 계좌 무제한");
            ccLang.get().addDefault("SHOP_BAL", "§f상점 계좌 잔액");
            ccLang.get().addDefault("SHOP_BAL_LOW", "§f상점이 돈을 충분히 가지고 있지 않습니다.");
            ccLang.get().addDefault("ON","켜짐");
            ccLang.get().addDefault("OFF","꺼짐");
            ccLang.get().addDefault("SET","설정");
            ccLang.get().addDefault("UNSET","설정해제");
            ccLang.get().addDefault("NULL(OPEN)","없음 (모두에게 열려있음)");

            ccLang.get().addDefault("LOG.LOG", "§f로그");
            ccLang.get().addDefault("LOG.CLEAR", "§f로그 삭제됨");
            ccLang.get().addDefault("LOG.SAVE", "§f로그 저장됨");
            ccLang.get().addDefault("LOG.DELETE", "§f로그 삭제");

            ccLang.get().addDefault("SHOP_CREATED", "§f상점 생성됨!");
            ccLang.get().addDefault("SHOP_DELETED", "§f상점 제거됨!");
            ccLang.get().addDefault("POSITION", "§f위치: ");
            ccLang.get().addDefault("SHOP_LIST", "§f상점 목록");

            ccLang.get().addDefault("TIME.OPEN", "Open");
            ccLang.get().addDefault("TIME.CLOSE", "Close");
            ccLang.get().addDefault("TIME.OPEN_LORE", "§f문 여는 시간 설정");
            ccLang.get().addDefault("TIME.CLOSE_LORE", "§f문 닫는 시간 설정");
            ccLang.get().addDefault("TIME.SHOPHOURS", "§f영업시간");
            ccLang.get().addDefault("TIME.OPEN24", "24시간 오픈");
            ccLang.get().addDefault("TIME.SHOP_IS_CLOSED", "§f상점이 문을 닫았습니다. 개점: {time}시. 현재시간: {curTime}시");
            ccLang.get().addDefault("TIME.SET_SHOPHOURS", "영업시간 설정");
            ccLang.get().addDefault("TIME.CUR", "§f현재 시간: {time}시");

            ccLang.get().addDefault("STOCKSTABILIZING.SS", "§f재고 안정화");
            ccLang.get().addDefault("STOCKSTABILIZING.L_R_SHIFT", "§e좌클릭: -0.1 우클릭: +0.1 Shift: x5");

            ccLang.get().addDefault("FLUC.FLUCTUATION", "§f무작위 재고 변동");
            ccLang.get().addDefault("FLUC.INTERVAL", "§f변화 간격");
            ccLang.get().addDefault("FLUC.STRENGTH", "§f변화 강도");

            ccLang.get().addDefault("TAX.SALESTAX", "§f판매세");
            ccLang.get().addDefault("TAX.USE_GLOBAL", "전역설정 사용 ({tax}%)");
            ccLang.get().addDefault("TAX.USE_LOCAL", "별도 설정");

            ccLang.get().addDefault("OUT_OF_STOCK", "§f재고 없음!");
            ccLang.get().addDefault("BUY_SUCCESS", "§f{item} {amount}개를 {price}에 구매함. 잔액: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS", "§f{item} {amount}개를 {price}에 판매함. 잔액: {bal}");
            ccLang.get().addDefault("BUY_SUCCESS_JP", "§f{item} {amount}개를 {price}포인트에 구매함. 남은포인트: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS_JP", "§f{item} {amount}개를 {price}포인트에 판매함. 남은포인트: {bal}");
            ccLang.get().addDefault("QSELL_RESULT", "§f거래한 상점: ");
            ccLang.get().addDefault("QSELL_NA", "§f해당 아이탬을 취급하는 상점이 없습니다.");
            ccLang.get().addDefault("DELIVERYCHARGE", "§f배달비: {fee}");
            ccLang.get().addDefault("DELIVERYCHARGE_EXEMPTION", "§f배달비: {fee} ({fee2} 면제됨)");
            ccLang.get().addDefault("DELIVERYCHARGE_NA", "§f다른 월드로 배달할 수 없습니다.");
            ccLang.get().addDefault("NOT_ENOUGH_MONEY", "§f돈이 부족합니다. 잔액: {bal}");
            ccLang.get().addDefault("NOT_ENOUGH_POINT", "§f포인트가 부족합니다. 잔액: {bal}");
            ccLang.get().addDefault("NO_ITEM_TO_SELL", "§f판매 할 아이탬이 없습니다.");
            ccLang.get().addDefault("INVEN_FULL", "§4인벤토리에 빈 공간이 없습니다!");
            ccLang.get().addDefault("IRREVERSIBLE", "§f이 행동은 되돌릴 수 없습니다!");

            ccLang.get().addDefault("HELP.TITLE", "§f도움말: {command} --------------------");
            ccLang.get().addDefault("HELP.SHOP", "상점을 엽니다.");
            ccLang.get().addDefault("HELP.CMD", "명령어 도움말 표시 토글.");
            ccLang.get().addDefault("HELP.CREATESHOP", "상점을 새로 만듭니다.");
            ccLang.get().addDefault("HELP.DELETESHOP", "기존의 상점을 제거합니다.");
            ccLang.get().addDefault("HELP.SETTAX", "판매 세금을 설정합니다.");
            ccLang.get().addDefault("HELP.SHOPADDHAND", "손에 들고 있는 아이탬을 상점에 추가합니다.");
            ccLang.get().addDefault("HELP.SHOPADDITEM", "상점에 아이탬을 추가합니다.");
            ccLang.get().addDefault("HELP.SHOPEDIT", "상점에 있는 아이탬을 수정합니다.");
            ccLang.get().addDefault("HELP.PRICE", "§7가격은 다음과 같이 계산됩니다: median*value/stock");
            ccLang.get().addDefault("HELP.INF_STATIC", "§7median<0 == 고정가격     stock<0 == 무한재고");
            ccLang.get().addDefault("HELP.EDITALL", "상점의 모든 아이탬을 한번에 수정합니다.");
            ccLang.get().addDefault("HELP.EDITALL2", "§c주의. 값이 유효한지는 확인하지 않음.");
            ccLang.get().addDefault("HELP.RELOAD", "플러그인을 재시작 합니다.");
            ccLang.get().addDefault("HELP.RELOADED", "플러그인 리로드됨!");
            ccLang.get().addDefault("HELP.USAGE", "사용법");
            ccLang.get().addDefault("HELP.CREATESHOP2", "퍼미션(나중에 바꿀 수 있습니다.)\n   true: dshop.user.shop.상점이름\n   false: 아무나 접근가능(기본값)\n   임의 입력: 해당 퍼미션 필요");
            ccLang.get().addDefault("HELP.ITEM_ALREADY_EXIST", "§7§o{item}(은)는 이미 판매중임.\n   {info}\n   명령어를 입력하면 값이 수정됩니다.");
            ccLang.get().addDefault("HELP.ITEM_INFO", "§7§o{item}의 현재 설정:\n   {info}");
            ccLang.get().addDefault("HELP.REMOVE_ITEM", "§f§o인자를 0으로 입력하면 이 아이탬을 상점에서 §4제거§f합니다.");
            ccLang.get().addDefault("HELP.QSELL", "§f빠르게 아이탬을 판매합니다.");
            ccLang.get().addDefault("HELP.DELETE_OLD_USER", "장기간 접속하지 않은 유저의 데이터를 삭제합니다.");
            ccLang.get().addDefault("HELP.CONVERT", "다른 상점 플러그인의 정보를 변환합니다.");
            ccLang.get().addDefault("HELP.ACCOUNT", "상점의 계좌 잔액을 설정합니다. -1 = 무제한");

            ccLang.get().addDefault("ITEM_ADDED", "아이탬 추가됨!");
            ccLang.get().addDefault("ITEM_UPDATED", "아이탬 수정됨!");
            ccLang.get().addDefault("ITEM_DELETED", "아이탬 제거됨!");
            ccLang.get().addDefault("CHANGES_APPLIED", "변경사항 적용됨. 새로운 값: ");
            ccLang.get().addDefault("RECOMMAND_APPLIED", "추천 값 적용됨. {playerNum}명 기준입니다. config파일에서 이 값을 바꿀 수 있습니다.");
            ccLang.get().addDefault("TRANSFER_SUCCESS", "송금 완료");

            ccLang.get().addDefault("ERR.NO_USER_ID", "§6플레이어 uuid를 찾을 수 없습니다. 상점 이용 불가능.");
            ccLang.get().addDefault("ERR.ITEM_NOT_EXIST", "상점에 해당 아이탬이 존재하지 않습니다.");
            ccLang.get().addDefault("ERR.ITEM_FORBIDDEN", "사용할 수 없는 아이탬 입니다.");
            ccLang.get().addDefault("ERR.NO_PERMISSION", "§e권한이 없습니다.");
            ccLang.get().addDefault("ERR.WRONG_USAGE", "잘못된 명령어 사용법. 도움말을 확인하세요.");
            ccLang.get().addDefault("ERR.NO_EMPTY_SLOT", "상점에 빈 공간이 없습니다.");
            ccLang.get().addDefault("ERR.WRONG_DATATYPE", "인자의 유형이 잘못 입력되었습니다.");
            ccLang.get().addDefault("ERR.VALUE_ZERO", "인자값이 0보다 커야 합니다.");
            ccLang.get().addDefault("ERR.WRONG_ITEMNAME", "유효하지 않은 아이탬 이름입니다.");
            ccLang.get().addDefault("ERR.HAND_EMPTY", "아이탬을 손에 들고 있어야 합니다.");
            ccLang.get().addDefault("ERR.HAND_EMPTY2", "§c§o아이탬을 손에 들고 있어야 합니다!");
            ccLang.get().addDefault("ERR.SHOP_NOT_FOUND", "§f해당 상점을 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_EXIST", "해당 이름을 가진 상점이 이미 존재합니다.");
            ccLang.get().addDefault("ERR.SIGNSHOP_REMOTE_ACCESS", "해당 상점은 표지판을 통해서만 접근할 수 있습니다.");
            ccLang.get().addDefault("ERR.LOCALSHOP_REMOTE_ACCESS", "해당 상점은 직접 방문해야만 사용할 수 있습니다.");
            ccLang.get().addDefault("ERR.MAX_LOWER_THAN_MIN", "최대 가격은 최소 가격보다 커야합니다.");
            ccLang.get().addDefault("ERR.DEFAULT_VALUE_OUT_OF_RANGE", "기본 가격은 최소 가격과 최대 가격 사이의 값이어야 합니다.");
            ccLang.get().addDefault("ERR.NO_RECOMMAND_DATA", "Worth.yml 파일에 이 아이탬의 정보가 없습니다. 추천값 사용 불가.");
            ccLang.get().addDefault("ERR.JOBSREBORN_NOT_FOUND", "Jobs reborn 플러그인을 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_HAS_INF_BAL", "{shop} 상점은 무한계좌 상점입니다.");
            ccLang.get().addDefault("ERR.SHOP_DIFF_CURRENCY", "두 상점이 서로 다른 통화를 사용합니다.");
            ccLang.get().addDefault("ERR.PLAYER_NOT_EXIST", "해당 플레이어를 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_LINK_FAIL", "상점 둘 중 하나는 실제 계좌이어야 합니다.");
            ccLang.get().addDefault("ERR.SHOP_LINK_TARGET_ERR", "목표 상점은 실제 계좌를 가지고 있어야 합니다.");
            ccLang.get().addDefault("ERR.NESTED_STRUCTURE", "계층 구조를 이루는것은 금지되어 있습니다. (ex. aa-bb, bb-cc)");
            ccLang.get().addDefault("ERR.CREATIVE","§eCreative mode 에서 이 명령어를 사용할 수 없습니다. 권한이 없습니다.");

            ccLang.get().options().copyDefaults(true);
            ccLang.save();
        }

        // 영어
        {
            ccLang.setup("Lang_v2_en-US",null);
            ccLang.get().addDefault("STARTPAGE.EDITOR_TITLE", "§3Start page editor");
            ccLang.get().addDefault("STARTPAGE.EDIT_NAME", "Change Name");
            ccLang.get().addDefault("STARTPAGE.EDIT_LORE", "Change Lore");
            ccLang.get().addDefault("STARTPAGE.EDIT_ICON", "Change Icon");
            ccLang.get().addDefault("STARTPAGE.EDIT_ACTION", "Change Action");
            ccLang.get().addDefault("STARTPAGE.SHOP_SHORTCUT", "Create shortcut button for shop");
            ccLang.get().addDefault("STARTPAGE.CREATE_DECO", "Create decorative button");
            ccLang.get().addDefault("STARTPAGE.ENTER_SHOPNAME", "Enter shop name.");
            ccLang.get().addDefault("STARTPAGE.ENTER_NAME", "Enter new name");
            ccLang.get().addDefault("STARTPAGE.ENTER_LORE", "Enter new lore");
            ccLang.get().addDefault("STARTPAGE.ENTER_ICON", "Enter new Icon (Minecraft material name. Case insensitive)");
            ccLang.get().addDefault("STARTPAGE.ENTER_ACTION", "Enter Command without '/'. This command will be execute when button pressed.");
            ccLang.get().addDefault("STARTPAGE.ENTER_COLOR", "Enter color. (ex.LIGHT_BLUE)");
            ccLang.get().addDefault("STARTPAGE.DEFAULT_SHOP_LORE", "§fGo to shop");

            ccLang.get().addDefault("TRADE_TITLE", "§3Tradeview");
            ccLang.get().addDefault("PALETTE_TITLE", "§3Item Palette");
            ccLang.get().addDefault("PALETTE_LORE", "§eLMB: Register this item on shop");
            ccLang.get().addDefault("ITEM_SETTING_TITLE", "§3Item Settings");
            ccLang.get().addDefault("QUICKSELL_TITLE", "§3Quick Sell");
            ccLang.get().addDefault("TRADE_LORE", "§fClick: Go to Tradeview");
            ccLang.get().addDefault("BUY", "§cBuy");
            ccLang.get().addDefault("BUYONLY_LORE", "§fThis item is Buy only");
            ccLang.get().addDefault("SELL", "§2Sell");
            ccLang.get().addDefault("SELLONLY_LORE", "§fThis item is Sell only");
            ccLang.get().addDefault("PRICE", "§fBuy: ");
            ccLang.get().addDefault("SELLPRICE", "§fSell: ");
            ccLang.get().addDefault("VALUE_BUY", "§fValue(Buy): ");
            ccLang.get().addDefault("VALUE_SELL", "§fValue(Sell): ");
            ccLang.get().addDefault("PRICE_MIN", "§fMin Price: ");
            ccLang.get().addDefault("PRICE_MAX", "§fMax Price: ");
            ccLang.get().addDefault("MEDIAN", "§fMedian: ");
            ccLang.get().addDefault("MEDIAN_HELP", "§fThe larger the median value, the more rapidly the price changes.");
            ccLang.get().addDefault("STOCK", "§fStock: ");
            ccLang.get().addDefault("INFSTOCK", "Infinite stock");
            ccLang.get().addDefault("STATICPRICE", "Static price");
            ccLang.get().addDefault("UNLIMITED", "Unlimited");
            ccLang.get().addDefault("TAXIGNORED", "Sales tax will be ignored.");
            ccLang.get().addDefault("TOGGLE_SELLABLE", "§eClick: Toggle Sellable");
            ccLang.get().addDefault("TOGGLE_BUYABLE", "§eClick: Toggle Buyable");
            ccLang.get().addDefault("BALANCE", "§3Balance");
            ccLang.get().addDefault("ITEM_MOVE_LORE", "§eRMB: Move");
            ccLang.get().addDefault("ITEM_COPY_LORE", "§eRMB: Copy");
            ccLang.get().addDefault("ITEM_EDIT_LORE", "§eShift+RMB: Edit");
            ccLang.get().addDefault("DECO_CREATE_LORE", "§eRMB: Add as decoration");
            ccLang.get().addDefault("DECO_DELETE_LORE", "§eShift + RMB: Delete");
            ccLang.get().addDefault("RECOMMEND", "§fUse recommended value");
            ccLang.get().addDefault("RECOMMEND_LORE", "§fAutomatically set values");
            ccLang.get().addDefault("DONE", "§fDone");
            ccLang.get().addDefault("DONE_LORE", "§fDone!");
            ccLang.get().addDefault("ROUNDDOWN", "§fRound down");
            ccLang.get().addDefault("SETTOMEDIAN", "§fSet to median");
            ccLang.get().addDefault("SETTOSTOCK", "§fSet to stock");
            ccLang.get().addDefault("SETTOVALUE", "§fSet to value");
            ccLang.get().addDefault("SAVE_CLOSE", "§fSave and close");
            ccLang.get().addDefault("CLOSE", "§fClose");
            ccLang.get().addDefault("CLOSE_LORE", "§fClose this window.");
            ccLang.get().addDefault("REMOVE", "§fRemove");
            ccLang.get().addDefault("REMOVE_LORE", "§fRemove this item from shop.");
            ccLang.get().addDefault("PAGE", "§fPage");
            ccLang.get().addDefault("PAGE_LORE", "§fLMB: Previous / RMB: Next");
            ccLang.get().addDefault("PAGE_INSERT", "§eShift+L: Insert page");
            ccLang.get().addDefault("PAGE_DELETE", "§eShift+R: §cDelete page");
            ccLang.get().addDefault("ITEM_MOVE_SELECTED", "Item selected. Right click on empty space.");
            ccLang.get().addDefault("SEARCH", "§fSearch");
            ccLang.get().addDefault("SEARCH_ITEM", "§fPlease enter the name of the item you are looking for.");
            ccLang.get().addDefault("SEARCH_CANCELED", "§fSearch canceled");
            ccLang.get().addDefault("INPUT_CANCELED", "§fInput canceled");
            ccLang.get().addDefault("ADDALL", "§fAdd all");
            ccLang.get().addDefault("RUSURE", "§fAre you sure? Type 'delete' to confirm.");
            ccLang.get().addDefault("CANT_DELETE_LAST_PAGE", "§fYou can't delete last page.");
            ccLang.get().addDefault("SHOP_BAL_INF", "§fUnlimited balance");
            ccLang.get().addDefault("SHOP_BAL", "§fShop balance");
            ccLang.get().addDefault("SHOP_BAL_LOW", "§fShop does not have enough money.");
            ccLang.get().addDefault("ON","On");
            ccLang.get().addDefault("OFF","Off");
            ccLang.get().addDefault("SET","Set");
            ccLang.get().addDefault("UNSET","Unset");
            ccLang.get().addDefault("NULL(OPEN)","null (Open for everyone)");

            ccLang.get().addDefault("LOG.LOG", "§fLog");
            ccLang.get().addDefault("LOG.CLEAR", "§fLog deleted");
            ccLang.get().addDefault("LOG.SAVE", "§fLog saved");
            ccLang.get().addDefault("LOG.DELETE", "§fDelete Log");

            ccLang.get().addDefault("SHOP_SETTING_TITLE", "§3Shop Settings");
            ccLang.get().addDefault("SHOP_INFO", "§3Shop Info");
            ccLang.get().addDefault("PERMISSION", "§fPermission");
            ccLang.get().addDefault("CUR_STATE", "Current");
            ccLang.get().addDefault("CLICK", "Click");
            ccLang.get().addDefault("MAXPAGE", "§fMax Page");
            ccLang.get().addDefault("MAXPAGE_LORE", "§fSet maximum number of pages");
            ccLang.get().addDefault("L_R_SHIFT", "§eLMB: -1 RMB: +1 Shift: x5");
            ccLang.get().addDefault("FLAG", "§fFlag");
            ccLang.get().addDefault("RMB_EDIT", "§eRMB: Edit");
            ccLang.get().addDefault("SIGNSHOP_LORE", "§fThis shop is only accessible from the sign.");
            ccLang.get().addDefault("LOCALSHOP_LORE", "§fPlayer must visit the actual location of the store.");
            ccLang.get().addDefault("LOCALSHOP_LORE2", "§fThis flag requires a position value to work.");
            ccLang.get().addDefault("DELIVERYCHARG_LORE", "§fPay delivery charge, Buy items from a distance.");
            ccLang.get().addDefault("JOBPOINT_LORE", "§fJobs Reborn point shop.");

            ccLang.get().addDefault("SHOP_CREATED", "§fShop Created!");
            ccLang.get().addDefault("SHOP_DELETED", "§fShop Deleted!");
            ccLang.get().addDefault("POSITION", "§fPosition: ");
            ccLang.get().addDefault("SHOP_LIST", "§fShop list");

            ccLang.get().addDefault("TIME.OPEN", "Open");
            ccLang.get().addDefault("TIME.CLOSE", "Close");
            ccLang.get().addDefault("TIME.OPEN_LORE", "§fSet Open time");
            ccLang.get().addDefault("TIME.CLOSE_LORE", "§fSet Close time");
            ccLang.get().addDefault("TIME.SHOPHOURS", "§fShop hours");
            ccLang.get().addDefault("TIME.OPEN24", "Open 24 Hours");
            ccLang.get().addDefault("TIME.SHOP_IS_CLOSED", "§fShop is closed. Open: {time}h. Current Time: {curTime}h");
            ccLang.get().addDefault("TIME.SET_SHOPHOURS", "Set shop hours");
            ccLang.get().addDefault("TIME.CUR", "§fCurrent Time: {time}h");

            ccLang.get().addDefault("FLUC.FLUCTUATION", "Random Stock Fluctuation");
            ccLang.get().addDefault("FLUC.INTERVAL", "Interval");
            ccLang.get().addDefault("FLUC.STRENGTH", "Strength");

            ccLang.get().addDefault("STOCKSTABILIZING.SS", "§fStock Stabilizing");
            ccLang.get().addDefault("STOCKSTABILIZING.L_R_SHIFT", "§eLMB: -0.1 RMB: +0.1 Shift: x5");

            ccLang.get().addDefault("TAX.SALESTAX", "§fSales tax");
            ccLang.get().addDefault("TAX.USE_GLOBAL", "Use global setting ({tax}%)");
            ccLang.get().addDefault("TAX.USE_LOCAL", "Separate setting");

            ccLang.get().addDefault("OUT_OF_STOCK", "§fOut of stock!");
            ccLang.get().addDefault("BUY_SUCCESS", "§fBought {item} x{amount} for {price}. Balance: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS", "§fSold {item} x{amount} for {price}. Balance: {bal}");
            ccLang.get().addDefault("BUY_SUCCESS_JP", "§fBought {item} x{amount} for {price}points. Balance: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS_JP", "§fSold {item} x{amount} for {price}points. Balance: {bal}");
            ccLang.get().addDefault("QSELL_RESULT", "§fTo: ");
            ccLang.get().addDefault("QSELL_NA", "§fThere are no shops to trade that item.");
            ccLang.get().addDefault("DELIVERYCHARGE", "§fDelivery charge");
            ccLang.get().addDefault("DELIVERYCHARGE_EXEMPTION", "§fDelivery charge: {fee} ({fee2} exempt)");
            ccLang.get().addDefault("DELIVERYCHARGE_NA", "§fCan't deliver to different world.");
            ccLang.get().addDefault("NOT_ENOUGH_MONEY", "§fNot enough money. Balance: {bal}");
            ccLang.get().addDefault("NOT_ENOUGH_POINT", "§fNot enough point. Balance: {bal}");
            ccLang.get().addDefault("NO_ITEM_TO_SELL", "§fNot enough item.");
            ccLang.get().addDefault("INVEN_FULL", "§4Inventory is full!");
            ccLang.get().addDefault("IRREVERSIBLE", "§fThis action is irreversible!");

            ccLang.get().addDefault("HELP.TITLE", "§fHelp: {command} --------------------");
            ccLang.get().addDefault("HELP.SHOP", "Open Shop GUI.");
            ccLang.get().addDefault("HELP.CMD", "Toggle Command Help.");
            ccLang.get().addDefault("HELP.CREATESHOP", "Create new shop.");
            ccLang.get().addDefault("HELP.DELETESHOP", "Delete exist shop.");
            ccLang.get().addDefault("HELP.SETTAX", "Set sale tax.");
            ccLang.get().addDefault("HELP.SHOPADDHAND", "Add Item to shop.");
            ccLang.get().addDefault("HELP.SHOPADDITEM", "Add Item to shop.");
            ccLang.get().addDefault("HELP.SHOPEDIT", "Edit shop item.");
            ccLang.get().addDefault("HELP.PRICE", "§7Formula: median*value/stock");
            ccLang.get().addDefault("HELP.INF_STATIC", "§7median<0 == static price     stock<0 == infinite stock");
            ccLang.get().addDefault("HELP.EDITALL", "Edit all shop items");
            ccLang.get().addDefault("HELP.EDITALL2", "§cWarning. There is no sanity check. Use at your own caution.");
            ccLang.get().addDefault("HELP.RELOAD", "Reload YML.");
            ccLang.get().addDefault("HELP.RELOADED", "Plugin reloaded");
            ccLang.get().addDefault("HELP.USAGE", "Usage");
            ccLang.get().addDefault("HELP.CREATESHOP2", "Permission(You can change this later.)\n   true: dshop.user.shop.shopname\n   false: no permission needed(Default)\n   user input: need that permission");
            ccLang.get().addDefault("HELP.ITEM_ALREADY_EXIST", "§7§o{item} is already selling.\n   {info}\n   Values will be update.");
            ccLang.get().addDefault("HELP.ITEM_INFO", "§7§o{item} is now selling for:\n   {info}");
            ccLang.get().addDefault("HELP.REMOVE_ITEM", "§f§oEnter 0 for value to §4Remove§f this item.");
            ccLang.get().addDefault("HELP.QSELL", "§fQuick Sell");
            ccLang.get().addDefault("HELP.DELETE_OLD_USER", "Delete Old Inactive User data from User.yml.");
            ccLang.get().addDefault("HELP.CONVERT", "Convert data from other shop plugin");
            ccLang.get().addDefault("HELP.ACCOUNT", "Set shop account balance. -1 = Infinite");

            ccLang.get().addDefault("ITEM_ADDED", "Item Added!");
            ccLang.get().addDefault("ITEM_UPDATED", "Item Updated!");
            ccLang.get().addDefault("ITEM_DELETED", "Item Removed!");
            ccLang.get().addDefault("CHANGES_APPLIED", "Changes applied. New value: ");
            ccLang.get().addDefault("RECOMMAND_APPLIED", "Suggestion applied. Based on {playerNum}players. This value can be edited in config");
            ccLang.get().addDefault("TRANSFER_SUCCESS", "Transfer success.");

            ccLang.get().addDefault("ERR.NO_USER_ID", "§6Cant find your uuid from server. Shop Unavailable.");
            ccLang.get().addDefault("ERR.ITEM_NOT_EXIST", "Item not exist.");
            ccLang.get().addDefault("ERR.ITEM_FORBIDDEN", "Forbidden Item.");
            ccLang.get().addDefault("ERR.NO_PERMISSION", "§eNo permission.");
            ccLang.get().addDefault("ERR.WRONG_USAGE", "Wrong usage");
            ccLang.get().addDefault("ERR.NO_EMPTY_SLOT", "Shop is full");
            ccLang.get().addDefault("ERR.WRONG_DATATYPE", "Wrong Argument type");
            ccLang.get().addDefault("ERR.VALUE_ZERO", "Argument must be greater than 0");
            ccLang.get().addDefault("ERR.WRONG_ITEMNAME", "There's no such item.");
            ccLang.get().addDefault("ERR.HAND_EMPTY", "You must be holding an item to sell.");
            ccLang.get().addDefault("ERR.HAND_EMPTY2", "§c§oYou must be holding an item to sell!");
            ccLang.get().addDefault("ERR.SHOP_NOT_FOUND", "§fShop not found");
            ccLang.get().addDefault("ERR.SHOP_EXIST", "This name already exist.");
            ccLang.get().addDefault("ERR.SIGNSHOP_REMOTE_ACCESS", "You can't access sign shop remotely.");
            ccLang.get().addDefault("ERR.LOCALSHOP_REMOTE_ACCESS", "You can't access local shop remotely.");
            ccLang.get().addDefault("ERR.MAX_LOWER_THAN_MIN", "Max price must be greater than Min price.");
            ccLang.get().addDefault("ERR.DEFAULT_VALUE_OUT_OF_RANGE", "Price must be between min and max");
            ccLang.get().addDefault("ERR.NO_RECOMMAND_DATA", "No data found in Worth.yml.");
            ccLang.get().addDefault("ERR.JOBSREBORN_NOT_FOUND", "'Jobs Reborn' not found.");
            ccLang.get().addDefault("ERR.SHOP_HAS_INF_BAL", "{shop} has infinite balance");
            ccLang.get().addDefault("ERR.SHOP_DIFF_CURRENCY", "These shops have different currency.");
            ccLang.get().addDefault("ERR.PLAYER_NOT_EXIST", "Player not exist.");
            ccLang.get().addDefault("ERR.SHOP_LINK_FAIL", "At least one of them must be an actual account.");
            ccLang.get().addDefault("ERR.SHOP_LINK_TARGET_ERR", "Target shop must have actual account.");
            ccLang.get().addDefault("ERR.NESTED_STRUCTURE", "Nested structure is forbidden. (ex. aa-bb, bb-cc)");
            ccLang.get().addDefault("ERR.CREATIVE","§eYou can not use this command in creative mode. No permission.");

            ccLang.get().options().copyDefaults(true);
            ccLang.save();
        }

        if(lang == null) lang = "en-US";

        if(!lang.equals("en-US") && !lang.equals("ko-KR"))
        {
            ConfigurationSection conf = ccLang.get().getConfigurationSection("");

            ccLang.setup("Lang_v2_"+lang,null);

            for (String s:conf.getKeys(true))
            {
                if(!ccLang.get().contains(s))
                {
                    console.sendMessage(dsPrefix_server + "String Key " + s + " added");
                    ccLang.get().addDefault(s,conf.get(s));
                }
            }
        }
        else
        {
            ccLang.setup("Lang_v2_"+lang,null);
        }

        ccLang.get().options().copyDefaults(true);
        ccLang.save();
    }

    private void SetupShopFile()
    {
        ccShop.setup("Shop",null);
        ccShop.get().options().header("Shop name can not contain formatting codes, '/' and ' '");
        ccShop.get().options().copyHeader(true);

        if(ccShop.get().getKeys(false).size() == 0)
        {
            ccShop.get().set("Main.Options.page",2);
            ccShop.get().set("Main.Options.title","Main");
            ccShop.get().set("Main.Options.lore","");
            ccShop.get().set("Main.Options.permission","");
            ccShop.get().set("Main.0.mat","DIRT");
            ccShop.get().set("Main.0.value",1);
            ccShop.get().set("Main.0.median",10000);
            ccShop.get().set("Main.0.stock",10000);
            ccShop.get().set("Main.1.mat","COBBLESTONE");
            ccShop.get().set("Main.1.value",1.5);
            ccShop.get().set("Main.1.median",10000);
            ccShop.get().set("Main.1.stock",10000);
            ccShop.get().set("OreShop.Options.page",2);
            ccShop.get().set("OreShop.Options.title","OreShop");
            ccShop.get().set("OreShop.Options.lore","");
            ccShop.get().set("OreShop.Options.permission","");
            ccShop.get().set("OreShop.1.mat","DIAMOND");
            ccShop.get().set("OreShop.1.value",3000);
            ccShop.get().set("OreShop.1.median",1000);
            ccShop.get().set("OreShop.1.stock",1000);
        }

        for (String s:ccShop.get().getKeys(false))
        {
            if(!ccShop.get().getConfigurationSection(s).contains("Options"))
            {
                ccShop.get().set(s+".Options.page",2);
                ccShop.get().set(s+".Options.permission","");
            }
        }

        ccShop.get().options().copyDefaults(true);
        ccShop.save();
    }

    private void SetupUserFile()
    {
        ccUser.setup("User",null);
        ccUser.get().options().copyDefaults(true);
        ccUser.save();
    }

    private void SetupStartpageFile()
    {
        ccStartpage.setup("Startpage",null);
        ccStartpage.get().options().header("LineBreak: \\, |, bracket is NOT working. Recommended character: /, _, ;, ※");
        ccStartpage.get().addDefault("Options.Title", "§3§lStart Page");
        ccStartpage.get().addDefault("Options.UiSlotCount", 27);
        ccStartpage.get().addDefault("Options.LineBreak","/");

        if(ccStartpage.get().getKeys(false).size() == 0)
        {
            ccStartpage.get().set("Buttons.0.displayName", "§3§lExample Button");
            ccStartpage.get().set("Buttons.0.lore", "§fThis is Example Button/§aClick empty slot to create new button");
            ccStartpage.get().set("Buttons.0.icon","SUNFLOWER");
            ccStartpage.get().set("Buttons.0.action","Dynamicshop Testfunction/Dynamicshop Testfunction");
        }
        ccStartpage.get().options().copyDefaults(true);
        ccStartpage.save();
    }

    private void SetupSignFile()
    {
        ccSign.setup("Sign",null);
        ccSign.get().options().copyDefaults(true);
        ccSign.save();
    }

    private void SetupWorthFile()
    {
        ccWorth.setup("Worth",null);
        ccWorth.get().addDefault("ACACIA_BOAT", 2.48);
        ccWorth.get().addDefault("ACACIA_BUTTON", 0.48);
        ccWorth.get().addDefault("ACACIA_DOOR", 1.05);
        ccWorth.get().addDefault("ACACIA_FENCE", 0.9);
        ccWorth.get().addDefault("ACACIA_FENCE_GATE", 2.05);
        ccWorth.get().addDefault("ACACIA_LEAVES", 0.25);
        ccWorth.get().addDefault("ACACIA_LOG", 1.5);
        ccWorth.get().addDefault("ACACIA_PLANKS", 0.48);
        ccWorth.get().addDefault("ACACIA_PRESSURE_PLATE", 0.9);
        ccWorth.get().addDefault("ACACIA_SAPLING", 0.65);
        ccWorth.get().addDefault("ACACIA_SLAB", 0.34);
        ccWorth.get().addDefault("ACACIA_STAIRS", 0.81);
        ccWorth.get().addDefault("ACACIA_TRAPDOOR", 1.25);
        ccWorth.get().addDefault("ACACIA_WOOD", 1.5);
        ccWorth.get().addDefault("ACTIVATOR_RAIL", 15.54);
        ccWorth.get().addDefault("ALLIUM", 2.0);
        ccWorth.get().addDefault("ANDESITE", 1.1);
        ccWorth.get().addDefault("ANVIL", 653.86);
        ccWorth.get().addDefault("APPLE", 1.65);
        ccWorth.get().addDefault("ARMOR_STAND", 1.81);
        ccWorth.get().addDefault("ARROW", 0.74);
        ccWorth.get().addDefault("AZURE_BLUET", 1.65);
        ccWorth.get().addDefault("BAKED_POTATO", 1.54);
        ccWorth.get().addDefault("BAT_SPAWN_EGG", 145);
        ccWorth.get().addDefault("BEACON", 107.29);
        ccWorth.get().addDefault("BEEF", 1.35);
        ccWorth.get().addDefault("BEETROOT", 3.25);
        ccWorth.get().addDefault("BEETROOT_SEEDS", 1.25);
        ccWorth.get().addDefault("BEETROOT_SOUP", 22.7);
        ccWorth.get().addDefault("BIRCH_BOAT", 2.48);
        ccWorth.get().addDefault("BIRCH_BUTTON", 0.48);
        ccWorth.get().addDefault("BIRCH_DOOR", 0.98);
        ccWorth.get().addDefault("BIRCH_FENCE", 1.37);
        ccWorth.get().addDefault("BIRCH_FENCE_GATE", 5.25);
        ccWorth.get().addDefault("BIRCH_LEAVES", 0.25);
        ccWorth.get().addDefault("BIRCH_LOG", 1.5);
        ccWorth.get().addDefault("BIRCH_PLANKS", 0.48);
        ccWorth.get().addDefault("BIRCH_PRESSURE_PLATE", 0.9);
        ccWorth.get().addDefault("BIRCH_SAPLING", 0.65);
        ccWorth.get().addDefault("BIRCH_SLAB", 0.25);
        ccWorth.get().addDefault("BIRCH_STAIRS", 0.74);
        ccWorth.get().addDefault("BIRCH_TRAPDOOR", 1.25);
        ccWorth.get().addDefault("BIRCH_WOOD", 1.5);
        ccWorth.get().addDefault("BLACK_BANNER", 28.25);
        ccWorth.get().addDefault("BLACK_BED", 15.25);
        ccWorth.get().addDefault("BLACK_CARPET", 3.13);
        ccWorth.get().addDefault("BLACK_CONCRETE", 2.54);
        ccWorth.get().addDefault("BLACK_CONCRETE_POWDER", 1.19);
        ccWorth.get().addDefault("BLACK_GLAZED_TERRACOTTA", 1.67);
        ccWorth.get().addDefault("BLACK_SHULKER_BOX", 54.95);
        ccWorth.get().addDefault("BLACK_STAINED_GLASS", 2.03);
        ccWorth.get().addDefault("BLACK_STAINED_GLASS_PANE", 0.77);
        ccWorth.get().addDefault("BLACK_TERRACOTTA", 1.48);
        ccWorth.get().addDefault("BLACK_WOOL", 4.65);
        ccWorth.get().addDefault("BLAZE_POWDER", 1.3);
        ccWorth.get().addDefault("BLAZE_ROD", 2.5);
        ccWorth.get().addDefault("BLAZE_SPAWN_EGG", 2500);
        ccWorth.get().addDefault("BLUE_BANNER", 31.25);
        ccWorth.get().addDefault("BLUE_BED", 16.75);
        ccWorth.get().addDefault("BLUE_CARPET", 3.53);
        ccWorth.get().addDefault("BLUE_CONCRETE", 2.61);
        ccWorth.get().addDefault("BLUE_CONCRETE_POWDER", 1.26);
        ccWorth.get().addDefault("BLUE_GLAZED_TERRACOTTA", 1.73);
        ccWorth.get().addDefault("BLUE_ORCHID", 1.65);
        ccWorth.get().addDefault("BLUE_SHULKER_BOX", 55.45);
        ccWorth.get().addDefault("BLUE_STAINED_GLASS", 2.09);
        ccWorth.get().addDefault("BLUE_STAINED_GLASS_PANE", 0.79);
        ccWorth.get().addDefault("BLUE_TERRACOTTA", 1.54);
        ccWorth.get().addDefault("BLUE_WOOL", 5.15);
        ccWorth.get().addDefault("BONE", 2.25);
        ccWorth.get().addDefault("BONE_BLOCK", 7.75);
        ccWorth.get().addDefault("BONE_MEAL", 0.85);
        ccWorth.get().addDefault("BOOK", 38.68);
        ccWorth.get().addDefault("BOOKSHELF", 118.54);
        ccWorth.get().addDefault("BOW", 3.1);
        ccWorth.get().addDefault("BOWL", 0.4);
        ccWorth.get().addDefault("BREAD", 2.35);
        ccWorth.get().addDefault("BREWING_STAND", 3.2);
        ccWorth.get().addDefault("BRICK", 0.44);
        ccWorth.get().addDefault("BRICKS", 1.85);
        ccWorth.get().addDefault("BRICK_SLAB", 0.94);
        ccWorth.get().addDefault("BRICK_STAIRS", 0.68);
        ccWorth.get().addDefault("BROWN_BANNER", 27.05);
        ccWorth.get().addDefault("BROWN_BED", 14.65);
        ccWorth.get().addDefault("BROWN_CARPET", 3.0);
        ccWorth.get().addDefault("BROWN_CONCRETE", 2.42);
        ccWorth.get().addDefault("BROWN_CONCRETE_POWDER", 1.17);
        ccWorth.get().addDefault("BROWN_GLAZED_TERRACOTTA", 1.64);
        ccWorth.get().addDefault("BROWN_MUSHROOM", 1.05);
        ccWorth.get().addDefault("BROWN_SHULKER_BOX", 54.75);
        ccWorth.get().addDefault("BROWN_STAINED_GLASS", 2.01);
        ccWorth.get().addDefault("BROWN_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("BROWN_TERRACOTTA", 1.46);
        ccWorth.get().addDefault("BROWN_WOOL", 4.45);
        ccWorth.get().addDefault("BUCKET", 45.66);
        ccWorth.get().addDefault("CACTUS", 1.45);
        ccWorth.get().addDefault("CACTUS_GREEN", 1.64);
        ccWorth.get().addDefault("CAKE", 9.12);
        ccWorth.get().addDefault("CARROT", 1.25);
        ccWorth.get().addDefault("CARROT_ON_A_STICK", 2.1);
        ccWorth.get().addDefault("CARVED_PUMPKIN", 0.35);
        ccWorth.get().addDefault("CAULDRON", 76.04);
        ccWorth.get().addDefault("CAVE_SPIDER_SPAWN_EGG", 35);
        ccWorth.get().addDefault("CHAINMAIL_BOOTS", 70);
        ccWorth.get().addDefault("CHAINMAIL_CHESTPLATE", 80);
        ccWorth.get().addDefault("CHAINMAIL_HELMET", 72.5);
        ccWorth.get().addDefault("CHAINMAIL_LEGGINGS", 75);
        ccWorth.get().addDefault("CHARCOAL", 1.69);
        ccWorth.get().addDefault("CHEST", 3.3);
        ccWorth.get().addDefault("CHEST_MINECART", 79.44);
        ccWorth.get().addDefault("CHICKEN", 1.15);
        ccWorth.get().addDefault("CHICKEN_SPAWN_EGG", 65);
        ccWorth.get().addDefault("CHISELED_QUARTZ_BLOCK", 6.43);
        ccWorth.get().addDefault("CHISELED_RED_SANDSTONE", 6.83);
        ccWorth.get().addDefault("CHISELED_SANDSTONE", 6.83);
        ccWorth.get().addDefault("CHISELED_STONE_BRICKS", 0.52);
        ccWorth.get().addDefault("CHORUS_FLOWER", 5.0);
        ccWorth.get().addDefault("CHORUS_FRUIT", 2.75);
        ccWorth.get().addDefault("CLAY", 0.25);
        ccWorth.get().addDefault("CLOCK", 241.5);
        ccWorth.get().addDefault("COAL", 1.5);
        ccWorth.get().addDefault("COAL_BLOCK", 13.5);
        ccWorth.get().addDefault("COAL_ORE", 15.74);
        ccWorth.get().addDefault("COARSE_DIRT", 0.43);
        ccWorth.get().addDefault("COBBLESTONE", 0.2);
        ccWorth.get().addDefault("COBBLESTONE_SLAB", 0.12);
        ccWorth.get().addDefault("COBBLESTONE_STAIRS", 0.33);
        ccWorth.get().addDefault("COBBLESTONE_WALL", 0.22);
        ccWorth.get().addDefault("COBWEB", 36.01);
        ccWorth.get().addDefault("COCOA_BEANS", 1.25);
        ccWorth.get().addDefault("COMPARATOR", 5.81);
        ccWorth.get().addDefault("COMPASS", 61.5);
        ccWorth.get().addDefault("COOKED_BEEF", 1.54);
        ccWorth.get().addDefault("COOKED_CHICKEN", 1.34);
        ccWorth.get().addDefault("COOKED_MUTTON", 1.49);
        ccWorth.get().addDefault("COOKED_PORKCHOP", 1.35);
        ccWorth.get().addDefault("COOKED_RABBIT", 1.54);
        ccWorth.get().addDefault("COOKED_SALMON", 1.64);
        ccWorth.get().addDefault("COOKIE", 0.36);
        ccWorth.get().addDefault("COW_SPAWN_EGG", 105);
        ccWorth.get().addDefault("CRACKED_STONE_BRICKS", 0.6);
        ccWorth.get().addDefault("CRAFTING_TABLE", 1.7);
        ccWorth.get().addDefault("CREEPER_HEAD", 4000.0);
        ccWorth.get().addDefault("CREEPER_SPAWN_EGG", 366);
        ccWorth.get().addDefault("CYAN_BANNER", 30.61);
        ccWorth.get().addDefault("CYAN_BED", 16.43);
        ccWorth.get().addDefault("CYAN_CARPET", 5.09);
        ccWorth.get().addDefault("CYAN_CONCRETE", 2.59);
        ccWorth.get().addDefault("CYAN_CONCRETE_POWDER", 1.24);
        ccWorth.get().addDefault("CYAN_DYE", 1.84);
        ccWorth.get().addDefault("CYAN_GLAZED_TERRACOTTA", 1.72);
        ccWorth.get().addDefault("CYAN_SHULKER_BOX", 55.34);
        ccWorth.get().addDefault("CYAN_STAINED_GLASS", 2.08);
        ccWorth.get().addDefault("CYAN_STAINED_GLASS_PANE", 0.79);
        ccWorth.get().addDefault("CYAN_TERRACOTTA", 1.53);
        ccWorth.get().addDefault("CYAN_WOOL", 5.04);
        ccWorth.get().addDefault("DANDELION", 1.25);
        ccWorth.get().addDefault("DANDELION_YELLOW", 0.88);
        ccWorth.get().addDefault("DARK_OAK_BOAT", 2.1);
        ccWorth.get().addDefault("DARK_OAK_BUTTON", 0.48);
        ccWorth.get().addDefault("DARK_OAK_DOOR", 0.83);
        ccWorth.get().addDefault("DARK_OAK_FENCE", 0.73);
        ccWorth.get().addDefault("DARK_OAK_FENCE_GATE", 1.9);
        ccWorth.get().addDefault("DARK_OAK_LEAVES", 0.25);
        ccWorth.get().addDefault("DARK_OAK_LOG", 1.5);
        ccWorth.get().addDefault("DARK_OAK_PLANKS", 0.48);
        ccWorth.get().addDefault("DARK_OAK_PRESSURE_PLATE", 0.9);
        ccWorth.get().addDefault("DARK_OAK_SAPLING", 0.65);
        ccWorth.get().addDefault("DARK_OAK_SLAB", 0.25);
        ccWorth.get().addDefault("DARK_OAK_STAIRS", 0.74);
        ccWorth.get().addDefault("DARK_OAK_TRAPDOOR", 1.25);
        ccWorth.get().addDefault("DARK_OAK_WOOD", 1.5);
        ccWorth.get().addDefault("DARK_PRISMARINE", 15.55);
        ccWorth.get().addDefault("DAYLIGHT_DETECTOR", 10.91);
        ccWorth.get().addDefault("DEAD_BUSH", 0.35);
        ccWorth.get().addDefault("DETECTOR_RAIL", 15.46);
        ccWorth.get().addDefault("DIAMOND", 125.0);
        ccWorth.get().addDefault("DIAMOND_AXE", 375.6);
        ccWorth.get().addDefault("DIAMOND_BLOCK", 1125);
        ccWorth.get().addDefault("DIAMOND_BOOTS", 500.1);
        ccWorth.get().addDefault("DIAMOND_CHESTPLATE", 1000.1);
        ccWorth.get().addDefault("DIAMOND_HELMET", 625.1);
        ccWorth.get().addDefault("DIAMOND_HOE", 250.6);
        ccWorth.get().addDefault("DIAMOND_HORSE_ARMOR", 165.0);
        ccWorth.get().addDefault("DIAMOND_LEGGINGS", 875.1);
        ccWorth.get().addDefault("DIAMOND_ORE", 113.7);
        ccWorth.get().addDefault("DIAMOND_PICKAXE", 375.6);
        ccWorth.get().addDefault("DIAMOND_SHOVEL", 125.6);
        ccWorth.get().addDefault("DIAMOND_SWORD", 250.35);
        ccWorth.get().addDefault("DIORITE", 1.8);
        ccWorth.get().addDefault("DIRT", 0.45);
        ccWorth.get().addDefault("DISPENSER", 5.25);
        ccWorth.get().addDefault("DONKEY_SPAWN_EGG", 1200);
        ccWorth.get().addDefault("DRAGON_BREATH", 6.12);
        ccWorth.get().addDefault("DRAGON_EGG", 9000.0);
        ccWorth.get().addDefault("DRAGON_HEAD", 125000.0);
        ccWorth.get().addDefault("DROPPER", 2.15);
        ccWorth.get().addDefault("EGG", 1.0);
        ccWorth.get().addDefault("ELDER_GUARDIAN_SPAWN_EGG", 190000);
        ccWorth.get().addDefault("ELYTRA", 85.0);
        ccWorth.get().addDefault("EMERALD", 65.0);
        ccWorth.get().addDefault("EMERALD_BLOCK", 585);
        ccWorth.get().addDefault("EMERALD_ORE", 93.7);
        ccWorth.get().addDefault("ENCHANTED_GOLDEN_APPLE", 1250.0);
        ccWorth.get().addDefault("ENCHANTING_TABLE", 328.68);
        ccWorth.get().addDefault("ENDERMAN_SPAWN_EGG", 366);
        ccWorth.get().addDefault("ENDERMITE_SPAWN_EGG", 31);
        ccWorth.get().addDefault("ENDER_CHEST", 36.8);
        ccWorth.get().addDefault("ENDER_EYE", 3.35);
        ccWorth.get().addDefault("ENDER_PEARL", 2.1);
        ccWorth.get().addDefault("END_CRYSTAL", 19.11);
        ccWorth.get().addDefault("END_ROD", 1.95);
        ccWorth.get().addDefault("END_STONE", 3.25);
        ccWorth.get().addDefault("END_STONE_BRICKS", 3.28);
        ccWorth.get().addDefault("EVOKER_SPAWN_EGG", 16000);
        ccWorth.get().addDefault("EXPERIENCE_BOTTLE", 65);
        ccWorth.get().addDefault("FEATHER", 1.25);
        ccWorth.get().addDefault("FERMENTED_SPIDER_EYE", 2.46);
        ccWorth.get().addDefault("FERN", 0.35);
        ccWorth.get().addDefault("FIREWORK_ROCKET", 3.65);
        ccWorth.get().addDefault("FIREWORK_STAR", 1.38);
        ccWorth.get().addDefault("FIRE_CHARGE", 1.38);
        ccWorth.get().addDefault("FISHING_ROD", 2.35);
        ccWorth.get().addDefault("FLINT", 1.05);
        ccWorth.get().addDefault("FLINT_AND_STEEL", 16.34);
        ccWorth.get().addDefault("FLOWER_POT", 1.41);
        ccWorth.get().addDefault("FURNACE", 1.7);
        ccWorth.get().addDefault("FURNACE_MINECART", 77.84);
        ccWorth.get().addDefault("GHAST_SPAWN_EGG", 1450);
        ccWorth.get().addDefault("GHAST_TEAR", 2.65);
        ccWorth.get().addDefault("GLASS", 1.84);
        ccWorth.get().addDefault("GLASS_BOTTLE", 1.87);
        ccWorth.get().addDefault("GLASS_PANE", 0.7);
        ccWorth.get().addDefault("GLOWSTONE", 6.3);
        ccWorth.get().addDefault("GLOWSTONE_DUST", 1.55);
        ccWorth.get().addDefault("GOLDEN_APPLE", 55.34);
        ccWorth.get().addDefault("GOLDEN_AXE", 181.16);
        ccWorth.get().addDefault("GOLDEN_BOOTS", 240.85);
        ccWorth.get().addDefault("GOLDEN_CARROT", 54.94);
        ccWorth.get().addDefault("GOLDEN_CHESTPLATE", 481.6);
        ccWorth.get().addDefault("GOLDEN_HELMET", 301.04);
        ccWorth.get().addDefault("GOLDEN_HOE", 120.98);
        ccWorth.get().addDefault("GOLDEN_HORSE_ARMOR", 125);
        ccWorth.get().addDefault("GOLDEN_LEGGINGS", 421.41);
        ccWorth.get().addDefault("GOLDEN_PICKAXE", 181.16);
        ccWorth.get().addDefault("GOLDEN_SHOVEL", 60.79);
        ccWorth.get().addDefault("GOLDEN_SWORD", 120.73);
        ccWorth.get().addDefault("GOLD_BLOCK", 541.71);
        ccWorth.get().addDefault("GOLD_INGOT", 60.19);
        ccWorth.get().addDefault("GOLD_NUGGET", 6.7);
        ccWorth.get().addDefault("GOLD_ORE", 60.0);
        ccWorth.get().addDefault("GRANITE", 3.45);
        ccWorth.get().addDefault("GRASS", 3.54);
        ccWorth.get().addDefault("GRASS_BLOCK", 3.54);
        ccWorth.get().addDefault("GRAVEL", 0.35);
        ccWorth.get().addDefault("GRAY_BANNER", 26.75);
        ccWorth.get().addDefault("GRAY_BED", 15.81);
        ccWorth.get().addDefault("GRAY_CARPET", 4.45);
        ccWorth.get().addDefault("GRAY_CONCRETE", 2.51);
        ccWorth.get().addDefault("GRAY_CONCRETE_POWDER", 1.16);
        ccWorth.get().addDefault("GRAY_DYE", 1.2);
        ccWorth.get().addDefault("GRAY_GLAZED_TERRACOTTA", 1.64);
        ccWorth.get().addDefault("GRAY_SHULKER_BOX", 54.7);
        ccWorth.get().addDefault("GRAY_STAINED_GLASS", 2.0);
        ccWorth.get().addDefault("GRAY_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("GRAY_TERRACOTTA", 1.45);
        ccWorth.get().addDefault("GRAY_WOOL", 4.4);
        ccWorth.get().addDefault("GREEN_BANNER", 29.38);
        ccWorth.get().addDefault("GREEN_BED", 15.81);
        ccWorth.get().addDefault("GREEN_CARPET", 3.26);
        ccWorth.get().addDefault("GREEN_CONCRETE", 30.37);
        ccWorth.get().addDefault("GREEN_CONCRETE_POWDER", 29.02);
        ccWorth.get().addDefault("GREEN_GLAZED_TERRACOTTA", 1.69);
        ccWorth.get().addDefault("GREEN_SHULKER_BOX", 55.14);
        ccWorth.get().addDefault("GREEN_STAINED_GLASS", 2.05);
        ccWorth.get().addDefault("GREEN_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("GREEN_TERRACOTTA", 1.5);
        ccWorth.get().addDefault("GREEN_WALL_BANNER", 0);
        ccWorth.get().addDefault("GREEN_WOOL", 4.84);
        ccWorth.get().addDefault("GUARDIAN_SPAWN_EGG", 1750);
        ccWorth.get().addDefault("GUNPOWDER", 1.25);
        ccWorth.get().addDefault("HEAVY_WEIGHTED_PRESSURE_PLATE", 30.48);
        ccWorth.get().addDefault("HOPPER", 79.34);
        ccWorth.get().addDefault("HOPPER_MINECART", 155.48);
        ccWorth.get().addDefault("HORSE_SPAWN_EGG", 55);
        ccWorth.get().addDefault("HUSK_SPAWN_EGG", 12500);
        ccWorth.get().addDefault("ICE", 5.79);
        ccWorth.get().addDefault("INK_SAC", 1.45);
        ccWorth.get().addDefault("IRON_AXE", 46.16);
        ccWorth.get().addDefault("IRON_BARS", 5.7);
        ccWorth.get().addDefault("IRON_BLOCK", 136.71);
        ccWorth.get().addDefault("IRON_BOOTS", 60.85);
        ccWorth.get().addDefault("IRON_CHESTPLATE", 121.6);
        ccWorth.get().addDefault("IRON_DOOR", 15.22);
        ccWorth.get().addDefault("IRON_HELMET", 76.04);
        ccWorth.get().addDefault("IRON_HOE", 30.98);
        ccWorth.get().addDefault("IRON_HORSE_ARMOR", 95.0);
        ccWorth.get().addDefault("IRON_INGOT", 15.19);
        ccWorth.get().addDefault("IRON_LEGGINGS", 106.41);
        ccWorth.get().addDefault("IRON_NUGGET", 1.68);
        ccWorth.get().addDefault("IRON_ORE", 15.0);
        ccWorth.get().addDefault("IRON_PICKAXE", 46.16);
        ccWorth.get().addDefault("IRON_SHOVEL", 15.79);
        ccWorth.get().addDefault("IRON_SWORD", 30.73);
        ccWorth.get().addDefault("IRON_TRAPDOOR", 60.85);
        ccWorth.get().addDefault("ITEM_FRAME", 40.1);
        ccWorth.get().addDefault("JACK_O_LANTERN", 0.91);
        ccWorth.get().addDefault("JUKEBOX", 128.3);
        ccWorth.get().addDefault("JUNGLE_BOAT", 2.1);
        ccWorth.get().addDefault("JUNGLE_BUTTON", 0.48);
        ccWorth.get().addDefault("JUNGLE_DOOR", 0.83);
        ccWorth.get().addDefault("JUNGLE_FENCE", 0.73);
        ccWorth.get().addDefault("JUNGLE_FENCE_GATE", 1.9);
        ccWorth.get().addDefault("JUNGLE_LEAVES", 0.25);
        ccWorth.get().addDefault("JUNGLE_LOG", 1.5);
        ccWorth.get().addDefault("JUNGLE_PLANKS", 0.48);
        ccWorth.get().addDefault("JUNGLE_PRESSURE_PLATE", 0.9);
        ccWorth.get().addDefault("JUNGLE_SAPLING", 0.85);
        ccWorth.get().addDefault("JUNGLE_SLAB", 0.25);
        ccWorth.get().addDefault("JUNGLE_STAIRS", 0.74);
        ccWorth.get().addDefault("JUNGLE_TRAPDOOR", 1.25);
        ccWorth.get().addDefault("JUNGLE_WOOD", 1.5);
        ccWorth.get().addDefault("LADDER", 0.62);
        ccWorth.get().addDefault("LAPIS_BLOCK", 17.65);
        ccWorth.get().addDefault("LAPIS_LAZULI", 1.95);
        ccWorth.get().addDefault("LAPIS_ORE", 23.74);
        ccWorth.get().addDefault("LAVA_BUCKET", 47.76);
        ccWorth.get().addDefault("LEAD", 2.18);
        ccWorth.get().addDefault("LEATHER", 38.0);
        ccWorth.get().addDefault("LEATHER_BOOTS", 152.1);
        ccWorth.get().addDefault("LEATHER_CHESTPLATE", 304.1);
        ccWorth.get().addDefault("LEATHER_HELMET", 190.10);
        ccWorth.get().addDefault("LEATHER_LEGGINGS", 266.10);
        ccWorth.get().addDefault("LEVER", 0.55);
        ccWorth.get().addDefault("LIGHT_BLUE_BANNER", 30.05);
        ccWorth.get().addDefault("LIGHT_BLUE_BED", 16.15);
        ccWorth.get().addDefault("LIGHT_BLUE_CARPET", 3.33);
        ccWorth.get().addDefault("LIGHT_BLUE_CONCRETE", 2.58);
        ccWorth.get().addDefault("LIGHT_BLUE_CONCRETE_POWDER", 1.23);
        ccWorth.get().addDefault("LIGHT_BLUE_DYE", 1.75);
        ccWorth.get().addDefault("LIGHT_BLUE_GLAZED_TERRACOTTA", 1.71);
        ccWorth.get().addDefault("LIGHT_BLUE_SHULKER_BOX", 55.25);
        ccWorth.get().addDefault("LIGHT_BLUE_STAINED_GLASS", 2.07);
        ccWorth.get().addDefault("LIGHT_BLUE_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("LIGHT_BLUE_TERRACOTTA", 1.52);
        ccWorth.get().addDefault("LIGHT_BLUE_WOOL", 4.95);
        ccWorth.get().addDefault("LIGHT_GRAY_BANNER", 30.05);
        ccWorth.get().addDefault("LIGHT_GRAY_BED", 16.15);
        ccWorth.get().addDefault("LIGHT_GRAY_CARPET", 3.33);
        ccWorth.get().addDefault("LIGHT_GRAY_CONCRETE", 2.58);
        ccWorth.get().addDefault("LIGHT_GRAY_CONCRETE_POWDER", 1.23);
        ccWorth.get().addDefault("LIGHT_GRAY_DYE", 1.75);
        ccWorth.get().addDefault("LIGHT_GRAY_GLAZED_TERRACOTTA", 1.71);
        ccWorth.get().addDefault("LIGHT_GRAY_SHULKER_BOX", 55.25);
        ccWorth.get().addDefault("LIGHT_GRAY_STAINED_GLASS", 2.07);
        ccWorth.get().addDefault("LIGHT_GRAY_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("LIGHT_GRAY_TERRACOTTA", 1.52);
        ccWorth.get().addDefault("LIGHT_GRAY_WOOL", 4.95);
        ccWorth.get().addDefault("LIGHT_WEIGHTED_PRESSURE_PLATE", 120.48);
        ccWorth.get().addDefault("LILAC", 1.75);
        ccWorth.get().addDefault("LILY_PAD", 2.25);
        ccWorth.get().addDefault("LIME_BANNER", 27.31);
        ccWorth.get().addDefault("LIME_BED", 14.78);
        ccWorth.get().addDefault("LIME_CARPET", 4.54);
        ccWorth.get().addDefault("LIME_CONCRETE", 2.52);
        ccWorth.get().addDefault("LIME_CONCRETE_POWDER", 1.17);
        ccWorth.get().addDefault("LIME_DYE", 1.29);
        ccWorth.get().addDefault("LIME_GLAZED_TERRACOTTA", 2.2);
        ccWorth.get().addDefault("LIME_SHULKER_BOX", 54.79);
        ccWorth.get().addDefault("LIME_STAINED_GLASS", 2.01);
        ccWorth.get().addDefault("LIME_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("LIME_TERRACOTTA", 2.01);
        ccWorth.get().addDefault("LIME_WALL_BANNER", 0);
        ccWorth.get().addDefault("LIME_WOOL", 4.49);
        ccWorth.get().addDefault("LLAMA_SPAWN_EGG", 45);
        ccWorth.get().addDefault("MAGENTA_BANNER", 32.15);
        ccWorth.get().addDefault("MAGENTA_BED", 17.2);
        ccWorth.get().addDefault("MAGENTA_CARPET", 3.57);
        ccWorth.get().addDefault("MAGENTA_CONCRETE", 2.63);
        ccWorth.get().addDefault("MAGENTA_CONCRETE_POWDER", 1.28);
        ccWorth.get().addDefault("MAGENTA_DYE", 2.1);
        ccWorth.get().addDefault("MAGENTA_GLAZED_TERRACOTTA", 1.75);
        ccWorth.get().addDefault("MAGENTA_SHULKER_BOX", 55.6);
        ccWorth.get().addDefault("MAGENTA_STAINED_GLASS", 2.11);
        ccWorth.get().addDefault("MAGENTA_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("MAGENTA_TERRACOTTA", 1.56);
        ccWorth.get().addDefault("MAGENTA_WOOL", 5.3);
        ccWorth.get().addDefault("MAGMA_BLOCK", 10.7);
        ccWorth.get().addDefault("MAGMA_CREAM", 2.65);
        ccWorth.get().addDefault("MAGMA_CUBE_SPAWN_EGG", 1400);
        ccWorth.get().addDefault("MAP", 63.15);
        ccWorth.get().addDefault("MELON", 2.35);
        ccWorth.get().addDefault("MELON_SEEDS", 0.35);
        ccWorth.get().addDefault("MELON_SLICE", 0.25);
        ccWorth.get().addDefault("MILK_BUCKET", 47.51);
        ccWorth.get().addDefault("MINECART", 76.04);
        ccWorth.get().addDefault("MOOSHROOM_SPAWN_EGG", 175);
        ccWorth.get().addDefault("MOSSY_COBBLESTONE", 1.45);
        ccWorth.get().addDefault("MOSSY_COBBLESTONE_WALL", 1.47);
        ccWorth.get().addDefault("MOSSY_STONE_BRICKS", 1.66);
        ccWorth.get().addDefault("MULE_SPAWN_EGG", 1200);
        ccWorth.get().addDefault("MUSHROOM_STEW", 2.3);
        ccWorth.get().addDefault("MUSIC_DISC_11", 350);
        ccWorth.get().addDefault("MUSIC_DISC_13", 65);
        ccWorth.get().addDefault("MUSIC_DISC_BLOCKS", 350);
        ccWorth.get().addDefault("MUSIC_DISC_CAT", 75);
        ccWorth.get().addDefault("MUSIC_DISC_CHIRP", 350);
        ccWorth.get().addDefault("MUSIC_DISC_FAR", 350);
        ccWorth.get().addDefault("MUSIC_DISC_MALL", 350);
        ccWorth.get().addDefault("MUSIC_DISC_MELLOHI", 350);
        ccWorth.get().addDefault("MUSIC_DISC_STAL", 3500);
        ccWorth.get().addDefault("MUSIC_DISC_STRAD", 3500);
        ccWorth.get().addDefault("MUSIC_DISC_WAIT", 3500);
        ccWorth.get().addDefault("MUSIC_DISC_WARD", 3500);
        ccWorth.get().addDefault("MUTTON", 1.3);
        ccWorth.get().addDefault("MYCELIUM", 5.79);
        ccWorth.get().addDefault("NAME_TAG", 5.0);
        ccWorth.get().addDefault("NETHERRACK", 0.35);
        ccWorth.get().addDefault("NETHER_BRICK", 0.54);
        ccWorth.get().addDefault("NETHER_BRICKS", 2.25);
        ccWorth.get().addDefault("NETHER_BRICK_FENCE", 2.27);
        ccWorth.get().addDefault("NETHER_BRICK_SLAB", 1.14);
        ccWorth.get().addDefault("NETHER_BRICK_STAIRS", 3.4);
        ccWorth.get().addDefault("NETHER_QUARTZ_ORE", 14.34);
        ccWorth.get().addDefault("NETHER_STAR", 50.0);
        ccWorth.get().addDefault("NETHER_WART", 0.55);
        ccWorth.get().addDefault("NETHER_WART_BLOCK", 5.05);
        ccWorth.get().addDefault("NOTE_BLOCK", 3.95);
        ccWorth.get().addDefault("OAK_BOAT", 2.1);
        ccWorth.get().addDefault("OAK_BUTTON", 0.48);
        ccWorth.get().addDefault("OAK_DOOR", 0.83);
        ccWorth.get().addDefault("OAK_FENCE", 0.73);
        ccWorth.get().addDefault("OAK_FENCE_GATE", 1.9);
        ccWorth.get().addDefault("OAK_LEAVES", 0.25);
        ccWorth.get().addDefault("OAK_LOG", 1.5);
        ccWorth.get().addDefault("OAK_PLANKS", 0.48);
        ccWorth.get().addDefault("OAK_PRESSURE_PLATE", 0.9);
        ccWorth.get().addDefault("OAK_SAPLING", 0.65);
        ccWorth.get().addDefault("OAK_SLAB", 0.25);
        ccWorth.get().addDefault("OAK_STAIRS", 0.74);
        ccWorth.get().addDefault("OAK_TRAPDOOR", 1.25);
        ccWorth.get().addDefault("OAK_WOOD", 1.5);
        ccWorth.get().addDefault("OBSERVER", 4.15);
        ccWorth.get().addDefault("OBSIDIAN", 10.0);
        ccWorth.get().addDefault("OCELOT_SPAWN_EGG", 35);
        ccWorth.get().addDefault("ORANGE_BANNER", 32.15);
        ccWorth.get().addDefault("ORANGE_BED", 17.2);
        ccWorth.get().addDefault("ORANGE_CARPET", 5.35);
        ccWorth.get().addDefault("ORANGE_CONCRETE", 2.63);
        ccWorth.get().addDefault("ORANGE_CONCRETE_POWDER", 1.28);
        ccWorth.get().addDefault("ORANGE_DYE", 2.1);
        ccWorth.get().addDefault("ORANGE_GLAZED_TERRACOTTA", 1.75);
        ccWorth.get().addDefault("ORANGE_SHULKER_BOX", 55.6);
        ccWorth.get().addDefault("ORANGE_STAINED_GLASS", 2.11);
        ccWorth.get().addDefault("ORANGE_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("ORANGE_TERRACOTTA", 1.56);
        ccWorth.get().addDefault("ORANGE_TULIP", 2.0);
        ccWorth.get().addDefault("ORANGE_WOOL", 5.3);
        ccWorth.get().addDefault("OXEYE_DAISY", 2.0);
        ccWorth.get().addDefault("PACKED_ICE", 7.89);
        ccWorth.get().addDefault("PAINTING", 5.2);
        ccWorth.get().addDefault("PAPER", 0.19);
        ccWorth.get().addDefault("PEONY", 1.75);
        ccWorth.get().addDefault("PIG_SPAWN_EGG", 65);
        ccWorth.get().addDefault("PINK_BANNER", 32.15);
        ccWorth.get().addDefault("PINK_BED", 17.2);
        ccWorth.get().addDefault("PINK_CARPET", 3.57);
        ccWorth.get().addDefault("PINK_CONCRETE", 2.63);
        ccWorth.get().addDefault("PINK_CONCRETE_POWDER", 1.28);
        ccWorth.get().addDefault("PINK_DYE", 2.1);
        ccWorth.get().addDefault("PINK_GLAZED_TERRACOTTA", 1.75);
        ccWorth.get().addDefault("PINK_SHULKER_BOX", 55.5);
        ccWorth.get().addDefault("PINK_STAINED_GLASS", 2.11);
        ccWorth.get().addDefault("PINK_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("PINK_TERRACOTTA", 1.56);
        ccWorth.get().addDefault("PINK_TULIP", 2.0);
        ccWorth.get().addDefault("PINK_WOOL", 5.3);
        ccWorth.get().addDefault("PISTON", 17.94);
        ccWorth.get().addDefault("PODZOL", 3.79);
        ccWorth.get().addDefault("POISONOUS_POTATO", 0.45);
        ccWorth.get().addDefault("POLAR_BEAR_SPAWN_EGG", 45);
        ccWorth.get().addDefault("POLISHED_ANDESITE", 1.13);
        ccWorth.get().addDefault("POLISHED_DIORITE", 1.83);
        ccWorth.get().addDefault("POLISHED_GRANITE", 3.48);
        ccWorth.get().addDefault("POPPED_CHORUS_FRUIT", 5.19);
        ccWorth.get().addDefault("POPPY", 1.55);
        ccWorth.get().addDefault("PORKCHOP", 1.25);
        ccWorth.get().addDefault("POTATO", 1.35);
        ccWorth.get().addDefault("POWERED_RAIL", 60.35);
        ccWorth.get().addDefault("PRISMARINE", 7.1);
        ccWorth.get().addDefault("PRISMARINE_BRICKS", 15.85);
        ccWorth.get().addDefault("PRISMARINE_CRYSTALS", 2.5);
        ccWorth.get().addDefault("PRISMARINE_SHARD", 1.75);
        ccWorth.get().addDefault("PUFFERFISH", 1.0);
        ccWorth.get().addDefault("PUMPKIN", 0.35);
        ccWorth.get().addDefault("PUMPKIN_PIE", 1.71);
        ccWorth.get().addDefault("PUMPKIN_SEEDS", 0.11);
        ccWorth.get().addDefault("PURPLE_BANNER", 28.03);
        ccWorth.get().addDefault("PURPLE_BED", 15.14);
        ccWorth.get().addDefault("PURPLE_CARPET", 3.11);
        ccWorth.get().addDefault("PURPLE_CONCRETE", 2.54);
        ccWorth.get().addDefault("PURPLE_CONCRETE_POWDER", 1.19);
        ccWorth.get().addDefault("PURPLE_DYE", 1.41);
        ccWorth.get().addDefault("PURPLE_GLAZED_TERRACOTTA", 1.66);
        ccWorth.get().addDefault("PURPLE_SHULKER_BOX", 53.4);
        ccWorth.get().addDefault("PURPLE_STAINED_GLASS", 2.03);
        ccWorth.get().addDefault("PURPLE_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("PURPLE_TERRACOTTA", 1.48);
        ccWorth.get().addDefault("PURPLE_WOOL", 4.61);
        ccWorth.get().addDefault("PURPUR_BLOCK", 5.21);
        ccWorth.get().addDefault("PURPUR_PILLAR", 5.35);
        ccWorth.get().addDefault("PURPUR_SLAB", 2.62);
        ccWorth.get().addDefault("PURPUR_STAIRS", 7.84);
        ccWorth.get().addDefault("QUARTZ", 1.55);
        ccWorth.get().addDefault("QUARTZ_BLOCK", 6.3);
        ccWorth.get().addDefault("QUARTZ_PILLAR", 6.3);
        ccWorth.get().addDefault("QUARTZ_SLAB", 3.17);
        ccWorth.get().addDefault("QUARTZ_STAIRS", 9.48);
        ccWorth.get().addDefault("RABBIT", 1.35);
        ccWorth.get().addDefault("RABBIT_FOOT", 1.75);
        ccWorth.get().addDefault("RABBIT_HIDE", 0.5);
        ccWorth.get().addDefault("RABBIT_SPAWN_EGG", 75);
        ccWorth.get().addDefault("RABBIT_STEW", 5.88);
        ccWorth.get().addDefault("RAIL", 5.72);
        ccWorth.get().addDefault("REDSTONE", 0.65);
        ccWorth.get().addDefault("REDSTONE_BLOCK", 5.85);
        ccWorth.get().addDefault("REDSTONE_LAMP", 9);
        ccWorth.get().addDefault("REDSTONE_ORE", 63.7);
        ccWorth.get().addDefault("REDSTONE_TORCH", 1);
        ccWorth.get().addDefault("RED_BANNER", 24.2);
        ccWorth.get().addDefault("RED_BED", 13.25);
        ccWorth.get().addDefault("RED_CARPET", 2.68);
        ccWorth.get().addDefault("RED_CONCRETE", 2.46);
        ccWorth.get().addDefault("RED_CONCRETE_POWDER", 1.11);
        ccWorth.get().addDefault("RED_GLAZED_TERRACOTTA", 1.58);
        ccWorth.get().addDefault("RED_MUSHROOM", 0.75);
        ccWorth.get().addDefault("RED_SAND", 1.65);
        ccWorth.get().addDefault("RED_SANDSTONE", 6.7);
        ccWorth.get().addDefault("RED_SANDSTONE_SLAB", 3.37);
        ccWorth.get().addDefault("RED_SANDSTONE_STAIRS", 10.08);
        ccWorth.get().addDefault("RED_SHULKER_BOX", 54.28);
        ccWorth.get().addDefault("RED_STAINED_GLASS", 1.95);
        ccWorth.get().addDefault("RED_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("RED_TERRACOTTA", 1.4);
        ccWorth.get().addDefault("RED_TULIP", 2.0);
        ccWorth.get().addDefault("RED_WOOL", 3.98);
        ccWorth.get().addDefault("REPEATER", 3.91);
        ccWorth.get().addDefault("ROSE_BUSH", 1.45);
        ccWorth.get().addDefault("ROSE_RED", 0.78);
        ccWorth.get().addDefault("ROTTEN_FLESH", 0.1);
        ccWorth.get().addDefault("SADDLE", 5.0);
        ccWorth.get().addDefault("SAND", 1.65);
        ccWorth.get().addDefault("SANDSTONE", 6.7);
        ccWorth.get().addDefault("SANDSTONE_SLAB", 3.37);
        ccWorth.get().addDefault("SANDSTONE_STAIRS", 10.08);
        ccWorth.get().addDefault("SEA_LANTERN", 19.6);
        ccWorth.get().addDefault("SHEARS", 30.48);
        ccWorth.get().addDefault("SHEEP_SPAWN_EGG", 95);
        ccWorth.get().addDefault("SHIELD", 17.69);
        ccWorth.get().addDefault("SHULKER_BOX", 55);
        ccWorth.get().addDefault("SHULKER_SHELL", 25);
        ccWorth.get().addDefault("SHULKER_SPAWN_EGG", 3500);
        ccWorth.get().addDefault("SIGN", 0.92);
        ccWorth.get().addDefault("SKELETON_HORSE_SPAWN_EGG", 360000);
        ccWorth.get().addDefault("SKELETON_SKULL", 7550.0);
        ccWorth.get().addDefault("SKELETON_SPAWN_EGG", 35);
        ccWorth.get().addDefault("SLIME_BLOCK", 11.35);
        ccWorth.get().addDefault("SLIME_SPAWN_EGG", 65);
        ccWorth.get().addDefault("SMOOTH_RED_SANDSTONE", 6.73);
        ccWorth.get().addDefault("SMOOTH_SANDSTONE", 6.73);
        ccWorth.get().addDefault("SNOWBALL", 0.1);
        ccWorth.get().addDefault("SNOW_BLOCK", 0.5);
        ccWorth.get().addDefault("SOUL_SAND", 1.05);
        ccWorth.get().addDefault("SPIDER_EYE", 1.05);
        ccWorth.get().addDefault("SPIDER_SPAWN_EGG", 45);
        ccWorth.get().addDefault("SPONGE", 3.64);
        ccWorth.get().addDefault("SPRUCE_BOAT", 2.1);
        ccWorth.get().addDefault("SPRUCE_BUTTON", 0.48);
        ccWorth.get().addDefault("SPRUCE_DOOR", 0.83);
        ccWorth.get().addDefault("SPRUCE_FENCE", 0.73);
        ccWorth.get().addDefault("SPRUCE_FENCE_GATE", 1.9);
        ccWorth.get().addDefault("SPRUCE_LEAVES", 0.25);
        ccWorth.get().addDefault("SPRUCE_LOG", 1.5);
        ccWorth.get().addDefault("SPRUCE_PLANKS", 0.48);
        ccWorth.get().addDefault("SPRUCE_PRESSURE_PLATE", 0.9);
        ccWorth.get().addDefault("SPRUCE_SAPLING", 0.65);
        ccWorth.get().addDefault("SPRUCE_SLAB", 0.22);
        ccWorth.get().addDefault("SPRUCE_STAIRS", 0.63);
        ccWorth.get().addDefault("SPRUCE_TRAPDOOR", 1.25);
        ccWorth.get().addDefault("SPRUCE_WOOD", 1.5);
        ccWorth.get().addDefault("SQUID_SPAWN_EGG", 30);
        ccWorth.get().addDefault("STICK", 0.25);
        ccWorth.get().addDefault("STICKY_PISTON", 19.29);
        ccWorth.get().addDefault("STONE", 0.39);
        ccWorth.get().addDefault("STONE_AXE", 1.2);
        ccWorth.get().addDefault("STONE_BRICKS", 0.41);
        ccWorth.get().addDefault("STONE_BRICK_SLAB", 0.21);
        ccWorth.get().addDefault("STONE_BRICK_STAIRS", 0.64);
        ccWorth.get().addDefault("STONE_BUTTON", 0.49);
        ccWorth.get().addDefault("STONE_HOE", 1.0);
        ccWorth.get().addDefault("STONE_PICKAXE", 1.2);
        ccWorth.get().addDefault("STONE_PRESSURE_PLATE", 0.88);
        ccWorth.get().addDefault("STONE_SHOVEL", 0.8);
        ccWorth.get().addDefault("STONE_SWORD", 0.75);
        ccWorth.get().addDefault("STRAY_SPAWN_EGG", 12500);
        ccWorth.get().addDefault("STRING", 0.75);
        ccWorth.get().addDefault("STRIPPED_ACACIA_LOG", 1.5);
        ccWorth.get().addDefault("SUGAR", 0.26);
        ccWorth.get().addDefault("SUGAR_CANE", 0.16);
        ccWorth.get().addDefault("SUNFLOWER", 1.65);
        ccWorth.get().addDefault("TALL_GRASS", 0.2);
        ccWorth.get().addDefault("TERRACOTTA", 1.29);
        ccWorth.get().addDefault("TNT", 12.95);
        ccWorth.get().addDefault("TNT_MINECART", 89.09);
        ccWorth.get().addDefault("TORCH", 0.46);
        ccWorth.get().addDefault("TOTEM_OF_UNDYING", 2500);
        ccWorth.get().addDefault("TRAPPED_CHEST", 19.24);
        ccWorth.get().addDefault("TRIPWIRE_HOOK", 15.94);
        ccWorth.get().addDefault("VEX_SPAWN_EGG", 14500);
        ccWorth.get().addDefault("VILLAGER_SPAWN_EGG", 65);
        ccWorth.get().addDefault("VINDICATOR_SPAWN_EGG", 1235000);
        ccWorth.get().addDefault("VINE", 1.15);
        ccWorth.get().addDefault("WATER_BUCKET", 47.01);
        ccWorth.get().addDefault("WET_SPONGE", 3.45);
        ccWorth.get().addDefault("WHEAT", 0.75);
        ccWorth.get().addDefault("WHEAT_SEEDS", 0.15);
        ccWorth.get().addDefault("WHITE_BANNER", 18.95);
        ccWorth.get().addDefault("WHITE_BED", 10.6);
        ccWorth.get().addDefault("WHITE_CARPET", 2.1);
        ccWorth.get().addDefault("WHITE_CONCRETE", 2.47);
        ccWorth.get().addDefault("WHITE_CONCRETE_POWDER", 1.12);
        ccWorth.get().addDefault("WHITE_GLAZED_TERRACOTTA", 1.59);
        ccWorth.get().addDefault("WHITE_SHULKER_BOX", 54.35);
        ccWorth.get().addDefault("WHITE_STAINED_GLASS", 1.96);
        ccWorth.get().addDefault("WHITE_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("WHITE_TERRACOTTA", 1.41);
        ccWorth.get().addDefault("WHITE_TULIP", 2.0);
        ccWorth.get().addDefault("WHITE_WOOL", 3.1);
        ccWorth.get().addDefault("WITCH_SPAWN_EGG", 125);
        ccWorth.get().addDefault("WITHER_SKELETON_SPAWN_EGG", 1350);
        ccWorth.get().addDefault("WOLF_SPAWN_EGG", 15);
        ccWorth.get().addDefault("WOODEN_AXE", 1.8);
        ccWorth.get().addDefault("WOODEN_HOE", 1.4);
        ccWorth.get().addDefault("WOODEN_PICKAXE", 1.8);
        ccWorth.get().addDefault("WOODEN_SHOVEL", 1.0);
        ccWorth.get().addDefault("WOODEN_SWORD", 1.4);
        ccWorth.get().addDefault("YELLOW_BANNER", 24.8);
        ccWorth.get().addDefault("YELLOW_BED", 13.53);
        ccWorth.get().addDefault("YELLOW_CARPET", 2.75);
        ccWorth.get().addDefault("YELLOW_CONCRETE", 2.47);
        ccWorth.get().addDefault("YELLOW_CONCRETE_POWDER", 1.12);
        ccWorth.get().addDefault("YELLOW_GLAZED_TERRACOTTA", 1.6);
        ccWorth.get().addDefault("YELLOW_SHULKER_BOX", 54.75);
        ccWorth.get().addDefault("YELLOW_STAINED_GLASS", 1.96);
        ccWorth.get().addDefault("YELLOW_STAINED_GLASS_PANE", 0.76);
        ccWorth.get().addDefault("YELLOW_TERRACOTTA", 1.41);
        ccWorth.get().addDefault("YELLOW_WOOL", 4.08);
        ccWorth.get().addDefault("ZOMBIE_HEAD", 7500.0);
        ccWorth.get().addDefault("ZOMBIE_HORSE_SPAWN_EGG", 12500);
        ccWorth.get().addDefault("ZOMBIE_PIGMAN_SPAWN_EGG", 65);
        ccWorth.get().addDefault("ZOMBIE_SPAWN_EGG", 25);
        ccWorth.get().addDefault("ZOMBIE_VILLAGER_SPAWN_EGG", 136);

        ccWorth.get().options().copyDefaults(true);
        ccWorth.save();
    }

    private void SetupSoundFile()
    {
        ccSound.setup("Sound",null);
        ccSound.get().options().header("Enter 0 to mute.\nhttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html");
        ccSound.get().addDefault("sell", "ENTITY_EXPERIENCE_ORB_PICKUP");
        ccSound.get().addDefault("buy", "ENTITY_EXPERIENCE_ORB_PICKUP");
        ccSound.get().addDefault("editItem", "ENTITY_PAINTING_PLACE");
        ccSound.get().addDefault("deleteItem", "BLOCK_GRAVEL_BREAK");
        ccSound.get().addDefault("addItem", "BLOCK_GRAVEL_PLACE");
        ccSound.get().addDefault("click", "BLOCK_METAL_STEP");
        ccSound.get().addDefault("tradeview", "ENTITY_CHICKEN_EGG");
        ccSound.get().options().copyDefaults(true);
        ccSound.save();
    }

    public static void SetupLogFile()
    {
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyMMdd-HHmmss");
        String timeStr = sdf.format (System.currentTimeMillis());
        ccLog.setup("Log_"+timeStr,"Log");
        ccLog.get().options().copyDefaults(true);
        ccLog.save();
    }

    // 명령어 자동완성
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if(!(sender instanceof Player)) return null;

        String senderUuid = ((Player) sender).getUniqueId().toString();

        try
        {
            ArrayList<String> temp = new ArrayList<>();
            ArrayList<String> alist = new ArrayList<>();

            if(cmd.getName().equalsIgnoreCase("shop") && args.length == 1)
            {
                if(!getConfig().getBoolean("UseShopCommand")) return alist;

                for (String s:ccShop.get().getKeys(false))
                {
                    ConfigurationSection options = ccShop.get().getConfigurationSection(s).getConfigurationSection("Options");

                    if(options.contains("flag.signshop") && !sender.hasPermission("dshop.admin.remoteaccess")) continue;

                    temp.add(s);
                }

                for (String s:temp)
                {
                    if(s.startsWith(args[0]) || s.toLowerCase().startsWith(args[0])) alist.add(s);
                }
                return alist;
            }
            else if(cmd.getName().equalsIgnoreCase("DynamicShop"))
            {
                if(args.length == 1)
                {
                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("main"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","main");
                        ShowHelp("main",(Player)sender,args);
                    }

                    temp.add("shop");
                    temp.add("qsell");
                    if(sender.hasPermission("dshop.admin.createshop")) temp.add("createshop");
                    if(sender.hasPermission("dshop.admin.deleteshop")) temp.add("deleteshop");
                    if(sender.hasPermission("dshop.admin.mergeshop")) temp.add("mergeshop");
                    if(sender.hasPermission("dshop.admin.renameshop")) temp.add("renameshop");
                    if(sender.hasPermission("dshop.admin.settax")) temp.add("settax");
                    if(sender.hasPermission("dshop.admin.setdefaultshop")) temp.add("setdefaultshop");
                    if(sender.hasPermission("dshop.admin.deleteOldUser")) temp.add("deleteOldUser");
                    if(sender.hasPermission("dshop.admin.convert")) temp.add("convert");
                    if(sender.hasPermission("dshop.admin.reload")) temp.add("reload");
                    temp.add("cmdHelp");

                    for (String s:temp)
                    {
                        if(s.startsWith(args[0])) alist.add(s);
                    }
                }
                else if(args.length >= 2 && args[0].equals("shop"))
                {
                    if(args.length == 2)
                    {
                        if(!ccUser.get().getString(senderUuid + ".tmpString").equals("shop"))
                        {
                            ccUser.get().set(senderUuid + ".tmpString","shop");
                            ShowHelp("shop",(Player)sender,args);
                        }

                        for (String s:ccShop.get().getKeys(false))
                        {
                            ConfigurationSection options = ccShop.get().getConfigurationSection(s).getConfigurationSection("Options");

                            if(options.contains("flag") && options.getConfigurationSection("flag").contains("signshop") && !sender.hasPermission("dshop.admin.remoteaccess")) continue;

                            temp.add(s);
                        }

                        for (String s:temp)
                        {
                            if(s.startsWith(args[1]) || s.toLowerCase().startsWith(args[1])) alist.add(s);
                        }
                    }
                    else if(args.length >= 3 && (!ccShop.get().contains(args[1]) || args[1].length() == 0))
                    {
                        return null;
                    }
                    else if(args.length == 3)
                    {
                        //add,addhand,edit,editall,permission,maxpage,flag
                        if(sender.hasPermission("dshop.admin.shopedit"))
                        {
                            temp.add("add");
                            temp.add("addhand");
                            temp.add("edit");
                            temp.add("editall");
                            temp.add("permission");
                            temp.add("maxpage");
                            temp.add("flag");
                            temp.add("position");
                            temp.add("shophours");
                            temp.add("fluctuation");
                            temp.add("stockStabilizing");
                            temp.add("account");
                            temp.add("hideStock");
                            temp.add("hidePricingType");
                            temp.add("sellbuy");
                            temp.add("log");
                        }

                        for (String s:temp)
                        {
                            if(s.startsWith(args[2])) alist.add(s);
                        }
                    }
                    else if(args.length >= 4)
                    {
                        if(args[2].equalsIgnoreCase("addhand") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("addhand"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","addhand");
                                ShowHelp("addhand",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("add") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                if(!ccUser.get().getString(senderUuid + ".tmpString").equals("add"))
                                {
                                    ccUser.get().set(senderUuid + ".tmpString","add");
                                    ShowHelp("add",(Player)sender,args);
                                }

                                for (Material m: Material.values())
                                {
                                    temp.add(m.name());
                                }

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3].toUpperCase())) alist.add(s);
                                }
                            }
                            else if(args.length == 5)
                            {
                                String mat = args[3].toUpperCase();
                                if(!(ccUser.get().getString(senderUuid + ".tmpString").contains("add") &&
                                        ccUser.get().getString(senderUuid + ".tmpString").length() > 3))
                                {
                                    if(Material.matchMaterial(mat) != null)
                                    {
                                        ccUser.get().set(senderUuid + ".tmpString","add"+args[3]);
                                        ShowHelp("add"+args[3],(Player)sender,args);
                                    }
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("edit") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                if(!ccUser.get().getString(senderUuid + ".tmpString").equals("edit"))
                                {
                                    ccUser.get().set(senderUuid + ".tmpString","edit");
                                    ShowHelp("edit",(Player)sender,args);
                                }

                                String shopName = args[1];

                                for (String s:ccShop.get().getConfigurationSection(shopName).getKeys(false))
                                {
                                    try
                                    {
                                        int i = Integer.parseInt(s);
                                        if(!ccShop.get().contains(shopName+"."+s+".value")) continue; // 장식용임
                                        temp.add(ccShop.get().getConfigurationSection(shopName+"."+s).getName()+"/"+ccShop.get().getString(shopName+"." + s +".mat"));
                                    }
                                    catch (Exception ignored){}
                                }

                                for (String s:temp)
                                {
                                    String upper = args[3].toUpperCase();

                                    if(s.startsWith(upper)) alist.add(s);
                                }
                            }
                            else if(args.length == 5)
                            {
                                String mat = args[3];
                                mat = mat.substring(mat.indexOf("/")+1);
                                mat = mat.toUpperCase();

                                if(!(ccUser.get().getString(senderUuid + ".tmpString").equals("edit"+mat)))
                                {
                                    if(Material.matchMaterial(mat) != null)
                                    {
                                        ccUser.get().set(senderUuid + ".tmpString","edit"+mat);
                                        ShowHelp("edit"+mat,(Player)sender,args);
                                    }
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("editall") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("editall"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","editall");
                                ShowHelp("editall",(Player)sender,args);
                            }
                            if(args.length == 4)
                            {
                                temp.add("value");
                                temp.add("valueMin");
                                temp.add("valueMax");
                                temp.add("stock");
                                temp.add("median");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }
                            else if(args.length == 5)
                            {
                                temp.add("=");
                                temp.add("+");
                                temp.add("-");
                                temp.add("/");
                                temp.add("*");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[4])) alist.add(s);
                                }
                            }
                            else if(args.length == 6)
                            {
                                if(args[4].equals("="))
                                {
                                    temp.add("value");
                                    temp.add("valueMin");
                                    temp.add("valueMax");
                                    temp.add("stock");
                                    temp.add("median");

                                    for (String s:temp)
                                    {
                                        if(s.startsWith(args[5])) alist.add(s);
                                    }
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("permission") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("permission"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","permission");
                                ShowHelp("permission",(Player)sender,args);
                            }
                            if(args.length >= 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("maxpage") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("maxpage"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","maxpage");
                                ShowHelp("maxpage",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("flag") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("signshop");
                                temp.add("localshop");
                                temp.add("deliverycharge");
                                temp.add("jobpoint");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }
                            else if(args.length > 4)
                            {
                                temp.add("set");
                                temp.add("unset");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[4])) alist.add(s);
                                }
                            }

                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("flag"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","flag");
                                ShowHelp("flag",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("position") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length >= 4)
                            {
                                temp.add("pos1");
                                temp.add("pos2");
                                temp.add("clear");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }

                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("position"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","position");
                                ShowHelp("position",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("shophours") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("shophours"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","shophours");
                                ShowHelp("shophours",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("fluctuation") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("off");
                                temp.add("30m");
                                temp.add("1h");
                                temp.add("2h");
                                temp.add("4h");
                                temp.add("12h");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }

                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("fluctuation"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","fluctuation");
                                ShowHelp("fluctuation",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("stockStabilizing") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("off");
                                temp.add("30m");
                                temp.add("1h");
                                temp.add("2h");
                                temp.add("4h");
                                temp.add("12h");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }
                            }

                            if(!ccUser.get().getString(senderUuid + ".tmpString").equals("stockStabilizing"))
                            {
                                ccUser.get().set(senderUuid + ".tmpString","stockStabilizing");
                                ShowHelp("stockStabilizing",(Player)sender,args);
                            }
                        }
                        else if(args[2].equalsIgnoreCase("account") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("set");
                                temp.add("linkto");
                                temp.add("transfer");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!ccUser.get().getString(senderUuid + ".tmpString").equals("account"))
                                {
                                    ccUser.get().set(senderUuid + ".tmpString","account");
                                    ShowHelp("account",(Player)sender,args);
                                }
                            }
                            else if(args.length == 5)
                            {
                                if(args[3].equals("linkto") || args[3].equals("transfer"))
                                {
                                    temp.addAll(ccShop.get().getKeys(false));
                                }

                                if(args[3].equals("set"))
                                {
                                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("accountSet"))
                                    {
                                        ccUser.get().set(senderUuid + ".tmpString","accountSet");
                                        ShowHelp("accountSet",(Player)sender,args);
                                    }
                                }
                                else if(args[3].equals("transfer"))
                                {
                                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("accountTransfer"))
                                    {
                                        ccUser.get().set(senderUuid + ".tmpString","accountTransfer");
                                        ShowHelp("accountTransfer",(Player)sender,args);
                                    }

                                    for (Player p:Bukkit.getServer().getOnlinePlayers())
                                    {
                                        temp.add(p.getName());
                                    }
                                }
                                else if(args[3].equals("linkto"))
                                {
                                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("accountLinkto"))
                                    {
                                        ccUser.get().set(senderUuid + ".tmpString","accountLinkto");
                                        ShowHelp("accountLinkto",(Player)sender,args);
                                    }
                                }

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[4])) alist.add(s);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("hideStock") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!ccUser.get().getString(senderUuid + ".tmpString").equals("hideStock"))
                                {
                                    ccUser.get().set(senderUuid + ".tmpString","hideStock");
                                    ShowHelp("hideStock",(Player)sender,args);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("hidePricingType") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("true");
                                temp.add("false");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!ccUser.get().getString(senderUuid + ".tmpString").equals("hidePricingType"))
                                {
                                    ccUser.get().set(senderUuid + ".tmpString","hidePricingType");
                                    ShowHelp("hidePricingType",(Player)sender,args);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("sellbuy") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("SellOnly");
                                temp.add("BuyOnly");
                                temp.add("Clear");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!ccUser.get().getString(senderUuid + ".tmpString").equals("sellbuy"))
                                {
                                    ccUser.get().set(senderUuid + ".tmpString","sellbuy");
                                    ShowHelp("sellbuy",(Player)sender,args);
                                }
                            }
                        }
                        else if(args[2].equalsIgnoreCase("log") && sender.hasPermission("dshop.admin.shopedit"))
                        {
                            if(args.length == 4)
                            {
                                temp.add("enable");
                                temp.add("disable");
                                temp.add("clear");

                                for (String s:temp)
                                {
                                    if(s.startsWith(args[3])) alist.add(s);
                                }

                                if(!ccUser.get().getString(senderUuid + ".tmpString").equals("log"))
                                {
                                    ccUser.get().set(senderUuid + ".tmpString","log");
                                    ShowHelp("log",(Player)sender,args);
                                }
                            }
                        }
                    }
                }
                else if(args[0].equalsIgnoreCase("createshop") && sender.hasPermission("dshop.admin.createshop"))
                {
                    if (args.length == 3)
                    {
                        temp.add("true");
                        temp.add("false");

                        for (String s:temp)
                        {
                            if(s.startsWith(args[2])) alist.add(s);
                        }
                    }

                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("createshop"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","createshop");
                        ShowHelp("createshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("deleteshop") && sender.hasPermission("dshop.admin.deleteshop"))
                {
                    temp.addAll(ccShop.get().getKeys(false));

                    for (String s:temp)
                    {
                        if(s.startsWith(args[1])) alist.add(s);
                    }

                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("deleteshop"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","deleteshop");
                        ShowHelp("deleteshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("mergeshop") && sender.hasPermission("dshop.admin.mergeshop"))
                {
                    if (args.length <= 3)
                    {
                        temp.addAll(ccShop.get().getKeys(false));

                        for (String s:temp)
                        {
                            if(s.startsWith(args[args.length-1])) alist.add(s);
                        }
                    }

                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("mergeshop"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","mergeshop");
                        ShowHelp("mergeshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("renameshop") && sender.hasPermission("dshop.admin.renameshop"))
                {
                    if(args.length == 2)
                    {
                        temp.addAll(ccShop.get().getKeys(false));

                        for (String s:temp)
                        {
                            if(s.startsWith(args[1])) alist.add(s);
                        }
                    }

                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("renameshop"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","renameshop");
                        ShowHelp("renameshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("cmdHelp"))
                {
                    if(args.length == 2)
                    {
                        alist.add("on");
                        alist.add("off");

                        if(!ccUser.get().getString(senderUuid + ".tmpString").equals("cmdHelp"))
                        {
                            ccUser.get().set(senderUuid + ".tmpString","cmdHelp");
                            ShowHelp("cmdHelp",(Player)sender,args);
                        }
                    }
                }
                else if(args[0].equalsIgnoreCase("settax"))
                {
                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("settax"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","settax");
                        ShowHelp("settax",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("setdefaultshop"))
                {
                    temp.addAll(ccShop.get().getKeys(false));

                    for (String s:temp)
                    {
                        if(s.startsWith(args[1])) alist.add(s);
                    }

                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("setdefaultshop"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","setdefaultshop");
                        ShowHelp("setdefaultshop",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("deleteOldUser"))
                {
                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("deleteOldUser"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","deleteOldUser");
                        ShowHelp("deleteOldUser",(Player)sender,args);
                    }
                }
                else if(args[0].equalsIgnoreCase("convert"))
                {
                    if(!sender.hasPermission("dshop.admin.convert")) return null;

                    if(args.length == 2)
                    {
                        temp.add("Shop");
                    }

                    for (String s:temp)
                    {
                        if(s.startsWith(args[1])) alist.add(s);
                    }

                    if(!ccUser.get().getString(senderUuid + ".tmpString").equals("convert"))
                    {
                        ccUser.get().set(senderUuid + ".tmpString","convert");
                        ShowHelp("convert",(Player)sender,args);
                    }
                }

                return alist;
            }
        }catch (Exception e){
            return null;
        }

        return null;
    }

    // 명령어 도움말 표시
    private void ShowHelp(String helpcode, Player player, String[] args)
    {
        if(!ccUser.get().getBoolean(player.getUniqueId() + ".cmdHelp")) return;

        if(helpcode.equals("main"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","main"));
            player.sendMessage(" - shop: " + ccLang.get().getString("HELP.SHOP"));
            player.sendMessage(" - qsell: " + ccLang.get().getString("HELP.QSELL"));
            player.sendMessage(" - cmdHelp: " + ccLang.get().getString("HELP.CMD"));
            if(player.hasPermission("dshop.admin.createshop"))player.sendMessage("§e - createshop: "+ccLang.get().getString("HELP.CREATESHOP"));
            if(player.hasPermission("dshop.admin.deleteshop"))player.sendMessage("§e - deleteshop: "+ccLang.get().getString("HELP.DELETESHOP"));
            if(player.hasPermission("dshop.admin.mergeshop"))player.sendMessage("§e - mergeshop");
            if(player.hasPermission("dshop.admin.renameshop"))player.sendMessage("§e - renameshop");
            if(player.hasPermission("dshop.admin.setdefaultshop"))player.sendMessage("§e - setdefaultshop ");
            if(player.hasPermission("dshop.admin.settax"))player.sendMessage("§e - settax: "+ccLang.get().getString("HELP.SETTAX"));
            if(player.hasPermission("dshop.admin.deleteOldUser"))player.sendMessage("§e - deleteOldUser: " + ccLang.get().getString("HELP.DELETE_OLD_USER"));
            if(player.hasPermission("dshop.admin.convert"))player.sendMessage("§e - convert: " + ccLang.get().getString("HELP.CONVERT"));
            if(player.hasPermission("dshop.admin.reload"))player.sendMessage("§e - reload: " + ccLang.get().getString("HELP.RELOAD"));
            player.sendMessage("");
        }
        else if(helpcode.equals("shop"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","shop"));

            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop [<shopname>]");
            if(player.hasPermission("dshop.admin.shopedit")||player.hasPermission("dshop.admin.shopedit")||player.hasPermission("dshop.admin.editall"))
            {
                player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE")
                        + ": /ds shop <shopname> <addhand | add | edit | editall | sellbuy | permission | maxpage | flag | position | shophours | fluctuation | stockStabilizing | hideStock | account | log>");
            }

            if(player.hasPermission("dshop.admin.shopedit")) player.sendMessage("§e - addhand: " + ccLang.get().getString("HELP.SHOPADDHAND"));
            if(player.hasPermission("dshop.admin.shopedit")) player.sendMessage("§e - add: " + ccLang.get().getString("HELP.SHOPADDITEM"));
            if(player.hasPermission("dshop.admin.shopedit")) player.sendMessage("§e - edit: " + ccLang.get().getString("HELP.SHOPEDIT"));
            if(player.hasPermission("dshop.admin.editall")) player.sendMessage("§e - editall: " + ccLang.get().getString("HELP.EDITALL"));
            player.sendMessage("");
        }
        else if(helpcode.equals("addhand") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","addhand"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> addhand <value> <median> <stock>");
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> addhand <value> <min value> <max value> <median> <stock>");
            player.sendMessage(" - " + ccLang.get().getString("HELP.SHOPADDHAND"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.PRICE"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.INF_STATIC"));

            ItemStack tempItem = player.getInventory().getItemInMainHand();

            if(tempItem != null && tempItem.getType() != Material.AIR)
            {
                int idx = DynaShopAPI.FindItemFromShop(args[1], tempItem);
                if(idx != -1)
                {
                    player.sendMessage("");
                    DynaShopAPI.SendItemInfo(player,args[1],idx,"HELP.ITEM_ALREADY_EXIST");
                }
            }
            else
            {
                player.sendMessage(" - " + ccLang.get().getString("ERR.HAND_EMPTY2"));
            }

            player.sendMessage("");
        }
        else if(helpcode.startsWith("add") && player.hasPermission("dshop.admin.shopedit"))
        {
            if(helpcode.length() > "add".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3]));
                    int idx = DynaShopAPI.FindItemFromShop(args[1], tempItem);

                    if(idx != -1)
                    {
                        DynaShopAPI.SendItemInfo(player,args[1],idx,"HELP.ITEM_ALREADY_EXIST");
                        player.sendMessage("");
                    }
                }catch (Exception ignored){}
            }
            else
            {
                player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","add"));
                player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <median> <stock>");
                player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <min value> <max value> <median> <stock>");
                player.sendMessage(" - " + ccLang.get().getString("HELP.SHOPADDITEM"));
                player.sendMessage(" - " + ccLang.get().getString("HELP.PRICE"));
                player.sendMessage(" - " + ccLang.get().getString("HELP.INF_STATIC"));

                player.sendMessage("");
            }
        }
        else if(helpcode.contains("edit") && !helpcode.equals("editall") && player.hasPermission("dshop.admin.shopedit"))
        {
            if(helpcode.length() > "edit".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3].substring(args[3].indexOf("/")+1)));
                    int idx = DynaShopAPI.FindItemFromShop(args[1], tempItem);

                    if(idx != -1)
                    {
                        DynaShopAPI.SendItemInfo(player,args[1],idx,"HELP.ITEM_INFO");
                        player.sendMessage(" - " + ccLang.get().getString("HELP.REMOVE_ITEM"));
                        player.sendMessage("");
                    }
                }catch (Exception ignored){}
            }
            else
            {
                player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","edit"));
                player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <median> <stock>");
                player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <min value> <max value> <median> <stock>");
                player.sendMessage(" - " + ccLang.get().getString("HELP.SHOPEDIT"));
                player.sendMessage(" - " + ccLang.get().getString("HELP.PRICE"));
                player.sendMessage(" - " + ccLang.get().getString("HELP.INF_STATIC"));

                player.sendMessage("");
            }
        }
        else if(helpcode.equals("editall") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","editall"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> editall <value | median | stock> <= | + | - | * | /> <amount>");
            player.sendMessage(" - " + ccLang.get().getString("HELP.EDITALL"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.EDITALL2"));

            player.sendMessage("");
        }
        else if(helpcode.equals("cmdHelp"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","cmdHelp"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds cmdHelp <on | off>");
            player.sendMessage(" - " + ccLang.get().getString("HELP.CMD"));

            player.sendMessage("");
        }
        else if(helpcode.equals("createshop") && player.hasPermission("dshop.admin.createshop"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","createshop"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds create <shopname> [<permission>]");
            player.sendMessage(" - " + ccLang.get().getString("HELP.CREATESHOP2"));

            player.sendMessage("");
        }
        else if(helpcode.equals("deleteshop") && player.hasPermission("dshop.admin.deleteshop"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","§c§ldeleteshop§f§r"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds deleteshop <shopname>");

            player.sendMessage("");
        }
        else if(helpcode.equals("mergeshop") && player.hasPermission("dshop.admin.mergeshop"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","mergeshop"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds mergeshop <shop1> <shop2>");

            player.sendMessage("");
        }
        else if(helpcode.equals("renameshop") && player.hasPermission("dshop.admin.renameshop"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","renameshop"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds renameshop <old name> <new name>");

            player.sendMessage("");
        }
        else if(helpcode.equals("permission") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","permission"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> permission [<true | false | custom >]");

            player.sendMessage("");
        }
        else if(helpcode.equals("maxpage") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","maxpage"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> maxpage <number>");

            player.sendMessage("");
        }
        else if(helpcode.equals("flag") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","flag"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> flag <flag> <set | unset>");

            player.sendMessage("");
        }
        else if(helpcode.equals("position") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","position"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> position <pos1 | pos2 | clear>");

            player.sendMessage("");
        }
        else if(helpcode.equals("shophours") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","shophours"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> shophours <open> <close>");

            player.sendMessage("");
        }
        else if(helpcode.equals("fluctuation") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","fluctuation"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> fluctuation <interval> <strength>");

            player.sendMessage("");
        }
        else if(helpcode.equals("stockStabilizing") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","stockStabilizing"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> stockStabilizing <interval> <strength>");

            player.sendMessage("");
        }
        else if(helpcode.equals("hideStock") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","hideStock"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> hideStock <true | false>");

            player.sendMessage("");
        }
        else if(helpcode.equals("hidePricingType") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","hidePricingType"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> hidePricingType <true | false>");

            player.sendMessage("");
        }
        else if(helpcode.equals("account") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","account"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account <set | linkto | transfer>");

            player.sendMessage("");
        }
        else if(helpcode.equals("accountSet") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","account set"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account set <amount>");
            player.sendMessage(" - " + ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        }
        else if(helpcode.equals("accountLinkto") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","account linkto"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account linkto <shopname>");
            //player.sendMessage(" - " + ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        }
        else if(helpcode.equals("accountTransfer") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","account transfer"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shopname> account transfer <target> <amount>");
            //player.sendMessage(" - " + ccLang.get().getString("HELP.ACCOUNT"));

            player.sendMessage("");
        }
        else if(helpcode.equals("settax") && player.hasPermission("dshop.admin.settax"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","settax"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds settax <value>");

            player.sendMessage("");
        }
        else if(helpcode.equals("setdefaultshop") && player.hasPermission("dshop.admin.setdefaultshop"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","setdefaultshop"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds setdefaultshop <shop name>");

            player.sendMessage("");
        }
        else if(helpcode.equals("deleteOldUser") && player.hasPermission("dshop.admin.deleteOldUser"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","§c§ldeleteOldUser§f§r"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds deleteOldUser <days>");
            player.sendMessage(" - " + ccLang.get().getString("HELP.DELETE_OLD_USER"));
            player.sendMessage(" - " + ccLang.get().getString("IRREVERSIBLE"));

            player.sendMessage("");
        }
        else if(helpcode.equals("convert") && player.hasPermission("dshop.admin.convert"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","convert"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds convert <plugin name>");
            player.sendMessage(" - " + "This is beta feature. Currently only support 'Shop'");
            player.sendMessage(" - " + "You need to Copy pages yml file to DynamicShop/Convert/Shop");
            player.sendMessage(" - " + "Item meta will be lost");

            player.sendMessage("");
        }
        else if(helpcode.equals("sellbuy") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","sellbuy"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shop name> sellbuy < sellonly | buyonly | clear >");

            player.sendMessage("");
        }
        else if(helpcode.equals("log") && player.hasPermission("dshop.admin.shopedit"))
        {
            player.sendMessage(dsPrefix + ccLang.get().getString("HELP.TITLE").replace("{command}","log"));
            player.sendMessage(" - " + ccLang.get().getString("HELP.USAGE") + ": /ds shop <shop name> log < enable | disable | clear >");

            player.sendMessage("");
        }
    }

    // 볼트 이코노미 초기화
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            console.sendMessage(dsPrefix_server + " Vault Not Found");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            console.sendMessage(dsPrefix_server + " RSP is null!");
            return false;
        }
        econ = rsp.getProvider();
        console.sendMessage(dsPrefix_server + " Vault Found");
        return econ != null;
    }

    @Override
    public void onDisable() { console.sendMessage(dsPrefix_server + " Disabled"); }

}
