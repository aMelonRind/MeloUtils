package io.github.amelonrind.meloutils.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.github.amelonrind.meloutils.MeloUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.Set;

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

    @SerialEntry(comment = "Expand chat width only when needed.")
    public boolean chatWidthFlex = true;

    @SerialEntry(comment = "Prevent any form of item damage from resetting block breaking progress.")
    public boolean preventBreakingReset = true;

    @SerialEntry(comment = "Keep the first few cooldown for block actions.")
    public boolean initialBlockActionCooldown = true;

    @SerialEntry(comment = "Break cooldown override.")
    public int breakCooldown = 5;

    @SerialEntry(comment = "Break cooldown override while sneaking.")
    public int sneakBreakCooldown = 5;

    @SerialEntry(comment = "Interact cooldown override.")
    public int interactCooldown = 4;

    @SerialEntry(comment = "Jump cooldown override.")
    public int jumpCooldown = 10;

    @SerialEntry(comment = "Reveal enchantment in the enchanting table.")
    public boolean revealEnchantment = true;

    @SerialEntry(comment = "Keeps player name tag even when F1 is enabled.")
    public boolean keepF1NameTag = true;

    @SerialEntry(comment = "Don't render item frame hitbox in F3+B.")
    public boolean noItemFrameHitbox = true;

    @SerialEntry(comment = "Enables auto jump in all directions.")
    public boolean autoJump360 = true;

    @SerialEntry(comment = "Pauses some mod render like GlowItem when PrtScn is pressed.")
    public boolean pauseRenderOnPrtScn = true;

    @SerialEntry(comment = "Disables some item's offhand interaction defined in the list.")
    public boolean disableOffhandInteraction = true;

    @SerialEntry(comment = "The list of items for disableOffhandInteraction.")
    public Set<Item> disableOffhandInteractionItems = Set.of(
            Items.SHIELD
    );

    public void fixValues() {
        if (chatWidthMultiplier < 1.0f) chatWidthMultiplier = 1.0f;
    }

}
