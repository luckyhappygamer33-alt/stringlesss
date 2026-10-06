package net.cat_metalhead.stringless;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.util.math.ChunkSectionPos;

public class StringlessSodiumCompat {
    public static final Set<Long> TRIPWIRE_SECTIONS = ConcurrentHashMap.newKeySet();

    public static void rebuildTripwireSections() {
        SodiumWorldRenderer renderer = SodiumWorldRenderer.instanceNullable();
        if (renderer == null)
            return;

        for (long key : TRIPWIRE_SECTIONS) {
            renderer.scheduleRebuildForChunk(
                    ChunkSectionPos.unpackX(key),
                    ChunkSectionPos.unpackY(key),
                    ChunkSectionPos.unpackZ(key),
                    true);
        }
    }

    public static void clear() {
        TRIPWIRE_SECTIONS.clear();
    }
}
