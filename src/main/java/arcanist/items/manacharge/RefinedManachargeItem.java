package arcanist.items.manacharge;

import arcanist.content.ModBuffs;
import arcanist.entities.projectiles.manacharge.BrickBreakerProjectile;
import arcanist.entities.projectiles.manacharge.ModularProjectileBase;
import arcanist.items.attackhandler.BurstManachargeHandler;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.attackHandler.SixShooterAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SixShooterProjectileToolItem;
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

        InventoryItem generator = generatorInventoryItem(item);
        ProjectileGeneratorItem generatorItem = (ProjectileGeneratorItem) generator.item;
        Inventory generatorInv = generatorItem.getInternalInventory(generator);

        ModularProjectileBase projectile = (ModularProjectileBase) ProjectileRegistry.getProjectile("acn_sawblade");

        //Pass this off to each lens with the amplification values
        ProjStats stats = abilityStats(item, generatorInv, ampItem(item));

        modifyProjectile(level, item, attackerMob, x, y, projectile, stats, seed);


        attackerMob.addAndSendAttackerProjectile(projectile, 20);
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {

        attackerMob.startAttackHandler(new BurstManachargeHandler(attackerMob, slot, item, this, seed, x, y));
        return item;
    }

    @Override
    public ProjStats abilityStats(InventoryItem item, Inventory generatorInv, AmpItem amplifier) {
        ProjStats stats = super.abilityStats(item, generatorInv, amplifier);
        stats.speed /= 1.5f;
        stats.bounce += 8;
        stats.pierce += 8;
        stats.range *= 8;
        stats.damage *= 4;
        stats.knockback *= 16;
        return stats;
    }
}
