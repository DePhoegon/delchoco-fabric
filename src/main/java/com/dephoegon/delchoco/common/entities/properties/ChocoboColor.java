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
    YELLOW("yellow",1, 0, CHOCOBO_ENTITY),
    GREEN("green",2,1, GREEN_CHOCOBO_ENTITY),
    BLUE("blue",3, 2, BLUE_CHOCOBO_ENTITY),
    WHITE("white",4, 3, WHITE_CHOCOBO_ENTITY),
    BLACK("black",5, 4, BLACK_CHOCOBO_ENTITY),
    GOLD("gold",6,5, GOLD_CHOCOBO_ENTITY),
    PINK("pink",7, 6, PINK_CHOCOBO_ENTITY),
    RED("red",8, 7, RED_CHOCOBO_ENTITY),
    PURPLE("purple",9, 8, PURPLE_CHOCOBO_ENTITY),
    FLAME("flame",10, 9, FLAME_CHOCOBO_ENTITY),
    ARMOR("armor_stand",11, 10, CHOCOBO_ARMOR_STAND_ENTITY);

    private final static Random rand = new Random();
    private final String colorTag;
    private final MutableText eggText;
    private final int customModelData;
    private final int chocoboColorID;
    private final EntityType<?> chocoboEntityType;
    ChocoboColor(String colorNameString, int CustomModelData, int chocoboColorID, EntityType<?> chocoboEntityType) {
        this.colorTag = colorNameString;
        this.customModelData = CustomModelData;
        this.eggText = Text.translatable("item." + DelChoco.DELCHOCO_ID + ".chocobo_egg.tooltip." + this.name().toLowerCase());
        this.chocoboColorID = chocoboColorID;
        this.chocoboEntityType = chocoboEntityType;
    }
    public int getCustomModelData() { return this.customModelData; }
    /**
     * @return Returns a random ChocoboColor, excluding ARMOR STAND color.
     */
    public static ChocoboColor getRandomColor() {
        ChocoboColor[] validColors = {YELLOW, GREEN, BLUE, WHITE, BLACK, GOLD, PINK, RED, PURPLE, FLAME};
        return validColors[rand.nextInt(validColors.length)];
    }
    public int getChocoboColorID() { return this.chocoboColorID; }
    public String getColorName() { return this.colorTag; }
    public static ChocoboColor getColorFromName(String name) {
        for (ChocoboColor color : values()) { if(name.equals(color.colorTag.toLowerCase())) { return color; } }
        return YELLOW;
    }
    /**
     * @param isChocobo If true, checks against the Chocobo Color ID, otherwise checks against Custom Model Data.
     * @return Returns a ChocoboColor based on the ID provided.
     */
    public static ChocoboColor getChocoboColorFromID(int id, boolean isChocobo) {
        for (ChocoboColor color : values()) {
            int switchID = isChocobo ? color.chocoboColorID : color.customModelData;
            if(id == switchID) { return color; }
        }
        return YELLOW;
    }
    public MutableText getEggText() { return eggText; }
    @Contract(pure = true)
    public EntityType<?> getEntityType() { return this.chocoboEntityType; }
}
