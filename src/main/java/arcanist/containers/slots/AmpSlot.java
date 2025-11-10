package arcanist.containers.slots;

import arcanist.items.manacharge.AmpItem;
import arcanist.items.manacharge.LensItem;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;

public class AmpSlot extends ContainerSlot {
    public AmpSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    public String getItemInvalidError(InventoryItem item) {
        return item.item instanceof AmpItem ? null : Localization.translate("ui", "enchantingscrollwrongtype", "item", ItemRegistry.getLocalization(item.item.getID()), "enchantment", "NO");
    }
}
