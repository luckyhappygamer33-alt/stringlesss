package net.cat_metalhead.stringless;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stringless implements ClientModInitializer {
	public static final String MOD_ID = "stringless";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();
		LOGGER.info("Mod initialized!");

		// ONLY IF USING SODIUM
		if (FabricLoader.getInstance().isModLoaded("sodium")) {
			WorldRenderEvents.START.register(ctx -> StringlessRenderHelper.onFrame(MinecraftClient.getInstance()));
			ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
				if (FabricLoader.getInstance().isModLoaded("sodium"))
					StringlessSodiumCompat.clear();
			});
		}

	}
}