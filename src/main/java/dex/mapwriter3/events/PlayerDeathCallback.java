package dex.mapwriter3.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerDeathCallback {
        Event<PlayerDeathCallback> EVENT = EventFactory.createArrayBacked(PlayerDeathCallback.class,
                (listeners) -> (player) -> {
                    for (PlayerDeathCallback listener : listeners) {
                        ActionResult result = listener.death(player);
                        if(result != ActionResult.PASS) {
                            return result;
                        }
                    }

                    return ActionResult.PASS;
                });

        ActionResult death(PlayerEntity player);
}
