package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.ChatWidth;
import io.github.amelonrind.meloutils.feature.KeepChat;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Shadow public abstract boolean isChatFocused();

    @Shadow @Final private List<ChatHudLine> messages;

    @Shadow protected abstract void refresh();

    @Shadow public abstract int getWidth();

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void keepMessages(boolean clearHistory, CallbackInfo ci) {
        KeepChat.onClearChat(ci);
    }

    @Inject(method = "getWidth()I", at = @At("RETURN"), cancellable = true)
    private void getWidth(CallbackInfoReturnable<Integer> cir) {
        ChatWidth.onGetChatWidth(cir, this::isChatFocused, this::refresh, messages);
    }

    @Unique private boolean cancelRefresh = false;

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void onRefresh(CallbackInfo ci) {
        if (cancelRefresh) {
            ci.cancel();
            return;
        }
        ChatWidth.onChatMessagesChange();
        cancelRefresh = true;
        getWidth();
        cancelRefresh = false;
    }

    @Inject(method = "addVisibleMessage", at = @At("HEAD"))
    private void onAddVisible(ChatHudLine message, CallbackInfo ci) {
        ChatWidth.onChatMessagesChange();
    }

}
