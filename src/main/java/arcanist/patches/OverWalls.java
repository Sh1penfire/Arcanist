package arcanist.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.level.maps.CollisionFilter;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = BulletProjectile.class, name = "getLevelCollisionFilter", arguments = {})
public class OverWalls {

    @Advice.OnMethodExit
    static void onExit(@Advice.This BulletProjectile bullet, @Advice.Return(readOnly = false) CollisionFilter filter) {


        System.out.println("Exited BulletProjectile.getLevelCollisionFilter(): " + bullet.getStringID());
    }
}
