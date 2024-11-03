package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class KeepF1NameTag {

    public static void shouldRenderNameTag(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && Config.get().keepF1NameTag) {
            cir.setReturnValue(true);
        }
    }

}
