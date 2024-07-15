package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GlowItem {

    private static boolean shouldGlow(Entity entity) {
        return entity.getType().equals(EntityType.ITEM) && Config.get().glowItem;
    }

    public static void onIsGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (shouldGlow(entity)) {
            cir.setReturnValue(true);
        }
    }

    public static void onGetTeamColorValue(Entity entity, CallbackInfoReturnable<Integer> cir) {
        if (shouldGlow(entity)) {
            cir.setReturnValue(Config.get().glowItemColor.getRGB());
        }
    }

}
