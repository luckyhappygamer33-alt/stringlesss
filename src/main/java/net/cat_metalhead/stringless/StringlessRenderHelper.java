package net.cat_metalhead.stringless;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.block.TripwireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.loader.api.FabricLoader;

public class StringlessRenderHelper {

    // Cached state for chunk build thread (runs off main thread)
    public static volatile boolean cachedShouldHide = false;
    private static boolean lastHideState = false;

    // Called from chunk build thread (Indigo/Sodium mixin)
    public static boolean shouldHide() {
        return cachedShouldHide;
    }

    // Called from render thread every frame (only used if per-frame layer skip is
    // available)
    private static void scheduleRebuild(MinecraftClient client) {
        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            int renderDist = client.options.getClampedViewDistance() * 16;
            BlockPos p = client.player.getBlockPos();
            SodiumWorldRenderer.instance().scheduleRebuildForBlockArea(
                    p.getX() - renderDist, client.world.getBottomY(), p.getZ() - renderDist,
                    p.getX() + renderDist, client.world.getTopYInclusive(), p.getZ() + renderDist,
                    true);
        } else {
            int renderDist = client.options.getClampedViewDistance() * 16;
            BlockPos p = client.player.getBlockPos();
            client.worldRenderer.scheduleBlockRenders(
                    p.getX() - renderDist, client.world.getBottomY(), p.getZ() - renderDist,
                    p.getX() + renderDist, client.world.getTopYInclusive(), p.getZ() + renderDist);
        }
    }

    private static boolean isLookingAtTripwire(MinecraftClient client) {
        if (client.crosshairTarget instanceof BlockHitResult hit
                && hit.getType() == HitResult.Type.BLOCK
                && client.world != null) {
            return client.world.getBlockState(hit.getBlockPos()).getBlock() instanceof TripwireBlock;
        }
        return false;
    }

    public static boolean shouldHideThisFrame() {
        MinecraftClient client = MinecraftClient.getInstance();
        ModConfig cfg = ModConfig.HANDLER.instance();

        return switch (cfg.renderMode) {
            case ALWAYS_VISIBLE -> false;
            case ALWAYS_HIDDEN -> true;
            case HOLDING_STRING -> {
                if (client.player == null)
                    yield false;
                yield client.player.getMainHandStack().getItem() != Items.STRING
                        && client.player.getOffHandStack().getItem() != Items.STRING;
            }
            case LOOKING_AT -> !isLookingAtTripwire(client);
        };
    }

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null)
            return;

        boolean shouldHide = shouldHideThisFrame();
        cachedShouldHide = shouldHide;

        if (shouldHide != lastHideState) {
            lastHideState = shouldHide;
            scheduleRebuild(client);
        }
    }

}