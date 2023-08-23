package com.dephoegon.delchoco.common;

public enum teamColors {
    YELLOW("ChocoboYellow", "Yellow"),
    GREEN("ChocoboGreen", "Green"),
    BLUE("ChocoboBlue", "Blue"),
    WHITE("ChocoboWhite", "White"),
    BLACK("ChocoboBlack", "Black"),
    GOLD("ChocoboGold", "Gold"),
    PINK("ChocoboPink", "Pink"),
    RED("ChocoboRed", "Red"),
    PURPLE("ChocoboPurple", "Purple"),
    FLAME("ChocoboFlame", "Flame");
    private final String teamName;
    private final String colorName;
    teamColors(String displayName, String color) {
        this.teamName = displayName;
        this.colorName = color;
    }
    public String getTeamName() { return this.teamName; }
    public String getColorName() { return this.colorName; }
}