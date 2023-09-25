package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
    public static final EntityType<Chocobo> CHOCOBO_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(DelChoco.DELCHOCO_ID, "chocobo"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Chocobo::new).spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.2f, 2.8f)).trackedUpdateRate(64).trackRangeChunks(8).build());
    public static void registerAttributes() {
        DelChoco.LOGGER.info("Registering Chocobo Attributes");
        FabricDefaultAttributeRegistry.register(CHOCOBO_ENTITY, Chocobo.createAttributes());
    }
}