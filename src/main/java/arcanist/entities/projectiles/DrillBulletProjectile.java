package arcanist.entities.projectiles;

import arcanist.content.ModColors;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.level.gameObject.container.ShippingChestObject;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;

public class DrillBulletProjectile extends BulletProjectile {
    public DrillBulletProjectile() {
        piercing = 2;
        canBreakObjects = true;
    }

    public DrillBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed * 2, distance, damage, knockback, owner);
        piercing = 2;
        canBreakObjects = true;
    }

    public void init() {
        super.init();
    }

    protected void spawnWallHitParticles(float x, float y) {
        Color c = this.getWallHitColor();
        if (c != null) {
            float height = this.getHeight();

            for(int i = 0; i < 5; ++i) {
                this.getLevel().entityManager.addParticle(x, y, this.spinningTypeSwitcher.next()).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 10.0F, (float)GameRandom.globalRandom.nextGaussian() * 10.0F).height(height).color(c).size((options, lifeTime, timeAlive, lifePercent) -> {
                    options.size(8, 8);
                }).lifeTime(200);
            }
        }
    }

    public InventoryItem getOwnerToolSlot() {
        Mob owner = this.getOwner();
        if (owner != null && owner.isPlayer) {
            PlayerInventoryManager inv = ((PlayerMob)owner).getInv();
            InventoryItem item = inv.streamInventorySlots(false, false, false, false).map(InventorySlot::getItem).filter((i) -> {
                return i != null && i.item instanceof ToolDamageItem;
            }).max((i, index) -> {
                return (int) ((ToolDamageItem) i.item).getToolTier(i, owner);
            }).get();
            if(item != null) return item;
            return null;
        } else {
            return null;
        }
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
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

            Attacker attacker = item == null ? new DrillbulletAttacker(getOwner()) : new ToolDamageItem.ToolDamageItemAttacker(getOwner(), invItem);
            float attackDamage = getDamage().getBuffedDamage(attacker);
            attackDamage *= getOwner().buffManager.getModifier(BuffModifiers.TOOL_DAMAGE);
            attackDamage += getOwner().buffManager.getModifier(BuffModifiers.TOOL_DAMAGE_FLAT);

            //TODO: Add a config setting to reduce damage dealt to player placed objects with drill bullets
            //Don't want people to accidentally destroy their base with this...
            if(object.getLevelObject().isPlayerPlaced) attackDamage = Math.max(Math.min(object.getLevelObject().object.objectHealth - 1, attackDamage), 1);
            object.level.entityManager.doObjectDamage(object.getLevelObject().layerID, object.tileX, object.tileY, (int) attackDamage, mineTier, attacker, client, true, object.tileX * 32, object.tileY * 32);
        }
    }

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), ModColors.midas, 22, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return ModColors.midas;
    }


    public static class DrillbulletAttacker implements Attacker {
        private final Mob owner;

        public DrillbulletAttacker(Mob owner) {
            this.owner = owner;
        }

        public GameMessage getAttackerName() {
            return this.owner.getAttackerName();
        }

        public DeathMessageTable getDeathMessages() {
            return this.owner.getDeathMessages();
        }

        public Mob getFirstAttackOwner() {
            return this.owner;
        }
    }
}
