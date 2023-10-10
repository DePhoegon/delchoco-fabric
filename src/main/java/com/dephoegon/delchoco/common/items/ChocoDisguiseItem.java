package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dephoegon.delbase.item.ShiftingDyes.CLEANSE_SHIFT_DYE;
import static com.dephoegon.delchoco.aid.dyeList.CHOCO_COLOR_ITEMS;
import static com.dephoegon.delchoco.common.effects.ChocoboCombatEvents.armorColorMatch;
import static com.dephoegon.delchoco.common.effects.ChocoboCombatEvents.armorMatch;
import static com.dephoegon.delchoco.common.entities.properties.ChocoboColor.*;
import static com.dephoegon.delchoco.common.init.ModArmorMaterial.*;

@SuppressWarnings("ALL")
public class ChocoDisguiseItem extends ArmorItem implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    public final static String NBTKEY_COLOR = "Color";
    public final static String yellow = YELLOW.getColorName(); // default
    public final static String green = GREEN.getColorName();
    public final static String pink = PINK.getColorName();
    public final static String red = RED.getColorName();
    public final static String blue = BLUE.getColorName();
    public final static String gold = GOLD.getColorName();
    public final static String black = BLACK.getColorName();
    public final static String flame = FLAME.getColorName();
    public final static String white = WHITE.getColorName();
    public final static String purple = PURPLE.getColorName();
    public ChocoDisguiseItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
        ItemStack stack = new ItemStack(this);
        stack.setNbt(serialize(NBTKEY_COLOR, yellow));
    }
    public NbtCompound getNbt() {
        ItemStack stack = new ItemStack(this);
        return stack.getNbt();
    }
    @Contract("_, _ -> new")
    public static @NotNull Identifier setCustomModel(String customModelData, @NotNull ChocoDisguiseItem armorItem) {
        ArmorMaterial armor = armorItem.getMaterial();
        String folder = "textures/models/armor/leather/";
        if (armor == IRON_CHOCO_DISGUISE) { folder = "textures/models/armor/iron/"; }
        if (armor == DIAMOND_CHOCO_DISGUISE) { folder = "textures/models/armor/diamond/"; }
        if (armor == NETHERITE_CHOCO_DISGUISE) { folder = "textures/models/armor/netherite/"; }
        String check = folder;
        if (customModelData.equals(green)) { folder = folder + "green.png"; }
        if (customModelData.equals(pink)) { folder = folder + "pink.png"; }
        if (customModelData.equals(red)) { folder = folder + "red.png"; }
        if (customModelData.equals(blue)) { folder = folder + "blue.png"; }
        if (customModelData.equals(gold)) { folder = folder + "gold.png"; }
        if (customModelData.equals(black)) { folder = folder + "black.png"; }
        if (customModelData.equals(flame)) { folder = folder + "flame.png"; }
        if (customModelData.equals(white)) { folder = folder + "white.png"; }
        if (customModelData.equals(purple)) { folder = folder + "purple.png"; }
        if (check.equals(folder)) { folder = folder + "yellow.png"; }
        return new Identifier(DelChoco.DELCHOCO_ID, folder);
    }
    public static String getCustomModelColor(@NotNull ItemStack stack) {
        NbtCompound color = stack.getNbt();
        if (color != null && color.contains(NBTKEY_COLOR)) { return color.getString(NBTKEY_COLOR); }
        return yellow;
    }
    public NbtCompound serialize(String key, String string) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(key, string);
        return nbt;
    }
    private void setNBT(@NotNull ItemStack itemStack, @NotNull ItemStack shrinkMe, @NotNull ChocoboColor chocoboColor) {
        // Will Refresh & remove Tags not in use
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(itemStack);
        NbtCompound tempTag = itemStack.getNbt();
        NbtCompound display = null;
        int damage = 0;
        int repairCost = 0;
        if (tempTag != null) {
            if (tempTag.contains("Damage")) { damage = tempTag.getInt("Damage"); }
            if (tempTag.contains("display")) { display = tempTag.getCompound("display"); }
            if (tempTag.contains("RepairCost")) { repairCost = Math.max(0, Math.min(tempTag.getInt("RepairCost")-1, 5)); }
        }
        itemStack.setNbt(serialize(NBTKEY_COLOR, chocoboColor.getColorName()));
        NbtCompound tag = itemStack.getNbt();
        assert tag != null;
        tag.putDouble("CustomModelData", chocoboColor.getCustomModelData());
        if (damage != 0) { tag.putInt("Damage", damage); }
        if (display != null) { tag.put("display", display); }
        tag.putInt("RepairCost", repairCost);
        EnchantmentHelper.set(enchantments, itemStack);
        shrinkMe.decrement(1);
    }
    public TypedActionResult<ItemStack> use(@NotNull World pLevel, @NotNull PlayerEntity pPlayer, @NotNull Hand pUsedHand) {
        ItemStack mainHand = pPlayer.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHand = pPlayer.getStackInHand(Hand.OFF_HAND);
        ItemStack outHand = mainHand;
        if (mainHand.getItem() instanceof ChocoDisguiseItem) {
            NbtCompound coloring = mainHand.getNbt();
            if (coloring == null || !coloring.contains(NBTKEY_COLOR)) {
                mainHand.setNbt(serialize(NBTKEY_COLOR, yellow));
                coloring = mainHand.getNbt();
            }
            if (CHOCO_COLOR_ITEMS.containsKey(offHand.getItem())) {
                if (coloring.contains(NBTKEY_COLOR)) {
                    ChocoboColor chocoboColor = CHOCO_COLOR_ITEMS.get(offHand.getItem());
                    String armorColor = coloring.getString(NBTKEY_COLOR);
                    if (!chocoboColor.getColorName().equals(armorColor)) { setNBT(mainHand, offHand, chocoboColor); }
                }
            } else if (offHand.getItem().asItem().getDefaultStack().equals(CLEANSE_SHIFT_DYE.getDefaultStack())) { setNBT(mainHand, offHand, YELLOW); }
        }
        if (offHand.getItem() instanceof ChocoDisguiseItem) {
            if (CHOCO_COLOR_ITEMS.containsKey(mainHand.getItem())) {
                ChocoboColor chocoboColor = CHOCO_COLOR_ITEMS.get(mainHand.getItem());
                NbtCompound coloring = offHand.getNbt();
                if (coloring == null || !coloring.contains(NBTKEY_COLOR)) {
                    offHand.setNbt(serialize(NBTKEY_COLOR, yellow));
                    coloring = offHand.getNbt();
                }
                if (CHOCO_COLOR_ITEMS.containsKey(mainHand.getItem())) {
                    String armorColor = coloring.getString(NBTKEY_COLOR);
                    if (!chocoboColor.getColorName().equals(armorColor)) { setNBT(offHand, mainHand, chocoboColor); }
                }
            } else if (mainHand.getItem().asItem().getDefaultStack().equals(CLEANSE_SHIFT_DYE.getDefaultStack())) { setNBT(offHand, mainHand, YELLOW); }
            outHand = offHand;
        }
        return TypedActionResult.success(outHand);
    }
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> tooltip, @NotNull TooltipContext flagIn) {
        super.appendTooltip(stack, level, tooltip, flagIn);
        tooltip.add(Text.translatable("item." + DelChoco.DELCHOCO_ID + ".choco_disguise_"+ getCustomModelColor(stack)));
    }
    public void inventoryTick(@NotNull ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (stack.getNbt() == null) { stack.setNbt(serialize(NBTKEY_COLOR, yellow)); }
        int repairCost = stack.getRepairCost();
        if (repairCost > 5) { stack.setRepairCost(repairCost-1); }
        ItemStack head = ((PlayerEntity) entity).getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = ((PlayerEntity) entity).getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = ((PlayerEntity) entity).getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = ((PlayerEntity) entity).getEquippedStack(EquipmentSlot.FEET);
        if (armorColorMatch(head, chest, legs, feet)) {
            boolean hasCpEffect = ((PlayerEntity) entity).hasStatusEffect(StatusEffects.CONDUIT_POWER) && Objects.requireNonNull(((PlayerEntity) entity).getStatusEffect(StatusEffects.CONDUIT_POWER)).getDuration() > 25;
            boolean hasNvEffect = ((PlayerEntity) entity).hasStatusEffect(StatusEffects.NIGHT_VISION) && Objects.requireNonNull(((PlayerEntity) entity).getStatusEffect(StatusEffects.NIGHT_VISION)).getDuration() > 25;
            NbtCompound tag = head.getNbt();
            assert tag != null;
            if (tag.contains(NBTKEY_COLOR)) {
                String color = tag.getString(NBTKEY_COLOR);
                if (color.equals(gold) && !hasNvEffect && !hasCpEffect) {
                    if(entity.isTouchingWater()) { ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 35, 0, false, false)); }
                    else { ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 35, 0, false, false)); }
                }
                if (color.equals(blue) && !hasCpEffect && entity.isTouchingWater()) {
                    ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 35, 0, false, false));
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
    public String getNBTKEY_COLOR() {
        ItemStack stack = new ItemStack(this);
        if (stack.getNbt() != null) {
            return stack.getNbt().getString(NBTKEY_COLOR);
        } else { return yellow; }
    }
    public static String getNBTKEY_COLOR(@NotNull ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().getString(NBTKEY_COLOR);
        } else { return yellow; }
    }
    public boolean isFireproof() {
        String color = getNBTKEY_COLOR();
        boolean netherite = this.getMaterial() == NETHERITE_CHOCO_DISGUISE;
        if (color.equals(gold) || color.equals(flame) || netherite) { return true; }
        return super.isFireproof();
    }
    @SuppressWarnings("removal")
    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        // This is all the extra data this event carries.
        // The living entity is the entity that's wearing the armor.
        // The Item stack and equipment slot-type are self-explanatory.
        LivingEntity livingEntity = event.getExtraDataOfType(LivingEntity.class).get(0);

        // Always loop the animation, but later on in this method we'll decide whether to actually play it
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));

        // If the living entity is an armor-stand just play the animation nonstop
        if (livingEntity instanceof ArmorStandEntity) { return PlayState.CONTINUE; }

        // The entity is a player, so we want to only play if the player is wearing the
        // full set of armor
        else if (livingEntity instanceof PlayerEntity player) {
            // Make sure the player is wearing all the armor.
            // If they are, continue playing the animation, otherwise stop
            ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);
            boolean isWearingAll = armorMatch(head, chest, legs, feet);
            return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
        }
        return PlayState.STOP;
    }

    // All you need to do here is add your animation controllers to the
    // AnimationData
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}