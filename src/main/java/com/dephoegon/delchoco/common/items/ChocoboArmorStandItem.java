package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.common.entities.ChocoboArmorStand;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboTweakedSnapShots;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ChocoboArmorStandItem extends Item {
    private final boolean isAdult;
    private final EntityType<ChocoboArmorStand> entityType;

    public ChocoboArmorStandItem(Settings properties, boolean isAdult, EntityType<ChocoboArmorStand> entityType) {
        super(properties);
        this.isAdult = isAdult;
        this.entityType = entityType;
    }

    @Override
    public @NotNull ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos placementPos = blockPos.offset(direction);
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();

        if (!world.isClient && player != null) {
            // Check if there's enough space to place the armor stand
            if (!world.isSpaceEmpty(null, entityType.createSimpleBoundingBox(
                    placementPos.getX() + 0.5, placementPos.getY(), placementPos.getZ() + 0.5))) {
                return ActionResult.FAIL;
            }

            // Create and place the armor stand
            ChocoboArmorStand armorStand = new ChocoboArmorStand(entityType, world);
            armorStand.setPosition(placementPos.getX() + 0.5, placementPos.getY(), placementPos.getZ() + 0.5);
            armorStand.setIsAdult(isAdult);

            // Set default scale
            armorStand.setScale(ChocoboTweakedSnapShots.setChocoScale(world.random.nextBoolean()));

            // Set rotation to face the player
            float yaw = player.getYaw();
            armorStand.setYaw(yaw + 180.0F);

            world.spawnEntity(armorStand);

            // Play placement sound
            world.playSound(null, placementPos, SoundEvents.ENTITY_ARMOR_STAND_PLACE,
                SoundCategory.BLOCKS, 0.75F, 0.8F);

            // Consume item if not in creative mode
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            return ActionResult.SUCCESS;
        }

        return world.isClient ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public @NotNull Text getName() { return Text.translatable(this.getTranslationKey()); }

    @Override
    public @NotNull Text getName(@NotNull ItemStack stack) {
        String baseKey = this.getTranslationKey();
        String typeKey = isAdult ? "adult" : "chichobo";
        return Text.translatable(baseKey + "." + typeKey);
    }

    public boolean isAdult() { return isAdult; }
}
