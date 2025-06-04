package com.dephoegon.delchoco.common.entities.properties;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.List;
import java.util.Optional;

public class MemoryTypes {
    public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_ATTACKABLE_PLAYER =new MemoryModuleType<>(Optional.empty());
    public static final MemoryModuleType<List<LivingEntity>> NEAREST_VISIBLE_LIVING_ENTITIES = new MemoryModuleType<>(Optional.empty());

    public static void registerAll() {
        Registry.register(Registries.MEMORY_MODULE_TYPE, "nearest_visible_attackable_player", NEAREST_VISIBLE_ATTACKABLE_PLAYER);
        Registry.register(Registries.MEMORY_MODULE_TYPE, "nearest_visible_living_entities", NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}