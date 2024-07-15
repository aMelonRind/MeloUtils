package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.GlowItem;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        GlowItem.onIsGlowing((Entity) (Object) this, cir);
    }

    @Inject(method = "getTeamColorValue()I", at = @At("HEAD"), cancellable = true)
    public void getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        GlowItem.onGetTeamColorValue((Entity) (Object) this, cir);
    }

}
