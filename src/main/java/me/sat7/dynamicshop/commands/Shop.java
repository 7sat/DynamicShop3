package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class Shop
{
    private Shop()
    {

    }

    public static String GetShopName(String[] args)
    {
        if (args.length == 1)
        {
            return DynamicShop.plugin.getConfig().getString("Command.DefaultShopName");
        }
        else if (args.length > 1)
        {
            return args[1];
        }
        else
        {
            return "";
        }
    }


    static void shopCommand(String[] args, CommandSender sender)
    {
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;

        if (player != null && args.length == 1 && DynamicShop.plugin.getConfig().getBoolean("Command.OpenStartPageInsteadOfDefaultShop"))
        {
            DynaShopAPI.openStartPage(player);
            return;
        }

        String shopName = GetShopName(args);

        // 그런 이름을 가진 상점이 있는지 확인
        if (player != null && !ShopUtil.shopConfigFiles.containsKey(shopName))
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.SHOP_NOT_FOUND"));
            return;
        }

        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        // 상점 UI 열기
        if (player != null && args.length <= 2)
        {
            //권한 확인
            String s = shopData.get().getString("Options.permission");
            if (s != null && s.length() > 0)
            {
                if (!player.hasPermission(s) && !player.hasPermission(s + ".buy") && !player.hasPermission(s + ".sell"))
                {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_PERMISSION"));
                    return;
                }
            }

            // 플래그 확인
            ConfigurationSection shopConf = shopData.get().getConfigurationSection("Options");
            if (shopConf.contains("flag.signshop"))
            {
                if (!player.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.SIGN_SHOP_REMOTE_ACCESS"));
                    return;
                }
            }
            if (shopConf.contains("flag.localshop") && !shopConf.contains("flag.deliverycharge") && shopConf.contains("world") && shopConf.contains("pos1") && shopConf.contains("pos2"))
            {
                boolean outside = !player.getWorld().getName().equals(shopConf.getString("world"));

                String[] shopPos1 = shopConf.getString("pos1").split("_");
                String[] shopPos2 = shopConf.getString("pos2").split("_");
                int x1 = Integer.parseInt(shopPos1[0]);
                int y1 = Integer.parseInt(shopPos1[1]);
                int z1 = Integer.parseInt(shopPos1[2]);
                int x2 = Integer.parseInt(shopPos2[0]);
                int y2 = Integer.parseInt(shopPos2[1]);
                int z2 = Integer.parseInt(shopPos2[2]);

                if (!((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2) ||
                        (x2 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x1)))
                    outside = true;
                if (!((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2) ||
                        (y2 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y1)))
                    outside = true;
                if (!((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2) ||
                        (z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1)))
                    outside = true;

                if (outside && !player.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS))
                {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.LOCAL_SHOP_REMOTE_ACCESS"));

                    String posString = t(player, "SHOP.SHOP_LOCATION");
                    posString = posString.replace("{x}", n(x1));
                    posString = posString.replace("{y}", n(y1));
                    posString = posString.replace("{z}", n(z1));
                    player.sendMessage(DynamicShop.dsPrefix(player) + posString);
                    return;
                }
            }
            if (shopConf.contains("shophours") && !player.hasPermission(P_ADMIN_SHOP_EDIT))
            {
                int curTime = (int) (player.getWorld().getTime()) / 1000 + 6;
                if (curTime > 24) curTime -= 24;

                String[] temp = shopConf.getString("shophours").split("~");

                int open = Integer.parseInt(temp[0]);
                int close = Integer.parseInt(temp[1]);

                if (close > open)
                {
                    if (!(open <= curTime && curTime < close))
                    {
                        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return;
                    }
                } else
                {
                    if (!(open <= curTime || curTime < close))
                    {
                        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "TIME.SHOP_IS_CLOSED").
                                replace("{time}", open + "").replace("{curTime}", curTime + ""));
                        return;
                    }
                }
            }

            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 그외 각종 상점관련 명령어
        else if (args.length >= 3)
        {
            CMDManager.RunCMD(args[2].toLowerCase(), args, sender);
        }
    }
}
