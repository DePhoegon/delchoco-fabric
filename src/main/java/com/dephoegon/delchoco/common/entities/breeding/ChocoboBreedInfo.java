package com.dephoegon.delchoco.common.entities.breeding;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChocoboBreedInfo {
    public final static String NBTKEY_MOTHER_STAT_SNAPSHOT = "mother";
    public final static String NBTKEY_FATHER_STAT_SNAPSHOT = "father";
    private ChocoboStatSnapshot mother = ChocoboStatSnapshot.DEFAULT;
    private ChocoboStatSnapshot father = ChocoboStatSnapshot.DEFAULT;

    public ChocoboStatSnapshot getMother() { return this.mother; }
    public ChocoboStatSnapshot getFather() { return this.father; }
    public ChocoboBreedInfo(ChocoboStatSnapshot mother, ChocoboStatSnapshot father) {
        this.mother = mother;
        this.father = father;
    }
    public ChocoboBreedInfo(@NotNull NbtCompound nbt) {
        if (nbt.contains(NBTKEY_MOTHER_STAT_SNAPSHOT)) { this.mother = new ChocoboStatSnapshot(nbt.getCompound(NBTKEY_MOTHER_STAT_SNAPSHOT)); }
        if (nbt.contains(NBTKEY_FATHER_STAT_SNAPSHOT)) { this.father = new ChocoboStatSnapshot(nbt.getCompound(NBTKEY_FATHER_STAT_SNAPSHOT)); }
    }
    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        nbt.put(NBTKEY_MOTHER_STAT_SNAPSHOT, this.mother.serialize());
        nbt.put(NBTKEY_FATHER_STAT_SNAPSHOT, this.father.serialize());
        return nbt;
    }
    public static @NotNull ChocoboBreedInfo getFromNbtOrDefault(@Nullable NbtCompound nbt) { return nbt != null ? new ChocoboBreedInfo(nbt) : new ChocoboBreedInfo(new ChocoboSnap().TWEAKED_DEFAULT, new ChocoboSnap().TWEAKED_DEFAULT); }
}