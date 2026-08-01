package net.cat_metalhead.stringless.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.cat_metalhead.stringless.StringlessRenderHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.math.BlockPos;

@Mixin(targets = "net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext", remap = false)
public class IndigoTerrainRenderContextMixin {

    @Inject(method = "bufferModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void stringless$skipTripwire(
            BlockStateModel model, BlockState blockState, BlockPos blockPos,
            CallbackInfo ci) {
        if (blockState.getBlock() instanceof TripwireBlock
                && StringlessRenderHelper.shouldHide()) {
            ci.cancel();
        }
    }
}
