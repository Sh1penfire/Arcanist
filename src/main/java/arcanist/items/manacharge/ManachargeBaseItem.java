package arcanist.items.manacharge;

import arcanist.content.ModBuffs;
import arcanist.content.ModContainers;
import arcanist.content.ModSounds;
import arcanist.entities.projectiles.manacharge.ModularProjectileBase;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.*;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.EnchantingScrollContainer;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FrostStaffProjectileToolItem;
import necesse.inventory.item.toolItem.summonToolItem.MagicBranchSummonToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ManachargeBaseItem extends ToolItem implements InternalInventoryItemInterface, TickItem, ItemInteractAction {

    public static int i;

    public float chargeCost, chargeRate;
    public HashMap<Integer, LensItem> lensIndexes = new HashMap<>();
    public HashMap<Integer, Float> multi = new HashMap<>();

    public ManachargeBaseItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory(new String[]{"equipment", "tools"});
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
        attackXOffset = 16;
        attackYOffset = 18;
        chargeCost = 3;
        chargeRate = 5;
        damageType = DamageTypeRegistry.MAGIC;
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.magicItemEnchantments.contains(enchantment.getID());
    }

    @Override
    public Inventory getNewInternalInventory(InventoryItem item) {
        return InternalInventoryItemInterface.super.getNewInternalInventory(item);
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

    public void tick(Inventory inventory, int slot, InventoryItem item, GameClock clock, GameState state, Entity entity, TileEntity tileEntity, WorldSettings worldSettings, Consumer<InventoryItem> setItem) {
        this.tickInternalInventory(item, clock, state, entity, tileEntity, worldSettings);
    }

    @Override
    public int getInternalInventorySize() {
        return 2;
    }

    //All the stuff for projectile handling

    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return (generatorInventoryItem(item) == null || ampInventoryItem(item) == null) ? "" : null;
    }

    public boolean getConstantUse(InventoryItem item) {
        return true;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "elderlywandtip"));
        return tooltips;
    }

    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        return this.applyInaccuracy(attackerMob, item, new Point(target.getX(), target.getY()));
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
        drawOptions.itemBeforeHand();
    }

    public ProjStats baseStats(InventoryItem item){
        return new ProjStats().applyPart(generatorItem(item).stats);
    }

    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        float charge = item.getGndData().getFloat("acn_charge", 0);
        if (charge > 0 || inInventory) {

            String amountString = String.valueOf((int) (charge)) + "%";
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString((float)(x + 28 - width), (float)(y + 16), amountString, tipFontOptions);
        }
    }

    public boolean canAbility(ItemAttackerMob attackerMob) {
        return attackerMob.buffManager.getStacks(ModBuffs.manacharge) >= chargeCost;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return canAbility(attackerMob);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        ability(level, x, y, attackerMob, item, seed);
        return item;
    }

    public void ability(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed){
        if (level.isServer()) {
            for(int i = 0; i < this.chargeCost; ++i) {
                attackerMob.buffManager.removeStack(ModBuffs.manacharge, true, true);
            }
        }

        if(level.isClient()) {
            SoundManager.playSound((new SoundSettings(ModSounds.sawCharge).volume(1)), (PrimitiveSoundEmitter) attackerMob);
        }
    }

    @Override
    public int getAttackCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.round(generatorItem(item).reload * (1.0f / this.getAttackSpeedModifier(item, attackerMob)));
    }

    public ProjStats stats(InventoryItem item, ProjectileGeneratorItem generator, Inventory generatorInv, AmpItem amplifier){

        lensIndexes.clear();
        multi.clear();
        for (i = 0; i < generatorInv.getSize(); i++) {
            InventoryItem lens = generatorInv.getItem(i);
            if(lens == null) continue;
            LensItem lensItem = (LensItem) lens.item;
            lensIndexes.put(i, lensItem);
            lensItem.amp.forEach((index, amount) -> {
                if(multi.containsKey(index + i)){
                    multi.put(index, multi.get(index + i) + amount);
                }
                else multi.put(index + i, amount);
            });
        }

        //Pass this off to each lens with the amplification values
        ProjStats stats = new ProjStats();

        lensIndexes.forEach((index, lensItem) -> {
            lensItem.modify(stats, multi.containsKey(index) ? multi.get(index) : 1, lensIndexes);
        });

        generator.applyStats(stats);

        amplifier.applyStats(stats);

        return stats;
    }

    public ProjStats abilityStats(InventoryItem item, Inventory generatorInv, AmpItem amplifier){

        lensIndexes.clear();
        multi.clear();
        for (i = 0; i < generatorInv.getSize(); i++) {
            InventoryItem lens = generatorInv.getItem(i);
            if(lens == null) continue;
            LensItem lensItem = (LensItem) lens.item;
            lensIndexes.put(i, lensItem);
            lensItem.amp.forEach((index, amount) -> {
                if(multi.containsKey(index + i)){
                    multi.put(index, multi.get(index + i) + amount);
                }
                else multi.put(index + i, amount);
            });
        }

        //Pass this off to each lens with the amplification values
        ProjStats stats = baseStats(item);

        lensIndexes.forEach((index, lensItem) -> {
            lensItem.modify(stats, multi.containsKey(index) ? multi.get(index) : 1, lensIndexes);
        });

        amplifier.applyStats(stats);

        return stats;
    }

    public InventoryItem generatorInventoryItem(InventoryItem manacharge){
        return getInternalInventory(manacharge).getItem(0);
    }
    public ProjectileGeneratorItem generatorItem(InventoryItem manacharge){
        return (ProjectileGeneratorItem) getInternalInventory(manacharge).getItem(0).item;
    }

    public InventoryItem ampInventoryItem(InventoryItem manacharge){
        return getInternalInventory(manacharge).getItem(1);
    }
    public AmpItem ampItem(InventoryItem manacharge){
        return (AmpItem) getInternalInventory(manacharge).getItem(1).item;
    }

    public void modifyProjectile(Level level, InventoryItem item, ItemAttackerMob attackerMob, int x, int y, ModularProjectileBase projectile, ProjStats stats, int seed){

        //Projectile stats
        projectile.setLevel(level);
        projectile.x = attackerMob.x;
        projectile.y = attackerMob.y;
        projectile.setTarget(x, y);
        projectile.speed = stats.speed;
        projectile.knockback = (int) stats.knockback;
        projectile.setDistance((int) (stats.range * (1 + stats.rangeMultiplier)));
        projectile.bouncing = (int) stats.bounce;
        projectile.piercing = (int) stats.pierce;
        projectile.objectDamageFract = stats.objectDamageFract;

        projectile.setOwner(attackerMob);
        projectile.resetUniqueID(new GameRandom((long)seed));

        GameDamage damage = new GameDamage(stats.damage * (1 + stats.damageMultiplier)).enchantedDamage(this.getEnchantment(item), ToolItemModifiers.DAMAGE, ToolItemModifiers.ARMOR_PEN, ToolItemModifiers.CRIT_CHANCE);
        GNDItemMap gndData = item.getGndData();
        float finalDamageModifier;
        if (gndData.hasKey("finalDamageMod")) {
            finalDamageModifier = Math.max(0.0F, gndData.getFloat("finalDamageMod"));
            damage = damage.modFinalMultiplier(finalDamageModifier);
        }

        if (gndData.hasKey("damageMod")) {
            finalDamageModifier = Math.max(0.0F, gndData.getFloat("damageMod"));
            damage = damage.modDamage(finalDamageModifier);
        }
        projectile.setDamage(damage);
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Inventory inv = getInternalInventory(item);
        InventoryItem generatorItem = inv.getItem(0);

        float charge = item.getGndData().getFloat("acn_charge", 0);
        charge += Math.max(1, 1 + getCritChance(item, attackerMob)) * chargeRate;
        while(charge >= 100){
            charge -= 100;
            attackerMob.buffManager.addBuff(new ActiveBuff(ModBuffs.manacharge, attackerMob, 10 * 1000, null), level.isServer());
        }
        item.getGndData().setFloat("acn_charge", charge);

        ProjectileGeneratorItem generator = (ProjectileGeneratorItem) generatorItem.item;

        Inventory generatorInv = generator.getInternalInventory(generatorItem);

        AmpItem amp = ampItem(item);

        ModularProjectileBase projectile = (ModularProjectileBase) ProjectileRegistry.getProjectile(generator.projectileType);

        //Pass this off to each lens with the amplification values
        ProjStats stats = stats(item, generator, generatorInv, amp);

        modifyProjectile(level, item, attackerMob, x, y, projectile, stats, seed);

        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        this.consumeMana(attackerMob, item);

        return item;
    }

    public GameDamage getFlatAttackDamage(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        if (gndData.hasKey("damage")) {
            GNDItem gndItem = gndData.getItem("damage");
            if (gndItem instanceof GNDItemGameDamage) {
                return ((GNDItemGameDamage)gndItem).damage;
            }

            if (gndItem instanceof GNDItem.GNDPrimitive) {
                float damage = ((GNDItem.GNDPrimitive)gndItem).getFloat();
                return new GameDamage(this.getDamageType(item), damage);
            }
        }

        return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)));
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            //this.playAttackSound(attackerMob);
        }

    }
    @Override
    protected SoundSettings getAttackSound() {
        return (new SoundSettings(GameResources.handgun)).volume(0.8f);
    }

    public SoundSettings getAbilitySound(){
        return (new SoundSettings(ModSounds.sawCharge).volume(1));
    }

    public static class ProjStats{
        public float damage;
        public float damageMultiplier;
        public float knockback;
        public float speed;
        public float homingPower;
        public float homingRange;
        public float objectDamageFract;
        public float lifestealFract;
        public float range;
        public float rangeMultiplier;

        public float
                pierce;
        public float bounce;

        //This calculcates the final stats as if it was a projectile gen or amp modifying the stats, so multipliers go directly onto the stats
        public ProjStats applyPart(ProjStats stats){
            damage += stats.damage;
            damage *= (1 + damageMultiplier);
            knockback += stats.knockback;
            speed += stats.speed;
            homingPower += stats.homingPower;
            homingRange += stats.homingRange;
            objectDamageFract *= (1 + stats.objectDamageFract);
            lifestealFract += stats.lifestealFract;

            range += stats.range;
            range *= (1 + stats.rangeMultiplier);

            pierce += stats.pierce;
            bounce += stats.bounce;
            return this;
        }

        public ProjStats setDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public ProjStats setKnockback(float knockback) {
            this.knockback = knockback;
            return this;
        }

        public ProjStats setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public ProjStats setHomingPower(float homingPower) {
            this.homingPower = homingPower;
            return this;
        }

        public ProjStats setHomingRange(float homingRange) {
            this.homingRange = homingRange;
            return this;
        }

        public ProjStats setObjectDamageFract(float objectDamageFract) {
            this.objectDamageFract = objectDamageFract;
            return this;
        }

        public ProjStats setLifestealFract(float lifestealFract) {
            this.lifestealFract = lifestealFract;
            return this;
        }

        public ProjStats setRange(float range) {
            this.range = range;
            return this;
        }

        public ProjStats setPierce(float pierce) {
            this.pierce = pierce;
            return this;
        }

        public ProjStats setBounce(float bounce) {
            this.bounce = bounce;
            return this;
        }
    }
}
