package net.cat_metalhead.stringless.mixin;

import net.cat_metalhead.stringless.TripwireTracker;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public class SodiumBlockRendererMixin {

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void stringless$suppressAndTrackTripwire(BlockStateModel model, BlockState state, BlockPos pos,
            BlockPos origin,
            CallbackInfo ci) {
        if (!(state.getBlock() instanceof TripWireBlock))
            return;

        ci.cancel(); // always suppress — overlay handles visibility per-frame

        SectionPos sec = SectionPos.of(origin);
        TripwireTracker.trackSodium(sec, pos.immutable(), state, model);
    }
}