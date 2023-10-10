package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.DelChoco;
import net.minecraft.entity.EntityType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;

import java.util.Random;

import static com.dephoegon.delchoco.common.init.ModEntities.*;

@SuppressWarnings("SameParameterValue")
public enum ChocoboColor {
    YELLOW("yellow",1),
    GREEN("green",2),
    BLUE("blue",3),
    WHITE("white",4),
    BLACK("black",5),
    GOLD("gold",6),
    PINK("pink",7),
    RED("red",8),
    PURPLE("purple",9),
    FLAME("flame",10);

    private final static Random rand = new Random();
    private final String colorTag;
    private final MutableText eggText;
    private final int customModelData;
    ChocoboColor(String colorNameString, int CustomModelData) {
        this.colorTag = colorNameString;
        this.customModelData = CustomModelData;
        this.eggText = Text.translatable("item." + DelChoco.DELCHOCO_ID + ".chocobo_egg.tooltip." + this.name().toLowerCase());
    }
    public int getCustomModelData() { return this.customModelData; }
    public static ChocoboColor getRandomColor() { return values()[rand.nextInt(values().length)]; }
    public String getColorName() { return this.colorTag; }
    public static ChocoboColor getColorFromName(String name) {
        for (ChocoboColor color : values()) { if(name.equals(color.colorTag.toLowerCase())) { return color; } }
        return YELLOW;
    }
    public MutableText getEggText() { return eggText; }
    @Contract(pure = true)
    public EntityType<?> getEntityTypeByColor() {
        ChocoboColor chocoboColor = this;
        return switch (chocoboColor) {
            case RED -> RED_CHOCOBO_ENTITY;
            case YELLOW -> YELLOW_CHOCOBO_ENTITY;
            case BLUE -> BLUE_CHOCOBO_ENTITY;
            case GOLD -> GOLD_CHOCOBO_ENTITY;
            case PINK -> PINK_CHOCOBO_ENTITY;
            case BLACK -> BLACK_CHOCOBO_ENTITY;
            case FLAME -> FLAME_CHOCOBO_ENTITY;
            case GREEN -> GREEN_CHOCOBO_ENTITY;
            case WHITE -> WHITE_CHOCOBO_ENTITY;
            case PURPLE -> PURPLE_CHOCOBO_ENTITY;
        };
    }
}
