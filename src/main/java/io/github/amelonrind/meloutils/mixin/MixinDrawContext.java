package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.amelonrind.meloutils.feature.ShrinkTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2ic;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DrawContext.class)
public class MixinDrawContext {

    @Shadow
    @Final
    private Matrix3x2fStack matrices;

    @WrapOperation(method = "drawTooltipImmediately", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;getPosition(IIIIII)Lorg/joml/Vector2ic;"))
    private Vector2ic checkOob(TooltipPositioner instance, int screenW, int screenH, int x, int y, int w, int h, Operation<Vector2ic> original, @Local(argsOnly = true) List<TooltipComponent> components) {
        return ShrinkTooltip.checkOob(screenW, screenH, w, h, components, (ww, hh) -> original.call(instance, screenW, screenH, x, y, ww, hh));
    }

    @Inject(method = "drawTooltipImmediately", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;render(Lnet/minecraft/client/gui/DrawContext;IIIILnet/minecraft/util/Identifier;)V"))
    private void transformTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, @Nullable Identifier texture, CallbackInfo ci) {
        ShrinkTooltip.transformTooltip(components, matrices);
    }
}
