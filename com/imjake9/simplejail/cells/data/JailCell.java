package com.imjake9.simplejail.cells.data;

import com.imjake9.simplejail.utils.SerializableLocation;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Cell")
public class JailCell implements ConfigurationSerializable {
    
    public SerializableLocation jailLoc;
    public SerializableLocation unjailLoc;
    
    public int jailLimit;
    
    public static JailCell deserialize(Map<String, Object> data) {
        JailCell cell = new JailCell();
        cell.jailLoc = (SerializableLocation) data.get("jailLoc");
        cell.unjailLoc = (SerializableLocation) data.get("unjailLoc");
        cell.jailLimit = (Integer) data.get("jailLimit");
        return cell;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> cell = new HashMap<String, Object>();
        cell.put("jailLoc", jailLoc);
        cell.put("unjailLoc", unjailLoc);
        cell.put("jailLimit", jailLimit);
        return cell;
    }
    
}
