package net.cat_metalhead.stringless;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stringless implements ClientModInitializer {
	public static final String MOD_ID = "stringless";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();
		LOGGER.info("Mod initialized!");

		// Extraction phase — reads world state, decides what to render
		// Stringless.java — only one registration needed
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(StringlessOverlayRenderer::draw);
		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> TripwireTracker.clear());
	}
}