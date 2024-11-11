package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.Cooldowns;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Shadow private int blockBreakingCooldown;

    @Unique
    private void checkCooldown() {
        blockBreakingCooldown = Cooldowns.checkBreakCooldown(blockBreakingCooldown);
    }

    @Inject(method = "attackBlock", at = @At("RETURN"))
    private void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        checkCooldown();
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("RETURN"))
    private void onUpdateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        checkCooldown();
    }

}
