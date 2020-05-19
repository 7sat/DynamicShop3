package me.sat7.dynamicshop.jobshook;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerPoints;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.LangUtil;

public final class JobsHook {
    public static boolean jobsRebornActive = false;

    private JobsHook() {

    }

    // JobsReborn의 points 수정
    public static boolean addJobsPoint(Player p, double amount)
    {
        if(!jobsRebornActive)
        {
            p.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.JOBSREBORN_NOT_FOUND"));
            return false;
        }

        PlayerPoints pp = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(p.getUniqueId());
        // 차감
        if(amount < 0.0)
        {
            if(pp.havePoints(amount * -1))
            {
                pp.takePoints(amount * -1);
                return true;
            }
            // 포인트 부족
            else
            {
                p.sendMessage(DynamicShop.dsPrefix+ LangUtil.ccLang.get().getString("NOT_ENOUGH_POINT")
                        .replace("{bal}", DynaShopAPI.df.format(getCurJobPoints(p))));
                return false;
            }
        }
        // 증가
        else
        {
            pp.addPoints(amount);
            return true;
        }
    }

    // JobsReborn. 플레이어의 잔액 확인
    public static double getCurJobPoints(Player p)
    {
        return Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(p.getUniqueId()).getCurrentPoints();
    }
}
