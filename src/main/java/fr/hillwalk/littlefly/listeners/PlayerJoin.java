package fr.hillwalk.littlefly.listeners;

import fr.hillwalk.littlefly.LittleFly;
import fr.hillwalk.littlefly.configs.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final LittleFly plugin;

    public PlayerJoin(LittleFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = new PlayerData(plugin, player);
        String playerName = player.getName();
        int defaultFlyTime = 0;
        String defaultParticles = ""; //
        playerData.setDefaultValues(playerName, defaultFlyTime, defaultParticles);
        playerData.saveData();
    }


}
