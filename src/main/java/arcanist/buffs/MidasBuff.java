package NecesseExpanded.Buffs.Trinkets;

import arcanist.content.ModColors;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;

public class MidasBuff extends Buff
{
    public void init(ActiveBuff activeBuff, BuffEventSubscriber eventSubscriber) {
        // Apply modifiers here
        activeBuff.setModifier(BuffModifiers.MAGIC_DAMAGE, 2f);
    }

    @Override
    public void onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event)
    {
        InventoryItem Treasure = new InventoryItem("coin", Math.max((int) event.damage.damage/10, 1));
        ItemPickupEntity TreasureEntity = Treasure.getPickupEntity(event.target.getLevel(), event.target.x, event.target.y);
        event.target.getLevel().entityManager.pickups.add(TreasureEntity);
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(ModColors.midas).givesLight(200.0F, 0.5F).height(16.0F);
        }
    }
}