package dex.mapwriter3.tasks;

import dex.mapwriter3.Mw;
import dex.mapwriter3.region.BlockColours;
import dex.mapwriter3.region.RegionManager;
import dex.mapwriter3.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class RebuildRegionsTask extends Task {

    final RegionManager regionManager;
    final BlockColours blockColours;
    final int x, z, w, h;
    final DimensionType dimension;
    String msg = "";

    public RebuildRegionsTask(Mw mw, int x, int z, int w, int h, DimensionType dimension) {
        this.regionManager = mw.regionManager;
        this.blockColours = mw.blockColours;
        this.x = x;
        this.z = z;
        this.w = w;
        this.h = h;
        this.dimension = dimension;
    }

    @Override
    public void run() {
        this.regionManager.blockColours = this.blockColours;
        this.regionManager.rebuildRegions(this.x, this.z, this.w, this.h, this.dimension);
    }

    @Override
    public void onComplete() {
        Utils.printBoth(I18n.translate("mw.task.rebuildregionstask.chatmsg.rebuild.compleet"));
    }

    @Override
    public boolean CheckForDuplicate() {
        return false;
    }

}
