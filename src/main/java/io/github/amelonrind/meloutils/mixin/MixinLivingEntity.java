package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.Cooldowns;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow private int jumpingCooldown;

    @Inject(method = "tickMovement", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/LivingEntity;jumpingCooldown:I"))
    private void onJumpCd(CallbackInfo ci) {
        jumpingCooldown = Cooldowns.checkJumpCooldown(this, jumpingCooldown);
    }

}
