package arcanist.containers;

import arcanist.containers.slots.GeneratorSlot;
import arcanist.containers.slots.AmpSlot;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class LensContainer extends Container {
    int itemID;
    InventoryItem item;
    public PlayerInventorySlot inventoryItemSlot;
    public int AMP_SLOT, GENERATOR_SLOT;
    public Inventory inventory;

    public LensContainer(NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);

        PacketReader reader = new PacketReader(content);
        this.itemID = reader.getNextShortUnsigned();
        int itemInventoryID = reader.getNextInt();
        int itemInventorySlot = reader.getNextInt();
        this.inventoryItemSlot = new PlayerInventorySlot(itemInventoryID, itemInventorySlot);


        this.item = this.inventoryItemSlot.getItem(client.playerMob.getInv());

        InternalInventoryItemInterface internalInventoryItemInterface = (InternalInventoryItemInterface)this.item.item;
        this.inventory = internalInventoryItemInterface.getInternalInventory(this.item);

        GENERATOR_SLOT = addSlot(new GeneratorSlot(inventory, 0));
        AMP_SLOT = addSlot(new AmpSlot(inventory, 1));
    }

    public void tick() {
        super.tick();
        InventoryItem item;
        if (this.client.isClient()) {
            if (this.inventory == null) {
                this.client.getClientClient().getClient().closeContainer(true);
                return;
            }

            item = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (this.item != item && item != null) {
                if (!(item.item instanceof InternalInventoryItemInterface)) {
                    this.client.getClientClient().getClient().closeContainer(true);
                    return;
                }

                this.inventory.override(((InternalInventoryItemInterface)item.item).getInternalInventory(item));
                this.item = item;
            }
        }

        if (this.inventory.isDirty()) {
            item = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (item != null) {
                ((InternalInventoryItemInterface)item.item).saveInternalInventory(item, this.inventory);
            }

            this.inventory.clean();
            this.inventoryItemSlot.markDirty(this.client.playerMob.getInv());
        }

    }

    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        } else if (this.inventoryItemSlot == null) {
            return false;
        } else {
            InventoryItem invItem = this.inventoryItemSlot.getItem(client.playerMob.getInv());
            return invItem != null && invItem.item.getID() == this.itemID && invItem.item instanceof InternalInventoryItemInterface;
        }
    }
}
