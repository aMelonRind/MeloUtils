package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Enchantment.class)
public class MixinEnchantment {

    @Redirect(method="getName", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    private static MutableText levelString(String key, @Local(argsOnly = true) int level) {
        return Text.translatableWithFallback(key, String.valueOf(level));
    }

}
