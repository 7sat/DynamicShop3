package me.sat7.dynamicshop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;

import me.sat7.dynamicshop.constants.Constants;

public class UpdateCheck {
    private static final int PROJECT_ID = 65603;
    private URL checkURL;
    private String newVersion;

    public UpdateCheck() {
        newVersion = DynamicShop.plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + PROJECT_ID);
        } catch (MalformedURLException e) {
            Bukkit.getLogger().warning("§4Could not connect to Spigot for updates!");
        }
        initUpdater();
    }

    void initUpdater() {
        // 업데이트 확인
        try {
            if(DynamicShop.plugin.getDescription().getVersion().contains("SNAPSHOT")) {
                DynamicShop.updateAvailable = false;
                DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +" Plugin is running a dev build!");
                DynamicShop.console.sendMessage("Be careful and monitor what happens!");
                DynamicShop.console.sendMessage(getResourceUrl());
                DynamicShop.console.sendMessage("§3-------------------------------------------------------");
            } else if(checkForUpdates()) {
                // this will print when outdated
                DynamicShop.updateAvailable = true;
                DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +"Plugin outdated!!");
                DynamicShop.console.sendMessage(getResourceUrl());
                DynamicShop.console.sendMessage("§3-------------------------------------------------------");
            } else {
                // this will print when no updates
                DynamicShop.updateAvailable = false;
                DynamicShop.console.sendMessage("§3-------------------------------------------------------");
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +" Plugin is up to date!");
                DynamicShop.console.sendMessage("Please rate my plugin if you like it");
                DynamicShop.console.sendMessage(getResourceUrl());
                DynamicShop.console.sendMessage("§3-------------------------------------------------------");
            }
        } catch (Exception e) {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX +"Failed to check update. Try again later.");
        }
    }

    public static String getResourceUrl() {
        return "https://spigotmc.org/resources/" + PROJECT_ID;
    }

    private boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !DynamicShop.plugin.getDescription().getVersion().equals(newVersion);
    }
}