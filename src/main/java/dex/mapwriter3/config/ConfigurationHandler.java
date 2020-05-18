package dex.mapwriter3.config;

import dex.mapwriter3.util.MwReference;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.aeonbits.owner.ConfigFactory;

import java.io.File;

public class ConfigurationHandler {
    // configuration files (global and world specific)
    public static Configuration configuration;

    String config = FabricLoader.getInstance().getConfigDirectory().toString() + "/mapwriter.cfg";
    ConfigFactory.setProperty("configDir", config);

    public static void init(File configFile) {
        // Create the configuration object from the given configuration file
        if (configuration == null) {
            configuration = new Configuration(configFile);
            setMapModeDefaults();
            loadConfig();

            configuration.get(MwReference.catOptions, "overlayModeIndex", MWConfig.overlayModeIndexDef).setShowInGui(false);
            configuration.get(MwReference.catOptions, "overlayZoomLevel", MWConfig.zoomInLevelsDef).setShowInGui(false);


        }
    }

    public static void loadConfig() {
        MWConfig.linearTextureScaling = configuration.getBoolean("linearTextureScaling", MwReference.catOptions, MWConfig.linearTextureScalingDef, "", "mw.config.linearTextureScaling");
        MWConfig.useSavedBlockColours = configuration.getBoolean("useSavedBlockColours", MwReference.catOptions, MWConfig.useSavedBlockColoursDef, "", "mw.config.useSavedBlockColours");
        MWConfig.teleportEnabled = configuration.getBoolean("teleportEnabled", MwReference.catOptions, MWConfig.teleportEnabledDef, "", "mw.config.teleportEnabled");
        MWConfig.teleportCommand = configuration.getString("teleportCommand", MwReference.catOptions, MWConfig.teleportCommandDef, "", "mw.config.teleportCommand");
        MWConfig.maxChunkSaveDistSq = configuration.getInt("maxChunkSaveDistSq", MwReference.catOptions, MWConfig.maxChunkSaveDistSqDef, 1, 256 * 256, "", "mw.config.maxChunkSaveDistSq");
        MWConfig.mapPixelSnapEnabled = configuration.getBoolean("mapPixelSnapEnabled", MwReference.catOptions, MWConfig.mapPixelSnapEnabledDef, "", "mw.config.mapPixelSnapEnabled");
        MWConfig.maxDeathMarkers = configuration.getInt("maxDeathMarkers", MwReference.catOptions, MWConfig.maxDeathMarkersDef, 0, 1000, "", "mw.config.maxDeathMarkers");
        MWConfig.chunksPerTick = configuration.getInt("chunksPerTick", MwReference.catOptions, MWConfig.chunksPerTickDef, 1, 500, "", "mw.config.chunksPerTick");
        MWConfig.saveDirOverride = configuration.getString("saveDirOverride", MwReference.catOptions, MWConfig.saveDirOverrideDef, "", "mw.config.saveDirOverride");
        MWConfig.portNumberInWorldNameEnabled = configuration.getBoolean("portNumberInWorldNameEnabled", MwReference.catOptions, MWConfig.portNumberInWorldNameEnabledDef, "", "mw.config.portNumberInWorldNameEnabled");
        MWConfig.undergroundMode = configuration.getBoolean("undergroundMode", MwReference.catOptions, MWConfig.undergroundModeDef, "", "mw.config.undergroundMode");
        MWConfig.regionFileOutputEnabledSP = configuration.getBoolean("regionFileOutputEnabledSP", MwReference.catOptions, MWConfig.regionFileOutputEnabledSPDef, "", "mw.config.regionFileOutputEnabledSP");
        MWConfig.regionFileOutputEnabledMP = configuration.getBoolean("regionFileOutputEnabledMP", MwReference.catOptions, MWConfig.regionFileOutputEnabledMPDef, "", "mw.config.regionFileOutputEnabledMP");
        MWConfig.backgroundTextureMode = configuration.getString("backgroundTextureMode", MwReference.catOptions, MWConfig.backgroundTextureModeDef, "", MWConfig.backgroundModeStringArray, "mw.config.backgroundTextureMode");
        MWConfig.zoomOutLevels = configuration.getInt("zoomOutLevels", MwReference.catOptions, MWConfig.zoomOutLevelsDef, 1, 256, "", "mw.config.zoomOutLevels");
        MWConfig.zoomInLevels = -configuration.getInt("zoomInLevels", MwReference.catOptions, -MWConfig.zoomInLevelsDef, 1, 256, "", "mw.config.zoomInLevels");

        MWConfig.configTextureSize = configuration.getInt("textureSize", MwReference.catOptions, MWConfig.configTextureSizeDef, 1024, 4096, "", "mw.config.textureSize");

        MWConfig.overlayModeIndex = configuration.getInt("overlayModeIndex", MwReference.catOptions, MWConfig.overlayModeIndexDef, 0, 1000, "", "mw.config.overlayModeIndex");
        MWConfig.overlayZoomLevel = configuration.getInt("overlayZoomLevel", MwReference.catOptions, MWConfig.overlayZoomLevelDef, MWConfig.zoomInLevels, MWConfig.zoomOutLevels, "", "mw.config.overlayZoomLevel");

        MWConfig.moreRealisticMap = configuration.getBoolean("moreRealisticMap", MwReference.catOptions, MWConfig.moreRealisticMapDef, "", "mw.config.moreRealisticMap");

        MWConfig.newMarkerDialog = configuration.getBoolean("newMarkerDialog", MwReference.catOptions, MWConfig.newMarkerDialogDef, "", "mw.config.newMarkerDialog");
        MWConfig.drawMarkersInWorld = configuration.getBoolean("drawMarkersInWorld", MwReference.catOptions, MWConfig.drawMarkersInWorldDef, "", "mw.config.drawMarkersInWorld");
        MWConfig.drawMarkersNameInWorld = configuration.getBoolean("drawMarkersNameInWorld", MwReference.catOptions, MWConfig.drawMarkersNameInWorldDef, "", "mw.config.drawMarkersNameInWorld");
        MWConfig.drawMarkersDistanceInWorld = configuration.getBoolean("drawMarkersDistanceInWorld", MwReference.catOptions, MWConfig.drawMarkersDistanceInWorldDef, "", "mw.config.drawMarkersDistanceInWorld");

        MWConfig.fullScreenMap.loadConfig();
        MWConfig.largeMap.loadConfig();
        MWConfig.smallMap.loadConfig();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equalsIgnoreCase(MwReference.MOD_ID)) {
            loadConfig();
        }
    }

    public static void setMapModeDefaults() {
        MWConfig.fullScreenMap.setDefaults();
        MWConfig.largeMap.setDefaults();
        MWConfig.smallMap.setDefaults();
    }
}