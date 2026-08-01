package net.cat_metalhead.stringless;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.block.TripwireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class StringlessRenderHelper {

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

    private static boolean isLookingAtTripwire(MinecraftClient client) {
        if (client.crosshairTarget instanceof BlockHitResult hit
                && hit.getType() == HitResult.Type.BLOCK
                && client.world != null) {
            return client.world.getBlockState(hit.getBlockPos()).getBlock() instanceof TripwireBlock;
        }
        return false;
    }

    // ONLY IF USING SODIUM - called from tick
    private static boolean lastSodiumHideState = false;

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null)
            return;

        boolean shouldHide = shouldHideThisFrame();
        if (shouldHide != lastSodiumHideState) {
            lastSodiumHideState = shouldHide;
            // trigger via block update which Sodium always responds to
            // rebuild all chunks in render distance
            int renderDist = client.options.getClampedViewDistance() * 16;
            BlockPos p = client.player.getBlockPos();

            SodiumWorldRenderer.instance().scheduleRebuildForBlockArea(
                    p.getX() - renderDist, client.world.getBottomY(), p.getZ() - renderDist,
                    p.getX() + renderDist, client.world.getTopY(), p.getZ() + renderDist,
                    true // important = high priority rebuild
            );
        }
    }

}