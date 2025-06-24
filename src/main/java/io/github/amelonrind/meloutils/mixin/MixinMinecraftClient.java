package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.amelonrind.meloutils.feature.Cooldowns;
import io.github.amelonrind.meloutils.feature.DisableOffhandInteraction;
import io.github.amelonrind.meloutils.feature.KeepF1NameTag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow private int itemUseCooldown;

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void onAttack(CallbackInfoReturnable<Boolean> cir) {
        Cooldowns.resetBreak();
    }

    @Inject(method = "doItemUse", at = @At("RETURN"))
    private void onInteract(CallbackInfo ci) {
        itemUseCooldown = Cooldowns.checkInteractCooldown(itemUseCooldown);
    }

    @Inject(method = "isHudEnabled", at = @At("RETURN"), cancellable = true)
    private static void isHudEnabled(CallbackInfoReturnable<Boolean> cir) {
        KeepF1NameTag.shouldRenderNameTag(cir);
    }

    @WrapOperation(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult onInteractItem(ClientPlayerInteractionManager instance, PlayerEntity player, Hand hand, Operation<ActionResult> original, @Local ItemStack stack) {
        return DisableOffhandInteraction.onInteract(hand, stack, () -> original.call(instance, player, hand));
    }

}
