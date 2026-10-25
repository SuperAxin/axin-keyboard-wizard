package com.axin.axinkeyboardwizard.services.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.platform.win32.WinUser.KEYBDINPUT;

final class WindowsKeyboardInput {

  private WindowsKeyboardInput() {}

  static INPUT createInput(int keyCode, boolean release) {
    // Map only the keys offered by the UI. JNativeHook codes are not Windows VK codes.
    int scanCode = switch (keyCode) {
      case NativeKeyEvent.VC_A -> 0x1E;
      case NativeKeyEvent.VC_B -> 0x30;
      case NativeKeyEvent.VC_C -> 0x2E;
      case NativeKeyEvent.VC_D -> 0x20;
      case NativeKeyEvent.VC_E -> 0x12;
      case NativeKeyEvent.VC_F -> 0x21;
      case NativeKeyEvent.VC_SPACE -> 0x39;
      case NativeKeyEvent.VC_ENTER -> 0x1C;
      case NativeKeyEvent.VC_F1 -> 0x3B;
      case NativeKeyEvent.VC_F2 -> 0x3C;
      case NativeKeyEvent.VC_F3 -> 0x3D;
      case NativeKeyEvent.VC_F4 -> 0x3E;
      case NativeKeyEvent.VC_F5 -> 0x3F;
      default -> throw new IllegalArgumentException("Windows 不支援的按鍵代碼：" + keyCode);
    };

    INPUT input = new INPUT();
    input.type = new DWORD(INPUT.INPUT_KEYBOARD);
    input.input.setType(KEYBDINPUT.class);
    input.input.ki.wVk = new WORD(0);
    input.input.ki.wScan = new WORD(scanCode);
    input.input.ki.dwFlags = new DWORD(KEYBDINPUT.KEYEVENTF_SCANCODE
            | (release ? KEYBDINPUT.KEYEVENTF_KEYUP : 0));
    return input;
  }

  static void send(int keyCode, boolean release) {
    INPUT input = createInput(keyCode, release);
    if (User32.INSTANCE.SendInput(new DWORD(1), new INPUT[]{input}, input.size()).intValue() != 1) {
      int error = Native.getLastError();
      throw new IllegalStateException("Windows 送鍵失敗（錯誤碼 " + error
              + "）。請確認遊戲與本程式的執行權限一致；錯誤碼無法判定是否遭權限限制。");
    }
  }
}
