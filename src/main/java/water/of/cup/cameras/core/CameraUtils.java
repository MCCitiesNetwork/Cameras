package main.java.water.of.cup.cameras.core;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CameraUtils {
    
    private static final NamespacedKey CAMERA_KEY = new NamespacedKey(Camera.getInstance(), "camera");
    
    /**
     * Check if an item is a camera using PDC tag
     */
    public static boolean isCamera(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        // Check PDC tag in PublicBukkitValues namespace
        NamespacedKey publicBukkitKey = new NamespacedKey("camera", "camera");
        return meta.getPersistentDataContainer().has(publicBukkitKey, PersistentDataType.BYTE);
    }
    
}
