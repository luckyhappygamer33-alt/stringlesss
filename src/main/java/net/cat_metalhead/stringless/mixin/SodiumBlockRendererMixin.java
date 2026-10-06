package net.cat_metalhead.stringless.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;

import net.cat_metalhead.stringless.StringlessRenderHelper;
import net.cat_metalhead.stringless.StringlessSodiumCompat;
import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public class SodiumBlockRendererMixin {

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void stringless$skipTripwire(BlockRenderContext ctx, ChunkBuildBuffers buffers, CallbackInfo ci) {
        StringlessSodiumCompat.TRIPWIRE_SECTIONS.add(ChunkSectionPos.from(ctx.pos()).asLong());
        if (ctx.state().getBlock() instanceof TripwireBlock && StringlessRenderHelper.shouldHideThisFrame()) {
            ci.cancel();
        }
    }
}