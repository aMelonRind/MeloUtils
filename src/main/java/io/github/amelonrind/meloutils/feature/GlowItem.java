package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;

import java.util.function.Supplier;

public class GlowItem {

    private static boolean shouldGlow() {
        return Config.get().glowItem && PauseRenderOnPrtScn.check();
    }

    public static boolean onIsGlowing(Supplier<Boolean> def) {
        return shouldGlow() || def.get();
    }

    public static int onGetTeamColorValue(int def) {
        return shouldGlow() && def == 0xFFFFFF ? Config.get().glowItemColor.getRGB() : def;
    }

}
