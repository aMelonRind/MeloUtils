package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.KeepChat;
import io.github.amelonrind.meloutils.feature.PauseRenderOnPrtScn;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.amelonrind.meloutils.MeloUtils.mc;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {

    @Inject(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private void onPressF3D(KeyInput key, CallbackInfoReturnable<Boolean> cir) {
        KeepChat.onPressF3D();
    }

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        if (window != mc.getWindow().getHandle()) return;
//        switch (key) {
//            case InputUtil.GLFW_KEY_PRINT_SCREEN -> PauseRenderOnPrtScn.onPrtScn();
//        }
        if (input.key() == InputUtil.GLFW_KEY_PRINT_SCREEN
            || SystemKeycodes.IS_MAC_OS && input.hasCtrlOrCmd() && input.key() == InputUtil.GLFW_KEY_4
        ) {
            PauseRenderOnPrtScn.onPrtScn();
        }
    }

}
