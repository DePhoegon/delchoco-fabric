package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.enchantments.ChocoboSweepEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments extends BaseReg {
    public static final Enchantment CHOCOBO_SWEEP = registerEnchantment("chocobo_sweep", new ChocoboSweepEnchantment());

    private static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(DelChoco.DELCHOCO_ID, name), enchantment);
    }

    public static void registerModEnchantments() {
        DelChoco.LOGGER.info("Registering Mod Enchantments");
    }
}
