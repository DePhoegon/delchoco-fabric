package com.dephoegon.delchoco.client.keybind;

import com.dephoegon.delbase.Delbase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyBindManager {
    public static KeyBinding L_ALT = registerKB_Keys("delchoco.key.in_water_glide", GLFW.GLFW_KEY_LEFT_ALT);
    public static KeyBinding L_SHIFT = registerKB_Keys("delchoco.key.stop_in_air_glide", InputUtil.GLFW_KEY_LEFT_SHIFT);

    private static KeyBinding registerKB_Keys(String name, int keyCode) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(name, InputUtil.Type.KEYSYM, keyCode, "tooltip.key.category"));
    }
    public static void registerKeyBinds() { Delbase.LOGGER.info("Registering KeyBinds for "+Delbase.Delbase_ID); }
}
