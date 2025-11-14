package arcanist.util;

import arcanist.Arcanist;
import arcanist.content.GNDKeys;
import arcanist.content.ModLensModifiers;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.gfx.gameTooltips.ListGameTooltips;

//Has some automation for formatting tooltips and values
public class Formatter {

    public static class Unit{
        public Unit(float conversionRate, String suffix){
            this.conversionRate = conversionRate;
            this.suffix = suffix;
        }

        public float conversionRate;
        public String suffix;

        public static Unit
                tilesPerSecond = new Unit(32, "tiles/second"),
                tiles = new Unit(32, "tiles"),
                objectDamage = new Unit(0.01f, "%");
    }

    public static String formatUnit(float value, Unit unit){
        return value/unit.conversionRate + " " + unit.suffix;
    }

    public static ListGameTooltips formatModifiers(ListGameTooltips tooltip, GNDItemMap data, boolean all){
        //I think it's more efficient to just have the one check for all rather than shoving it into every single value check here
        if(all){
            ModLensModifiers.flat.forEach(modifier -> {
                float value = data.getFloat(modifier.id + GNDKeys.ADDITIVE_SUFFIX);
                System.out.println("Trying to get modifier " + modifier.id + GNDKeys.ADDITIVE_SUFFIX + " in item and result is: " + value);
                tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.sign(value) + (modifier.unit != null ? formatUnit(value, modifier.unit) : value)));
            });
            ModLensModifiers.multi.forEach(modifier -> {
            float value = (float) Math.ceil(data.getFloat(modifier.id + GNDKeys.MULTIPLICATIVE_SUFFIX) * 100);
            tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.sign(value) + (modifier.unit != null ? formatUnit(value, modifier.unit) : value)) + "%");
            });
            //Realistically if the flag is there it's enabled
            ModLensModifiers.flags.forEach(modifier -> {
                boolean value = data.getBoolean(modifier.id);
                tooltip.add(Localization.translate("acn_modifiers", modifier.id, "enables", value));
            });
        }

        else{
            ModLensModifiers.flat.forEach(modifier -> {
                float value = data.getFloat(modifier.id + GNDKeys.ADDITIVE_SUFFIX);
                if (value != 0) tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.sign(value) + (modifier.unit != null ? formatUnit(value, modifier.unit) : value)));
            });
            ModLensModifiers.multi.forEach(modifier -> {
                float value = (float) Math.ceil(data.getFloat(modifier.id + GNDKeys.MULTIPLICATIVE_SUFFIX) * 100);
                if (value != 0)
                    tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.sign(value) + (modifier.unit != null ? formatUnit(value, modifier.unit) : value)) + "%");
            });
            //Realistically if the flag is there it's enabled
            ModLensModifiers.flags.forEach(modifier -> {
                boolean value = data.getBoolean(modifier.id);
                if (value) tooltip.add(Localization.translate("acn_modifiers", modifier.id, "enables", value));
            });
        }
        return tooltip;
    }
    public static ListGameTooltips formatStats(ListGameTooltips tooltip, GNDItemMap data, boolean all){
        //I think it's more efficient to just have the one check for all rather than shoving it into every single value check here
        if(all){
            ModLensModifiers.nonFlags.forEach(modifier -> {
                float value = data.getFloat(modifier.id);
                tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", modifier.unit != null ? formatUnit(value, modifier.unit) : value));
            });
            ModLensModifiers.flags.forEach(modifier -> {
                boolean value = data.getBoolean(modifier.id);
                tooltip.add(Localization.translate("acn_modifiers", modifier.id, "enabled", value));
            });
        }

        else{
            //Realistically if the flag is there it's enabled
            ModLensModifiers.nonFlags.forEach(modifier -> {
                float value = data.getFloat(modifier.id);
                if (value != 0) tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", modifier.unit != null ? formatUnit(value, modifier.unit) : value));
            });
            ModLensModifiers.flags.forEach(modifier -> {
                boolean value = data.getBoolean(modifier.id);
                if (value) tooltip.add(Localization.translate("acn_modifiers", modifier.id, "enabled", value));
            });
        }
        return tooltip;
    }

    //No negative because scuffed
    public static String sign(float value){
        return (value >= 0 ? "+" : "");
    }
    public static String percent(float value){
        return (int) (value * 100) + "%";
    }
    public static String enabled(boolean value){
        return Localization.translate(Arcanist.prefixID("keywords"), value ? "enabled" : "disabled");
    }
}
