package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.commands.shop.*;
import org.bukkit.entity.Player;

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

    public static void Init()
    {
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

        CMDHashMap.clear();
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
    }

    public static void RunCMD(String key, String[] args, Player player)
    {
        if(CMDHashMap.containsKey(key))
        {
            CMDHashMap.get(key).RunCMD(args, player);
        }
    }
}
