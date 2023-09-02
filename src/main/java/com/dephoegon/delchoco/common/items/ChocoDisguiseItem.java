package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.dephoegon.delbase.item.ShiftingDyes.CLEANSE_SHIFT_DYE;
import static com.dephoegon.delchoco.aid.dyeList.CHOCO_COLOR_ITEMS;
import static com.dephoegon.delchoco.common.entities.properties.ChocoboColor.*;
import static com.dephoegon.delchoco.common.init.ModArmorMaterial.*;

public class ChocoDisguiseItem extends ArmorItem {
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
    public Identifier setCustomModel(String customModelData) {
        ArmorMaterial armor = this.getMaterial();
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
    private String getCustomModelColor(@NotNull ItemStack stack) {
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
            if (CHOCO_COLOR_ITEMS.containsKey(offHand.getItem())) {
                ChocoboColor chocoboColor = CHOCO_COLOR_ITEMS.get(offHand.getItem());
                NbtCompound coloring = mainHand.getNbt();
                if (coloring != null && coloring.contains(NBTKEY_COLOR)) {
                    String armorColor = coloring.getString(NBTKEY_COLOR);
                    if ((armorColor.equals(yellow) || chocoboColor.getColorName().equals(yellow)) && !chocoboColor.getColorName().equals(armorColor)) { setNBT(mainHand, offHand, chocoboColor); }
                } else if (!chocoboColor.getColorName().equals(yellow)) { setNBT(mainHand, offHand,chocoboColor); }
            }
            if (offHand.getItem().getDefaultStack() == CLEANSE_SHIFT_DYE.getDefaultStack()) {
                NbtCompound coloring = mainHand.getNbt();
                if (coloring != null && coloring.contains(NBTKEY_COLOR) && !coloring.getString(NBTKEY_COLOR).equals(yellow)) { setNBT(mainHand, offHand, YELLOW); }
            }
        }
        if (offHand.getItem() instanceof ChocoDisguiseItem) {
            if (CHOCO_COLOR_ITEMS.containsKey(mainHand.getItem())) {
                ChocoboColor chocoboColor = CHOCO_COLOR_ITEMS.get(mainHand.getItem());
                NbtCompound coloring = offHand.getNbt();
                if (coloring != null && coloring.contains(NBTKEY_COLOR)) {
                    String armorColor = coloring.getString(NBTKEY_COLOR);
                    if ((armorColor.equals(yellow) || chocoboColor.getColorName().equals(yellow)) && !chocoboColor.getColorName().equals(armorColor)) { setNBT(offHand, mainHand, chocoboColor); }
                } else if (!chocoboColor.getColorName().equals(yellow)) { setNBT(offHand, mainHand, chocoboColor); }
            }
            if (mainHand.getItem().getDefaultStack() == CLEANSE_SHIFT_DYE.getDefaultStack()) {
                NbtCompound coloring = mainHand.getNbt();
                if (coloring != null && coloring.contains(NBTKEY_COLOR) && !coloring.getString(NBTKEY_COLOR).equals(yellow)) { setNBT(offHand, mainHand, YELLOW); }
            }
            outHand = offHand;
        }
        return TypedActionResult.success(outHand);
    }
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> tooltip, @NotNull TooltipContext flagIn) {
        super.appendTooltip(stack, level, tooltip, flagIn);
        tooltip.add(new TranslatableText("item." + DelChoco.DELCHOCO_ID + ".choco_disguise_"+ getCustomModelColor(stack)));
    }
    public String getNBTKEY_COLOR() {
        ItemStack stack = new ItemStack(this);
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
}