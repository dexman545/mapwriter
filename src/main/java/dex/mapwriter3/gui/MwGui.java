package dex.mapwriter3.gui;

import dex.mapwriter3.Mw;
import dex.mapwriter3.api.IMwDataProvider;
import dex.mapwriter3.api.MwAPI;
import dex.mapwriter3.config.MWConfig;
import dex.mapwriter3.config.WorldConfig;
import dex.mapwriter3.forge.MwKeyHandler;
import dex.mapwriter3.map.MapRenderer;
import dex.mapwriter3.map.MapView;
import dex.mapwriter3.map.Marker;
import dex.mapwriter3.map.mapmode.FullScreenMapMode;
import dex.mapwriter3.map.mapmode.MapMode;
import dex.mapwriter3.tasks.MergeTask;
import dex.mapwriter3.tasks.RebuildRegionsTask;
import dex.mapwriter3.util.Logging;
import dex.mapwriter3.util.MwReference;
import dex.mapwriter3.util.Utils;
import dex.mapwriter3.util.VersionCheck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Environment(EnvType.CLIENT)
public class MwGui extends Screen {
    private Mw mw;
    public MapMode mapMode;
    private MapView mapView;
    private MapRenderer map;

    private String[] HelpText1 = new String[]
            {
                    "mw.gui.mwgui.keys",
                    "",
                    "  Space",
                    "  Delete",
                    "  C",
                    "  Home",
                    "  End",
                    "  N",
                    "  T",
                    "  P",
                    "  R",
                    "  U",
                    "  L",
                    "",
                    "mw.gui.mwgui.helptext.1",
                    "mw.gui.mwgui.helptext.2",
                    "mw.gui.mwgui.helptext.3",
                    "mw.gui.mwgui.helptext.4",
                    "mw.gui.mwgui.helptext.5",
                    "mw.gui.mwgui.helptext.6",
                    "",
                    "mw.gui.mwgui.helptext.7",
                    "mw.gui.mwgui.helptext.8",
                    "mw.gui.mwgui.helptext.9"
            };
    private String[] HelpText2 = new String[]
            {
                    "",
                    "",
                    "mw.gui.mwgui.helptext.nextmarkergroup",
                    "mw.gui.mwgui.helptext.deletemarker",
                    "mw.gui.mwgui.helptext.cyclecolour",
                    "mw.gui.mwgui.helptext.centermap",
                    "mw.gui.mwgui.helptext.centermapplayer",
                    "mw.gui.mwgui.helptext.selectnextmarker",
                    "mw.gui.mwgui.helptext.teleport",
                    "mw.gui.mwgui.helptext.savepng",
                    "mw.gui.mwgui.helptext.regenerate",
                    "mw.gui.mwgui.helptext.undergroundmap",
                    "mw.gui.mwgui.helptext.markerlist"
            };

    private final static double PAN_FACTOR = 0.3D;

    private static final int menuY = 5;
    private static final int menuX = 5;

    private int mouseLeftHeld = 0;
    private int mouseLeftDragStartX = 0;
    private int mouseLeftDragStartY = 0;
    private double viewXStart;
    private double viewZStart;
    private Marker movingMarker = null;
    private int movingMarkerXStart = 0;
    private int movingMarkerZStart = 0;
    private int mouseBlockX = 0;
    private int mouseBlockY = 0;
    private int mouseBlockZ = 0;

    private MwGuiLabel helpLabel;
    private MwGuiLabel optionsLabel;
    private MwGuiLabel dimensionLabel;
    private MwGuiLabel groupLabel;
    private MwGuiLabel overlayLabel;
    private MwGuiLabel updateLabel;
    private MwGuiMarkerListOverlay MarkerOverlay;

    private MwGuiLabel helpTooltipLabel;
    private MwGuiLabel updateTooltipLabel;
    private MwGuiLabel statusLabel;
    private MwGuiLabel markerLabel;

    public static MwGui instance;

    private URI clickedLinkURI;

    public MwGui(Mw mw) {
        this.mw = mw;
        this.mapMode = new FullScreenMapMode();
        this.mapView = new MapView(this.mw, true);
        this.map = new MapRenderer(this.mw, this.mapMode, this.mapView);

        this.mapView.setDimension(this.mw.miniMap.view.getDimension());
        this.mapView.setViewCentreScaled(this.mw.playerX, this.mw.playerZ, this.mw.playerDimension);
        this.mapView.setZoomLevel(ConfigurationHandler.mwConfig.fullScreenZoomLevel());

        this.initLabels();

        this.MarkerOverlay = new MwGuiMarkerListOverlay(this, this.mw.markerManager);

        instance = this;
    }

    public MwGui(Mw mw, DimensionType dim, int x, int z) {
        this(mw);
        this.mapView.setDimension(dim);
        this.mapView.setViewCentreScaled(x, z, dim);
        this.mapView.setZoomLevel(ConfigurationHandler.mwConfig.fullScreenZoomLevel());
    }

    // called when gui is displayed and every time the screen
    // is resized
    @Override
    public void init() {
        this.helpLabel.setParentWidthAndHeight(this.width, this.height);
        this.optionsLabel.setParentWidthAndHeight(this.width, this.height);
        this.dimensionLabel.setParentWidthAndHeight(this.width, this.height);
        this.groupLabel.setParentWidthAndHeight(this.width, this.height);
        this.overlayLabel.setParentWidthAndHeight(this.width, this.height);
        this.updateLabel.setParentWidthAndHeight(this.width, this.height);

        this.helpTooltipLabel.setParentWidthAndHeight(this.width, this.height);
        this.updateTooltipLabel.setParentWidthAndHeight(this.width, this.height);
        this.statusLabel.setParentWidthAndHeight(this.width, this.height);
        this.markerLabel.setParentWidthAndHeight(this.width, this.height);

        this.MarkerOverlay.setDimensions(MwGuiMarkerListOverlay.listWidth, this.height - 20, MwGuiMarkerListOverlay.ListY, (10 + this.height) - 20, this.width - 110);
    }

    public void initLabels() {
        this.helpLabel = new MwGuiLabel(new String[]
                {
                        "[" + I18n.translate("mw.gui.mwgui.help") + "]"
                }, null, menuX, menuY, true, false, this.width, this.height);
        this.optionsLabel = new MwGuiLabel(new String[]
                {
                        "[" + I18n.translate("mw.gui.mwgui.options") + "]"
                }, null, 0, 0, true, false, this.width, this.height);
        this.dimensionLabel = new MwGuiLabel(null, null, 0, 0, true, false, this.width, this.height);
        this.groupLabel = new MwGuiLabel(null, null, 0, 0, true, false, this.width, this.height);
        this.overlayLabel = new MwGuiLabel(null, null, 0, 0, true, false, this.width, this.height);
        String updateString = "[" + I18n.translate("mw.gui.mwgui.newversion", VersionCheck.getLatestVersion()) + "]";
        this.updateLabel = new MwGuiLabel(new String[]
                {
                        updateString
                }, null, 0, 0, true, false, this.width, this.height);
        this.helpTooltipLabel = new MwGuiLabel(this.HelpText1, this.HelpText2, 0, 0, true, false, this.width, this.height);

        this.updateTooltipLabel = new MwGuiLabel(new String[]
                {
                        VersionCheck.getUpdateURL()
                }, null, 0, 0, true, false, this.width, this.height);

        this.statusLabel = new MwGuiLabel(null, null, 0, 0, true, false, this.width, this.height);
        this.markerLabel = new MwGuiLabel(null, null, 0, 0, true, true, this.width, this.height);

        this.optionsLabel.drawToRightOf(this.helpLabel);
        this.dimensionLabel.drawToRightOf(this.optionsLabel);
        this.groupLabel.drawToRightOf(this.dimensionLabel);
        this.overlayLabel.drawToRightOf(this.groupLabel);
        this.updateLabel.drawToRightOf(this.overlayLabel);

        this.helpTooltipLabel.drawToBelowOf(this.helpLabel);
        this.updateTooltipLabel.drawToBelowOf(this.helpLabel);
    }

    // called when a button is pressed
	/*@Override
	protected void actionPerformed(ButtonWidget button)
	{
	}*/

    // get a marker near the specified block pos if it exists.
    // the maxDistance is based on the view width so that you need to click
    // closer
    // to a marker when zoomed in to select it.
    public Marker getMarkerNearScreenPos(int x, int y) {
        Marker nearMarker = null;
        for (Marker marker : this.mw.markerManager.visibleMarkerList) {
            if (marker.screenPos != null) {
                if (marker.screenPos.distanceSq(x, y) < 6.0) {
                    nearMarker = marker;
                }
            }
        }
        return nearMarker;
    }

    public int getHeightAtBlockPos(int bX, int bZ) {
        int bY = 0;
        DimensionType worldDimension = this.mw.mc.world.getDimension().getType();
        if ((worldDimension == this.mapView.getDimension()) && (worldDimension != DimensionType.THE_NETHER)) {
            bY = this.mw.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, bX, bZ);
        }
        return bY;
    }

    public boolean isPlayerNearScreenPos(int x, int y) {
        Point.Double p = this.map.playerArrowScreenPos;
        return p.distanceSq(x, y) < 9.0;
    }

    public void deleteSelectedMarker() {
        if (this.mw.markerManager.selectedMarker != null) {
            // MwUtil.log("deleting marker %s",
            // this.mw.markerManager.selectedMarker.name);
            this.mw.markerManager.delMarker(this.mw.markerManager.selectedMarker);
            this.mw.markerManager.update();
            this.mw.markerManager.selectedMarker = null;
        }
    }

    public void mergeMapViewToImage() {
        this.mw.chunkManager.saveChunks();
        this.mw.executor.addTask(new MergeTask(this.mw.regionManager, (int) this.mapView.getX(), (int) this.mapView.getZ(), (int) this.mapView.getWidth(), (int) this.mapView.getHeight(), this.mapView.getDimension(), this.mw.worldDir, this.mw.worldDir.getName()));

        Utils.printBoth(I18n.translate("mw.gui.mwgui.chatmsg.merge", this.mw.worldDir.getAbsolutePath()));
    }

    public void regenerateView() {
        Utils.printBoth(I18n.translate("mw.gui.mwgui.chatmsg.regenmap",
                (int) this.mapView.getWidth(),
                (int) this.mapView.getHeight(),
                (int) this.mapView.getMinX(),
                (int) this.mapView.getMinZ()));
        // this.mw.reloadBlockColours();
        this.mw.executor.addTask(new RebuildRegionsTask(this.mw, (int) this.mapView.getMinX(), (int) this.mapView.getMinZ(), (int) this.mapView.getWidth(), (int) this.mapView.getHeight(), this.mapView.getDimension()));
    }

    // c is the ascii equivalent of the key typed.
    // key is the lwjgl key code.
    @Override
    public boolean charTyped(char c, int key) { //TODO change to proper keybdining inputs
        // MwUtil.log("MwGui.keyTyped(%c, %d)", c, key);
        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE:
                this.exitGui();
                break;

            case GLFW.GLFW_KEY_DELETE:
                this.deleteSelectedMarker();
                break;

            case GLFW.GLFW_KEY_SPACE:
                // next marker group
                this.mw.markerManager.nextGroup();
                this.mw.markerManager.update();
                break;

            case GLFW.GLFW_KEY_C:
                // cycle selected marker colour
                if (this.mw.markerManager.selectedMarker != null) {
                    this.mw.markerManager.selectedMarker.colourNext();
                }
                break;

            case GLFW.GLFW_KEY_N:
                // select next visible marker
                this.mw.markerManager.selectNextMarker();
                break;

            case GLFW.GLFW_KEY_HOME:
                // centre map on player
                this.mapView.setViewCentreScaled(this.mw.playerX, this.mw.playerZ, this.mw.playerDimension);
                break;

            case GLFW.GLFW_KEY_END:
                // centre map on selected marker
                this.centerOnSelectedMarker();
                break;

            case GLFW.GLFW_KEY_P:
                this.mergeMapViewToImage();
                this.exitGui();
                break;

            case GLFW.GLFW_KEY_T:
                if (this.mw.markerManager.selectedMarker != null) {
                    this.mw.teleportToMarker(this.mw.markerManager.selectedMarker);
                    this.exitGui();
                } else {
                    this.mw.mc.openScreen(new MwGuiTeleportDialog(this, this.mw, this.mapView, this.mouseBlockX, ConfigurationHandler.mwConfig.defaultTeleportHeight(), this.mouseBlockZ));
                }
                break;

            case GLFW.GLFW_KEY_LEFT:
                this.mapView.panView(-PAN_FACTOR, 0);
                break;
            case GLFW.GLFW_KEY_RIGHT:
                this.mapView.panView(PAN_FACTOR, 0);
                break;
            case GLFW.GLFW_KEY_UP:
                this.mapView.panView(0, -PAN_FACTOR);
                break;
            case GLFW.GLFW_KEY_DOWN:
                this.mapView.panView(0, PAN_FACTOR);
                break;

            case GLFW.GLFW_KEY_R:
                this.regenerateView();
                this.exitGui();
                break;

            case GLFW.GLFW_KEY_L:
                this.MarkerOverlay.setEnabled(!this.MarkerOverlay.getEnabled());
                break;

            default:
                if (MwKeyHandler.keyMapGui.isPressed()) {
                    this.exitGui();
                } else if (MwKeyHandler.keyZoomIn.isPressed()) {
                    this.mapView.adjustZoomLevel(-1);
                } else if (MwKeyHandler.keyZoomOut.isPressed()) {
                    this.mapView.adjustZoomLevel(1);
                } else if (MwKeyHandler.keyNextGroup.isPressed()) {
                    this.mw.markerManager.nextGroup();
                    this.mw.markerManager.update();
                } else if (MwKeyHandler.keyUndergroundMode.isPressed()) {
                    this.mw.toggleUndergroundMode();
                    this.mapView.setUndergroundMode(ConfigurationHandler.mwConfig.undergroundMode());
                }
                break;
        }

        return true;
    }

    // override Screen's handleMouseInput to process
    // the scroll wheel.
    @Override
    public void handleMouseInput() throws IOException {
        if (this.MarkerOverlay.isMouseInField() && (this.mouseLeftHeld == 0)) {
            this.MarkerOverlay.handleMouseInput();
        } else if ((MwAPI.getCurrentDataProvider() != null) && MwAPI.getCurrentDataProvider().onMouseInput(this.mapView, this.mapMode)) {
            return;
        } else {
            int x = (Mouse.getEventX() * this.width) / this.mw.mc.getWindow().getFramebufferWidth();
            int y = this.height - ((Mouse.getEventY() * this.height) / this.mw.mc.getWindow().getFramebufferHeight()) - 1;
            int direction = Mouse.getEventDWheel();
            if (direction != 0) {
                this.mouseDWheelScrolled(x, y, direction);
            }
        }
        super.handleMouseInput();
    }

    // mouse button clicked. 0 = LMB, 1 = RMB, 2 = MMB
    @Override
    protected void mouseClicked(int x, int y, int button) {
        Marker marker = this.getMarkerNearScreenPos(x, y);
        Marker prevMarker = this.mw.markerManager.selectedMarker;

        if (this.MarkerOverlay.isMouseInField() && (this.mouseLeftHeld == 0)) {
            this.MarkerOverlay.handleMouseInput();
        } else {
            if (button == 0) {
                if (this.dimensionLabel.posWithin(x, y)) {
                    this.mw.mc.openScreen(new MwGuiDimensionDialog(this, this.mw, this.mapView, this.mapView.getDimension()));
                } else if (this.optionsLabel.posWithin(x, y)) {
                    try {
                        Screen newScreen = ModGuiConfig.class.getConstructor(Screen.class).newInstance(this);
                        this.mw.mc.openScreen(newScreen);
                    } catch (Exception e) {
                        Logging.logError("There was a critical issue trying to build the config GUI for %s", MwReference.MOD_ID);
                    }
                } else if (this.updateLabel.posWithin(x, y)) {
                    URI uri;

                    if (!this.mw.mc.options.chatLinks) {
                        return;
                    }

                    try {
                        uri = new URI(VersionCheck.getUpdateURL());

                        if (!MwReference.PROTOCOLS.contains(uri.getScheme().toLowerCase())) {
                            throw new URISyntaxException(uri.toString(), "Unsupported protocol: " + uri.getScheme().toLowerCase());
                        }

                        if (this.mw.mc.options.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mw.mc.openScreen(new GuiConfirmOpenLink(this, uri.toString(), 31102009, false));
                        } else {
                            Utils.openWebLink(uri);
                        }
                    } catch (URISyntaxException urisyntaxexception) {
                        Logging.logError("Can\'t open url for %s", urisyntaxexception);
                    }
                } else {
                    this.mouseLeftHeld = 1;
                    this.mouseLeftDragStartX = x;
                    this.mouseLeftDragStartY = y;
                    this.mw.markerManager.selectedMarker = marker;

                    if ((marker != null) && (prevMarker == marker)) {
                        // clicked previously selected marker.
                        // start moving the marker.
                        this.movingMarker = marker;
                        this.movingMarkerXStart = marker.x;
                        this.movingMarkerZStart = marker.z;
                    }
                }

            } else if (button == 1) {
                this.openMarkerGui(marker, x, y);
            } else if (button == 2) {
                Point blockPoint = this.mapMode.screenXYtoBlockXZ(this.mapView, x, y);

                IMwDataProvider provider = MwAPI.getCurrentDataProvider();
                if (provider != null) {
                    provider.onMiddleClick(this.mapView.getDimension(), blockPoint.x, blockPoint.y, this.mapView);
                }
            }

            this.viewXStart = this.mapView.getX();
            this.viewZStart = this.mapView.getZ();
            // this.viewSizeStart = this.mapManager.getViewSize();
        }
    }

    // mouse button released. 0 = LMB, 1 = RMB, 2 = MMB
    // not called on mouse movement.
    @Override
    protected void mouseReleased(int x, int y, int button) {
        // MwUtil.log("MwGui.mouseMovedOrUp(%d, %d, %d)", x, y, button);
        if (button == 0) {
            this.mouseLeftHeld = 0;
            this.movingMarker = null;
        } else if (button == 1) {
            // this.mouseRightHeld = 0;
        }
    }

    // zoom on mouse direction wheel scroll
    public void mouseDWheelScrolled(int x, int y, int direction) {
        Marker marker = this.getMarkerNearScreenPos(x, y);
        if ((marker != null) && (marker == this.mw.markerManager.selectedMarker)) {
            if (direction > 0) {
                marker.colourNext();
            } else {
                marker.colourPrev();
            }

        } else if (this.dimensionLabel.posWithin(x, y)) {
            int n = (direction > 0) ? 1 : -1;
            this.mapView.nextDimension(WorldConfig.getInstance().dimensionList, n);

        } else if (this.groupLabel.posWithin(x, y)) {
            int n = (direction > 0) ? 1 : -1;
            this.mw.markerManager.nextGroup(n);
            this.mw.markerManager.update();
        } else if (this.overlayLabel.posWithin(x, y)) {
            int n = (direction > 0) ? 1 : -1;
            if (MwAPI.getCurrentDataProvider() != null) {
                MwAPI.getCurrentDataProvider().onOverlayDeactivated(this.mapView);
            }

            if (n == 1) {
                MwAPI.setNextProvider();
            } else {
                MwAPI.setPrevProvider();
            }

            if (MwAPI.getCurrentDataProvider() != null) {
                MwAPI.getCurrentDataProvider().onOverlayActivated(this.mapView);
            }

        } else {
            int zF = (direction > 0) ? -1 : 1;
            this.mapView.zoomToPoint(this.mapView.getZoomLevel() + zF, this.mouseBlockX, this.mouseBlockZ);
            ConfigurationHandler.mwConfig.fullScreenZoomLevel() = this.mapView.getZoomLevel();
        }
    }

    // closes this gui
    public void exitGui() {
        this.mw.mc.openScreen((Screen) null);
    }

    /**
     * Called when the screen is unloaded. Used to disable GLFW repeat
     * events
     */
    @Override
    public void onGuiClosed() {
        GLFW.enableRepeatEvents(false);
        this.mw.miniMap.view.setDimension(this.mapView.getDimension());
        GLFW.enableRepeatEvents(false);
    }

    // called every frame
    @Override
    public void updateScreen() {
    }

    public void drawStatus(int bX, int bY, int bZ) {
        StringBuilder builder = new StringBuilder();
        if (bY != 0) {
            builder.append(I18n.translate("mw.gui.mwgui.status.cursor", bX, bY, bZ));
        } else {
            builder.append(I18n.translate("mw.gui.mwgui.status.cursorNoY", bX, bZ));
        }

        if (this.mw.mc.world != null) {
            if (!this.mw.mc.world.getWorldChunk(new BlockPos(bX, 0, bZ)).isEmpty()) {
                builder.append(", ");
                builder.append(I18n.translate("mw.gui.mwgui.status.biome", this.mw.mc.world.getBiomeGenForCoords(new BlockPos(bX, 0, bZ)).biomeName));
            }
        }

        IMwDataProvider provider = MwAPI.getCurrentDataProvider();
        if (provider != null) {
            builder.append(provider.getStatusString(this.mapView.getDimension(), bX, bY, bZ));
        }
        String s = builder.toString();
        int x = (this.width / 2) - 10 - (this.textRenderer.getStringWidth(s) / 2);

        this.statusLabel.setCoords(x, this.height - 21);
        this.statusLabel.setText(new String[]
                {
                        builder.toString()
                }, null);
        this.statusLabel.draw();
    }

    // also called every frame
    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {

        this.drawDefaultBackground();
        double xOffset = 0.0;
        double yOffset = 0.0;
        // double zoomFactor = 1.0;

        if (this.mouseLeftHeld > 2) {
            xOffset = ((this.mouseLeftDragStartX - mouseX) * this.mapView.getWidth()) / this.mapMode.w;
            yOffset = ((this.mouseLeftDragStartY - mouseY) * this.mapView.getHeight()) / this.mapMode.h;

            if (this.movingMarker != null) {
                double scale = this.mapView.getDimensionScaling(this.movingMarker.dimension);
                this.movingMarker.x = this.movingMarkerXStart - (int) (xOffset / scale);
                this.movingMarker.z = this.movingMarkerZStart - (int) (yOffset / scale);
            } else {
                this.mapView.setViewCentre(this.viewXStart + xOffset, this.viewZStart + yOffset);
            }
        }

        if (this.mouseLeftHeld > 0) {
            this.mouseLeftHeld++;
        }

        // draw the map
        this.map.draw();

        // let the renderEngine know we have changed the texture.
        // this.mw.mc.renderEngine.resetBoundTexture();

        // get the block the mouse is currently hovering over
        Point p = this.mapMode.screenXYtoBlockXZ(this.mapView, mouseX, mouseY);
        this.mouseBlockX = p.x;
        this.mouseBlockZ = p.y;
        this.mouseBlockY = this.getHeightAtBlockPos(this.mouseBlockX, this.mouseBlockZ);

        // draw the label near mousepointer
        this.drawMarkerLabel(mouseX, mouseY, f);

        // draw status message
        this.drawStatus(this.mouseBlockX, this.mouseBlockY, this.mouseBlockZ);

        // draw labels
        this.drawLabel(mouseX, mouseY, f);

        this.MarkerOverlay.drawScreen(mouseX, mouseY, f);
        ;

        super.drawScreen(mouseX, mouseY, f);
    }

    private void drawMarkerLabel(int mouseX, int mouseY, float f) {
        // draw name of marker under mouse cursor
        Marker marker = this.getMarkerNearScreenPos(mouseX, mouseY);
        if (marker != null) {
            this.markerLabel.setText(new String[]
                    {
                            marker.name,
                            String.format("(%d, %d, %d)", marker.x, marker.y, marker.z)
                    }, null);
            this.markerLabel.setCoords(mouseX + 8, mouseY);
            this.markerLabel.draw();
        }

        // draw name of player under mouse cursor
        if (this.isPlayerNearScreenPos(mouseX, mouseY)) {
            this.markerLabel.setText(new String[]
                    {
                            this.mw.mc.player.getEntityName(),
                            String.format("(%d, %d, %d)", this.mw.playerXInt, this.mw.playerYInt, this.mw.playerZInt)
                    }, null);
            this.markerLabel.setCoords(mouseX + 8, mouseY);
            this.markerLabel.draw();
        }
    }

    private void drawLabel(int mouseX, int mouseY, float f) {
        this.helpLabel.draw();
        this.optionsLabel.draw();
        String dimString = "[" + I18n.translate("mw.gui.mwgui.dimension", this.mapView.getDimension()) + "]";
        this.dimensionLabel.setText(new String[]
                {
                        dimString
                }, null);
        this.dimensionLabel.draw();

        String groupString = "[" + I18n.translate("mw.gui.mwgui.group.1", this.mw.markerManager.getVisibleGroupName()) + "]";
        this.groupLabel.setText(new String[]
                {
                        groupString
                }, null);
        this.groupLabel.draw();

        String overlayString = "[" + I18n.translate("mw.gui.mwgui.overlay", MwAPI.getCurrentProviderName()) + "]";
        this.overlayLabel.setText(new String[]
                {
                        overlayString
                }, null);
        this.overlayLabel.draw();

        if (!VersionCheck.isLatestVersion()) {

            this.updateLabel.draw();
        }

        // help message on mouse over
        if (this.helpLabel.posWithin(mouseX, mouseY)) {
            this.helpTooltipLabel.draw();
        }
        if (this.updateLabel.posWithin(mouseX, mouseY)) {
            this.updateTooltipLabel.draw();
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (id == 31102009) {
            if (result) {
                Utils.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            this.mw.mc.openScreen(this);
        }
    }

    public void centerOnSelectedMarker() {
        if (this.mw.markerManager.selectedMarker != null) {
            this.mapView.setViewCentreScaled(this.mw.markerManager.selectedMarker.x, this.mw.markerManager.selectedMarker.z, 0);
        }
    }

    public void openMarkerGui(Marker m, int mouseX, int mouseY) {
        if ((m != null) && (this.mw.markerManager.selectedMarker == m)) {
            // right clicked previously selected marker.
            // edit the marker
            if (ConfigurationHandler.mwConfig.newMarkerDialog()) {
                this.mw.mc.openScreen(new MwGuiMarkerDialogNew(this, this.mw.markerManager, m));
            } else {
                this.mw.mc.openScreen(new MwGuiMarkerDialog(this, this.mw.markerManager, m));
            }

        } else if (m == null) {
            // open new marker dialog
            String group = this.mw.markerManager.getVisibleGroupName();
            if (group.equals("none")) {
                group = I18n.translate("mw.gui.mwgui.group.2");
            }

            int mx, my, mz;
            if (this.isPlayerNearScreenPos(mouseX, mouseY)) {
                // marker at player's locations
                mx = this.mw.playerXInt;
                my = this.mw.playerYInt;
                mz = this.mw.playerZInt;

            } else {
                // marker at mouse pointer location
                mx = this.mouseBlockX;
                my = (this.mouseBlockY > 0) ? this.mouseBlockY : ConfigurationHandler.mwConfig.defaultTeleportHeight();
                mz = this.mouseBlockZ;
            }
            if (ConfigurationHandler.mwConfig.newMarkerDialog()) {
                this.mw.mc.openScreen(new MwGuiMarkerDialogNew(this, this.mw.markerManager, "", group, mx, my, mz, this.mapView.getDimension()));
            } else {
                this.mw.mc.openScreen(new MwGuiMarkerDialog(this, this.mw.markerManager, "", group, mx, my, mz, this.mapView.getDimension()));
            }
        }
    }
}
