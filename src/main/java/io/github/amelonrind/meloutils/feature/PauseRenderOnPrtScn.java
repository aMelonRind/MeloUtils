package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;

public class PauseRenderOnPrtScn {
    public static long timeoutNanos = 5_000_000_000L;
    private static boolean pressed = false;
    private static long last = 0L;

    public static void onPrtScn() {
        last = System.nanoTime();
        pressed = true;
    }

    public static boolean check() {
        if (!pressed) return true;
        if (!Config.get().pauseRenderOnPrtScn || System.nanoTime() - last >= timeoutNanos) {
            pressed = false;
            return true;
        }
        return false;
    }

}
