package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.dMAX_STAMINA;

public class ModAttributes {
    public static final EntityAttribute CHOCOBO_MAX_STAMINA = Registry.register(
            Registries.ATTRIBUTE,
            new Identifier(DelChoco.DELCHOCO_ID, "chocobo_max_stamina"),
            new ClampedEntityAttribute("chocobo.maxStamina", dMAX_STAMINA.getDefault(), dMAX_STAMINA.getMin(), dMAX_STAMINA.getMax()).setTracked(true)
    );
}