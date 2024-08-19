package io.github.amelonrind.meloutils.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.amelonrind.meloutils.MeloUtils;
import io.github.amelonrind.meloutils.feature.KeepChat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModMenuApiImpl implements ModMenuApi {

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull MutableText translatable(String key) {
        return MeloUtils.translatable("settings." + key);
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull OptionDescription descriptionOf(String key) {
        return OptionDescription.createBuilder()
                .text(translatable(key + ".description"))
                .build();
    }

    private static Option<Boolean> optionOf(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(translatable(name))
                .description(descriptionOf(name))
                .binding(true, getter, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        Config def = new Config();
        return p -> {
            Config cfg = Config.get();
            return YetAnotherConfigLib.createBuilder()
                    .title(translatable("title"))
                    .category(ConfigCategory.createBuilder()
                            .name(translatable("category.utils"))
                            .option(optionOf("keepChat", () -> cfg.keepChat, val -> {
                                KeepChat.isClearingChatWithF3D = false;
                                cfg.keepChat = val;
                            }))
                            .option(optionOf("glowItem", () -> cfg.glowItem, val -> cfg.glowItem = val))
                            .option(Option.<Color>createBuilder()
                                    .name(translatable("glowItemColor"))
                                    .description(descriptionOf("glowItemColor"))
                                    .binding(def.glowItemColor, () -> cfg.glowItemColor, val -> cfg.glowItemColor = val)
                                    .controller(ColorControllerBuilder::create)
                                    .build())
                            .option(Option.<Float>createBuilder()
                                    .name(translatable("chatWidthMultiplier"))
                                    .description(descriptionOf("chatWidthMultiplier"))
                                    .binding(def.chatWidthMultiplier, () -> cfg.chatWidthMultiplier, val -> cfg.chatWidthMultiplier = val)
                                    .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                            .range(1.0f, 4.0f)
                                            .step(0.05f)
                                            .formatValue(v -> Text.literal(String.format("x%.2f", v))))
                                    .build())
                            .option(optionOf("chatWidthFlex", () -> cfg.chatWidthFlex, val -> cfg.chatWidthFlex = val))
                            .option(Option.<Integer>createBuilder()
                                    .name(translatable("breakCooldown"))
                                    .description(descriptionOf("breakCooldown"))
                                    .binding(def.breakCooldown, () -> cfg.breakCooldown, val -> cfg.breakCooldown = val)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(0, def.breakCooldown)
                                            .step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(translatable("sneakBreakCooldown"))
                                    .description(descriptionOf("sneakBreakCooldown"))
                                    .binding(def.sneakBreakCooldown, () -> cfg.sneakBreakCooldown, val -> cfg.sneakBreakCooldown = val)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(0, def.sneakBreakCooldown)
                                            .step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(translatable("interactCooldown"))
                                    .description(descriptionOf("interactCooldown"))
                                    .binding(def.interactCooldown, () -> cfg.interactCooldown, val -> cfg.interactCooldown = val)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(0, def.interactCooldown)
                                            .step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(translatable("jumpCooldown"))
                                    .description(descriptionOf("jumpCooldown"))
                                    .binding(def.jumpCooldown, () -> cfg.jumpCooldown, val -> cfg.jumpCooldown = val)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(0, def.jumpCooldown)
                                            .step(1))
                                    .build())
                            .option(optionOf("revealEnchantment", () -> cfg.revealEnchantment, val -> cfg.revealEnchantment = val))
                            .build())
                    .save(Config.HANDLER::save)
                    .build()
                    .generateScreen(p);
        };
    }

}
