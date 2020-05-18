package dex.mapwriter3.gui;

import dex.mapwriter3.Mw;
import dex.mapwriter3.config.MWConfig;
import dex.mapwriter3.map.MapView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class MwGuiTeleportDialog extends MwGuiTextDialog {

    final Mw mw;
    final MapView mapView;
    final int teleportX, teleportZ;

    public MwGuiTeleportDialog(Screen parentScreen, Mw mw, MapView mapView, int x, int y, int z) {
        super(parentScreen, I18n.translate("mw.gui.mwguimarkerdialognew.title") + ":", Integer.toString(y), I18n.translate("mw.gui.mwguimarkerdialognew.error"));
        this.mw = mw;
        this.mapView = mapView;
        this.teleportX = x;
        this.teleportZ = z;
        this.backToGameOnSubmit = true;
    }

    @Override
    public boolean submit() {
        boolean done = false;
        int height = this.getInputAsInt();
        if (this.inputValid) {
            height = Math.min(Math.max(0, height), 255);
            MWConfig.defaultTeleportHeight = height;
            this.mw.teleportToMapPos(this.mapView, this.teleportX, height, this.teleportZ);
            done = true;
        }
        return done;
    }
}
