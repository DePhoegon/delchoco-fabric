package com.dephoegon.delchoco.aid;

import com.dephoegon.delchoco.client.keybind.KeyBindManager;

import static com.dephoegon.delbase.aid.util.kb.isKB_KeyBindDown;

public class chocoKB {
    private static boolean LShift() { return isKB_KeyBindDown(KeyBindManager.L_SHIFT); }
    private static boolean LCtrl() { return isKB_KeyBindDown(KeyBindManager.L_ALT); }
    public static boolean HShift() { return LShift(); }
    public static boolean HAlt() { return LCtrl(); }
}
