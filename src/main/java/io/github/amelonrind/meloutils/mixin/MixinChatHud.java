package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.KeepChat;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void keepMessages(boolean clearHistory, CallbackInfo ci) {
        KeepChat.onClearChat(ci);
    }

}
