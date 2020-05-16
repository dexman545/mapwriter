package dex.mapwriter3;

import dex.mapwriter3.events.PlayerDeathCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapwriterFabric implements ClientModInitializer {

    public static final String MOD_ID = "mapwriter";
    public static final String MOD_NAME = "Mapwriter 3";
    public static Logger logger = LogManager.getLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        logger.info("Loaded!");

        //ClientTickCallback.EVENT.register();

    }
}