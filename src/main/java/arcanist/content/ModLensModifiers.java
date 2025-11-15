package arcanist.content;

import arcanist.util.Formatter;
import necesse.engine.network.gameNetworkData.GNDItemMap;

import java.util.ArrayList;

import static arcanist.content.ModLensModifiers.ModifierEntry.*;

//The recognised list of stats modifiers, which the mod will pull from for things like tooltips.
//Some get used for projectiles themselves, others get used for the manacharge
public class ModLensModifiers {
    public static ArrayList<ModifierEntry> flat = new ArrayList<>(), multi = new ArrayList<>(), flags = new ArrayList<>(), nonFlags = new ArrayList<>();

    public static ModifierEntry

    //projectile stats
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
    bounce,

    //Manacharge stats
    shots,
    bursts,
    inaccuracy;


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
        shots = pair("shots");
        bursts = flat("bursts");
        inaccuracy = pair("inaccuracy");
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

        //Summ up the effects
        public static void sumModifiers(GNDItemMap origin, GNDItemMap modifier){
            ModLensModifiers.flat.forEach((entry) -> {
                float value = origin.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, 0) + modifier.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, 0);
                if(value != 0) origin.setFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, value);
            });
            ModLensModifiers.multi.forEach((entry) -> {
                float value = origin.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, 0) + modifier.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, 0);
                if(value != 0) origin.setFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, value);
            });
            ModLensModifiers.flags.forEach((entry) -> {
                boolean value = modifier.getBoolean(entry.id);
                if(value) origin.setBoolean(entry.id, true);
            });
        }

        /**
         * Sums up additive modifiers, multiplies multiplicatives
         * Assumes multiplicative is the
         */
        public static void applyModifiers(GNDItemMap origin, GNDItemMap modifier){
            ModLensModifiers.flat.forEach((entry) -> {
                //Unchanged from sumModifiers
                float value = origin.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, 0) + modifier.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, 0);
                if(value != 0) origin.setFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, value);
            });
            ModLensModifiers.multi.forEach((entry) -> {
                //Say base stat is 2x and the modifier on the gun is +50% (or 0.5)
                //We'd expect it to become 3x (2 * 1.5) instead of 0.5 (1 * 0.5)
                float value = Math.max(0, (1 + Math.max(0, origin.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, 0))) * (1 + Math.max(modifier.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, 0), 0)) - 1);
                if(value != 0) origin.setFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, value);
            });
            ModLensModifiers.flags.forEach((entry) -> {
                boolean value = modifier.getBoolean(entry.id);
                if(value) origin.setBoolean(entry.id, true);
            });
        }
    }

    public enum ModifierType{
        NUMBER,
        FLAG;
    }

}
