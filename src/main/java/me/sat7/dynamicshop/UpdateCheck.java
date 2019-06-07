package me.sat7.dynamicshop;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateCheck {
    private int project;
    private URL checkURL;
    private String newVersion;
    private JavaPlugin plugin;

    public UpdateCheck(JavaPlugin plugin, int projectID) {
        this.plugin = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
            //Bukkit.getLogger().warning("§4Could not connect to Spigot, plugin disabled!");
            //Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public String getResourceUrl() {
        return "https://spigotmc.org/resources/" + project;
    }

    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equals(newVersion);
    }
}