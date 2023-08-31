package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static com.dephoegon.delbase.aid.util.kb.isKB_KeyBindDown;
import static com.dephoegon.delbase.aid.util.kb.keyCheck;
import static com.dephoegon.delchoco.client.keybind.KeyBindManager.L_ALT;
import static com.dephoegon.delchoco.client.keybind.KeyBindManager.L_SHIFT;

public class chocoKB {
    private static boolean LAlt() { return isKB_KeyBindDown(L_ALT); }
    private static boolean RAlt() { return keyCheck(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT); }
    private static boolean ChocoLShift() { return isKB_KeyBindDown(L_SHIFT); }
    private static boolean ChocoRShift() { return keyCheck(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT); }
    public static boolean isChocoboWaterGlide() { if (isKB_KeyBindDown(L_ALT)) { return (RAlt() || LAlt()); } else { return LAlt(); } }
    public static boolean isChocoShiftDown() { if (isKB_KeyBindDown(L_SHIFT)) { return (ChocoLShift() || ChocoRShift()); } else  { return ChocoLShift(); } }
    public static boolean hideChocoboMountInFirstPerson(@NotNull Chocobo chocobo) { return chocobo.isLogicalSideForUpdatingMovement() && !MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson(); }
}
