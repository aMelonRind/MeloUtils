package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import org.lwjgl.glfw.GLFWScrollCallbackI;

public class FixHorizontalScroll {

    public static GLFWScrollCallbackI wrapCallback(GLFWScrollCallbackI original) {
        return (handle, dx, dy) -> {
            if (dx != 0 && Config.get().fixHorizontalScroll) {
                if (dy == 0) {
                    dy = dx;
                    dx = 0;
                } else {
                    dx = -dx;
                }
            }
            original.invoke(handle, dx, dy);
        };
    }
}
