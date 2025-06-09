package com.dephoegon.delchoco.common.entities.spwanerColors;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Box;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.dephoegon.delchoco.aid.chocoboChecks.IS_OCEAN;
import static com.dephoegon.delchoco.aid.chocoboChecks.IS_SPARSE;
import static com.dephoegon.delchoco.common.init.ModItems.BLACK_CHOCOBO_SPAWN_EGG;
import static com.dephoegon.delchoco.common.init.ModItems.YELLOW_CHOCOBO_SPAWN_EGG;

public class Black extends Chocobo {
    public Black(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
    }
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, @Nullable EntityData spawnDataIn, @Nullable NbtCompound dataTag) {
        this.setChocobo(ChocoboColor.BLACK);
        return super.initialize(worldIn, difficultyIn, SpawnReason.SPAWNER, spawnDataIn, dataTag);
    }
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putByte(NBTKEY_CHOCOBO_COLOR, (byte) ChocoboColor.BLACK.ordinal());
    }
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setChocoboColor(ChocoboColor.BLACK);
    }
    public void onDeath(DamageSource source) {
        if (onDeathCheck(1000, 85)) {  this.dropStack(new ItemStack(BLACK_CHOCOBO_SPAWN_EGG)); }
        super.onDeath(source);
    }
    public boolean isPersistent() { return this.isTamed() || this.isCustomNameVisible(); }
}