package arcanist.content;

import NecesseExpanded.Buffs.Trinkets.MidasBuff;
import arcanist.buffs.PropickTrinketBuff;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ModBuffs {
    public static Buff
    midas, propick, manasight;

    public static void load(){
        midas = BuffRegistry.registerBuff("midas", new MidasBuff());

        //Trinket buffs
        propick = BuffRegistry.registerBuff("propick", new PropickTrinketBuff());

        manasight = BuffRegistry.registerBuff("manasight", new Buff() {
            @Override
            public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {

            }
        });
    }
}
