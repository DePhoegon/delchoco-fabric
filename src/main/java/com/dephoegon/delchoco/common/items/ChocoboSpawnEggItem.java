package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delchoco.common.init.ModEntities.CHOCOBO_ENTITY;
import static java.lang.Math.random;

public class ChocoboSpawnEggItem extends Item {
    private final ChocoboColor color;
    public ChocoboSpawnEggItem(Settings settings, ChocoboColor color) {
        super(settings);
        this.color = color;
    }
    private Text name(@NotNull ItemStack egg) { return egg.getName(); }
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        World worldIn = context.getWorld();
        if (worldIn.isClient()) { return ActionResult.SUCCESS; }
        BlockEntity blockEntity;
        final BlockPos pos = context.getBlockPos();
        final PlayerEntity player = context.getPlayer();
        BlockState blockState = worldIn.getBlockState(pos);
        if (blockState.isOf(Blocks.SPAWNER) && (blockEntity = worldIn.getBlockEntity(pos)) instanceof MobSpawnerBlockEntity) {
            MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
            mobSpawnerLogic.setEntityId(CHOCOBO_ENTITY);
            blockEntity.markDirty();
            worldIn.updateListeners(pos, blockState, blockState, Block.NOTIFY_ALL);
            context.getStack().decrement(1);
            return ActionResult.CONSUME;
        } else {
            final Chocobo chocobo = CHOCOBO_ENTITY.create(worldIn);
            if (chocobo != null) {
                if (player != null) { if (player.isInSneakingPose()) { chocobo.setBreedingAge(-7500); } }

                chocobo.refreshPositionAndAngles(pos.getX() + .5, pos.getY() + 1.5F, pos.getZ() + .5, MathHelper.wrapDegrees(worldIn.random.nextFloat() * 360.0F), 0.0F);
                chocobo.headYaw = chocobo.getYaw();
                chocobo.bodyYaw = chocobo.getYaw();
                Text nameCheck = name(context.getStack());
                if (context.getStack().hasCustomName()) { chocobo.setCustomName(nameCheck); }
                chocobo.setMale(.50f > (float) random());
                chocobo.setChocobo(color);
                chocobo.setFromEgg(true);
                chocobo.setChocoboScale(chocobo.isMale(), 0, false);
                chocobo.initialize((ServerWorld) worldIn, worldIn.getLocalDifficulty(chocobo.getBlockPos().down()), SpawnReason.SPAWN_EGG, null, null);
                worldIn.spawnEntity(chocobo);
                chocobo.playAmbientSound();
                context.getStack().decrement(1);
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.SUCCESS;
    }
}