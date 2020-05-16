package dex.mapwriter3.config;

import dex.mapwriter3.Mw;
import dex.mapwriter3.util.MwReference;
import dex.mapwriter3.util.Utils;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldConfig {
    private static WorldConfig instance = null;

    public Configuration worldConfiguration = null;

    // list of available dimensions
    public List<Integer> dimensionList = new ArrayList<Integer>();

    private WorldConfig() {
        // load world specific config file
        File worldConfigFile = new File(Mw.getInstance().worldDir, MwReference.worldDirConfigName);
        this.worldConfiguration = new Configuration(worldConfigFile);

        this.InitDimensionList();
    }

    public static WorldConfig getInstance() {
        if (instance == null) {
            synchronized (WorldConfig.class) {
                if (instance == null) {
                    instance = new WorldConfig();
                }
            }
        }

        return instance;
    }

    public void saveWorldConfig() {
        this.worldConfiguration.save();
    }

    // Dimension List
    public void InitDimensionList() {
        this.dimensionList.clear();
        this.worldConfiguration.get(MwReference.catWorld, "dimensionList", Utils.integerListToIntArray(this.dimensionList));
        this.addDimension(0);
        this.cleanDimensionList();
    }

    public void addDimension(DimensionType dimension) {
        int i = this.dimensionList.indexOf(dimension);
        if (i < 0) {
            this.dimensionList.add(dimension);
        }
    }

    public void cleanDimensionList() {
        List<Integer> dimensionListCopy = new ArrayList<DimensionType>(this.dimensionList);
        this.dimensionList.clear();
        for (DimensionType dimension : dimensionListCopy) {
            this.addDimension(dimension);
        }
    }

}
