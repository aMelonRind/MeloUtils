package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;

import static io.github.amelonrind.meloutils.MeloUtils.mc;

public class Cooldowns {

    public static int checkBreakCooldown(int cooldown) {
        if (cooldown != 5) return cooldown;
        if (mc.player != null && mc.player.isSneaking()) {
            return Config.get().sneakBreakCooldown;
        } else {
            return Config.get().breakCooldown;
        }
    }

    public static int checkInteractCooldown(int cooldown) {
        return cooldown == 4 ? Config.get().interactCooldown : cooldown;
    }

    public static int checkJumpCooldown(Object entity, int cooldown) {
        return (cooldown == 10 && mc.player == entity) ? Config.get().jumpCooldown : cooldown;
    }

}
