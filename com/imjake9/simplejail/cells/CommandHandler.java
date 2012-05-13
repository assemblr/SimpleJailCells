package com.imjake9.simplejail.cells;

import com.imjake9.simplejail.JailException;
import com.imjake9.simplejail.SimpleJail;
import com.imjake9.simplejail.cells.SimpleJailCells.JailCellMessage;
import com.imjake9.simplejail.cells.data.Jail;
import com.imjake9.simplejail.cells.data.JailCell;
import com.imjake9.simplejail.cells.data.JailDataManager;
import com.imjake9.simplejail.cells.data.SerializableLocation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;


public class CommandHandler implements CommandExecutor {
    
    public CommandHandler() {
        
        // Register all commands
        SimpleJailCells plugin = SimpleJailCells.getInstance();
        PluginCommand[] commands = {
            plugin.getCommand("createjail"),
            plugin.getCommand("removejail"),
            plugin.getCommand("createcell"),
            plugin.getCommand("removecell"),
            plugin.getCommand("jailflag"),
            plugin.getCommand("cellflag"),
        };
        for (PluginCommand c : commands) {
            c.setExecutor(this);
            if (c.getPermission() == null) c.setPermission("simplejailcells." + c.getName());
            c.setPermissionMessage(ChatColor.RED + "You do not have permission to use that command (" + c.getPermission() + ").");
        }
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        
        // Get the real command's name
        commandLabel = cmd.getName();
        
        // Make sure the sender is a player
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        
        // Handle command
        if (commandLabel.equalsIgnoreCase("createjail")) {
            
            // usage: /<command> <name>
            if (args.length != 1) return false;
            
            // Create new jail
            Jail jail = new Jail();
            jail.jailLoc = jail.unjailLoc = new SerializableLocation(player.getLocation());
            jail.jailLimit = (Integer) PlayerInfoManager.getInstance().getInfo(player, "jailLimit", 0);
            // Save jail
            JailDataManager.getInstance().set(args[0], jail);
            JailDataManager.getInstance().save();
            
            // Send success message
            JailCellMessage.JAIL_CREATED.send(sender, args[0]);
            return true;
            
        } else if (commandLabel.equalsIgnoreCase("removejail")) {
            
            // usage: /<command> <name>
            if (args.length != 1) return false;
            
            // Get jail
            Jail jail = JailDataManager.getInstance().get(args[0]);
            // Make sure jail exists
            if (jail == null) {
                JailCellMessage.NO_JAIL_WITH_NAME.send(sender, args[0]);
                return true;
            }
            // Unjail all players in jail
            for (String jailed : SimpleJail.getPlugin().getJailedPlayers()) {
                String jailName = SimpleJail.getPlugin().getJailString(jailed, "jailname");
                if (jailName != null && jailName.equals(args[0])) {
                    try {
                        SimpleJail.getPlugin().unjailPlayer(jailed, jail.unjailLoc);
                    } catch (JailException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            // Remove jail
            JailDataManager.getInstance().set(args[0], null);
            JailDataManager.getInstance().save();
            
            // Send success message
            JailCellMessage.JAIL_REMOVED.send(sender, args[0]);
            return true;
            
        } else if (commandLabel.equalsIgnoreCase("createcell")) {
            
            // usage: /<command> <jail:name>
            if (args.length != 1) return false;
            
            // Split jail:cell tag
            String[] tag = args[0].split(":");
            if (tag.length != 2) return false;
            
            // Get jail
            Jail jail = JailDataManager.getInstance().get(tag[0]);
            if (jail == null) {
                JailCellMessage.NO_JAIL_WITH_NAME.send(sender, tag[0]);
                return true;
            }
            // Add cell
            JailCell cell = new JailCell();
            cell.jailLoc = cell.unjailLoc = new SerializableLocation(player.getLocation());
            cell.jailLimit = (Integer) PlayerInfoManager.getInstance().getInfo(player, "jailLimit", 0);
            jail.cells.put(tag[1], cell);
            // Save jail
            JailDataManager.getInstance().set(tag[0], jail);
            JailDataManager.getInstance().save();
            
            // Send success message
            JailCellMessage.CELL_CREATED.send(sender, tag[1], tag[0]);
            return true;
            
        } else if (commandLabel.equalsIgnoreCase("removecell")) {
            
            // usage: /<command> <jail:name>
            if (args.length != 1) return false;
            
            // Split jail:cell tag
            String[] tag = args[0].split(":");
            if (tag.length != 2) return false;
            
            // Get jail
            Jail jail = JailDataManager.getInstance().get(tag[0]);
            if (jail == null) {
                JailCellMessage.NO_JAIL_WITH_NAME.send(sender, tag[0]);
                return true;
            }
            // Unjail all players in cell
            for (String jailed : SimpleJail.getPlugin().getJailedPlayers()) {
                String cellName = SimpleJail.getPlugin().getJailString(jailed, "cellname");
                if (cellName != null && cellName.equals(args[0])) {
                    try {
                        SimpleJail.getPlugin().unjailPlayer(jailed, jail.unjailLoc);
                    } catch (JailException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            // Remove cell
            if (!jail.cells.containsKey(tag[1])) {
                JailCellMessage.NO_CELL_WITH_NAME.send(sender, tag[1], tag[0]);
                return true;
            }
            jail.cells.remove(tag[1]);
            // Save jail
            JailDataManager.getInstance().set(tag[0], jail);
            JailDataManager.getInstance().save();
            
            // Send success message
            JailCellMessage.CELL_REMOVED.send(sender, tag[1], tag[0]);
            return true;
            
        } else if (commandLabel.equalsIgnoreCase("jailflag")) {
            
            // usage: /<command> <jail> <flag> [value]
            if (args.length != 2 && args.length != 3) return false;
            
            // Get jail
            Jail jail = JailDataManager.getInstance().get(args[0]);
            if (jail == null) {
                JailCellMessage.NO_JAIL_WITH_NAME.send(sender, args[0]);
                return true;
            }
            // Set flag
            String flag = args[1];
            String value = args.length == 3 ? args[2] : null;
            if (flag.equalsIgnoreCase("jail")) {
                jail.jailLoc = new SerializableLocation(player.getLocation());
            } else if (flag.equalsIgnoreCase("unjail")) {
                jail.unjailLoc = new SerializableLocation(player.getLocation());
            } else {
                JailCellMessage.NO_SUCH_FLAG.send(sender, flag);
                return false;
            }
            // Save jail
            JailDataManager.getInstance().set(args[0], jail);
            JailDataManager.getInstance().save();
            
            // Send success message
            JailCellMessage.FLAG_SET.send(sender, flag);
            return true;
            
        } else if (commandLabel.equalsIgnoreCase("cellflag")) {
            
            // usage: /<command> <jail:cell> <flag> [value]
            if (args.length != 2 && args.length != 3) return false;
            
            // Split jail:cell tag
            String[] tag = args[0].split(":");
            if (tag.length != 2) return false;
            
            // Get jail
            Jail jail = JailDataManager.getInstance().get(tag[0]);
            if (jail == null) {
                JailCellMessage.NO_JAIL_WITH_NAME.send(sender, tag[0]);
                return true;
            }
            // Get cell
            JailCell cell = jail.cells.get(tag[1]);
            if (cell == null) {
                JailCellMessage.NO_CELL_WITH_NAME.send(sender, tag[1], tag[0]);
                return true;
            }
            // Set flag
            String flag = args[1];
            String value = args.length == 3 ? args[2] : null;
            if (flag.equalsIgnoreCase("jail")) {
                cell.jailLoc = new SerializableLocation(player.getLocation());
            } else if (flag.equalsIgnoreCase("unjail")) {
                cell.unjailLoc = new SerializableLocation(player.getLocation());
            } else {
                JailCellMessage.NO_SUCH_FLAG.send(sender, flag);
                return false;
            }
            // Save jail
            JailDataManager.getInstance().set(args[0], jail);
            JailDataManager.getInstance().save();
            
            // Send success message
            JailCellMessage.FLAG_SET.send(sender, flag);
            return true;
            
        }
        
        return false;
        
    }
    
}
