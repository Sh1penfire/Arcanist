package arcanist.items.manacharge;

import arcanist.content.ModContainers;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

import java.util.function.Consumer;
import java.util.function.Supplier;


//Look mom, im a code reuser!
//Haha! Haha hah... ha...
//God this is going to take awhile
public class AmpItem extends Item implements InternalInventoryItemInterface {

    public int capacity;

    public int manaCost, reload;

    public AmpItem(int capacity){
        super(1);
        this.capacity = capacity;
        this.reload = (int) (0.125f * 1000);
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
}
