package net.cat_metalhead.stringless.mixin;

import net.cat_metalhead.stringless.StringlessRenderHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public class SodiumBlockRendererMixin {

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void stringless$skipTripwire(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin,
            CallbackInfo ci) {
        if (state.getBlock() instanceof TripwireBlock && StringlessRenderHelper.shouldHideThisFrame()) {
            ci.cancel();
        }
    }
}