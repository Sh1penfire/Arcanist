package arcanist.content;

import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;

//Copied from Necesse Expanded
public class UpdateAmmoTypes
{
    public static void update()
    {
        System.out.println("[Arcanist] Updating ammo types for ranged weapons...");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("acn_midas_bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("acn_drill_bullet");
    }
}