package dex.mapwriter3.config;

import java.io.File;

import dex.mapwriter3.util.MwReference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler
{
	// configuration files (global and world specific)
	public static Configuration configuration;

	public static void init(File configFile)
	{
		// Create the configuration object from the given configuration file
		if (configuration == null)
		{
			configuration = new Configuration(configFile);
			setMapModeDefaults();
			loadConfig();
			
			configuration.get(MwReference.catOptions, "overlayModeIndex", Config.overlayModeIndexDef).setShowInGui(false);
			configuration.get(MwReference.catOptions, "overlayZoomLevel", Config.zoomInLevelsDef).setShowInGui(false);
			
			
		}
	}

	public static void loadConfig()
	{		
		Config.linearTextureScaling = configuration.getBoolean("linearTextureScaling", MwReference.catOptions, Config.linearTextureScalingDef, "", "mw.config.linearTextureScaling");
		Config.useSavedBlockColours = configuration.getBoolean("useSavedBlockColours", MwReference.catOptions, Config.useSavedBlockColoursDef, "", "mw.config.useSavedBlockColours");
		Config.teleportEnabled = configuration.getBoolean("teleportEnabled", MwReference.catOptions, Config.teleportEnabledDef, "", "mw.config.teleportEnabled");
		Config.teleportCommand = configuration.getString("teleportCommand", MwReference.catOptions, Config.teleportCommandDef, "", "mw.config.teleportCommand");
		Config.maxChunkSaveDistSq = configuration.getInt("maxChunkSaveDistSq", MwReference.catOptions, Config.maxChunkSaveDistSqDef, 1, 256 * 256, "", "mw.config.maxChunkSaveDistSq");
		Config.mapPixelSnapEnabled = configuration.getBoolean("mapPixelSnapEnabled", MwReference.catOptions, Config.mapPixelSnapEnabledDef, "", "mw.config.mapPixelSnapEnabled");
		Config.maxDeathMarkers = configuration.getInt("maxDeathMarkers", MwReference.catOptions, Config.maxDeathMarkersDef, 0, 1000, "", "mw.config.maxDeathMarkers");
		Config.chunksPerTick = configuration.getInt("chunksPerTick", MwReference.catOptions, Config.chunksPerTickDef, 1, 500, "", "mw.config.chunksPerTick");
		Config.saveDirOverride = configuration.getString("saveDirOverride", MwReference.catOptions, Config.saveDirOverrideDef, "", "mw.config.saveDirOverride");
		Config.portNumberInWorldNameEnabled = configuration.getBoolean("portNumberInWorldNameEnabled", MwReference.catOptions, Config.portNumberInWorldNameEnabledDef, "", "mw.config.portNumberInWorldNameEnabled");
		Config.undergroundMode = configuration.getBoolean("undergroundMode", MwReference.catOptions, Config.undergroundModeDef, "", "mw.config.undergroundMode");
		Config.regionFileOutputEnabledSP = configuration.getBoolean("regionFileOutputEnabledSP", MwReference.catOptions, Config.regionFileOutputEnabledSPDef, "", "mw.config.regionFileOutputEnabledSP");
		Config.regionFileOutputEnabledMP = configuration.getBoolean("regionFileOutputEnabledMP", MwReference.catOptions, Config.regionFileOutputEnabledMPDef, "", "mw.config.regionFileOutputEnabledMP");
		Config.backgroundTextureMode = configuration.getString("backgroundTextureMode", MwReference.catOptions, Config.backgroundTextureModeDef, "", Config.backgroundModeStringArray, "mw.config.backgroundTextureMode");
		Config.zoomOutLevels = configuration.getInt("zoomOutLevels", MwReference.catOptions, Config.zoomOutLevelsDef, 1, 256, "", "mw.config.zoomOutLevels");
		Config.zoomInLevels = -configuration.getInt("zoomInLevels", MwReference.catOptions, -Config.zoomInLevelsDef, 1, 256, "", "mw.config.zoomInLevels");

		Config.configTextureSize = configuration.getInt("textureSize", MwReference.catOptions, Config.configTextureSizeDef, 1024, 4096, "", "mw.config.textureSize");
		
		Config.overlayModeIndex = configuration.getInt("overlayModeIndex", MwReference.catOptions, Config.overlayModeIndexDef, 0, 1000, "", "mw.config.overlayModeIndex");
		Config.overlayZoomLevel = configuration.getInt("overlayZoomLevel", MwReference.catOptions, Config.overlayZoomLevelDef, Config.zoomInLevels, Config.zoomOutLevels, "", "mw.config.overlayZoomLevel");

		Config.moreRealisticMap = configuration.getBoolean("moreRealisticMap", MwReference.catOptions, Config.moreRealisticMapDef, "", "mw.config.moreRealisticMap");

		Config.newMarkerDialog = configuration.getBoolean("newMarkerDialog", MwReference.catOptions, Config.newMarkerDialogDef, "", "mw.config.newMarkerDialog");
		Config.drawMarkersInWorld = configuration.getBoolean("drawMarkersInWorld", MwReference.catOptions, Config.drawMarkersInWorldDef, "", "mw.config.drawMarkersInWorld");
		Config.drawMarkersNameInWorld = configuration.getBoolean("drawMarkersNameInWorld", MwReference.catOptions, Config.drawMarkersNameInWorldDef, "", "mw.config.drawMarkersNameInWorld");
		Config.drawMarkersDistanceInWorld = configuration.getBoolean("drawMarkersDistanceInWorld", MwReference.catOptions, Config.drawMarkersDistanceInWorldDef, "", "mw.config.drawMarkersDistanceInWorld");

		Config.fullScreenMap.loadConfig();
		Config.largeMap.loadConfig();
		Config.smallMap.loadConfig();

		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase(MwReference.MOD_ID))
		{
			loadConfig();
		}
	}

	public static void setMapModeDefaults()
	{
		Config.fullScreenMap.setDefaults();
		Config.largeMap.setDefaults();
		Config.smallMap.setDefaults();
	}
}