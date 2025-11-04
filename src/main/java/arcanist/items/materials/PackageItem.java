package arcanist.items.materials;

import necesse.inventory.item.matItem.MatItem;

//TODO: Figure out if this should represent 1 bar or have variants for each stage in oreproc
public class PackageItem extends MatItem {
    public PackageItem(int stackSize, String... globalIngredients) {
        super(stackSize, globalIngredients);
        setItemCategory("materials", "acn_packages");
        keyWords.add("package");
    }
}
