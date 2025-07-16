package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.effects.CustomRegenerationEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {

    // Register our custom regeneration effect
    public static final StatusEffect CUSTOM_REGENERATION = Registry.register(
        Registries.STATUS_EFFECT,
        new Identifier(DelChoco.DELCHOCO_ID, "custom_regeneration"),
        new CustomRegenerationEffect()
    );

    /**
     * Initialize and register all mod effects
     */
    public static void init() {
        DelChoco.LOGGER.info("Registering Status Effects for " + DelChoco.DELCHOCO_ID);
        // Registration happens automatically through static field initialization
    }
}
