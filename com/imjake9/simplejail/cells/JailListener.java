package com.imjake9.simplejail.cells;

import com.imjake9.simplejail.JailException;
import com.imjake9.simplejail.SimpleJail;
import com.imjake9.simplejail.api.SimpleJailCommandListener;
import com.imjake9.simplejail.cells.SimpleJailCells.JailCellMessage;
import com.imjake9.simplejail.cells.data.Jail;
import com.imjake9.simplejail.cells.data.JailCell;
import com.imjake9.simplejail.cells.data.JailDataManager;
import com.imjake9.simplejail.events.PlayerUnjailEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class JailListener implements Listener, SimpleJailCommandListener {
    
    private final SimpleJailCells plugin;
    
    public JailListener(SimpleJailCells plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerUnjail(PlayerUnjailEvent e) {
        
        String jailName = e.getInfo().getString("jailname");
        if (jailName == null) return;
        Jail jail = JailDataManager.getInstance().get(jailName);
        JailCell cell = jail;
        if (jail == null) return;
        String cellName = e.getInfo().getString("cellname");
        if (cellName != null) {
            cell = jail.cells.get(cellName);
            if (cell == null) cell = jail;
        }
        e.setUnjailLocation(cell.unjailLoc);
        
    }
    
    @Override
    public HandleStatus handleJailCommand(CommandSender sender, String command, String[] args) {
        
        // Handle commands
        if (command.equalsIgnoreCase("jail")) {
            
            // usage: /<command> <player> <jail[:cell]|*|.> [time]
            //        * = random jail, . = closest jail
            if (args.length != 2 && args.length != 3) return HandleStatus.FAILURE;
            
            // Get player to jail
            String player = args[0];
            Player bPlayer = Bukkit.getPlayer(player);
            boolean online = bPlayer != null && bPlayer.isOnline();
            if (online) player = bPlayer.getName();
            
            // Get jail information
            String jail = args[1];
            String cell = null;
            if (!jail.equals("*") && !jail.equals(".")) {
                String[] tag = args[1].split(":");
                if (tag.length == 2) {
                    jail = tag[0];
                    cell = tag[1];
                } else if (tag.length != 1) return HandleStatus.FAILURE;
                // Validate jail/cell
                Jail j = JailDataManager.getInstance().get(jail);
                if (j == null) {
                    JailCellMessage.NO_JAIL_WITH_NAME.send(sender, tag[0]);
                    return HandleStatus.SUCCESS;
                }
                if (cell != null) {
                    JailCell c = j.cells.get(cell);
                    if (c == null) {
                        JailCellMessage.NO_CELL_WITH_NAME.send(sender, tag[1], tag[0]);
                        return HandleStatus.SUCCESS;
                    }
                }
            }
            // Parse special characters
            if (jail.equals("*")) {
                jail = JailDataManager.getInstance().getRandomJail();
            } else if (jail.equals(".")) {
                if (!online) {
                    JailCellMessage.PLAYER_MUST_BE_ONLINE.send(sender);
                    return HandleStatus.FAILURE;
                }
                jail = JailDataManager.getInstance().getClosestJail(bPlayer.getLocation());
            }
            // Get final location
            Jail j = JailDataManager.getInstance().get(jail);
            Location loc = j.jailLoc;
            if (cell != null) loc = j.cells.get(cell).jailLoc;
            
            // Jail player
            try {
                if (args.length == 3) {
                    int time = SimpleJail.getPlugin().parseTimeString(args[2]);
                    SimpleJail.getPlugin().jailPlayer(player, sender.getName(), time, loc);
                } else {
                    SimpleJail.getPlugin().jailPlayer(player, sender.getName(), loc);
                }
            } catch (JailException ex) {
                sender.sendMessage(ex.getFormattedMessage());
                return HandleStatus.FAILURE;
            }
            
            // Update player metadata
            SimpleJail.getPlugin().setJailParameter(player, "jailname", jail);
            if (cell != null) SimpleJail.getPlugin().setJailParameter(player, "cellname", cell);
            
            // Send success message
            JailCellMessage.JAIL_TO_CELL.send(sender, player, jail);
            return HandleStatus.SUCCESS;
        }
        
        return HandleStatus.UNHANDLED;
        
    }
    
}
