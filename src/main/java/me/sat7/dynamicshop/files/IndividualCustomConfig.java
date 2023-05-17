package me.sat7.dynamicshop.files;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;

public class IndividualCustomConfig<T> {

	private File folder;
	private Map<String, FileConfiguration> customConfigs;
	private Function<T, String> function;
	private BiConsumer<T, FileConfiguration> consumer;

	public void setup(String folder, Function<T, String> function, BiConsumer<T, FileConfiguration> consumer) {
		this.consumer = consumer;
		this.function = function;
		this.folder = new File(Bukkit.getServer().getPluginManager().getPlugin("DynamicShop").getDataFolder(), folder);
		this.customConfigs = new HashMap<>();
		if (!this.folder.exists()) {
			this.folder.mkdir();
		} else {
			for(File file : this.folder.listFiles()) {
				if (file.getName().endsWith(".yml")) {
					customConfigs.put(file.getName().substring(0, file.getName().length()-4), YamlConfiguration.loadConfiguration(file));
				}
			}
		}
    }
    
    public FileConfiguration create(T name) {
		File file = getFile(name);
		try {
			file.createNewFile();
			FileConfiguration config = new YamlConfiguration();
			customConfigs.put(function.apply(name), config);
			return config;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

    public boolean open(T name)
    {
		File file = getFile(name);

        if (!file.exists())
        {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + function.apply(name) + ".yml not found");
            return false;
        }
        
        customConfigs.put(function.apply(name), YamlConfiguration.loadConfiguration(file));
        return true;
    }

    public FileConfiguration get(T name){
		String text = function.apply(name);
		FileConfiguration temp = customConfigs.get(text);
		if (customConfigs.containsKey(text) && temp != null) {
			return temp;
		} else {
			if (open(name)) {
				return customConfigs.get(text);
			} else {
				FileConfiguration config = new YamlConfiguration();
				customConfigs.put(text, config);
				consumer.accept(name, config);
				return config;
			}
		}
    }

    public void save(T name){
		String text = function.apply(name);
		if (!customConfigs.containsKey(text)) return;
        try{
            customConfigs.get(text).save(getFile(name));
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

	public void saveAll() throws IOException {
		for (Map.Entry<String, FileConfiguration> entry : customConfigs.entrySet()) {
			entry.getValue().save(new File(folder, entry.getKey() + ".yml"));
		}
	}
    
    private File getFile(T name) {
		return new File(folder, function.apply(name) + ".yml");
    }

    public void reload(T name){
		File file = getFile(name);
        customConfigs.put(function.apply(name), YamlConfiguration.loadConfiguration(file));
    }
    
    public Map<String, FileConfiguration> getConfigs(){
		return Collections.unmodifiableMap(customConfigs);
    }
    
    public void remove(String config) {
		customConfigs.remove(config);
		new File(folder, config + ".yml").delete();
    }
    
    public void remove(T name) {
		customConfigs.remove(function.apply(name));
		getFile(name).delete();
    }

}
