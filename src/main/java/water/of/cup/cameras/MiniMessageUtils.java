package main.java.water.of.cup.cameras;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class MiniMessageUtils {
    
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    /**
     * Send a MiniMessage formatted message to a player
     */
    public static void sendMessage(Player player, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        
        Component component = miniMessage.deserialize(message);
        // Convert Adventure Component to legacy string for Bukkit compatibility
        String legacyMessage = LegacyComponentSerializer.legacySection().serialize(component);
        player.sendMessage(legacyMessage);
    }
    
    /**
     * Send a MiniMessage formatted message to a player with placeholders
     */
    public static void sendMessage(Player player, String message, String placeholder, String value) {
        if (message == null || message.isEmpty()) {
            return;
        }
        
        Component component = miniMessage.deserialize(message, Placeholder.parsed(placeholder, value));
        // Convert Adventure Component to legacy string for Bukkit compatibility
        String legacyMessage = LegacyComponentSerializer.legacySection().serialize(component);
        player.sendMessage(legacyMessage);
    }
    
    /**
     * Parse a MiniMessage string to Component
     */
    public static Component parse(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        
        return miniMessage.deserialize(message);
    }
}
