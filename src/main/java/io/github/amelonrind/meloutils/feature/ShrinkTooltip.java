package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2ic;

import java.util.List;
import java.util.WeakHashMap;
import java.util.function.BiFunction;

public class ShrinkTooltip {
    private static final WeakHashMap<List<TooltipComponent>, TransformInfo> scales = new WeakHashMap<>();

    public static Vector2ic checkOob(int sw, int sh, int w, int h, List<TooltipComponent> ref, BiFunction<Integer, Integer, Vector2ic> positioner) {
        if (!Config.get().shrinkTooltip || w + 8 <= sw && h + 8 < sh) {
            return positioner.apply(w, h);
        } else {
            int guiScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
            int targetScale = Math.min(sw * guiScale / (w + 8), sh * guiScale / (h + 8));
            if (targetScale >= guiScale) {
                return positioner.apply(w, h);
            } else {
                Vector2ic pos = positioner.apply(w * targetScale / guiScale, h * targetScale / guiScale);
                scales.put(ref, new TransformInfo(pos, (float) targetScale / guiScale));
                return pos;
            }
        }
    }

    public static void transformTooltip(List<TooltipComponent> ref, Matrix3x2fStack matrices) {
        TransformInfo info = scales.remove(ref);
        if (info == null) return;

        matrices.translate(info.pos.x(), info.pos.y());
        matrices.scale(info.scale, info.scale);
        matrices.translate(-info.pos.x(), -info.pos.y());
    }

    private record TransformInfo(Vector2ic pos, float scale) {}
}
