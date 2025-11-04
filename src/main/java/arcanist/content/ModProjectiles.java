package arcanist.content;

import arcanist.entities.projectiles.DrillBulletProjectile;
import arcanist.entities.projectiles.MidasBulletProjectile;
import necesse.engine.registries.ProjectileRegistry;

public class ModProjectiles {
    public static void load(){
        ProjectileRegistry.registerProjectile("acn_midas_bullet", MidasBulletProjectile.class, "acn_midas_bullet", "acn_midas_bullet_shadow");
        ProjectileRegistry.registerProjectile("acn_drill_bullet", DrillBulletProjectile.class, "acn_drill_bullet", "acn_drill_bullet_shadow");
    }
}
