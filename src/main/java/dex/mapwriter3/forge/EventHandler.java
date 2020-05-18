package dex.mapwriter3.forge;

import dex.mapwriter3.Mw;
import dex.mapwriter3.config.MWConfig;
import dex.mapwriter3.events.PlayerDeathCallback;
import dex.mapwriter3.overlay.OverlaySlime;
import dex.mapwriter3.util.Logging;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.LiteralText;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Environment(EnvType.CLIENT)
public class EventHandler {

    Mw mw;

    public EventHandler(Mw mw) {
        this.mw = mw;

        PlayerDeathCallback.EVENT.register(player -> {
            this.mw.onPlayerDeath((PlayerEntity) player);
            return ActionResult.PASS;
        });
    }

    @SubscribeEvent
    public void eventChunkLoad(ChunkEvent.Load event) {
        if (event.world.isRemote) {
            this.mw.onChunkLoad(event.getChunk());
        }
    }

    @SubscribeEvent
    public void eventChunkUnload(ChunkEvent.Unload event) {
        if (event.world.isRemote) {
            this.mw.onChunkUnload(event.getChunk());
        }
    }

    @SubscribeEvent
    public void onClientChat(ClientChatReceivedEvent event) {
        if (OverlaySlime.seedFound || !OverlaySlime.seedAsked) {
            return;
        }
        try { // I don't want to crash the game when we derp up in here
            if (event.message instanceof ChatComponentTranslation) {
                ChatComponentTranslation component = (ChatComponentTranslation) event.message;
                if (component.getKey().equals("commands.seed.success")) {
                    OverlaySlime.setSeed((Long) component.getFormatArgs()[0]);
                    event.setCanceled(true); // Don't let the player see this
                    // seed message, They didn't do
                    // /seed, we did
                }
            } else if (event.message instanceof LiteralText) {
                LiteralText component = (LiteralText) event.message;
                String msg = component.getUnformattedText();
                if (msg.startsWith("Seed: ")) { // Because bukkit...
                    OverlaySlime.setSeed(Long.parseLong(msg.substring(6)));
                    event.setCanceled(true); // Don't let the player see this
                    // seed message, They didn't do
                    // /seed, we did
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public void eventPlayerDeath(LivingDeathEvent event) {
        if (!event.isCanceled()) {
            if (event.entityLiving.getEntityId() == net.minecraft.client.MinecraftClient.getInstance().player.getEntityId()) {
                this.mw.onPlayerDeath((EntityPlayerMP) event.entityLiving);
            }
        }
    }*/

    @SubscribeEvent
    public void renderMap(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            Mw.getInstance().onTick();
        }
    }

    @SubscribeEvent
    public void onTextureStitchEventPost(TextureStitchEvent.Post event) {
        if (MWConfig.reloadColours) {
            Logging.logInfo("Skipping the first generation of blockcolours, models are not loaded yet", (Object[]) null);
        } else {
            this.mw.reloadBlockColours();
        }
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (Mw.getInstance().ready) {
            Mw.getInstance().markerManager.drawMarkersWorld(event.partialTicks);
        }
    }


    //a bit odd way to reload the blockcolours. if the models are not loaded yet then the uv values and icons will be wrong.
    //this only happens if fml.skipFirstTextureLoad is enabled.
    @SubscribeEvent
    public void onGuiOpenEvent(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu && MWConfig.reloadColours) {
            this.mw.reloadBlockColours();
            MWConfig.reloadColours = false;
        }
    }
}
