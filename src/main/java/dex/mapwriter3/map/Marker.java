package dex.mapwriter3.map;

import dex.mapwriter3.map.mapmode.MapMode;
import dex.mapwriter3.util.Render;
import dex.mapwriter3.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class Marker {
    public final String name;
    public final String groupName;
    public int x;
    public int y;
    public int z;
    public DimensionType dimension;
    public int colour;

    public Point.Double screenPos = new Point.Double(0, 0);

    public Marker(String name, String groupName, int x, int y, int z, DimensionType dimension, int colour) {
        this.name = Utils.mungeStringForConfig(name);
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.colour = colour;
        this.groupName = Utils.mungeStringForConfig(groupName);
    }

    public String getString() {
        return String.format("%s %s (%d, %d, %d) %s %06x", this.name, this.groupName, this.x, this.y, this.z, this.dimension.toString(), this.colour & 0xffffff);
    }

    public void colourNext() {
        this.colour = Utils.getNextColour();
    }

    public void colourPrev() {
        this.colour = Utils.getPrevColour();
    }

    public void draw(MapMode mapMode, MapView mapView, int borderColour) {
        double scale = mapView.getDimensionScaling(this.dimension);
        Point.Double p = mapMode.getClampedScreenXY(mapView, this.x * scale, this.z * scale);
        this.screenPos.setLocation(p.x + mapMode.xTranslation, p.y + mapMode.yTranslation);

        // draw a coloured rectangle centered on the calculated (x, y)
        double mSize = mapMode.config.markerSize;
        double halfMSize = mapMode.config.markerSize / 2.0;
        Render.setColour(borderColour);
        Render.drawRect(p.x - halfMSize, p.y - halfMSize, mSize, mSize);
        Render.setColour(this.colour);
        Render.drawRect((p.x - halfMSize) + 0.5, (p.y - halfMSize) + 0.5, mSize - 1.0, mSize - 1.0);
    }

    // arraylist.contains was producing unexpected results in some situations
    // rather than figure out why i'll just control how two markers are compared
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Marker) {
            Marker m = (Marker) o;
            return (this.name == m.name) && (this.groupName == m.groupName) && (this.x == m.x) && (this.y == m.y) && (this.z == m.z) && (this.dimension == m.dimension);
        }
        return false;
    }

    public double getDistanceToMarker(Entity entityIn) {
        double d0 = this.x - entityIn.getX();
        double d1 = this.y - entityIn.getY();
        double d2 = this.z - entityIn.getZ();
        return MathHelper.sqrt((d0 * d0) + (d1 * d1) + (d2 * d2));
    }

    public float getRed() {
        return (((colour >> 16) & 0xff) / 255.0f);
    }

    public float getGreen() {
        return (((colour >> 8) & 0xff) / 255.0f);
    }

    public float getBlue() {
        return (((colour) & 0xff) / 255.0f);
    }
}