package main.java.water.of.cup.cameras.listeners;

import main.java.water.of.cup.cameras.Camera;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

public class PrepareItemCraft implements Listener {

    private Camera instance = Camera.getInstance();

    @EventHandler
    public void prepareItemCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;
        ItemStack result = recipe.getResult();
        if (result == null) return;
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        if (meta.getDisplayName().equals(ChatColor.DARK_BLUE + "Camera")) {
            if (instance.getConfig().getBoolean("settings.camera.permissions")) {
                for (HumanEntity he : event.getViewers()) {
                    if (he instanceof Player) {
                        if (!he.hasPermission("cameras.craft")) {
                            event.getInventory().setResult(new ItemStack(Material.AIR));
                        }
                    }
                }
            }
        }
    }
}
