package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.amelonrind.meloutils.feature.RevealEnchantment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnchantmentScreen.class)
public class MixinEnchantmentScreen {

    @Inject(method = "render", at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER, target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void onTooltip(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local List<Text> list, @Local(ordinal = 3) int index) {
        RevealEnchantment.appendText(list, index, ((EnchantmentScreen) (Object) this).getScreenHandler(), context);
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantingPhrases;generatePhrase(Lnet/minecraft/client/font/TextRenderer;I)Lnet/minecraft/text/StringVisitable;"))
    private StringVisitable onPhrase(EnchantingPhrases instance, TextRenderer textRenderer, int width, @Local(ordinal = 5) int index) {
        return RevealEnchantment.getPhrase(((EnchantmentScreen) (Object) this).getScreenHandler(), index);
    }

}
