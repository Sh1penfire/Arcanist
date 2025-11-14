package arcanist.content;

import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;

import java.util.ArrayList;

public class UpdateCategories {
    public static void update(){

        ArrayList<Integer> logs = GlobalIngredientRegistry.getGlobalIngredient("anylog").getObtainableRegisteredItemIDs();
        logs.forEach(logId -> {
            if(logId == ModItems.charloag.getID()) return;
            Item log = ItemRegistry.getItem(logId);
            log.addGlobalIngredient("anycharrable");
        });
    }
}
