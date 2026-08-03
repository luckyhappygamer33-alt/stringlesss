package net.cat_metalhead.stringless;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.cat_metalhead.stringless.ModConfig.RenderMode;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StringlessOverlayRenderer {
    // Called during drawing phase — renders what extraction decided
    public static void draw(LevelRenderContext ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        ClientLevel level = mc.level;
        if (level == null)
            return;

        RenderMode mode = ModConfig.HANDLER.instance().renderMode;
        List<BlockPos> positions = switch (mode) {
            case ALWAYS_VISIBLE -> TripwireTracker.getAll();
            case ALWAYS_HIDDEN -> List.of();
            case HOLDING_STRING -> isHoldingString(mc) ? TripwireTracker.getAll() : List.of();
            case LOOKING_AT -> {
                BlockPos hit = getLookedAtTripwire(mc);
                yield hit != null ? TripwireTracker.getAll() : List.of();
            }
        };
        if (positions.isEmpty())
            return;

        RenderType renderType = RenderTypes.cutoutMovingBlock();
        Vec3 cam = ctx.levelState().cameraRenderState.pos;

        ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(renderType.bufferSize());
        BufferBuilder builder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, renderType.format());

        ModelBlockRenderer renderer = new ModelBlockRenderer(
                mc.options.ambientOcclusion().get(), true, mc.getBlockColors());
        BlockModelLighter.enableCaching();

        BlockQuadOutput output = (x, y, z, quad, instance) -> builder.putBlockBakedQuad(x, y, z, quad, instance);

        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof TripWireBlock)) {
                TripwireTracker.remove(pos);
                continue;
            }
            // Look up by CURRENT state — this is always fresh from the world,
            // so connections are correct even before the section finishes recompiling.
            // Fall back to pos-based lookup only if this exact state was never seen before.
            BlockStateModel model = TripwireTracker.getModelForState(state);
            if (model == null)
                model = TripwireTracker.getModel(pos);
            if (model == null)
                continue;

            renderer.tesselateBlock(output,
                    (float) (pos.getX() - cam.x),
                    (float) (pos.getY() - cam.y),
                    (float) (pos.getZ() - cam.z),
                    level, pos, state, model, state.getSeed(pos));
        }

        BlockModelLighter.clearCache();

        MeshData mesh = builder.build();

        if (mesh == null)
            return;
        if (mesh.drawState().vertexCount() == 0) {
            mesh.close();
            return;
        }

        // draw() internally uses outputColorTextureOverride (set by the frame graph)
        // for the correct render target, handles all uniforms, and closes mesh
        renderType.draw(mesh);// uploads bytes to GPU, then closes mesh for us
        byteBufferBuilder.close(); // ByteBufferBuilder is done
    }

    // ── Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private static boolean isHoldingString(Minecraft mc) {
        return mc.player.getMainHandItem().is(Items.STRING)
                || mc.player.getOffhandItem().is(Items.STRING);
    }

    private static BlockPos getLookedAtTripwire(Minecraft mc) {
        if (!(mc.hitResult instanceof BlockHitResult bhr))
            return null;
        BlockPos pos = bhr.getBlockPos();
        ClientLevel level = mc.level;
        if (level == null)
            return null;
        return level.getBlockState(pos).getBlock() instanceof TripWireBlock ? pos : null;
    }
}