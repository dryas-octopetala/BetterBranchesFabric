package wereweiller.betterbranches;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wereweiller.betterbranches.block.AllBlocks;

public class BetterBranches implements ModInitializer {
	public static final String MOD_ID = "betterbranches";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		AllBlocks.initialize();

		LOGGER.info("Hello Fabric world!");
	}
}