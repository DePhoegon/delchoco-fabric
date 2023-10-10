package com.dephoegon.delchoco.mixin;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin  {
    @Shadow
    private int spawnDelay;
    @Shadow
    private MobSpawnerEntry spawnEntry;
    @Shadow
    private int maxNearbyEntities;
    @Shadow
    private int spawnRange;
    @Shadow
    private int spawnCount;
    private String ChocoVariant = "";
    public String getChocoVariant() { return this.ChocoVariant; }
    public void setChocoVariant(String string) { this.ChocoVariant = string; }
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void serverTick(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        boolean isChocobo = false;
        for (int i = 0; i < this.spawnCount; ++i) {
            if (--this.spawnDelay <= 0) {
                boolean blCheck = false;
                NbtCompound nbtCheck = this.spawnEntry.getNbt();
                NbtList nbtListCheck = nbtCheck.getList("Pos", NbtElement.DOUBLE_TYPE);
                double fc;
                int j = nbtListCheck.size();
                Random worldRandom = world.getRandom();
                double dc = j >= 1 ? nbtListCheck.getDouble(0) : (double) pos.getX() + (worldRandom.nextDouble() - worldRandom.nextDouble()) * (double) this.spawnRange + 0.5;
                double ec = j >= 2 ? nbtListCheck.getDouble(1) : (double) (pos.getY() + worldRandom.nextInt(3) - 1);
                double d2c = fc = j >= 3 ? nbtListCheck.getDouble(2) : (double) pos.getZ() + (worldRandom.nextDouble() - worldRandom.nextDouble()) * (double) this.spawnRange + 0.5;
                BlockPos blockPosCheck = new BlockPos(dc, ec, fc);
                Entity entityCheck = EntityType.loadEntityWithPassengers(nbtCheck, world, entity -> {
                    entity.refreshPositionAndAngles(dc, ec, fc, entity.getYaw(), entity.getPitch());
                    return entity;
                });
                if (entityCheck == null) {
                    ((MobSpawnerLogicAccessor) this).callUpdateSpawns(world, pos);
                    return;
                }
                if (entityCheck instanceof Chocobo chocobo) {
                    isChocobo = true;
                    if (this.ChocoVariant.isEmpty()) { this.ChocoVariant = ChocoboColor.YELLOW.getColorName(); }
                    ((Chocobo) entityCheck).setChocobo(ChocoboColor.valueOf(this.getChocoVariant()));
                    int k = world.getNonSpectatingEntities(entityCheck.getClass(), new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(this.spawnRange)).size();
                    if (k >= this.maxNearbyEntities) {
                        ((MobSpawnerLogicAccessor) this).callUpdateSpawns(world, pos);
                        return;
                    }
                    entityCheck.refreshPositionAndAngles(entityCheck.getX(), entityCheck.getY(), entityCheck.getZ(), worldRandom.nextFloat() * 360.0f, 0f);
                    if (this.spawnEntry.getCustomSpawnRules().isEmpty() && !chocobo.canSpawn(world, SpawnReason.SPAWNER) || !chocobo.canSpawn(world)) continue;
                    if (this.spawnEntry.getNbt().getSize() == 1 && this.spawnEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
                        chocobo.initialize(world, world.getLocalDifficulty(chocobo.getBlockPos()), SpawnReason.SPAWNER, null, null);
                    }
                    if (!world.spawnNewEntityAndPassengers(entityCheck)) {
                        ((MobSpawnerLogicAccessor) this).callUpdateSpawns(world, pos);
                        return;
                    }
                    world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
                    world.emitGameEvent(entityCheck, GameEvent.ENTITY_PLACE, blockPosCheck);
                    chocobo.playSpawnEffects();
                    blCheck = true;
                } else { isChocobo = false; }
                if (blCheck) {
                    ((MobSpawnerLogicAccessor) this).callUpdateSpawns(world, pos);
                }
            }
        }
        if (isChocobo) { ci.cancel(); }
    }
}