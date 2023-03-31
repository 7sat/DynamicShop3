package me.sat7.dynamicshop.utilities;

public final class MathUtil
{
    public static final long hourInMilliSeconds = 1000 * 60 * 60;
    public static final long dayInMilliSeconds = 1000 * 60 * 60 * 24;
    public static final long dayInTick = 20 * 60 * 60 * 24;

    private MathUtil()
    {

    }

    public static int RoundDown(double value)
    {
        int intNum = (int)value;
        if(intNum != value)
            return intNum;

        if (value < 10)
            return intNum;

        int temp = 10;
        for (int i = 0; i < 7; i++)
        {
            if (intNum % temp != 0 && intNum > temp)
            {
                intNum = (intNum / temp) * temp;
                break;
            }
            temp *= 10;
        }

        if (intNum < 1) intNum = 1;

        return intNum;
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

    public static long Clamp (long value, long min, long max)
    {
        if (value < min)
            return min;
        else if (value > max)
            return max;
        return value;
    }

    public static int SafeAdd(int a, int b)
    {
        int temp = a + b;
        if (b > 0)
        {
            if (a > Integer.MAX_VALUE - b - 1)
                return Integer.MAX_VALUE - 1;
        } else
        {
            if (a < Integer.MIN_VALUE - b)
                return Integer.MIN_VALUE;
        }

        return temp;
    }

    public static long TickToMilliSeconds(long tick)
    {
        return tick * 50;
    }

    public static long MilliSecondsToTick(long ms)
    {
        return ms / 50;
    }

    public static long RoundDown_Time_Min(long value)
    {
        value = value / 1000 / 60;
        value = value * 1000 * 60;
        return  value;
    }
    public static long RoundDown_Time_Hour(long value)
    {
        value = value / 1000 / 60 / 60;
        value = value * 1000 * 60 * 60;
        return  value;
    }
}
