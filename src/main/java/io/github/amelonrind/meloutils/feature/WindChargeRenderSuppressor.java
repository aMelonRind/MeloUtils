package io.github.amelonrind.meloutils.feature;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.util.math.Vec3d;

public class WindChargeRenderSuppressor {
    private static final Object2ByteMap<Vec3d> count = new Object2ByteOpenHashMap<>();

    public static void clear() {
        count.clear();
    }

    public static boolean shouldRender(Entity e) {
        if (!(e instanceof AbstractWindChargeEntity)) return true;
        Vec3d pos = e.getEntityPos();
        byte amt = count.getByte(pos);
        if (amt > 5) {
            return false;
        } else {
            count.put(pos, (byte) (amt + 1));
            return true;
        }
    }
}
