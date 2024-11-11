package io.github.amelonrind.meloutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Redirect(method = "onPlayerList", at = @At(value = "INVOKE", remap = false, target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void onPlayerList(Logger instance, String s, Object o, Object o1, @Local(argsOnly = true) PlayerListS2CPacket packet, @Local PlayerListS2CPacket.Entry entry) {
        String actions = packet.getActions().stream().map(action -> switch (action) {
            case UPDATE_GAME_MODE -> "UPDATE_GAME_MODE: " + entry.gameMode().name();
            case UPDATE_LISTED -> entry.listed() ? "UPDATE_LISTED" : "!UPDATE_LISTED";
            case UPDATE_LATENCY -> "UPDATE_LATENCY: " + entry.latency();
            case UPDATE_DISPLAY_NAME -> {
                Text name = entry.displayName();
                yield "UPDATE_DISPLAY_NAME: " + (name == null ? null : name.getString());
            }
            default -> action.name();
        }).collect(Collectors.joining(", "));
        instance.warn("Unknown player info {} ({})", entry.profileId(), actions);
    }

}
