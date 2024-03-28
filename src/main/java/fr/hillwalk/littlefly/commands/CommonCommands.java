package fr.hillwalk.littlefly.commands;

import fr.hillwalk.littlefly.LittleFly;
import fr.hillwalk.littlefly.configs.ParticleConfig;
import fr.hillwalk.littlefly.configs.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CommonCommands implements CommandExecutor {

    private final LittleFly instance;
    private final ParticleConfig permissionChecker;

    public CommonCommands(LittleFly instance) {
        this.instance = instance;
        this.permissionChecker = new ParticleConfig(instance);
    }

    private String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("messages." + key));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false; // No sub-command provided
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(instance.prefix + getMessage("player_only"));
                return true;
            }
            if (!sender.hasPermission("littlefly.list")) {
                sender.sendMessage(instance.prefix + getMessage("no_permission"));
                return true;
            }

            File particlesFolder = new File(instance.getDataFolder(), "particles");
            if (!particlesFolder.exists() || !particlesFolder.isDirectory()) {
                sender.sendMessage(instance.prefix + getMessage("particles_folder_missing"));
                return true;
            }

            File[] particleFiles = particlesFolder.listFiles();
            if (particleFiles == null || particleFiles.length == 0) {
                sender.sendMessage(instance.prefix + getMessage("no_particles_found"));
                return true;
            }

            sender.sendMessage(instance.prefix + getMessage("list_particles_header"));
            for (File file : particleFiles) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    String particleName = file.getName().replace(".yml", "");
                    sender.sendMessage("- " + particleName);
                }
            }

            return true;
        } else if (args[0].equalsIgnoreCase("particle")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(instance.prefix + getMessage("player_only"));
                return true;
            }
            if (!sender.hasPermission("littlefly.selectparticle")) {
                sender.sendMessage(instance.prefix + getMessage("no_permission"));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(instance.prefix + getMessage("selectparticle_invalid_usage"));
                return true;
            }

            String particleName = args[1];
            Player player = (Player) sender;
            File particleFile = new File(instance.getDataFolder() + "/particles", particleName + ".yml");
            if (!particleFile.exists()) {
                sender.sendMessage(instance.prefix + getMessage("particle_not_found"));
                return true;
            }

            // Check if the player has permission to use the selected particle
            String particlePermission = permissionChecker.getParticlePermission(particleName);
            if (particlePermission == null || !player.hasPermission(particlePermission)) {
                sender.sendMessage(instance.prefix + getMessage("no_permission") + " " + ChatColor.translateAlternateColorCodes('&', "&6"+particlePermission+"&r"));
                return true;
            }

            // Set the selected particle for the player
            PlayerData playerData = new PlayerData(instance, player);
            playerData.setCustomParticles(particleName);
            playerData.saveData();

            String message = getMessage("particle_selected");
            String replaced = message.replaceAll("%particle%", particleName);

            sender.sendMessage(instance.prefix + replaced);
            return true;
        } else if (args[0].equalsIgnoreCase("removeparticle")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(instance.prefix + getMessage("player_only"));
                return true;
            }

            Player player = (Player) sender;

            // Remove the selected particle for the player
            PlayerData playerData = new PlayerData(instance, player);
            playerData.setCustomParticles(null);
            playerData.saveData();

            sender.sendMessage(instance.prefix + getMessage("particle_removed"));
            return true;
        }


        return false;
    }
}
