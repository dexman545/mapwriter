package dex.mapwriter3.map.mapmode;

import dex.mapwriter3.config.MWConfig;

public class FullScreenMapMode extends MapMode {
    public FullScreenMapMode() {
        super(ConfigurationHandler.mwConfig.fullScreenMap());
    }
}
