package com.axin.axinkeyboardwizard.services.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.platform.win32.WinUser.KEYBDINPUT;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindowsKeyboardInputTests {

  @Test
  void fPressAndReleaseUsePhysicalScanCode() {
    INPUT press = WindowsKeyboardInput.createInput(NativeKeyEvent.VC_F, false);
    INPUT release = WindowsKeyboardInput.createInput(NativeKeyEvent.VC_F, true);

    assertEquals(INPUT.INPUT_KEYBOARD, press.type.intValue());
    assertEquals(0, press.input.ki.wVk.intValue());
    assertEquals(0x21, press.input.ki.wScan.intValue());
    assertEquals(press.input.ki.wScan, release.input.ki.wScan);
    assertEquals(KEYBDINPUT.KEYEVENTF_SCANCODE, press.input.ki.dwFlags.intValue());
    assertEquals(KEYBDINPUT.KEYEVENTF_SCANCODE | KEYBDINPUT.KEYEVENTF_KEYUP,
            release.input.ki.dwFlags.intValue());
    // Exercise native structure serialization without loading Windows user32.
    press.write();
    press.read();
    assertEquals(0x21, press.input.ki.wScan.intValue());
  }

  @Test
  void rejectsUnknownKeysInsteadOfSendingAnUnrelatedKey() {
    assertThrows(IllegalArgumentException.class,
            () -> WindowsKeyboardInput.createInput(-1, false));
  }
}
