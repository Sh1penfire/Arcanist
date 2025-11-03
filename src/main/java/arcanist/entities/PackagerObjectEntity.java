package arcanist.entities;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import arcanist.ModTechs;
import necesse.entity.objectEntity.AnyLogFueledProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public class PackagerObjectEntity extends AnyLogFueledProcessingTechInventoryObjectEntity {
    public static int logFuelTime = 40000;
    public static int recipeProcessTime = 8000;

    public PackagerObjectEntity(Level level, int x, int y) {
        super(level, "packager", x, y, 2, 2, false, false, true, new Tech[]{ModTechs.PACKAGING});
    }

    public int getFuelTime(InventoryItem item) {
        return logFuelTime;
    }

    public int getProcessTime(Recipe recipe) {
        return recipeProcessTime;
    }

    public boolean shouldBeAbleToChangeKeepFuelRunning() {
        return false;
    }
}
