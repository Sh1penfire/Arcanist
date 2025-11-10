package arcanist.items.manacharge;

import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class FrostyManachargeItem extends ManachargeBaseItem{
    public FrostyManachargeItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
    }

    @Override
    public void ability(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        super.ability(level, x, y, attackerMob, item, seed);
    }
}
