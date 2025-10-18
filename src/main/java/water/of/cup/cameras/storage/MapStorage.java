package main.java.water.of.cup.cameras.storage;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;


public class MapStorage {

    /**
     * Store map data directly into Minecraft's map system instead of text files.
     * This prevents parsing errors and makes maps more robust.
     * 
     * @param id The map ID
     * @param data The 2D byte array representing the map pixels
     */
    public static void store(int id, byte[][] data) {
        try {
            // Get the MapView for this ID
            MapView mapView = Bukkit.getMap(id);
            if (mapView == null) {
                Bukkit.getLogger().severe("MapView not found for mapId: " + id);
                return;
            }
            
            // Convert 2D array to 1D array for map data
            byte[] mapData = convertToMapData(data);
            
            // Write directly to the map using reflection
            MapReflection.setMapData(mapView, mapData);
            
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error storing map data for mapId: " + id);
            e.printStackTrace();
        }
    }
    
    /**
     * Convert 2D byte array to 1D map data array.
     * Minecraft maps expect data in row-major order.
     * 
     * @param data 2D byte array [x][y]
     * @return 1D byte array for map data
     */
    private static byte[] convertToMapData(byte[][] data) {
        byte[] mapData = new byte[128 * 128];
        
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                mapData[y * 128 + x] = data[x][y];
            }
        }
        
        return mapData;
    }
    
}
