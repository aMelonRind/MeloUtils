package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;

public class NoItemFrameHitbox {

    public static boolean shouldNotRenderHitbox(Entity entity) {
        return Config.get().noItemFrameHitbox && entity instanceof ItemFrameEntity;
    }

}
