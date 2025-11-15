package arcanist.items.manacharge;

import arcanist.content.ModLensModifiers;
import arcanist.entities.projectiles.manacharge.ModularProjectileBase;
import arcanist.items.attackhandler.BurstManachargeHandler;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class RefinedManachargeItem extends ManachargeBaseItem{
    public RefinedManachargeItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        chargeCost = 1;
    }

    @Override
    public void ability(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        super.ability(level, x, y, attackerMob, item, seed);

        ModularProjectileBase projectile = getAbilityProjectile(item.getGndData(), "acn_sawblade", level, x, y, attackerMob);
        projectile.getUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, 20);
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {

        attackerMob.startAttackHandler(new BurstManachargeHandler(attackerMob, slot, item, this, seed, x, y, Math.max(1, 1 + (int) item.getGndData().getFloat(ModLensModifiers.bursts.id))));
        return item;
    }
}
