package fr.hillwalk.littlefly.utils;

import fr.hillwalk.littlefly.LittleFly;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class Utils {

    private final LittleFly instance;

    public Utils(LittleFly instance){

        this.instance = instance;

    }

    public List<String> getWorlds() {
        return instance.getConfig().getStringList("worlds_whitelist");
    }

    public boolean isWorldAllowed(Player player) {
        World world = player.getWorld();
        List<String> allowedWorlds = getWorlds();
        return allowedWorlds.contains(world.getName());
    }

    public static String convertTime(int seconds) {
        if (seconds < 0) {
            return "Invalid time";
        }

        int weeks = seconds / 604800;
        int days = (seconds % 604800) / 86400;
        int hours = (seconds % 86400) / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        StringBuilder builder = new StringBuilder();

        if (weeks > 0) {
            builder.append(weeks).append(" week").append(weeks > 1 ? "s" : "").append(" ");
        }
        if (days > 0) {
            builder.append(days).append(" day").append(days > 1 ? "s" : "").append(" ");
        }
        if (hours > 0) {
            builder.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(" ");
        }
        if (minutes > 0) {
            builder.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(" ");
        }
        if (remainingSeconds > 0) {
            builder.append(remainingSeconds).append(" second").append(remainingSeconds > 1 ? "s" : "").append(" ");
        }

        String result = builder.toString().trim();
        return result.isEmpty() ? "Less than a second" : result;
    }


}
