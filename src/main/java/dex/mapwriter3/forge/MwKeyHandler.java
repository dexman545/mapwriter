package dex.mapwriter3.forge;

import dex.mapwriter3.Mw;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class MwKeyHandler {
    public static KeyBinding keyMapGui = new KeyBinding("key.mw_open_gui", GLFW.GLFW_KEY_M, "Mapwriter");
    public static KeyBinding keyNewMarker = new KeyBinding("key.mw_new_marker", GLFW.GLFW_KEY_INSERT, "Mapwriter");
    public static KeyBinding keyMapMode = new KeyBinding("key.mw_next_map_mode", GLFW.GLFW_KEY_N, "Mapwriter");
    public static KeyBinding keyNextGroup = new KeyBinding("key.mw_next_marker_group", GLFW.GLFW_KEY_COMMA, "Mapwriter");
    public static KeyBinding keyTeleport = new KeyBinding("key.mw_teleport", GLFW.GLFW_KEY_PERIOD, "Mapwriter");
    public static KeyBinding keyZoomIn = new KeyBinding("key.mw_zoom_in", GLFW.GLFW_KEY_PAGE_UP, "Mapwriter");
    public static KeyBinding keyZoomOut = new KeyBinding("key.mw_zoom_out", GLFW.GLFW_KEY_PAGE_DOWN, "Mapwriter");
    public static KeyBinding keyUndergroundMode = new KeyBinding("key.mw_underground_mode", GLFW.GLFW_KEY_U, "Mapwriter");

    public final KeyBinding[] keys =
            {
                    keyMapGui,
                    keyNewMarker,
                    keyMapMode,
                    keyNextGroup,
                    keyTeleport,
                    keyZoomIn,
                    keyZoomOut,
                    keyUndergroundMode
            };

    public MwKeyHandler() {
        ArrayList<String> listKeyDescs = new ArrayList<String>();
        // Register bindings
        for (KeyBinding key : this.keys) {
            if (key != null) {
                ClientRegistry.registerKeyBinding(key);
            }
            listKeyDescs.add(key.getKeyDescription());
        }
    }

    @SubscribeEvent
    public void keyEvent(InputEvent.KeyInputEvent event) {
        if (!Loader.isModLoaded("notenoughkeys")) {
            this.checkKeys();
        }
    }

    private void checkKeys() {
        for (KeyBinding key : this.keys) {
            if ((key != null) && key.isPressed()) {
                Mw.getInstance().onKeyDown(key);
            }
        }
    }
}
