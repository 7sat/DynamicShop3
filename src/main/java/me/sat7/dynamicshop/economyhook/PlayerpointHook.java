package me.sat7.dynamicshop.economyhook;

import me.sat7.dynamicshop.DynamicShop;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class PlayerpointHook
{
    public static boolean isPPActive = false;

    public static boolean addPP(Player p, double amount)
    {
        return addPP(p, (int)amount);
    }

    public static boolean addPP(Player p, int amount)
    {
        if (!isPPActive)
        {
            p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "ERR.PLAYER_POINTS_NOT_FOUND"));
            return false;
        }

        UUID uuid = p.getUniqueId();

        int current = DynamicShop.ppAPI.look(uuid);
        if (amount < 0 && current + amount < 0)
        {
            p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "MESSAGE.NOT_ENOUGH_PLAYER_POINT")
                    .replace("{bal}", n(current)));
            return false;
        }

        return DynamicShop.ppAPI.give(uuid, amount);
    }

    public static int getCurrentPP(Player p)
    {
        if (!isPPActive)
        {
            p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "ERR.PLAYER_POINTS_NOT_FOUND"));
            return 0;
        }

        UUID uuid = p.getUniqueId();

        return DynamicShop.ppAPI.look(uuid);
    }
}
