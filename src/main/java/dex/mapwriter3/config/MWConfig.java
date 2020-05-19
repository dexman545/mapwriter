package dex.mapwriter3.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

@org.aeonbits.owner.Config.HotReload(type = org.aeonbits.owner.Config.HotReloadType.ASYNC) //set value = X for interval of X seconds. Default: 5
@org.aeonbits.owner.Config.Sources({"file:${configDir}"})
public interface MWConfig extends Config, Reloadable, Accessible, Mutable {

    public static enum backgroundMode {
        NONE ("mw.config.backgroundTextureMode.none"),
        STATIC ("mw.config.backgroundTextureMode.static"),
        PANNING ("mw.config.backgroundTextureMode.panning");

        public final String modeString;

        backgroundMode(String modeString) {
            this.modeString = modeString;
        }
    }

    // configuration options
    @DefaultValue("true")
    boolean linearTextureScaling();
    @DefaultValue("false")
    boolean undergroundMode();
    @DefaultValue("true")
    boolean teleportEnabled();
    @DefaultValue("tp")
    String teleportCommand();
    @DefaultValue("80")
    int defaultTeleportHeight();
    @DefaultValue("5")
    int zoomOutLevels();
    @DefaultValue("-5")
    int zoomInLevels();
    @DefaultValue("false")
    boolean useSavedBlockColours();
    @DefaultValue("16384") //128 * 128
    int maxChunkSaveDistSq();
    @DefaultValue("true")
    boolean mapPixelSnapEnabled();
    @DefaultValue("2048")
    int configTextureSize();
    @DefaultValue("3")
    int maxDeathMarkers();
    @DefaultValue("5")
    int chunksPerTick();
    @DefaultValue("true")
    boolean portNumberInWorldNameEnabled();
    @DefaultValue("")
    String saveDirOverride();
    @DefaultValue("true")
    boolean regionFileOutputEnabledSP();
    @DefaultValue("true")
    boolean regionFileOutputEnabledMP();
    @DefaultValue("NONE")
    backgroundMode backgroundTextureMode();
    @DefaultValue("false")
    boolean moreRealisticMap();
    @DefaultValue("true")
    boolean newMarkerDialog();
    @DefaultValue("false")
    boolean drawMarkersInWorld();
    @DefaultValue("false")
    boolean drawMarkersNameInWorld();
    @DefaultValue("false")
    boolean drawMarkersDistanceInWorld();

    // World configuration Options
    @DefaultValue("0")
    int overlayModeIndex();
    @DefaultValue("0")
    int overlayZoomLevel();
    @DefaultValue("0")
    int fullScreenZoomLevel();

    /*largeMapModeConfig largeMap = new largeMapModeConfig(MwReference.catLargeMapConfig);
    smallMapModeConfig smallMap = new smallMapModeConfig(MwReference.catSmallMapConfig);
    MapModeConfig fullScreenMap = new MapModeConfig(MwReference.catFullMapConfig);*/

}
