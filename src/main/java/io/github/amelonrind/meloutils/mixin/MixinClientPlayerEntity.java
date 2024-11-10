package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amelonrind.meloutils.feature.AutoJump360;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @WrapOperation(method = "autoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getRotationVecClient()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d onAutoJumpAngleDetection(ClientPlayerEntity instance, Operation<Vec3d> original) {
        return AutoJump360.getRotationVec(() -> original.call(instance));
    }

}
