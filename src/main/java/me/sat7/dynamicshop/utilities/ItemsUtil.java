package me.sat7.dynamicshop.utilities;

import java.util.ArrayList;

import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class ItemsUtil
{
    private ItemsUtil()
    {

    }

    // 지정된 이름,lore,수량의 아이탬 스택 생성및 반환
    public static ItemStack createItemStack(Material material, ItemMeta _meta, String name, ArrayList<String> lore, int amount)
    {
        ItemStack istack = new ItemStack(material, amount);

        ItemMeta meta = _meta;
        if (_meta == null) meta = istack.getItemMeta();
        if (name != null && !name.equals("")) meta.setDisplayName(name);
        meta.setLore(lore);
        istack.setItemMeta(meta);
        return istack;
    }

    // 아이탬 이름 정돈
    public static String getBeautifiedName(Material mat)
    {
        return getBeautifiedName(mat.toString());
    }

    public static String getBeautifiedName(String matName)
    {
        String temp = matName.replace("_", " ").toLowerCase();
        String[] temparr = temp.split(" ");

        StringBuilder finalStr = new StringBuilder();
        for (String s : temparr)
        {
            s = ("" + s.charAt(0)).toUpperCase() + s.substring(1);
            finalStr.append(s).append(" ");
        }
        finalStr = new StringBuilder(finalStr.substring(0, finalStr.length() - 1));

        return finalStr.toString();
    }

    // 아이탬 정보 출력
    public static void sendItemInfo(CommandSender sender, String shopName, int idx, String msgType)
    {
        if(sender instanceof Player)
            sendItemInfo((Player) sender, shopName, idx, msgType);
    }

    public static void sendItemInfo(Player player, String shopName, int idx, String msgType)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        String info = " value:" + data.get().getDouble(idx + ".value");

        double valueMin = data.get().getDouble(idx + ".valueMin");
        if (valueMin > 0.01) info += " min:" + valueMin;
        double valueMax = data.get().getDouble(idx + ".valueMax");
        if (valueMax > 0) info += " max:" + valueMax;

        info += " median:" + data.get().getInt(idx + ".median");
        info += " stock:" + data.get().getInt(idx + ".stock");

        player.sendMessage(" - " + t(player, msgType).
                replace("{item}", data.get().getString(idx + ".mat")).
                replace("{info}", info)
        );
    }
}
