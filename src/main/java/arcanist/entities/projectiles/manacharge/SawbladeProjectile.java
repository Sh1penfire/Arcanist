package arcanist.entities.projectiles.manacharge;

import arcanist.content.ModTextures;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.SapphireRevolverProjectile;
import necesse.entity.projectile.boomerangProjectile.FrostBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.SpinningProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class SawbladeProjectile extends ModularProjectileBase{
    public int spriteX;
    public TextureDrawOptionsEnd options;

    public GameTexture tooth;

    public SawbladeProjectile(boolean isNetworkCapable, boolean hasHitbox){
        super(isNetworkCapable, hasHitbox);
    }
    public SawbladeProjectile(){
        super();
    }

    @Override
    public void init() {
        super.init();
        width = height = 42/2;
    }

    public Color getParticleColor() {
        return new Color(63, 105, 151);
    }

    protected void modifySpinningParticle(ParticleOption particle) {
        particle.lifeTime(1000);
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(63, 105, 151), 12.0F, 200, this.getHeight());
    }

    public float getAngle() {
        return this.getSpinningSpeed() * (float)(this.getWorldEntity().getTime() - this.spawnTime);
    }

    protected float getSpinningSpeed() {
        return 1.0F;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getHeight() / 2);
        }
    }


    @Override
    public void clientTick() {
        super.clientTick();
    }

    //Copying from sapphite revolver

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            this.attachTextureToTarget(mob, x, y);
        } else if (this.bounced == this.getTotalBouncing()) {
            this.attachTextureToTarget(null, x, y);
        }
    }

    private void attachTextureToTarget(Mob mob, float x, float y) {
        this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, (float) GameRandom.globalRandom.getIntBetween(10, 20), 5000L) {
            float randRot = GameRandom.getFloatBetween(GameRandom.globalRandom, 0, 360);
            public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                GameLight light = level.getLightLevel(this);
                int drawX = camera.getDrawX(x) - 2;
                int drawY = camera.getDrawY(y - height) - 2;
                float alpha = 1.0F;
                long lifeCycleTime = this.getLifeCycleTime();
                int fadeTime = 1000;
                if (lifeCycleTime >= this.lifeTime - (long)fadeTime) {
                    alpha = Math.abs((float)(lifeCycleTime - (this.lifeTime - (long)fadeTime)) / (float)fadeTime - 1.0F);
                }

                final TextureDrawOptions options = ModTextures.sawTooth.initDraw().sprite(spriteX, 0, 18, 32).light(light).rotate(randRot, 8, 0).pos(drawX, drawY).alpha(alpha);
                EntityDrawable drawable = new EntityDrawable(this) {
                    public void draw(TickManager tickManager) {
                        options.draw();
                    }
                };
                if (target != null) {
                    topList.add(drawable);
                } else {
                    list.add(drawable);
                }

            }
        }, Particle.GType.IMPORTANT_COSMETIC);
    }
}
