package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.aid.world.ChocoboConfig;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import com.dephoegon.delchoco.common.init.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.dephoegon.delchoco.aid.chocoboChecks.*;
import static com.dephoegon.delchoco.aid.world.WorldConfig.FloatChocoConfigGet;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dPOS_GAIN;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dPOS_LOSS;
import static com.dephoegon.delchoco.common.entities.breeding.ChocoboTweakedSnapShots.setChocoScale;
import static java.lang.Math.*;

public class BreedingHelper {
    private static double minCheck(double one, double two) { return round((one + two) / 2) < 11 ? round((one+two)/2+1) : round((one + two) / 2); }
    private static boolean alter(BlockState centerDefaultBlockstate, BlockState NEWS_blockstate, BlockPos centerPos, @NotNull World world) { return world.getBlockState(centerPos).getBlock().getDefaultState() == centerDefaultBlockstate && world.getBlockState(centerPos.north()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.south()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.east()).getBlock().getDefaultState() == NEWS_blockstate && world.getBlockState(centerPos.west()).getBlock().getDefaultState() == NEWS_blockstate; }
    private static final String[] PREFIX = {"Swift", "Golden", "Silver", "Tiny", "Giant", "Happy", "Sad", "Angry", "Calm", "Brave", "Shiny", "Dashing", "Prancing", "Majestic", "Noble", "Gallant", "Fierce", "Graceful", "Mighty", "Daring", "Bold", "Fearless", "Loyal", "Reliable", "Steady", "Sturdy", "Robust", "Hardy", "Energetic", "Vigorous", "Vital", "Vibrant", "Vivacious", "Vigilant", "Voracious", "Vorpal", "Vicious", "Vexing", "Vexatious", "Volatile", "Vivifying", "Viv"};
    private static final String[] SUFFIX = {"Feather", "Beak", "Wing", "Claw", "Eye", "Talon", "Plume", "Crest", "Squawk", "Caw", "Trotter", "Runner", "Racer", "Sprinter", "Flyer", "Pacer", "Strider", "Galloper", "Charger", "Dasher", "Leaper", "Bounder", "Jumper", "Hopper", "Skipper", "Bouncer", "Pouncer", "Soarer", "Glider", "Swooper", "Diver", "Plunger", "Ducker", "Diver", "Dropper", "Plummet", "Plunge", "Plumage", "Plummet", "Plummeting", "Plummeted"};
    public static Text getChocoName() {
        return Text.of(PREFIX[(int) (Math.random() * PREFIX.length)] + " " + SUFFIX[(int) (Math.random() * SUFFIX.length)]);
    }
    public static Chocobo getChicoboFromParents(Chocobo mother, Chocobo father, ServerWorld world) {
        return getChicoboFromBreedInfo(new ChocoboBreedInfo(new ChocoboStatSnapshot(mother), new ChocoboStatSnapshot(father)), world);
    }
    /**
     * Returns a new {@link Chocobo} instance based on the provided {@link ChocoboBreedInfo}.
     * The new chocobo's stats are calculated based on the parents' stats and some randomization.
     *
     * @param breedInfo The breeding information containing the parents' stats.
     * @param world The world in which the new chocobo will be spawned.
     * @return A new chocobo instance with calculated stats, or null if the chocobo cannot be created.
    */
    public static Chocobo getChicoboFromBreedInfo(@NotNull ChocoboBreedInfo breedInfo, ServerWorld world) {

        final Chocobo baby = ModEntities.CHOCOBO_ENTITY.create(world);
        if (baby == null) { return null; }
        // If the baby chocobo cannot be created, return null

        final ChocoboStatSnapshot motherSnap = breedInfo.getMother();
        final ChocoboStatSnapshot fatherSnap = breedInfo.getFather();

        // Speed
        float speed = ((motherSnap.speed + fatherSnap.speed) / 2f) * (FloatChocoConfigGet(ChocoboConfig.POSS_SPEED_LOSS.get(), dPOS_LOSS.getDefault()) + ((float) random() * FloatChocoConfigGet(ChocoboConfig.POSS_SPEED_GAIN.get(), dPOS_GAIN.getDefault())));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(max(min(speed, (ChocoboConfig.MAX_SPEED.get() / 100f)), (ChocoboConfig.DEFAULT_SPEED.get() / 100f)));

        // Health
        float health = round(((motherSnap.health + fatherSnap.health) / 2) * (ChocoboConfig.POSS_HEALTH_LOSS.get() + ((float) random() * ChocoboConfig.POSS_HEALTH_GAIN.get())));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(max(min(health, ChocoboConfig.MAX_HEALTH.get()), ChocoboConfig.DEFAULT_HEALTH.get()));

        // Attack Damage
        double attack = minCheck(motherSnap.attack, fatherSnap.attack) * (ChocoboConfig.POSS_STAT_LOSS.get() + ((float) random() * (ChocoboConfig.POSS_STAT_GAIN.get() +25D)));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(max(min(attack, ChocoboConfig.MAX_ATTACK.get()), ChocoboConfig.DEFAULT_ATTACK_DAMAGE.get()));

        // Defense
        double defence = minCheck(motherSnap.defense, fatherSnap.defense) * (ChocoboConfig.POSS_STAT_LOSS.get() + ((float) random() * (ChocoboConfig.POSS_STAT_GAIN.get() +25D)));
        Objects.requireNonNull(baby.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(max(min(defence, ChocoboConfig.MAX_ARMOR.get()), ChocoboConfig.DEFAULT_ARMOR.get()));

        // Defense Toughness
        double toughness = minCheck(motherSnap.toughness, fatherSnap.toughness) * (ChocoboConfig.POSS_STAT_LOSS.get() + ((float) random() * (ChocoboConfig.POSS_STAT_GAIN.get() +25D)));
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
        ChocoboColor mColor = motherSnap.color;
        ChocoboColor fColor = fatherSnap.color;
        ChocoboColor bColor = eggColor(mColor, fColor, yellow, .03f);

        if (mColor == yellow) {
            if (fColor == yellow) {
                int rng = (int)(random()*(100)+1);
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
        int scale = motherSnap.scale+fatherSnap.scale+(setChocoScale(baby.isMale()));
        baby.setChocoboScale(baby.isMale(), Math.floorDiv(scale, 3), true);
        baby.setWaterBreath(motherSnap.waterBreath || fatherSnap.waterBreath || isWaterBreathingChocobo(bColor));
        baby.setWitherImmune(motherSnap.witherImmune || fatherSnap.witherImmune || isWitherImmuneChocobo(bColor));
        baby.setPoisonImmune(motherSnap.poisonImmune || fatherSnap.poisonImmune || isPoisonImmuneChocobo(bColor));
        baby.setFlame(motherSnap.flameBlood || fatherSnap.flameBlood || bColor == flame);
        baby.setFromEgg(true);
        baby.setCustomName(getChocoName());

        return baby;
    }
    private static ChocoboColor eggColor(ChocoboColor mother, ChocoboColor father, ChocoboColor baby, float chance) {
        boolean newColor = chance > Math.random();
        if (newColor) { return baby; }
        else return .50f > Math.random() ? mother : father;
    }
}