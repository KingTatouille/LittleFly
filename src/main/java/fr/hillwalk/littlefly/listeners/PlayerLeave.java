package fr.hillwalk.littlefly.listeners;

import fr.hillwalk.littlefly.LittleFly;
import fr.hillwalk.littlefly.configs.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    private final LittleFly instance;

    public PlayerLeave(LittleFly instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Sauvegarder la durée de vol du joueur
        int flightDuration = instance.getFlightDuration(player);
        PlayerData playerData = new PlayerData(instance, player);
        playerData.setCurrentFlyTime(flightDuration);
        playerData.saveData();

        // Supprimer la durée de vol du joueur
        instance.removeFlightDuration(player);
    }

}
