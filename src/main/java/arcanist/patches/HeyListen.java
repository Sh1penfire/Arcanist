package arcanist.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.CollisionFilter;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = Projectile.class, name = "getLevelCollisionFilter", arguments = {})
public class HeyListen {

    @Advice.OnMethodExit
    public static void onExit(@Advice.This Projectile projectile, @Advice.Return(readOnly = false)CollisionFilter filter){

        CollisionFilter original = filter;
        //filter = new CollisionFilter().addFilter(new NoneFilter(projectile.getOwner(), original));


        System.out.println("Exited BulletProjectile.getLevelCollisionFilter(): " + projectile.getStringID());
    }
}

