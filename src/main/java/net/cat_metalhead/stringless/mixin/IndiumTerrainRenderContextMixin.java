package net.cat_metalhead.stringless.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.cat_metalhead.stringless.StringlessRenderHelper;
import net.minecraft.block.TripwireBlock;

@Mixin(targets = "link.infra.indium.renderer.render.TerrainRenderContext", remap = false)
public class IndiumTerrainRenderContextMixin {

    @Inject(method = "tessellateBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void stringless$skipTripwire(BlockRenderContext ctx, CallbackInfo ci) {
        if (ctx.state().getBlock() instanceof TripwireBlock && StringlessRenderHelper.shouldHideThisFrame()) {
            ci.cancel();
        }
    }
}
