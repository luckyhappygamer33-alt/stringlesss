package net.cat_metalhead.stringless.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.cat_metalhead.stringless.StringlessRenderHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "renderLayer", at = @At("HEAD"), cancellable = true)
    private void stringless$skipTripwireLayer(
            RenderLayer renderLayer, double x, double y, double z,
            Matrix4f matrix4f, Matrix4f positionMatrix,
            CallbackInfo ci) {
        if (renderLayer == RenderLayer.getTripwire()
                && StringlessRenderHelper.shouldHideThisFrame()) {
            ci.cancel();
        }
    }
}
