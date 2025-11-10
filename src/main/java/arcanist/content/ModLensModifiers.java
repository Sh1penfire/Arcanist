package arcanist.content;

import java.util.ArrayList;

import static arcanist.content.ModLensModifiers.ModifierEntry.*;

//The recognised list of modifiers, which the mod will pull from for things like tooltips.
public class ModLensModifiers {
    public static ArrayList<ModifierEntry> additiveModifiers = new ArrayList<>(), multiplicativeModifiers = new ArrayList<>(), flagModifiers = new ArrayList<>();

    public static ModifierEntry
    damage,
    knockback,
    speed,
    homingPower,
    homingRange,
    objectDamageFract,
    range,
    pierce,
    bounce;


    public static void load(){
        damage = pair("damage");
        knockback = pair("knockback");
        speed = pair("speed");
        homingPower = pair("homingPower");
        homingRange = pair("homingRange");
        objectDamageFract = pair("objectDamageFract");
        range = pair("range");
        pierce = pair("pierce");
        bounce = pair("bounce");
    }

    public static class ModifierEntry{
        public ModifierEntry(String id, ModifierType type){
            this.id = id;
            this.type = type;
        }
        public String id;
        public ModifierType type;

        public static ModifierEntry additive(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.NUMBER);
            additiveModifiers.add(entry);
            return entry;
        }
        public static ModifierEntry multiplicative(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.NUMBER);
            multiplicativeModifiers.add(entry);
            return entry;
        }
        public static ModifierEntry flag(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.FLAG);
            multiplicativeModifiers.add(entry);
            return entry;
        }

        public static ModifierEntry pair(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.NUMBER);
            additiveModifiers.add(entry);
            multiplicativeModifiers.add(entry);
            return entry;
        }
    }

    public enum ModifierType{
        NUMBER,
        FLAG;
    }

}
