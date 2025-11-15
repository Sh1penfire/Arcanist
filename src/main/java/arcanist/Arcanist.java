package arcanist;

import arcanist.content.*;
import arcanist.tiles.CrusherObject;
import arcanist.tiles.PackagerObject;
import arcanist.tiles.ScorcherObject;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

import static arcanist.content.ModItems.clusterMap;
import static arcanist.content.ModItems.dustMap;

@ModEntry
public class Arcanist {

    //Prefix for all the mod's content & GND data keys
    public static String ID = "acn";

    public static String prefixID(String input){
        return ID + "_" + input;
    }

    public void init() {
        ModLensModifiers.load();
        ModTechs.register();
        ModContainers.load();

        /*
        BONUS_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> new BonusPartContainerForm<>(client, new BonusPartContainer(client.getClient(), uniqueSeed, (BonusPartStationObjectEntity)oe, new PacketReader(content))), (client, uniqueSeed, oe, content, serverObject) -> new BonusPartContainer(client, uniqueSeed, (BonusPartStationObjectEntity)oe, new PacketReader(content)));
        GunsmithContainer.openAndSendContainer(gunsmith.BONUS_CONTAINER, player.getServerClient(), level, x, y);
        FueledProcessingOEInventoryContainer
        ContainerRegistry.FUELED_PROCESSING_STATION_CONTAINER

         */

        // Register our tiles
        //TileRegistry.registerTile("exampletile", new ExampleTile(), 1, true);

        //Crusher..?
        ObjectRegistry.registerObject("acn_crusher", new CrusherObject(), 1, true);
        //Crusher??
        ObjectRegistry.registerObject("acn_packager", new PackagerObject(), 1, true);
        //Crusher???
        ObjectRegistry.registerObject("acn_scorcher", new ScorcherObject(), 1, true);

        // Register out objects
        //ObjectRegistry.registerObject("exampleobject", new ExampleObject(), 2, true);

        ModItems.load();
        ModProjectiles.load();
        ModBuffs.load();

        // Register our mob
        //MobRegistry.registerMob("examplemob", ExampleMob.class, true);

        // Register our projectile
        //ProjectileRegistry.registerProjectile("exampleprojectile", ExampleProjectile.class, "exampleprojectile", "exampleprojectile_shadow");

        // Register our buff
        //BuffRegistry.registerBuff("examplebuff", new ExampleBuff());

    }

    public void initResources() {
        ModSounds.load();
        ModTextures.load();

        // Sometimes your textures will have a black or other outline unintended under rotation or scaling
        // This is caused by alpha blending between transparent pixels and the edge
        // To fix this, run the preAntialiasTextures gradle task
        // It will process your textures and save them again with a fixed alpha edge color

    }

    public void crushingRecipe(String source, String output, int amount){
        Recipes.registerModRecipe(new Recipe(
                output,
                amount,
                ModTechs.CRUSHING,
                new Ingredient[]{
                        new Ingredient(source, 1)
                }
        ));
    }

    //Used for dusts to packages, taking 2 dust + 1 log per package
    public void packagingRecipe(String source, String packed, int amount, int produced){
        Recipes.registerModRecipe(new Recipe(
                packed,
                produced,
                ModTechs.PACKAGING,
                new Ingredient[]{
                        new Ingredient(source, amount),
                        new Ingredient("anylog", 1)
                }
        ));
    }
    public void packagingRecipe(String source, String packed, int amount){
        packagingRecipe(source, packed, amount, 1);
    }

    //Used for things like bullets/arrows
    public void containmentRecipe(String material, String container, String packed, int amount){
        Recipes.registerModRecipe(new Recipe(
                packed,
                amount,
                ModTechs.PACKAGING,
                new Ingredient[]{
                        new Ingredient(container, amount),
                        new Ingredient(material, 1)
                }
        ));
    }
    public void infuseArrow(String material, String output, int amount){
        containmentRecipe(material, "stonearrow", output, amount);
    }

    public void infuseBullet(String material, String output, int amount){
        containmentRecipe(material, "simplebullet", output, amount);
    }

    public void scorchingRecipe(String source, String bar, int requirement){
        Recipes.registerModRecipe(new Recipe(
                bar,
                1,
                ModTechs.SCORCHING,
                new Ingredient[]{
                        new Ingredient(source, requirement)
                }
        ));
    }

    public void setupCrushing(String bar, String ore, String dust, String packed, int efficiency){
        crushingRecipe(bar, dust, 2);
        crushingRecipe(ore, dust, 1 * efficiency);
        packagingRecipe(dust, packed, 2);
        scorchingRecipe(packed, bar, 1);
        dustMap.put(ore, dust);
    }

    public void clusterCrushing(String material, String cluster){
        clusterMap.put(material, cluster);
        crushingRecipe(cluster, material, 3);
    }

    public void postInit() {
        UpdateAmmoTypes.update();
        UpdateCategories.update();
        // Add recipes
        setupCrushing("copperbar", "copperore", "acn_copper_dust", "acn_copper_dust_package", 1);
        setupCrushing("ironbar", "ironore", "acn_iron_dust", "acn_iron_dust_package", 1);
        setupCrushing("goldbar", "goldore", "acn_gold_dust", "acn_gold_dust_package", 2);
        setupCrushing("ivybar", "ivyore", "acn_ivy_paste", "acn_ivy_paste_package", 1);

        infuseArrow("torch", "firearrow", 20);
        infuseArrow("frostshard", "frostarrow", 20);
        infuseBullet("voidshard", "frostbullet", 100);
        infuseBullet("voidshard", "voidbullet", 200);
        infuseBullet("acn_gold_dust", "acn_midas_bullet", 10);
        infuseBullet("ironpickaxe", "acn_drill_bullet", 10);

        //Doing theese manually
        //I know necesse expanded already has bomb recipies using fertiliser so im using the firemone seeds instead
        Recipes.registerModRecipe(new Recipe(
                "ironbomb",
                1,
                ModTechs.PACKAGING,
                new Ingredient[]{
                        new Ingredient("firemoneseed", 2)
                }
        ));

        clusterCrushing("frostshard", "acn_frostshard_cluster");
        clusterCrushing("quartz", "acn_quartz_cluster");

        Recipes.registerModRecipe(new Recipe(
                "acn_ore_pouch",
                1,
                RecipeTechRegistry.DEMONIC_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("acn_runed_steel", 12),
                        new Ingredient("leather", 20),
                        new Ingredient("miningpotion", 5)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "acn_propick",
                1,
                RecipeTechRegistry.DEMONIC_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("acn_runed_steel", 12),
                        new Ingredient("acn_gold_dust", 12),
                        new Ingredient("leather", 8)
                }
        ).showAfter("calmingminersbouquet"));

        Recipes.registerModRecipe(new Recipe(

                "acn_runestone_paste",
                4,
                ModTechs.CRUSHING,
                new Ingredient[]{
                        new Ingredient("runestone", 1)
                }
        ));

        dustMap.put("runestone", "acn_runestone_paste");

        Recipes.registerModRecipe(new Recipe(
                "acn_charloag",
                2,
                ModTechs.SCORCHING,
                new Ingredient[]{
                        new Ingredient("anycharrable", 1)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "acn_midas_bullet",
                10,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("simplebullet", 10),
                        new Ingredient("acn_gold_dust", 1)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "astral_fulgurite",
                1,
                RecipeTechRegistry.ALCHEMY,
                new Ingredient[]{
                        new Ingredient("ruby", 10)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "acn_runed_steel",
                1,
                RecipeTechRegistry.ALCHEMY,
                new Ingredient[]{
                        new Ingredient("acn_iron_dust", 2),
                        new Ingredient("acn_runestone_paste", 8),
                        new Ingredient("acn_charloag", 2)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "acn_scorcher",
                1,
                RecipeTechRegistry.DEMONIC_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anystone", 12)
                }
        ).showAfter("forge"));

        Recipes.registerModRecipe(new Recipe(
                "acn_packager",
                1,
                RecipeTechRegistry.DEMONIC_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anystone", 15),
                        new Ingredient("clay", 12)
                }
        ).showAfter("acn_scorcher"));

        Recipes.registerModRecipe(new Recipe(
                "acn_crusher",
                1,
                RecipeTechRegistry.DEMONIC_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anystone", 40),
                        new Ingredient("ironbomb", 2)
                }
        ).showAfter("acn_packager"));

        clusterMap.put("frostshard", "acn_frostshard_cluster");
        clusterMap.put("quartz", "acn_quartz_cluster");

        Recipes.registerModRecipe(new Recipe(
                prefixID("icicle_generator"),
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("anylog", 30),
                        new Ingredient("demonicbar", 10),
                        new Ingredient(prefixID("runed_steel"), 12)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                prefixID("gilded_amp"),
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("batwing", 4),
                        new Ingredient("sapphire", 3),
                        new Ingredient(prefixID("gold_dust"), 24)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                prefixID("manacharge_pistol"),
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("anylog", 9),
                        new Ingredient(prefixID("runed_steel"), 20)
                }
        ));


        Recipes.registerModRecipe(new Recipe(
                prefixID("focal_lens"),
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("glass", 5),
                        new Ingredient(prefixID("runed_steel"), 3)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                prefixID("sharp_lens"),
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("glass", 5),
                        new Ingredient(prefixID("runed_steel"), 3)
                }
        ).showAfter(prefixID("focal_lens")));
        Recipes.registerModRecipe(new Recipe(
                prefixID("drilling_lens"),
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("glass", 5),
                        new Ingredient(prefixID("runed_steel"), 3)
                }
        ).showAfter(prefixID("sharp_lens")));

        // Add out example mob to default cave mobs.
        // Spawn tables use a ticket/weight system. In general, common mobs have about 100 tickets.
        //Biome.defaultCaveMobs.add(100, "examplemob");

        //GameEvents.addListener(MobLootTableDropsEvent.class, new PropickEventHandler());
        //GameEvents.addListener(MobLootTableDropsEvent.class, new PropickEventHandler());
    }

}
