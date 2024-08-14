package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.handler.DecoderHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DecoderHandler.class)
public class MixinDecoderHandler {

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;getPacketId()Lnet/minecraft/network/packet/PacketType;"), cancellable = true)
    public void onDecodeFail(ChannelHandlerContext context, ByteBuf buf, List<Object> objects, CallbackInfo ci, @Local Packet<?> packet) {
        if (packet == null) ci.cancel();
    }

}
