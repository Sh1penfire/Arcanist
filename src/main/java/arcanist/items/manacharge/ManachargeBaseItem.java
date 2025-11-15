package arcanist.items.manacharge;

import arcanist.content.*;
import arcanist.entities.projectiles.manacharge.ModularProjectileBase;
import arcanist.util.Formatter;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
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
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ManachargeBaseItem extends ToolItem implements InternalInventoryItemInterface, TickItem, ItemInteractAction {

    public static int i;
    public static GNDItemMap manachargeData;

    public float chargeCost, chargeRate;

    //Theese get put into the base stats
    public int shots, bursts;
    public float inaccuracy;

    /*
    shots
    bursts
    inaccuracy
     */

    //Modifications to the projectile, please don't put manacharge data in here~ (or do, it's up to you)
    public GNDItemMap baseStats = new GNDItemMap(), abilityStats = new GNDItemMap();

    public String tooltipKey;

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
        tooltipKey = "manacharge_tip";
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

    public void floatAbilityStat(ModLensModifiers.ModifierEntry entry, float amount){
        abilityStats.setFloat(entry.id, amount);
    }

    public void boolAbilityStat(ModLensModifiers.ModifierEntry entry, boolean value){
        abilityStats.setBoolean(entry.id, value);
    }

    @Override
    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        InventoryItem inventoryItem = super.getDefaultItem(player, amount);
        GNDItemMap data = inventoryItem.getGndData().setBoolean(GNDKeys.MODIFIER_ITEM, true);
        inventoryItem.setGndData(data);
        return inventoryItem;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", tooltipKey));
        GNDItemMap data = item.getGndData();
        return Formatter.formatStats(tooltips, data, false);
    };

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.magicItemEnchantments.contains(enchantment.getID());
    }

    //All the inventory stuff
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

    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        InternalInventoryItemInterface.super.saveInternalInventory(item, inventory);
        sumEffects(item);
    }

    //Look it's not the most optimised but it's easier to work with and that's what I'd rather have if somebody comes knocking about how tf this works
    //Im doing a separate run for abilities since im not modifying them
    public void sumEffects(InventoryItem manacharge){
        Inventory inv = getInternalInventory(manacharge);
        if(inv.getUsedSlots() < 2) return;

        ProjectileGeneratorItem generator = generatorItem(manacharge);

        manachargeData = manacharge.getGndData();
        GNDItemMap genData = generatorInventoryItem(manacharge).getGndData();
        GNDItemMap ampData = ampInventoryItem(manacharge).getGndData();

        //(base + gen.flat + amp.flat) * gen.multi * amp.multi
        //Done in two separate phases
        ModLensModifiers.flat.forEach((entry) -> {
            float value = generator.baseStats.getFloat(entry.id) + baseStats.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX) + genData.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX) + ampData.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX);
            manachargeData.setFloat(entry.id, value);

            float abilityValue = abilityStats.getFloat(entry.id) + baseStats.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX) + genData.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX) + ampData.getFloat(entry.id + GNDKeys.ADDITIVE_SUFFIX);
            manachargeData.setFloat(entry.id + GNDKeys.ABILITY_SUFFIX, abilityValue);
        });
        ModLensModifiers.multi.forEach((entry) -> {
            float value = manachargeData.getFloat(entry.id) * (1 + baseStats.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX)) * (1 + genData.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX)) * (1 + ampData.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX));
            manachargeData.setFloat(entry.id, value);

            float abilityValue = manachargeData.getFloat(entry.id + GNDKeys.ABILITY_SUFFIX) * (1 + baseStats.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX)) * (1 + genData.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX)) * (1 + ampData.getFloat(entry.id + GNDKeys.MULTIPLICATIVE_SUFFIX));
            manachargeData.setFloat(entry.id + GNDKeys.ABILITY_SUFFIX, abilityValue);
        });
        ModLensModifiers.flags.forEach((entry) -> {
            boolean flag = genData.getBoolean(entry.id);
            if(flag) manachargeData.setBoolean(entry.id, flag);
        });

        manacharge.getGndData().addAll(manachargeData);
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

    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        return this.applyInaccuracy(attackerMob, item, new Point(target.getX(), target.getY()));
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
        drawOptions.itemBeforeHand();
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

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {

        manachargeData = item.getGndData();

        if(level.isServer()){
            float charge = item.getGndData().getFloat("acn_charge", 0);
            charge += Math.max(1, 1 + getCritChance(item, attackerMob)) * chargeRate;
            while(charge >= 100){
                charge -= 100;
                attackerMob.buffManager.addBuff(new ActiveBuff(ModBuffs.manacharge, attackerMob, 10 * 1000, null), level.isServer());
            }
            manachargeData.setFloat("acn_charge", charge);
        }

        ProjectileGeneratorItem generator = generatorItem(item);


        GameRandom random = new GameRandom(seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));

        float inaccuracy = manachargeData.getFloat(ModLensModifiers.inaccuracy.id);
        int shots = (int) manachargeData.getFloat(ModLensModifiers.shots.id);
        for(int i = 0; i < Math.max(1, 1 + shots); i++){
            Projectile projectile = getProjectile(item.getGndData(), generator.projectileType, level, x, y, attackerMob);
            projectile.getUniqueID(random);
            attackerMob.addAndSendAttackerProjectile(projectile, 20, (spreadRandom.nextFloat() - 0.5f) * inaccuracy);
        }

        this.consumeMana(attackerMob, item);

        return item;
    }

    public ModularProjectileBase getProjectile(GNDItemMap stats, String projectileID, Level level, float x, float y, Mob owner){
        float velocity = stats.getFloat(ModLensModifiers.speed.id);
        int range = (int) stats.getFloat(ModLensModifiers.range.id);
        GameDamage damage = new GameDamage(stats.getFloat(ModLensModifiers.damage.id));
        int knockback = (int) stats.getFloat(ModLensModifiers.knockback.id);
        ModularProjectileBase proj = (ModularProjectileBase) ProjectileRegistry.getProjectile(projectileID, level, owner.getX(), owner.getY(), x, y, velocity, range, damage, knockback, owner);
        proj.setStats(stats);
        return proj;
    };

    //Not all manacharges will use this
    public ModularProjectileBase getAbilityProjectile(GNDItemMap stats, String projectileID, Level level, float x, float y, Mob owner){
        float velocity = stats.getFloat(ModLensModifiers.speed.id + GNDKeys.ABILITY_SUFFIX);
        int range = (int) stats.getFloat(ModLensModifiers.range.id + GNDKeys.ABILITY_SUFFIX);
        GameDamage damage = new GameDamage(stats.getFloat(ModLensModifiers.damage.id + GNDKeys.ABILITY_SUFFIX));
        int knockback = (int) stats.getFloat(ModLensModifiers.knockback.id + GNDKeys.ABILITY_SUFFIX);
        ModularProjectileBase proj = (ModularProjectileBase) ProjectileRegistry.getProjectile(projectileID, level, owner.getX(), owner.getY(), x, y, velocity, range, damage, knockback, owner);
        proj.fromAbility = true;
        proj.setStats(stats);
        return proj;
    };

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
}
