package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.subTypes.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    // Main chocobo entity - summonable
    public static final EntityType<Chocobo> CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Chocobo::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());

    // Color-specific spawner entities - NOT summonable (kept for Spawner use)
    public static final EntityType<Yellow> YELLOW_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "yellow_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Yellow::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Green> GREEN_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "green_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Green::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Blue> BLUE_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "blue_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Blue::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<White> WHITE_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "white_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, White::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Black> BLACK_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "black_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Black::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Gold> GOLD_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "gold_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Gold::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Pink> PINK_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "pink_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Pink::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Red> RED_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "red_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Red::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Purple> PURPLE_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "purple_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Purple::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());
    public static final EntityType<Flame> FLAME_CHOCOBO_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "flame_spawner_chocobo"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Flame::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(5).disableSummon().build());

    // Armor stand entity - NOT summonable
    public static final EntityType<ArmorStandChocobo> CHOCOBO_ARMOR_STAND_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo_armor_stand"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, ArmorStandChocobo::new).dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackRangeChunks(4).trackedUpdateRate(Integer.MAX_VALUE).disableSummon().build());

    public static void registerAttributes() {
        DelChoco.LOGGER.info("Registering Chocobo Attributes");
        // Main chocobo entity
        FabricDefaultAttributeRegistry.register(CHOCOBO_ENTITY, Chocobo.createAttributes());

        // Color-specific spawner entities (for internal use)
        FabricDefaultAttributeRegistry.register(YELLOW_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(GREEN_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(BLUE_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(WHITE_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(BLACK_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(GOLD_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(PINK_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(RED_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(PURPLE_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(FLAME_CHOCOBO_ENTITY, Chocobo.createAttributes());
        FabricDefaultAttributeRegistry.register(CHOCOBO_ARMOR_STAND_ENTITY, ArmorStandChocobo.createAttributes());
    }
}