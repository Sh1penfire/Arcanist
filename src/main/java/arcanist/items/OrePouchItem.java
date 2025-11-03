package arcanist.items;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.miscItem.PouchItem;

public class OrePouchItem extends PouchItem {
    static String category;
    public OrePouchItem() {
        this.rarity = Rarity.RARE;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "orepouch_tip"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedores", "amount", this.getStoredItemAmounts(item)));
        return tooltips;
    }
    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestItem(item.item);
    }

    public boolean isValidRequestItem(Item item) {
        category = ItemCategory.getItemsCategory(item).stringID;
        return this.isValidRequestType(item.type) && (category.equals("ore") || category.equals("dusts") || category.equals("minerals"));
    }

    public boolean isValidRequestType(Item.Type type) {
        return type == Type.MAT;
    }

    public int getInternalInventorySize() {
        return 10;
    }
}
