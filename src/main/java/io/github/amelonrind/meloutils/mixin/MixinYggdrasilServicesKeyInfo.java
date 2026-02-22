package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.security.SignatureException;

@Mixin(YggdrasilServicesKeyInfo.class)
public class MixinYggdrasilServicesKeyInfo {

    @WrapOperation(method = "validateProperty", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void suppressStacktrace(Logger logger, String s, Object o, Object e, Operation<Void> original) {
        if (e instanceof SignatureException se && se.getMessage().startsWith("Bad signature length: ")) {
            logger.warn(s, o);
        } else {
            original.call(logger, s, o, e);
        }
    }
}
