package io.github.amelonrind.meloutils.mixin;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class MixinShulkerBoxBlockEntity {

    @Shadow private AnimationStage animationStage;

    @Inject(method = "onSyncedBlockEvent", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;viewerCount:I"))
    private void onBlockSync(int type, int data, CallbackInfoReturnable<Boolean> cir) {
        if (data > 1) {
            animationStage = AnimationStage.OPENING;
        }
    }

}
