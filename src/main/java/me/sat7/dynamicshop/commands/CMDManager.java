package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.commands.shop.*;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CMDManager
{
    public static final HashMap<String, DSCMD> CMDHashMap = new HashMap<>();

    public static CommandHelp commandHelp;
    public static CreateShop createShop;
    public static DeleteShop deleteShop;
    public static DeleteUser deleteUser;
    public static MergeShop mergeShop;
    public static OpenShop openShop;
    public static Reload reload;
    public static RenameShop renameShop;
    public static SetDefaultShop setDefaultShop;
    public static SetTax setTax;

    public static Account account;
    public static Add add;
    public static AddHand addHand;
    public static Edit edit;
    public static EditAll editAll;
    public static Enable enable;
    public static Flag flag;
    public static Fluctuation fluctuation;
    public static Log log;
    public static MaxPage maxPage;
    public static Permission permission;
    public static Position position;
    public static SellBuy sellBuy;
    public static SetToRecAll setToRecAll;
    public static ShopHours shopHours;
    public static StockStabilizing stockStabilizing;
    public static Command command;

    public static ItemInfo itemInfo;

    public static void Init()
    {
        CMDHashMap.clear();

        // ds
        commandHelp = new CommandHelp();
        createShop = new CreateShop();
        deleteShop = new DeleteShop();
        deleteUser = new DeleteUser();
        mergeShop = new MergeShop();
        openShop = new OpenShop();
        renameShop = new RenameShop();
        reload = new Reload();
        setDefaultShop = new SetDefaultShop();
        setTax = new SetTax();
        itemInfo = new ItemInfo();

        CMDHashMap.put("cmdhelp", commandHelp);
        CMDHashMap.put("createshop", createShop);
        CMDHashMap.put("deleteshop", deleteShop);
        CMDHashMap.put("deleteolduser", deleteUser);
        CMDHashMap.put("mergeshop", mergeShop);
        CMDHashMap.put("openshop", openShop);
        CMDHashMap.put("renameshop", renameShop);
        CMDHashMap.put("reload", reload);
        CMDHashMap.put("setdefaultshop", setDefaultShop);
        CMDHashMap.put("settax", setTax);
        CMDHashMap.put("iteminfo", itemInfo);

        // ds shop
        account = new Account();
        add = new Add();
        addHand = new AddHand();
        edit = new Edit();
        editAll = new EditAll();
        enable = new Enable();
        flag = new Flag();
        fluctuation = new Fluctuation();
        log = new Log();
        maxPage = new MaxPage();
        permission = new Permission();
        position = new Position();
        sellBuy = new SellBuy();
        setToRecAll = new SetToRecAll();
        shopHours = new ShopHours();
        stockStabilizing = new StockStabilizing();
        command = new Command();

        CMDHashMap.put("account", account);
        CMDHashMap.put("add", add);
        CMDHashMap.put("addhand", addHand);
        CMDHashMap.put("edit", edit);
        CMDHashMap.put("editall", editAll);
        CMDHashMap.put("enable", enable);
        CMDHashMap.put("flag", flag);
        CMDHashMap.put("fluctuation", fluctuation);
        CMDHashMap.put("log", log);
        CMDHashMap.put("maxpage", maxPage);
        CMDHashMap.put("permission", permission);
        CMDHashMap.put("position", position);
        CMDHashMap.put("sellbuy", sellBuy);
        CMDHashMap.put("settorecall", setToRecAll);
        CMDHashMap.put("shophours", shopHours);
        CMDHashMap.put("stockstabilizing", stockStabilizing);
        CMDHashMap.put("command", command);
    }

    public static void RunCMD(String key, String[] args, CommandSender sender)
    {
        if(CMDHashMap.containsKey(key))
        {
            CMDHashMap.get(key).RunCMD(args, sender);
        }
    }
}
