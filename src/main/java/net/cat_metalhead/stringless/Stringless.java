package net.cat_metalhead.stringless;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stringless implements ModInitializer {
	public static final String MOD_ID = "stringless";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.HANDLER.load();
		LOGGER.info("Mod initialized!");
	}
}