package me.sat7.dynamicshop.utilities;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemsUtil {
    private ItemsUtil() {

    }

    // 지정된 이름,lore,수량의 아이탬 스택 생성및 반환
    public static ItemStack createItemStack(Material material, ItemMeta _meta, String name, ArrayList<String> lore, int amount)
    {
        ItemStack istack = new ItemStack(material,amount);

        ItemMeta meta = _meta;
        if(_meta == null) meta = istack.getItemMeta();
        if(!name.equals("")) meta.setDisplayName(name);
        meta.setLore(lore);
        istack.setItemMeta(meta);
        return istack;
    }

    // 아이탬 이름 정돈
    public static String getBeautifiedName(Material mat)
    {
        String temp = mat.toString().replace("_"," ").toLowerCase();
        String[] temparr = temp.split(" ");

        StringBuilder finalStr = new StringBuilder();
        for (String s:temparr)
        {
            s = (""+s.charAt(0)).toUpperCase() + s.substring(1);
            finalStr.append(s).append(" ");
        }
        finalStr = new StringBuilder(finalStr.substring(0, finalStr.length() - 1));

        return finalStr.toString();
    }

    // 아이탬 정보 출력
    public static void sendItemInfo(Player player, String shopName, int idx, String msgType)
    {
        String info = " value:" + ShopUtil.ccShop.get().getDouble(shopName+"." + idx+ ".value");

        double valueMin = ShopUtil.ccShop.get().getDouble(shopName+"."+idx+".valueMin");
        if(valueMin > 0.01) info += " min:" + valueMin;
        double valueMax = ShopUtil.ccShop.get().getDouble(shopName+"."+idx+".valueMax");
        if(valueMax > 0) info += " max:" + valueMax;

        info += " median:" + ShopUtil.ccShop.get().getInt(shopName+"." + idx + ".median");
        info += " stock:" + ShopUtil.ccShop.get().getInt(shopName+"." + idx + ".stock");

        player.sendMessage(" - " + LangUtil.ccLang.get().getString(msgType).
                replace("{item}", ShopUtil.ccShop.get().getString(shopName+"." + idx + ".mat")).
                replace("{info}",info)
        );
    }
}
