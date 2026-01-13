package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import static io.github.amelonrind.meloutils.MeloUtils.mc;

public class ChatWidth {
    private static final WeakHashMap<IdentityHashedText, Integer> widthCache = new WeakHashMap<>();
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
                flexWidth = messages.stream().mapToInt(l -> widthCache.computeIfAbsent(new IdentityHashedText(l.content()), IdentityHashedText::getWidth)).max().orElse(30) + 10;
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

    record IdentityHashedText(Text text) {

        public int getWidth() {
            if (text == null) return 0;
            return ChatMessages.breakRenderedChatMessageLines(text, Integer.MAX_VALUE, mc.textRenderer)
                    .stream().mapToInt(mc.textRenderer::getWidth).max().orElse(0);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(text);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return text == ((IdentityHashedText) o).text;
        }

    }

}
