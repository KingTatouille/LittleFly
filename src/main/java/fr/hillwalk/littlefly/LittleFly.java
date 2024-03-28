package fr.hillwalk.littlefly;

import fr.hillwalk.littlefly.commands.CommonCommands;
import fr.hillwalk.littlefly.commands.FlyCommand;
import fr.hillwalk.littlefly.commands.TimeFlyCommand;
import fr.hillwalk.littlefly.configs.PlayerData;
import fr.hillwalk.littlefly.listeners.PlayerJoin;
import fr.hillwalk.littlefly.listeners.PlayerLeave;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class LittleFly extends JavaPlugin {


    public String prefix;

    private HashMap<UUID, Integer> flightDurationMap = new HashMap<>();

    @Override
    public void onEnable() {

       //Take placeholderapi
        getLogger().info("LittleFly has been enabled!");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {

            getLogger().info("PlaceholderAPI detected!");

        } else {

            getLogger().warning("Could not find PlaceholderAPI! This is a softdependency.");
        }

        // Save default messages.yml if it doesn't exist
        saveDefaultConfig();

        // Load messages from config
        reloadConfig();

        // Register command executors
        getCommand("tfly").setExecutor(new TimeFlyCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("lfly").setExecutor(new CommonCommands(this));

        // Register event listener
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);

        // Load player data
        loadPlayerData();

        // Load the particles data
        loadParticles();

        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("LittleFly has been disabled!");
    }

    private void loadParticles() {
        File particlesFolder = new File(getDataFolder(), "particles");
        if (!particlesFolder.exists()) {
            particlesFolder.mkdirs();
            return;
        }

        File[] particleFiles = particlesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (particleFiles == null) {
            return;
        }

        for (File particleFile : particleFiles) {
            getLogger().info("Particles added: " + particleFile);
            loadParticle(particleFile);
        }
    }

    private YamlConfiguration loadParticle(File particleFile) {
        // Charge le fichier YAML
        YamlConfiguration config = YamlConfiguration.loadConfiguration(particleFile);

        // Traitez les données du fichier YAML si nécessaire

        return config;
    }


    // Load player data from files
    private void loadPlayerData() {
        File playersFolder = new File(getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdirs();
        }

        File[] playerFiles = playersFolder.listFiles();
        if (playerFiles != null) {
            for (File playerFile : playerFiles) {
                if (playerFile.isFile() && playerFile.getName().endsWith(".yml")) {
                    String fileName = playerFile.getName();
                    String uuidStr = fileName.substring(0, fileName.length() - 4); // Remove .yml extension
                    UUID uuid = UUID.fromString(uuidStr);
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        PlayerData playerData = new PlayerData(this, player);
                        playerData.saveData(); // Ensure data is saved (for creating default data if necessary)
                    }
                }
            }
        }
    }

    // Méthode pour obtenir la durée de vol d'un joueur
    public int getFlightDuration(Player player) {
        return flightDurationMap.get(player.getUniqueId());
    }

    // Méthode pour définir la durée de vol d'un joueur
    public void setFlightDuration(Player player, int duration) {
        flightDurationMap.put(player.getUniqueId(), duration);
    }

    // Méthode pour supprimer la durée de vol d'un joueur
    public void removeFlightDuration(Player player) {
        flightDurationMap.remove(player.getUniqueId());
    }



}
