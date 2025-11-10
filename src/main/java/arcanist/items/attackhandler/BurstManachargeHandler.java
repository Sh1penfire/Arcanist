package arcanist.items.attackhandler;

import arcanist.items.manacharge.ManachargeBaseItem;
import arcanist.items.manacharge.RefinedManachargeItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketFireSixShooter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SixShooterProjectileToolItem;

import java.awt.geom.Point2D;

public class BurstManachargeHandler extends MouseAngleAttackHandler {
    public InventoryItem item;
    public RefinedManachargeItem toolItem;
    public int attackSeed;
    private int shotsRemaining = 3, maxShots = 3;
    private int shots;
    public float timeBuffer;
    private final GameRandom random = new GameRandom();

    public float delay = 1.5f * 1000;

    public BurstManachargeHandler(ItemAttackerMob itemAttacker, ItemAttackSlot slot, InventoryItem item, RefinedManachargeItem toolItem, int seed, int startTargetX, int startTargetY) {
        super(itemAttacker, slot, 50, 1000.0F, startTargetX, startTargetY);
        this.attackSeed = seed;
        this.timeBuffer = 1500;
        this.item = item;
        this.toolItem = toolItem;
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null && !ChaserAINode.hasLineOfSightToTarget(this.attackerMob, this.lastItemAttackerTarget, 5.0F)) {
            this.attackerMob.endAttackHandler(true);
        } else {
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0F);
            int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0F);
            if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
                int seed = Item.getRandomAttackSeed(this.random.seeded((long)GameRandom.prime(this.attackSeed * this.shots)));
                GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, seed, 0);
                this.timeBuffer += this.updateInterval;

                while(true) {
                    float speedModifier = this.getSpeedModifier();

                    //Not ready to shoot yet
                    if (this.timeBuffer < toolItem.getAttackCooldownTime(item, attackerMob) * speedModifier) {
                        break;
                    }

                    seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));

                    shots++;
                    shotsRemaining--;

                    this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), this.item, this.slot, 0, seed, attackMap);
                    if (this.attackerMob.isClient()) {
                        playFireSound(this.attackerMob);
                    } else if (this.attackerMob.isServer()) {
                        this.attackerMob.sendAttackerPacket(this.attackerMob, new PacketFireSixShooter(this.attackerMob));
                    }

                    if (this.shotsRemaining <= 0) {
                        if (this.attackerMob.isClient()) {
                            this.playReloadEffects(this.attackerMob, maxShots);
                        }

                        this.shotsRemaining = maxShots;
                        this.timeBuffer = -getCanceledReloadTime(toolItem.getAttackCooldownTime(item, attackerMob));
                        break;
                    }

                    this.timeBuffer = 0;
                }
            }

        }
    }

    public static void playFireSound(Mob target) {
        SoundManager.playSound(GameResources.explosionHeavy, SoundEffect.effect(target).pitch(2.5F).volume(0.5F));
        SoundManager.playSound(GameResources.crystalHit3, SoundEffect.effect(target).pitch(2.0F).volume(0.5F));
    }

    private void playReloadEffects(Mob target, int particleCount) {
        if (particleCount == 1) {
            SoundManager.playSound(GameResources.crystalHit2, SoundEffect.effect(target).pitch(1.5F).volume(0.5F));
        } else {
            SoundManager.playSound(GameResources.coins, SoundEffect.effect(target).pitch(0.75F).volume(0.5F));
        }

        for(int i = 0; i < particleCount; ++i) {
            float xMove = GameRandom.globalRandom.getFloatBetween(-0.05F, 0.05F);
            float yStart = this.attackerMob.y + GameRandom.globalRandom.getFloatBetween(-7.0F, 7.0F);
            this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + GameRandom.globalRandom.getFloatBetween(-5.0F, 5.0F), this.attackerMob.y, Particle.GType.COSMETIC).sprite(GameResources.bulletCasingParticles.sprite(0, 0, 6, 8)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                pos.x += xMove;
                pos.y = (float)((double)yStart - 5.0 * Math.abs(1.2999999523162842 * Math.cos((double)(10.0F * lifePercent))));
            }).rotates(360.0F, 720.0F).size((options, lifeTime, timeAlive, lifePercent) -> {
                options.size(6, 8);
            }).fadesAlpha(0.0F, 0.2F).lifeTimeBetween(1000, 2000);
        }

    }

    public float getSpeedModifier() {
        return 1.0f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
    }

    public float getCanceledReloadTime(float base) {
        if (this.shots == 0) {
            return 0;
        } else {
            return this.shots % maxShots != 0 ? base * (maxShots - this.shotsRemaining) : base * maxShots;
        }
    }

    public void onEndAttack(boolean bySelf) {
        if (this.shotsRemaining < maxShots && this.attackerMob.isClient()) {
            this.playReloadEffects(this.attackerMob, maxShots - this.shotsRemaining);
        }

        if (this.attackerMob.isPlayer) {
            this.attackerMob.startItemCooldown(this.toolItem, (int)(this.getCanceledReloadTime(toolItem.getAttackCooldownTime(item, attackerMob)) * this.getSpeedModifier()));
        }

        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}
