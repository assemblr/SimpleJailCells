package com.imjake9.simplejail.cells;

import com.imjake9.simplejail.SimpleJail;
import com.imjake9.simplejail.api.SimpleJailCommandListener.Priority;
import com.imjake9.simplejail.cells.data.Jail;
import com.imjake9.simplejail.cells.data.JailCell;
import com.imjake9.simplejail.cells.data.JailDataManager;
import com.imjake9.simplejail.cells.data.SerializableLocation;
import com.imjake9.simplejail.utils.MessageTemplate;
import com.imjake9.simplejail.utils.Messager;
import com.imjake9.simplejail.utils.Messaging;
import com.imjake9.simplejail.utils.Messaging.MessageLevel;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleJailCells extends JavaPlugin {
    
    public static final Logger log = Logger.getLogger("Minecraft");
    
    private static SimpleJailCells plugin;
    
    private Messager messager;
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
    
    public static Messager getMessager() {
        return plugin.messager;
    }
    
    @Override
    public void onEnable() {
        
        plugin = this;
        messager = new Messager(this);
        
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
    public enum JailCellMessage implements MessageTemplate {
        CELL_CREATED (MessageLevel.COMPLETE, "Cell <i>%1</i> added to <i>%2</i>."),
        CELL_REMOVED (MessageLevel.COMPLETE, "Cell <i>%1</i> removed from <i>%2</i>."),
        FLAG_SET (MessageLevel.COMPLETE, "Flag <i>%1</i> updated."),
        JAIL_CREATED (MessageLevel.COMPLETE, "Jail <i>%1</i> created."),
        JAIL_REMOVED (MessageLevel.COMPLETE, "Jail <i>%1<i> deleted."),
        JAIL_TO_CELL (MessageLevel.COMPLETE, "Player <i>%1</i> sent to <i>%2</i>."),
        JAILED_TO_CELL (MessageLevel.COMPLETE, "You have been jailed to <i>%1</i>!"),
        MISSING_VALUE (MessageLevel.ERROR, "That flag requires a value."),
        NO_CELL_WITH_NAME (MessageLevel.ERROR, "No cell name <i>%1</i> in <i>%2</i>."),
        NO_JAIL_WITH_NAME (MessageLevel.ERROR, "No jail named <i>%1</i>."),
        NO_SUCH_FLAG (MessageLevel.ERROR, "There is no key named <i>%1</i>."),
        PLAYER_MUST_BE_ONLINE (MessageLevel.ERROR, "Player must be online to use the . parameter.");
        
        private MessageLevel level;
        private String format;
        
        JailCellMessage(MessageLevel level, String format) {
            this.level = level;
            this.format = Messaging.parseStyling(level.getOpeningTag() + format + level.getClosingTag());
        }
        
        /**
         * Gets the message's level.
         *
         * @return level
         */
        @Override
        public MessageLevel getLevel() {
            return level;
        }
        
        /**
         * Gets the message as a String.
         * 
         * @return the message
         */
        @Override
        public String getMessage() {
            return format;
        }
    }
    
}
