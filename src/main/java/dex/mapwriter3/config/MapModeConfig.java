package dex.mapwriter3.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

@org.aeonbits.owner.Config.HotReload(type = org.aeonbits.owner.Config.HotReloadType.ASYNC) //set value = X for interval of X seconds. Default: 5
@org.aeonbits.owner.Config.Sources({"file:${mapModeConfigDir}"})
public interface MapModeConfig extends Config, Reloadable, Accessible, Mutable {

    // Double for biome mode?
    enum coordsMode {
        DISABLED("mw.config.map.coordsMode.disabled"),
        SMALL("mw.config.map.coordsMode.small"),
        LARGE("mw.config.map.coordsMode.large");

        public final String coordinateMode;

        coordsMode(String coordinateMode) {
            this.coordinateMode = coordinateMode;
        }
    }

    String[] miniMapPositionStringArray =
            {
                    "mw.config.map.position.topRight",
                    "mw.config.map.position.topLeft",
                    "mw.config.map.position.botRight",
                    "mw.config.map.position.botLeft"
            };

    enum minimapPos {
        TOP_RIGHT("mw.config.map.position.topRight"),
        TOP_LEFT("mw.config.map.position.topLeft"),
        BOTTOM_RIGHT("mw.config.map.position.botRight"),
        BOTTOM_LEFT("mw.config.map.position.botLeft"),
        LARGE("Large"),
        FULLSCREEN("Fullscreen");

        public final String pos;


        minimapPos(String pos) {
            this.pos = pos;
        }
    }

    @DefaultValue("true")
    boolean enabled();
    @DefaultValue("false")
    boolean rotate();
    @DefaultValue("false")
    boolean circular();
    @DefaultValue("DISABLED")
    coordsMode coordsMode();
    @DefaultValue("false")
    boolean borderMode();
    @DefaultValue("5")
    int playerArrowSize();
    @DefaultValue("5")
    int markerSize();
    @DefaultValue("3")
    int trailMarkerSize();
    @DefaultValue("100")
    int alphaPercent();
    @DefaultValue("-1")
    int heightPercent();
    @DefaultValue("FULLSCREEN")
    String Position();
    @DefaultValue("DISABLED")
    coordsMode biomeMode();

}