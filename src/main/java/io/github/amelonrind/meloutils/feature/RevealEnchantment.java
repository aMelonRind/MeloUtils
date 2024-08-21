package io.github.amelonrind.meloutils.feature;

import io.github.amelonrind.meloutils.config.Config;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.amelonrind.meloutils.MeloUtils.mc;
import static net.minecraft.enchantment.Enchantments.*;

public class RevealEnchantment {
    public static List<RegistryKey<Enchantment>> LIST1204 = List.of(
            PROTECTION, FIRE_PROTECTION, FEATHER_FALLING, BLAST_PROTECTION, PROJECTILE_PROTECTION, RESPIRATION,
            AQUA_AFFINITY, THORNS, DEPTH_STRIDER, SHARPNESS, SMITE, BANE_OF_ARTHROPODS, KNOCKBACK, FIRE_ASPECT,
            LOOTING, SWEEPING_EDGE, EFFICIENCY, SILK_TOUCH, UNBREAKING, FORTUNE, POWER, PUNCH, FLAME, INFINITY,
            LUCK_OF_THE_SEA, LURE, LOYALTY, IMPALING, RIPTIDE, CHANNELING, MULTISHOT, QUICK_CHARGE, PIERCING
    );
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
    private static final WeakHashMap<EnchantmentScreenHandler, List<List<Text>>> allPowerPredictions = new WeakHashMap<>();

    public static StringVisitable getPhrase(EnchantmentScreenHandler handler, int index) {
        assert mc.world != null;
        return mc.world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT).getEntry(handler.enchantmentId[index])
                .map(ref -> Enchantment.getName(ref, handler.enchantmentLevel[index]))
                .orElseGet(Text::empty);
    }

    public static void appendText(List<Text> list, int index, EnchantmentScreenHandler handler, DrawContext context) {
        if (!Config.get().revealEnchantment) return;
        ItemStack stack = handler.getSlot(0).getStack();
        update(handler, stack);
        list.addAll(texts[index]);

        renderAllPowerPredictions(handler, stack, context);
    }

    private static void update(EnchantmentScreenHandler handler, ItemStack stack) {
        boolean unchanged = true;
        if (!ItemStack.areEqual(lastStack, stack)) {
            lastStack = stack.copy();
            allPowerPredictions.clear();
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
        boolean doMulti = false;
        if (!seedFound) {
            if (hasIrregularData) {
                appendHeader(Text.translatable("meloutils.revealEnchantment.irregularData").formatted(Formatting.DARK_GRAY));
            }
            if (reducedSeeds.length > 0) {
                appendHeader(Text.translatable("meloutils.revealEnchantment.crackProgress", reducedSeeds.length).formatted(Formatting.DARK_GRAY));
                if (reducedSeeds.length < 5) doMulti = true;
            }
            if (!doMulti) return;
        }

        Supplier<Stream<RegistryEntry<Enchantment>>> streamSupplier = getEnchantmentList(world);
        Random random = new FastRandom();
        Text seedHeader = Text.translatable("meloutils.revealEnchantment.seed", seed).formatted(Formatting.DARK_GRAY);
        if (doMulti) {
            for (int seed : reducedSeeds) {
                appendEnchantmentPrediction(stack, random, seed, seedHeader, streamSupplier);
            }
        } else {
            appendEnchantmentPrediction(stack, random, seed, seedHeader, streamSupplier);
        }
    }

    private static void appendHeader(Text text) {
        for (List<Text> list : texts) list.add(text);
    }

    private static void appendEnchantmentPrediction(ItemStack stack, Random random, int seed, Text seedHeader, Supplier<Stream<RegistryEntry<Enchantment>>> streamSupplier) {
        Text asterisk = Text.literal(" *").formatted(Formatting.GOLD);
        for (int i = 0; i < 3; i++) {
            List<Text> t = texts[i];
            if (powers[i] <= 0) continue;
            List<EnchantmentLevelEntry> list = generateEnchantments(streamSupplier.get(), stack, i, powers[i], random, seed);
            if (list.isEmpty()) continue;

            EnchantmentLevelEntry e = list.size() == 1 ? null : list.get(random.nextInt(list.size()));
            t.add(ScreenTexts.EMPTY);
            t.add(seedHeader);
            for (EnchantmentLevelEntry entry : list) {
                Text name = Enchantment.getName(entry.enchantment, entry.level);
                t.add(entry == e ? Text.empty().append(name).append(asterisk) : name);
            }
        }
    }

    private static void renderAllPowerPredictions(EnchantmentScreenHandler handler, ItemStack stack, DrawContext context) {
        assert mc.world != null;
        if (!Screen.hasAltDown()) return;
        if (!seedFound) return;
        // since the seed wouldn't be found if the item is invalid, we skip the checks here

        List<List<Text>> listOfTexts = allPowerPredictions.get(handler);
        if (listOfTexts == null) {
            listOfTexts = new ArrayList<>();
            allPowerPredictions.put(handler, listOfTexts);

            // iterate through possible shelves and slots to generate contexts
            long raw = FastRandom.getRawSeed(seed);
            FastRandom random = new FastRandom();
            Set<EnchantingContext> contexts = new HashSet<>();
            for (int shelves = 15; shelves >= 0; shelves--) {
                random.setRawSeed(raw);
                for (int i = 0; i < 3; i++) {
                    int power = PowerInfo.getPower(random, i, shelves);
                    if (power > 0) {
                        contexts.add(new EnchantingContext(i, power, shelves));
                    }
                }
            }

            // generate enchantments
            Supplier<Stream<RegistryEntry<Enchantment>>> streamSupplier = getEnchantmentList(mc.world);
            Map<Set<ELEWrapper>, Set<EnchantingContext>> map = new HashMap<>();
            for (EnchantingContext ctx : contexts) {
                List<EnchantmentLevelEntry> list = ctx.generate(streamSupplier.get(), stack, random, seed);
                if (list.isEmpty()) continue;
                map.computeIfAbsent(
                        list.stream().map(ELEWrapper::new).collect(Collectors.toUnmodifiableSet()),
                        key -> new HashSet<>()
                ).add(ctx);
            }

            int maxRows = context.getScaledWindowHeight() / 10 - 2;
            List<Text> texts = new ArrayList<>();
            texts.add(Text.translatable("meloutils.revealEnchantment.seed", seed).formatted(Formatting.AQUA));
            texts.add(ScreenTexts.EMPTY);
            Text indent = Text.literal("  ");
            for (Map.Entry<Set<ELEWrapper>, Set<EnchantingContext>> ent : map.entrySet().stream().sorted(Comparator
                        .<Map.Entry<Set<ELEWrapper>, Set<EnchantingContext>>>comparingInt(e -> e.getKey().size())
                        .thenComparingInt(e -> e.getKey().stream().mapToInt(w -> w.base().level).sum()).reversed()
                    ).toList()
            ) {
                Set<EnchantingContext> filtered = ent.getValue();

                // remove bad enchanting contexts
                for (EnchantingContext ctx : Set.copyOf(filtered)) {
                    if (filtered.stream().anyMatch(ctx::defWorseThan)) {
                        filtered.remove(ctx);
                    }
                }

                if (texts.size() + filtered.size() + ent.getKey().size() > maxRows) {
                    listOfTexts.add(texts);
                    texts = new ArrayList<>();
                }

                for (EnchantingContext ctx : new TreeSet<>(filtered)) {
                    texts.add(ctx.toTranslated());
                }
                for (ELEWrapper entry : ent.getKey()) {
                    texts.add(Text.empty().append(indent).append(entry.toTranslated()));
                }
            }
            if (!texts.isEmpty()) listOfTexts.add(texts);
        }

        if (!listOfTexts.isEmpty()) {
            int maxWidth = context.getScaledWindowWidth() - 9;
            int x = -9;
            for (List<Text> texts : listOfTexts) {
                context.drawTooltip(mc.textRenderer, texts, x, 15);
                x += texts.stream().mapToInt(mc.textRenderer::getWidth).max().orElse(0) + 7;
                if (x > maxWidth) break;
            }
        }
    }

    private static Supplier<Stream<RegistryEntry<Enchantment>>> getEnchantmentList(ClientWorld world) {
        Registry<Enchantment> registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
        Optional<RegistryEntryList.Named<Enchantment>> opt = registry.getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (opt.isPresent()) {
            RegistryEntryList.Named<Enchantment> named = opt.get();
            if (named.stream().findFirst().isPresent()) {
                return named::stream;
            }
        }
        List<RegistryEntry<Enchantment>> list = LIST1204.stream()
                .map(registry::getEntry)
                .filter(Optional::isPresent)
                .map(optional -> (RegistryEntry<Enchantment>) optional.get())
                .toList();
        return list::stream;
    }

    private static List<EnchantmentLevelEntry> generateEnchantments(Stream<RegistryEntry<Enchantment>> entryList, ItemStack stack, int slot, int level, Random random, int seed) {
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

    // dont crack by generating enchantments because it's relatively unreliable
    // TODO crack future
    /**
     * @return if the crack process has been reset due to irregular data
     */
    private static boolean crackSeed(int shelves) {
        PowerInfo info = new PowerInfo(shelves, powers[0], powers[1], powers[2]);
        Optional<PowerInfo> opt = powerInfos.stream().filter(p -> p.is(shelves)).findFirst();
        boolean hasIrregularData = false;
        if (opt.isEmpty()) {
            powerInfos.add(info);
        } else if (!opt.get().equals(info)) {
            hasIrregularData = true;
            resetSeeds();
            powerInfos.add(info);
        }
        if (seedFound) return false;
        Random random = new FastRandom();
        IntStream stream;
        if (powerInfos.size() == 1) {
            int seedR = seedRight & 0xFFFF;
            stream = IntStream.rangeClosed(0, 0xFFFF).map(left -> left << 16 | seedR);
        } else {
            stream = Arrays.stream(reducedSeeds);
        }
        reducedSeeds = stream.filter(seed -> info.test(random, seed)).toArray();
        checkReduced();
        return hasIrregularData;
    }

    private static void resetSeeds() {
        seedFound = false;
        reducedSeeds = new int[0];
        powerInfos.clear();
        allPowerPredictions.clear();
    }

    private static void checkReduced() {
        if (reducedSeeds.length == 0) {
            resetSeeds();
        } else if (reducedSeeds.length == 1) {
            seed = reducedSeeds[0];
            seedFound = true;
        }
    }

    public static final class EnchantingContext implements Comparable<EnchantingContext> {
        public final int slot;
        public final int power;
        private int shelves;

        public EnchantingContext(int slot, int power, int shelves) {
            this.slot = slot;
            this.power = power;
            this.shelves = shelves;
        }

        public Text toTranslated() {
            return Text.translatable("meloutils.revealEnchantment.allPower.info", slot + 1, power, shelves).formatted(Formatting.GREEN);
        }

        public List<EnchantmentLevelEntry> generate(Stream<RegistryEntry<Enchantment>> entryList, ItemStack stack, Random random, int seed) {
            return generateEnchantments(entryList, stack, slot, power, random, seed);
        }

        public boolean defWorseThan(EnchantingContext other) {
            return other.slot <= slot && other.power <= power && other.shelves <= shelves && this != other;
        }

        @Override
        public int compareTo(@NotNull EnchantingContext b) {
            if (slot != b.slot) return slot < b.slot ? -1 : 1;
            if (power != b.power) return power < b.power ? -1 : 1;
            if (shelves != b.shelves) return shelves < b.shelves ? -1 : 1;
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (EnchantingContext) obj;
            if (this.slot != that.slot || this.power != that.power) return false;
            if (this.shelves != that.shelves) {
                if (this.shelves < that.shelves) {
                    that.shelves = this.shelves;
                } else {
                    this.shelves = that.shelves;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(slot, power);
        }
    }

    public record ELEWrapper(EnchantmentLevelEntry base) {

        public Text toTranslated() {
            return Enchantment.getName(base.enchantment, base.level);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ELEWrapper that = (ELEWrapper) o;
            return Objects.equals(base.enchantment.value(), that.base.enchantment.value()) && base.level == that.base.level;
        }

        @Override
        public int hashCode() {
            return Objects.hash(base.enchantment.value(), base.level);
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

        public boolean test(Random random, int seed) {
            random.setSeed(seed);
            return test(random);
        }

        public boolean test(Random random) {
            return getPower(random, 0, shelves) == power0
                    && getPower(random, 1, shelves) == power1
                    && getPower(random, 2, shelves) == power2;
        }

        public boolean is(int shelves) {
            return shelves == this.shelves;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != PowerInfo.class) return false;
            PowerInfo o = (PowerInfo) other;
            return shelves == o.shelves && power0 == o.power0 && power1 == o.power1 && power2 == o.power2;
        }
    }

    @SuppressWarnings("unused")
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
