package fr.hillwalk.littlefly.particules;

import fr.hillwalk.littlefly.LittleFly;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public class PlayerParticles {

    private final Player player;
    private BukkitRunnable task;
    private final LittleFly instance;

    public PlayerParticles(Player player, LittleFly instance, FileConfiguration particleConfig) {
        this.player = player;
        this.instance = instance;

        ConfigurationSection particlesSection = particleConfig.getConfigurationSection("particles");
        if (particlesSection != null) {
            Set<String> particleKeys = particlesSection.getKeys(false);
            for (String particleKey : particleKeys) {
                ConfigurationSection particleSection = particlesSection.getConfigurationSection(particleKey);
                if (particleSection != null) {
                    String particleType = particleSection.getString("type", "FLAME");
                    double offsetX = particleSection.getDouble("offsetX", 0.1);
                    double offsetY = particleSection.getDouble("offsetY", 0.1);
                    double offsetZ = particleSection.getDouble("offsetZ", 0.1);
                    double speed = particleSection.getDouble("speed", 0.05);
                    start(particleType, offsetX, offsetY, offsetZ, speed);
                }
            }
        }
    }

    // Start emitting particles for the player
    public void start(String particleType, double offsetX, double offsetY, double offsetZ, double speed) {
        if (task != null) {
            task.cancel();
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation().add(0, 0, 0); // Adjust Y coordinate to prevent particles from being inside the player
                loc.getWorld().spawnParticle(Particle.valueOf(particleType.toUpperCase()), loc, 1, offsetX, offsetY, offsetZ, speed);
            }
        };
        task.runTaskTimer(instance, 0L, 1L); // Emit particles every tick
    }

    // Stop emitting particles for the player
    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
