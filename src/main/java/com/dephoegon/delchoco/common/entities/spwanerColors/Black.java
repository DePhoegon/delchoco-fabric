package com.dephoegon.delchoco.common.entities.spwanerColors;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.dephoegon.delchoco.common.init.ModItems.BLACK_CHOCOBO_SPAWN_EGG;

public class Black extends Chocobo {
    public Black(EntityType<? extends Chocobo> entityType, World world) {
        super(entityType, world);
    }
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, @Nullable EntityData spawnDataIn, @Nullable NbtCompound dataTag) {
        this.setChocobo(ChocoboColor.BLACK);
        return super.initialize(worldIn, difficultyIn, SpawnReason.SPAWNER, spawnDataIn, dataTag);
    }
    public void writeCustomDataToNbt(@NotNull NbtCompound compound) {
        this.setChocoboColor(ChocoboColor.BLACK); // Ensure the color is set before writing
        super.writeCustomDataToNbt(compound);
    }
    public void readCustomDataFromNbt(@NotNull NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setChocoboColor(ChocoboColor.BLACK);
    }
    public void onDeath(DamageSource source) {
        if (onDeathCheck(1000, 85)) {  this.dropStack(new ItemStack(BLACK_CHOCOBO_SPAWN_EGG)); }
        super.onDeath(source);
    }
    public boolean isPersistent() { return this.isTamed() || this.isCustomNameVisible(); }
}