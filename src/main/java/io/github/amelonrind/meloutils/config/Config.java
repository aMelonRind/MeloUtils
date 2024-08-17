package io.github.amelonrind.meloutils.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.github.amelonrind.meloutils.MeloUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.awt.*;

public class Config {
    public static final ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of(MeloUtils.MOD_ID, "main"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve(MeloUtils.MOD_ID).resolve("settings.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static Config get() {
        return HANDLER.instance();
    }

    @SerialEntry(comment = "Keeps chat even if switched server or disconnected. You can still clear chat with F3+D.")
    public boolean keepChat = true;

    @SerialEntry(comment = "Glow dropped items.")
    public boolean glowItem = true;

    @SerialEntry(comment = "The color of glowing item.")
    public Color glowItemColor = new Color(0xFF8C00);

    @SerialEntry(comment = "The width multiplier on focused chat.")
    public float chatWidthMultiplier = 2.0f;

    @SerialEntry(comment = "If true, expand chat width only when needed.")
    public boolean chatWidthFlex = true;

    @SerialEntry(comment = "Break cooldown override.")
    public int breakCooldown = 5;

    @SerialEntry(comment = "Break cooldown override while sneaking.")
    public int sneakBreakCooldown = 5;

    @SerialEntry(comment = "Interact cooldown override.")
    public int interactCooldown = 4;

    @SerialEntry(comment = "Jump cooldown override.")
    public int jumpCooldown = 10;

    public void fixValues() {
        if (chatWidthMultiplier < 1.0f) chatWidthMultiplier = 1.0f;
    }

}
