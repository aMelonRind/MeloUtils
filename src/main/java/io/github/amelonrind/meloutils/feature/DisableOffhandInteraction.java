package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

public class DisableOffhandInteraction {

    public static ActionResult onInteract(Hand hand, ItemStack stack, Supplier<ActionResult> original) {
        if (hand == Hand.OFF_HAND
                && Config.get().disableOffhandInteraction
                && Config.get().disableOffhandInteractionItems.contains(stack.getItem())) {
            return ActionResult.FAIL;
        }
        return original.get();
    }

}
