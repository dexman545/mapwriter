package dex.mapwriter3.region;

import net.minecraft.util.Identifier;

public interface IChunk {
    public Identifier getBlockAndMetadata(int x, int y, int z);

    public int getBiome(int x, int z);

    public int getLightValue(int x, int y, int z);

    public int getMaxY();
}
