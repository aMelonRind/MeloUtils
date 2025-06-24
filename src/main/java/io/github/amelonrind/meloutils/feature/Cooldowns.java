package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;

import static io.github.amelonrind.meloutils.MeloUtils.mc;

public class Cooldowns {
    public static int minBreakCooldown = 5;
    public static int minInteractCooldown = 4;

    public static void resetBreak() {
        Config cfg = Config.get();
        if (cfg.initialBlockActionCooldown) {
            minBreakCooldown = 6;
        } else {
            minBreakCooldown = 0;
        }
    }

    public static void resetInteract() {
        Config cfg = Config.get();
        if (cfg.initialBlockActionCooldown) {
            minInteractCooldown = 5;
        } else {
            minInteractCooldown = 0;
        }
    }

    public static int checkBreakCooldown(int cooldown) {
        if (cooldown != 5) return cooldown;
        if (minBreakCooldown > 0) minBreakCooldown--;
        int value;
        if (mc.player != null && mc.player.isSneaking()) {
            value = Config.get().sneakBreakCooldown;
        } else {
            value = Config.get().breakCooldown;
        }
        return Math.max(value, minBreakCooldown);
    }

    public static int checkInteractCooldown(int cooldown) {
        if (cooldown != 4) return cooldown;
        if (minInteractCooldown > 0) minInteractCooldown--;
        return Math.max(Config.get().interactCooldown, minInteractCooldown);
    }

    public static int checkJumpCooldown(Object entity, int cooldown) {
        return (cooldown == 10 && mc.player == entity) ? Config.get().jumpCooldown : cooldown;
    }

}
