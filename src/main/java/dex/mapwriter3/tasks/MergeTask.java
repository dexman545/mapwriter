package dex.mapwriter3.tasks;

import dex.mapwriter3.region.MergeToImage;
import dex.mapwriter3.region.RegionManager;
import dex.mapwriter3.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;

@Environment(EnvType.CLIENT)
public class MergeTask extends Task {

    final RegionManager regionManager;
    final File outputDir;
    final String basename;
    final int x, z, w, h;
    final DimensionType dimension;
    String msg = "";

    public MergeTask(RegionManager regionManager, int x, int z, int w, int h, DimensionType dimension, File outputDir, String basename) {
        this.regionManager = regionManager;
        this.x = x;
        this.z = z;
        this.w = w;
        this.h = h;
        this.dimension = dimension;
        this.outputDir = outputDir;
        this.basename = basename;
    }

    @Override
    public void run() {
        int count = MergeToImage.merge(this.regionManager, this.x, this.z, this.w, this.h, this.dimension, this.outputDir, this.basename);
        if (count > 0) {
            this.msg = I18n.translate("mw.task.mergetask.chatmsg.merge.done", this.outputDir);
        } else {
            this.msg = I18n.translate("mw.task.mergetask.chatmsg.merge.error", this.outputDir);
        }
    }

    @Override
    public void onComplete() {
        Utils.printBoth(this.msg);
    }

    @Override
    public boolean CheckForDuplicate() {
        return false;
    }

}
