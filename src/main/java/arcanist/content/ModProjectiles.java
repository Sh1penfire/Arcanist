package arcanist.content;

import arcanist.entities.projectiles.DrillBulletProjectile;
import arcanist.entities.projectiles.MidasBulletProjectile;
import necesse.engine.registries.ProjectileRegistry;

public class ModProjectiles {
    public static void load(){
        ProjectileRegistry.registerProjectile("midas_bullet", MidasBulletProjectile.class, "midas_bullet", "midas_bullet_shadow");
        ProjectileRegistry.registerProjectile("drill_bullet", DrillBulletProjectile.class, "drill_bullet", "drill_bullet_shadow");
    }
}
