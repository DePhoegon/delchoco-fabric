package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.DelChoco;
import net.minecraft.text.TranslatableText;

import java.util.Random;

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
    private final TranslatableText eggText;
    private final int customModelData;
    ChocoboColor(String colorNameString, int CustomModelData) {
        this.colorTag = colorNameString;
        this.customModelData = CustomModelData;
        this.eggText = new TranslatableText("item." + DelChoco.DELCHOCO_ID + ".chocobo_egg.tooltip." + this.name().toLowerCase());
    }
    public int getCustomModelData() { return this.customModelData; }
    public static ChocoboColor getRandomColor() { return values()[rand.nextInt(values().length)]; }
    public String getColorName() { return this.colorTag; }
    public static ChocoboColor getColorFromName(String name) {
        for (ChocoboColor color : values()) { if(name.equals(color.colorTag.toLowerCase())) { return color; } }
        return YELLOW;
    }
    public TranslatableText getEggText() { return eggText; }
}
