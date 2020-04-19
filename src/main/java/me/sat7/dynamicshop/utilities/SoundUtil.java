package me.sat7.dynamicshop.utilities;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;

public final class SoundUtil {
    public static CustomConfig ccSound;

    private SoundUtil() {

    }

    // 소리 재생
    public static void playerSoundEffect(Player player, String key)
    {
        try
        {
            player.playSound(player.getLocation(), Sound.valueOf(ccSound.get().getString(key)),1,1);
        }
        catch (Exception e)
        {
            if(ccSound.get().contains(key))
            {
                if(ccSound.get().getString(key).length() > 1)
                {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Sound play failed: " + key + "/" + ccSound.get().getString(key));
                }
            }
            else
            {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Sound play failed. Path is missing: " + key);
            }
        }
    }

    public static void setupSoundFile()
    {
        ccSound.setup("Sound",null);
        ccSound.get().options().header("Enter 0 to mute.\nhttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html");
        ccSound.get().addDefault("sell", "ENTITY_EXPERIENCE_ORB_PICKUP");
        ccSound.get().addDefault("buy", "ENTITY_EXPERIENCE_ORB_PICKUP");
        ccSound.get().addDefault("editItem", "ENTITY_PAINTING_PLACE");
        ccSound.get().addDefault("deleteItem", "BLOCK_GRAVEL_BREAK");
        ccSound.get().addDefault("addItem", "BLOCK_GRAVEL_PLACE");
        ccSound.get().addDefault("click", "BLOCK_METAL_STEP");
        ccSound.get().addDefault("tradeview", "ENTITY_CHICKEN_EGG");
        ccSound.get().options().copyDefaults(true);
        ccSound.save();
    }
}
