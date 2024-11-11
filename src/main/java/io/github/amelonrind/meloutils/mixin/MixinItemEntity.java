package io.github.amelonrind.meloutils.mixin;

import io.github.amelonrind.meloutils.feature.GlowItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ItemEntity.class, priority = 1001)
public abstract class MixinItemEntity extends Entity implements Ownable {

    MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isGlowing() {
        return GlowItem.onIsGlowing(super::isGlowing);
    }

    @Override
    public int getTeamColorValue() {
        return GlowItem.onGetTeamColorValue(super.getTeamColorValue());
    }

}
