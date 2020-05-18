package dex.mapwriter3.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkLoadCallback {
    Event<ChunkLoadCallback> EVENT = EventFactory.createArrayBacked(ChunkLoadCallback.class,
            (listeners) -> (chunk) -> {
                for (ChunkLoadCallback listener : listeners) {
                    ActionResult result = listener.load(chunk);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult load(WorldChunk chunk);
}
