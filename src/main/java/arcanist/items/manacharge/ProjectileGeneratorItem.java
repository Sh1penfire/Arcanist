package arcanist.items.manacharge;

import arcanist.content.GNDKeys;
import arcanist.content.ModLensModifiers;
import arcanist.util.Formatter;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.FrostFlameBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.IronBowProjectileToolItem;

import java.util.function.Supplier;


//The manacharge base actually reads from this it doesn't "generate" projectiles lololololo
public class ProjectileGeneratorItem extends Item implements InternalInventoryItemInterface {

    static GNDItemMap tmpGND = new GNDItemMap();

    public String projectileType;
    public int capacity;

    public int manaCost, reload;
    //Stuff that I don't want to make a billion fields for
    //Note: PUT THE ACTUAL STATS IN NOT WITH ADDITIVE OR MULTIPLICATIVE
    public GNDItemMap baseStats = new GNDItemMap();

    public ProjectileGeneratorItem(int capacity, String projectileType){
        super(1);
        this.capacity = capacity;
        this.projectileType = projectileType;
        this.reload = (int) (0.125f * 1000);
    }

    public void floatStat(ModLensModifiers.ModifierEntry entry, float amount){
        baseStats.setFloat(entry.id, amount);
    }

    public void boolStat(ModLensModifiers.ModifierEntry entry, boolean value){
        baseStats.setBoolean(entry.id, value);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltip = super.getTooltips(item, perspective, blackboard);
        GNDItemMap data = item.getGndData();
        return Formatter.formatModifiers(tooltip, data, false, false);
    };

    @Override
    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        InventoryItem item = super.getDefaultItem(player, amount);
        item.getGndData().setBoolean(GNDKeys.MODIFIER_ITEM, true);
        return item;
    }

    //Boilerplate code
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }

            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }

            if (playerSlot != null) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    this.openContainer(client, playerSlot);
                }

                return new ContainerActionResult(-1002911334);
            } else {
                return new ContainerActionResult(208675834, Localization.translate("itemtooltip", "rclickinvopenerror"));
            }
        };
    }

    @Override
    public boolean isValidItem(InventoryItem item) {
        return true;
    }

    protected void openContainer(ServerClient client, PlayerInventorySlot inventorySlot) {
        PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.ITEM_INVENTORY_CONTAINER, ItemInventoryContainer.getContainerContent(this, inventorySlot));
        ContainerRegistry.openAndSendContainer(client, p);
    }

    @Override
    public int getInternalInventorySize() {
        return capacity;
    }

    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        InternalInventoryItemInterface.super.saveInternalInventory(item, inventory);
        sumEffects(item);
    }

    public void sumEffects(InventoryItem generator){
        Inventory inv = getInternalInventory(generator);
        GNDItemMap tmpGND = generator.getGndData();

        //Reset all the values
        ModLensModifiers.flat.forEach((entry) -> {
            tmpGND.setFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, 0);
        });
        ModLensModifiers.multi.forEach((entry) -> {
            tmpGND.setFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, 0);
        });
        ModLensModifiers.flags.forEach((entry) -> {
            tmpGND.setBoolean(entry.id, false);
        });

        for (int i = 0; i < capacity; i++) {
            InventoryItem item = inv.getItem(i);
            if(item == null) continue;
            GNDItemMap data = item.getGndData();

            ModLensModifiers.flat.forEach((entry) -> {
                float value = data.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, 0) + tmpGND.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, 0);
                if(value != 0) tmpGND.setFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, value);
            });
            ModLensModifiers.multi.forEach((entry) -> {
                float value = data.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, 0) + tmpGND.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, 0);
                if(value != 0) tmpGND.setFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, value);
            });
            ModLensModifiers.flags.forEach((entry) -> {
                boolean value = data.getBoolean(entry.id);
                if(value) tmpGND.setBoolean(entry.id, true);
            });
        }
    }
}
