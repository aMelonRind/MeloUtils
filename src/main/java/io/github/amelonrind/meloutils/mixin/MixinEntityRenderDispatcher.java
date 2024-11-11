package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.amelonrind.meloutils.feature.NoItemFrameHitbox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasReducedDebugInfo()Z"))
    private boolean onHitboxCheck(MinecraftClient instance, @Local(argsOnly = true) Entity entity) {
//        if (NoItemFrameHitbox.shouldNotRenderHitbox(entity)) return true;
//        return false;
        return NoItemFrameHitbox.shouldNotRenderHitbox(entity);
    }

}
