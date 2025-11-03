//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package arcanist.tiles;

import arcanist.entities.PackagerObjectEntity;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle.GType;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PackagerObject extends GameObject implements SettlementWorkstationObject {
    public GameTexture texture;

    public PackagerObject() {
        super(new Rectangle(32, 32));
        this.setItemCategory(new String[]{"objects", "craftingstations"});
        this.setCraftingCategory(new String[]{"craftingstations"});
        this.isLightTransparent = true;
        this.roomProperties.add("metalwork");
        this.lightHue = 50.0F;
        this.lightSat = 0.2F;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null && forgeObjectEntity.isFuelRunning() ? 100 : 0;
    }

    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        if (GameRandom.globalRandom.nextInt(10) == 0) {
            PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
            if (forgeObjectEntity != null && forgeObjectEntity.isFuelRunning()) {
                int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
                level.entityManager.addParticle((float)(tileX * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(tileY * 32 + 32), GType.COSMETIC).smokeColor().heightMoves((float)startHeight, (float)(startHeight + 20)).lifeTime(1000);
            }
        }

    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/forge");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20) : new Rectangle(x * 32 + 6, y * 32 + 2, 20, 28);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        boolean isFueled = false;
        PackagerObjectEntity objectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (objectEntity != null) {
            isFueled = objectEntity.isFuelRunning();
        }

        int spriteHeight = this.texture.getHeight() - 32;
        final TextureDrawOptions options = this.texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).addObjectDamageOverlay(this, level, tileX, tileY).light(light).pos(drawX, drawY - (spriteHeight - 32));
        final TextureDrawOptionsEnd flame;
        if (isFueled && rotation == 2) {
            int spriteX = (int)(level.getWorldEntity().getWorldTime() % 1200L / 300L);
            flame = this.texture.initDraw().sprite(spriteX, spriteHeight / 32, 32).light(light).pos(drawX, drawY);
        } else {
            flame = null;
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
                if (flame != null) {
                    flame.draw();
                }

            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteHeight = this.texture.getHeight() - 32;
        this.texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).alpha(alpha).draw(drawX, drawY - (spriteHeight - 32));
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new PackagerObjectEntity(level, x, y);
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (level.isServer()) {
            CraftingStationContainer.openAndSendContainer(ContainerRegistry.FUELED_PROCESSING_STATION_CONTAINER, player.getServerClient(), level, x, y);
        }

    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "packager_tip"));
        return tooltips;
    }

    public PackagerObjectEntity getForgeObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        return objectEntity instanceof PackagerObjectEntity ? (PackagerObjectEntity)objectEntity : null;
    }

    public Stream<Recipe> streamSettlementRecipes(Level level, int tileX, int tileY) {
        PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? Recipes.streamRecipes(forgeObjectEntity.techs) : Stream.empty();
    }

    public boolean isProcessingInventory(Level level, int tileX, int tileY) {
        return true;
    }

    public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity == null) {
            return false;
        } else {
            return forgeObjectEntity.getExpectedResults().crafts < 10 && (forgeObjectEntity.isFuelRunning() || forgeObjectEntity.canUseFuel());
        }
    }

    public int getMaxCraftsAtOnce(Level level, int tileX, int tileY, Recipe recipe) {
        return 5;
    }

    public InventoryRange getProcessingInputRange(Level level, int tileX, int tileY) {
        PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getInputInventoryRange() : null;
    }

    public InventoryRange getProcessingOutputRange(Level level, int tileX, int tileY) {
        PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getOutputInventoryRange() : null;
    }

    public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level level, int tileX, int tileY) {
        PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getCurrentAndExpectedResults().items : new ArrayList();
    }

    public SettlementRequestOptions getFuelRequestOptions(Level level, int tileX, int tileY) {
        return new SettlementRequestOptions(5, 10) {
            public SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords records) {
                return ((SettlementStorageGlobalIngredientIDIndex)records.getIndex(SettlementStorageGlobalIngredientIDIndex.class)).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anylog"));
            }
        };
    }

    public InventoryRange getFuelInventoryRange(Level level, int tileX, int tileY) {
        PackagerObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null) {
            Inventory inventory = forgeObjectEntity.getInventory();
            if (inventory != null && forgeObjectEntity.fuelSlots > 0) {
                return new InventoryRange(inventory, 0, forgeObjectEntity.fuelSlots - 1);
            }
        }

        return null;
    }

    protected boolean shouldPlayInteractSound(Level level, int tileX, int tileY) {
        return true;
    }

    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.defaultOpen;
    }
}
