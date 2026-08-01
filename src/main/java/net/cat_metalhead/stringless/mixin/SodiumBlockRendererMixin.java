package net.cat_metalhead.stringless.mixin;

import net.cat_metalhead.stringless.StringlessRenderHelper;
import net.minecraft.block.TripwireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;

@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public class SodiumBlockRendererMixin {

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void stringless$skipTripwire(BlockRenderContext ctx, ChunkBuildBuffers buffers, CallbackInfo ci) {
        if (ctx.state().getBlock() instanceof TripwireBlock && StringlessRenderHelper.shouldHideThisFrame()) {
            ci.cancel();
        }
    }
}