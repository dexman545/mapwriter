package dex.mapwriter3.gui;

import dex.mapwriter3.Mw;
import dex.mapwriter3.config.WorldConfig;
import dex.mapwriter3.map.MapView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class MwGuiDimensionDialog extends MwGuiTextDialog {

    final Mw mw;
    final MapView mapView;
    final DimensionType dimension;

    public MwGuiDimensionDialog(Screen parentScreen, Mw mw, MapView mapView, DimensionType dimension) {
        super(parentScreen, I18n.translate("mw.gui.mwguidimensiondialog.title") + ":", dimension.toString(), I18n.translate("mw.gui.mwguidimensiondialog.error"));
        this.mw = mw;
        this.mapView = mapView;
        this.dimension = dimension;
    }

    @Override
    public boolean submit() {
        boolean done = false;
        DimensionType dimension = DimensionType.byId(Identifier.tryParse(this.getInputAsString()));
        if (this.inputValid) {
            this.mapView.setDimensionAndAdjustZoom(dimension);
            this.mw.miniMap.view.setDimension(dimension);
            WorldConfig.getInstance().addDimension(dimension);
            done = true;
        }
        return done;
    }
}
