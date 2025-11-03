package arcanist.entities.projectiles;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;

public class ModBullet extends BulletProjectile {

    public ModBullet(){
        super();
    }
    public ModBullet(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    //JAVAAAA GENERIC TYPEEEEEEEEEEEEES
    public static ModBullet create(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner){
        return new ModBullet(x, y, targetX, targetY, velocity, range * 4, damage, knockback, owner);
    };
}