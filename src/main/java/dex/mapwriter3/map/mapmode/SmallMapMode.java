package dex.mapwriter3.map.mapmode;

import dex.mapwriter3.config.ConfigurationHandler;
import dex.mapwriter3.config.MWConfig;
import dex.mapwriter3.config.MapModeConfig;

public class SmallMapMode extends MapMode {
    public SmallMapMode() {
        super();
        ConfigurationHandler.mapModeConfig.setProperty("Position", MapModeConfig.minimapPos.TOP_RIGHT.pos);
    }
}
