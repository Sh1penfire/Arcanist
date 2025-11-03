package arcanist.content;

import arcanist.items.*;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.gfx.GameColor;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemCategoryManager;
import necesse.inventory.item.matItem.MatItem;

import java.util.ArrayList;
import java.util.HashMap;

public class ModItems {

    public static HashMap<String, String> dustMap = new HashMap<>();
    public static HashMap<String, String> clusterMap = new HashMap<>();

    public static Item
            charloag;

    public static void load(){
        GlobalIngredientRegistry.registerGlobalIngredient("anycharrable", GameMessageBuilder.colorCoded(GameColor.WHITE, "itemtooltip", "charrable_tip"), GameMessageBuilder.colorCoded(GameColor.WHITE, "itemtooltip", "charrable_tip"));

        /*
        ItemCategory.createCategory("C-A-A", "materials");
        createCategory("C-A-A", "materials", "ore");
        createCategory("C-B-A", "materials", "minerals");
        createCategory("C-C-A", "materials", "bars");
        createCategory("C-D-A", "materials", "stone");
        createCategory("C-E-A", "materials", "logs");
        createCategory("C-F-A", "materials", "specialfish");
        createCategory("C-G-A", "materials", "flowers");
        createCategory("C-H-A", "materials", "mobdrops");
        createCategory("C-I-A", "materials", "essences");

         */
        ItemCategory dusts = ItemCategory.createCategory("C-C-A", "materials", "dusts");
        ItemCategory packages = ItemCategory.createCategory("C-C-A", "materials", "packages");
        ItemCategory clusters = ItemCategory.createCategory("C-B-A", "materials", "clusters");

        // Register our items

        ItemRegistry.registerItem("midas_bullet", new MidasBulletItem(), 1, true);
        ItemRegistry.registerItem("drill_bullet", new DrillBulletItem(), 1, true);

        ItemRegistry.registerItem("copper_dust", new DustItem(100), 0.5f, true);
        ItemRegistry.registerItem("iron_dust", new DustItem(100), 1, true);
        ItemRegistry.registerItem("gold_dust", new DustItem(100), 5, true);
        ItemRegistry.registerItem("runestone_paste", new DustItem(100), 5, true);
        ItemRegistry.registerItem("ivy_paste", new DustItem(100), 5, true);

        ItemRegistry.registerItem("copper_dust_package", new PackageItem(500), 1, true);
        ItemRegistry.registerItem("iron_dust_package", new PackageItem(500), 2, true);
        ItemRegistry.registerItem("gold_dust_package", new PackageItem(500), 10, true);
        ItemRegistry.registerItem("ivy_paste_package", new PackageItem(500), 7, true);

        ItemRegistry.registerItem("runed_steel", new MatItem(250).setItemCategory("materials", "bars"), 4, true);

        ItemRegistry.registerItem("frostshard_cluster", new ClusterItem(250), 4, true);
        ItemRegistry.registerItem("quartz_cluster", new ClusterItem(250), 4, true);

        ItemRegistry.registerItem("charloag", charloag = new MatItem(500, "anylog").setItemCategory("materials", "logs"), 10, true);


        ItemRegistry.registerItem("ore_pouch", new OrePouchItem(), 250, true);

        ItemRegistry.registerItem("propick", new PropickTrinket(), 10, true);

        ArrayList<Integer> logs = GlobalIngredientRegistry.getGlobalIngredient("anylog").getObtainableRegisteredItemIDs();
        logs.forEach(logId -> {
            if(logId == charloag.getID()) return;
            Item log = ItemRegistry.getItem(logId);
            log.addGlobalIngredient("anycharrable");
        });

        ArrayList<Integer> ores = GlobalIngredientRegistry.getGlobalIngredient("anylog").getObtainableRegisteredItemIDs();

        ItemRegistry.registerItem("astral_fulgurite", new Item(64), 30, true);
    }
}
