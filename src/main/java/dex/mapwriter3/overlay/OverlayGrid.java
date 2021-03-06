package dex.mapwriter3.overlay;

import dex.mapwriter3.api.IMwChunkOverlay;
import dex.mapwriter3.api.IMwDataProvider;
import dex.mapwriter3.map.MapView;
import dex.mapwriter3.map.mapmode.MapMode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

import java.awt.*;
import java.util.ArrayList;

public class OverlayGrid implements IMwDataProvider {

    public class ChunkOverlay implements IMwChunkOverlay {

        Point coord;

        public ChunkOverlay(int x, int z) {
            this.coord = new Point(x, z);
        }

        @Override
        public Point getCoordinates() {
            return this.coord;
        }

        @Override
        public int getColor() {
            return 0x00ffffff;
        }

        @Override
        public float getFilling() {
            return 1.0f;
        }

        @Override
        public boolean hasBorder() {
            return true;
        }

        @Override
        public float getBorderWidth() {
            return 0.5f;
        }

        @Override
        public int getBorderColor() {
            return 0xff000000;
        }

    }

    @Override
    public ArrayList<IMwChunkOverlay> getChunksOverlay(DimensionType dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ) {
        int minChunkX = (MathHelper.ceil(minX) >> 4) - 1;
        int minChunkZ = (MathHelper.ceil(minZ) >> 4) - 1;
        int maxChunkX = (MathHelper.ceil(maxX) >> 4) + 1;
        int maxChunkZ = (MathHelper.ceil(maxZ) >> 4) + 1;
        int cX = (MathHelper.ceil(centerX) >> 4) + 1;
        int cZ = (MathHelper.ceil(centerZ) >> 4) + 1;

        int limitMinX = Math.max(minChunkX, cX - 100);
        int limitMaxX = Math.min(maxChunkX, cX + 100);
        int limitMinZ = Math.max(minChunkZ, cZ - 100);
        int limitMaxZ = Math.min(maxChunkZ, cZ + 100);

        ArrayList<IMwChunkOverlay> chunks = new ArrayList<IMwChunkOverlay>();
        for (int x = limitMinX; x <= limitMaxX; x++) {
            for (int z = limitMinZ; z <= limitMaxZ; z++) {
                chunks.add(new ChunkOverlay(x, z));
            }
        }

        return chunks;
    }

    @Override
    public String getStatusString(DimensionType dim, int bX, int bY, int bZ) {
        return null;
    }

    @Override
    public void onMiddleClick(DimensionType dim, int bX, int bZ, MapView mapview) {

    }

    @Override
    public void onDimensionChanged(DimensionType dimension, MapView mapview) {
    }

    @Override
    public void onMapCenterChanged(double vX, double vZ, MapView mapview) {

    }

    @Override
    public void onZoomChanged(int level, MapView mapview) {

    }

    @Override
    public void onOverlayActivated(MapView mapview) {

    }

    @Override
    public void onOverlayDeactivated(MapView mapview) {

    }

    @Override
    public void onDraw(MapView mapview, MapMode mapmode) {

    }

    @Override
    public boolean onMouseInput(MapView mapview, MapMode mapmode) {

        return false;
    }

}
