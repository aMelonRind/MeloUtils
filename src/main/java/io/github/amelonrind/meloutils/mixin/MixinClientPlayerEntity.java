package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amelonrind.meloutils.feature.AutoJump360;
import io.github.amelonrind.meloutils.feature.BrilliantwStackable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @WrapOperation(method = "autoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getRotationVecClient()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d onAutoJumpAngleDetection(ClientPlayerEntity instance, Operation<Vec3d> original) {
        return AutoJump360.getRotationVec(() -> original.call(instance));
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        BrilliantwStackable.onPlayerInit(this.networkHandler);
    }

}
