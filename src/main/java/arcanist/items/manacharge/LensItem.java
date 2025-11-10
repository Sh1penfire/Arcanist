package arcanist.items.manacharge;

import arcanist.util.Formatter;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

import java.util.ArrayList;
import java.util.HashMap;

//Holds modifiers to the projectiles spawned by the projectile generator
public class LensItem extends Item {
    public float damage,
    damageMultiplier,
    knockback,
    speed,
    homingPower,
    homingRange,
    objectDamageFract,
    lifestealFract,
    range,
    rangeMultiplier;

    public int
    pierce,
    bounce;

    //HERE WE GOOOOOOOOOOOOOOOOOOOOOOO
    public HashMap<String, Float> additives = new HashMap<>(), multis = new HashMap<>();

    //Amplication for other lenses based on position offset
    public HashMap<Integer, Float> amp = new HashMap<>();
    //Redirects amp effects, and strengthens/weakens them in the process. Can't redirect the same effect more than once. Can split amp effects if directed to more than one lens.
    public HashMap<Integer, Float> mirror = new HashMap<>();


    public LensItem() {
        super(1);

    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltip = super.getTooltips(item, perspective, blackboard);
        GNDItemMap itemData = item.getGndData();
        if(damage != 0) tooltip.add(Localization.translate("acn_modifiers", "damage", "amount", damage));
        if(damageMultiplier != 0) tooltip.add(Localization.translate("acn_modifiers", "damage_multi", "amount", Formatter.percentSign(damageMultiplier)));
        if(speed != 0) tooltip.add(Localization.translate("acn_modifiers", "speed", "amount", speed/32));
        if(objectDamageFract != 0) tooltip.add(Localization.translate("acn_modifiers", "object_damage_fract", "amount", objectDamageFract * 100));
        if(pierce != 0) tooltip.add(Localization.translate("acn_modifiers", "pierce", "amount", pierce));
        return tooltip;
    }

    @Override
    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        InventoryItem self = super.getDefaultItem(player, amount);
        return self;
    }

    public void modify(ManachargeBaseItem.ProjStats stats, float amp, HashMap<Integer, LensItem> map){
        stats.damage += damage * amp;
        stats.damageMultiplier += damageMultiplier * amp;
        stats.knockback += knockback * amp;
        stats.speed += speed * amp;
        stats.range += range * amp;
        stats.range += rangeMultiplier * amp;
        stats.objectDamageFract += objectDamageFract * amp;


        stats.setPierce(stats.pierce + pierce);
        stats.setBounce(stats.bounce + bounce);
    }
}
