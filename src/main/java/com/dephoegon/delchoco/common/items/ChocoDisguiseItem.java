package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.client.clientHandler;
import com.dephoegon.delchoco.client.models.armor.ChocoDisguiseModel;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.dephoegon.delbase.item.ShiftingDyes.CLEANSE_SHIFT_DYE;
import static com.dephoegon.delchoco.aid.dyeList.CHOCO_COLOR_ITEMS;
import static com.dephoegon.delchoco.common.init.ModArmorMaterial.*;

public class ChocoDisguiseItem extends ArmorItem {
    private final Lazy<BipedEntityModel<?>> model;
    public final static String NBTKEY_COLOR = "Color";
    public final static String yellow = "yellow"; // default
    public final static String green = "green";
    public final static String pink = "pink";
    public final static String red = "red";
    public final static String blue = "blue";
    public final static String gold = "gold";
    public final static String black = "black";
    public final static String flame = "flame";
    public final static String white = "white";
    public final static String purple = "purple";
    public ChocoDisguiseItem(ArmorMaterial material, EquipmentSlot slot, Settings settings, Lazy<BipedEntityModel<?>> model) {
        super(material, slot, settings);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) { this.model = new Lazy<>(() -> this.provideArmorModelForSlot(this.slot));}
        else { this.model = null; }
    }
    public static String staticSetCustomModel(String customData) { return setCustomModel(customData); }
    public Identifier setCustomModel(String customModelData) {
        ArmorMaterial armor = this.getMaterial();
        String folder = "textures/models/armor/leather/";
        if (armor == IRON_CHOCO_DISGUISE) { folder = "textures/models/armor/iron/"; }
        if (armor == DIAMOND_CHOCO_DISGUISE) { folder = "textures/models/armor/diamond/"; }
        if (armor == NETHERITE_CHOCO_DISGUISE) { folder = "textures/models/armor/netherite/"; }
        folder = switch (customModelData) {
            default -> folder + "yellow.png";
            case green -> folder + "green.png";
            case pink -> folder + "pink.png";
            case red -> folder + "red.png";
            case blue -> folder + "blue.png";
            case gold -> folder + "gold.png";
            case black -> folder + "black.png";
            case flame -> folder + "flame.png";
            case white -> folder + "white.png";
            case purple -> folder + "purple.png";
        };
        return new Identifier(DelChoco.DELCHOCO_ID, folder);
    }
    private String itemColor(@NotNull ChocoboColor chocoboColor) {
        return switch (chocoboColor) {
            default -> yellow;
            case GREEN -> green;
            case PINK -> pink;
            case RED -> red;
            case BLUE -> blue;
            case GOLD -> gold;
            case BLACK -> black;
            case FLAME -> flame;
            case WHITE -> white;
            case PURPLE -> purple;
        };
    }
    public Identifier getArmorTexture(@NotNull ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        NbtCompound color = stack.getNbt();
        if (color != null && color.contains(NBTKEY_COLOR)) { return setCustomModel(color.getString(NBTKEY_COLOR)); }
        return setCustomModel(yellow);
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
    private void setNBT(@NotNull ItemStack itemStack, @NotNull ItemStack shrinkMe, String colorValue, @NotNull ChocoboColor chocoboColor) {
        // Will Refresh & remove Tags not in use
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        NbtCompound tempTag = itemStack.getNbt();
        NbtCompound display = null;
        int damage = 0;
        int repairCost = 0;
        if (tempTag != null) {
            if (tempTag.contains("Damage")) { damage = tempTag.getInt("Damage"); }
            if (tempTag.contains("display")) { display = tempTag.getCompound("display"); }
            if (tempTag.contains("RepairCost")) { repairCost = Math.max(0, Math.min(tempTag.getInt("RepairCost")-1, 5)); }
        }
        itemStack.setTag(serialize(NBTKEY_COLOR, colorValue));
        NbtCompound tag = itemStack.getNbt();
        assert tag != null;
        tag.putDouble("CustomModelData", chocoboColor.getCustomModelData());
        if (damage != 0) { tag.putInt("Damage", damage); }
        if (display != null) { tag.put("display", display); }
        tag.putInt("RepairCost", repairCost);
        EnchantmentHelper.setEnchantments(enchantments, itemStack);
        shrinkMe.shrink(1);
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
                    if ((armorColor.equals(yellow) || itemColor(chocoboColor).equals(yellow)) && !itemColor(chocoboColor).equals(armorColor)) { setNBT(mainHand, offHand, itemColor(chocoboColor), chocoboColor); }
                } else if (!itemColor(chocoboColor).equals(yellow)) { setNBT(mainHand, offHand, itemColor(chocoboColor),chocoboColor); }
            }
            if (offHand.getItem().getDefaultStack() == CLEANSE_SHIFT_DYE.getDefaultStack()) {
                NbtCompound coloring = mainHand.getNbt();
                if (coloring != null && coloring.contains(NBTKEY_COLOR) && !coloring.getString(NBTKEY_COLOR).equals(yellow)) { setNBT(mainHand, offHand, yellow, ChocoboColor.YELLOW); }
            }
        }
        if (offHand.getItem() instanceof ChocoDisguiseItem) {
            if (CHOCO_COLOR_ITEMS.containsKey(mainHand.getItem())) {
                ChocoboColor chocoboColor = CHOCO_COLOR_ITEMS.get(mainHand.getItem());
                NbtCompound coloring = offHand.getNbt();
                if (coloring != null && coloring.contains(NBTKEY_COLOR)) {
                    String armorColor = coloring.getString(NBTKEY_COLOR);
                    if ((armorColor.equals(yellow) || itemColor(chocoboColor).equals(yellow)) && !itemColor(chocoboColor).equals(armorColor)) { setNBT(offHand, mainHand, itemColor(chocoboColor), chocoboColor); }
                } else if (!itemColor(chocoboColor).equals(yellow)) { setNBT(offHand, mainHand, itemColor(chocoboColor), chocoboColor); }
            }
            if (mainHand.getItem().getDefaultStack() == CLEANSE_SHIFT_DYE.getDefaultStack()) {
                NbtCompound coloring = mainHand.getNbt();
                if (coloring != null && coloring.contains(NBTKEY_COLOR) && !coloring.getString(NBTKEY_COLOR).equals(yellow)) { setNBT(offHand, mainHand, yellow, ChocoboColor.YELLOW); }
            }
            outHand = offHand;
        }
        return TypedActionResult.success(outHand);
    }
    public BipedEntityModel<?> provideArmorModelForSlot(EquipmentSlot slot) { return new ChocoDisguiseModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(clientHandler.CHOCO_DISGUISE_LAYER), slot); }

    // Forge Method
    public void initializeClient(@NotNull Consumer<IItemRenderProperties> consumer) { consumer.accept(new IItemRenderProperties() {
        public BipedEntityModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel<?> _default) { return model.get(); }
    }); }
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
    public boolean isFireResistant() {
        String color = getNBTKEY_COLOR();
        if (color.equals(gold) || color.equals(flame)) { return true; }
        return super.isFireResistant();
    }
}