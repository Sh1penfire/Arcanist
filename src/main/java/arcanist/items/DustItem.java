package arcanist.items;

import necesse.inventory.item.matItem.MatItem;

public class DustItem extends MatItem {
    public DustItem(int stackSize, String... globalIngredients) {
        super(stackSize, globalIngredients);
        setItemCategory("materials", "dusts");
        keyWords.add("dust");
    }
}
