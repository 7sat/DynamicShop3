package me.sat7.dynamicshop.jobshook;

import com.gamingmesh.jobs.container.JobsPlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerPoints;

import me.sat7.dynamicshop.DynamicShop;

import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class JobsHook
{
    public static boolean jobsRebornActive = false;

    private JobsHook()
    {

    }

    // JobsReborn의 points 수정
    public static boolean addJobsPoint(Player p, double amount)
    {
        if (!jobsRebornActive)
        {
            p.sendMessage(DynamicShop.dsPrefix + t("ERR.JOBS_REBORN_NOT_FOUND"));
            return false;
        }

        PlayerPoints pp = JobsHook.getJobsPlayerPoints(p);
        // 차감
        if (amount < 0.0)
        {
            if (pp.havePoints(amount * -1))
            {
                pp.takePoints(amount * -1);
                return true;
            }
            // 포인트 부족
            else
            {
                p.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.NOT_ENOUGH_POINT")
                        .replace("{bal}", n(getCurJobPoints(p))));
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
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p);
        return jobsPlayer.getPointsData().getCurrentPoints();
    }

    public static PlayerPoints getJobsPlayerPoints(Player p)
    {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p);
        return jobsPlayer.getPointsData();
    }
}
