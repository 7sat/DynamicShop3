package me.sat7.dynamicshop.utilities;

public final class MathUtil
{
    private MathUtil()
    {

    }

    // 내림
    public static int RoundDown(int old)
    {
        if (old < 10)
        {
            return old;
        }

        if (old % 10 != 0)
        {
            old = (old / 10) * 10;
        } else if (old % 100 != 0)
        {
            old = (old / 100) * 100;
        } else if (old % 1000 != 0)
        {
            old = (old / 1000) * 1000;
        } else
        {
            old = (old / 10000) * 10000;
        }

        if (old < 1) old = 1;

        return old;
    }

    public static int Clamp (int value, int min, int max)
    {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        return value;
    }

    public static double Clamp (double value, double min, double max)
    {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        return value;
    }
}
