package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.FixHorizontalScroll;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Mouse.class)
public class MixinMouse {

    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setMouseCallbacks(Lnet/minecraft/client/util/Window;Lorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V"))
    private GLFWScrollCallbackI modifyScrollParams(GLFWScrollCallbackI scrollCallback) {
        return FixHorizontalScroll.wrapCallback(scrollCallback);
    }
}
