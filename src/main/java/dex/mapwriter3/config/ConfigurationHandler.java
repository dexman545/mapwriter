package dex.mapwriter3.config;

import net.fabricmc.loader.api.FabricLoader;
import org.aeonbits.owner.ConfigFactory;

import java.io.File;

public class ConfigurationHandler {

    public static MapModeConfig mapModeConfig;
    public static MWConfig mwConfig;

    public static void init(File configFile) {
        loadConfig();
    }

    public static void loadConfig() {
        String config = FabricLoader.getInstance().getConfigDirectory().toString() + "/mapwriter.cfg";
        ConfigFactory.setProperty("configDir", config);
        String configMapMode = FabricLoader.getInstance().getConfigDirectory().toString() + "/mapwriter/mapmode.cfg";
        ConfigFactory.setProperty("mapModeConfigDir", configMapMode);

        mwConfig = ConfigFactory.create(MWConfig.class);
        mapModeConfig = ConfigFactory.create(MapModeConfig.class);

        mwConfig.addReloadListener(event -> {

        });

        mapModeConfig.addReloadListener(event -> {

        });
    }
}