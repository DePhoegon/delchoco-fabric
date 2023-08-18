package com.dephoegon.delchoco.common.entities.properties;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.utils.GenericByteEnumSerializer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;

public class ModDataSerializers {
    public final static TrackedData<ChocoboColor> CHOCOBO_COLOR = DataTracker.registerData(Chocobo.class, new GenericByteEnumSerializer<>(ChocoboColor.values()));
    public final static TrackedData<MovementType> MOVEMENT_TYPE = DataTracker.registerData(Chocobo.class, new GenericByteEnumSerializer<>(MovementType.values()));
    public static void init() {
        DelChoco.LOGGER.info("Registering Chocobo Enum Serializers");
    }
}