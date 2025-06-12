package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.common.entities.Chocobo;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import static com.dephoegon.delbase.aid.util.kb.isKB_KeyBindDown;
import static com.dephoegon.delchoco.client.keybind.KeyBindManager.L_ALT;

public class chocoKB {
    private static boolean LAlt() { return isKB_KeyBindDown(L_ALT); }
    public static boolean isChocoboWaterGlide() {  return LAlt(); }
    // Shows Chocobo if Has no riders
    // if the rider isn't the controlling side for movement of the chocobo,
    // & if the game camera is in third person (any camera that is not first person)
    public static boolean showChocobo(@NotNull Chocobo chocobo) { return !chocobo.isLogicalSideForUpdatingMovement() || MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson(); }
}