package net.cat_metalhead.stringless;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stringless implements ClientModInitializer {
	public static final String MOD_ID = "stringless";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();
		LOGGER.info("Mod initialized!");

		ClientTickEvents.END_CLIENT_TICK.register(StringlessRenderHelper::onClientTick);

	}
}