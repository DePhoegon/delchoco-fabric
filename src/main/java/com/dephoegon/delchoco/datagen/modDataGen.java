package com.dephoegon.delchoco.datagen;

import com.dephoegon.delchoco.common.world.worldgen.ModFeatureConfigs;
import com.dephoegon.delchoco.common.world.worldgen.ModPlacements;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.NotNull;

public class modDataGen implements DataGeneratorEntrypoint {
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(modWorldGen::new);
    }
    public void buildRegistry(@NotNull RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModFeatureConfigs::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacements::bootstrap);
    }
}