package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.init.ModAttributes;
import com.dephoegon.delchoco.common.init.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.dephoegon.delchoco.DelChoco.chocoConfigHolder;
import static com.dephoegon.delchoco.aid.chocoboChecks.*;
import static com.dephoegon.delchoco.aid.world.WorldConfig.FloatChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dPOS_GAIN;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dPOS_LOSS;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboSnap.setChocoScale;
import static java.lang.Math.*;

public class BreedingHelper {
    private static double minCheck(double one, double two) { return round((one + two) / 2) < 11 ? round((one+two)/2+1) : round((one + two) / 2); }

    public static @Nullable Chocobo createChild(ChocoboBreedInfo breedInfo, World world, ItemStack egg) {
        final Chocobo baby = ModEntities.CHOCOBO_ENTITY.create(world);
        if (baby == null) { return null; }
        final ChocoboStatSnapshot mother = breedInfo.getMother();
        final ChocoboStatSnapshot father = breedInfo.getFather();

        baby.setGeneration(mother.generation > father.generation ? mother.generation+1 : father.generation+1);
        float health = round(((mother.health + father.health) / 2) * (chocoConfigHolder.chocoboPossibleLossHealth + ((float) random() * chocoConfigHolder.chocoboPossibleGainHealth)));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(min(health, chocoConfigHolder.chocoboMaxHealth));
        float speed = ((mother.speed + father.speed) / 2f) * (FloatChocoConfigGet(chocoConfigHolder.chocoboPossibleLossSpeed, dPOS_LOSS.getDefault()) + ((float) random() * FloatChocoConfigGet(chocoConfigHolder.chocoboPossibleGainSpeed, dPOS_GAIN.getDefault())));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(min(speed, (chocoConfigHolder.chocoboSpeed / 100f)));
        float stamina = round((mother.stamina + father.stamina) / 2) * (FloatChocoConfigGet(chocoConfigHolder.chocoboPossibleLossStamina, dPOS_LOSS.getDefault()) + ((float) random() * FloatChocoConfigGet(chocoConfigHolder.chocoboPossibleGainStamina, dPOS_GAIN.getDefault())));
        Objects.requireNonNull(baby.getAttributeInstance(ModAttributes.CHOCOBO_MAX_STAMINA)).setBaseValue(min(stamina, chocoConfigHolder.chocoboMaxStamina));
        double attack = minCheck(mother.attack, father.attack) * (chocoConfigHolder.chocoboPossibleLoss + ((float) random() * (chocoConfigHolder.chocoboPossibleGain +25D)));
        attack = Math.max(attack, chocoConfigHolder.chocoboAttackDamage);
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(min(attack, chocoConfigHolder.chocoboMaxStrength));
        double defence = minCheck(mother.defense, father.defense) * (chocoConfigHolder.chocoboPossibleLoss + ((float) random() * (chocoConfigHolder.chocoboPossibleGain +25D)));
        defence = Math.max(defence, chocoConfigHolder.chocoboArmor);
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(min(defence, chocoConfigHolder.chocoboMaxArmor));
        double toughness = minCheck(mother.toughness, father.toughness) * (chocoConfigHolder.chocoboPossibleLoss + ((float) random() * (chocoConfigHolder.chocoboPossibleGain +25D)));
        toughness = Math.max(toughness, chocoConfigHolder.chocoboArmorToughness);
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(min(toughness, chocoConfigHolder.chocoboArmorToughness));

        ChocoboColor yellow = ChocoboColor.YELLOW;
        ChocoboColor green = ChocoboColor.GREEN;
        ChocoboColor blue = ChocoboColor.BLUE;
        ChocoboColor white = ChocoboColor.WHITE;
        ChocoboColor black = ChocoboColor.BLACK;
        ChocoboColor gold = ChocoboColor.GOLD;
        ChocoboColor pink = ChocoboColor.PINK;
        ChocoboColor red = ChocoboColor.RED;
        ChocoboColor purple = ChocoboColor.PURPLE;
        ChocoboColor flame = ChocoboColor.FLAME;
        ChocoboColor mColor = mother.color;
        ChocoboColor fColor = father.color;
        ChocoboColor bColor = eggColor(mColor, fColor, yellow, .03f);

        if (mColor == yellow) {
            if (fColor == yellow) {
                int rng = (int)(random()*(100-1+1)+1);
                if (rng < 25) { bColor = eggColor(mColor, fColor, yellow, .50f); }
                else if (rng < 50) { bColor = eggColor(mColor, fColor, green, .50f); }
                else if (rng < 75) { bColor = eggColor(mColor, fColor, blue, .50f); }
                else { bColor = eggColor(mColor, fColor, white, .50f); }
            }
            if (fColor == black) { bColor = eggColor(mColor, fColor, gold, .35f); }
        }
        if (mColor == green) { if (fColor == blue) { bColor = eggColor(mColor,fColor, black, .40f); } }
        if (mColor == blue) {
            if (fColor == red) { bColor = eggColor(mColor, fColor, purple, .40f); }
            if (fColor == green) { bColor = eggColor(mColor, fColor, black, .40f); }
        }
        if (mColor == white) {
            if (fColor == flame) { bColor = eggColor(mColor, fColor, red, .60f); }
            if (fColor == red) { bColor = eggColor(mColor, fColor, pink, .50f); }
        }
        if (mColor == black) { if (fColor == yellow) { bColor = eggColor(mColor, fColor, gold, .35f); } }
        if (mColor == red) {
            if (fColor == white) { bColor = eggColor(mColor, fColor, pink, .50f); }
            if (fColor == blue) { bColor = eggColor(mColor, fColor, purple, .40f); }
        }
        if (mColor == flame) { if (fColor == white) { bColor = eggColor(mColor, fColor, red, .60f); } }

        baby.setMale(.50f > (float) random());
        baby.setChocoboColor(bColor);
        int scale = mother.scale+father.scale+(setChocoScale(baby.isMale()));
        baby.setChocoboScale(baby.isMale(), Math.floorDiv(scale, 3), true);
        baby.setWaterBreath(mother.waterBreath || father.waterBreath || isWaterBreathingChocobo(bColor));
        baby.setWitherImmune(mother.witherImmune || father.witherImmune || isWitherImmuneChocobo(bColor));
        baby.setPoisonImmune(mother.poisonImmune || father.poisonImmune || isPoisonImmuneChocobo(bColor));
        baby.setFlame(mother.flameBlood || father.flameBlood || bColor == flame);
        baby.setFromEgg(true);
        if (egg.hasCustomName()) { baby.setCustomName(egg.getName()); }
        baby.setBreedingAge(-7500);
        return baby;
    }
    private static ChocoboColor eggColor(ChocoboColor mother, ChocoboColor father, ChocoboColor baby, float chance) {
        boolean newColor = chance > Math.random();
        if (newColor) { return baby; }
        else return .50f > Math.random() ? mother : father;
    }
    private static boolean alter(BlockState centerDefaultBlockstate, BlockState NEWS_blockstate, BlockPos centerPos, @NotNull World world) { return world.getBlockState(centerPos).getBlock().getDefaultState() == centerDefaultBlockstate && world.getBlockState(centerPos.north()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.south()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.east()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.west()).getBlock().getDefaultState() == NEWS_blockstate; }
}