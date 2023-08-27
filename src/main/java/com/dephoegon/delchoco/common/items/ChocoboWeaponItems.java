package com.dephoegon.delchoco.common.items;

import com.google.common.collect.Maps;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Util;

import java.util.Map;

public class ChocoboWeaponItems extends SwordItem {
    private final float attackSpeed;
    public static final int CHOCOBO_DAMAGE_MODIFIER = 5;
    public static final Map<Integer, ToolMaterial> CHOCOBO_WEAPON_TIERS = Util.make(Maps.newHashMap(), (map) -> {
        map.put(1, ToolMaterials.STONE);
        map.put(2, ToolMaterials.IRON);
        map.put(3, ToolMaterials.DIAMOND);
        map.put(4, ToolMaterials.NETHERITE);
    });
    private static final Map<ToolMaterial, Integer> CHOCOBO_WEAPON_TIER = Util.make(Maps.newHashMap(), (map) -> { for (int i = 1; i <= CHOCOBO_WEAPON_TIERS.size(); i++) { map.put(CHOCOBO_WEAPON_TIERS.get(i), i); } });
    private static int totalTierDamage(ToolMaterial tier, int additive, boolean initialTier) {
        int out = initialTier ? (int) tier.getAttackDamage() + additive : (int) (tier.getAttackDamage()/2)+additive;
        int nextLowestTier = CHOCOBO_WEAPON_TIER.get(tier)-1;
        return nextLowestTier > 0 ? totalTierDamage(CHOCOBO_WEAPON_TIERS.get(nextLowestTier), out, false) : out;
    }
    public ChocoboWeaponItems(ToolMaterial toolMaterial, float attackSpeed, Settings settings) {
        super(toolMaterial, (totalTierDamage(toolMaterial, CHOCOBO_DAMAGE_MODIFIER, true) - (int)toolMaterial.getAttackDamage()), attackSpeed, settings);
        this.attackSpeed = attackSpeed + toolMaterial.getMiningSpeedMultiplier();
    }
    public float getAttackSpeed() { return this.attackSpeed; }
    public boolean isFireproof() {
        boolean netherite = this.getMaterial() == ToolMaterials.NETHERITE;
        if (netherite) { return true; }
        return super.isFireproof();
    }
}