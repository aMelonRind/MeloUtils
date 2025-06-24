package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.Cooldowns;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.amelonrind.meloutils.MeloUtils.mc;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Inject(method = "setPressed", at = @At("TAIL"))
    private void setPressed(boolean pressed, CallbackInfo ci) {
        if (!pressed) return;
        if ((Object) this == mc.options.useKey) {
            Cooldowns.resetInteract();
        }
    }

}
