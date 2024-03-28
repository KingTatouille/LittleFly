package fr.hillwalk.littlefly.configs;

import fr.hillwalk.littlefly.LittleFly;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {

    private final LittleFly plugin;
    private final UUID uuid;
    private File file;
    private FileConfiguration data;

    public PlayerData(LittleFly plugin, Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        this.file = new File(plugin.getDataFolder() + "/players", uuid.toString() + ".yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    // Get player's name
    public String getName() {
        return data.getString("Name", "");
    }

    // Set player's name
    public void setName(String name) {
        data.set("Name", name);
    }

    // Get player's current fly time
    public int getCurrentFlyTime() {
        return data.getInt("CurrentFlyTime");
    }

    // Set player's current fly time
    public void setCurrentFlyTime(int time) {
        data.set("CurrentFlyTime", time);
    }

    // Get player's custom particles
    public String getCustomParticles() {
        return data.getString("CustomParticles", "");
    }

    // Set player's custom particles
    public void setCustomParticles(String particles) {
        data.set("CustomParticles", particles);
    }

    // Set default values for player's data
    public void setDefaultValues(String name, int flyTime, String particles) {
        setName(name);
        setCurrentFlyTime(flyTime);
        setCustomParticles(particles);
    }

    // Save player's data configuration
    public void saveData() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data file for " + uuid.toString());
        }
    }
}
