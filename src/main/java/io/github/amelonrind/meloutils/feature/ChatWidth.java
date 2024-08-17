package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

import static io.github.amelonrind.meloutils.MeloUtils.mc;

public class ChatWidth {
    private static int lastWidth = 40;
    private static boolean shouldUpdateFlexWidth = true;
    private static int flexWidth = 0;

    public static void onGetChatWidth(CallbackInfoReturnable<Integer> cir, Supplier<Boolean> isFocused, Runnable changeCb, List<ChatHudLine> messages) {
        int width = cir.getReturnValueI();
        float mul = Config.get().chatWidthMultiplier;
        if (mul == 1.0f || !isFocused.get()) {
            if (lastWidth != width) {
                lastWidth = width;
                changeCb.run();
            }
            return;
        }

        int width2 = Math.max(40, Math.min((int) Math.ceil(width * mul), mc.getWindow().getScaledWidth() - 20));

        if (Config.get().chatWidthFlex) {
            if (shouldUpdateFlexWidth) {
                flexWidth = messages.stream().mapToInt(l -> mc.textRenderer.getWidth(l.content())).max().orElse(30) + 10;
                shouldUpdateFlexWidth = false;
            }
            width2 = Math.min(Math.max(width, flexWidth), width2);
        }

        if (lastWidth != width2) {
            lastWidth = width2;
            changeCb.run();
        }

        cir.setReturnValue(width2);
    }

    public static void onChatMessagesChange() {
        shouldUpdateFlexWidth = true;
    }

}
