package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.BaseRandom;
import net.minecraft.util.math.random.CheckedRandom.Splitter;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.amelonrind.meloutils.MeloUtils.mc;
import static net.minecraft.enchantment.Enchantments.*;

public class RevealEnchantment {
    private static WeakReference<EnchantmentScreenHandler> lastHandler = new WeakReference<>(null);
    private static ItemStack lastStack = ItemStack.EMPTY;
    private static int seedRight = 0;
    private static int[] reducedSeeds = new int[0];
    private static boolean seedFound = false;
    private static int seed = 0;
    private static final int[] powers = new int[3];
    private static final List<PowerInfo> powerInfos = new ArrayList<>();
    @SuppressWarnings("unchecked")
    private static final List<Text>[] texts = new List[]{ new ArrayList<>(), new ArrayList<>(), new ArrayList<>() };
    public static List<RegistryKey<Enchantment>> list1204 = List.of(
            PROTECTION, FIRE_PROTECTION, FEATHER_FALLING, BLAST_PROTECTION, PROJECTILE_PROTECTION, RESPIRATION,
            AQUA_AFFINITY, THORNS, DEPTH_STRIDER, SHARPNESS, SMITE, BANE_OF_ARTHROPODS, KNOCKBACK, FIRE_ASPECT,
            LOOTING, SWEEPING_EDGE, EFFICIENCY, SILK_TOUCH, UNBREAKING, FORTUNE, POWER, PUNCH, FLAME, INFINITY,
            LUCK_OF_THE_SEA, LURE, LOYALTY, IMPALING, RIPTIDE, CHANNELING, MULTISHOT, QUICK_CHARGE, PIERCING
    );

    public static StringVisitable getPhrase(EnchantmentScreenHandler handler, int index) {
        assert mc.world != null;
        return mc.world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT).getEntry(handler.enchantmentId[index])
                .map(ref -> Enchantment.getName(ref, handler.enchantmentLevel[index]))
                .orElseGet(Text::empty);
    }

    public static void appendText(List<Text> list, int index, EnchantmentScreenHandler handler) {
        if (!Config.get().revealEnchantment) return;
        update(handler);
        list.addAll(texts[index]);
    }

    private static void update(EnchantmentScreenHandler handler) {
        ItemStack stack = handler.getSlot(0).getStack();
        boolean unchanged = true;
        if (!ItemStack.areEqual(lastStack, stack)) {
            lastStack = stack.copy();
            unchanged = false;
        }
        if (lastHandler.get() != handler) {
            lastHandler = new WeakReference<>(handler);
            unchanged = false;
        }
        if (!Arrays.equals(powers, handler.enchantmentPower)) {
            powers[0] = handler.enchantmentPower[0];
            powers[1] = handler.enchantmentPower[1];
            powers[2] = handler.enchantmentPower[2];
            unchanged = false;
        }
        if (seedRight != handler.getSeed()) {
            seedRight = handler.getSeed();
            resetSeeds();
            unchanged = false;
        }
        if (unchanged) return;

        for (List<Text> list : texts) list.clear();
        if (stack.isEmpty() || !stack.isEnchantable() || stack.getItem().getEnchantability() <= 0) return;

        ClientWorld world = mc.world;
        if (world == null) return;

        int shelves = tryGetBookshelves(world);
        if (shelves == -1) {
            appendHeader(Text.translatable("meloutils.revealEnchantment.noShelf").formatted(Formatting.DARK_GRAY));
            return;
        }

        boolean hasIrregularData = crackSeed(shelves);
        if (!seedFound) {
            if (hasIrregularData) {
                appendHeader(Text.translatable("meloutils.revealEnchantment.irregularData").formatted(Formatting.DARK_GRAY));
            } else if (reducedSeeds.length > 0) {
                appendHeader(Text.translatable("meloutils.revealEnchantment.crackProgress", reducedSeeds.length).formatted(Formatting.DARK_GRAY));
            } else {
                appendHeader(Text.translatable("meloutils.revealEnchantment.collectedInfo", powerInfos.size(), 3).formatted(Formatting.DARK_GRAY));
            }
            return;
        } else {
            appendHeader(Text.translatable("meloutils.revealEnchantment.seed", seed).formatted(Formatting.DARK_GRAY));
        }

//        MeloUtils.logChat(Text.literal(String.format("[Debug] Generating enchantment with seed %d and power [%d, %d, %d] on item %s", seed, powers[0], powers[1], powers[2], stack.getName().getString())));

        Registry<Enchantment> registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
        Supplier<Stream<RegistryEntry<Enchantment>>> streamSupplier = getEnchantmentList(registry);

        Random random = new FastRandom();
        for (int i = 0; i < 3; i++) {
            List<Text> t = texts[i];
            if (powers[i] <= 0) continue;
            List<EnchantmentLevelEntry> list = generateEnchantments(streamSupplier.get(), stack, i, powers[i], random);
            if (list != null && !list.isEmpty()) {
//                EnchantmentLevelEntry entry = list.get(random.nextInt(list.size()));
                t.add(ScreenTexts.EMPTY);
                for (EnchantmentLevelEntry entry : list) {
                    t.add(Enchantment.getName(entry.enchantment, entry.level));
                }
            }
        }
    }

    private static void appendHeader(Text text) {
        for (List<Text> list : texts) list.add(text);
    }

    private static Supplier<Stream<RegistryEntry<Enchantment>>> getEnchantmentList(Registry<Enchantment> registry) {
        Optional<RegistryEntryList.Named<Enchantment>> opt = registry.getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (opt.isPresent()) {
            RegistryEntryList.Named<Enchantment> named = opt.get();
            if (named.stream().findFirst().isPresent()) {
                return named::stream;
            }
        }
        List<RegistryEntry<Enchantment>> list = list1204.stream()
                .map(registry::getEntry)
                .filter(Optional::isPresent)
                .map(optional -> (RegistryEntry<Enchantment>) optional.get())
                .toList();
        return list::stream;
    }

    private static List<EnchantmentLevelEntry> generateEnchantments(Stream<RegistryEntry<Enchantment>> entryList, ItemStack stack, int slot, int level, Random random) {
        random.setSeed(seed + slot);

        List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(random, stack, level, entryList);
        if (stack.isOf(Items.BOOK) && list.size() > 1) {
            list.remove(random.nextInt(list.size()));
        }

        return list;
    }

    private static int tryGetBookshelves(ClientWorld world) {
        if (!(mc.crosshairTarget instanceof BlockHitResult bhr) || bhr.getType() == HitResult.Type.MISS) return -1;
        BlockPos pos = bhr.getBlockPos();
        if (!world.getBlockState(pos).isOf(Blocks.ENCHANTING_TABLE)) return -1;
        int count = 0;
        for (BlockPos offs : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (EnchantingTableBlock.canAccessPowerProvider(world, pos, offs)) {
                if (++count >= 15) return 15;
            }
        }
        return count;
    }

    // TODO crack by generating enchantments to reduce info requirement
    // TODO crack future
    private static boolean crackSeed(int shelves) {
        PowerInfo info = new PowerInfo(shelves, powers[0], powers[1], powers[2]);
        Optional<PowerInfo> opt = powerInfos.stream().filter(p -> p.is(shelves)).findFirst();
        boolean hasIrregularData = false;
        if (opt.isEmpty()) {
            powerInfos.add(info);
        } else if (!opt.get().equals(shelves, powers)) {
            hasIrregularData = true;
            resetSeeds();
            powerInfos.add(info);
        }
        if (seedFound) return false;
        int size = powerInfos.size();
        if (size == 3) {
            FastRandom random = new FastRandom();
            int seedR = seedRight & 0xFFFF;
            reducedSeeds = IntStream.rangeClosed(0, 0xFFFF)
                    .map(left -> left << 16 | seedR)
                    .filter(seed -> {
                        long rawSeed = FastRandom.getRawSeed(seed);
                        for (PowerInfo p : powerInfos) {
                            random.setRawSeed(rawSeed);
                            if (!p.test(random)) return false;
                        }
                        return true;
                    }).toArray();
            checkReduced();
        } else if (size > 3) {
            Random random = new FastRandom();
            reducedSeeds = Arrays.stream(reducedSeeds)
                    .filter(seed -> {
                        random.setSeed(seed);
                        return info.test(random);
                    })
                    .toArray();
            checkReduced();
        }
        return hasIrregularData;
    }

    private static void resetSeeds() {
        seedFound = false;
        reducedSeeds = new int[0];
        powerInfos.clear();
    }

    private static void checkReduced() {
        if (reducedSeeds.length == 0) {
            resetSeeds();
        } else if (reducedSeeds.length == 1) {
            seedFound = true;
            seed = reducedSeeds[0];
        }
    }

    public record PowerInfo(int shelves, int power0, int power1, int power2) {
        public static int getPower(Random random, int index, int shelves) {
            int n = random.nextInt(8) + 1 + (shelves >> 1) + random.nextInt(shelves + 1);
            n = switch (index) {
                case 0 -> Math.max(n / 3, 1);
                case 1 -> n * 2 / 3 + 1;
                default -> Math.max(n, shelves * 2);
            };
            return n <= index ? 0 : n;
        }

        public boolean test(Random random) {
            return getPower(random, 0, shelves) == power0
                    && getPower(random, 1, shelves) == power1
                    && getPower(random, 2, shelves) == power2;
        }

        public boolean is(int shelves) {
            return shelves == this.shelves;
        }

        public boolean equals(int shelves, int[] powers) {
            return is(shelves) && powers[0] == power0 && powers[1] == power1 && powers[2] == power2;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != PowerInfo.class) return false;
            PowerInfo o = (PowerInfo) other;
            return shelves == o.shelves && power0 == o.power0 && power1 == o.power1 && power2 == o.power2;
        }
    }

    public static class FastRandom implements BaseRandom {
        private static final int BITS = 48;
        private static final long MASK = 0xFFFFFFFFFFFFL;
        private static final long MUL = 0x5DEECE66DL;
        private long seed = 0;
        private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

        public static long getRawSeed(long seed) {
            return (seed ^ MUL) & MASK;
        }

        public FastRandom() {}

        public FastRandom(long seed) {
            setSeed(seed);
        }

        @Override
        public int next(int bits) {
             this.seed = this.seed * MUL + 11L & MASK;
             return (int)(this.seed >> BITS - bits);
        }

        @Override
        public Random split() {
            return new FastRandom(nextLong());
        }

        @Override
        public RandomSplitter nextSplitter() {
            return new Splitter(nextLong());
        }

        @Override
        public void setSeed(long seed) {
            this.seed = (seed ^ MUL) & MASK;
        }

        public void setRawSeed(long seed) {
            this.seed = seed;
        }

        @Override
        public double nextGaussian() {
            return gaussianGenerator.next();
        }
    }
}
