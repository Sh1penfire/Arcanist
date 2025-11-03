package arcanist.buffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;

import java.util.ArrayList;

import static arcanist.content.ModItems.clusterMap;
import static arcanist.content.ModItems.dustMap;

public class PropickTrinketBuff extends TrinketBuff {
    //public static HashMap<String, String> clusterMap = new HashMap<>();

    //Idk the impact of initialising a string every time im doing this but e
    static String category;

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        //tooltips.add(Localization.translate("itemtooltip", "propick_tip"), 350);
        return tooltips;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        eventSubscriber.subscribeEvent(MobObjectDamagedEvent.class, (event) -> {

            if (event.level.isServer()) {
                if (event.totalDamage > 0 && isValidObject(event.result.levelObject.object)) {
                    if (event.attacker instanceof ToolDamageItem.ToolDamageItemAttacker && event.result.destroyed && !event.result.levelTile.isPlayerPlaced) {

                        int tileX = event.result.levelTile.tileX, tileY = event.result.levelTile.tileY;
                        int x = tileX * 32, y = tileY * 32;
                        GameObject object = event.result.levelObject.object;

                        LootTable drops = object.getLootTable(event.level, event.result.objectLayerID, tileX, tileY);
                        ArrayList<LootItem> added = new ArrayList<>();
                        for (LootItemInterface i : drops.items) {
                            LootItem loot = (LootItem) i;
                            if (clusterMap.containsKey(loot.itemStringID)) {
                                added.add(LootItem.between(clusterMap.get(loot.itemStringID), 0, 1));
                            }
                            if (dustMap.containsKey(loot.itemStringID)) {
                                added.add(LootItem.between(dustMap.get(loot.itemStringID), 0, 1));
                            }
                        }

                        if(added.isEmpty()){
                            for (LootItemInterface i : drops.items) {
                                LootItem loot = (LootItem) i;
                                category = ItemCategory.getItemsCategory(ItemRegistry.getItem(loot.itemStringID)).stringID;
                                System.out.println(category);
                                if (category.equals("ore") || category.equals("minerals")) {
                                    added.add(LootItem.between(loot.itemStringID, 0, 1));
                                }
                            }
                        }
                        for (LootItem drop : added) {
                            InventoryItem Treasure = drop.getItem(GameRandom.globalRandom);
                            ItemPickupEntity TreasureEntity = Treasure.getPickupEntity(event.level, x, y);
                            event.level.entityManager.pickups.add(TreasureEntity);
                        }


                        //TODO: Figure out some way to modify the drops of existing blocks to be conditional based on trinket buffs player has when breaking
                        //System.out.println(added);
                        //drops.items.addAll(added);


                    }
                }
            }
        });
    }

    public boolean isValidObject(GameObject object) {
        return object.isOre;
    }

    /*
    public static class MinersProstheticAttacker implements Attacker {
        private final Mob owner;

        public MinersProstheticAttacker(Mob owner) {
            this.owner = owner;
        }

        public GameMessage getAttackerName() {
            return this.owner.getAttackerName();
        }

        public DeathMessageTable getDeathMessages() {
            return this.owner.getDeathMessages();
        }

        public Mob getFirstAttackOwner() {
            return this.owner;
        }
    }

     */
}
