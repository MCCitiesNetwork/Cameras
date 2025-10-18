package main.java.water.of.cup.cameras.listeners;

import main.java.water.of.cup.cameras.core.CameraUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class CameraPlace implements Listener {
    @EventHandler
    public void cameraPlaced(BlockPlaceEvent e) {
        //Prevent players from placing Cameras

        ItemStack item = e.getItemInHand();
        if (CameraUtils.isCamera(item)) {
            e.setCancelled(true);
        }
    }
}
