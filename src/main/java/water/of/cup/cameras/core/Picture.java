package main.java.water.of.cup.cameras.core;

import main.java.water.of.cup.cameras.utils.MiniMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.HashMap;

public class Picture {
    private static HashMap<Player, Long> delayMap = new HashMap<>();

    public static boolean takePicture(Player p) {
        Camera instance = Camera.getInstance();
        if (instance.getResourcePackManager().areTexturesReady()) {
            if (instance.getConfig().getBoolean("settings.delay.enabled")) {
                if (!delayMap.containsKey(p)) {
                    delayMap.put(p, System.currentTimeMillis());
                } else {
                    int delay = instance.getConfig().getInt("settings.delay.amount");
                    if (System.currentTimeMillis() - delayMap.get(p) >= delay) {
                        delayMap.put(p, System.currentTimeMillis());
                    } else {
                        String message = instance.getConfig().getString("settings.messages.delay");
                        MiniMessageUtils.sendMessage(p, message);
                        return false;
                    }
                }
            }
        } else {
            if (instance.getResourcePackManager().isLoaded()) {
                MiniMessageUtils.sendMessage(p, "<red>Camera textures are still loading, please wait...");
            } else {
                String message = instance.getConfig().getString("settings.messages.notready");
                MiniMessageUtils.sendMessage(p, message);
            }
            return false;
        }

        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        MapView mapView = Bukkit.createMap(p.getWorld());

        mapView.setTrackingPosition(false);

        for (MapRenderer renderer : mapView.getRenderers())
            mapView.removeRenderer(renderer);

        main.java.water.of.cup.cameras.rendering.Renderer customRenderer = new main.java.water.of.cup.cameras.rendering.Renderer();
        mapView.addRenderer(customRenderer);
        mapMeta.setMapView(mapView);

        itemStack.setItemMeta(mapMeta);
        p.getInventory().addItem(itemStack);

        // Send success message
        String successMessage = instance.getConfig().getString("settings.messages.success");
        MiniMessageUtils.sendMessage(p, successMessage);

        return true;
    }

}
