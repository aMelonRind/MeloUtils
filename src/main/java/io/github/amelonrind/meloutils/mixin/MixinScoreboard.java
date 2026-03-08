package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amelonrind.meloutils.feature.SuppressLogs;
import net.minecraft.scoreboard.Scoreboard;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Scoreboard.class)
public class MixinScoreboard {

    @WrapOperation(method = "addTeam(Ljava/lang/String;)Lnet/minecraft/scoreboard/Team;", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void suppressExistingTeam(Logger instance, String s, Object o, Operation<Void> original) {
        if ("Requested creation of existing team '{}'".equals(s)) {
            SuppressLogs.log(() -> original.call(instance, s, o));
        } else {
            original.call(instance, s, o);
        }
    }
}
