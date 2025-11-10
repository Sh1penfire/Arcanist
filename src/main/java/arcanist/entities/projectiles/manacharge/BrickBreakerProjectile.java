package arcanist.entities.projectiles.manacharge;

import arcanist.packets.ModularProjectileTargetUpdatePacket;
import necesse.engine.Settings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketProjectileTargetUpdate;
import necesse.engine.util.*;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.function.Predicate;

public class BrickBreakerProjectile extends ModularProjectileBase{
    //Copying this logic into modular projectile base soon
    public Entity target;
    public Point targetPos;
    public float turnSpeed = 0.5F;
    public boolean clearTargetPosWhenAligned;
    public boolean clearTargetWhenAligned;
    public boolean stopsAtTarget;
    private float originalSpeed;
    private boolean isStoppedAtTarget;
    public float angleLeftToTurn = -1.0F;
    private int updateTicker;

    public BrickBreakerProjectile(boolean isNetworkCapable, boolean hasHitbox) {
        super(isNetworkCapable, hasHitbox);
    }

    public BrickBreakerProjectile(boolean hasHitbox) {
        super(hasHitbox);
    }

    public BrickBreakerProjectile() {
    }

    public void addTargetData(PacketWriter writer) {
        if (this.target != null) {
            writer.putNextMaxValue(1, 2);
            if (this.target == null) {
                writer.putNextInt(-1);
            } else {
                writer.putNextInt(this.target.getUniqueID());
            }
        } else if (this.targetPos != null) {
            writer.putNextMaxValue(2, 2);
            writer.putNextInt(this.targetPos.x);
            writer.putNextInt(this.targetPos.y);
        } else {
            writer.putNextMaxValue(0, 2);
        }

    }

    public void applyTargetData(PacketReader reader) {
        switch (reader.getNextMaxValue(2)) {
            case 0:
                this.target = null;
                this.targetPos = null;
                break;
            case 1:
                this.targetPos = null;
                int targetID = reader.getNextInt();
                if (targetID == -1) {
                    this.target = null;
                } else {
                    this.target = GameUtils.getLevelMob(targetID, this.getLevel());
                }
                break;
            case 2:
                this.target = null;
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                this.targetPos = new Point(x, y);
        }

    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        this.addTargetData(writer);
        writer.putNextBoolean(this.angleLeftToTurn >= 0.0F);
        if (this.angleLeftToTurn >= 0.0F) {
            writer.putNextFloat(this.angleLeftToTurn);
        }

    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.applyTargetData(reader);
        if (reader.getNextBoolean()) {
            this.angleLeftToTurn = reader.getNextFloat();
        }

    }

    public void init() {
        super.init();
        this.updateTicker = 0;
        this.stopsAtTarget = false;
        this.isStoppedAtTarget = false;
        this.originalSpeed = 0.0F;
    }

    public float tickMovement(float delta) {
        if (this.removed()) {
            return 0.0F;
        } else {
            float toTarget = this.hasTarget() ? (float)(new Point(this.getTargetX(), this.getTargetY())).distance((double)this.x, (double)this.y) : 1.0F;
            if (this.isStoppedAtTarget && toTarget >= 1.0F) {
                this.speed = this.originalSpeed;
                this.isStoppedAtTarget = false;
            }

            if (this.isStoppedAtTarget) {
                float moveX = this.getMoveDist(this.dx * this.originalSpeed, delta);
                float moveY = this.getMoveDist(this.dy * this.originalSpeed, delta);
                double totalDist = Math.sqrt((double)(moveX * moveX + moveY * moveY));
                if (Double.isNaN(totalDist)) {
                    totalDist = 0.0;
                }

                this.traveledDistance = (float)((double)this.traveledDistance + totalDist);
                this.checkRemoved();
                float width = this.getWidth();
                int size = (int)(width <= 0.0F ? 2.0 : Math.ceil((double)width));
                this.checkCollision(new Rectangle(this.getX() - size / 2, this.getY() - size / 2, size, size));
                return (float)totalDist;
            } else {
                return super.tickMovement(delta);
            }
        }
    }

    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        if (!this.isStoppedAtTarget) {
            this.updateTarget();
            if (this.hasTarget()) {
                float delta = (float)movedDist;
                int tx = this.getTargetX();
                int ty = this.getTargetY();
                float angle = this.getTurnSpeed(tx, ty, delta);
                if (this.angleLeftToTurn >= 0.0F) {
                    angle = Math.min(this.angleLeftToTurn, angle);
                    this.angleLeftToTurn -= angle;
                }

                if (this.turnToward((float)tx, (float)ty, angle)) {
                    if (this.clearTargetPosWhenAligned) {
                        this.targetPos = null;
                    }

                    if (this.clearTargetWhenAligned) {
                        this.target = null;
                    }

                    if (this.stopsAtTarget) {
                        float toTarget = this.hasTarget() ? (float)(new Point(this.getTargetX(), this.getTargetY())).distance((double)this.x, (double)this.y) : 1.0F;
                        if ((double)toTarget <= movedDist) {
                            this.isStoppedAtTarget = true;
                            this.originalSpeed = this.speed;
                        }
                    }
                }
            }
        }

        if (this.isStoppedAtTarget) {
            this.speed = 0.0F;
            this.x = (float)this.getTargetX();
            this.y = (float)this.getTargetY();
        }

    }

    public float getOriginalSpeed() {
        return this.originalSpeed;
    }

    public final float getTurnSpeed(float delta) {
        return this.turnSpeed * delta;
    }

    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return this.getTurnSpeed(delta);
    }

    protected float dynamicTurnSpeedMod(int targetX, int targetY, float startDistance) {
        float distance = (float)(new Point(targetX, targetY)).distance((double)this.getX(), (double)this.getY());
        return distance < startDistance && distance > 5.0F ? Math.abs(distance - startDistance) / startDistance * 4.0F + 1.0F : 1.0F;
    }

    protected float dynamicTurnSpeedMod(int targetX, int targetY) {
        return this.dynamicTurnSpeedMod(targetX, targetY, this.speed * 1.5F);
    }

    protected float invDynamicTurnSpeedMod(int targetX, int targetY, float maxDistance) {
        float distance = (float)(new Point(targetX, targetY)).distance((double)this.getX(), (double)this.getY());
        if (distance > maxDistance && distance > 5.0F) {
            float deltaAngle = Math.abs(this.getAngleDifference(this.getAngleToTarget((float)targetX, (float)targetY)));
            float mod = Math.abs(distance - maxDistance) / maxDistance;
            if (deltaAngle < 90.0F) {
                mod *= 3.0F;
            }

            return 1.0F + mod;
        } else {
            return 1.0F;
        }
    }

    protected float invDynamicTurnSpeedMod(int targetX, int targetY) {
        return this.invDynamicTurnSpeedMod(targetX, targetY, (float)this.getTurnRadius());
    }

    public static int getTurnRadius(float velocity, float turnSpeed) {
        float distancePerDegree = velocity / turnSpeed;
        float circumference = distancePerDegree * 360.0F;
        return (int)((double)circumference / 6.283185307179586);
    }

    public int getTurnRadius() {
        return getTurnRadius(this.getMoveDist(this.speed, 1.0F), this.getTurnSpeed(1.0F));
    }

    public void serverTick() {
        super.serverTick();
        if (this.isStoppedAtTarget) {
            this.updateTarget();
        }

        ++this.updateTicker;
        if (this.updateTicker >= 20) {
            this.sendServerTargetUpdate(false);
            this.updateTicker = 0;
        }

    }

    public void clientTick() {
        super.clientTick();
        if (this.isStoppedAtTarget) {
            this.updateTarget();
        }

    }

    public void sendClientTargetUpdate() {
        this.getLevel().getClient().network.sendPacket(new ModularProjectileTargetUpdatePacket(this));
    }

    public void sendServerTargetUpdate(boolean forced) {
        if (Settings.strictServerAuthority || forced) {
            this.getLevel().getServer().network.sendToClientsWithEntity(new ModularProjectileTargetUpdatePacket(this), this);
        }

    }

    public boolean hasTarget() {
        return this.target != null && !this.target.removed() || this.targetPos != null;
    }

    public int getTargetX() {
        if (this.target != null && !this.target.removed()) {
            return this.target.getX();
        } else {
            return this.targetPos != null ? this.targetPos.x : this.getX();
        }
    }

    public int getTargetY() {
        if (this.target != null && !this.target.removed()) {
            return this.target.getY();
        } else {
            return this.targetPos != null ? this.targetPos.y : this.getY();
        }
    }

    public void updateTarget() {
    }

    public void drawDebug(GameCamera camera) {
        FontOptions fontOptions = new FontOptions(16);
        int drawX = camera.getDrawX(this.x) - 16;
        int drawY = camera.getDrawY(this.y - this.getHeight());
        float angle = this.getAngleToTarget((float)camera.getMouseLevelPosX(), (float)camera.getMouseLevelPosY());
        float dif = this.getAngleDifference(angle);
        FontManager.bit.drawString((float)drawX, (float)(drawY - 16), "" + dif, fontOptions);
        FontManager.bit.drawString((float)drawX, (float)(drawY - 32), "" + angle, fontOptions);
        FontManager.bit.drawString((float)drawX, (float)(drawY - 48), "" + this.getAngle(), fontOptions);
        int targetX = (int)(this.x + this.dx * 160.0F);
        int targetY = (int)(this.y + this.dy * 160.0F);
        Renderer.initQuadDraw(6, 6).color(0.0F, 1.0F, 0.0F).draw(camera.getDrawX(targetX) - 3, camera.getDrawY(targetY) - 3);
        if (this.target != null) {
            Renderer.initQuadDraw(30, 30).color(1.0F, 0.0F, 0.0F, 0.5F).draw(camera.getDrawX(this.target.getX()) - 15, camera.getDrawY(this.target.getY()) - 15);
        }

        FontManager.bit.drawString((float)drawX, (float)(drawY - 64), this.dx + ", " + this.dy, fontOptions);
        FontManager.bit.drawString((float)drawX, (float)(drawY - 80), "" + this.getTeam(), fontOptions);
        FontManager.bit.drawString((float)drawX, (float)(drawY - 96), "" + this.getOwnerID(), fontOptions);
        FontManager.bit.drawString((float)drawX, (float)(drawY - 112), "" + (this.target == null ? "null" : this.target.getUniqueID()), fontOptions);
        LineHitbox.fromAngled((float)camera.getDrawX(this.x), (float)camera.getDrawY(this.y), this.getAngle(), 10.0F, this.getWidth()).draw(1.0F, 0.0F, 0.0F, 0.5F);
        Mob m = this.target != null ? (Mob)this.target : (Mob)this.streamTargets(this.getOwner(), (Shape)null).min((m1, m2) -> {
            return (int)(m1.getDistance(this.x, this.y) - m2.getDistance(this.x, this.y));
        }).orElse(null);
        if (m != null) {
            Line2D.Float perpLine = new Line2D.Float(m.x, m.y, m.x + -this.dy, m.y + this.dx);
            Point2D p = GameMath.getIntersectionPoint(new Line2D.Float(this.x, this.y, this.x + this.dx, this.y + this.dy), perpLine, true);
            Renderer.drawLineRGBA(camera.getDrawX(m.x), camera.getDrawY(m.y), camera.getDrawX((float)p.getX()), camera.getDrawY((float)p.getY()), 1.0F, 0.0F, 0.0F, 1.0F);
            Renderer.drawLineRGBA(camera.getDrawX(this.x), camera.getDrawY(this.y), camera.getDrawX((float)p.getX()), camera.getDrawY((float)p.getY()), 1.0F, 0.0F, 0.0F, 1.0F);
            Rectangle hit = m.getHitBox();
            Renderer.initQuadDraw(hit.width, hit.height).color(0.0F, 0.0F, 1.0F, 0.5F).draw(camera.getDrawX(hit.x), camera.getDrawY(hit.y));
        }

    }
}
