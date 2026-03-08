package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amelonrind.meloutils.feature.SuppressLogs;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/client/texture/PlayerSkinProvider$1")
public class MixinPlayerSkinProvider {

    @WrapOperation(method = "method_54647", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
    private static void suppressInvalidSig(Logger instance, String s, Object o, Operation<Void> original) {
        if ("Profile contained invalid signature for textures property (profile id: {})".equals(s)) {
            SuppressLogs.log(() -> original.call(instance, s, o));
        } else {
            original.call(instance, s, o);
        }
    }
}
