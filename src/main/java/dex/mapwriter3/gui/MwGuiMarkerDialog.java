package dex.mapwriter3.gui;

import dex.mapwriter3.map.Marker;
import dex.mapwriter3.map.MarkerManager;
import dex.mapwriter3.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class MwGuiMarkerDialog extends MwGuiTextDialog {
    private final MarkerManager markerManager;
    private Marker editingMarker;
    private String markerName = "";
    private String markerGroup = "";
    private int markerX = 0;
    private int markerY = 80;
    private int markerZ = 0;
    private int state = 0;
    private DimensionType dimension = DimensionType.OVERWORLD;

    public MwGuiMarkerDialog(Screen parentScreen, MarkerManager markerManager, String markerName, String markerGroup, int x, int y, int z, DimensionType dimension) {
        super(parentScreen, I18n.translate("mw.gui.mwguimarkerdialog.title.new") + ":", markerName, I18n.translate("mw.gui.mwguimarkerdialog.error"));
        this.markerManager = markerManager;
        this.markerName = markerName;
        this.markerGroup = markerGroup;
        this.markerX = x;
        this.markerY = y;
        this.markerZ = z;
        this.editingMarker = null;
        this.dimension = dimension;
    }

    public MwGuiMarkerDialog(Screen parentScreen, MarkerManager markerManager, Marker editingMarker) {
        super(parentScreen, I18n.translate("mw.gui.mwguimarkerdialog.title.edit") + ":", editingMarker.name, I18n.translate("mw.gui.mwguimarkerdialog.error"));
        this.markerManager = markerManager;
        this.editingMarker = editingMarker;
        this.markerName = editingMarker.name;
        this.markerGroup = editingMarker.groupName;
        this.markerX = editingMarker.x;
        this.markerY = editingMarker.y;
        this.markerZ = editingMarker.z;
        this.dimension = editingMarker.dimension;
    }

    @Override
    public boolean submit() {
        boolean done = false;
        switch (this.state) {
            case 0:
                this.markerName = this.getInputAsString();
                if (this.inputValid) {
                    this.title = I18n.translate("mw.gui.mwguimarkerdialog.title.group") + ":";
                    this.setText(this.markerGroup);
                    this.error = I18n.translate("mw.gui.mwguimarkerdialog.error.group");
                    this.state++;
                }
                break;
            case 1:
                this.markerGroup = this.getInputAsString();
                if (this.inputValid) {
                    this.title = I18n.translate("mw.gui.mwguimarkerdialog.title.x") + ":";
                    this.setText("" + this.markerX);
                    this.error = I18n.translate("mw.gui.mwguimarkerdialog.error.x");
                    this.state++;
                }
                break;
            case 2:
                this.markerX = this.getInputAsInt();
                if (this.inputValid) {
                    this.title = I18n.translate("mw.gui.mwguimarkerdialog.title.y") + ":";
                    this.setText("" + this.markerY);
                    this.error = I18n.translate("mw.gui.mwguimarkerdialog.error.y");
                    this.state++;
                }
                break;
            case 3:
                this.markerY = this.getInputAsInt();
                if (this.inputValid) {
                    this.title = I18n.translate("mw.gui.mwguimarkerdialog.title.z") + ":";
                    this.setText("" + this.markerZ);
                    this.error = I18n.translate("mw.gui.mwguimarkerdialog.error.z");
                    this.state++;
                }
                break;
            case 4:
                this.markerZ = this.getInputAsInt();
                if (this.inputValid) {
                    done = true;
                    int colour = Utils.getCurrentColour();
                    if (this.editingMarker != null) {
                        colour = this.editingMarker.colour;
                        this.markerManager.delMarker(this.editingMarker);
                        this.editingMarker = null;
                    }
                    this.markerManager.addMarker(this.markerName, this.markerGroup, this.markerX, this.markerY, this.markerZ, this.dimension, colour);
                    this.markerManager.setVisibleGroupName(this.markerGroup);
                    this.markerManager.update();
                }
                break;
        }
        return done;
    }
}
