package io.github.amelonrind.meloutils.feature;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

public class PreventBreakingReset {
    private static ComponentChanges cache = ComponentChanges.EMPTY;
    private static ComponentChanges cache2 = ComponentChanges.EMPTY;

    public static boolean checkStackEqual(ItemStack stack, ItemStack stack2, Operation<Boolean> orig) {
        if (Config.get().preventBreakingReset) {
            if (stack == stack2) return true;
            if (!stack.isOf(stack2.getItem())) return false;
            ComponentChanges changes = stack.getComponentChanges();
            ComponentChanges changes2 = stack2.getComponentChanges();
            if (cache.equals(changes) && cache2.equals(changes2)) return true;
            if (changes.equals(changes2) ||
                    changes.withRemovedIf(DataComponentTypes.DAMAGE::equals).equals(
                    changes2.withRemovedIf(DataComponentTypes.DAMAGE::equals)
            )) {
                cache = changes;
                cache2 = changes2;
                return true;
            } else {
                return false;
            }
        } else {
            return orig.call(stack, stack2);
        }
    }

}
