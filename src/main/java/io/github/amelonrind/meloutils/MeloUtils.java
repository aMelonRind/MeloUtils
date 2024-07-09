package io.github.amelonrind.meloutils;

import io.github.amelonrind.meloutils.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MeloUtils implements ClientModInitializer {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final String MOD_ID = "meloutils";
    public static final Logger LOGGER = LogManager.getLogger(MeloUtils.class);
    private static final Text chatPrefix = Text.empty()
            .append(Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
            .append(Text.literal("MeloUtils").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)))
            .append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
            .append(" ");

    @Override
    public void onInitializeClient() {
        Config.HANDLER.load();
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull MutableText translatable(String key) {
        return Text.translatable(MOD_ID + "." + key);
    }

    public static @NotNull MutableText translatable(String key, Object ...args) {
        return Text.translatable(MOD_ID + "." + key, args);
    }

    public static void logTranslatableChat(String key) {
        logChat(translatable(key));
    }

    public static void logTranslatableChat(String key, Object ...args) {
        logChat(translatable(key, args));
    }

    public static void logChat(Text text) {
        mc.inGameHud.getChatHud().addMessage(Text.empty().append(chatPrefix).append(text));
    }

}
