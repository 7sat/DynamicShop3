package me.sat7.dynamicshop.utilities;

public final class MathUtil
{
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
}
