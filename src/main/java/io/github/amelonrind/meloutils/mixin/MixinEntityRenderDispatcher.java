package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.NoItemFrameHitbox;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public abstract class MixinEntityRenderDispatcher {

    @Inject(method = "drawHitbox", at = @At(value = "HEAD"), cancellable = true)
    private void onHitboxCheck(Entity entity, float tickProgress, boolean inLocalServer, CallbackInfo ci) {
        if (NoItemFrameHitbox.shouldNotRenderHitbox(entity)) {
            ci.cancel();
        }
    }

}
