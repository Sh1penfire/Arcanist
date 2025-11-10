package arcanist.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ManachargeStackBuff extends Buff {
    public ManachargeStackBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public int getStackSize(ActiveBuff buff) {
        return 100;
    }

    public boolean overridesStackDuration() {
        return true;
    }

    public boolean showsFirstStackDurationText() {
        return super.showsFirstStackDurationText();
    }
}
