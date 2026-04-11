package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.WindChargeRenderSuppressor;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderManager.class)
public class MixinEntityRenderManager {

    @Inject(method = "shouldRender", at = @At("TAIL"), cancellable = true)
    private void shouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && !WindChargeRenderSuppressor.shouldRender(entity)) {
            cir.setReturnValue(false);
        }
    }
}
