package net.cat_metalhead.stringless.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringlessMixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger("stringless");
    private static final boolean SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
    private static final boolean INDIUM = FabricLoader.getInstance().isModLoaded("indium");

    // actually used methods
    @Override
    public boolean shouldApplyMixin(String targetClass, String mixinClass) {
        if (mixinClass.endsWith("SodiumBlockRendererMixin"))
            return SODIUM;
        if (mixinClass.endsWith("IndiumTerrainRenderContextMixin"))
            return SODIUM && INDIUM;
        if (mixinClass.endsWith("WorldRendererMixin"))
            return !SODIUM;
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
        if (SODIUM && INDIUM) {
            LOGGER.info("Sodium + Indium detected - enabling Sodium and FRAPI render hooks.");
        } else if (SODIUM) {
            LOGGER.info("Sodium detected - using Sodium renderer mixin.");
        } else {
            LOGGER.info("Sodium not detected - using vanilla renderer mixin.");
        }
    }

    // rest of methods is present just because of interface implemented
    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String target, org.objectweb.asm.tree.ClassNode cls, String mixin, IMixinInfo info) {
    }

    @Override
    public void postApply(String target, org.objectweb.asm.tree.ClassNode cls, String mixin, IMixinInfo info) {
    }
}