package arcanist.items;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;

public class ClusterItem extends MatItem {
    public ClusterItem(int stackSize, String... globalIngredients) {
        super(stackSize, globalIngredients);
        setItemCategory("materials", "clusters");
        keyWords.add("cluster");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "cluster_tip"));

        return tooltips;

    }
}
