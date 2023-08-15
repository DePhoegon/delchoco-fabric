package com.dephoegon.delchoco.common.items;

import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.entities.properties.ChocoboColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ChocoboSpawnEggItem extends Item {
    private final ChocoboColor color;
    public ChocoboSpawnEggItem(Settings settings, ChocoboColor color) {
        super(settings);
        this.color = color;
    }
    private Text name(@NotNull ItemStack egg) { return egg.getName(); }
    public ActionResult useOnBlock(ItemUsageContext context) {
        World worldin = context.getWorld();
        if (worldin.isClient()) { return ActionResult.SUCCESS; }

        final Chocobo chocobo;
    }
}