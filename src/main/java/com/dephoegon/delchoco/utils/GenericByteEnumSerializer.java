package com.dephoegon.delchoco.utils;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public class GenericByteEnumSerializer<E extends Enum<E>> implements TrackedDataHandler<E> {
    private final E[] values;

    public GenericByteEnumSerializer(E[] values) { this.values = values; }
    public void write(@NotNull PacketByteBuf buf, @NotNull E value) { buf.writeByte(value.ordinal());}

    public E read(@NotNull PacketByteBuf buf) { return values[buf.readByte()]; }

    public TrackedData<E> create(int i) {
        return new TrackedData<>(i, this);
    }

    public E copy(E var1) { return var1; }
}