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
        for (int i = 0; i < 5; i++)
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
}
