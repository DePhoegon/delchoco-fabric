package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.init.ModAttributes;
import com.dephoegon.delchoco.common.init.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.dephoegon.delchoco.aid.chocoboChecks.*;
import static com.dephoegon.delchoco.aid.world.WorldConfig.FloatChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dPOS_GAIN;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dPOS_LOSS;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboSnap.setChocoScale;
import static java.lang.Math.*;

public class BreedingHelper {
    private static double minCheck(double one, double two) { return round((one + two) / 2) < 11 ? round((one+two)/2+1) : round((one + two) / 2); }

    public static @Nullable Chocobo createChild(ChocoboBreedInfo breedInfo, World world) {
        final Chocobo baby = ModEntities.CHOCOBO_ENTITY.create(world);
        if (baby == null) { return null; }
        final ChocoboStatSnapshot mother = breedInfo.getMother();
        final ChocoboStatSnapshot father = breedInfo.getFather();

        baby.setGeneration(mother.generation > father.generation ? mother.generation+1 : father.generation+1);
        float health = round(((mother.health + father.health) / 2) * (ChocoboConfig.POSS_HEALTH_LOSS.get() + ((float) random() * ChocoboConfig.POSS_HEALTH_GAIN.get())));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(max(min(health, ChocoboConfig.MAX_HEALTH.get()), ChocoboConfig.DEFAULT_HEALTH.get()));
        float speed = ((mother.speed + father.speed) / 2f) * (FloatChocoConfigGet(ChocoboConfig.POSS_SPEED_LOSS.get(), dPOS_LOSS.getDefault()) + ((float) random() * FloatChocoConfigGet(ChocoboConfig.POSS_SPEED_GAIN.get(), dPOS_GAIN.getDefault())));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(max(min(speed, (ChocoboConfig.MAX_SPEED.get() / 100f)), (ChocoboConfig.DEFAULT_SPEED.get() / 100f)));
        float stamina = round((mother.stamina + father.stamina) / 2) * (FloatChocoConfigGet(ChocoboConfig.POSS_STAMINA_LOSS.get(), dPOS_LOSS.getDefault()) + ((float) random() * FloatChocoConfigGet(ChocoboConfig.POSS_STAMINA_GAIN.get(), dPOS_GAIN.getDefault())));
        Objects.requireNonNull(baby.getAttributeInstance(ModAttributes.CHOCOBO_STAMINA)).setBaseValue(max(min(stamina, ChocoboConfig.MAX_STAMINA.get()), ChocoboConfig.DEFAULT_STAMINA.get()));
        double attack = minCheck(mother.attack, father.attack) * (ChocoboConfig.POSS_STAT_LOSS.get() + ((float) random() * (ChocoboConfig.POSS_STAT_GAIN.get() +25D)));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(max(min(attack, ChocoboConfig.MAX_ATTACK.get()), ChocoboConfig.DEFAULT_ATTACK_DAMAGE.get()));
        double defence = minCheck(mother.defense, father.defense) * (ChocoboConfig.POSS_STAT_LOSS.get() + ((float) random() * (ChocoboConfig.POSS_STAT_GAIN.get() +25D)));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(max(min(defence, ChocoboConfig.MAX_ARMOR.get()), ChocoboConfig.DEFAULT_ARMOR.get()));
        double toughness = minCheck(mother.toughness, father.toughness) * (ChocoboConfig.POSS_STAT_LOSS.get() + ((float) random() * (ChocoboConfig.POSS_STAT_GAIN.get() +25D)));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(max(min(toughness, ChocoboConfig.MAX_ARMOR_TOUGHNESS.get()), ChocoboConfig.DEFAULT_ARMOR_TOUGHNESS.get()));

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
        baby.setCustomName(getChocoName());
        baby.setBreedingAge(-7500);
        return baby;
    }
    private static ChocoboColor eggColor(ChocoboColor mother, ChocoboColor father, ChocoboColor baby, float chance) {
        boolean newColor = chance > Math.random();
        if (newColor) { return baby; }
        else return .50f > Math.random() ? mother : father;
    }
    private static boolean alter(BlockState centerDefaultBlockstate, BlockState NEWS_blockstate, BlockPos centerPos, @NotNull World world) { return world.getBlockState(centerPos).getBlock().getDefaultState() == centerDefaultBlockstate && world.getBlockState(centerPos.north()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.south()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.east()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.west()).getBlock().getDefaultState() == NEWS_blockstate; }
    private static final String[] PREFIX = {"Swift", "Golden", "Silver", "Tiny", "Giant", "Happy", "Sad", "Angry", "Calm", "Brave", "Shiny", "Dashing", "Prancing", "Majestic", "Noble", "Gallant", "Fierce", "Graceful", "Mighty", "Daring", "Bold", "Fearless", "Loyal", "Reliable", "Steady", "Sturdy", "Robust", "Hardy", "Energetic", "Vigorous", "Vital", "Vibrant", "Vivacious", "Vigilant", "Voracious", "Vorpal", "Vicious", "Vexing", "Vexatious", "Volatile", "Vivifying", "Viv"};
    private static final String[] SUFFIX = {"Feather", "Beak", "Wing", "Claw", "Eye", "Talon", "Plume", "Crest", "Squawk", "Caw", "Trotter", "Runner", "Racer", "Sprinter", "Flyer", "Pacer", "Strider", "Galloper", "Charger", "Dasher", "Leaper", "Bounder", "Jumper", "Hopper", "Skipper", "Bouncer", "Pouncer", "Soarer", "Glider", "Swooper", "Diver", "Plunger", "Ducker", "Diver", "Dropper", "Plummet", "Plunge", "Plumage", "Plummet", "Plummeting", "Plummeted"};
    public static Text getChocoName() {
        return Text.of(PREFIX[(int) (Math.random() * PREFIX.length)] + " " + SUFFIX[(int) (Math.random() * SUFFIX.length)]);
    }
}