package com.dephoegon.delchoco.common.init;

import com.dephoegon.delchoco.DelChoco;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent AMBIENT_SOUND = register("entity.chocobo.kweh");
    public static final SoundEvent WHISTLE_SOUND_FOLLOW = register("entity.chocobo.kwehwhistlefollow");
    public static final SoundEvent WHISTLE_SOUND_STAY = register("entity.chocobo.kwehwhistlestay");
    public static final SoundEvent WHISTLE_SOUND_WANDER = register("entity.chocobo.kwehwhistlewander");

    private static SoundEvent register(String id) {
        Identifier identifier = new Identifier(DelChoco.DELCHOCO_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
    public static void registerSounds() {
        DelChoco.LOGGER.info("Registering Sounds");
    }
}