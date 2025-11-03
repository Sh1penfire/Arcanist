package arcanist.content;

import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;

//Copied from Necesse Expanded
public class UpdateAmmoTypes
{
    public static void update()
    {
        System.out.println("[Industresse] Updating ammo types for ranged weapons...");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("midas_bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("drill_bullet");
    }
}