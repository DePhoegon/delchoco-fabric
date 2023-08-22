package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModAttributes {
    public static final EntityAttribute CHOCOBO_MAX_STAMINA = Registry.register(
            Registry.ATTRIBUTE,
            new Identifier(DelChoco.DELCHOCO_ID, "chocobo_max_stamina"),
            new ClampedEntityAttribute("chocobo.maxStamina", 10.0D, 10D, 1024.0D).setTracked(true)
    );
}