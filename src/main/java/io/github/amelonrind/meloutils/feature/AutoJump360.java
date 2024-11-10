package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.util.math.Vec3d;

import java.util.function.Supplier;

public class AutoJump360 {

    public static Vec3d getRotationVec(Supplier<Vec3d> def) {
        return Config.get().autoJump360 ? Vec3d.ZERO : def.get();
    }

}
