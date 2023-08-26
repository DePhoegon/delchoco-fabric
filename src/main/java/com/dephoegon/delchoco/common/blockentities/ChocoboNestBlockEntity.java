package com.dephoegon.delchoco.common.blockentities;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.world.StaticGlobalVariables;
import com.dephoegon.delchoco.common.blocks.ChocoboEggBlock;
import com.dephoegon.delchoco.common.blocks.StrawNestBlock;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.breeding.BreedingHelper;
import com.dephoegon.delchoco.common.entities.breeding.ChocoboBreedInfo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboInventory;
import com.dephoegon.delchoco.common.init.ModItems;
import com.dephoegon.delchoco.common.inventory.NestContainer;
import com.dephoegon.delchoco.common.items.ChocoboEggBlockItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.ChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.dEGG_HATCH;
import static com.dephoegon.delchoco.common.blocks.ChocoboEggBlock.NBTKEY_BREEDINFO;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboBreedInfo.getFromNbtOrDefault;

public class ChocoboNestBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public ChocoboNestBlockEntity(BlockPos pos, BlockState state) { super(ModItems.STRAW_NEST_BLOCK_ENTITY, pos, state); }
    public static final Identifier UPDATE_PACKET_ID = new Identifier(DelChoco.DELCHOCO_ID, "block_entity_update");
    private static class CheckOffset {
        Vec3i offset;
        boolean shouldBeAir;

        CheckOffset(Vec3i offset, boolean shouldBeAir) {
            this.offset = offset;
            this.shouldBeAir = shouldBeAir;
        }
    }
    private final static CheckOffset[] SHELTER_CHECK_OFFSETS = new CheckOffset[] {
            new CheckOffset(new Vec3i(0, 1, 0), true),
            new CheckOffset(new Vec3i(0, 2, 0), true),
            new CheckOffset(new Vec3i(-1, 3, -1), false),
            new CheckOffset(new Vec3i(-1, 3, 0), false),
            new CheckOffset(new Vec3i(-1, 3, 1), false),
            new CheckOffset(new Vec3i(0, 3, -1), false),
            new CheckOffset(new Vec3i(0, 3, 0), false),
            new CheckOffset(new Vec3i(0, 3, 1), false),
            new CheckOffset(new Vec3i(1, 3, -1), false),
            new CheckOffset(new Vec3i(1, 3, 0), false),
            new CheckOffset(new Vec3i(1, 3, 1), false),
    };

    @SuppressWarnings("WeakerAccess")
    public static final String NBTKEY_IS_SHELTERED = "IsSheltered";
    @SuppressWarnings("WeakerAccess")
    public static final String NBTKEY_TICKS = "Ticks";
    @SuppressWarnings("WeakerAccess")
    public static final String NBTKEY_NEST_INVENTORY = "Inventory";

    private final ChocoboInventory inventory = new ChocoboInventory(1, null) {
        @Contract(pure = true)
        public boolean isValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof ChocoboEggBlockItem;
        }
    };
    private boolean isSheltered;
    private int ticks = 0;

    public static void serverTick(World world, BlockPos pos, BlockState state, @NotNull ChocoboNestBlockEntity blockEntity) {
        blockEntity.ticks++;
        if (blockEntity.ticks > 1_000_000) {
            blockEntity.ticks = 0;
        }
        boolean changed = false;

        if (blockEntity.ticks % 5 == 0 && !blockEntity.getEggItemStack().isEmpty()) {
            changed = blockEntity.updateEgg();
        }
        if (blockEntity.ticks % 5 == 0 && blockEntity.getEggItemStack().isEmpty() && blockEntity.getCachedState().get(StrawNestBlock.HAS_EGG)) {
            blockEntity.onInventoryChanged();
        }
        if (blockEntity.ticks % 200 == 100) {
            changed |= blockEntity.updateSheltered();
        }
        if (changed) {
            world.setBlockState(pos, state);
        }
    }
    private boolean updateEgg() {
        ItemStack egg = this.getEggItemStack();
        if (ChocoboEggBlock.isNotChocoboEgg(egg)) { return false; }
        if (!egg.hasNbt()) { egg.setSubNbt(NBTKEY_BREEDINFO, getFromNbtOrDefault(null).serialize()); }
        NbtCompound nbt = egg.getOrCreateSubNbt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE);
        int time = nbt.getInt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE_TIME);
        time += this.isSheltered ? 2 : 1;
        nbt.putInt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE_TIME, time);
        if (time < ChocoConfigGet(StaticGlobalVariables.getEggHatchTimeTicks(), dEGG_HATCH.getDefault())) { return false; }

        // egg is ready to hatch
        ChocoboBreedInfo breedInfo = ChocoboBreedInfo.getFromNbtOrDefault(egg.getSubNbt(NBTKEY_BREEDINFO));
        Chocobo baby = BreedingHelper.createChild(breedInfo, this.world, egg);
        if (baby == null) { return false; }
        baby.updatePositionAndAngles(this.getPos().getX() + 0.5, this.getPos().getY() + 0.2, this.getPos().getZ() + 0.5, 0.0F, 0.0F);
        assert this.world != null;
        this.world.spawnEntity(baby);

        Random random = baby.getRandom();
        for (int i = 0; i < 7; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            double d3 = random.nextDouble() * baby.getWidth() * 2.0D - baby.getWidth();
            double d4 = 0.5D + random.nextDouble() * baby.getHeight();
            double d5 = random.nextDouble() * baby.getWidth() * 2.0D - baby.getWidth();
            this.world.addParticle(ParticleTypes.HEART, baby.getX() + d3, baby.getY() + d4, baby.getZ() + d5, d0, d1, d2);
        }
        this.setEggItemStack(ItemStack.EMPTY);
        return true;
    }
    private boolean updateSheltered() {
        // TODO: Make this better, use "can see sky" for shelter detection
        boolean sheltered = isSheltered();
        if (this.isSheltered != sheltered) {
            this.isSheltered = sheltered;
            return true;
        }
        return false;
    }
    public ItemStack getEggItemStack() { return this.inventory.getStack(0); }
    public void setEggItemStack(@NotNull ItemStack itemStack) {
        if (itemStack.isEmpty()) { this.inventory.setStack(0, ItemStack.EMPTY); }
        else if (!ChocoboEggBlock.isNotChocoboEgg(itemStack)) {
            this.inventory.setStack(0, itemStack);
            if (itemStack.hasNbt()) {
                NbtCompound nbt = itemStack.getOrCreateSubNbt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE);
                int time = nbt.getInt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE_TIME);
                nbt.putInt(ChocoboEggBlock.NBTKEY_HATCHINGSTATE_TIME, time);
            }
        }
    }
    public Inventory getInventory() { return this.inventory; }
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        this.isSheltered = nbt.getBoolean(NBTKEY_IS_SHELTERED);
        this.ticks = nbt.getInt(NBTKEY_TICKS);
        this.inventory.load(nbt.getCompound(NBTKEY_NEST_INVENTORY));
    }
    public void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean(NBTKEY_IS_SHELTERED, this.isSheltered);
        nbt.putInt(NBTKEY_TICKS, this.ticks);
        nbt.put(NBTKEY_NEST_INVENTORY, this.inventory.save());
    }
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return BlockEntityUpdateS2CPacket.create(this);
    }
    public @NotNull NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        this.readNbt(nbt);
        return nbt;
    }
    public ScreenHandler createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) { return new NestContainer(id, playerInventory, this); } // Nest Container
    // to be done
    public Text getDisplayName() { return new TranslatableText(DelChoco.DELCHOCO_ID + ".container.nest"); }
    public void onInventoryChanged() {
        this.markDirty();
        BlockState newState = ModItems.STRAW_NEST.getDefaultState().with(StrawNestBlock.HAS_EGG, !this.getEggItemStack().isEmpty());
        Objects.requireNonNull(this.getWorld()).setBlockState(this.getPos(), newState);
    }
    public boolean isSheltered() {
        boolean sheltered = true;
        for (CheckOffset checkOffset : SHELTER_CHECK_OFFSETS) {
            assert world != null;
            BlockPos spot = this.getPos().add(checkOffset.offset);
            if (world.getBlockState(spot).getCollisionShape(world, spot).isEmpty() != checkOffset.shouldBeAir) {
                sheltered = false;
                break;
            }
        }
        return sheltered;
    }
}
