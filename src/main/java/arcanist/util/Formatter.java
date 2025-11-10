package arcanist.util;

import arcanist.Arcanist;
import arcanist.content.GNDKeys;
import arcanist.content.ModLensModifiers;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.gfx.gameTooltips.ListGameTooltips;

//Has some automation for formatting tooltips and values
public class Formatter {

    public static ListGameTooltips formatModifiers(ListGameTooltips tooltip, GNDItemMap data, boolean all){
        if (!data.getBoolean(GNDKeys.MODIFIER_ITEM)) return tooltip;
        else {
            //I think it's more efficient to just have the one check for all rather than shoving it into every single value check here
            if(all){
                ModLensModifiers.additiveModifiers.forEach(modifier -> {
                    float value = data.getFloat(modifier.id + GNDKeys.ADDITIVE_SUFFIX);
                    tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.sign(value)));
                });
                ModLensModifiers.multiplicativeModifiers.forEach(modifier -> {
                    float value = data.getFloat(modifier.id + GNDKeys.MULTIPLICATIVE_SUFFIX);
                    tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.percentSign(value)));
                });
                //Realistically if the flag is there it's enabled
                ModLensModifiers.flagModifiers.forEach(modifier -> {
                    boolean value = data.getBoolean(modifier.id);
                    tooltip.add(Localization.translate("acn_modifiers", modifier.id, "enables", value));
                });
            }

            else{
                ModLensModifiers.additiveModifiers.forEach(modifier -> {
                    float value = data.getFloat(modifier.id + GNDKeys.ADDITIVE_SUFFIX);
                    if (value != 0) tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.sign(value)));
                });
                ModLensModifiers.multiplicativeModifiers.forEach(modifier -> {
                    float value = data.getFloat(modifier.id + GNDKeys.MULTIPLICATIVE_SUFFIX);
                    if (value != 0)
                        tooltip.add(Localization.translate("acn_modifiers", modifier.id, "value", Formatter.percentSign(value)));
                });
                //Realistically if the flag is there it's enabled
                ModLensModifiers.flagModifiers.forEach(modifier -> {
                    boolean value = data.getBoolean(modifier.id);
                    if (value) tooltip.add(Localization.translate("acn_modifiers", modifier.id, "enables", value));
                });
            }

            return tooltip;
        }
    }
    public static String sign(float value){
        return (value >= 0 ? "+" : "-") + (int) (value * 100)/100;
    }
    public static String percentSign(float value){
        return (value >= 0 ? "+" : "-") + (int) (value * 100) + "%";
    }
    public static String percent(float value){
        return (int) (value * 100) + "%";
    }
    public static String enabled(boolean value){
        return Localization.translate(Arcanist.prefixID("keywords"), value ? "enabled" : "disabled");
    }
}
