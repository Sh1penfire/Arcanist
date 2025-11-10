package arcanist.entities.projectiles.manacharge;

import arcanist.entities.projectiles.DrillBulletProjectile;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.BombProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.RicochetableProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.resourcePotions.HealthPotionItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BloodGrimoireProjectileToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

import java.util.List;

public class ModularProjectileBase extends Projectile implements RicochetableProjectile {

    public ModularProjectileBase(boolean isNetworkCapable, boolean hasHitbox) {
        super(isNetworkCapable, hasHitbox);
    }

    public ModularProjectileBase(boolean hasHitbox) {
        super(hasHitbox);
    }

    public ModularProjectileBase() {
        super();
    }

    //Im going to have to add properties for all this stuff and it's going to be painful and absolutely not soul crushing hahahahahahahahahahahahahahahaha
    //Yeah this is going to be one of those projectiles
    public float homingPower;
    public float explosionPower;
    public float objectDamageFract;
    public float lifestealFract;
    public int mineTier;

    //But heres the kicker
    //h a h a h a h a h
    public float AOE;

    public boolean homeToCursor;

    //Knockback and damage already exist so ill just leave those

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(homingPower);
        writer.putNextFloat(explosionPower);
        writer.putNextFloat(objectDamageFract);
        writer.putNextFloat(lifestealFract);
        writer.putNextFloat(AOE);
        writer.putNextInt(mineTier);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        homingPower = reader.getNextFloat();
        explosionPower = reader.getNextFloat();
        objectDamageFract = reader.getNextFloat();
        lifestealFract = reader.getNextFloat();
        AOE = reader.getNextFloat();
        mineTier = reader.getNextInt();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables orderableDrawables, OrderableDrawables orderableDrawables1, OrderableDrawables orderableDrawables2, Level level, TickManager tickManager, GameCamera gameCamera, PlayerMob playerMob) {

    }

    @Override
    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
    }

    public InventoryItem getOwnerToolSlot() {
        Mob owner = getOwner();
        if (owner != null && owner.isPlayer) {
            PlayerInventoryManager inv = ((PlayerMob) owner).getInv();
            return inv.streamInventorySlots(false, false, false, false).map(InventorySlot::getItem).filter((i) -> {
                return i != null && i.item instanceof ToolDamageItem;
            }).max((i, index) -> {
                return (int) ((ToolDamageItem) i.item).getToolTier(i, owner);
            }).get();
        } else {
            return null;
        }

    }

    public void applyDamage(Mob mob, float x, float y, float knockbackDirX, float knockbackDirY) {
        mob.isServerHit(this.getDamage(), knockbackDirX, knockbackDirY, (float)this.knockback, this);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        Level level = getLevel();

        if(getOwner().isPlayer && object != null) {
            PlayerMob player = (PlayerMob) getOwner();
            ServerClient client = player != null && player.isServerClient() ? player.getServerClient() : null;

            InventoryItem invItem = getOwnerToolSlot();
            ToolDamageItem item = null;
            if(invItem != null) item = (ToolDamageItem) invItem.item;
            float mineTier = -1f;
            if(item != null) {
                mineTier = item.getToolTier(invItem, getOwner());
            }

            Attacker attacker = item == null ? new DrillBulletProjectile.DrillbulletAttacker(getOwner()) : new ToolDamageItem.ToolDamageItemAttacker(getOwner(), invItem);
            float attackDamage = getDamage().getBuffedDamage(attacker);
            attackDamage *= getOwner().buffManager.getModifier(BuffModifiers.TOOL_DAMAGE);
            attackDamage += getOwner().buffManager.getModifier(BuffModifiers.TOOL_DAMAGE_FLAT);

            //Applies after tool damage flat so that people don't accidentally destroy their entire house with this
            attackDamage *= objectDamageFract;

            //Look if people shoot a splitting projectile inside their house with infinibounce its not my fault anymore
            object.level.entityManager.doObjectDamage(object.getLevelObject().layerID, object.tileX, object.tileY, (int) attackDamage, mineTier, attacker, client, true, object.tileX * 32, object.tileY * 32);
        }
    }
}
