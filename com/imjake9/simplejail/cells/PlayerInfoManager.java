package com.imjake9.simplejail.cells;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerInfoManager implements Listener {
    
    private static PlayerInfoManager instance;
    
    public static PlayerInfoManager getInstance() {
        if (instance == null)
            instance = new PlayerInfoManager();
        return instance;
    }
    
    private Map<String, Map<String, Object>> playerInfo;
    
    public PlayerInfoManager() {
        playerInfo = new HashMap<String, Map<String, Object>>();
    }
    
    public Object getInfo(String player, String key, Object def) {
        return (playerInfo.containsKey(player) && playerInfo.get(player).containsKey(key))
                ? playerInfo.get(player).get(key)
                : def;
    }
    
    public Object getInfo(Player player, String key, Object def) {
        return getInfo(player.getName(), key, def);
    }
    
    public void setInfo(String player, String key, Object value) {
        if (!playerInfo.containsKey(player)) playerInfo.put(player, new HashMap<String, Object>());
        playerInfo.get(player).put(key, value);
    }
    
    public void setInfo(Player player, String key, Object value) {
        setInfo(player.getName(), key, value);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (playerInfo.containsKey(event.getPlayer().getName()))
            playerInfo.remove(event.getPlayer().getName());
    }

}
