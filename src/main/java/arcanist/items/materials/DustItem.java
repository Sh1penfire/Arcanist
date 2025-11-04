package arcanist.items.materials;

import necesse.inventory.item.matItem.MatItem;

public class DustItem extends MatItem {
    public DustItem(int stackSize, String... globalIngredients) {
        super(stackSize, globalIngredients);
        setItemCategory("materials", "acn_dusts");
        keyWords.add("dust");
    }
}
