package arcanist.content;

import arcanist.buffs.ManachargeStackBuff;
import arcanist.buffs.MidasBuff;
import arcanist.buffs.PropickTrinketBuff;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ModBuffs {
    public static Buff
    midas, propick, manasight;

    public static void load(){
        midas = BuffRegistry.registerBuff("acn_midas", new MidasBuff());

        //Trinket buffs
        propick = BuffRegistry.registerBuff("acn_propick", new PropickTrinketBuff());

        manasight = BuffRegistry.registerBuff("acn_manasight", new Buff() {
            @Override
            public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {

            }
        });
    }
}
