package dex.mapwriter3.config;

import dex.mapwriter3.Mw;
import dex.mapwriter3.util.MwReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.dimension.DimensionType;
import org.aeonbits.owner.ConfigFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WorldConfig {
    private static WorldConfig instance = null;

    public WorldConfigStorage worldConfiguration;
    private String worldFile;

    // list of available dimensions
    public List<DimensionType> dimensionList = new ArrayList<DimensionType>();

    private WorldConfig() {
        // load world specific config file
        File worldConfigFile = new File(Mw.getInstance().worldDir, MwReference.worldDirConfigName);
        worldFile = worldConfigFile.getAbsolutePath();

        ConfigFactory.setProperty("worldDir", worldConfigFile.getAbsolutePath());


        this.worldConfiguration = ConfigFactory.create(WorldConfigStorage.class);

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
        try {
            this.worldConfiguration.store(new FileOutputStream(worldFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Dimension List
    public void InitDimensionList() {
        this.dimensionList.clear();
        this.worldConfiguration.setProperty("dimensionList", null);
        this.worldConfiguration.getProperty("dimensionList", null);
        //this.worldConfiguration.get("dimensionList", Utils.integerListToIntArray(this.dimensionList));
        this.addDimension(DimensionType.OVERWORLD);
        this.cleanDimensionList();
    }

    public void addDimension(DimensionType dimension) {
        this.dimensionList.add(dimension);
    }

    public void cleanDimensionList() { //wtf does this do
        List<DimensionType> dimensionListCopy = new ArrayList<>(this.dimensionList);
        this.dimensionList.clear();
        for (DimensionType dimension : dimensionListCopy) {
            this.addDimension(dimension);
        }
    }

}
