package arcanist.patches;

import arcanist.content.ModBuffs;
import necesse.entity.mobs.Mob;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.TilePosition;

import java.util.function.Predicate;

public class NoneFilter implements Predicate<TilePosition> {

    private final Mob owner;
    private final CollisionFilter original;

    public NoneFilter(Mob owner, CollisionFilter original){
        this.owner = owner;
        this.original = original;
    }

    @Override
    public boolean test(TilePosition tp) {
        //TODO: Setup projectile elevation
        if(true) return true;
        else return !owner.buffManager.hasBuff(ModBuffs.manasight) && original.testFilter(tp);
    }
}
