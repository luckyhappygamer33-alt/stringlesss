package net.cat_metalhead.stringless;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class ModConfig {
        public static final ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
                        .id(Identifier.of("stringless",
                                        "config"))
                        .serializer(config -> GsonConfigSerializerBuilder.create(config)
                                        .setPath(FabricLoader.getInstance().getConfigDir()
                                                        .resolve("stringless.json"))
                                        .build())
                        .build();

        public enum RenderMode {
                ALWAYS_VISIBLE,
                ALWAYS_HIDDEN,
                HOLDING_STRING,
                LOOKING_AT
        }

        // Global
        @SerialEntry
        @AutoGen(category = "general")
        @dev.isxander.yacl3.config.v2.api.autogen.EnumCycler
        public RenderMode renderMode = RenderMode.HOLDING_STRING;
}
