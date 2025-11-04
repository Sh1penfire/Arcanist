package arcanist.items.manacharge;

import arcanist.content.ModContainers;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.networkAction.CustomNetworkAction;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseProjectileAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.followingProjectile.ElderlyWandProjectile;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ElderlyWandProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ManachargeBaseItem extends ToolItem implements InternalInventoryItemInterface, TickItem {

    public ManachargeBaseItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory(new String[]{"equipment", "tools"});
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
    }

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
        PacketOpenContainer p = new PacketOpenContainer(ModContainers.LENS_CONTAINER, ItemInventoryContainer.getContainerContent(this, inventorySlot));
        ContainerRegistry.openAndSendContainer(client, p);
    }

    @Override
    public void tick(Inventory inventory, int i, InventoryItem inventoryItem, GameClock gameClock, GameState gameState, Entity entity, TileEntity tileEntity, WorldSettings worldSettings, Consumer<InventoryItem> consumer) {

    }

    @Override
    public int getInternalInventorySize() {
        return 2;
    }

    //All the stuff for projectile handling

    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (this.getAnimInverted(item)) {
            drawOptions.swingRotationInv(attackProgress);
        } else {
            drawOptions.swingRotation(attackProgress);
        }

    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "elderlywandtip"));
        return tooltips;
    }

    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        return this.applyInaccuracy(attackerMob, item, new Point(target.getX(), target.getY()));
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Inventory inv = getInternalInventory(item);

        /*
        //inv.getIt

        //ElderlyWandProjectile projectile = new ElderlyWandProjectile(attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom((long)seed));
        if (attackerMob.isAttackHandlerFrom(item, slot)) {
            ((MouseProjectileAttackHandler)attackerMob.getAttackHandler()).addProjectiles(new FollowingProjectile[]{projectile});
        } else {
            attackerMob.startAttackHandler(new MouseProjectileAttackHandler(attackerMob, slot, this.getAttackRange(item), new FollowingProjectile[]{projectile}));
        }

        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        this.consumeMana(attackerMob, item);

         */
        return item;
    }

    protected SoundSettings getAttackSound() {
        return (new SoundSettings(GameResources.magicbolt2)).volume(0.8F);
    }
}
