package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static com.dephoegon.delbase.aid.util.kb.isKB_KeyBindDown;
import static com.dephoegon.delbase.aid.util.kb.keyCheck;
import static com.dephoegon.delchoco.client.keybind.KeyBindManager.L_ALT;

public class chocoKB {
    private static boolean LAlt() { return isKB_KeyBindDown(L_ALT); }
    private static boolean RAlt() { return keyCheck(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT); }
    public static boolean isChocoboWaterGlide() { return (RAlt() || LAlt()); }
    public static boolean hideChocoboMountInFirstPerson(@NotNull Chocobo chocobo) { return chocobo.isLogicalSideForUpdatingMovement() && !MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson(); }
}
