package com.dephoegon.delchoco.common.entities.breeding;

import com.dephoegon.delchoco.aid.world.ChocoboConfig;

import static com.dephoegon.delchoco.aid.chocoboChecks.*;
import static com.dephoegon.delchoco.common.entities.properties.ChocoboColor.FLAME;
import static com.dephoegon.delchoco.common.entities.properties.ChocoboColor.getRandomColor;
import static com.dephoegon.delchoco.utils.RandomHelper.random;

public class ChocoboSnap {
    final public ChocoboStatSnapshot TWEAKED_DEFAULT = new ChocoboStatSnapshot();

    private void setTWEAKED() {
        this.TWEAKED_DEFAULT.generation = 1;
        this.TWEAKED_DEFAULT.health = boundedIntRange(5, 10, ChocoboConfig.DEFAULT_HEALTH.get());
        this.TWEAKED_DEFAULT.stamina = ChocoboConfig.DEFAULT_STAMINA.get();
        this.TWEAKED_DEFAULT.speed = ChocoboConfig.DEFAULT_SPEED.get() / 100f;
        this.TWEAKED_DEFAULT.attack = boundedIntRange(1, 3, ChocoboConfig.DEFAULT_ATTACK_DAMAGE.get());
        this.TWEAKED_DEFAULT.defense = boundedIntRange(2, 4, ChocoboConfig.DEFAULT_ARMOR.get());
        this.TWEAKED_DEFAULT.toughness = boundedIntRange(1, 3, ChocoboConfig.DEFAULT_ARMOR_TOUGHNESS.get());
        this.TWEAKED_DEFAULT.color = getRandomColor();
        this.TWEAKED_DEFAULT.flameBlood = TWEAKED_DEFAULT.color == FLAME;
        this.TWEAKED_DEFAULT.waterBreath = isWaterBreathingChocobo(this.TWEAKED_DEFAULT.color);
        this.TWEAKED_DEFAULT.witherImmune = isWitherImmuneChocobo(this.TWEAKED_DEFAULT.color);
        this.TWEAKED_DEFAULT.poisonImmune = isPoisonImmuneChocobo(this.TWEAKED_DEFAULT.color);
        this.TWEAKED_DEFAULT.scale = setChocoScale(.50f > (float) Math.random());
    }
    public ChocoboSnap() { setTWEAKED(); }
    public int boundedIntRange(int lower, int upper, int origin) {
        int lowEnd = Math.max(origin - lower, 0);
        int upEnd = origin+upper;
        int range = upEnd-lowEnd;
        return random.nextInt(range)+lowEnd;
    }
    public static int setChocoScale(boolean isMale) {
        int base = random.nextInt(50) + 1;
        return isMale ? base - 20 : base - 30;
    }
}