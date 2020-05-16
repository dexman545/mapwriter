package dex.mapwriter3.config;

import dex.mapwriter3.gui.ModGuiConfig.ModBooleanEntry;
import dex.mapwriter3.util.MwReference;

public class smallMapModeConfig extends largeMapModeConfig
{
	public smallMapModeConfig(String configCategory)
	{
		super(configCategory);
	}

	@Override
	public void loadConfig()
	{
		super.loadConfig();
		this.heightPercent = ConfigurationHandler.configuration.getInt("heightPercent", this.configCategory, this.heightPercentDef, 0, 100, "", "mw.config.map.heightPercent");
		this.Position = ConfigurationHandler.configuration.getString("Position", this.configCategory, this.PositionDef, "", miniMapPositionStringArray, "mw.config.map.position");
	}

	@Override
	public void setDefaults()
	{
		this.rotateDef = true;
		this.circularDef = true;
		this.coordsModeDef = coordsModeStringArray[1];
		this.biomeModeDef = coordsModeStringArray[0];
		this.borderModeDef = true;
		this.playerArrowSizeDef = 4;
		this.markerSizeDef = 3;
		this.heightPercentDef = 30;
		this.PositionDef = MapModeConfig.miniMapPositionStringArray[0];

		ConfigurationHandler.configuration.get(MwReference.catSmallMapConfig, "enabled", this.enabledDef).setRequiresWorldRestart(true);
		ConfigurationHandler.configuration.get(this.configCategory, "rotate", this.rotate).setConfigEntryClass(ModBooleanEntry.class);
	}
}
