package fr.hillwalk.littlefly.commands;

import fr.hillwalk.littlefly.LittleFly;
import fr.hillwalk.littlefly.configs.ParticleConfig;
import fr.hillwalk.littlefly.configs.PlayerData;
import fr.hillwalk.littlefly.particules.PlayerParticles;
import fr.hillwalk.littlefly.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

import static java.lang.String.format;

public class TimeFlyCommand implements CommandExecutor {

    private final LittleFly instance;

    public TimeFlyCommand(LittleFly instance) {
        this.instance = instance;
    }

    private String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("messages." + key));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check if the command was executed by a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(instance.prefix + getMessage("player_only"));
            return true;
        }

        Player player = (Player) sender;
        Utils utils = new Utils(instance);

        if (!utils.isWorldAllowed(player)) {
            sender.sendMessage(instance.prefix + getMessage("not_in_world"));
            return true;
        }

        // Check if the player has permission to use the command
        if (!player.hasPermission("littlefly.tfly")) {
            player.sendMessage(instance.prefix + getMessage("no_permission") + " => littlefly.tfly");
            return true;
        }

        // Check the number of arguments
        if (args.length < 2) {
            player.sendMessage(instance.prefix + getMessage("tfly_invalid_usage"));
            return true;
        }

        Player target = player;

        // Check if the player has permission to use the command for others
        if (player.hasPermission("littlefly.tfly.others") && args.length > 2) {
            target = instance.getServer().getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(instance.prefix + getMessage("player_not_found"));
                return true;
            }
        }

        // Check if the third argument is a valid integer
        int duration;
        try {
            duration = Integer.parseInt(args[args.length - 1]);
        } catch (NumberFormatException e) {
            player.sendMessage(instance.prefix + getMessage("invalid_duration"));
            return true;
        }

        // Activate flight with time limit for the target player
        instance.setFlightDuration(target, duration);

        // Activer le vol avec une limite de temps pour le joueur cible
        activateFlightWithTimeLimit(target, duration);

        return true;
    }

    private void activateFlightWithTimeLimit(Player player, int duration) {
        // Enable flight for the player
        player.setAllowFlight(true);
        player.setFlying(true);
        PlayerData data = new PlayerData(instance, player);
        String particleName = data.getCustomParticles();
        ParticleConfig particleConfig = new ParticleConfig(instance);

        FileConfiguration particleInfo = particleConfig.getInfos(particleName);
        if (particleInfo == null) {
            // Handle case where particle configuration is not found
            player.sendMessage(instance.prefix + getMessage("no_particles_found"));
            return;
        }

        String particleJava = particleInfo.getString("type");
        double offsetX = particleInfo.getDouble("offsetX");
        double offsetY = particleInfo.getDouble("offsetY");
        double offsetZ = particleInfo.getDouble("offsetZ");
        double speed = particleInfo.getDouble("speed");


        if (particleInfo == null) {
            // Gérer le cas où la configuration des particules n'est pas trouvée
            player.sendMessage(instance.prefix + getMessage("no_particles_found"));
            return;
        }

        PlayerParticles particles = new PlayerParticles(player, instance, particleInfo); // Utilisez la configuration des particules pour instancier PlayerParticles
        particles.start(particleJava.toUpperCase(), offsetX, offsetY, offsetZ, speed); // Démarrez l'émission des particules pour le joueur

        String messageTime = getMessage("t_flight_enabled");
        String replaced = messageTime.replaceAll("%time%", Utils.convertTime(duration));

        player.sendMessage(instance.prefix + replaced);
        instance.setFlightDuration(player, duration);

        // Check if action bar messages are enabled in the config
        if (instance.getConfig().getBoolean("show_action_bar")) {
            // Schedule task to update action bar with remaining flight duration
            Bukkit.getScheduler().runTaskTimer(instance, () -> {
                int remainingTime = duration - 1;
                String message = instance.getConfig().getString("messages.action_bar_message");
                String replacedMessage = PlaceholderAPI.setPlaceholders(player, message); // Using PlaceholderAPI

                replacedMessage = replacedMessage.replaceAll("%time%", Utils.convertTime(remainingTime));

                String actionBarMessage = ChatColor.translateAlternateColorCodes('&', replacedMessage);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(format(actionBarMessage)));
            }, 0L, 20L); // Update every second
        } else {
            // Schedule task to send messages at specified intervals
            List<Integer> intervals = instance.getConfig().getIntegerList("announcement_intervals");
            Bukkit.getScheduler().runTaskTimer(instance, () -> {
                int remainingTime = duration - 1;
                if (intervals.contains(remainingTime)) {
                    String message = instance.getConfig().getString("messages.action_bar_message");
                    String replacedMessage = PlaceholderAPI.setPlaceholders(player, message); // Using PlaceholderAPI

                    replacedMessage = replacedMessage.replaceAll("%time%", Utils.convertTime(remainingTime));

                    String actionBarMessage = ChatColor.translateAlternateColorCodes('&', replacedMessage);
                    player.sendMessage(actionBarMessage);
                }
            }, 0L, 20L); // Update every second
        }

        // Schedule task to disable flight after specified duration
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            // Disable flight after specified duration
            player.setAllowFlight(false);
            player.setFlying(false);
            particles.stop();

            // Remove flight duration from the player
            instance.removeFlightDuration(player);

            // Notify player that flight has been disabled
            player.sendMessage(instance.prefix + getMessage("t_flight_disabled"));
        }, duration * 20L); // Convert seconds to ticks (1 second = 20 ticks)
    }

}
