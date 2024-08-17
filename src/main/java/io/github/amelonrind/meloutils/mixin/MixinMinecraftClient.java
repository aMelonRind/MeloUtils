package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.Cooldowns;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow private int itemUseCooldown;

    @Inject(method = "doItemUse", at = @At("RETURN"))
    private void onInteract(CallbackInfo ci) {
        itemUseCooldown = Cooldowns.checkInteractCooldown(itemUseCooldown);
    }

}
