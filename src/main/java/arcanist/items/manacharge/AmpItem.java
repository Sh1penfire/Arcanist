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

import java.util.function.Supplier;

import static arcanist.content.ModLensModifiers.ModifierEntry.applyModifiers;
import static arcanist.content.ModLensModifiers.ModifierEntry.sumModifiers;


//Look mom, im a code reuser!
//Haha! Haha hah... ha...
//God this is going to take awhile
public class AmpItem extends Item implements InternalInventoryItemInterface {

    public int capacity;

    public int manaCost, reload;

    public GNDItemMap baseStats = new GNDItemMap();

    public AmpItem(int capacity){
        super(1);
        this.capacity = capacity;
        this.reload = (int) (0.125f * 1000);
    }

    public void flat(ModLensModifiers.ModifierEntry entry, float amount){
        baseStats.setFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX, amount);
    }

    public void multi(ModLensModifiers.ModifierEntry entry, float amount){
        baseStats.setFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX, amount);
    }

    public void flag(ModLensModifiers.ModifierEntry entry, boolean value){
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
        item.getGndData().addAll(baseStats);
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

    //Bit of boilerplate, could have this in an interface somehow but idk how I'd do that
    public void sumEffects(InventoryItem amplifier){
        Inventory inv = getInternalInventory(amplifier);
        GNDItemMap tmpGND = amplifier.getGndData();

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
            sumModifiers(tmpGND, item.getGndData());
        }
        applyModifiers(tmpGND, baseStats);
    }
}
