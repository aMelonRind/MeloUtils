package io.github.amelonrind.meloutils.feature;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;

import static net.minecraft.item.Items.*;

public class BrilliantwStackable {
    private static final Set<Item> STACKABLE_ITEMS = ImmutableSet.of(
            POTION, SPLASH_POTION, LINGERING_POTION,
            LEATHER_HORSE_ARMOR, IRON_HORSE_ARMOR, GOLDEN_HORSE_ARMOR, DIAMOND_HORSE_ARMOR,
            MINECART, HOPPER_MINECART, CHEST_MINECART, FURNACE_MINECART, TNT_MINECART, COMMAND_BLOCK_MINECART,

            OAK_BOAT, OAK_CHEST_BOAT, SPRUCE_BOAT, SPRUCE_CHEST_BOAT, BIRCH_BOAT, BIRCH_CHEST_BOAT,
            JUNGLE_BOAT, JUNGLE_CHEST_BOAT, ACACIA_BOAT, ACACIA_CHEST_BOAT, CHERRY_BOAT, CHERRY_CHEST_BOAT,
            DARK_OAK_BOAT, DARK_OAK_CHEST_BOAT, MANGROVE_BOAT, MANGROVE_CHEST_BOAT,

            WHITE_BED, ORANGE_BED, MAGENTA_BED, LIGHT_BLUE_BED, YELLOW_BED, LIME_BED, PINK_BED, GRAY_BED,
            LIGHT_GRAY_BED, CYAN_BED, PURPLE_BED, BLUE_BED, BROWN_BED, GREEN_BED, RED_BED, BLACK_BED,

            SHULKER_BOX, WHITE_SHULKER_BOX, ORANGE_SHULKER_BOX, MAGENTA_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX,
            YELLOW_SHULKER_BOX, LIME_SHULKER_BOX, PINK_SHULKER_BOX, GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX,
            CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX, BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, GREEN_SHULKER_BOX,
            RED_SHULKER_BOX, BLACK_SHULKER_BOX,

            BUCKET, WATER_BUCKET, LAVA_BUCKET, POWDER_SNOW_BUCKET, MILK_BUCKET, PUFFERFISH_BUCKET, SALMON_BUCKET,
            COD_BUCKET, TROPICAL_FISH_BUCKET, AXOLOTL_BUCKET, TADPOLE_BUCKET,

            MUSIC_DISC_13, MUSIC_DISC_CAT, MUSIC_DISC_BLOCKS, MUSIC_DISC_CHIRP, MUSIC_DISC_CREATOR,
            MUSIC_DISC_CREATOR_MUSIC_BOX, MUSIC_DISC_FAR, MUSIC_DISC_MALL, MUSIC_DISC_MELLOHI, MUSIC_DISC_STAL,
            MUSIC_DISC_STRAD, MUSIC_DISC_WARD, MUSIC_DISC_11, MUSIC_DISC_WAIT, MUSIC_DISC_OTHERSIDE,
            MUSIC_DISC_RELIC, MUSIC_DISC_5, MUSIC_DISC_PIGSTEP, MUSIC_DISC_PRECIPICE,

            MUSHROOM_STEW, RABBIT_STEW, SUSPICIOUS_STEW,

            SADDLE, CAKE, SNOWBALL, ENDER_PEARL
    );
    public static boolean isInBrilliantw = false;

    public static void onPlayerInit(ClientPlayNetworkHandler networkHandler) {
        isInBrilliantw = Optional.of(networkHandler)
                .map(ClientPlayNetworkHandler::getServerInfo)
                .map(info -> info.address.toLowerCase().contains("brilliantw.net"))
                .orElse(false);
    }

    public static void onGetMaxCount(Item item, CallbackInfoReturnable<Integer> cir) {
        if (isInBrilliantw && STACKABLE_ITEMS.contains(item)) {
            cir.setReturnValue(64);
        }
    }
}
