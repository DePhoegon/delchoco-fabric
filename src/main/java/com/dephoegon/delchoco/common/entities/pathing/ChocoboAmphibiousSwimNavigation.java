package com.dephoegon.delchoco.common.entities.pathing;

import com.dephoegon.delchoco.common.entities.AbstractChocobo;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.world.World;

public class ChocoboAmphibiousSwimNavigation extends AmphibiousSwimNavigation {
    public ChocoboAmphibiousSwimNavigation(AbstractChocobo chocobo, World world) {
        super(chocobo, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new ChocoboAmphibiousPathNodeMaker((AbstractChocobo) this.entity);
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, range);
    }
}