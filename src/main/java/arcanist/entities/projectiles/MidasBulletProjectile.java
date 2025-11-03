package arcanist.entities.projectiles;

import java.awt.Color;

import arcanist.content.ModBuffs;
import arcanist.content.ModColors;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

public class MidasBulletProjectile extends BulletProjectile {
    public MidasBulletProjectile() {
    }

    public MidasBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed * 2, distance, damage, knockback, owner);
        canBreakObjects = true;
    }

    public void init() {
        super.init();
    }

    @Override
    public float tickMovement(float delta) {
        if (Math.random() > 0.5F) {
            for(int i = 0; i < 2; ++i) {
                this.getLevel().entityManager.addParticle(x, y, this.spinningTypeSwitcher.next()).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 10.0F, (float)GameRandom.globalRandom.nextGaussian() * 10.0F).height(height).color(getWallHitColor()).size((options, lifeTime, timeAlive, lifePercent) -> {
                    options.size(8, 8);
                }).lifeTime(200);
            }
        }
        return super.tickMovement(delta);
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

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                mob.buffManager.addBuff(new ActiveBuff(ModBuffs.midas, mob, 5000, null), true, true);
            }
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
}
