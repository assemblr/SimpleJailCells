package com.imjake9.simplejail.cells.data;

import com.imjake9.simplejail.cells.SimpleJailCells;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;


public final class JailDataManager {
    
    private static JailDataManager instance;
    
    public static JailDataManager init() {
        return instance = new JailDataManager();
    }
    
    public static JailDataManager getInstance() {
        return instance;
    }
    
    private YamlConfiguration config;
    private File f = new File(SimpleJailCells.getInstance().getDataFolder(), "cells.yml");
    
    public JailDataManager() {
        
        config = new YamlConfiguration();
        load();
        
    }
    
    public void load() {
        try {
            if (!f.exists()) f.createNewFile();
            config.load(f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void save() {
        try {
            config.save(f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Jail get(String key) {
        return (Jail) config.get(key);
    }
    
    public String getRandomJail() {
        List<String> keys = new ArrayList<String>(config.getKeys(false));
        if (keys.isEmpty()) return null;
        Collections.shuffle(keys);
        return keys.get(0);
    }
    
    public String getClosestJail(Location loc) {
        String closest = null;
        double dist = Double.MAX_VALUE;
        for (String key : config.getKeys(false)) {
            Jail jail = (Jail) config.get(key);
            if (!loc.getWorld().equals(jail.jailLoc.getWorld())) continue;
            if (loc.distance(jail.jailLoc) < dist) {
                closest = key;
                dist = loc.distance(jail.jailLoc);
            }
        }
        return closest;
    }
    
    public void set(String key, Jail jail) {
        config.set(key, jail);
    }

}
