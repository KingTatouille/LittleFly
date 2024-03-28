package fr.hillwalk.littlefly.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ParticleConfig {

    private final Plugin plugin;

    public ParticleConfig(Plugin plugin) {
        this.plugin = plugin;

    }

    public String getParticlePermission(String particleName) {
        File particleFile = new File(plugin.getDataFolder() + "/particles", particleName + ".yml");
        if (!particleFile.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(particleFile);
        return config.getString("permission");
    }


    public FileConfiguration getInfos(String particleName) {
        File particleFile = new File(plugin.getDataFolder() + "/particles", particleName + ".yml");
        if (!particleFile.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(particleFile);
    }

}