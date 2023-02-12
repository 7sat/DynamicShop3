package me.sat7.dynamicshop.utilities;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public final class StringUtil {

    private StringUtil() {}

    private static final String characters = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~¦ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×áíóúñÑªº¿®¬½¼¡«»";
    private static final int[] extraWidth = {4,2,5,6,6,6,6,3,5,5,5,6,2,6,2,6,6,6,6,6,6,6,6,6,6,6,2,2,5,6,5,6,7,6,6,6,6,6,6,6,6,4,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,4,6,6,3,6,6,6,6,6,5,6,6,2,6,5,3,6,6,6,6,6,6,6,4,6,6,6,6,6,6,5,2,5,7,6,6,6,6,6,6,6,6,6,6,6,6,4,6,3,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,6,3,6,6,6,6,6,6,6,7,6,6,6,2,6,6,8,9,9,6,6,6,8,8,6,8,8,8,8,8,6,6,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,6,9,9,9,5,9,9,8,7,7,8,7,8,8,8,7,8,8,7,9,9,6,7,7,7,7,7,9,6,7,8,7,6,6,9,7,6,7,1};

    /**
     * Get the width that a character is displayed with in the default resource pack.
     * This relies on a hardcoded character to width mapping and might not be precise in places.
     * @param c The character to get the width of
     * @return The width of the character (will return 10 for characters that we don't know the width of)
     */
    private static int getMinecraftCharWidth(char c) {
        if (c != ChatColor.COLOR_CHAR) { //
            int index = characters.indexOf(c);
            if (index > -1) {
                return extraWidth[index];
            } else {
                return 10;
            }
        }
        return 0;
    }

    /**
     * Get the width that a string is displayed with in the default resource pack.
     * This relies on a hardcoded character to width mapping and might not be precise in places.
     * @param string The string to get the width of
     * @return The width of the string
     */
    private static int getMinecraftStringWidth(String string) {
        int width = 0;
        for (char c : string.toCharArray()) {
            width += getMinecraftCharWidth(c);
        }
        return width;
    }

    /**
     * Capitalizes every first letter of a word
     *
     * @param string    String to reformat
     * @param separator Word separator
     * @return Reformatted string
     */
    private static String capitalizeFirstLetter(String string, char separator) {
        if (string == null || string.isEmpty()) {
            return string;
        }

        // Split into words
        String[] words = string.toLowerCase(Locale.ROOT).split(String.valueOf(separator));
        // Capitalize every word and return joined string
        return Arrays.stream(words)
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    /**
     * Capitalizes every first letter of a word
     *
     * @param string String to reformat
     * @return Reformatted string
     * @see StringUtil#capitalizeFirstLetter(String, char)
     */
    private static String capitalizeFirstLetter(String string) {
        return capitalizeFirstLetter(string, ' ');
    }

    /**
     * Short the item name proportional to a sign.
     *
     * @param itemName name of item.
     * @return the short-named item.
     * @see StringUtil#getShortenedNameSign(String)
     */
    public static String getShortenedNameSign(String itemName) {
        int maxWidth = 90; // Default value...
        itemName = capitalizeFirstLetter(itemName.replace('_', ' '), ' ');
        int width = getMinecraftStringWidth(itemName);
        if (width <= maxWidth) {
            return itemName;
        }
        String[] itemParts = itemName.split("[ \\-]");
        itemName = String.join("", itemParts);
        width = getMinecraftStringWidth(itemName);
        if (width <= maxWidth) {
            return itemName;
        }
        int exceeding = width - maxWidth;
        int shortestIndex = 0;
        int longestIndex = 0;
        for (int i = 0; i < itemParts.length; i++) {
            if (getMinecraftStringWidth(itemParts[longestIndex]) < getMinecraftStringWidth(itemParts[i])) {
                longestIndex = i;
            }
            if (getMinecraftStringWidth(itemParts[shortestIndex]) > getMinecraftStringWidth(itemParts[i])) {
                shortestIndex = i;
            }
        }
        int shortestWidth = getMinecraftStringWidth(itemParts[shortestIndex]);
        int longestWidth = getMinecraftStringWidth(itemParts[longestIndex]);
        int remove = longestWidth - shortestWidth;
        while (remove > 0 && exceeding > 0) {
            int endWidth = getMinecraftCharWidth(itemParts[longestIndex].charAt(itemParts[longestIndex].length() - 1));
            itemParts[longestIndex] = itemParts[longestIndex].substring(0, itemParts[longestIndex].length() - 1);
            remove -= endWidth;
            exceeding -= endWidth;
        }

        for (int i = itemParts.length - 1; i >= 0 && exceeding > 0; i--) {
            int partWidth = getMinecraftStringWidth(itemParts[i]);

            if (partWidth > shortestWidth) {
                remove = partWidth - shortestWidth;
            }

            if (remove > exceeding) {
                remove = exceeding;
            }

            while (remove > 0) {
                int endWidth = getMinecraftCharWidth(itemParts[i].charAt(itemParts[i].length() - 1));
                itemParts[i] = itemParts[i].substring(0, itemParts[i].length() - 1);
                remove -= endWidth;
                exceeding -= endWidth;
            }
        }

        while (exceeding > 0) {
            for (int i = itemParts.length - 1; i >= 0 && exceeding > 0; i--) {
                int endWidth = getMinecraftCharWidth(itemParts[i].charAt(itemParts[i].length() - 1));
                itemParts[i] = itemParts[i].substring(0, itemParts[i].length() - 1);
                exceeding -= endWidth;
            }
        }
        return String.join("", itemParts);
    }
}
