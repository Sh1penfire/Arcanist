package arcanist.containers;

import arcanist.containers.slots.GeneratorSlot;
import arcanist.containers.slots.LensSlot;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.EnchantableSpecificSlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class LensContainer extends Container {
    int itemID;
    InventoryItem item;
    public PlayerInventorySlot inventoryItemSlot;
    public int LENS_SLOT, GENERATOR_SLOT;
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
        LENS_SLOT = addSlot(new LensSlot(inventory, 1));
    }
}
