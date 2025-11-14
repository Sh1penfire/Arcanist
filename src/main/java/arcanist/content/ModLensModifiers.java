package arcanist.content;

import arcanist.util.Formatter;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.resourcePotions.ManaPotionItem;

import java.util.ArrayList;

import static arcanist.content.ModLensModifiers.ModifierEntry.*;

//The recognised list of stats modifiers, which the mod will pull from for things like tooltips.
public class ModLensModifiers {
    public static ArrayList<ModifierEntry> flat = new ArrayList<>(), multi = new ArrayList<>(), flags = new ArrayList<>(), nonFlags = new ArrayList<>();

    public static ModifierEntry
    damage,
    knockback,
    speed,
    homingPower,
    homingRange,
    explosionPower,
    objectDamageFract,
    lifestealFract,
    range,
    pierce,
    bounce;


    public static void load(){
        damage = pair("damage");
        knockback = pair("knockback");
        pierce = pair("pierce");
        bounce = pair("bounce");
        speed = pair("speed");
        range = pair("range");
        speed.unit = Formatter.Unit.tilesPerSecond;
        range.unit = Formatter.Unit.tiles;
        homingPower = pair("homingPower");
        homingRange = pair("homingRange");
        explosionPower = pair("explosionPower");
        objectDamageFract = pair("objectDamageFract");
        objectDamageFract.unit = Formatter.Unit.objectDamage;
        lifestealFract = pair("lifestealFract");
    }

    public static class ModifierEntry{
        public ModifierEntry(String id, ModifierType type){
            this.id = id;
            this.type = type;
        }
        public String id;
        public ModifierType type;
        //Can be null
        public Formatter.Unit unit;

        public static ModifierEntry flat(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.NUMBER);
            flat.add(entry);
            nonFlags.add(entry);
            return entry;
        }
        public static ModifierEntry multi(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.NUMBER);
            multi.add(entry);
            nonFlags.add(entry);
            return entry;
        }
        public static ModifierEntry flag(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.FLAG);
            multi.add(entry);
            return entry;
        }

        public static ModifierEntry pair(String id){
            ModifierEntry entry = new ModifierEntry(id, ModifierType.NUMBER);
            flat.add(entry);
            multi.add(entry);
            nonFlags.add(entry);
            return entry;
        }

    }

    public enum ModifierType{
        NUMBER,
        FLAG;
    }

}
