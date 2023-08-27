package com.dephoegon.delchoco.common.blockentities;

import com.dephoegon.delchoco.aid.ChocoList;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboBreedInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class ChocoboEggBlockEntity extends BlockEntity {
    public final static String NBTKEY_BREEDINFO = "BreedInfo";
    private ChocoboBreedInfo breedInfo;
    public ChocoboEggBlockEntity(BlockPos pos, BlockState state) { super(ChocoList.CHOCOBO_EGG_BLOCK_ENTITY, pos, state); }
    public void readNbt(@NotNull NbtCompound compound) {
        super.readNbt(compound);
        this.breedInfo = new ChocoboBreedInfo(compound.getCompound(NBTKEY_BREEDINFO));
    }
    public void writeNbt(@NotNull NbtCompound compound) {
        super.writeNbt(compound);
        if (this.breedInfo != null) { compound.put(NBTKEY_BREEDINFO, this.breedInfo.serialize()); }
    }
    public @NotNull NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        this.writeNbt(nbt);
        return nbt;
    }
    public ChocoboBreedInfo getBreedInfo() { return this.breedInfo; }
    public void setBreedInfo(ChocoboBreedInfo breedInfo) { this.breedInfo = breedInfo; }
}