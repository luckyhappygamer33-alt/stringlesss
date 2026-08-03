package net.cat_metalhead.stringless;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

public class TripwireTracker {

    // Written by compilation threads, read by render thread.
    private static final Map<SectionPos, Set<Entry>> sections = new ConcurrentHashMap<>();
    // Each compilation thread tracks its own current section.
    private static final ThreadLocal<SectionPos> currentSection = new ThreadLocal<>();

    // SODIUM
    private static final ThreadLocal<SectionPos> sodiumCurrentSection = new ThreadLocal<>();
    private static final Map<BlockState, BlockStateModel> stateToModel = new ConcurrentHashMap<>();

    /**
     * private static final Map<SectionPos, Set<Entry>> sections = new
     * ConcurrentHashMap<>();
     * Called at the start of SectionCompiler.compile — clears the section's old
     * entries.
     */
    public static void beginSection(SectionPos pos) {
        currentSection.set(pos);
        sections.put(pos, ConcurrentHashMap.newKeySet());
    }

    private record Entry(BlockPos pos, BlockStateModel model) {
    }

    public static void track(BlockPos pos, BlockState state, BlockStateModel model) {
        SectionPos sec = currentSection.get();
        if (sec == null)
            return;
        Set<Entry> set = sections.get(sec);
        if (set != null)
            set.add(new Entry(pos.immutable(), model));
        stateToModel.put(state, model); // universal cache — survives between recompiles
    }

    public static @Nullable BlockStateModel getModel(BlockPos pos) {
        for (Set<Entry> set : sections.values())
            for (Entry e : set)
                if (e.pos().equals(pos))
                    return e.model();
        return null;
    }

    /** Called at RETURN of SectionCompiler.compile — cleans up empty sections. */
    public static void endSection() {
        SectionPos sec = currentSection.get();
        if (sec != null)
            sections.computeIfPresent(sec, (k, v) -> v.isEmpty() ? null : v);
        currentSection.remove();
    }

    /**
     * Returns all currently tracked tripwire positions across all loaded sections.
     */
    public static List<BlockPos> getAll() {
        List<BlockPos> out = new ArrayList<>();
        for (Set<Entry> s : sections.values())
            for (Entry e : s)
                out.add(e.pos());
        return out;
    }

    /** Call on level unload or mode change that needs a full re-bake. */
    // clear() must NOT wipe stateToModel — those mappings are valid across levels
    public static void clear() {
        sections.clear();
        // stateToModel intentionally kept — it's a universal BlockState→model caches
    }

    // SODIUM
    public static void trackSodium(SectionPos sec, BlockPos pos, BlockState state, BlockStateModel model) {
        SectionPos prev = sodiumCurrentSection.get();
        if (!sec.equals(prev)) {
            // First tripwire seen from this section on this thread — clear its old entries
            sections.put(sec, ConcurrentHashMap.newKeySet());
            sodiumCurrentSection.set(sec);
        }
        Set<Entry> set = sections.get(sec);
        if (set != null)
            set.add(new Entry(pos, model));
        stateToModel.put(state, model);
    }

    // Called from draw() to lazily remove positions where tripwire was broken
    public static void remove(BlockPos pos) {
        for (Set<Entry> set : sections.values())
            set.removeIf(e -> e.pos().equals(pos));
    }

    public static @Nullable BlockStateModel getModelForState(BlockState state) {
        return stateToModel.get(state);
    }
}