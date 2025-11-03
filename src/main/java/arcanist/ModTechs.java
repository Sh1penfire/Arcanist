package arcanist;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Tech;

public class ModTechs {
    public static Tech
    CRUSHING,
    PACKAGING,
    SCORCHING;
    public static void register(){
        CRUSHING = register("crushing", "crusher");
        PACKAGING = register("packaging", "packager");
        SCORCHING = register("scorching", "scorcher");
    }

    public static Tech register(String tech, String object){
        return RecipeTechRegistry.registerTech(tech, object);
    }
}
