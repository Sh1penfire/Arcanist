package arcanist.items.manacharge;

import arcanist.content.GNDKeys;
import arcanist.content.ModLensModifiers;
import arcanist.util.Formatter;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class LensModifierItem extends Item {

    //The default values
    public ArrayList<NumberEntry> additive = new ArrayList<>(), multiplicative = new ArrayList<>();
    public ArrayList<FlagEntry> flags = new ArrayList<>();

    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        InventoryItem inventoryItem = super.getDefaultItem(player, amount);
        GNDItemMap data = inventoryItem.getGndData();

        //Let the mod know that the default values have already been initialised
        data.setBoolean(GNDKeys.MODIFIER_ITEM, true);

        additive.forEach((entry) -> {
            data.setFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, entry.value);
        });
        multiplicative.forEach((entry) -> {
            data.setFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, entry.value);
        });
        flags.forEach((entry) -> {
            data.setBoolean(entry.id, entry.value);
        });

        inventoryItem.setGndData(data);
        return inventoryItem;
    }

    //TODO: replace with hashing function?
    @Override
    public boolean isSameItem(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return this == them.item && sameStats(me.getGndData(), them.getGndData());
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltip = super.getTooltips(item, perspective, blackboard);
        GNDItemMap data = item.getGndData();
        return Formatter.formatModifiers(tooltip, data, false);
    };

    public LensModifierItem(int stacksize) {
        super(stacksize);
    }
    public LensModifierItem() {
        super(10);
    }

    //I feel like this code has the potential to be absolutely terrible if ran in quick succession
    public static boolean sameStats(GNDItemMap scribble, GNDItemMap tickle){
        //bop
        for (ModLensModifiers.ModifierEntry entry : ModLensModifiers.additiveModifiers) {
            if(scribble.getFloat(entry.id) != tickle.getFloat(entry.id)) return false;
        }
        for (ModLensModifiers.ModifierEntry entry : ModLensModifiers.multiplicativeModifiers) {
            if(scribble.getFloat(entry.id) != tickle.getFloat(entry.id)) return false;
        }
        for (ModLensModifiers.ModifierEntry entry : ModLensModifiers.flagModifiers) {
            if(scribble.getBoolean(entry.id) != tickle.getBoolean(entry.id)) return false;
        }
        return true;
    }

    //I know theres libraries out there to solve mapping strings to values, maybe I should have used those instead of what im about to do
    public static class NumberEntry{
        public NumberEntry(String id, float value){
            this.id = id;
            this.value = value;
        }

        public String id;
        public float value;
    }

    public static class FlagEntry{
        public FlagEntry(String id, boolean value){
            this.id = id;
            this.value = value;
        }

        public String id;
        public boolean value;
    }

    public void additiveModifier(String id, float value){
        additive.add(new NumberEntry(id, value));
    }
    public void multiplicativeModifier(String id, float value){
        multiplicative.add(new NumberEntry(id, value));
    }
    public void flagModifier(String id, boolean value){
        flags.add(new FlagEntry(id, value));
    }
}
