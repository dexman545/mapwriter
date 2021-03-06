package dex.mapwriter3.api;

import dex.mapwriter3.map.MapView;
import dex.mapwriter3.map.mapmode.MapMode;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;

public interface IMwDataProvider {
    public ArrayList<IMwChunkOverlay> getChunksOverlay(DimensionType dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ);

    // Returns what should be added to the status bar by the addon.
    public String getStatusString(DimensionType dim, int bX, int bY, int bZ);

    // Call back for middle click.
    public void onMiddleClick(DimensionType dim, int bX, int bZ, MapView mapview);

    // Callback for dimension change on the map
    public void onDimensionChanged(DimensionType dimension, MapView mapview);

    public void onMapCenterChanged(double vX, double vZ, MapView mapview);

    public void onZoomChanged(int level, MapView mapview);

    public void onOverlayActivated(MapView mapview);

    public void onOverlayDeactivated(MapView mapview);

    public void onDraw(MapView mapview, MapMode mapmode);

    public boolean onMouseInput(MapView mapview, MapMode mapmode);
}
