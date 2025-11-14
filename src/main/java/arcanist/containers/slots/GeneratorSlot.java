package arcanist.containers.slots;

import arcanist.items.manacharge.ProjectileGeneratorItem;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;

public class GeneratorSlot extends ContainerSlot {
    public GeneratorSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    public String getItemInvalidError(InventoryItem item) {
        return item.item instanceof ProjectileGeneratorItem ? null : Localization.translate("ui", "enchantingscrollwrongtype", "item", ItemRegistry.getLocalization(item.item.getID()), "enchantment", "NO");
    }
}
