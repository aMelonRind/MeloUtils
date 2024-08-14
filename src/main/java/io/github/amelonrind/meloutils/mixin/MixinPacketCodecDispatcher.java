package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.amelonrind.meloutils.MeloUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.handler.PacketCodecDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.Charset;

@Mixin(PacketCodecDispatcher.class)
public class MixinPacketCodecDispatcher {

    @Inject(method = "decode(Lio/netty/buffer/ByteBuf;)Ljava/lang/Object;", at = @At(value = "INVOKE", target = "Ljava/lang/String;valueOf(Ljava/lang/Object;)Ljava/lang/String;"), cancellable = true)
    public <B extends ByteBuf, V> void onDecodeFail(B byteBuf, CallbackInfoReturnable<V> cir, @Local Exception e) {
        String msg = e.getMessage();
        if (msg.length() != 64) return;
        if (msg.startsWith("Non [a-z0-9/._-] character in path of location: minecraft:\\u")) {
            MeloUtils.LOGGER.warn("failed to decode packet with identifier error. buffer: {}", byteBuf.toString(Charset.defaultCharset()));
            cir.setReturnValue(null);
        }
    }

}
