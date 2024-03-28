package fr.hillwalk.littlefly.commands;

import fr.hillwalk.littlefly.LittleFly;
import fr.hillwalk.littlefly.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FlyCommand implements CommandExecutor {

    private final LittleFly instance;


    public FlyCommand(LittleFly instance) {
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


        // Check if the player has permission to use the command for others
        if (player.hasPermission("littlefly.fly.others") && args.length > 0) {
            Player target = instance.getServer().getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(instance.prefix + getMessage("player_not_found"));
                return true;
            }

            toggleFlight(target);
            return true;
        }

        // Check if the player has permission to use the command for self
        if (!player.hasPermission("littlefly.fly")) {
            player.sendMessage(instance.prefix + getMessage("no_permission") + " => legendfly.fly");
            return true;
        }

        toggleFlight(player);
        return true;
    }

    private void toggleFlight(Player player) {
        // Check if the player can currently fly
        if (player.getAllowFlight()) {
            // Flight is currently enabled, disable it
            player.setAllowFlight(false);
            player.setFlying(false);

            // Notify player that flight has been disabled
            player.sendMessage(instance.prefix + getMessage("flight_disabled"));
        } else {
            // Flight is currently disabled, enable it
            player.setAllowFlight(true);
            player.setFlying(true);

            // Notify player that flight has been enabled
            player.sendMessage(instance.prefix + getMessage("flight_enabled"));
        }
    }
}
