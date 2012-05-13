package com.imjake9.simplejail.cells;

import com.imjake9.simplejail.SimpleJail;
import com.imjake9.simplejail.api.SimpleJailCommandListener.Priority;
import com.imjake9.simplejail.cells.data.Jail;
import com.imjake9.simplejail.cells.data.JailCell;
import com.imjake9.simplejail.cells.data.JailDataManager;
import com.imjake9.simplejail.cells.data.SerializableLocation;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleJailCells extends JavaPlugin {
    
    public static final Logger log = Logger.getLogger("Minecraft");
    
    private static SimpleJailCells plugin;
    
    private CommandHandler commandHandler;
    private JailListener listener;
    
    static {
        ConfigurationSerialization.registerClass(SerializableLocation.class, "BukkitLocation");
        ConfigurationSerialization.registerClass(Jail.class, "Jail");
        ConfigurationSerialization.registerClass(JailCell.class, "Cell");
    }
    
    public static SimpleJailCells getInstance() {
        return plugin;
    }
    
    @Override
    public void onEnable() {
        
        plugin = this;
        
        // Deal with Cells commands
        commandHandler = new CommandHandler();
        
        // Set up SimpleJail events
        listener = new JailListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
        this.getServer().getPluginManager().registerEvents(PlayerInfoManager.getInstance(), this);
        SimpleJail.getPlugin().registerCommandListener(listener, Priority.HIGH);
        SimpleJail.getPlugin().setCommandUsage("jail", "/<command> <player> <jail[:cell]|*|.> [time]");
        
        // Load config files
        this.loadConfig();
        
    }
    
    @Override
    public void onDisable() {
        plugin = null;
    }
    
    private void loadConfig() {
        
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        
        JailDataManager.init();
        
    }
    
    
    
    /**
     * Manages various SimpleJailCell messages.
     */
    public enum JailCellMessage {
        CELL_CREATED (ChatColor.AQUA + "Cell '%1' added to '%2'."),
        CELL_REMOVED (ChatColor.AQUA + "Cell '%1' removed from '%2'."),
        FLAG_SET (ChatColor.AQUA + "Flag '%1' updated."),
        JAIL_CREATED (ChatColor.AQUA + "Jail '%1' created."),
        JAIL_REMOVED (ChatColor.AQUA + "Jail '%1' deleted."),
        JAIL_TO_CELL (ChatColor.AQUA + "Player '%1' sent to '%2'."),
        JAILED_TO_CELL (ChatColor.AQUA + "You have been jailed to '%1'!"),
        MISSING_VALUE (ChatColor.RED + "That flag requires a value."),
        NO_CELL_WITH_NAME (ChatColor.RED + "No cell name '%1' in '%2'."),
        NO_JAIL_WITH_NAME (ChatColor.RED + "No jail named '%1'."),
        NO_SUCH_FLAG (ChatColor.RED + "There is no key named '%1'."),
        PLAYER_MUST_BE_ONLINE (ChatColor.RED + "Player must be online to use the . parameter.");
        
        private String format;
        
        JailCellMessage(String format) {
            this.format = format;
        }
        
        /**
         * Gets the message as a String.
         * 
         * @return the message
         */
        public String message() {
            return format;
        }
        
        /**
         * Gets the message with arguments filled.
         * 
         * @param args list of arguments
         * @return the message
         */
        public String message(String... args) {
            String message = format;
            for(int i = 1; ; i++) {
                if (message.indexOf("%" + i) > 0) {
                    message = message.replaceAll("%" + i, args[i - 1]);
                } else break;
            }
            return message;
        }
        
        /**
         * Sends a message.
         * 
         * @param sender reciever
         */
        public void send(CommandSender sender) {
            sender.sendMessage(format);
        }
        
        /**
         * Sends a message with arguments.
         * 
         * @param sender reciever
         * @param args list of arguments
         */
        public void send(CommandSender sender, String... args) {
            sender.sendMessage(message(args));
        }
        
        /**
         * Prints a message prefixed with [SimpleJail] to the console.
         */
        public void print() {
            log.info("[SimpleJailCell] " + format);
        }
        
        
        /**
         * Prints a message with arguments prefixed with [SimpleJail] to the console.
         * 
         * @param args 
         */
        public void print(String... args) {
            log.info("[SimpleJailCell] " + message(args));
        }
        
    }
    
}
