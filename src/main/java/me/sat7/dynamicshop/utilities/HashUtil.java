package me.sat7.dynamicshop.utilities;

import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil
{
    public static String CreateHashString(String mat, String meta)
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignore)
        {
            return mat + meta;
        }

        byte[] messageDigest = md.digest(mat.getBytes());
        return convertToHex(messageDigest);
    }

    public static String GetItemHash(ItemStack item)
    {
        String metaString = item.getItemMeta() == null ? "" : item.getItemMeta().toString();
        return CreateHashString(item.getType().toString(), metaString);
    }

    private static String convertToHex(final byte[] messageDigest)
    {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32)
        {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
