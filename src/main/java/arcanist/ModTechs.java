package arcanist;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Tech;

public class ModTechs {
    public static Tech
    CRUSHING,
    PACKAGING,
    SCORCHING;
    public static void register(){
        CRUSHING = register("acn_crushing", "acn_crusher");
        PACKAGING = register("acn_packaging", "acn_packager");
        SCORCHING = register("acn_scorching", "acn_scorcher");
    }

    public static Tech register(String tech, String object){
        return RecipeTechRegistry.registerTech(tech, object);
    }
}
