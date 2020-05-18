package dex.mapwriter3.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkUnloadCallback {
    Event<ChunkUnloadCallback> EVENT = EventFactory.createArrayBacked(ChunkUnloadCallback.class,
            (listeners) -> (chunk) -> {
                for (ChunkUnloadCallback listener : listeners) {
                    ActionResult result = listener.unload(chunk);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult unload(WorldChunk chunk);
}
