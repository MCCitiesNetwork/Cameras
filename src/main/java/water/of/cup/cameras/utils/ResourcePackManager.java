package main.java.water.of.cup.cameras.utils;

import main.java.water.of.cup.cameras.core.Camera;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class ResourcePackManager {

    private File resourcePackFile;
    private HashMap<Material, BufferedImage> imageHashMap = new HashMap<>();
    private boolean isLoaded;

    public void initialize() {
        File dataFolder = Camera.getInstance().getDataFolder();
        File resourcesDir = new File(dataFolder, "textures");
        if (!resourcesDir.exists()) {
            resourcesDir.mkdir();
        }

        // Look for extracted textures directly in resources folder
        if (!resourcesDir.exists() || resourcesDir.listFiles() == null || resourcesDir.listFiles().length == 0) {
            Bukkit.getLogger().info("No textures found, downloading from official Minecraft client asynchronously...");
            
            // Run download asynchronously to avoid blocking the main thread
            new BukkitRunnable() {
                @Override
                public void run() {
                    downloadResourcePack();
                    
                    // Check if download was successful
                    if (resourcesDir.exists() && resourcesDir.listFiles() != null && resourcesDir.listFiles().length > 0) {
                        // Set the resource pack file and initialize textures
                        resourcePackFile = resourcesDir;
                        initializeImageHashmap();
                    } else {
                        Bukkit.getLogger().warning("[Camera] Failed to download or extract textures. Please check your internet connection and restart.");
                    }
                }
            }.runTaskAsynchronously(Camera.getInstance());
            
            // Return early - textures will be loaded when download completes
            return;
        }
        
        this.resourcePackFile = resourcesDir;

        Bukkit.getLogger().info("Loading block textures from official Minecraft client (this may take a while)");
        initializeImageHashmap();
    }

    public File getTextureByMaterial(Material material) {
        if (this.resourcePackFile == null) {
            Bukkit.getLogger().warning("Tried getting texture file but no resource path found.");
            return null;
        }

        String textureName = material.toString().toLowerCase();
        File[] listOfFiles = this.resourcePackFile.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();

                if (fileName.toLowerCase().contains(textureName))
                    return file;
                while (textureName.contains("_")) {
                    textureName = textureName.substring(0, textureName.lastIndexOf('_'));
                    if (fileName.toLowerCase().contains(textureName))
                        return file;
                }
            }
        }

        return null;
    }

    private void initializeImageHashmap() {
        if (this.resourcePackFile == null) {
            Bukkit.getLogger().warning("Tried getting texture file but no resource path found.");
            return;
        }

        // Run texture loading asynchronously to avoid blocking
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Material material : Material.values()) {
                    File textureFile = getTextureByMaterial(material);
                    if (textureFile != null) {
                        try {
                            BufferedImage image = ImageIO.read(textureFile);
                            imageHashMap.put(material, image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                isLoaded = true;
            }
        }.runTaskAsynchronously(Camera.getInstance());
    }

    public HashMap<Material, BufferedImage> getImageHashMap() {
        return this.imageHashMap;
    }

    private void downloadResourcePack() {
        File destLocation = new File(Camera.getInstance().getDataFolder() + "/textures/");
        File clientJarLocation = new File(Camera.getInstance().getDataFolder() + "/textures/client.jar");
        
        try {
            // Download the official Minecraft client JAR
            try (BufferedInputStream in = new BufferedInputStream(URI.create("https://piston-data.mojang.com/v1/objects/a19d9badbea944a4369fd0059e53bf7286597576/client.jar").toURL().openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(clientJarLocation)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }

            if (!destLocation.exists()) {
                destLocation.mkdirs();
            }

            // Extract only the block textures from the client JAR
            extractBlockTexturesFromJar(clientJarLocation, destLocation);
            
            // Clean up the JAR file
            clientJarLocation.delete();
            
        } catch (IOException e) {
            Bukkit.getLogger().severe("[Camera] Failed to download or extract client JAR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Extracts block texture files from the Minecraft client JAR.
     * Only extracts the textures we need for block rendering.
     */
    private void extractBlockTexturesFromJar(File jarFile, File destDir) {
        int extractedCount = 0;
        try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
            java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
            
            while (entries.hasMoreElements()) {
                java.util.jar.JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                
                // Only extract block texture files
                if (entryName.startsWith("assets/minecraft/textures/block/") && 
                    entryName.endsWith(".png") && 
                    !entry.isDirectory()) {
                    
                    // Create the destination file directly in destDir (no subfolders)
                    String fileName = entryName.substring(entryName.lastIndexOf('/') + 1);
                    File destFile = new File(destDir, fileName);
                    
                    // Extract the file
                    try (java.io.InputStream in = jar.getInputStream(entry);
                         FileOutputStream out = new FileOutputStream(destFile)) {
                        
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        extractedCount++;
                    }
                }
            }
            
        } catch (IOException e) {
            Bukkit.getLogger().severe("[Camera] Failed to extract textures from JAR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }
    
    /**
     * Check if textures are actually loaded and ready for use.
     * This is used internally for optimization, not for blocking camera usage.
     */
    public boolean areTexturesReady() {
        return this.isLoaded && this.imageHashMap != null && !this.imageHashMap.isEmpty();
    }
}
