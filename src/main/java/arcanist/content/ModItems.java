package arcanist.content;

import arcanist.items.*;
import arcanist.items.bullets.DrillBulletItem;
import arcanist.items.bullets.MidasBulletItem;
import arcanist.items.manacharge.*;
import arcanist.items.materials.ClusterItem;
import arcanist.items.materials.DustItem;
import arcanist.items.materials.PackageItem;
import arcanist.items.trinkets.PropickTrinket;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.gfx.GameColor;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.item.miscItem.TelescopeItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

import java.util.HashMap;

import static arcanist.Arcanist.prefixID;
import static necesse.engine.registries.ItemRegistry.registerItem;

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
        ItemCategory dusts = ItemCategory.createCategory("C-C-A", "materials", "acn_dusts");
        ItemCategory packages = ItemCategory.createCategory("C-C-A", "materials", "acn_packages");
        ItemCategory clusters = ItemCategory.createCategory("C-B-A", "materials", "acn_clusters");

        // Register our items

        registerItem("acn_midas_bullet", new MidasBulletItem(), 1, true);
        registerItem("acn_drill_bullet", new DrillBulletItem(), 1, true);

        registerItem("acn_copper_dust", new DustItem(100), 0.5f, true);
        registerItem("acn_iron_dust", new DustItem(100), 1, true);
        registerItem("acn_gold_dust", new DustItem(100), 5, true);
        registerItem("acn_runestone_paste", new DustItem(100), 5, true);
        registerItem("acn_ivy_paste", new DustItem(100), 5, true);

        registerItem("acn_copper_dust_package", new PackageItem(500), 1, true);
        registerItem("acn_iron_dust_package", new PackageItem(500), 2, true);
        registerItem("acn_gold_dust_package", new PackageItem(500), 10, true);
        registerItem("acn_ivy_paste_package", new PackageItem(500), 7, true);

        registerItem("acn_runed_steel", new MatItem(250).setItemCategory("materials", "bars"), 4, true);

        registerItem("acn_frostshard_cluster", new ClusterItem(250), 4, true);
        registerItem("acn_quartz_cluster", new ClusterItem(250), 4, true);

        charloag = register("acn_charloag", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 10, true);

        registerItem("acn_ore_pouch", new OrePouchItem(), 250, true);

        registerItem("acn_propick", new PropickTrinket(), 10, true);

        registerItem("acn_manacharge_pistol", new RefinedManachargeItem(640, null){{

            manaCost = new FloatUpgradeValue(1, 0.25f);

            floatAbilityStat(ModLensModifiers.damage, 48);
            floatAbilityStat(ModLensModifiers.range, 48 * 32);
            floatAbilityStat(ModLensModifiers.speed, 4 * 32);
            floatAbilityStat(ModLensModifiers.bounce, 8);
            floatAbilityStat(ModLensModifiers.pierce, 8);

            flat(ModLensModifiers.bursts, 2);
        }}, 10, true);

        registerItem("acn_focal_lens", new LensModifierItem(){{
            flat(ModLensModifiers.pierce.id, 2);
            flat(ModLensModifiers.speed.id, 64);
        }}, 10, true);
        registerItem("acn_sharp_lens", new LensModifierItem(){{
            multi(ModLensModifiers.damage.id, 0.25f);
            flat(ModLensModifiers.speed.id, 32);
            flat(ModLensModifiers.range.id, -32);
        }}, 10, true);

        registerItem("acn_drilling_lens", new LensModifierItem(){{
            flat(ModLensModifiers.objectDamageFract.id, 0.5f);
        }}, 10, true);
        registerItem(prefixID("icicle_generator"), new ProjectileGeneratorItem(2, prefixID("ice_pellet")){{
            floatStat(ModLensModifiers.damage, 12);
            floatStat(ModLensModifiers.range, 12 * 32);
            floatStat(ModLensModifiers.speed, 8 * 32);

        }}, 150, true);
        registerItem(prefixID("gilded_amp"), new AmpItem(0){{
            multi(ModLensModifiers.damage, 0.25f);
        }}, 10, true);


        //EXAMPLE GENERATOR, AMP AND LENS
        registerItem("acn_generator", new ProjectileGeneratorItem(4, "acn_ice_pellet"){{
            floatStat(ModLensModifiers.damage, 12);
            floatStat(ModLensModifiers.range, 12 * 32);
            floatStat(ModLensModifiers.speed, 8 * 32);
        }}, 10, true);
        registerItem("acn_MINUS", new LensModifierItem(){{
            flat(ModLensModifiers.objectDamageFract.id, -0.5f);
        }}, 10, true);
        registerItem("acn_amp", new AmpItem(2){{
            //
        }}, 10, true);

        registerItem("astral_fulgurite", new Item(64), 30, true);
    }

    //I like having the item definition outside the method call .w.
    public static Item register(String ID, Item item, float brokerValue, boolean obtainable){
        registerItem(ID, item, brokerValue, obtainable);
        return item;
    }
}
