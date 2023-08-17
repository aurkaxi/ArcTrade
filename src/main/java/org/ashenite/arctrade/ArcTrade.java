package org.ashenite.arctrade;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.ashenite.arctrade.Commands.TradeCMD;
import org.ashenite.arctrade.Listeners.TradeLST;
import org.bson.Document;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class ArcTrade extends JavaPlugin {

	private static ArcTrade plugin;
	private FileConfiguration customConfig;
	private MongoClient dbclient;


	@Override
	public void onEnable() {
		// Plugin Instance
		plugin = this;

		// Custom Configuration
		createCustomConfig();

		// MongoDB
		String uri = Objects.requireNonNull(customConfig.getString("mongodb_uri"));
		try {
			dbclient = MongoClients.create(uri);
			getLogger().info("Connected to MongoDB!");
		} catch (Exception e) {
			getLogger().severe("Could not connect to MongoDB!");
			e.printStackTrace();
			return;
		}

		// Commands
		Objects.requireNonNull(getCommand("trade")).setExecutor(new TradeCMD());

		// Listeners
		getServer().getPluginManager().registerEvents(new TradeLST(), this);
	}

	public static ArcTrade getPlugin() {
		return plugin;
	}

	public FileConfiguration getCustomConfig() {
		return customConfig;
	}

	private void createCustomConfig() {
		// Custom Config
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			boolean res = configFile.getParentFile().mkdirs();
			if (!res) {
				getLogger().warning("Could not create config.yml directory!");
			}
			saveResource("config.yml", false);
		}
		customConfig = new YamlConfiguration();
		try {
			customConfig.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void reloadConf() {
		File configFile = new File(getDataFolder(), "config.yml");
		customConfig = new YamlConfiguration();
		try {
			customConfig.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		System.out.println("Reloaded " + this.getName() + "'s Configurations!");
	}

	public MongoCollection<Document> getColl(String name) {
		return dbclient.getDatabase("Minecraft").getCollection(name);
	}
}
