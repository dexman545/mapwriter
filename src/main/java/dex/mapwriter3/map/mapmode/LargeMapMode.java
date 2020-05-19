package dex.mapwriter3.map.mapmode;

import dex.mapwriter3.config.ConfigurationHandler;
import dex.mapwriter3.config.MapModeConfig;

public class LargeMapMode extends MapMode {
    public LargeMapMode() {
        super();
        ConfigurationHandler.mapModeConfig.setProperty("Position", MapModeConfig.minimapPos.LARGE.pos);
    }
}
