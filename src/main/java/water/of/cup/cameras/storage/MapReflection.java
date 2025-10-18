package main.java.water.of.cup.cameras.storage;

import org.bukkit.map.MapView;

import java.lang.reflect.Field;

/**
 * Utility class for accessing Minecraft's internal map data using reflection.
 * This allows us to write directly to map files instead of using text files.
 * Based on ArtMap's reflection approach for Paper 1.21.8 compatibility.
 */
public class MapReflection {
    
    /**
     * Gets the raw map data from a MapView using reflection.
     * Uses the same approach as ArtMap for Paper 1.21.8 compatibility.
     * 
     * @param mapView The MapView to get data from
     * @return The raw map data as a byte array
     */
    public static byte[] getMapData(MapView mapView) {
        try {
            // Access the worldMap field directly on MapView (like ArtMap does)
            Object worldMap = getField(mapView, "worldMap");
            
            // Try to get the colors field (modern versions)
            try {
                return (byte[]) getField(worldMap, "colors");
            } catch (NoSuchFieldException e) {
                // Fallback for older versions (1.17 style)
                return (byte[]) getField(worldMap, "g");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[128 * 128]; // Return blank map on error
        }
    }
    
    /**
     * Sets the raw map data for a MapView using reflection.
     * Uses the same approach as ArtMap for Paper 1.21.8 compatibility.
     * 
     * @param mapView The MapView to set data for
     * @param mapData The raw map data as a byte array
     */
    public static void setMapData(MapView mapView, byte[] mapData) {
        try {
            // Set map center to prevent tracking (like ArtMap does)
            mapView.setCenterX(-999999);
            mapView.setCenterZ(-999999);
            
            // Access the worldMap field directly on MapView
            Object worldMap = getField(mapView, "worldMap");
            
            // Try to set the colors field (modern versions)
            try {
                setField(worldMap, "colors", mapData);
            } catch (NoSuchFieldException e) {
                // Fallback for older versions (1.17 style)
                setField(worldMap, "g", mapData);
            }
            
            // Set scale to farthest (like ArtMap does)
            mapView.setScale(MapView.Scale.FARTHEST);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if a MapView has valid map data.
     * 
     * @param mapView The MapView to check
     * @return True if the map has valid data
     */
    public static boolean hasValidMapData(MapView mapView) {
        byte[] data = getMapData(mapView);
        return data != null && data.length == 128 * 128;
    }
    
    /**
     * Helper method to get a field value using reflection.
     * Based on ArtMap's reflection utility.
     */
    private static Object getField(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
    
    /**
     * Helper method to set a field value using reflection.
     * Based on ArtMap's reflection utility.
     */
    private static void setField(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
